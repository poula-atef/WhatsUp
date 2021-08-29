package com.example.whatsup;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SuggestFriendAdapter extends RecyclerView.Adapter<SuggestFriendAdapter.SuggestFriendViewHolder> {


    @NonNull
    @Override
    public SuggestFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SuggestFriendViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_small_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestFriendViewHolder holder, int position) {

        if(position % 2 == 0)
            holder.active.setVisibility(View.GONE);
        else
            holder.active.setVisibility(View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class SuggestFriendViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        View active;
        public SuggestFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.img);
            active = itemView.findViewById(R.id.active);
        }
    }
}
