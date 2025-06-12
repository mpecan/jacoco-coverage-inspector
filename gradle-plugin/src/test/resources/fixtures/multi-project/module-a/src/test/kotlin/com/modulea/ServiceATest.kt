package com.modulea

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ServiceATest {
    
    @Test
    fun `processRequest returns formatted string`() {
        val service = ServiceA()
        assertThat(service.processRequest("test")).isEqualTo("Processed: test")
    }
    
    @Test
    fun `validateData returns true for non-blank`() {
        val service = ServiceA()
        assertThat(service.validateData("data")).isTrue()
    }
}