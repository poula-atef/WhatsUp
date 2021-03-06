package com.example.whatsup.POJO.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsup.POJO.Classes.Friend;
import com.example.whatsup.POJO.Classes.User;
import com.example.whatsup.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendsMessagesAdapter extends RecyclerView.Adapter<FriendsMessagesAdapter.FriendsMessagesViewHolder> {

    private List<Friend> friends;
    private List<String> friendsIds;
    private Context context;

    public FriendsMessagesAdapter() {
        friends = new ArrayList<>();
        friendsIds = new ArrayList<>();
    }

    @NonNull
    @Override
    public FriendsMessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new FriendsMessagesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsMessagesViewHolder holder, int position) {
        Glide.with(context).load(friends.get(position).getProfileImageUrl()).into(holder.img);
        holder.name.setText(friends.get(position).getUserName());
        holder.time.setText(friends.get(position).getLastDate());
        if (!friends.get(position).getLastMessage().equals(context.getString(R.string.WHATSUP_IMG_CONSTANT))) {
            holder.message.setText(friends.get(position).getLastMessage());
            holder.message.setVisibility(View.VISIBLE);
            holder.imgMessage.setVisibility(View.GONE);
            if (friends.get(position).getSeen() == 1) {
                holder.message.setTypeface(null, Typeface.BOLD);
            } else {
                holder.message.setTypeface(null, Typeface.NORMAL);
            }
        } else {
            holder.message.setVisibility(View.GONE);
            holder.imgMessage.setVisibility(View.VISIBLE);
        }
    }

    public void setFriendsIds(List<String> friendsIds) {
        this.friendsIds = friendsIds;
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
    }

    @Override
    public int getItemCount() {
        if (friends != null)
            return friends.size();
        return 0;
    }

    public class FriendsMessagesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img;
        TextView message, time, name, imgMessage;

        public FriendsMessagesViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            message = itemView.findViewById(R.id.message);
            imgMessage = itemView.findViewById(R.id.img_message);
            time = itemView.findViewById(R.id.time);
            name = itemView.findViewById(R.id.name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("users");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot shot : snapshot.getChildren()) {
                        if (shot.getKey().equals(friendsIds.get(getAdapterPosition()))) {
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("user", shot.getValue(User.class));
                            Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_chatFragment, bundle);
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }
}
