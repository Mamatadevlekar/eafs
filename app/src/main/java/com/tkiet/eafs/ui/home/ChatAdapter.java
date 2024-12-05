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

    private List<ChatItem> chatItemList;
    private String currentUserId;

    public ChatAdapter(List<ChatItem> chatItemList, String currentUserId) {
        this.chatItemList = chatItemList;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        return chatItemList.get(position).getType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ChatItem.TYPE_MESSAGE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_right_message, parent, false);
            return new MessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_date_header, parent, false);
            return new DateViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatItem chatItem = chatItemList.get(position);

        if (chatItem.getType() == ChatItem.TYPE_MESSAGE) {
            Message message = chatItem.getMessage();
            String formattedTime = DateFormat.format("hh:mm a", message.getTimestamp()).toString();
            MessageViewHolder messageHolder = (MessageViewHolder) holder;

            messageHolder.messageText.setText(message.getMessageText());
            messageHolder.timestamp.setText(formattedTime);
        } else {
            DateViewHolder dateHolder = (DateViewHolder) holder;
            dateHolder.dateHeader.setText(chatItem.getDateHeader());
        }
    }

    @Override
    public int getItemCount() {
        return chatItemList.size();
    }

    // ViewHolder for messages
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timestamp;

        public MessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageTextRight); // or Left based on your design
            timestamp = itemView.findViewById(R.id.messageTimestampRight); // or Left based on your design
        }
    }

    // ViewHolder for date headers
    public static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView dateHeader;

        public DateViewHolder(View itemView) {
            super(itemView);
            dateHeader = itemView.findViewById(R.id.dateHeader);
        }
    }
}
