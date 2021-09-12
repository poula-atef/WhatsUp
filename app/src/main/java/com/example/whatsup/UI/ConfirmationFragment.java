package com.example.whatsup.UI;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.whatsup.POJO.Classes.User;
import com.example.whatsup.R;
import com.example.whatsup.databinding.FragmentConfirmationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ConfirmationFragment extends Fragment {

    private FragmentConfirmationBinding binding;
    private String verificationId;

    public ConfirmationFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentConfirmationBinding.inflate(inflater);

        verificationId = getArguments().getString("verificationId");

        binding.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.verificationCode.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Verification Code is Required !!", Toast.LENGTH_SHORT).show();
                    return;
                }

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, binding.verificationCode.getText().toString());
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_confirmationFragment_to_userDetailsFragment);

                        } else {
                            Toast.makeText(getContext(), "Unexpected Error happened, try again later !!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return binding.getRoot();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}