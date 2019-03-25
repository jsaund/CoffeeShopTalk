import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import model.*
import util.log
import java.lang.Thread.sleep
import kotlin.system.measureTimeMillis
/*
fun main() = runBlocking {
    val orders = listOf(
        Menu.Cappuccino(CoffeeBean.Regular, Milk.Whole),
        Menu.Cappuccino(CoffeeBean.Premium, Milk.Breve),
        Menu.Cappuccino(CoffeeBean.Regular, Milk.NonFat),
        Menu.Cappuccino(CoffeeBean.Decaf, Milk.Whole),
        Menu.Cappuccino(CoffeeBean.Regular, Milk.NonFat),
        Menu.Cappuccino(CoffeeBean.Decaf, Milk.NonFat)
    )

    orders.forEach { log(it) }

    val ordersChannel = Channel<Menu>()
    launch(CoroutineName("cashier")) {
        orders.forEach { ordersChannel.send(it) }
        ordersChannel.close()
    }

    val espressoMachine = EspressoMachine(this)

    val time = measureTimeMillis {
        coroutineScope {
            launch(CoroutineName("barista-1")) { processOrders(ordersChannel, espressoMachine) }
            launch(CoroutineName("barista-2")) { processOrders(ordersChannel, espressoMachine) }
        }
    }
    log("time: $time ms")

    espressoMachine.destroy()
}

private suspend fun processOrders(orders: ReceiveChannel<Menu>, espressoMachine: EspressoMachine) {
    orders.consumeEach { order ->
        val groundBeans = grindCoffeeBeans(order.beans())
        coroutineScope {
            val espressoDeferred = async { espressoMachine.pullEspressoShot(groundBeans) }
            val steamedMilkDeferred = async { espressoMachine.steamMilk(order.milk()) }
            val cappuccino = makeCappuccino(order as Menu.Cappuccino, espressoDeferred.await(), steamedMilkDeferred.await())
            log("serve: $cappuccino")
        }
    }
}

private suspend fun grindCoffeeBeans(beans: CoffeeBean): CoffeeBean.GroundBeans {
    log("grinding coffee beans...")
    delay(3000)
    return CoffeeBean.GroundBeans(beans)
}


private suspend fun makeCappuccino(order: Menu.Cappuccino, espresso: Espresso, steamedMilk: Milk.SteamedMilk): Beverage.Cappuccino {
    log("making cappuccino")
    delay(500)
    return Beverage.Cappuccino(order, espresso, steamedMilk)
}
*/

fun main() {
    val orders = listOf(
        Menu.Cappuccino(CoffeeBean.Regular, Milk.Whole),
        Menu.Cappuccino(CoffeeBean.Premium, Milk.Breve),
        Menu.Cappuccino(CoffeeBean.Regular, Milk.NonFat),
        Menu.Cappuccino(CoffeeBean.Decaf, Milk.Whole),
        Menu.Cappuccino(CoffeeBean.Regular, Milk.NonFat),
        Menu.Cappuccino(CoffeeBean.Decaf, Milk.NonFat)
    )

    orders.forEach { log(it) }

    orders.forEach {
        val groundBeans = grindCoffeeBeans(it.beans)
        val espresso = pullEspressoShot(groundBeans)
        val steamedMilk = steamMilk(it.milk)
        val cappuccino = makeCappuccino(it, espresso, steamedMilk)
        log("serve: $cappuccino")
    }
}


private fun grindCoffeeBeans(beans: CoffeeBean): CoffeeBean.GroundBeans {
    log("grinding coffee beans")
    sleep(1000)
    return CoffeeBean.GroundBeans(beans)
}

private fun pullEspressoShot(groundBeans: CoffeeBean.GroundBeans): Espresso {
    log("pulling espresso shot")
    sleep(600)
    return Espresso(groundBeans)
}

private fun steamMilk(milk: Milk): Milk.SteamedMilk {
    log("steaming milk")
    sleep(300)
    return Milk.SteamedMilk(milk)
}

