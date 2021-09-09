package com.example.whatsup.POJO.FCM;

import com.example.whatsup.POJO.Classes.PushedNotification;
import com.example.whatsup.POJO.Constants;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface FCMApi {

    @Headers({"Authorization: key=" + Constants.SERVER_KEY,"Content-Type: " + Constants.CONTENT_TYPE})
    @POST("fcm/send")
    Call<ResponseBody> pushNotification(@Body PushedNotification notification);
}
