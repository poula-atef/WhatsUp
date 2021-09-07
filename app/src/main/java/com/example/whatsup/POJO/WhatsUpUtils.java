package com.example.whatsup.POJO;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.whatsup.POJO.Classes.Friend;
import com.example.whatsup.POJO.Classes.Message;
import com.example.whatsup.POJO.Classes.User;
import com.example.whatsup.R;
import com.example.whatsup.UI.ConfirmationFragment;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class WhatsUpUtils {

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
        String imageUrl = PreferenceManager.getDefaultSharedPreferences(context).getString("profile", "");
        User user = new User(userName, birthDate, phoneNumber, true, null, imageUrl, uid, "online");
        reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(context, "Unexpected Error Happened, try again later !!", Toast.LENGTH_SHORT).show();
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


    public static void setUserFriendLastMessage(Message lastMessage, String userId, User user, Context context, String senderId) {

        Friend friend = new Friend();
        friend.setSenderId(userId);
        friend.setLastDate(lastMessage.getTime());
        friend.setUserName(user.getUserName());
        friend.setProfileImageUrl(user.getImageUrl());
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
                            if (shot.getKey().equals(user.getUserId())) {
                                Friend frnd = shot.getValue(Friend.class);
                                if (frnd.getSenderId().equals(senderId)
                                        || (frnd.getSeen() == 2
                                        && frnd.getLastMessage().equals(friend.getLastMessage()))) {
                                    friend.setSeen(2);
                                } else {
                                    friend.setSeen(1);
                                }
                                FirebaseDatabase.getInstance()
                                        .getReference("users")
                                        .child(userId)
                                        .child("friends")
                                        .child(user.getUserId())
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


}
