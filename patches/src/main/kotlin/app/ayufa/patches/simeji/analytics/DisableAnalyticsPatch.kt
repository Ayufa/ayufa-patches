package app.ayufa.patches.simeji.analytics

import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.bytecodePatch
import app.ayufa.patches.simeji.shared.Constants.COMPATIBILITY_SIMEJI
import app.ayufa.patches.simeji.shared.returnVoidImmediately

private const val APPSFLYER_STATISTIC_CLASS =
    "Ljp/baidu/simeji/ad/statistic/AppsflyerStatistic;"
private const val STATISTIC_HELPER_CLASS =
    "Ljp/baidu/simeji/ad/statistic/StatisticHelper;"
private const val NEW_STATISTIC_CLASS = "Lw5/e;"

@Suppress("unused")
val disableAnalyticsPatch = bytecodePatch(
    name = "Disable analytics reporting",
    description = "Stops Simeji's event reporting: every AppsFlyer " +
        "event entry point and the in-house statistic event dispatch " +
        "become no-ops, so usage events are no longer sent. The " +
        "statistic module initialization is deliberately left intact " +
        "(the keyboard reads its config when the window is shown), and " +
        "core keyboard, conversion and account features are not touched.",
    default = false
) {
    compatibleWith(COMPATIBILITY_SIMEJI)

    execute {
        // AppsFlyer facade: all of its public statistic* entry points
        // funnel into AppsFlyerLib.logEvent. Silencing the facade
        // silences AppsFlyer event reporting from Simeji code.
        val appsflyerStatistic = mutableClassDefByOrNull(APPSFLYER_STATISTIC_CLASS)
            ?: throw PatchException("AppsflyerStatistic class not found")
        val appsflyerMethods = appsflyerStatistic.methods.filter { method ->
            method.name.startsWith("statistic") && method.returnType == "V"
        }
        if (appsflyerMethods.isEmpty()) {
            throw PatchException("No AppsflyerStatistic entry points found")
        }
        appsflyerMethods.forEach { it.returnVoidImmediately() }

        // In-house statistic events: silence only the event dispatch
        // (onEvent overloads). initStatistic must NOT be touched: it
        // also installs the statistic module's config object, which
        // the keyboard dereferences in onWindowShown — removing it
        // crashes the keyboard with a NullPointerException (verified
        // on Simeji 23.7).
        val statisticHelper = mutableClassDefByOrNull(STATISTIC_HELPER_CLASS)
            ?: throw PatchException("StatisticHelper class not found")
        val helperMethods = statisticHelper.methods.filter { method ->
            method.returnType == "V" && method.name == "onEvent"
        }
        if (helperMethods.isEmpty()) {
            throw PatchException("No StatisticHelper onEvent entry points found")
        }
        helperMethods.forEach { it.returnVoidImmediately() }

        // The statistic module's own senders: b() dispatches single
        // events, f() flushes queued real-time logs. Both only send
        // broadcasts to the statistic receiver.
        val newStatistic = mutableClassDefByOrNull(NEW_STATISTIC_CLASS)
            ?: throw PatchException("New statistic class (w5.e) not found")
        val senderMethods = newStatistic.methods.filter { method ->
            method.returnType == "V" &&
                ((method.name == "b" &&
                    method.parameterTypes == listOf(
                        "Landroid/content/Context;", "I", "Ljava/lang/String;", "Z"
                    )) ||
                    (method.name == "f" &&
                        method.parameterTypes == listOf("Landroid/content/Context;")))
        }
        if (senderMethods.size != 2) {
            throw PatchException(
                "Expected 2 statistic sender methods, found ${senderMethods.size}"
            )
        }
        senderMethods.forEach { it.returnVoidImmediately() }
    }
}
