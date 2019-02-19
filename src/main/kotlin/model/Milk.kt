package model

sealed class Milk {
    abstract fun price(): Float

    object Whole: Milk() {
        override fun price() = 0.00f

        override fun toString() = "whole milk"
    }

    object NonFat: Milk() {
        override fun price() = 0.00f

        override fun toString() = "non-fat milk"
    }

    object Breve: Milk() {
        override fun price() = 1.00f

        override fun toString() = "breve"
    }

    data class SteamedMilk(val milk: Milk): Milk() {
        override fun price() = 0.00f

        override fun toString() = "steamed milk"
    }
}