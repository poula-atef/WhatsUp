package com.example.whatsup.POJO.FCM;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.View;

import androidx.navigation.Navigation;

import com.example.whatsup.POJO.Classes.CallData;
import com.example.whatsup.POJO.Classes.PushedCall;
import com.example.whatsup.POJO.Constants;
import com.example.whatsup.POJO.WhatsUpUtils;
import com.example.whatsup.R;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {
    private static Retrofit retrofit;
    private static FCMApi api;
    private static MediaPlayer player;
    private static Handler handler;
    private static Runnable runnable;

    private static synchronized Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static synchronized FCMApi getApiInstance() {
        if (api == null) {
            api = getRetrofitInstance().create(FCMApi.class);
        }
        return api;
    }

    public static synchronized void startMediaPlayer(Context context) {
        if (player == null)
            player = MediaPlayer.create(context, R.raw.light_of_xmas);
        player.start();
    }

    public static synchronized void stopMediaPlayer() {
        if (player == null)
            return;
        player.stop();
        player.release();
        player = null;
    }

    public static void setTimerForEndCall(String token, View view) {
        if (handler == null)
            handler = new Handler();
        if(runnable == null)
            runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        Client.getApiInstance().pushCall(new PushedCall(new
                                CallData(null, null, null, Constants.END_STATE, null)
                                , token))
                                .enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        Navigation.findNavController(view).navigate(R.id.action_outgoingCallFragment_to_mainFragment);
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                                    }
                                });


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

        handler.postDelayed(runnable, Constants.CALL_TIME_OUT);
    }

    public static void stopTimerForEndCall() {
        handler.removeCallbacks(runnable);
    }

}
