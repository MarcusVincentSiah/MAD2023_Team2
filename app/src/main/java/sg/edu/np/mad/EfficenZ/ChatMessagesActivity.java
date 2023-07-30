package sg.edu.np.mad.EfficenZ;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import sg.edu.np.mad.EfficenZ.model.ChatMessage;

public class ChatMessagesActivity extends AppCompatActivity {

    private User receivedUser;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private FirebaseFirestore database;

    private TextView name;

    private SharedPreferences prefs;
    private FirebaseAuth mAuth;

    private EditText inputMessage;
    private RecyclerView recyclerView;

    private FrameLayout send;

    private ProgressBar progressBar;

    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_messages);

        mAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.chatRecyclerView);
        name = findViewById(R.id.textName);
        inputMessage = findViewById(R.id.inputMessage);
        send = findViewById(R.id.layoutSend);
        progressBar = findViewById(R.id.progressBar);
        backBtn = findViewById(R.id.imageBack);

        loadReceiverDetails();
        init();
        listenMessages();

        send.setOnClickListener(v -> {
            sendMessage();
        });

        backBtn.setOnClickListener(v -> {
            onBackPressed();
        });

    }

    private void init() {
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                chatMessages,
                mAuth.getCurrentUser().getUid());


        recyclerView.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();
    }

    private void sendMessage() {

        if (inputMessage.getText().toString() != null) {
            HashMap<String, Object> message = new HashMap<>();
            message.put("senderId", mAuth.getCurrentUser().getUid());
            message.put("receiverId", receivedUser.userId);
            message.put("message", inputMessage.getText().toString());
            message.put("timeStamp", new Date());
            database.collection("Chat").add(message);
        }

        else {
            Toast.makeText(ChatMessagesActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
        }


        inputMessage.setText(null);
    }

    private void listenMessages() {
        database.collection("Chat")
                .whereEqualTo("senderId", mAuth.getCurrentUser().getUid())
                .whereEqualTo("receiverId", receivedUser.userId)
                .addSnapshotListener(eventListener);
        database.collection("Chat")
                .whereEqualTo("senderId", receivedUser.userId)
                .whereEqualTo("receiverId", mAuth.getCurrentUser().getUid())
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if(error != null) {
            return;
        }
        if(value != null) {
            int count = chatMessages.size();

            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if(documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId= documentChange.getDocument().getString("senderId");
                    chatMessage.receiverId = documentChange.getDocument().getString("receiverId");
                    chatMessage.message = documentChange.getDocument().getString("message");
                    chatMessage.dateTime = getReadableDateTIme(documentChange.getDocument().getDate("timeStamp"));
                    chatMessage.dateObject = documentChange.getDocument().getDate("timeStamp");
                    chatMessages.add(chatMessage);
                }
            }
            Collections.sort(chatMessages, (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            if(count == 0) {
                chatAdapter.notifyDataSetChanged();
            } else {
                chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                recyclerView.smoothScrollToPosition(chatMessages.size() - 1);
            }
            recyclerView.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.GONE);
    };

    private void loadReceiverDetails() {
        receivedUser = (User) getIntent().getSerializableExtra("User");
        String fullName = receivedUser.first_name + " " + receivedUser.last_name;
        name.setText(fullName);
    }

    private String getReadableDateTIme(Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ChatMessagesActivity.this, ChatActivity.class);
        startActivity(intent);
        finish();
    }
}