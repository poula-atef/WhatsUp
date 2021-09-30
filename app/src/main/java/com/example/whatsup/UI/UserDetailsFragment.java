package com.example.whatsup.UI;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.whatsup.POJO.Classes.User;
import com.example.whatsup.POJO.Constants;
import com.example.whatsup.databinding.FragmentUserDetailsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class UserDetailsFragment extends Fragment {

    private FragmentUserDetailsBinding binding;

    public UserDetailsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserDetailsBinding.inflate(inflater);

        FirebaseDatabase
                .getInstance()
                .getReference("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean found = false;
                        for (DataSnapshot shot : snapshot.getChildren()) {
                            User user = shot.getValue(User.class);
                            if (user.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                binding.phoneDetails.setText(user.getPhoneNumber());
                                binding.birthDate.setText(user.getBirthDate());
                                binding.usernameDetails.setText(user.getUserName());
                                Glide.with(UserDetailsFragment.this).load(user.getImageUrl()).into(binding.imgPicker);
                                PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString(Constants.PROFILE_IMAGE_URL, user.getImageUrl()).apply();
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            String phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
                            String imageUrl = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(Constants.PROFILE_IMAGE_URL, "?");

                            binding.phoneDetails.setText(phone);
                            if (!imageUrl.equals("?"))
                                Glide.with(UserDetailsFragment.this).load(imageUrl).into(binding.imgPicker);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }


}