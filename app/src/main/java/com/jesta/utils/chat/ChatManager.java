package com.jesta.utils.chat;

import android.content.Context;
import androidx.annotation.NonNull;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.*;
import com.google.firebase.messaging.FirebaseMessaging;
import com.jesta.data.Mission;
import com.jesta.data.User;
import com.jesta.utils.db.SysManager;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.facebook.FacebookSdk.getCacheDir;

public class ChatManager {
    private final String SEND_MESSAGE_ENDPOINT = "https://us-central1-jesta-42.cloudfunctions.net/messaging/sendChatMessage";
    private final SysManager sysManager = new SysManager();
    private static FirebaseMessaging _messaging = FirebaseMessaging.getInstance();
//    private static DatabaseReference _chatDatabase = FirebaseDatabase.getInstance().getReference("chat");

    public Task subscribeToChatRoom(ChatRoom room) {
        final TaskCompletionSource<String> source = new TaskCompletionSource<>();
        _messaging.subscribeToTopic(room.getId()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (!task.isSuccessful()) {
                    source.setException(task.getException());
                }
                source.setResult("subscribed");
            }
        });
        return source.getTask();
    }

    public Task sendMessage(Context context, String roomId, User sender, String message) {
        final TaskCompletionSource<String> source = new TaskCompletionSource<>();
        RequestQueue queue = Volley.newRequestQueue(context);

        // TODO add authorization header

        String topicName = roomId;
        String body = message;
        String senderId = sender.getId();

        try {
            body = URLEncoder.encode(body, StandardCharsets.UTF_8.toString());
            senderId = URLEncoder.encode(senderId, StandardCharsets.UTF_8.toString());
        }
        catch (Exception e) {
            // todo something
        }

        String url = SEND_MESSAGE_ENDPOINT + "?topic=" + topicName + "&body=" + body + "&sender=" + senderId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        source.setResult(response);
                        System.out.println(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        source.setException(error);
                        System.out.println(error.getMessage());
                    }
                })
        {};
        queue.add(stringRequest);

        return source.getTask();
    }

    public Task getMessagesByRoomId(String roomId) {
        final TaskCompletionSource<List<ChatMessage>> source = new TaskCompletionSource<>();
        DatabaseReference roomDBRef = FirebaseDatabase.getInstance().getReference("chat/" + roomId);
        roomDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ChatMessage> messagesList = new ArrayList<>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    HashMap dbMsg = (HashMap) ds.getValue();
                    String msgKey = ds.getKey();
                    if (dbMsg == null) {
                        throw new NullPointerException("dbUser is null");
                    }
                    String senderId = (String) dbMsg.get("sender");
                    User sender = sysManager.getUserByID(senderId);
                    ChatMessage message = new ChatMessage(msgKey, (String) dbMsg.get("body"), sender, (String) dbMsg.get("time"));
                    messagesList.add(message);
                }

                Collections.sort(messagesList, new ChatMessage.SortByDate());
                source.setResult(messagesList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                source.setException(databaseError.toException());
            }
        });
        // return the task so it could be waited on the caller
        return source.getTask();
    }

}
