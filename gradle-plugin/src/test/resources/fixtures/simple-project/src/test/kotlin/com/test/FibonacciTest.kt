package com.test

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.jvm.java

class FibonacciTest {
    
    private lateinit var fibonacci: Fibonacci
    
    @BeforeEach
    fun setUp() {
        fibonacci = Fibonacci()
    }
    
    @Nested
    @DisplayName("Calculate single Fibonacci number")
    inner class CalculateTests {
        
        @Test
        @DisplayName("Should return 0 for n=0")
        fun `calculate returns 0 for n equals 0`() {
            assertThat(fibonacci.calculate(0)).isEqualTo(0)
        }
        
        @Test
        @DisplayName("Should return 1 for n=1")
        fun `calculate returns 1 for n equals 1`() {
            assertThat(fibonacci.calculate(1)).isEqualTo(1)
        }
        
        @ParameterizedTest
        @DisplayName("Should calculate correct Fibonacci numbers")
        @CsvSource(
            "2, 1",
            "3, 2",
            "4, 3",
            "5, 5",
            "6, 8",
            "7, 13",
            "8, 21",
            "9, 34",
            "10, 55",
            "15, 610",
            "20, 6765"
        )
        fun `calculate returns correct Fibonacci number`(n: Int, expected: Long) {
            assertThat(fibonacci.calculate(n))
                .`as`("Fibonacci number at position $n")
                .isEqualTo(expected)
        }
        
        @Test
        @DisplayName("Should throw exception for negative input")
        fun `calculate throws exception for negative input`() {
            assertThatThrownBy { fibonacci.calculate(-1) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("negative")
        }
    }
    
    @Nested
    @DisplayName("Generate Fibonacci sequence")
    inner class SequenceTests {
        
        @Test
        @DisplayName("Should return empty list for count=0")
        fun `sequence returns empty list for count 0`() {
            assertThat(fibonacci.sequence(0)).isEmpty()
        }
        
        @Test
        @DisplayName("Should return correct sequence for small counts")
        fun `sequence returns correct values for small counts`() {
            assertThat(fibonacci.sequence(1)).containsExactly(0)
            assertThat(fibonacci.sequence(2)).containsExactly(0, 1)
            assertThat(fibonacci.sequence(5)).containsExactly(0, 1, 1, 2, 3)
        }
        
        @Test
        @DisplayName("Should return correct sequence for larger count")
        fun `sequence returns correct values for count 10`() {
            val expected = listOf<Long>(0, 1, 1, 2, 3, 5, 8, 13, 21, 34)
            
            assertThat(fibonacci.sequence(10))
                .hasSize(10)
                .containsExactlyElementsOf(expected)
        }
        
        @Test
        @DisplayName("Should throw exception for negative count")
        fun `sequence throws exception for negative count`() {
            assertThatThrownBy { fibonacci.sequence(-1) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessageContaining("non-negative")
        }
    }
}