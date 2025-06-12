package com.test

import kotlin.collections.forEach
import kotlin.collections.map
import kotlin.ranges.until

class Fibonacci {
    
    fun calculate(n: Int): Long {
        require(n >= 0) { "Fibonacci number cannot be calculated for negative values" }
        
        return when (n) {
            0 -> 0
            1 -> 1
            else -> calculateIterative(n)
        }
    }
    
    private fun calculateIterative(n: Int): Long {
        var prev = 0L
        var current = 1L

        (2..n).forEach { i ->
            val next = prev + current
            prev = current
            current = next
        }
        
        return current
    }
    
    fun sequence(count: Int): List<Long> {
        require(count >= 0) { "Count must be non-negative" }
        
        return (0 until count).map { calculate(it) }
    }
}