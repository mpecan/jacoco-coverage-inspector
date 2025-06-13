package com.modulea

class ServiceA {
    
    fun processRequest(input: String): String {
        return "Processed: $input"
    }
    
    fun validateData(data: String): Boolean {
        return data.isNotBlank()
    }
}