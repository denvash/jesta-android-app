package com.jesta.data.chat;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.*;
import com.google.firebase.messaging.FirebaseMessaging;
import com.jesta.R;
import com.jesta.data.Relation;
import com.jesta.data.User;
import com.jesta.gui.activities.MainActivity;
import com.jesta.gui.fragments.ChatFragment;
import com.jesta.utils.db.SysManager;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.tapadoo.alerter.Alerter;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

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

    public Task subscribeToChatRoom(String roomId) {
        final TaskCompletionSource<String> source = new TaskCompletionSource<>();
        _messaging.subscribeToTopic(roomId).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    public static class SortByDate implements Comparator<Message>
    {
        @Override
        public int compare(Message o1, Message o2) {
            if (o1.getDate().before(o2.getDate())) {
                return -1;
            }
            else {
                return 1;
            }
        }
    }

    public Task getMessagesByRoomId(String roomId) {
        final TaskCompletionSource<List<Message>> source = new TaskCompletionSource<>();
        DatabaseReference roomDBRef = FirebaseDatabase.getInstance().getReference("chat/" + roomId);
        roomDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Message> messagesList = new ArrayList<>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    HashMap dbMsg = (HashMap) ds.getValue();
                    String msgKey = ds.getKey();
                    if (dbMsg == null) {
                        throw new NullPointerException("dbUser is null");
                    }
                    String senderId = (String) dbMsg.get("sender");
                    User sender = sysManager.getUserByID(senderId);

                    Author UIAuthor = new Author(sender.getId(), sender.getDisplayName(), sender.getPhotoUrl());
                    Date date = new Date(Long.parseLong((String) dbMsg.get("time")));
                    Message message = new Message(msgKey, UIAuthor, date, (String) dbMsg.get("body"));
                    messagesList.add(message);
                }

                Collections.sort(messagesList, new SortByDate());
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

    public void listenForChatUpdateAdapter(final MessagesListAdapter<Message> adapter, final String roomId, final List<Message> messagesHistory) {
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
                Author UIAuthor = new Author(sender.getId(), sender.getDisplayName(), sender.getPhotoUrl());
                Date date = new Date(Long.parseLong((String)dbMsg.get("time")));
                Message UIMessage = new Message(msgKey, UIAuthor, date, (String)dbMsg.get("body"));

                if (!messagesHistory.contains(UIMessage)) {
                    adapter.addToStart(UIMessage, true);
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
    }

    public void listenForChatAndNotify(final Activity activity) {
        final ArrayList<String> filteredChatRooms = new ArrayList<>();
        Task userRelTask = sysManager.getUserRelations(sysManager.getCurrentUserFromDB().getId());
        userRelTask.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (!task.isSuccessful()) {
                    // todo
                    return;
                }

                List<Relation> userRelations = (List<Relation>) task.getResult();
                for (final Relation relation : userRelations) {
                    final String roomId = relation.getId();
                    filteredChatRooms.add(roomId);
                    // todo sepereate the code below to another function
                    getMessagesByRoomId(roomId).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (!task.isSuccessful()) {
                                // todo
                                return;
                            }
                            final List<Message> messagesHistory = (List<Message>) task.getResult();


                            // listen for child add event; e.g. new message has arrived
                            DatabaseReference roomDBRef = FirebaseDatabase.getInstance().getReference("chat/" + roomId);
                            roomDBRef.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot ds, @Nullable String s) {
                                    HashMap dbMsg = (HashMap) ds.getValue();
                                    String msgKey = ds.getKey();
                                    if (dbMsg == null) {
                                        throw new NullPointerException("dbUser is null");
                                    }
                                    String senderId = (String) dbMsg.get("sender");
                                    final User sender = sysManager.getUserByID(senderId);
                                    Author UIAuthor = new Author(sender.getId(), sender.getDisplayName(), sender.getPhotoUrl());
                                    Date date = new Date(Long.parseLong((String) dbMsg.get("time")));
                                    final Message UIMessage = new Message(msgKey, UIAuthor, date, (String) dbMsg.get("body"));

                                    // don't show message from myself
                                    if (UIMessage.getAuthor().getDbID().equals(sysManager.getCurrentUserFromDB().getId())) {
                                        return;
                                    }

                                    if (!messagesHistory.contains(UIMessage)) {
                                        Alerter.create(sysManager.getActivity())
                                                .setTitle(sender.getDisplayName() + " says: ")
                                                .setText(UIMessage.getText())
                                                .setBackgroundColorRes(R.color.colorPrimary)
                                                .setDuration(5000)
                                                .setIcon(R.drawable.ic_jesta_chat)
                                                // todo go to chat room
                                                .addButton("GO TO CHAT", R.style.AlertButton, new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        if (activity instanceof MainActivity) {
                                                            ((MainActivity) activity)
                                                                    .getInstance()
                                                                    .getFragNavController()
                                                                    .pushFragment(new ChatFragment().newInstance(roomId));
                                                        }
                                                    }
                                                })
                                                .show();
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
                        }
                    });
                }
            }
        });
    }
}
