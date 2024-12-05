package com.tkiet.eafs.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tkiet.eafs.R;
import com.tkiet.eafs.databinding.FragmentDashboardBinding;
import com.tkiet.eafs.ui.home.ChatActivity;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private RecyclerView chatUsersRecyclerView;
    private List<ChatUser> chatUserList;
    private ChatUserAdapter chatUserAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initialize RecyclerView
        chatUsersRecyclerView = view.findViewById(R.id.chatUsersRecyclerView);
        chatUsersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Log.d("AuthCheck", "User is authenticated with ID: " + currentUserId);
        } else {
            Log.d("AuthCheck", "User is not authenticated");
        }

        chatUserList = new ArrayList<>();
        chatUserAdapter = new ChatUserAdapter(chatUserList, userId -> {
            // Handle click: Open ChatActivity
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
        chatUsersRecyclerView.setAdapter(chatUserAdapter);

        loadChatUsers();
        return view;
    }

    private void loadChatUsers() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("Messages").child(currentUserId);

        messagesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatUserList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    // Fetch additional user data if needed (e.g., name, photo)
                    // For simplicity, assume we have a method getUserInfo(userId)
                    getUserInfo(userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserInfo(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue(String.class);
                String photoUrl = snapshot.child("photoUrl").getValue(String.class);
                chatUserList.add(new ChatUser(userId, name, photoUrl));
                chatUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
