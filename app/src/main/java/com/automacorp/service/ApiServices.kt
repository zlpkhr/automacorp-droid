package com.automacorp.service

import android.annotation.SuppressLint
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

object ApiServices {
    private const val API_USERNAME = "user"
    private const val API_PASSWORD = "password"

    val roomsApiService: RoomsApiService by lazy {
        val client = getUnsafeOkHttpClient()
            .addInterceptor(BasicAuthInterceptor(API_USERNAME, API_PASSWORD))
            .build()

        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .baseUrl("http://automacorp.devmind.cleverapps.io/api/")
            .build()
            .create(RoomsApiService::class.java)
    }

    class BasicAuthInterceptor(private val username: String, private val password: String) :
        Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain
                .request()
                .newBuilder()
                .header("Authorization", Credentials.basic(username, password))
                .build()
            return chain.proceed(request)
        }
    }

    private fun getUnsafeOkHttpClient(): OkHttpClient.Builder =
        OkHttpClient.Builder().apply {
            val trustManager = @SuppressLint("CustomX509TrustManager")
            object : X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
            val sslContext = SSLContext.getInstance("SSL").also {
                it.init(null, arrayOf(trustManager), SecureRandom())
            }
            sslSocketFactory(sslContext.socketFactory, trustManager)
            hostnameVerifier { hostname, _ -> hostname.contains("cleverapps.io") }
            addInterceptor(BasicAuthInterceptor(API_USERNAME, API_PASSWORD))
        }

}