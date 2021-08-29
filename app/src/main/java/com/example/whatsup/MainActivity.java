package com.example.whatsup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.example.whatsup.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SuggestFriendAdapter adapter = new SuggestFriendAdapter();

        binding.recFriends.setAdapter(adapter);
        binding.recFriends.setHasFixedSize(true);
        binding.recFriends.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));



        FriendsMessagesAdapter messagesAdapter = new FriendsMessagesAdapter();

        binding.recMessages.setAdapter(messagesAdapter);
        binding.recMessages.setHasFixedSize(true);
        binding.recMessages.setLayoutManager(new LinearLayoutManager(this));

    }
}