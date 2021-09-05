package com.example.whatsup.POJO.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsup.POJO.Classes.Message;
import com.example.whatsup.POJO.Classes.User;
import com.example.whatsup.R;
import com.example.whatsup.UI.ImageFragment;
import com.example.whatsup.UI.MainFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewholder> {

    private List<Message> messages;
    private Context context;
    private List<Boolean> imgList;
    String TAG = "tag";
    private MainFragment.OnChildChangeListener listener;

    public MessagesAdapter(List<Message> messages) {
        setMessages(messages);
    }

    public MessagesAdapter() {
    }

    @NonNull
    @Override
    public MessageViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == 0)
            return new MessageViewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.green_message, parent, false));
        return new MessageViewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.grey_message, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewholder holder, int position) {
        if (!imgList.get(position)) {
            holder.img.setVisibility(View.GONE);
            holder.message.setVisibility(View.VISIBLE);
            if (!holder.message.getText().equals(messages.get(position).getMessage()))
                holder.message.setText(messages.get(position).getMessage());
        } else {
            holder.img.setVisibility(View.VISIBLE);
            holder.message.setVisibility(View.GONE);
            holder.img.setContentDescription(messages.get(position).getImageUrl());
        }
        if (!holder.messageTime.getText().equals(messages.get(position).getTime()))
            holder.messageTime.setText(messages.get(position).getTime());
        if (messages.get(position).getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            if (messages.get(position).getSeen() == 1)
                holder.messageSeen.setText("Reached");
            else if (messages.get(position).getSeen() == 2)
                holder.messageSeen.setText("Seen");
        }

    }

    @Override
    public int getItemCount() {
        if (messages == null)
            return 0;
        return messages.size();
    }

    public void putOneElement(Message message, int position) {
        messages.add(message);
        if (message.isImg())
            imgList.add(true);
        else
            imgList.add(false);
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
        imgList = new ArrayList<>();
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).isImg()) {
                Log.d(TAG, messages.get(i).getMessage() + " : true");
                imgList.add(true);
            } else {
                Log.d(TAG, messages.get(i).getMessage() + " : false");
                imgList.add(false);
            }
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
            return 0;
        return 1;
    }

    public void setListener(MainFragment.OnChildChangeListener listener) {
        this.listener = listener;
    }

    public class MessageViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView message, messageTime, messageSeen;
        ImageView img;

        public MessageViewholder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_message);
            message = itemView.findViewById(R.id.message);
            messageTime = itemView.findViewById(R.id.message_time);
            messageSeen = itemView.findViewById(R.id.message_seen);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if(imgList.get(getAdapterPosition()))
                listener.onChildChangeWithStack(new ImageFragment(messages.get(getAdapterPosition()).getImageUrl()));
        }
    }
}
