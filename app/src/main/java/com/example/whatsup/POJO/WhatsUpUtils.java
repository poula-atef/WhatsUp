package com.example.whatsup.POJO;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.whatsup.POJO.Classes.Friend;
import com.example.whatsup.POJO.Classes.Message;
import com.example.whatsup.POJO.Classes.NotificationData;
import com.example.whatsup.POJO.Classes.PushedNotification;
import com.example.whatsup.POJO.Classes.User;
import com.example.whatsup.POJO.FCM.Client;
import com.example.whatsup.POJO.FCM.NotificationBroadcastReceiver;
import com.example.whatsup.R;
import com.example.whatsup.UI.ConfirmationFragment;
import com.example.whatsup.UI.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.example.whatsup.UI.MainFragment.OnChildChangeListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WhatsUpUtils {

    static String TAG = "tag";

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void showDatePickerDialog(Context context, DatePickerDialog.OnDateSetListener onDateSetListener) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        DatePickerDialog dialog = new DatePickerDialog(context, R.style.MyDatePickerStyle);
        dialog.updateDate(year, month, day);
        dialog.setOnDateSetListener(onDateSetListener);
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context.getColor(R.color.blue1));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(context.getColor(R.color.blue1));

    }

    public static void signUpWithPhoneNumber(OnChildChangeListener listener, String countryCode, String phoneNumber, Activity activity) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                String TAG = "tag";
                Log.d(TAG, "onVerificationFailed: " + e.getMessage());
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);

                listener.onChildChangeWithoutStack(new ConfirmationFragment(verificationId));
            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+" + countryCode + phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(activity)
                        .setCallbacks(mCallbacks)
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public static void setUserDataToFirebaseDatabase(String userName, String birthDate, String phoneNumber, String uid, Context context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("users").child(uid);
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    String imageUrl = PreferenceManager.getDefaultSharedPreferences(context).getString("profile", "");
                    User user = new User(userName, birthDate, phoneNumber, true, null, imageUrl, uid, "online", task.getResult());
                    reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(context, "Unexpected Error Happened, try again later !!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    public static void saveImageInFirebaseStorage(Uri imageUri, Context context, Activity activity) {

        ((ImageView) activity.findViewById(R.id.right_arrow)).setVisibility(View.GONE);
        ((ProgressBar) activity.findViewById(R.id.pb)).setVisibility(View.VISIBLE);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference("profile_images").child(FirebaseAuth.getInstance().getCurrentUser().getUid() + "_profile");
        reference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    reference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("profile", task.getResult().toString()).apply();
                                ((ImageView) activity.findViewById(R.id.right_arrow)).setVisibility(View.VISIBLE);
                                ((ProgressBar) activity.findViewById(R.id.pb)).setVisibility(View.GONE);
                            }
                        }
                    });
                } else {
                    Toast.makeText(context, "Unexpected Error Happened, try again later !!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static String getCurrentTimeFormat() {
        Date date = Calendar.getInstance().getTime();
        String type = "am";
        int hour = date.getHours();
        if (hour > 12) {
            hour -= 12;
            type = "pm";
        } else if (hour == 0) {
            hour = 12;
        }
        int minute = date.getMinutes();

        return hour + ":" + minute + " " + type;
    }

    public static void makeMeOnline() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.child("active").setValue(true);
        reference.child("lastSeen").setValue("online");

    }

    public static void makeMeOffline() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.child("active").setValue(false);
        reference.child("lastSeen").setValue(getCurrentTimeFormat());

    }


    public static void sendImageMessage(Uri imageUri, Context context, User user) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference("messages_images")
                .child(Calendar.getInstance().getTime().getTime() + "_" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "_img");
        reference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    reference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Message message = new Message(FirebaseAuth.getInstance().getCurrentUser().getUid(), user.getUserId()
                                        , "", true, getCurrentTimeFormat(), task.getResult().toString(), 1);
                                sendMessage(message, context, user);
                            }
                        }
                    });
                } else {
                    Toast.makeText(context, "Unexpected Error Happened, try again later !!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public static void sendMessage(Message message, Context context, User user) {


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("messages");


        reference
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(user.getUserId())
                .push()
                .setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(context, "Unexpected Error Happened, try again later !!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        reference
                .child(user.getUserId())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .push()
                .setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(context, "Unexpected Error Happened, try again later !!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    public static void setUserFriendLastMessage(Message lastMessage, String userId, User currentUser, Context context, String senderId, String receiverToken) {

        Friend friend = new Friend();
        friend.setSenderId(userId);
        friend.setLastDate(lastMessage.getTime());
        friend.setUserName(currentUser.getUserName());
        friend.setProfileImageUrl(currentUser.getImageUrl());
        if (lastMessage.isImg())
            friend.setLastMessage(context.getString(R.string.WHATSUP_IMG_CONSTANT));
        else {
            if (lastMessage.getSenderId().equals(userId)) {
                friend.setLastMessage("You: " + lastMessage.getMessage());
            } else {
                friend.setLastMessage(lastMessage.getMessage());
            }
        }
        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("friends")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot shot : snapshot.getChildren()) {
                            if (shot.getKey().equals(currentUser.getUserId())) {
                                Friend frnd = shot.getValue(Friend.class);
                                if (frnd.getSenderId().equals(senderId)
                                        || (frnd.getSeen() == 2 && frnd.getLastMessage().equals(friend.getLastMessage()))) {
                                    friend.setSeen(2);
                                } else {
                                    friend.setSeen(1);
                                    if (!currentUser.getToken().equals(receiverToken)) {
                                        pushNotification(new PushedNotification(new NotificationData(
                                                currentUser.getUserName()
                                                , friend.getLastMessage()
                                                , currentUser.getImageUrl()
                                                ,Long.parseLong(currentUser.getPhoneNumber().substring(1)))
                                                , receiverToken));
                                    }
                                }
                                FirebaseDatabase.getInstance()
                                        .getReference("users")
                                        .child(userId)
                                        .child("friends")
                                        .child(currentUser.getUserId())
                                        .setValue(friend);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    public static void pushNotification(PushedNotification notification) {
        try {
            Client.getApiInstance().pushNotification(notification).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "task success: " + response.code());
                    } else {
                        Log.d(TAG, "task not success: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            Log.d(TAG, "task not success: " + e.getMessage());
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void createNotification(RemoteMessage message, Context context) {

        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            createNotificationChannel(manager);
        }

        Glide.with(context).asBitmap().load(message.getData().get("imgUrl")).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                Intent intent = new Intent(context, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent
                        .getActivities(context, 0, new Intent[]{intent}, PendingIntent.FLAG_UPDATE_CURRENT);

                int notificationId = (int)(Long.parseLong(message.getData().get("senderNumber"))/100000);

                Notification notification = new NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                        .setContentTitle(message.getData().get("title"))
                        .setContentText(message.getData().get("message"))
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.ic_message)
                        .setLargeIcon(resource)
                        .addAction(createReplayAction(context,notificationId))
                        .addAction(createSeenAction(context,notificationId))
                        .build();

                manager.notify(notificationId, notification);

            }
        });
    }

    private static NotificationCompat.Action createSeenAction(Context context, int notificationId) {
        Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
        intent.setAction(Constants.SEEN);
        intent.putExtra("notification_id",notificationId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(null, Constants.SEEN, pendingIntent).build();
        return action;
    }

    private static NotificationCompat.Action createReplayAction(Context context, int notificationId) {
        RemoteInput remoteInput = new RemoteInput.Builder(Constants.REPLAY_RESULT_KEY).setLabel("Replay..").build();
        Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
        intent.setAction(Constants.REPLAY);
        intent.putExtra("notification_id",notificationId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(null, Constants.REPLAY, pendingIntent).addRemoteInput(remoteInput).build();
        return action;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void createNotificationChannel(NotificationManager manager) {
        NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(true);
        channel.setLightColor(Color.GREEN);
        channel.setDescription("channel_description");
        manager.createNotificationChannel(channel);
    }


    public static void closeAnyNotification(Context context, int notificationId){
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
    }

}
