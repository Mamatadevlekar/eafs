package com.tkiet.eafs.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tkiet.eafs.R;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private Button sendButton;
    private List<Message> messageList;
    private ChatAdapter chatAdapter;
    private String currentUserId, otherUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize views
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        // Initialize message list and adapter
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList, currentUserId);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        // Get the current and other user IDs
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        otherUserId = getIntent().getStringExtra("userId");

        loadMessages();

        // Send button click listener
        sendButton.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessage(messageText);
                messageInput.setText("");
            }
        });
    }

    private void loadMessages() {
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Messages")
                .child(currentUserId)
                .child(otherUserId);

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    messageList.add(message);
                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    private void sendMessage(String messageText) {
        String messageId = FirebaseDatabase.getInstance().getReference().push().getKey();
        long timestamp = System.currentTimeMillis();
        Message message = new Message(currentUserId, otherUserId, messageText, timestamp);

        if (messageId != null) {
            // Save message to the sender's chat
            DatabaseReference senderRef = FirebaseDatabase.getInstance().getReference("Messages")
                    .child(currentUserId)
                    .child(otherUserId)
                    .child(messageId);

            senderRef.setValue(message).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Save message to the receiver's chat
                    DatabaseReference receiverRef = FirebaseDatabase.getInstance().getReference("Messages")
                            .child(otherUserId)
                            .child(currentUserId)
                            .child(messageId);

                    receiverRef.setValue(message).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            // Optionally show a success toast
                            Toast.makeText(ChatActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
                        } else {
                            // Handle failure to save to the receiver
                            Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Handle failure to save to the sender
                    Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // If message ID is null, log an error
            Log.e("ChatActivity", "Failed to generate message ID");
        }
    }

}
