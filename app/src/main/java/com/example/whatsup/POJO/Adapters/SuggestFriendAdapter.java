package com.example.whatsup.POJO.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsup.POJO.Classes.User;
import com.example.whatsup.R;

import java.util.List;

public class SuggestFriendAdapter extends RecyclerView.Adapter<SuggestFriendAdapter.SuggestFriendViewHolder> {

    private List<User> users;
    private Context context;
    String TAG = "tag";

    @NonNull
    @Override
    public SuggestFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new SuggestFriendViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_small_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestFriendViewHolder holder, int position) {
        Glide.with(context).load(users.get(position).getImageUrl()).into(holder.icon);
        holder.name.setText(users.get(position).getUserName());


    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public int getItemCount() {
        if(users == null)
            return 0;
        return users.size();

    }

    public class SuggestFriendViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;
        public SuggestFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.img);
            name = itemView.findViewById(R.id.name);
        }
    }
}
