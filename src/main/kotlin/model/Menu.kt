package model

import util.format

sealed class Menu {
    abstract fun price(): Float

    abstract fun beans(): CoffeeBean

    abstract fun milk(): Milk

    data class Cappuccino(val beans: CoffeeBean, val milk: Milk): Menu() {
        override fun price() = 3.50f + beans.price() + milk.price()

        override fun beans() = beans

        override fun milk() = milk

        override fun toString() = "cappuccino: beans=$beans milk=$milk price=$${price().format(2)}"
    }
}