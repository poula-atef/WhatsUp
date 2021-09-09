package com.example.whatsup.POJO.FCM;

import com.example.whatsup.POJO.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {
    private static Retrofit retrofit;
    private static FCMApi api;

    private static synchronized Retrofit getRetrofitIstance() {
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static synchronized FCMApi getApiInstance(){
        if(api == null){
            api = getRetrofitIstance().create(FCMApi.class);
        }
        return api;
    }
}
