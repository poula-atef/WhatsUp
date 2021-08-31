package com.example.whatsup.POJO.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsup.POJO.Classes.Friend;
import com.example.whatsup.R;

import java.util.ArrayList;
import java.util.List;

public class FriendsMessagesAdapter extends RecyclerView.Adapter<FriendsMessagesAdapter.FriendsMessagesViewHolder> {

    private List<Friend> friends;

    public FriendsMessagesAdapter() {
        friends = new ArrayList<>();
    }

    @NonNull
    @Override
    public FriendsMessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FriendsMessagesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsMessagesViewHolder holder, int position) {

    }

    public List<Friend> getFriends() {
        return friends;
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class FriendsMessagesViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView message,time,name;
        public FriendsMessagesViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            message = itemView.findViewById(R.id.message);
            time =  itemView.findViewById(R.id.time);
            name =  itemView.findViewById(R.id.name);
        }
    }
}
