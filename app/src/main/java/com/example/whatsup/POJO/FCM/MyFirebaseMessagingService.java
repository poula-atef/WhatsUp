package com.example.whatsup.POJO.FCM;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.navigation.NavDeepLinkBuilder;

import com.example.whatsup.POJO.Classes.CallData;
import com.example.whatsup.POJO.Classes.PushedCall;
import com.example.whatsup.POJO.Constants;
import com.example.whatsup.POJO.WhatsUpUtils;
import com.example.whatsup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        if (message.getData().get("callType") != null
                || message.getData().get("state") != null) {
            handleCall(message);
        } else {
            String imgUrl = PreferenceManager.getDefaultSharedPreferences(this).getString(Constants.CURRENT_FRIEND_CHAT, "?");
            if (!imgUrl.equals("?") && imgUrl.equals(message.getData().get("imgUrl")))
                return;
            WhatsUpUtils.createNotification(message, this, null);
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDatabase
                    .getInstance()
                    .getReference("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("token")
                    .setValue(s);
        }
    }

    private void handleCall(RemoteMessage message) {
        if (message.getData().get("state") == null) {
            try {
                Bundle bundle = new Bundle();
                bundle.putString("sender_name", message.getData().get("senderName"));
                bundle.putString("sender_number", message.getData().get("senderNumber"));
                bundle.putString("call_type", message.getData().get("callType"));
                bundle.putString("sender_token", message.getData().get("senderToken"));

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                String myToken = preferences.getString(Constants.MY_TOKEN, "?");
                String userName = preferences.getString(Constants.USER_NAME, "?");
                String userPhone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

                Client.getApiInstance().pushCall(new PushedCall(new
                        CallData(userName, userPhone, bundle.getString("call_type"), Constants.RINGING_STATE, myToken)
                        , bundle.getString("sender_token")))
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });

                Client.startMediaPlayer(getApplicationContext());

                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("call",true).apply();

                new NavDeepLinkBuilder(getApplicationContext())
                        .setGraph(R.navigation.whatsup_nav)
                        .setArguments(bundle)
                        .setDestination(R.id.incomingCallFragment)
                        .createPendingIntent()
                        .send();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (message.getData().get("state").equals(Constants.REJECT_STATE)) {
                try {
                    Client.stopMediaPlayer();
                    new NavDeepLinkBuilder(getApplicationContext())
                            .setGraph(R.navigation.whatsup_nav)
                            .setDestination(R.id.mainFragment)
                            .createPendingIntent()
                            .send();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (message.getData().get("state").equals(Constants.ACCEPT_STATE)) {
                try {
                    Client.stopMediaPlayer();
                    String token = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(Constants.MY_TOKEN, "?");
                    new NavDeepLinkBuilder(getApplicationContext())
                            .setGraph(R.navigation.whatsup_nav)
                            .setDestination(R.id.mainFragment)
                            .createPendingIntent()
                            .send();
                    WhatsUpUtils.startCall(getApplicationContext(), message.getData().get("callType"), token);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (message.getData().get("state").equals(Constants.RINGING_STATE)) {
                try {
                    Bundle bundle = new Bundle();
                    bundle.putString("receiver_name", message.getData().get("senderName"));
                    bundle.putString("receiver_number", message.getData().get("senderNumber"));
                    bundle.putString("call_type", message.getData().get("callType"));
                    bundle.putString("receiver_token", message.getData().get("senderToken"));
                    bundle.putString("state", Constants.RINGING_STATE);

                    PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("call",true).apply();

                    new NavDeepLinkBuilder(getApplicationContext())
                            .setGraph(R.navigation.whatsup_nav)
                            .setDestination(R.id.outgoingCallFragment)
                            .setArguments(bundle)
                            .createPendingIntent()
                            .send();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (message.getData().get("state").equals(Constants.END_STATE)) {
                try {
                    Client.stopMediaPlayer();
                    new NavDeepLinkBuilder(getApplicationContext())
                            .setGraph(R.navigation.whatsup_nav)
                            .setDestination(R.id.mainFragment)
                            .createPendingIntent()
                            .send();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