private fun makeCappuccino(order: Menu.Cappuccino, espresso: Espresso, steamedMilk: Milk.SteamedMilk): Beverage.Cappuccino {
    log("making cappuccino")
    sleep(150)
    return Beverage.Cappuccino(order, espresso, steamedMilk)
}















































/* [1] Simple approach that's sequential
fun main() {
    val orders = listOf(
        Menu.Cappuccino(CoffeeBean.Regular, Milk.Whole),
        Menu.Cappuccino(CoffeeBean.Premium, Milk.Breve),
        Menu.Cappuccino(CoffeeBean.Regular, Milk.NonFat),
        Menu.Cappuccino(CoffeeBean.Decaf, Milk.Whole),
        Menu.Cappuccino(CoffeeBean.Regular, Milk.NonFat),
        Menu.Cappuccino(CoffeeBean.Decaf, Milk.NonFat)
    )

    orders.forEach { log(it) }

    val time = measureTimeMillis {
        orders.forEach {
            val groundBeans = grindCoffeeBeans(it.beans)
            val espresso = pullEspressoShot(groundBeans)
            val steamedMilk = steamMilk(it.milk)
            val cappuccino = makeCappuccino(it, espresso, steamedMilk)
            log("serve: $cappuccino")
        }
    }
    log("time: $time ms")
}

private fun grindCoffeeBeans(beans: CoffeeBean): CoffeeBean.GroundBeans {
    log("grinding coffee beans")
    sleep(3000)
    return CoffeeBean.GroundBeans(beans)
}

private fun pullEspressoShot(groundBeans: CoffeeBean.GroundBeans): Espresso {
    log("pulling espresso shot")
    sleep(2000)
    return Espresso(groundBeans)
}

private fun steamMilk(milk: Milk): Milk.SteamedMilk {
    log("steaming milk")
    sleep(1000)
    return Milk.SteamedMilk(milk)
}

private fun makeCappuccino(order: Menu.Cappuccino, espresso: Espresso, steamedMilk: Milk.SteamedMilk): Beverage.Cappuccino {
    log("making cappuccino")
    sleep(500)
    return Beverage.Cappuccino(order, espresso, steamedMilk)
}

*/


/* [2] Update to use coroutines and demonstrate that the list of orders is processed twice concurrently
fun main() = runBlocking {
    val orders = listOf(
        Menu.Cappuccino(CoffeeBean.Regular, Milk.Whole),
        Menu.Cappuccino(CoffeeBean.Premium, Milk.Breve),
        Menu.Cappuccino(CoffeeBean.Regular, Milk.NonFat),
        Menu.Cappuccino(CoffeeBean.Decaf, Milk.Whole),
        Menu.Cappuccino(CoffeeBean.Regular, Milk.NonFat),
        Menu.Cappuccino(CoffeeBean.Decaf, Milk.NonFat)
    )

    orders.forEach { log(it) }

    val time = measureTimeMillis {
        coroutineScope {
            launch(CoroutineName("barista-1") + Dispatchers.Default) { processOrder(orders) }
            launch(CoroutineName("barista-2") + Dispatchers.Default) { processOrder(orders) }
        }
    }
    log("time: $time ms")
}

private suspend fun processOrder(orders: List<Menu>) {
    orders.forEach {
        val groundBeans = grindCoffeeBeans(it.beans())
        val espresso = pullEspressoShot(groundBeans)
        val steamedMilk = steamMilk(it.milk())
        val cappuccino = makeCappuccino(it as Menu.Cappuccino, espresso, steamedMilk)
        log("serve: $cappuccino")
    }
}

private suspend fun grindCoffeeBeans(beans: CoffeeBean): CoffeeBean.GroundBeans {
    log("grinding coffee beans")
    delay(3000)
    return CoffeeBean.GroundBeans(beans)
}

private suspend fun pullEspressoShot(groundBeans: CoffeeBean.GroundBeans): Espresso {
    log("pulling espresso shot")
    delay(2000)
    return Espresso(groundBeans)
}

private suspend fun steamMilk(milk: Milk): Milk.SteamedMilk {
    log("steaming milk")
    delay(1000)
    return Milk.SteamedMilk(milk)
}

private suspend fun makeCappuccino(
    order: Menu.Cappuccino,
    espresso: Espresso,
    steamedMilk: Milk.SteamedMilk
): Beverage.Cappuccino {
    log("making cappuccino")
    delay(500)
    return Beverage.Cappuccino(order, espresso, steamedMilk)
}

*/


