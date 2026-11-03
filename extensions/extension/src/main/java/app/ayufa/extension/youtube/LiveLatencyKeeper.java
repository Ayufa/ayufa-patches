/*
 * Copyright 2026 Ayufa.
 * Part of Ayufa YouTube Patches (GPL-3.0).
 *
 * The hooking design follows the approach proven by Morphe's
 * "Remember live stream playback position" patch
 * (MorpheApp/morphe-patches, GPL-3.0): a once-per-second playback
 * time callback, live detection by a growing video length, and
 * seeking through VideoInformation.
 */

package app.ayufa.extension.youtube;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Keeps live stream playback close to the live edge.
 *
 * <p>While a live stream plays, the playback position slowly falls behind
 * the live edge (the end of the seek bar). This class is called about once
 * per second with the current playback time by the "Live latency keeper"
 * patch. When the video is confirmed to be an ongoing live stream and the
 * position is behind the edge by more than the configured threshold, it
 * seeks forward to (almost) the live edge.
 *
 * <p>Playback state (video length, live flag, seeking) is read from the
 * official Morphe extension class
 * {@code app.morphe.extension.youtube.patches.VideoInformation} through
 * reflection, so this class has no compile-time dependency on the official
 * patches. If the official Morphe patches are not applied, every call is a
 * no-op.
 *
 * <p>The feature can be turned off in Morphe settings (Ayufa patch
 * settings). The setting is stored by the Morphe settings UI in the
 * "morphe_prefs" shared preferences and read here on every call, so
 * toggling takes effect immediately. If the setting was never touched,
 * the feature defaults to on.
 */
@SuppressWarnings("unused")
public final class LiveLatencyKeeper {

    private static final String TAG = "AyufaLiveKeeper";

    private static final String MORPHE_PREFS_NAME = "morphe_prefs";
    private static final String KEY_KEEP_LIVE_AT_EDGE = "ayufa_keep_live_at_edge";

    // Shared preferences holding the Morphe settings, resolved lazily.
    private static SharedPreferences morphePrefs;

    // region Configuration.
    //
    // The values in the static initializer are the defaults. At patch time
    // the patch replaces the static initial values of these three fields
    // with the values of the patch options (check interval / threshold /
    // safety margin). Do not make these fields final.

    private static int checkIntervalSeconds;
    private static int thresholdSeconds;
    private static int safetySeconds;

    static {
        checkIntervalSeconds = 45;
        thresholdSeconds = 15;
        safetySeconds = 2;
    }

    // endregion

    /**
     * Minimum video length growth that confirms an ongoing live stream,
     * same value as Morphe's remember-live-stream-position patch.
     */
    private static final long LIVE_GROWTH_CONFIRM_MS = 3000;

    /**
     * How long catch-up is suppressed after the user manually rewinds.
     */
    private static final long MANUAL_REWIND_SUPPRESS_MS = 5 * 60 * 1000L;

    /**
     * If the video length does not grow for this long, the stream is
     * treated as ended (or the DVR window stopped moving) and catch-up
     * stops for this video.
     */
    private static final long LENGTH_PLATEAU_MS = 120 * 1000L;

    /**
     * Small delay before seeking, so the seek does not run inside the
     * playback time callback itself (seeking synchronously from that
     * callback can recurse into it).
     */
    private static final long SEEK_DELAY_MS = 200;

    // Reflection handles to Morphe's VideoInformation (official patches).
    private static boolean apiResolutionAttempted;
    private static Method videoInformationIsLiveStream;
    private static Method videoInformationGetVideoLength;
    private static Method videoInformationGetVideoId;
    private static Method videoInformationSeekTo;

    // Per-video state.
    private static String currentVideoId = "";
    private static long baselineVideoLength;
    private static boolean liveConfirmed;
    private static boolean streamEnded;
    private static long lastLength;
    private static long lastLengthGrowthAtMs;
    private static long lastTickTimeMs = -1;
    private static int stalledTicks;
    private static long lastCheckAtMs;
    private static long suppressUntilMs;
    private static boolean seekPending;

    private LiveLatencyKeeper() {
    }

