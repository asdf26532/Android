package com.han.airquality.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitConnection {
    companion object {
        private const val BASE_URL = "http://api.airvisual.com/v2"
        private var INSTANCE:Retrofit? = null

        fun getInstance() : Retrofit {
            if(INSTANCE == null) {
                INSTANCE = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return INSTANCE!!
        }
    }
}