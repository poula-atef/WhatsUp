package com.example.whatsup.UI;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.whatsup.POJO.FriendsMessagesAdapter;
import com.example.whatsup.POJO.SuggestFriendAdapter;
import com.example.whatsup.databinding.FragmentMainBinding;


public class MainFragment extends Fragment {

    private FragmentMainBinding binding;

    public MainFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMainBinding.inflate(inflater);

        SuggestFriendAdapter adapter = new SuggestFriendAdapter();

        binding.recFriends.setAdapter(adapter);
        binding.recFriends.setHasFixedSize(true);
        binding.recFriends.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));



        FriendsMessagesAdapter messagesAdapter = new FriendsMessagesAdapter();

        binding.recMessages.setAdapter(messagesAdapter);
        binding.recMessages.setHasFixedSize(true);
        binding.recMessages.setLayoutManager(new LinearLayoutManager(getContext()));


        return binding.getRoot();
    }
    public interface OnChildChangeListener{
        void onChildChange(Fragment fragment);
    }
}