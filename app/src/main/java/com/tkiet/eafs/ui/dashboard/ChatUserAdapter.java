package com.tkiet.eafs.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tkiet.eafs.R;

import java.util.List;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.ViewHolder> {

    private List<ChatUser> chatUserList;
    private OnUserClickListener onUserClickListener;

    public interface OnUserClickListener {
        void onUserClick(String userId);
    }

    public ChatUserAdapter(List<ChatUser> chatUserList, OnUserClickListener listener) {
        this.chatUserList = chatUserList;
        this.onUserClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatUser user = chatUserList.get(position);
        holder.userNameTextView.setText(user.getUserName());
        holder.itemView.setOnClickListener(v -> onUserClickListener.onUserClick(user.getUserId()));
    }

    @Override
    public int getItemCount() {
        return chatUserList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
        }
    }
}
