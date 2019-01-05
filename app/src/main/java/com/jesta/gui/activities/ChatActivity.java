package com.jesta.gui.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.*;
import com.jesta.R;
import com.jesta.data.User;
import com.jesta.utils.adapters.MessageListAdapter;
import com.jesta.utils.chat.ChatManager;
import com.jesta.utils.chat.ChatMessage;
import com.jesta.utils.db.SysManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView mMessageRecycler;
    private SysManager sysManager = new SysManager();
    private ChatManager chatManager = new ChatManager();
    private List<ChatMessage> messagesLiveSnapShot;
    MessageListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);




        Bundle b = getIntent().getExtras();
        final String roomId = b.getString("roomId");
        final Context context = this;


        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(context));
        messagesLiveSnapShot = new ArrayList<ChatMessage>();
        adapter = new MessageListAdapter(messagesLiveSnapShot);
        mMessageRecycler.setAdapter(adapter);


        // listen for child add event; e.g. new message has arrived
        DatabaseReference roomDBRef = FirebaseDatabase.getInstance().getReference("chat/" + roomId);
        roomDBRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot ds, @Nullable String s) {
                HashMap dbMsg = (HashMap)ds.getValue();
                String msgKey = ds.getKey();
                if (dbMsg == null) {
                    throw new NullPointerException("dbUser is null");
                }
                String senderId = (String)dbMsg.get("sender");
                User sender = sysManager.getUserByID(senderId);
                ChatMessage message = new ChatMessage(msgKey, (String)dbMsg.get("body"), sender, (String)dbMsg.get("time"));
                if (!messagesLiveSnapShot.contains(message)) {
                    messagesLiveSnapShot.add(message);
                    Collections.sort(messagesLiveSnapShot, new ChatMessage.SortByDate());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        final Button sendBtn = findViewById(R.id.button_chatbox_send);
        final EditText inputEditText = findViewById(R.id.edittext_chatbox);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputTxt = inputEditText.getText().toString();
                if (inputTxt.replaceAll("\\s","").equals("")) {
                    Toast.makeText(context, "Can't send empty message!", Toast.LENGTH_SHORT).show();
                    return;
                }

                sendBtn.setEnabled(false);
//                inputTxt.setEnabled(false);
                chatManager.sendMessage(getApplicationContext(), roomId, sysManager.getCurrentUserFromDB(), inputTxt).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        inputEditText.setText("");
                        if (!task.isSuccessful()) {
                            // todo some error
                            return;
                        }
                        String result = (String)task.getResult();
//                        inputTxt.setEnabled(true);
                        sendBtn.setEnabled(true);
                    }
                });
            }
        });





    }
}
