package com.test

class UntestedService {
    
    fun processData(data: String): String {
        return data.uppercase()
    }
    
    fun validateInput(input: String): Boolean {
        return input.isNotBlank() && input.length > 3
    }
    
    fun complexCalculation(x: Int, y: Int): Int {
        val intermediate = x * 2 + y * 3
        return if (intermediate > 100) {
            intermediate / 2
        } else {
            intermediate * 2
        }
    }
}