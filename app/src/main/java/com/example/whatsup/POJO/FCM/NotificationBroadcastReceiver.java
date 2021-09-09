package com.example.whatsup.POJO.FCM;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.whatsup.POJO.Constants;
import com.example.whatsup.POJO.WhatsUpUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    String TAG = "tag";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: " + intent.getAction());
        if (intent.getAction().equals(Constants.SEEN)) {
            String senderId = intent.getStringExtra("sender_id");
            FirebaseDatabase
                    .getInstance()
                    .getReference("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("friends")
                    .child(senderId)
                    .child("seen")
                    .setValue(2);
            WhatsUpUtils.markMessagesAsSeen(FirebaseAuth.getInstance().getCurrentUser().getUid(), senderId);
            WhatsUpUtils.markMessagesAsSeen(senderId, FirebaseAuth.getInstance().getCurrentUser().getUid());
        } else if (intent.getAction().equals(Constants.REPLAY)) {

        }
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(intent.getIntExtra("notification_id", 1));
    }
}
