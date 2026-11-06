package app.ayufa.patches.simeji.shared

import app.morphe.patcher.patch.ApkFileType
import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility

internal object Constants {

    /**
     * Simeji (Japanese keyboard + emoji) by Baidu Japan.
     *
     * 23.7 is the current release (2026-10-27). The app is distributed
     * as split APKs; Morphe merges the splits (or a manually merged
     * single APK can be used). The signing certificate below was
     * verified against both the Play-installed copy and the APKPure
     * XAPK of 23.7 (identical certificate).
     */
    val COMPATIBILITY_SIMEJI = Compatibility(
        name = "Simeji",
        packageName = "com.adamrocker.android.input.simeji",
        apkFileType = ApkFileType.APK_REQUIRED,
        appIconColor = 0xFF0033,
        signatures = setOf(
            // Baidu Japan signing certificate (SHA-256)
            "4e5a78c0450316e38afab3ecb6bac9932c095f2b34360c40129857b78657e24f"
        ),
        targets = listOf(
            AppTarget(
                version = "23.7",
                minSdk = 23
            )
        )
    )
}
