package com.example.whatsup.POJO.FCM;

import android.app.NotificationManager;
import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.whatsup.POJO.Classes.Message;
import com.example.whatsup.POJO.Classes.NotificationData;
import com.example.whatsup.POJO.Classes.PushedNotification;
import com.example.whatsup.POJO.Constants;
import com.example.whatsup.POJO.WhatsUpUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    String TAG = "tag";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: " + intent.getAction());
        String senderId = intent.getStringExtra("sender_id");
        if (intent.getAction().equals(Constants.SEEN)) {

            makeFirebaseReferenceInstance(FirebaseAuth.getInstance().getCurrentUser().getUid(), senderId)
                    .child("seen")
                    .setValue(2);

        } else if (intent.getAction().equals(Constants.REPLAY)) {

            Bundle bundle = RemoteInput.getResultsFromIntent(intent);
            if (bundle == null)
                return;
            FirebaseDatabase
                    .getInstance()
                    .getReference("messages")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(senderId)
                    .push()
                    .setValue(new Message(FirebaseAuth.getInstance().getCurrentUser().getUid()
                            , senderId, bundle.getString(Constants.REPLAY_RESULT_KEY), false, WhatsUpUtils.getCurrentTimeFormat(), "", 2));

            FirebaseDatabase
                    .getInstance()
                    .getReference("messages")
                    .child(senderId)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .push()
                    .setValue(new Message(FirebaseAuth.getInstance().getCurrentUser().getUid()
                            , senderId, bundle.getString(Constants.REPLAY_RESULT_KEY), false, WhatsUpUtils.getCurrentTimeFormat(), "", 1));

            makeFirebaseReferenceInstance(FirebaseAuth.getInstance().getCurrentUser().getUid(), senderId)
                    .child("seen")
                    .setValue(2);

            makeFirebaseReferenceInstance(senderId, FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("seen")
                    .setValue(1);

            makeFirebaseReferenceInstance(senderId, FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("lastMessage")
                    .setValue(bundle.getString(Constants.REPLAY_RESULT_KEY));

            makeFirebaseReferenceInstance(FirebaseAuth.getInstance().getCurrentUser().getUid(), senderId)
                    .child("lastMessage")
                    .setValue("You: " + bundle.getString(Constants.REPLAY_RESULT_KEY));

            makeFirebaseReferenceInstance(senderId, FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("lastDate")
                    .setValue(WhatsUpUtils.getCurrentTimeFormat());

            makeFirebaseReferenceInstance(FirebaseAuth.getInstance().getCurrentUser().getUid(), senderId)
                    .child("lastDate")
                    .setValue(WhatsUpUtils.getCurrentTimeFormat());

            WhatsUpUtils.pushNotification(new PushedNotification(new NotificationData(
                    FirebaseAuth.getInstance().getCurrentUser().getUid()
                    , bundle.getString(Constants.REPLAY_RESULT_KEY)
                    , PreferenceManager.getDefaultSharedPreferences(context).getString("profile", "?")
                    , Long.parseLong(PreferenceManager.getDefaultSharedPreferences(context).getString("user_name", "?").substring(1))
                    , senderId)
                    , PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.CURRENT_FRIEND_TOKEN, "?")));
        }
        WhatsUpUtils.markMessagesAsSeen(FirebaseAuth.getInstance().getCurrentUser().getUid(), senderId);
        WhatsUpUtils.markMessagesAsSeen(senderId, FirebaseAuth.getInstance().getCurrentUser().getUid());
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(intent.getIntExtra("notification_id", 1));
    }

    private DatabaseReference makeFirebaseReferenceInstance(String firstUserId, String secondUserId) {
        return FirebaseDatabase
                .getInstance()
                .getReference("users")
                .child(firstUserId)
                .child("friends")
                .child(secondUserId);
    }

}
