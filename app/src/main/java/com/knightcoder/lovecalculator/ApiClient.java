package com.knightcoder.lovecalculator;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface ApiClient {
    @Headers(
            {
             //your api_key from rapidapi.com
             "x-rapidapi-key:API_KEY",// like this one =>c411f451bamsh3de1f010a9f8489p198102jsn59735d62cd18
             "x-rapidapi-host:love-calculator.p.rapidapi.com"
            }
    )
    @GET("getPercentage")
    Call<Lovers> getLoversPercentage(@Query("fname") String fname, @Query("sname") String sname);
}
