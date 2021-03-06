package com.example.whatsup.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.whatsup.databinding.FragmentImageBinding;


public class ImageFragment extends Fragment {

    private String imageUrl;
    private FragmentImageBinding binding;

    public ImageFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        imageUrl = getArguments().getString("imgUrl");
        binding = FragmentImageBinding.inflate(inflater);
        Glide.with(getContext()).load(imageUrl).into(binding.img);
        return binding.getRoot();
    }
}