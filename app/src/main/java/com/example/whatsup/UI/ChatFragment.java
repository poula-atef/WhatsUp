package com.example.whatsup.UI;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.whatsup.POJO.Adapters.MessagesAdapter;
import com.example.whatsup.POJO.Classes.Message;
import com.example.whatsup.POJO.Classes.User;
import com.example.whatsup.POJO.WhatsUpUtils;
import com.example.whatsup.databinding.FragmentChatBinding;
import com.example.whatsup.UI.MainFragment.OnChildChangeListener;
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
    private OnChildChangeListener listener;
    String TAG = "tag";

    public ChatFragment() {

    }

    public ChatFragment(User user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater);
        MessagesAdapter adapter = new MessagesAdapter(new ArrayList<>());
        adapter.setListener(listener);
        binding.recChat.setAdapter(adapter);
        binding.recChat.setLayoutManager(new LinearLayoutManager(getContext()));
        prepareUserData();

        updateUserData();

        markAllMessagesAsSeen();

        getAllChatMessages(user);

        binding.sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.etMessage.getText().toString().isEmpty())
                    return;
                Message message = new Message(FirebaseAuth.getInstance().getCurrentUser().getUid(), user.getUserId()
                        , binding.etMessage.getText().toString(), false, WhatsUpUtils.getCurrentTimeFormat(), "", 1);
                WhatsUpUtils.sendMessage(message, getContext(), user);
                binding.etMessage.getText().clear();
            }
        });

        binding.addImgChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.sendImageMessage(user);
            }
        });

        return binding.getRoot();
    }

    private void markAllMessagesAsSeen() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("messages")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(user.getUserId());
        final DatabaseReference ref1 = reference;
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (binding == null || binding.recChat.getAdapter() == null || user == null || !user.isActive())
                    return;
                List<Message> messages = new ArrayList<>();
                for (DataSnapshot shot : snapshot.getChildren()) {
                    Message message = shot.getValue(Message.class);
                    if (message.getReceiverId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        message.setSeen(2);
                        ref1.child(shot.getKey()).setValue(message);
                    }
                    messages.add(message);
                }

                ((MessagesAdapter) binding.recChat.getAdapter()).setMessages(messages);
                ((LinearLayoutManager) binding.recChat.getLayoutManager()).scrollToPosition(messages.size() - 1);
                ((MessagesAdapter) binding.recChat.getAdapter()).notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference = database.getReference().child("messages")
                .child(user.getUserId())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        final DatabaseReference ref2 = reference;
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (binding == null || binding.recChat.getAdapter() == null || user == null || !user.isActive())
                    return;
                List<Message> messages = new ArrayList<>();
                for (DataSnapshot shot : snapshot.getChildren()) {
                    Message message = shot.getValue(Message.class);
                    if (message.getReceiverId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        message.setSeen(2);
                        messages.add(message);
                        ref2.child(shot.getKey()).setValue(message);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void prepareUserData() {
        binding.username.setText(user.getUserName());
        Glide.with(this).load(user.getImageUrl()).into(binding.img);
        binding.lastSeen.setText(user.getLastSeen());
        if (user.isActive()) {
            binding.active.setVisibility(View.VISIBLE);
        } else {
            binding.active.setVisibility(View.INVISIBLE);
        }
    }

    private void updateUserData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("users").child(user.getUserId());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (binding == null || binding.recChat.getAdapter() == null)
                    return;
                User user1 = snapshot.getValue(User.class);
                if (user == null || user1 == null)
                    return;
                if (user.isActive() != user1.isActive()) {
                    if (user1.isActive() && user1.getLastSeen().equals("online")) {
                        binding.active.setVisibility(View.VISIBLE);
                        binding.lastSeen.setText(user1.getLastSeen());

                        user = user1;
                    } else if (!user1.isActive() && !user1.getLastSeen().equals("online")) {
                        binding.active.setVisibility(View.INVISIBLE);
                        binding.lastSeen.setText("Last seen at " + user1.getLastSeen());
                        user = user1;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getAllChatMessages(User user) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("messages")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(user.getUserId());
        first = true;
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (binding == null || binding.recChat.getAdapter() == null)
                    return;
                List<Message> messages = new ArrayList<>();
                for (DataSnapshot shot : snapshot.getChildren()) {
                    Message message = shot.getValue(Message.class);
                    messages.add(message);
                }
                if (first) {
                    ((MessagesAdapter) binding.recChat.getAdapter()).setMessages(messages);
                    ((MessagesAdapter) binding.recChat.getAdapter()).notifyDataSetChanged();
                    LinearLayoutManager llm = new LinearLayoutManager(getContext());
                    llm.scrollToPosition(messages.size() - 1);
                    binding.recChat.setLayoutManager(llm);
                    first = false;
                } else {
                    ((MessagesAdapter) binding.recChat.getAdapter()).putOneElement(messages.get(messages.size() - 1), messages.size() - 1);
                    ((MessagesAdapter) binding.recChat.getAdapter()).notifyItemInserted(messages.size() - 1);
                    ((LinearLayoutManager) binding.recChat.getLayoutManager()).scrollToPosition(messages.size() - 1);
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