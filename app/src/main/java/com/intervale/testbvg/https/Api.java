package com.intervale.testbvg.https;

import com.intervale.testbvg.https.model.PaymentRequest;
import com.intervale.testbvg.https.model.PaymentResponse;
import com.intervale.testbvg.https.model.Token;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Api {

    @POST("/token")
    Call<Token> getToken();


    @POST("/payment/{token}/start")
    Call<PaymentResponse> startPayment(@Path("token") String token, @Body PaymentRequest paymentRequest);

}
