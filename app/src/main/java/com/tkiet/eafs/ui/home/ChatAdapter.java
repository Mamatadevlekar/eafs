package com.tkiet.eafs.ui.home;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.tkiet.eafs.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> messageList;
    private String currentUserId;

    public ChatAdapter(List<Message> messageList, String currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        // Check if the message is from the current user
        if (messageList.get(position).getSenderId().equals(currentUserId)) {
            return 1;  // Right side
        } else {
            return 2;  // Left side
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            // Inflate the layout for the current user's message (right side)
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_right_message, parent, false);
            return new RightMessageViewHolder(view);
        } else {
            // Inflate the layout for the other user's message (left side)
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_left_message, parent, false);
            return new LeftMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);

        if (holder instanceof RightMessageViewHolder) {
            // Set the message text for the current user's message (right side)
            ((RightMessageViewHolder) holder).messageTextRight.setText(message.getMessageText());
        } else if (holder instanceof LeftMessageViewHolder) {
            // Set the message text for the other user's message (left side)
            ((LeftMessageViewHolder) holder).messageTextLeft.setText(message.getMessageText());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // ViewHolder for the current user's message (right side)
    public static class RightMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextRight;

        public RightMessageViewHolder(View itemView) {
            super(itemView);
            messageTextRight = itemView.findViewById(R.id.messageTextRight);
        }
    }

    // ViewHolder for the other user's message (left side)
    public static class LeftMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextLeft;

        public LeftMessageViewHolder(View itemView) {
            super(itemView);
            messageTextLeft = itemView.findViewById(R.id.messageTextLeft);
        }
    }
}
