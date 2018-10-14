package com.intervale.testbvg.https;

import android.util.Log;

import com.intervale.testbvg.R;
import com.intervale.testbvg.https.logging.HttpLoggingInterceptor;
import com.intervale.testbvg.https.model.Dst;
import com.intervale.testbvg.https.model.Params;
import com.intervale.testbvg.https.model.PaymentRequest;
import com.intervale.testbvg.https.model.PaymentResponse;
import com.intervale.testbvg.https.model.Src;
import com.intervale.testbvg.https.model.Token;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpHelper {

    private static HttpHelper helper;
    private static Retrofit retrofit;
    private static Api api;

    public static synchronized HttpHelper getInstance() {
        if (helper == null) {
            helper = new HttpHelper();
        }
        return helper;
    }

    public void init(String url) {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();


        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(Api.class);
    }

    private String token;

    public void getToken(final HttpCallback httpCallback) {

        Call<Token> tokenCall = api.getToken();
        tokenCall.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful()) {
                    Log.d("HttpHelper", "response " + response.body());
                    httpCallback.response(response.body().getToken());
                } else {
                    httpCallback.error("Token Error","");
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                httpCallback.error("Token Error","Timeout");
            }
        });
    }

    public void startPayment(String token, final HttpCallback httpCallback) {

        PaymentRequest paymentRequest = new PaymentRequest(new Src(),new Dst(),new Params(),
                "200","1000","12", "http://ok");
        Call<PaymentResponse> responseCall = api.startPayment(token,paymentRequest);
        responseCall.enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("HttpHelper", "response " + response.body());
                    httpCallback.response(response.body().toString());
                } else {
                    httpCallback.error("Token Error","");
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                httpCallback.error("Token Error","");
            }
        });
    }

    public interface HttpCallback {
        void response(String response);

        void error(String error, String description);
    }

}
