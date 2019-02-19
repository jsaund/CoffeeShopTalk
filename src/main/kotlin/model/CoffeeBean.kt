package model

sealed class CoffeeBean {
    abstract fun price(): Float

    object Premium: CoffeeBean() {
        override fun price() = 1.00f
        override fun toString() = "premium"
    }

    object Regular: CoffeeBean() {
        override fun price() = 0.00f
        override fun toString() = "regular"
    }

    object Decaf: CoffeeBean() {
        override fun price() = 0.50f
        override fun toString() = "decaf"
    }

    data class GroundBeans(val coffeeBean: CoffeeBean): CoffeeBean() {
        override fun price() = 0.00f
        override fun toString() = "ground $coffeeBean"
    }
}