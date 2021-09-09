package com.example.whatsup.UI;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.whatsup.POJO.Adapters.FriendsMessagesAdapter;
import com.example.whatsup.POJO.Adapters.MessagesAdapter;
import com.example.whatsup.POJO.Adapters.SuggestFriendAdapter.onItemClickListener;
import com.example.whatsup.POJO.Adapters.SuggestFriendAdapter;
import com.example.whatsup.POJO.Classes.Friend;
import com.example.whatsup.POJO.Classes.Message;
import com.example.whatsup.POJO.Classes.User;
import com.example.whatsup.databinding.FragmentMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainFragment extends Fragment implements onItemClickListener {

    private FragmentMainBinding binding;
    private FriendsMessagesAdapter messageAdapter;
    private SuggestFriendAdapter friendAdapter;
    private List<User> users;
    private OnChildChangeListener listener;
    String TAG = "tag";

    public MainFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMainBinding.inflate(inflater);

        friendAdapter = new SuggestFriendAdapter();
        binding.recFriends.setHasFixedSize(true);
        binding.recFriends.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));


        messageAdapter = new FriendsMessagesAdapter();
        binding.recMessages.setAdapter(messageAdapter);
        binding.recMessages.setHasFixedSize(true);
        binding.recMessages.setLayoutManager(new LinearLayoutManager(getContext()));


        getSearchResults();

        getAllMessages();

        return binding.getRoot();
    }

    private void getAllMessages() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("friends");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (binding == null || binding.recMessages.getAdapter() == null)
                    return;
                List<Friend> friends = new ArrayList<>();
                List<String> ids = new ArrayList<>();
                for (DataSnapshot shot : snapshot.getChildren()) {
                    Friend friend = shot.getValue(Friend.class);
                    friends.add(friend);
                    ids.add(shot.getKey());
                }

                ((FriendsMessagesAdapter) binding.recMessages.getAdapter()).setListener(listener);
                ((FriendsMessagesAdapter) binding.recMessages.getAdapter()).setFriendsIds(ids);
                ((FriendsMessagesAdapter) binding.recMessages.getAdapter()).setFriends(friends);
                ((FriendsMessagesAdapter) binding.recMessages.getAdapter()).notifyDataSetChanged();
                binding.recMessages.setLayoutManager(new LinearLayoutManager(getContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getSearchResults() {
        binding.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (users == null) {
                    users = new ArrayList<>();
                    getAllUsers(charSequence);
                } else {
                    fillFiendsAdapter(charSequence);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void fillFiendsAdapter(CharSequence charSequence) {
        List<User> res = new ArrayList<>();
        if (users == null)
            return;
        if (!charSequence.toString().isEmpty()) {
            for (User user : users) {
                if (user.getUserName().contains(charSequence.toString())) {
                    res.add(user);
                }
            }
        }

        friendAdapter.setUsers(res);
        friendAdapter.setListener(this);
        binding.recFriends.setAdapter(friendAdapter);
    }

    private void getAllUsers(CharSequence charSequence) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (users != null)
                    users.clear();
                for (DataSnapshot shot : snapshot.getChildren()) {
                    User user = shot.getValue(User.class);
                    if (users != null && !user.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                        users.add(user);
                }
                fillFiendsAdapter(charSequence);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (OnChildChangeListener) context;
    }

    @Override
    public void onitemClick(User user) {
        listener.onChildChangeWithStack(new ChatFragment(user));
    }

    public interface OnChildChangeListener {
        void onChildChangeWithoutStack(Fragment fragment);

        void onChildChangeWithStack(Fragment fragment);

        void sendImageMessage(User user);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
        messageAdapter = null;
        friendAdapter = null;
        users = null;
    }


}