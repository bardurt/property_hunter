package com.bardur.domus.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class RequestPayload(
    val query: List<Query>,
    val response: Response
)

data class Query(
    val code: String,
    val selection: Selection
)

data class Selection(
    val filter: String,
    val values: List<String>
)

data class Response(
    val format: String
)

data class StatbankResponse(
    val columns: List<Column>,
    val comments: List<String>,
    val data: List<DataItem>,
    val metadata: List<MetadataItem>
)

data class Column(
    val code: String,
    val text: String,
    val type: String,
    val comment: String? = null
)

data class DataItem(
    val key: List<String>,
    val values: List<Double>
)

data class MetadataItem(
    val updated: String,
    val label: String,
    val source: String
)


interface StatbankApi {

    @POST("api/v1/fo/H2/AM/LON/LON02/lon_mv_hov_ar.px")
    suspend fun getStatistics(
        @Body payload: RequestPayload
    ): retrofit2.Response<StatbankResponse> // Replace Any with a proper data class if you know the response structure
}


object RetrofitClient {

    private val logging = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val instance: StatbankApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://statbank.hagstova.fo/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(StatbankApi::class.java)
    }
}