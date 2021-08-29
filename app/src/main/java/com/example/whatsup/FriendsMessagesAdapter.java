package com.example.whatsup;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FriendsMessagesAdapter extends RecyclerView.Adapter<FriendsMessagesAdapter.FriendsMessagesViewHolder> {
    @NonNull
    @Override
    public FriendsMessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FriendsMessagesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsMessagesViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class FriendsMessagesViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView message,time,name;
        View active;
        public FriendsMessagesViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            message = itemView.findViewById(R.id.message);
            time =  itemView.findViewById(R.id.time);
            name =  itemView.findViewById(R.id.name);
            active =  itemView.findViewById(R.id.active);
        }
    }
}
