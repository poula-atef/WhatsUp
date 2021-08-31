package com.example.whatsup.UI;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.whatsup.R;
import com.example.whatsup.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    String TAG = "tag";

    public ProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater);

        binding.phoneProfile.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        binding.usernameProfile.setText(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("user_name","?"));
        binding.birthDateProfile.setText(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("birth_date","?"));
        Glide.with(this).load(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("profile","?")).into(binding.profileImg);
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

}