/* [3] Update the coroutines to consume new orders from the cashier
fun main() = runBlocking {
    val orders = listOf(
        Menu.Cappuccino(CoffeeBean.Regular, Milk.Whole),
        Menu.Cappuccino(CoffeeBean.Premium, Milk.Breve),
        Menu.Cappuccino(CoffeeBean.Regular, Milk.NonFat),
        Menu.Cappuccino(CoffeeBean.Decaf, Milk.Whole),
        Menu.Cappuccino(CoffeeBean.Regular, Milk.NonFat),
        Menu.Cappuccino(CoffeeBean.Decaf, Milk.NonFat)
    )

    orders.forEach { log(it) }

    val ordersChannel = produce(CoroutineName("cashier")) {
        orders.forEach{
            log("processing order: $it")
            send(it)
        }
    }

    val time = measureTimeMillis {
        coroutineScope {
            launch(CoroutineName("barista-1") + Dispatchers.Default) { processOrder(ordersChannel) }
            launch(CoroutineName("barista-2") + Dispatchers.Default) { processOrder(ordersChannel) }
        }
    }
    log("time: $time ms")
}

private suspend fun processOrder(ordersChannel: ReceiveChannel<Menu>) {
    ordersChannel.consumeEach {
        val groundBeans = grindCoffeeBeans(it.beans())
        val espresso = pullEspressoShot(groundBeans)
        val steamedMilk = steamMilk(it.milk())
        val cappuccino = makeCappuccino(it as Menu.Cappuccino, espresso, steamedMilk)
        log("serve: $cappuccino")
    }
}

private suspend fun grindCoffeeBeans(beans: CoffeeBean): CoffeeBean.GroundBeans {
    log("grinding coffee beans")
    delay(3000)
    return CoffeeBean.GroundBeans(beans)
}

private suspend fun pullEspressoShot(groundBeans: CoffeeBean.GroundBeans): Espresso {
    log("pulling espresso shot")
    delay(2000)
    return Espresso(groundBeans)
}

private suspend fun steamMilk(milk: Milk): Milk.SteamedMilk {
    log("steaming milk")
    delay(1000)
    return Milk.SteamedMilk(milk)
}

private suspend fun makeCappuccino(
    order: Menu.Cappuccino,
    espresso: Espresso,
    steamedMilk: Milk.SteamedMilk
): Beverage.Cappuccino {
    log("making cappuccino")
    delay(500)
    return Beverage.Cappuccino(order, espresso, steamedMilk)
}

*/


