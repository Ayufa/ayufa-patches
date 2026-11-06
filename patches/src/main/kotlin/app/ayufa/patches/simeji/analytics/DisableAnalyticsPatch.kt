package app.ayufa.patches.simeji.analytics

import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.bytecodePatch
import app.ayufa.patches.simeji.shared.Constants.COMPATIBILITY_SIMEJI
import app.ayufa.patches.simeji.shared.returnVoidImmediately

private const val APPSFLYER_STATISTIC_CLASS =
    "Ljp/baidu/simeji/ad/statistic/AppsflyerStatistic;"
private const val STATISTIC_HELPER_CLASS =
    "Ljp/baidu/simeji/ad/statistic/StatisticHelper;"

@Suppress("unused")
val disableAnalyticsPatch = bytecodePatch(
    name = "Disable analytics reporting",
    description = "Stops Simeji's event reporting: every AppsFlyer " +
        "event entry point and the in-house statistic reporter become " +
        "no-ops, so usage events are no longer sent. Core keyboard, " +
        "conversion and account features are not touched.",
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

        // In-house statistic reporter (api.simeji.me): init + events.
        val statisticHelper = mutableClassDefByOrNull(STATISTIC_HELPER_CLASS)
            ?: throw PatchException("StatisticHelper class not found")
        val helperMethods = statisticHelper.methods.filter { method ->
            method.returnType == "V" &&
                (method.name == "initStatistic" || method.name == "onEvent")
        }
        if (helperMethods.isEmpty()) {
            throw PatchException("No StatisticHelper entry points found")
        }
        helperMethods.forEach { it.returnVoidImmediately() }
    }
}
