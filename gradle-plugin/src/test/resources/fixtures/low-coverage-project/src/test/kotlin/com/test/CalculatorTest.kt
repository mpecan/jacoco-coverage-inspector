package com.test

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CalculatorTest {
    
    private lateinit var calculator: Calculator
    
    @BeforeEach
    fun setUp() {
        calculator = Calculator()
    }
    
    @Test
    fun `add returns correct sum`() {
        assertThat(calculator.add(2, 3)).isEqualTo(5)
    }
    
    @Test
    fun `subtract returns correct difference`() {
        assertThat(calculator.subtract(5, 3)).isEqualTo(2)
    }
}