/* [4] Update to use espresso machine
fun main() = runBlocking {
    val orders = listOf(
        Menu.Cappuccino(CoffeeBean.Regular, Milk.Whole),
        Menu.Cappuccino(CoffeeBean.Premium, Milk.Breve),
        Menu.Cappuccino(CoffeeBean.Regular, Milk.NonFat),
        Menu.Cappuccino(CoffeeBean.Decaf, Milk.Whole),
        Menu.Cappuccino(CoffeeBean.Regular, Milk.NonFat),
        Menu.Cappuccino(CoffeeBean.Decaf, Milk.NonFat)
    )

    orders.forEach { log(it) }

    val espressoMachine = EspressoMachine(this)

    val ordersChannel = produce(context = CoroutineName("cashier")) {
        orders.forEach {
            log("processing order: $it")
            send(it)
        }
    }

    val time = measureTimeMillis {
        coroutineScope {
            launch(CoroutineName("barista-1") + Dispatchers.Default) { processOrder(ordersChannel, espressoMachine) }
            launch(CoroutineName("barista-2") + Dispatchers.Default) { processOrder(ordersChannel, espressoMachine) }
        }
    }
    log("time: $time ms")
}

private suspend fun processOrder(ordersChannel: ReceiveChannel<Menu>, espressoMachine: EspressoMachine) {
    ordersChannel.consumeEach {
        val groundBeans = grindCoffeeBeans(it.beans())
        val espresso = espressoMachine.pullEspressoShot(groundBeans)
        val steamedMilk = espressoMachine.steamMilk(it.milk())
        val cappuccino = makeCappuccino(it as Menu.Cappuccino, espresso, steamedMilk)
        log("serve: $cappuccino")
    }
}

private suspend fun grindCoffeeBeans(beans: CoffeeBean): CoffeeBean.GroundBeans {
    log("grinding coffee beans")
    delay(3000)
    return CoffeeBean.GroundBeans(beans)
}


private suspend fun makeCappuccino(
    order: Menu.Cappuccino,
    espresso: Espresso,
    steamedMilk: Milk.SteamedMilk
): Beverage.Cappuccino {
    log("making cappuccino")
    delay(500)
    return Beverage.Cappuccino(order, espresso, steamedMilk)
}
*/


/* [5] Update to shutdown espresso machine and show that structured concurrency is important
fun main() = runBlocking {
    val orders = listOf(
        Menu.Cappuccino(CoffeeBean.Regular, Milk.Whole),
        Menu.Cappuccino(CoffeeBean.Premium, Milk.Breve),
        Menu.Cappuccino(CoffeeBean.Regular, Milk.NonFat),
        Menu.Cappuccino(CoffeeBean.Decaf, Milk.Whole),
        Menu.Cappuccino(CoffeeBean.Regular, Milk.NonFat),
        Menu.Cappuccino(CoffeeBean.Decaf, Milk.NonFat)
    )

    orders.forEach { log(it) }

    val espressoMachine = EspressoMachine(this)

    val ordersChannel = produce(context = CoroutineName("cashier")) {
        orders.forEach {
            log("processing order: $it")
            send(it)
        }
    }

    val time = measureTimeMillis {
        coroutineScope {
            launch(CoroutineName("barista-1") + Dispatchers.Default) { processOrder(ordersChannel, espressoMachine) }
            launch(CoroutineName("barista-2") + Dispatchers.Default) { processOrder(ordersChannel, espressoMachine) }
        }
    }
    log("time: $time ms")

    espressoMachine.destroy()
}

private suspend fun processOrder(ordersChannel: ReceiveChannel<Menu>, espressoMachine: EspressoMachine) {
    ordersChannel.consumeEach {
        val groundBeans = grindCoffeeBeans(it.beans())
        coroutineScope {
            val espressoDeferred = async { espressoMachine.pullEspressoShot(groundBeans) }
            val steamedMilkDeferred = async { espressoMachine.steamMilk(it.milk()) }
            val cappuccino =
                makeCappuccino(it as Menu.Cappuccino, espressoDeferred.await(), steamedMilkDeferred.await())
            log("serve: $cappuccino")
        }
    }
}

private suspend fun grindCoffeeBeans(beans: CoffeeBean): CoffeeBean.GroundBeans {
    log("grinding coffee beans")
    delay(3000)
    return CoffeeBean.GroundBeans(beans)
}


private suspend fun makeCappuccino(
    order: Menu.Cappuccino,
    espresso: Espresso,
    steamedMilk: Milk.SteamedMilk
): Beverage.Cappuccino {
    log("making cappuccino")
    delay(500)
    return Beverage.Cappuccino(order, espresso, steamedMilk)
}

*/

