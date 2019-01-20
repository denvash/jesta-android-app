package com.jesta.utils.db;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.*;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jesta.R;
import com.jesta.data.Mission;
import com.jesta.data.Relation;
import com.jesta.data.Status;
import com.jesta.data.User;
import com.jesta.data.chat.Author;
import com.jesta.data.chat.ChatManager;
import com.jesta.data.chat.ChatRoom;
import com.jesta.data.chat.Message;
import com.jesta.data.notification.Topic;
import com.jesta.data.notification.TopicDescriptor;
import com.jesta.gui.activities.MainActivity;
import com.jesta.gui.fragments.ChatFragment;
import com.jesta.gui.fragments.StatusFragment;
import com.tapadoo.alerter.Alerter;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.jesta.data.ConstantsKt.*;
import static com.jesta.utils.db.SysManager.DBTask.*;

public class SysManager {
    private static final String ACCESS_TOKEN = "AIzaSyDsS65FgkAHOS-eeb8iiYN1mF6bLqt_3rE";
    // authentication
    private FirebaseAuth _auth = FirebaseAuth.getInstance();
    private FirebaseInstanceId _firebaseInstance = FirebaseInstanceId.getInstance();

    // users management
    private static HashMap<String, User> _usersDict = new HashMap<>();
    private static HashMap<String, Mission> _jestasDict = new HashMap<>();
    private static HashMap<String, Relation> _relationsDict = new HashMap<>();
    private User _currentUser = null;

    // db
    private static DatabaseReference _usersDatabase = FirebaseDatabase.getInstance().getReference("users");
    private static DatabaseReference _jestasDatabase = FirebaseDatabase.getInstance().getReference("jestas");
    private static DatabaseReference _relationsDatabase = FirebaseDatabase.getInstance().getReference("relations");
    private static DatabaseReference _graveyardDatabase = FirebaseDatabase.getInstance().getReference("graveyard");

    // messaging
    private static FirebaseMessaging _messaging = FirebaseMessaging.getInstance();

    // storage
    private StorageReference _storage = FirebaseStorage.getInstance().getReference();


    // layout and _activity
    private static Activity _activity;

    public SysManager(Activity currentActivity) {
        _activity = currentActivity;

        // 20190120 listener added following bug when a new user X created during user Y running the app,
        // and not doing RELOAD_USERS, so new user X wasn't in _usersDict and app was crashing on a new message received
        // from user X
        DatabaseReference usersDBRef = FirebaseDatabase.getInstance().getReference("users");
        usersDBRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot ds, @Nullable String s) {
                User dbUser = ds.getValue(User.class);
                String userId = ds.getKey();
                if (dbUser == null) {
                    throw new NullPointerException("dbUser is null");
                }

                // new user added while app is running
                if (!_usersDict.containsKey(userId)) {
                    _usersDict.put(userId, dbUser);
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

    public enum DBTask {
        RELOAD_USERS, // update _usersDict
        RELOAD_JESTAS, // update _jestasDict
        RELOAD_RELATIONS, // update _relationsDict
        UPLOAD_FILE
    }

    public Task createDBTask(DBTask taskName, Uri uri) {
        if (taskName == UPLOAD_FILE) {
            final TaskCompletionSource<String> source = new TaskCompletionSource<>();
            final String randomFileName = "images/" + UUID.randomUUID().toString();
            _storage.child(randomFileName).putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            _storage.child(randomFileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUrl) {
                                    source.setResult(downloadUrl.toString());
                                }

                                public void onFailure(@NonNull Exception e) {
                                    source.setException(e);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            source.setException(e);
                        }
                    });
            return source.getTask();
        }
        return null;
    }

