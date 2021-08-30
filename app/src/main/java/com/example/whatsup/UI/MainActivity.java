package com.example.whatsup.UI;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsup.POJO.WhatsUpUtils;
import com.example.whatsup.R;
import com.example.whatsup.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.example.whatsup.UI.MainFragment.OnChildChangeListener;

public class MainActivity extends AppCompatActivity implements OnChildChangeListener {

    private ActivityMainBinding binding;
    String TAG = "tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null)
            onChildChange(new SplashFragment());
        else {
            if(checkDataComplete()){
                Log.d(TAG, "onCreate: userDetails!!");
                onChildChange(new UserDetailsFragment());
            }
            else{
                Log.d(TAG, "onCreate: Main!!");
                onChildChange(new MainFragment());
            }
        }
    }

    private boolean checkDataComplete() {
        return !PreferenceManager.getDefaultSharedPreferences(this).getBoolean("profile_image",false)
                || PreferenceManager.getDefaultSharedPreferences(this).getString("user_name","?").equals("?")
                || PreferenceManager.getDefaultSharedPreferences(this).getString("birth_date","?").equals("?");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onComponentClick(View view) {
        int id = view.getId();
        if (id == R.id.send) {
            String countryCode = ((TextView) findViewById(R.id.country_code)).getText().toString();
            String phoneNumber = ((TextView) findViewById(R.id.phone_details)).getText().toString();

            if (countryCode.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(MainActivity.this, "All Fields Are Required !!", Toast.LENGTH_SHORT).show();
                return;
            }

            WhatsUpUtils.signUpWithPhoneNumber(this,countryCode,phoneNumber,this);

        }
        else if (id == R.id.login_btn) {
            onChildChange(new LogFragment());
        }
        else if (id == R.id.exit) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signOut();
            PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply();
            onChildChange(new SplashFragment());
        }
        else if (id == R.id.back_to_splash) {
            onChildChange(new SplashFragment());
        }
        else if(id == R.id.back_to_log){
            onChildChange(new LogFragment());
        }
        else if (id == R.id.img_picker) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(intent,1);
        }
        else if(id == R.id.birth_date){
            WhatsUpUtils.showDatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                    ((TextView)findViewById(R.id.birth_date)).setText(mDay + "/" + (mMonth + 1) + "/" + mYear);
                }
            });
        }
        else if(id == R.id.confirm_details){
            if(((ImageView)findViewById(R.id.right_arrow)).getVisibility() == View.GONE)
                return;

            String userName = ((EditText)findViewById(R.id.username_details)).getText().toString();
            String birthDate = ((TextView)findViewById(R.id.birth_date)).getText().toString();
            String phoneNumber = ((EditText)findViewById(R.id.phone_details)).getText().toString();
            if(!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("profile_image",false)
                || userName.isEmpty() || birthDate.equals(getString(R.string.birth_date))){
                Toast.makeText(this, "All Fields are Required !!", Toast.LENGTH_SHORT).show();
                return;
            }

            PreferenceManager.getDefaultSharedPreferences(this).edit().putString("user_name",userName).apply();
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString("birth_date",birthDate).apply();

            WhatsUpUtils.setUserDataToFirebaseDatabase(userName,birthDate,phoneNumber,FirebaseAuth.getInstance().getCurrentUser().getUid(),this);
            Log.d(TAG, "onComponentClick: from onComponentClick !!!");
            onChildChange(new MainFragment());
        }
    }

    @Override
    public void onChildChange(Fragment fragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                    R.anim.enter_right_to_left
                    , R.anim.exit_right_to_left
                    , R.anim.enter_left_to_right
                    , R.anim.exit_left_to_right)
                .replace(R.id.container, fragment)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            ((ImageView)findViewById(R.id.img_picker)).setImageURI(data.getData());
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("profile_image",true).apply();
            WhatsUpUtils.saveImageInFirebaseStorage(data.getData(),this,this);
        }
    }
}

