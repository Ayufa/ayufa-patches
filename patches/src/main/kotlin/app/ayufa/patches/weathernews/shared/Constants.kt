package app.ayufa.patches.weathernews.shared

import app.morphe.patcher.patch.ApkFileType
import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility

internal object Constants {

    /**
     * Weathernews (ウェザーニュース) by Weathernews Inc.
     *
     * The patches target 7.5.1 (2026-10-22). The app is distributed
     * as split APKs (the Play / XAPK set: base + global module +
     * config splits); the fingerprints below live in the base APK.
     *
     * Unlike Simeji 23.7, no app-layer signature self-check and no
     * native signature validation were found in 7.5.1 (verified by
     * static analysis 2026-11-06), so re-signed builds are not
     * expected to break the way Simeji's did. Premium membership is
     * server-issued account state (AcctProfData / MemberType from
     * aplapi.weathernews.jp, receipts verified server-side), so
     * premium features are out of scope for patching; only local
     * display surfaces (ads, in-app message popups) are patched.
     */
    val COMPATIBILITY_WEATHERNEWS = Compatibility(
        name = "ウェザーニュース",
        packageName = "wni.WeathernewsTouch.jp",
        apkFileType = ApkFileType.APK_REQUIRED,
        appIconColor = 0x009FE8,
        signatures = setOf(
            // Weathernews Inc. signing certificate (SHA-256)
            "6ccb0ee3a9839b5a7c2b0e1eb8a744b73c730a4695863407efda6f92dd1798d9"
        ),
        targets = listOf(
            AppTarget(
                version = "7.5.1",
                minSdk = 28
            )
        )
    )
}
