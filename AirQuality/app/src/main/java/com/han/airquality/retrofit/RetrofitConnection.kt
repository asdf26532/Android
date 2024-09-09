package com.han.airquality.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitConnection {
    companion object {
        private const val BASE_URL = "https://api.airvisual.com/v2/"
        private var INSTANCE:Retrofit? = null

        fun getInstance() : Retrofit {
            if(INSTANCE == null) {
                // Logging Interceptor 추가
                val logging = HttpLoggingInterceptor()
                logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                val httpClient = OkHttpClient.Builder()
                httpClient.addInterceptor(logging)

                INSTANCE = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient.build())  // OkHttp 클라이언트 추가
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return INSTANCE!!
        }
    }
}