    public Task createDBTask(DBTask taskName) {
        if (taskName == RELOAD_USERS) {
            final TaskCompletionSource<List<User>> source = new TaskCompletionSource<>();
            _usersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<User> usersList = new ArrayList<>();

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        User dbUser = ds.getValue(User.class);
                        if (dbUser == null) {
                            throw new NullPointerException("dbUser is null");
                        }
                        _usersDict.put(dbUser.getId(), dbUser);
                        usersList.add(dbUser);
                    }
                    source.setResult(usersList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    source.setException(databaseError.toException());
                }
            });
            // return the task so it could be waited on the caller
            return source.getTask();
        } else if (taskName == RELOAD_JESTAS) {
            final TaskCompletionSource<List<Mission>> source = new TaskCompletionSource<>();
            _jestasDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Mission> jestasList = new ArrayList<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Mission dbJesta = ds.getValue(Mission.class);
                        if (dbJesta == null) {
                            throw new NullPointerException("dbJesta is null");
                        }
                        _jestasDict.put(dbJesta.getId(), dbJesta);
                        jestasList.add(dbJesta);
                    }

                    source.setResult(jestasList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    source.setException(databaseError.toException());
                }
            });
            // return the task so it could be waited on the caller
            return source.getTask();
        } else if (taskName == RELOAD_RELATIONS) {
            final TaskCompletionSource<List<Relation>> source = new TaskCompletionSource<>();
            _relationsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Relation> relationsList = new ArrayList<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Relation dbRelation = ds.getValue(Relation.class);
                        if (dbRelation == null) {
                            throw new NullPointerException("dbRelUserJesta is null");
                        }
                        _relationsDict.put(dbRelation.getId(), dbRelation);
                        relationsList.add(dbRelation);
                    }

                    source.setResult(relationsList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    source.setException(databaseError.toException());
                }
            });
            // return the task so it could be waited on the caller
            return source.getTask();
        }
        return null;
    }

    ;

    public FirebaseAuth getFirebaseAuth() {
        return _auth;
    }

    /**
     * Called when user authenticated against firebase auth.
     * Sets the currentUser of the system, with the user's data got from DB.
     * If user isn't on DB yet, it will insert a new entry for him at the DB.
     *
     * @param task
     * @param context
     */
    public void signInUser(@NonNull Task<AuthResult> task, Context context) throws Exception {
        if (!task.isSuccessful()) {
            throw new Exception(task.getException());
        }

        // Sign in via firebaseAuth success
        FirebaseUser firebaseUser = _auth.getCurrentUser();

        if (firebaseUser == null || firebaseUser.getEmail() == null) {
            throw new NullPointerException("firebaseUser or email is null");
        }

        User user = getCurrentUserFromDB();
        if (user != null) {// user exists in db
            // todo: store login time
        } else {// create new user and store in DB
            user = new User(firebaseUser);
            setUserOnDB(user);
        }

        // unsubscribe from all system messages topics
        try {
            _firebaseInstance.deleteInstanceId();
        } catch (IOException ioException) {
            MainActivity.Companion.getInstance().alertError(ioException.getMessage());
        }

        // subscribe to inbox for receiving system messages from other devices
        subscribeToTopic(new Topic(TopicDescriptor.USER_INBOX, user, null));

        // update the current user in the system
        _currentUser = user;

    }

    public void signOutUser(Context context) {
        _auth.signOut();
        _currentUser = null;

    }

    /**
     * Note: RELOAD_USERS should be called before using this function at the first time
     * Call RELOAD_USERS every time you need the users list to be updated from DB
     *
     * @return
     */
    public User getCurrentUserFromDB() {
        FirebaseUser firebaseUser = _auth.getCurrentUser();
        if (firebaseUser == null) {
            return null;
        }

        // get the currently logged in user based on firebase Auth and users db
        User currentUser = _usersDict.get(firebaseUser.getUid());
        return currentUser;
    }

    public void setRelationOnGraveDB(Relation rel) {
        _graveyardDatabase.child("relations").child(rel.getId()).setValue(rel);
    }

    public void setMissionOnGraveDB(Mission mission) {
        _graveyardDatabase.child("jestas").child(mission.getId()).setValue(mission);
    }

    public void setUserOnDB(User user) {
        _usersDatabase.child(user.getId()).setValue(user);
        _usersDict.put(user.getId(), user);
    }

    public void setMissionOnDB(Mission mission) {
        _jestasDatabase.child(mission.getId()).setValue(mission);
        _jestasDict.put(mission.getId(), mission);

    }

    public void setRelationOnDB(Relation rel) {
        _relationsDatabase.child(rel.getId()).setValue(rel);
        _relationsDict.put(rel.getId(), rel);
    }

    public User getUserByID(String id) {
        return _usersDict.get(id);
    }

    public Mission getMissionByID(String id) {
        if (id == null || id.equals("null")) {
            return null;
        }
        return _jestasDict.get(id);
    }

    public Relation getRelationByID(String id) {
        if (id == null || id.equals("null")) {
            return null;
        }
        return _relationsDict.get(id);
    }

    public Relation getRelation(User doer, User poster, Mission jesta) {
        if (doer == null || poster == null || jesta == null) {
            return null;
        }

        Collection<Relation> relations = _relationsDict.values();
        for (Relation relation : relations) {
            if (relation.getDoerID().equals(doer.getId()) && relation.getPosterID().equals(poster.getId())
                    && relation.getMissionID().equals(jesta.getId())) {
                return relation;
            }
        }
        return null;
    }

    public void removeRelation(Relation rel) {
        _relationsDatabase.child(rel.getId()).removeValue();
        _relationsDict.remove(rel.getId());
    }

    public void removeMission(Mission mission) {
        _jestasDatabase.child(mission.getId()).removeValue();
        _jestasDict.remove(mission.getId());
    }

    public Task getUserRelations(String id) {
        if (id == null || id.equals("null")) {
            return null;
        }
        final String usr_id = id;
        final List<Relation> relUserList = new ArrayList<>();
        Task<List<Relation>> allRels = this.createDBTask(DBTask.RELOAD_RELATIONS);
        final TaskCompletionSource<List<Relation>> source = new TaskCompletionSource<>();
        allRels.addOnCompleteListener(new OnCompleteListener<List<Relation>>() {
            @Override
            public void onComplete(@NonNull Task<List<Relation>> task) {

                if (!task.isSuccessful()) {
                    MainActivity.Companion.getInstance().alertError(Objects.requireNonNull(task.getException()).getMessage());
                    return;
                }

                List<Relation> lst = task.getResult();
                for (Relation i : lst) {
                    if (i.getDoerID().equals(usr_id) || i.getPosterID().equals(usr_id))
                        relUserList.add(i);
                }
                source.setResult(relUserList);
            }
        });
        return source.getTask();
    }

    public Task getStatusList() {
        final String user = this.getCurrentUserFromDB().getId();
        final HashMap<String, Status> statusMap = new HashMap<>();
        Task<List<Relation>> allRels = this.createDBTask(DBTask.RELOAD_RELATIONS);
        final TaskCompletionSource<List<Status>> source = new TaskCompletionSource<>();
        allRels.addOnCompleteListener(new OnCompleteListener<List<Relation>>() {
            @Override
            public void onComplete(@NonNull Task<List<Relation>> task) {

                if (!task.isSuccessful()) {
                    MainActivity.Companion.getInstance().alertError(Objects.requireNonNull(task.getException()).getMessage());
                    return;
                }

                List<Relation> lst = task.getResult();
                for (Relation i : lst) {
                    if (i.getPosterID().equals(user)) {
                        Status sts = statusMap.get(i.getMissionID());
                        if (sts == null) {
                            sts = new Status();
                            sts.setMissionID(i.getMissionID());
                            sts.setPoster(true);
                            statusMap.put(i.getMissionID(), sts);
                        }
                        if (i.getDoerID().equals(RELATION_EMPTY_DOER_ID))
                            sts.getDoerIDList().add(0, i);
                        else {
                            sts.getDoerIDList().add(i);
                            if (sts.getStatus() != RELATION_STATUS_DONE
                                    && i.getStatus() == RELATION_STATUS_DONE)
                                sts.setStatus(RELATION_STATUS_DONE);
                            else if (sts.getStatus() == RELATION_STATUS_INIT
                                    && i.getStatus() == RELATION_STATUS_IN_PROGRESS)
                                sts.setStatus(RELATION_STATUS_IN_PROGRESS);
                        }
                    } else if (i.getDoerID().equals(user)) {
                        Status sts = new Status();
                        sts.setMissionID(i.getMissionID());
                        sts.setStatus(i.getStatus());
                        sts.getDoerIDList().add(i);
                        statusMap.put(i.getMissionID(), sts);
                    }
                }
                source.setResult(new ArrayList<Status>(statusMap.values()));
            }
        });
        return source.getTask();
    }

    public void moveToGraveDB(Status sts) {
        for (Relation i : sts.getDoerIDList()) {
            this.setRelationOnGraveDB(i);
            this.removeRelation(i);
        }
        Mission mission = getMissionByID(sts.getMissionID());
        this.setMissionOnGraveDB(mission);
        this.removeMission(mission);
    }

    public void updateRelationsDone(Status sts) {
        sts.setStatus(RELATION_STATUS_DONE);
        for (Relation i : sts.getDoerIDList()) {
            if (i.getStatus() == RELATION_STATUS_IN_PROGRESS) {
                i.setStatus(RELATION_STATUS_DONE);
                this.setRelationOnDB(i);
            }
        }
    }

    public void onAcceptDoer(Relation rel, Mission mission) {
        if (mission.getNumOfPeople() == 0)
            return;
        mission.setNumOfPeople(mission.getNumOfPeople() - 1);
        if (mission.getNumOfPeople() == 0)
            mission.setAvailable(false);
        this.setMissionOnDB(mission);

        rel.setStatus(RELATION_STATUS_IN_PROGRESS);
        this.setRelationOnDB(rel);
        User doer = getUserByID(rel.getDoerID());
        answerTodoJestaForUser(doer, mission, true).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (!task.isSuccessful()) {
                    MainActivity.Companion.getInstance().alertError(Objects.requireNonNull(task.getException()).getMessage());
                }
            }
        });
    }

    public void onDeclineUser(Relation rel) {
        rel.setStatus(RELATION_STATUS_USER_DECLINED);
        this.setRelationOnDB(rel);
        User doer = getUserByID(rel.getDoerID());
        Mission mission = getMissionByID(rel.getMissionID());
        answerTodoJestaForUser(doer, mission, false).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (!task.isSuccessful()) {
                    MainActivity.Companion.getInstance().alertError(Objects.requireNonNull(task.getException()).getMessage());
                    return;
                }
            }
        });
    }

    /**
     * Messaging and push notifications
     */

    public Task subscribeToTopic(Topic topic) {
        final TaskCompletionSource<String> source = new TaskCompletionSource<>();
        _messaging.subscribeToTopic(topic.toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        source.setResult("msg_subscribed");

                        if (!task.isSuccessful()) {
                            source.setException(task.getException());
                        }

                    }
                });
        return source.getTask();
    }

    public Task unsubscribeFromTopic(Topic topic) {
        final TaskCompletionSource<String> source = new TaskCompletionSource<>();
        _messaging.unsubscribeFromTopic(topic.toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        source.setResult("msg_unsubscribed");

                        if (!task.isSuccessful()) {
                            source.setException(task.getException());
                        }

                    }
                });
        return source.getTask();
    }

    private static String inputstreamToString(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNext()) {
            stringBuilder.append(scanner.nextLine());
        }
        return stringBuilder.toString();
    }

    public Task askTodoJestaForUser(Mission jesta) {
        final TaskCompletionSource<String> source = new TaskCompletionSource<>();
        RequestQueue queue = Volley.newRequestQueue(_activity.getApplicationContext());

        final String SEND_MESSAGE_ENDPOINT = "https://us-central1-jesta-42.cloudfunctions.net/messaging/askTodoJestaForUser";
        // TODO add authorization header

        // TODO Remove this debugging hack
        String authorId = jesta.getPosterID();
        if (authorId.equals("null") || authorId == null) {
            authorId = getCurrentUserFromDB().getId();
        }

        String url = null;
        try {
            User author = getUserByID(authorId);
            String receiverInbox = author.getId() + "_" + TopicDescriptor.USER_INBOX;

            String receiver = author.getId();
            String sender = getCurrentUserFromDB().getId();

            String jestaId = jesta.getId();


            String title = "You got a Doer! \uD83E\uDD29";
            String body = getCurrentUserFromDB().getDisplayName() + " offered to do a Jesta for you! Check out the Status tab!";


            title = URLEncoder.encode(title, StandardCharsets.UTF_8.toString());
            body = URLEncoder.encode(body, StandardCharsets.UTF_8.toString());
            jestaId = URLEncoder.encode(jestaId, StandardCharsets.UTF_8.toString());
            sender = URLEncoder.encode(sender, StandardCharsets.UTF_8.toString());
            receiver = URLEncoder.encode(receiver, StandardCharsets.UTF_8.toString());


            url = SEND_MESSAGE_ENDPOINT + "?topic=" + receiverInbox +
                    "&title=" + title +
                    "&body=" + body +
                    "&receiver=" + receiver +
                    "&sender=" + sender +
                    "&jesta=" + jestaId;


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

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
                });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        return source.getTask();
    }

    public Task answerTodoJestaForUser(User receiver, Mission jesta, Boolean isAccept) {
        final TaskCompletionSource<String> source = new TaskCompletionSource<>();
        RequestQueue queue = Volley.newRequestQueue(_activity.getApplicationContext());
        final String PROJECT_ID = "jesta-42";

        final String SEND_MESSAGE_ENDPOINT = "https://us-central1-jesta-42.cloudfunctions.net/messaging/askTodoJestaForUser";
        // TODO add authorization header

        String receiverId = receiver.getId();
        String receiverInbox = receiverId + "_" + TopicDescriptor.USER_INBOX;
        String sender = getCurrentUserFromDB().getId();
        String jestaId = jesta.getId();

        String title, body;
        if (isAccept) {
            title = "You got a match!  \uD83E\uDD29";
            body = getCurrentUserFromDB().getDisplayName() + " accepted you as a doer!";
        } else {
            title = "Bad news \uD83E\uDD7A";
            body = getCurrentUserFromDB().getDisplayName() + " declined you as a doer!";
        }

        try {
            // TODO change to post request to avoid this shit
            title = URLEncoder.encode(title, StandardCharsets.UTF_8.toString());
            body = URLEncoder.encode(body, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            MainActivity.Companion.getInstance().alertError(e.getMessage());
        }

        String url = SEND_MESSAGE_ENDPOINT + "?topic=" + receiverInbox +
                "&title=" + title +
                "&body=" + body +
                "&receiver=" + receiverId +
                "&sender=" + sender +
                "&jesta=" + jestaId;

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
                });

