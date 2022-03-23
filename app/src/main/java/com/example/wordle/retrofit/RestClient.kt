package com.example.wordle.retrofit

import com.example.wordle.Constant
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class RestClient {

    private val loggingInterceptor by lazy {
        HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    private val authorizationInterceptor by lazy {
        AuthorizationInterceptor()
    }

    private val okhttpClient = OkHttpClient.Builder()
        .connectTimeout(2, TimeUnit.MINUTES) //요청을 시작한 후 서버와의 TCP handshake가 완료되기까지 지속되는 시간이다.
        .readTimeout(
            2,
            TimeUnit.MINUTES
        ) // 서버로부터 바이트가 전송되는 속도. 서버로부터의 응답까지의 시간이 읽기 시간 초과보다 크면 요청이 실패로 계산된다.
        .writeTimeout(2, TimeUnit.MINUTES) //얼마나 빨리 서버에 바이트를 보낼 수 있는지
        .addInterceptor(loggingInterceptor)
       // .addInterceptor(authorizationInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(Constant.BASE_URL)
        .client(okhttpClient)
        .addConverterFactory(TikXmlConverterFactory.create())
        .build()

    val service = retrofit.create(RetrofitService::class.java)

}