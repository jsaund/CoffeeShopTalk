
import kotlinx.coroutines.*
import model.*
import util.log
import kotlin.system.measureTimeMillis

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

