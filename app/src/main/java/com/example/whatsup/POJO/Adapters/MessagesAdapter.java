package com.example.whatsup.POJO.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsup.POJO.Classes.Message;
import com.example.whatsup.POJO.Classes.User;
import com.example.whatsup.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewholder> {

    private List<Message>messages;

    @NonNull
    @Override
    public MessageViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 0)
            return new MessageViewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.green_message,parent,false));
        return new MessageViewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.grey_message,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewholder holder, int position) {
        holder.message.setText(messages.get(position).getMessage());
        holder.messageTime.setText(messages.get(position).getTime());
        holder.messageTime.setText(messages.get(position).getTime());
        if(messages.get(position).getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            if (messages.get(position).getSeen() == 1)
                holder.messageSeen.setText("Reached");
            else if (messages.get(position).getSeen() == 2)
                holder.messageSeen.setText("Seen");
        }

    }

    @Override
    public int getItemCount() {
        if(messages == null)
            return 0;
        return messages.size();
    }


    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        if(messages.get(position).getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
            return 0;
        return 1;
    }



    public class MessageViewholder extends RecyclerView.ViewHolder {
        TextView message,messageTime,messageSeen;
        public MessageViewholder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            messageTime = itemView.findViewById(R.id.message_time);
            messageSeen = itemView.findViewById(R.id.message_seen);
        }


    }
}
