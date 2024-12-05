package com.tkiet.eafs.ui.home;

import android.text.format.DateFormat;
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
        // Determine if the message is from the current user
        return messageList.get(position).getSenderId().equals(currentUserId) ? 1 : 2;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_right_message, parent, false);
            return new RightMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_left_message, parent, false);
            return new LeftMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        String formattedTime = DateFormat.format("hh:mm a", message.getTimestamp()).toString(); // Format timestamp

        if (holder instanceof RightMessageViewHolder) {
            ((RightMessageViewHolder) holder).messageTextRight.setText(message.getMessageText());
            ((RightMessageViewHolder) holder).messageTimestampRight.setText(formattedTime);
        } else if (holder instanceof LeftMessageViewHolder) {
            ((LeftMessageViewHolder) holder).messageTextLeft.setText(message.getMessageText());
            ((LeftMessageViewHolder) holder).messageTimestampLeft.setText(formattedTime);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // ViewHolder for the current user's message (right side)
    public static class RightMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextRight, messageTimestampRight;

        public RightMessageViewHolder(View itemView) {
            super(itemView);
            messageTextRight = itemView.findViewById(R.id.messageTextRight);
            messageTimestampRight = itemView.findViewById(R.id.messageTimestampRight);
        }
    }

    // ViewHolder for the other user's message (left side)
    public static class LeftMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextLeft, messageTimestampLeft;

        public LeftMessageViewHolder(View itemView) {
            super(itemView);
            messageTextLeft = itemView.findViewById(R.id.messageTextLeft);
            messageTimestampLeft = itemView.findViewById(R.id.messageTimestampLeft);
        }
    }
}
