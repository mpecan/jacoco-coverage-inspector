package com.moduleb

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ServiceBTest {
    
    @Test
    fun `calculate returns sum`() {
        val service = ServiceB()
        assertThat(service.calculate(2, 3)).isEqualTo(5)
    }
}