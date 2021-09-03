package com.example.whatsup.UI;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsup.POJO.Adapters.MessagesAdapter;
import com.example.whatsup.POJO.Classes.Message;
import com.example.whatsup.POJO.Classes.User;
import com.example.whatsup.POJO.WhatsUpUtils;
import com.example.whatsup.R;
import com.example.whatsup.databinding.FragmentChatBinding;
import com.example.whatsup.UI.MainFragment.OnChildChangeListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private User user;
    private boolean first;
    private MessagesAdapter messagesAdapter;
    private OnChildChangeListener listener;

    public ChatFragment() {

    }

    public ChatFragment(User user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater);

        prepareUserData();

        updateUserData();

        getAllChatMessages();

        binding.sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
                binding.etMessage.getText().clear();
            }
        });

        return binding.getRoot();
    }

    private void prepareUserData() {
        binding.username.setText(user.getUserName());
        Glide.with(this).load(user.getImageUrl()).into(binding.img);
        binding.lastSeen.setText(user.getLastSeen());
        if(user.isActive()){
            binding.active.setVisibility(View.VISIBLE);
        }
        else{
            binding.active.setVisibility(View.INVISIBLE);
        }
    }

    private void updateUserData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("users").child(user.getUserId());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user1 = snapshot.getValue(User.class);
                if(user == null || user1 == null)
                    return;
                if(user.isActive() != user1.isActive()){
                    if(user1.isActive() && user1.getLastSeen().equals("online")){
                        binding.active.setVisibility(View.VISIBLE);
                        binding.lastSeen.setText(user1.getLastSeen());
                        user = user1;
                    }
                    else if(!user1.isActive() && !user1.getLastSeen().equals("online")){
                        binding.active.setVisibility(View.INVISIBLE);
                        binding.lastSeen.setText(user1.getLastSeen());
                        user = user1;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage() {
        if(binding.etMessage.getText().toString().isEmpty())
            return;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("messages");

        Message message = new Message(FirebaseAuth.getInstance().getCurrentUser().getUid(), user.getUserId()
                , binding.etMessage.getText().toString(), false, WhatsUpUtils.getCurrentTimeFormat(), "", 1);

        reference
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(user.getUserId())
                .push()
                .setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getContext(), "Unexpected Error Happened, try again later !!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        reference
                .child(user.getUserId())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .push()
                .setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getContext(), "Unexpected Error Happened, try again later !!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void getAllChatMessages() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("messages")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(user.getUserId());
        first = true;
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Message> messages = new ArrayList<>();
                for (DataSnapshot shot : snapshot.getChildren()) {
                    Message message = shot.getValue(Message.class);
                    messages.add(message);
                }
                if (first) {
                    messagesAdapter = new MessagesAdapter();
                    messagesAdapter.setMessages(messages);
                    binding.recChat.setAdapter(messagesAdapter);
                    LinearLayoutManager llm = new LinearLayoutManager(getContext());
                    llm.scrollToPosition(messages.size() - 1);
                    binding.recChat.setLayoutManager(llm);
                    first = false;
                } else {
                    ((MessagesAdapter) binding.recChat.getAdapter()).getMessages().add(messages.get(messages.size() - 1));
                    ((LinearLayoutManager) binding.recChat.getLayoutManager()).scrollToPosition(messages.size() - 1);
                    ((MessagesAdapter) binding.recChat.getAdapter()).notifyItemInserted(messages.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        user = null;
        binding = null;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (OnChildChangeListener) context;
    }

}