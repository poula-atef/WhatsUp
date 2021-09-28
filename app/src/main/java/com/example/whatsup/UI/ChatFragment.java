package com.example.whatsup.UI;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.whatsup.POJO.Adapters.MessagesAdapter;
import com.example.whatsup.POJO.Classes.Message;
import com.example.whatsup.POJO.Classes.User;
import com.example.whatsup.POJO.Constants;
import com.example.whatsup.POJO.WhatsUpUtils;
import com.example.whatsup.R;
import com.example.whatsup.UI.MainFragment.OnChildChangeListener;
import com.example.whatsup.databinding.FragmentChatBinding;
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
    private User user, currentUser;
    private boolean first, updated;
    private OnChildChangeListener listener;
    String TAG = "tag";

    public ChatFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater);


        user = getArguments().getParcelable("user");

        WhatsUpUtils.closeAnyNotification(getContext(), (int) (Long.parseLong(user.getPhoneNumber().substring(1)) / 100000));

        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString(Constants.CURRENT_FRIEND_CHAT, user.getImageUrl()).apply();
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString(Constants.CURRENT_FRIEND_TOKEN, user.getToken()).apply();

        MessagesAdapter adapter = new MessagesAdapter(new ArrayList<>());
        binding.recChat.setAdapter(adapter);
        binding.recChat.setLayoutManager(new LinearLayoutManager(getContext()));

        prepareUserData();

        updateUserData();

        getCurrentUserData();

        markAllMessagesAsSeen();

        getAllChatMessages(user);

        binding.sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.etMessage.getText().toString().isEmpty())
                    return;
                updated = false;
                Message message = new Message(FirebaseAuth.getInstance().getCurrentUser().getUid(), user.getUserId()
                        , binding.etMessage.getText().toString(), false, WhatsUpUtils.getCurrentTimeFormat(), "", 1
                        , currentUser.getPhoneNumber(), currentUser.getImageUrl(), currentUser.getUserName());

                WhatsUpUtils.sendMessage(message, getContext(), user,currentUser);
                binding.etMessage.getText().clear();
            }
        });

        binding.addImgChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.sendImageMessage(user);
            }
        });

        binding.videoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("call_type","Video Call");
                bundle.putString("receiver_token",user.getToken());
                bundle.putString("receiver_name",user.getUserName());
                bundle.putString("receiver_number",user.getPhoneNumber());
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_chatFragment_to_outGoingCallFragment,bundle);
            }
        });

        binding.voiceCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("call_type","Voice Call");
                bundle.putString("receiver_token",user.getToken());
                bundle.putString("receiver_name",user.getUserName());
                bundle.putString("receiver_number",user.getPhoneNumber());
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_chatFragment_to_outGoingCallFragment,bundle);
            }
        });

        return binding.getRoot();
    }

    private void getCurrentUserData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot shot : snapshot.getChildren()) {
                    User user = shot.getValue(User.class);
                    if (user.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        currentUser = user;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
//                if (!updated) {
//                    updated = true;
//                    ((MessagesAdapter) binding.recChat.getAdapter()).setMessages(messages);
//                    ((LinearLayoutManager) binding.recChat.getLayoutManager()).scrollToPosition(messages.size() - 1);
//                    ((MessagesAdapter) binding.recChat.getAdapter()).notifyDataSetChanged();
//
                if (!messages.isEmpty()) {
                    FirebaseDatabase.getInstance()
                            .getReference("users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("friends").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (user == null)
                                return;
                            for (DataSnapshot shot : snapshot.getChildren()) {
                                if (shot.getKey().equals(user.getUserId())) {
                                    FirebaseDatabase.getInstance()
                                            .getReference("users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("friends")
                                            .child(user.getUserId())
                                            .child("seen")
                                            .setValue(2);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
//                }
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

                    ((MessagesAdapter) binding.recChat.getAdapter()).setMessages(messages);
                    ((LinearLayoutManager) binding.recChat.getLayoutManager()).scrollToPosition(messages.size() - 1);
                    ((MessagesAdapter) binding.recChat.getAdapter()).notifyDataSetChanged();


                    WhatsUpUtils.setUserFriendLastMessage(messages.get(messages.size() - 1)
                            , FirebaseAuth.getInstance().getCurrentUser().getUid(), user, getContext(), messages.get(messages.size() - 1).getSenderId()
                            , user.getToken());

                    WhatsUpUtils.setUserFriendLastMessage(messages.get(messages.size() - 1)
                            , user.getUserId(), currentUser, getContext(), messages.get(messages.size() - 1).getSenderId(), user.getToken());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        user = null;
        binding = null;
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().remove(Constants.CURRENT_FRIEND_CHAT).apply();
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().remove(Constants.CURRENT_FRIEND_TOKEN).apply();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (OnChildChangeListener) context;
    }


}