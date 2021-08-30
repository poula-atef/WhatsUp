package com.example.whatsup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsup.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.example.whatsup.MainFragment.OnChildChangeListener;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements OnChildChangeListener {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null)
            onChildChange(new SplashFragment());
        else
            onChildChange(new MainFragment());
    }

    public void onComponentClick(View view) {
        int id = view.getId();
        if (id == R.id.send) {
            String countryCode = ((TextView) findViewById(R.id.country_code)).getText().toString();
            String phoneNumber = ((TextView) findViewById(R.id.phone)).getText().toString();
            if (countryCode.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(MainActivity.this, "All Fields Are Required !!", Toast.LENGTH_SHORT).show();
                return;
            }

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

                    onChildChange(new ConfirmationFragment(verificationId));
                }
            };

            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(mAuth)
                            .setPhoneNumber("+" + countryCode + phoneNumber)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(this)
                            .setCallbacks(mCallbacks)
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);


        }
        else if (id == R.id.login_btn) {
            onChildChange(new LogFragment());
        }
        else if (id == R.id.exit) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signOut();
            onChildChange(new SplashFragment());
        }
        else if (id == R.id.back_to_splash) {
            getSupportFragmentManager().popBackStack();
        }
        else if (id == R.id.back_to_log) {
            getSupportFragmentManager().popBackStack();
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
                .addToBackStack(null)
                .commit();
    }


}

