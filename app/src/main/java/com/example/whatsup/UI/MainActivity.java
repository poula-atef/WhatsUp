package com.example.whatsup.UI;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import com.example.whatsup.POJO.Classes.User;
import com.example.whatsup.POJO.Constants;
import com.example.whatsup.POJO.WhatsUpUtils;
import com.example.whatsup.R;
import com.example.whatsup.UI.MainFragment.OnChildChangeListener;
import com.example.whatsup.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity implements OnChildChangeListener {

    private ActivityMainBinding binding;
    private User user;
    String TAG = "tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC);

        WhatsUpUtils.determineStartFragment(this,binding.getRoot());

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putString(Constants.MY_TOKEN,task.getResult()).apply();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        WhatsUpUtils.checkNotificationIntent(this,binding.getRoot());
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onComponentClick(View view) {
        int id = view.getId();
        if (id == R.id.send) {
            String countryCode = ((TextView) findViewById(R.id.country_code)).getText().toString();
            String phoneNumber = ((TextView) findViewById(R.id.phone_profile)).getText().toString();

            if (countryCode.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(MainActivity.this, "All Fields Are Required !!", Toast.LENGTH_SHORT).show();
                return;
            }
            ((ProgressBar) findViewById(R.id.pb_log)).setVisibility(View.VISIBLE);
            ((ImageView) findViewById(R.id.message_icon)).setVisibility(View.GONE);
            WhatsUpUtils.signUpWithPhoneNumber(binding.getRoot(), countryCode, phoneNumber, this);

        } else if (id == R.id.login_btn) {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_splashFragment_to_logFragment);
        } else if (id == R.id.exit) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signOut();
            PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply();
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_mainFragment_to_splashFragment);
        } else if (id == R.id.img_picker) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        } else if (id == R.id.birth_date) {
            WhatsUpUtils.showDatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                    ((TextView) findViewById(R.id.birth_date)).setText(mDay + "/" + (mMonth + 1) + "/" + mYear);
                }
            });
        } else if (id == R.id.confirm_details) {
            if (((ImageView) findViewById(R.id.right_arrow)).getVisibility() == View.GONE)
                return;

            String userName = ((EditText) findViewById(R.id.username_details)).getText().toString();
            String birthDate = ((TextView) findViewById(R.id.birth_date)).getText().toString();
            String phoneNumber = ((EditText) findViewById(R.id.phone_details)).getText().toString();
            if (PreferenceManager.getDefaultSharedPreferences(this).getString(Constants.PROFILE_IMAGE_URL, "?").equals("?")
                    || userName.isEmpty() || birthDate.equals(getString(R.string.birth_date))) {
                Toast.makeText(this, "All Fields are Required !!", Toast.LENGTH_SHORT).show();
                return;
            }

            PreferenceManager.getDefaultSharedPreferences(this).edit().putString(Constants.USER_NAME, userName).apply();
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString(Constants.BIRTH_DATE, birthDate).apply();

            WhatsUpUtils.setUserDataToFirebaseDatabase(userName, birthDate, phoneNumber, FirebaseAuth.getInstance().getCurrentUser().getUid(), this);
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_userDetailsFragment_to_mainFragment);
        } else if (id == R.id.profile) {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_mainFragment_to_profileFragment);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 1) {
                ((ImageView) findViewById(R.id.img_picker)).setImageURI(data.getData());
                WhatsUpUtils.saveImageInFirebaseStorage(data.getData(), this, this);
            } else if (requestCode == 2) {
                WhatsUpUtils.sendImageMessage(data.getData(), this, user);
                user = null;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        WhatsUpUtils.makeMeOffline(this);
    }

    @Override
    public void sendImageMessage(User user) {
        this.user = user;
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2);
    }
}

