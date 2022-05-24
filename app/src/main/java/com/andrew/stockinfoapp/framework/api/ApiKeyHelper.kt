package com.andrew.stockinfoapp.framework.api

import com.andrew.stockinfoapp.domain.Constants

class ApiKeyHelper {
    companion object {
        var index = 0

        // public key can only be used every so often, so switch between 2 keys
        fun getApiKey() = if (index++ % 2 == 0) Constants.API_KEY_1 else Constants.API_KEY_2
    }
}