    /**
     * Injection point. Called approximately once per second while a video
     * is playing (and while it is loading), with the current playback time.
     *
     * @param playbackTimeMs Current playback position in milliseconds.
     */
    public static void videoTimeChanged(long playbackTimeMs) {
        try {
            if (playbackTimeMs <= 0) {
                return;
            }
            if (!resolveVideoInformationApi()) {
                return;
            }
            if (!isEnabledInSettings()) {
                return;
            }

            String videoId = (String) videoInformationGetVideoId.invoke(null);
            if (videoId == null) {
                videoId = "";
            }
            if (!videoId.equals(currentVideoId)) {
                resetForVideo(videoId);
            }
            if (streamEnded) {
                return;
            }

            long videoLength = (Long) videoInformationGetVideoLength.invoke(null);
            if (videoLength <= 0) {
                // Player is not fully loaded yet.
                return;
            }

            long now = System.currentTimeMillis();

            // Track length growth (live edge movement) and playback stalls.
            if (videoLength > lastLength) {
                lastLength = videoLength;
                lastLengthGrowthAtMs = now;
            }
            if (playbackTimeMs == lastTickTimeMs) {
                stalledTicks++;
            } else {
                stalledTicks = 0;
            }
            // Manual rewind: the position jumped backwards between ticks.
            if (lastTickTimeMs > 0 && playbackTimeMs < lastTickTimeMs - LIVE_GROWTH_CONFIRM_MS) {
                suppressUntilMs = now + MANUAL_REWIND_SUPPRESS_MS;
            }
            lastTickTimeMs = playbackTimeMs;

            if (baselineVideoLength == 0) {
                baselineVideoLength = videoLength;
                return;
            }

            if (!liveConfirmed) {
                boolean isLive = (Boolean) videoInformationIsLiveStream.invoke(null);
                // The duration of an ongoing live stream grows in real time,
                // while the duration of a regular video (or a premiere with a
                // fixed duration) never changes.
                if (isLive && videoLength - baselineVideoLength >= LIVE_GROWTH_CONFIRM_MS) {
                    liveConfirmed = true;
                } else {
                    if (!isLive) {
                        baselineVideoLength = videoLength;
                    }
                    return;
                }
            }

            // The live edge stopped moving: stream ended or DVR window
            // plateaued. Stop catching up for this video.
            if (lastLengthGrowthAtMs > 0 && now - lastLengthGrowthAtMs > LENGTH_PLATEAU_MS) {
                streamEnded = true;
                return;
            }

            // Throttle the actual check to the configured interval.
            if (now - lastCheckAtMs < checkIntervalSeconds * 1000L) {
                return;
            }
            lastCheckAtMs = now;

            if (now < suppressUntilMs || seekPending) {
                return;
            }
            if (stalledTicks >= 2) {
                // Paused or buffering: do not yank a stalled player.
                return;
            }

            long behindMs = videoLength - playbackTimeMs;
            if (behindMs <= thresholdSeconds * 1000L) {
                return;
            }

            final long targetMs = videoLength - safetySeconds * 1000L;
            seekPending = true;
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                try {
                    Object result = videoInformationSeekTo.invoke(null, targetMs);
                    Log.d(TAG, "Catch-up seek to " + targetMs + " result: " + result
                            + " (was behind " + behindMs + " ms)");
                } catch (Throwable ex) {
                    Log.e(TAG, "Catch-up seek failed", ex);
                } finally {
                    seekPending = false;
                }
            }, SEEK_DELAY_MS);
        } catch (Throwable ex) {
            // Never break playback because of this feature.
            Log.e(TAG, "videoTimeChanged failure", ex);
        }
    }

    /**
     * Reads the on/off switch from the Morphe settings. Defaults to on
     * when the setting was never touched or cannot be read.
     */
    private static boolean isEnabledInSettings() {
        try {
            if (morphePrefs == null) {
                Context context = resolveAppContext();
                if (context == null) {
                    // Too early to tell; stay enabled and retry next call.
                    return true;
                }
                morphePrefs = context.getSharedPreferences(
                        MORPHE_PREFS_NAME, Context.MODE_PRIVATE);
            }
            return morphePrefs.getBoolean(KEY_KEEP_LIVE_AT_EDGE, true);
        } catch (Throwable ignored) {
            return true;
        }
    }

    private static Context resolveAppContext() {
        // Morphe's shared Utils holds the app context whenever the
        // official patches are applied.
        try {
            Class<?> utils = Class.forName("app.morphe.extension.shared.Utils");
            Object context = utils.getMethod("getContext").invoke(null);
            if (context instanceof Context) {
                return (Context) context;
            }
        } catch (Throwable ignored) {
            // Fall through to the framework way.
        }
        try {
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object application = activityThread.getMethod("currentApplication").invoke(null);
            if (application instanceof Context) {
                return (Context) application;
            }
        } catch (Throwable ignored) {
            // No context available yet.
        }
        return null;
    }

    private static void resetForVideo(String videoId) {
        currentVideoId = videoId;
        baselineVideoLength = 0;
        liveConfirmed = false;
        streamEnded = false;
        lastLength = 0;
        lastLengthGrowthAtMs = 0;
        lastTickTimeMs = -1;
        stalledTicks = 0;
        lastCheckAtMs = 0;
        suppressUntilMs = 0;
        seekPending = false;
    }

    /**
     * Resolves the static methods of Morphe's VideoInformation class once.
     *
     * @return True if the official Morphe patches are present.
     */
    private static boolean resolveVideoInformationApi() {
        if (apiResolutionAttempted) {
            return videoInformationSeekTo != null;
        }
        apiResolutionAttempted = true;
        try {
            Class<?> videoInformation =
                    Class.forName("app.morphe.extension.youtube.patches.VideoInformation");
            videoInformationIsLiveStream = videoInformation.getMethod("isLiveStream");
            videoInformationGetVideoLength = videoInformation.getMethod("getVideoLength");
            videoInformationGetVideoId = videoInformation.getMethod("getVideoId");
            videoInformationSeekTo = videoInformation.getMethod("seekTo", long.class);
            return true;
        } catch (Throwable ex) {
            // Official Morphe patches are not applied (or changed).
            // Silently disable: this patch is an add-on to them.
            videoInformationIsLiveStream = null;
            videoInformationGetVideoLength = null;
            videoInformationGetVideoId = null;
            videoInformationSeekTo = null;
            Log.d(TAG, "Morphe VideoInformation not available; latency keeper disabled");
            return false;
        }
    }
}