// Add the request to the RequestQueue.
        queue.add(stringRequest);

        return source.getTask();
    }

    public Task sendMessageToTopic(Topic topic, String title, String body) throws JSONException, MalformedURLException, IOException {
        final TaskCompletionSource<String> source = new TaskCompletionSource<>();
        RequestQueue queue = Volley.newRequestQueue(_activity.getApplicationContext());
        final String PROJECT_ID = "jesta-42";

        final String SEND_MESSAGE_ENDPOINT = "https://us-central1-jesta-42.cloudfunctions.net/messaging/sendMessage";
        // TODO add authorization header

        String topicName = topic.topicName();

        if (title == null || body == null) {
            MainActivity.Companion.getInstance().alertError(null);
        } else {
            // TODO change to post request to avoid this shit
            title = URLEncoder.encode(title, StandardCharsets.UTF_8.toString());
            body = URLEncoder.encode(body, StandardCharsets.UTF_8.toString());
        }

        String url = SEND_MESSAGE_ENDPOINT + "?topic=" + topicName + "&title=" + title + "&body=" + body;

// Request a string response from the provided URL.
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
                }) {
        };

// Add the request to the RequestQueue.
        queue.add(stringRequest);

        return source.getTask();
    }

    public void listenForIncomingInboxMessages(final Activity activity) {
        // listen for child add event; e.g. new message has arrived
        final String receiverInbox = getCurrentUserFromDB().getId() + "_" + TopicDescriptor.USER_INBOX;
        DatabaseReference roomDBRef = FirebaseDatabase.getInstance().getReference("inbox/" + receiverInbox);
        roomDBRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot ds, @Nullable String s) {
                final HashMap dbMsg = (HashMap) ds.getValue();
                String msgKey = ds.getKey();
                if (dbMsg == null) {
                    throw new NullPointerException("dbUser is null");
                }
                String senderId = (String) dbMsg.get("sender");
                User sender = getUserByID(senderId);
                Author UIAuthor = new Author(sender.getId(), sender.getDisplayName(), sender.getPhotoUrl());
                Date date = new Date(Long.parseLong((String) dbMsg.get("time")));
                final Message UIMessage = new Message(msgKey, UIAuthor, date, (String) dbMsg.get("body"));

                // delete the message we got !!!
                DatabaseReference msgDBRef = FirebaseDatabase.getInstance().getReference("inbox/" + receiverInbox + "/" + msgKey);
                msgDBRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Alerter.create(activity)
                                .setTitle((String) dbMsg.get("title"))
                                .setText(UIMessage.getText())
                                .setBackgroundColorRes(R.color.colorPrimary)
                                .setIcon(R.drawable.ic_jesta_diamond_normal)
                                .setDuration(5000)
                                .show();
                    }
                });
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
        final ChatManager chatManager = new ChatManager(_activity);
        _relationsDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot ds, @Nullable String s) {
                // IMPORTANT: RelationId used as ChatId
                final String roomId = ds.getKey();
                // listen for child add event; e.g. new message has arrived
                chatManager.getMessagesByRoomId(roomId).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (!task.isSuccessful()) {
                            MainActivity.Companion.getInstance().alertError(Objects.requireNonNull(task.getException()).getMessage());
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
                                final User sender = getUserByID(senderId);
                                Author UIAuthor = new Author(sender.getId(), sender.getDisplayName(), sender.getPhotoUrl());
                                Date date = new Date(Long.parseLong((String) dbMsg.get("time")));
                                final Message UIMessage = new Message(msgKey, UIAuthor, date, (String) dbMsg.get("body"));

                                // don't show message from myself
                                if (UIMessage.getAuthor().getDbID().equals(getCurrentUserFromDB().getId())) {
                                    return;
                                }

                                if (!messagesHistory.contains(UIMessage)) {
                                    Alerter.create(_activity)
                                            .setTitle(sender.getDisplayName() + " says: ")
                                            .setText(UIMessage.getText())
                                            .setBackgroundColorRes(R.color.colorPrimary)
                                            .setDuration(5000)
                                            .setIcon(R.drawable.ic_jesta_chat)
                                            .addButton("Go to Chat", R.style.AlertButton, new View.OnClickListener() {
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

    /**
     * Layout, UI and system settings and logs
     */

    public String getChatRoomId(ChatRoom chatRoom) {
        Relation relation = getRelation(chatRoom.getAsker(), chatRoom.getPoster(), chatRoom.getJesta());
        return relation.getId();
    }

}
