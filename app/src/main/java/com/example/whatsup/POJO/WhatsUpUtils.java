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

import com.example.whatsup.POJO.Classes.User;
import com.example.whatsup.R;
import com.example.whatsup.UI.ConfirmationFragment;
import com.example.whatsup.UI.MainFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.example.whatsup.UI.MainFragment.OnChildChangeListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
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

                listener.onChildChange(new ConfirmationFragment(verificationId));
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

    public static void setUserDataToFirebaseDatabase(String userName, String birthDate, String phoneNumber, String uid,Context context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("users").child(uid);
        String imageUrl = PreferenceManager.getDefaultSharedPreferences(context).getString("profile","");
        User user = new User(userName,birthDate,phoneNumber,null,imageUrl);
        reference.push().setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(context, "Unexpected Error Happened, try again later !!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void saveImageInFirebaseStorage(Uri imageUri,Context context,Activity activity) {

        ((ImageView)activity.findViewById(R.id.right_arrow)).setVisibility(View.GONE);
        ((ProgressBar)activity.findViewById(R.id.pb)).setVisibility(View.VISIBLE);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference("profile_images").child(FirebaseAuth.getInstance().getCurrentUser().getUid()+ "_profile");
        reference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    reference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("profile",task.getResult().toString()).apply();
                                ((ImageView)activity.findViewById(R.id.right_arrow)).setVisibility(View.VISIBLE);
                                ((ProgressBar)activity.findViewById(R.id.pb)).setVisibility(View.GONE);
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(context, "Unexpected Error Happened, try again later !!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
