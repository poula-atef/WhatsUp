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
import com.example.whatsup.databinding.FragmentUserDetailsBinding;
import com.google.firebase.auth.FirebaseAuth;


public class UserDetailsFragment extends Fragment {

    private FragmentUserDetailsBinding binding;

    public UserDetailsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserDetailsBinding.inflate(inflater);
        String phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        String imageUrl = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("profile","?");

        binding.phoneDetails.setText(phone);
        if(!imageUrl.equals("?"))
            Glide.with(this).load(imageUrl).into(binding.imgPicker);

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }


}