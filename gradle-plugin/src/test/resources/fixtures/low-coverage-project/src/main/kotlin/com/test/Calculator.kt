package com.test

class Calculator {
    
    fun add(a: Int, b: Int): Int {
        return a + b
    }
    
    fun subtract(a: Int, b: Int): Int {
        return a - b
    }
    
    fun multiply(a: Int, b: Int): Int {
        return a * b
    }
    
    fun divide(a: Int, b: Int): Int {
        if (b == 0) {
            throw IllegalArgumentException("Division by zero")
        }
        return a / b
    }
    
    fun power(base: Int, exponent: Int): Int {
        if (exponent < 0) {
            throw IllegalArgumentException("Negative exponent not supported")
        }
        
        var result = 1
        repeat(exponent) {
            result *= base
        }
        return result
    }
    
    fun factorial(n: Int): Long {
        if (n < 0) {
            throw IllegalArgumentException("Factorial of negative number")
        }
        
        var result = 1L
        for (i in 1..n) {
            result *= i
        }
        return result
    }
    
    fun isPrime(n: Int): Boolean {
        if (n < 2) return false
        if (n == 2) return true
        if (n % 2 == 0) return false
        
        for (i in 3..kotlin.math.sqrt(n.toDouble()).toInt() step 2) {
            if (n % i == 0) return false
        }
        return true
    }
}