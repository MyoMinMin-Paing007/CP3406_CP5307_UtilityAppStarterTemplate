package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

data class ExchangeRateResponse(
    @com.google.gson.annotations.SerializedName("base_code")
    val baseCode: String,
    @com.google.gson.annotations.SerializedName("rates")
    val rates: Map<String, Double>
)

interface CurrencyApiService {
    @GET("latest/{baseCurrency}")
    suspend fun getRates(@Path("baseCurrency") baseCurrency: String): ExchangeRateResponse
}

object RetrofitInstance {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://open.er-api.com/v6/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: CurrencyApiService = retrofit.create(CurrencyApiService::class.java)
}