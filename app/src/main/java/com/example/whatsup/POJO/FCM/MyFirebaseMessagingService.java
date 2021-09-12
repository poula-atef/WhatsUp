package com.example.whatsup.POJO.FCM;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.whatsup.POJO.Constants;
import com.example.whatsup.POJO.WhatsUpUtils;
import com.example.whatsup.R;
import com.example.whatsup.UI.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        String imgUrl = PreferenceManager.getDefaultSharedPreferences(this).getString(Constants.CURRENT_FRIEND_CHAT, "?");
        if (!imgUrl.equals("?") && imgUrl.equals(message.getData().get("imgUrl")))
            return;
        WhatsUpUtils.createNotification(message, this,null);
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
}
