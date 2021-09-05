package com.example.whatsup.UI;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.whatsup.UI.MainFragment.OnChildChangeListener;
import com.example.whatsup.databinding.FragmentConfirmationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;


public class ConfirmationFragment extends Fragment {

    private FragmentConfirmationBinding binding;
    private String verificationId;
    private OnChildChangeListener listener;

    public ConfirmationFragment() {
    }

    public ConfirmationFragment(String verificationId) {
        this.verificationId = verificationId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentConfirmationBinding.inflate(inflater);

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
                            listener.onChildChangeWithoutStack(new UserDetailsFragment());
                        }
                        else{
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
        listener = (OnChildChangeListener) context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}