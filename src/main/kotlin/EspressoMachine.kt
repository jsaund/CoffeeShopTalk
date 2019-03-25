import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.selects.select
import model.CoffeeBean
import model.Espresso
import model.Milk
import util.log
import kotlin.coroutines.CoroutineContext

class EspressoMachine(scope: CoroutineScope): CoroutineScope {
    private val job = Job(scope.coroutineContext[Job])

    override val coroutineContext: CoroutineContext
        get() = job + CoroutineName("EspressoMachine") + Dispatchers.Unconfined

    private data class EspressoShotRequest(
        val deferredEspressoShot: CompletableDeferred<Espresso>,
        val groundBeans: CoffeeBean.GroundBeans
    )

    private data class SteamMilkRequest(
        val deferredSteamedMilk: CompletableDeferred<Milk.SteamedMilk>,
        val milk: Milk
    )

    private val portafilterOne: SendChannel<EspressoShotRequest> = actor {
        consumeEach { request ->
            val espresso = processPullEspressoShot(request.groundBeans)
            request.deferredEspressoShot.complete(espresso)
        }
    }
    private val portafilterTwo: SendChannel<EspressoShotRequest> = actor {
        consumeEach { request ->
            val espresso = processPullEspressoShot(request.groundBeans)
            request.deferredEspressoShot.complete(espresso)
        }
    }

    private val steamWandOne: SendChannel<SteamMilkRequest> = actor {
        consumeEach { request ->
            val steamedMilk = processSteamMilk(request.milk)
            request.deferredSteamedMilk.complete(steamedMilk)
        }
    }
    private val steamWandTwo: SendChannel<SteamMilkRequest> = actor {
        consumeEach { request ->
            val steamedMilk = processSteamMilk(request.milk)
            request.deferredSteamedMilk.complete(steamedMilk)
        }
    }

    suspend fun pullEspressoShot(groundBeans: CoffeeBean.GroundBeans): Espresso {
        val request = EspressoShotRequest(CompletableDeferred(), groundBeans)

        return select {
            portafilterOne.onSend(request) {
                request.deferredEspressoShot.await()
            }
            portafilterTwo.onSend(request) {
                request.deferredEspressoShot.await()
            }
        }
    }

    suspend fun steamMilk(milk: Milk): Milk.SteamedMilk {
        val request = SteamMilkRequest(CompletableDeferred(), milk)

        return select {
            steamWandOne.onSend(request) {
                request.deferredSteamedMilk.await()
            }
            steamWandTwo.onSend(request) {
                request.deferredSteamedMilk.await()
            }
        }
    }

    fun destroy() {
        coroutineContext.cancel()
    }

    private suspend fun processPullEspressoShot(groundBeans: CoffeeBean.GroundBeans): Espresso {
        log("pulling espresso shot")
        delay(600)
        return Espresso(groundBeans)
    }

    private suspend fun processSteamMilk(milk: Milk): Milk.SteamedMilk {
        log("steaming milk")
        delay(300)
        return Milk.SteamedMilk(milk)
    }
}
