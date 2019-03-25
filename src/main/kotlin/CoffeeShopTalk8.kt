import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import model.*
import util.log
import java.lang.Thread.sleep
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

    val ordersChannel = produce(CoroutineName("cashier")) {
        orders.forEach { send(it) }
    }

    val time = measureTimeMillis {
        coroutineScope {
            launch(CoroutineName("barista-1")) { processOrders(ordersChannel) }
            launch(CoroutineName("barista-2")) { processOrders(ordersChannel) }
        }
    }
    log("time: $time ms")
}

private suspend fun processOrders(orders: ReceiveChannel<Menu.Cappuccino>) {
    orders.consumeEach {
        val groundBeans = grindCoffeeBeans(it.beans())
        val espresso = pullEspressoShot(groundBeans)
        val steamedMilk = steamMilk(it.milk())
        val cappuccino = makeCappuccino(it, espresso, steamedMilk)
        log("serve: $cappuccino")
    }
}

private suspend fun grindCoffeeBeans(beans: CoffeeBean): CoffeeBean.GroundBeans {
    log("grinding coffee beans")
    delay(1000)
    return CoffeeBean.GroundBeans(beans)
}

private suspend fun pullEspressoShot(groundBeans: CoffeeBean.GroundBeans): Espresso {
    log("pulling espresso shot")
    delay(600)
    return Espresso(groundBeans)
}

private suspend fun steamMilk(milk: Milk): Milk.SteamedMilk {
    log("steaming milk")
    delay(300)
    return Milk.SteamedMilk(milk)
}

private suspend fun makeCappuccino(order: Menu.Cappuccino, espresso: Espresso,
    steamedMilk: Milk.SteamedMilk): Beverage.Cappuccino {
    log("making cappuccino")
    delay(100)
    return Beverage.Cappuccino(order, espresso, steamedMilk)
}
