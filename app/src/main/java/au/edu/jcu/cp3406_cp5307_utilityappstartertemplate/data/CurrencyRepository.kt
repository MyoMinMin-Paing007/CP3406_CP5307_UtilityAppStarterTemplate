package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data

import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.network.ExchangeRateResponse
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.network.RetrofitInstance

class CurrencyRepository {
    suspend fun getRates(baseCurrency: String): ExchangeRateResponse {
        return RetrofitInstance.api.getRates(baseCurrency)
    }
}