package com.jesta.util;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.*;
import com.jesta.R;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import static com.jesta.util.SysManager.DBTask.RELOAD_JESTAS;
import static com.jesta.util.SysManager.DBTask.RELOAD_USERS;
import static com.jesta.util.SysManager.DBTask.UPLOAD_FILE;

/**
 * System manager
 */
public class SysManager {
    // authentication
    private FirebaseAuth _auth = FirebaseAuth.getInstance();

    // users db and management
    private DatabaseReference _usersDatabase = FirebaseDatabase.getInstance().getReference("users");
    private DatabaseReference _jestasDatabase = FirebaseDatabase.getInstance().getReference("jestas");
    private static HashMap<String, User> _usersDict = new HashMap<>();
    private static HashMap<String, Mission> _jestasDict = new HashMap<>();
    private User _currentUser = null;

    // storage
    private StorageReference _storage = FirebaseStorage.getInstance().getReference();


    // layout and _activity
    private Activity _activity;
    private TextView _backButtonTv;

    // loading animation
    private ProgressBar _pgsBar;

    public SysManager(Fragment fragment) {

    }

    public SysManager(Activity currentActivity) {
        _activity = currentActivity;

        // Set listener if back button is available
        _backButtonTv = (TextView) _activity.findViewById(R.id.back_button);
        if (_backButtonTv != null) {
            _backButtonTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    _activity.finish();
                }
            });
        }
    }

    public void startLoadingAnim() {
        _activity.setContentView(R.layout.loading);
        _pgsBar = (ProgressBar)_activity.findViewById(R.id.pBar);
        _pgsBar.setVisibility(View.VISIBLE);
    }

    public void stopLoadingAnim() {
        _pgsBar.setVisibility(View.INVISIBLE);
    }

    public void startLoadingAnim(ProgressBar pgsBar) {
        pgsBar.setVisibility(View.VISIBLE);
    }

    public void stopLoadingAnim(ProgressBar pgsBar) {
        pgsBar.setVisibility(View.INVISIBLE);
    }

    /**
     * User authentication
     * TODO-MAX: implement updateUserInDB etc'...
     */

    public enum DBTask
    {
        RELOAD_USERS, // update _usersDict
        RELOAD_JESTAS, // update _jestasDict
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
                            _storage.child(randomFileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                            {
                                @Override
                                public void onSuccess(Uri downloadUrl)
                                {
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
//                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
//                                    .getTotalByteCount());
//                        }
//                    });
            return source.getTask();
        }
        return null;
    }

    public Task createDBTask(DBTask taskName) {
        // todo firebase realtime db - change permission from public to private (only for app)
        if (taskName == RELOAD_USERS) {
            final TaskCompletionSource<List<User>> source = new TaskCompletionSource<>();
            _usersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<User> usersList = new ArrayList<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        HashMap dbUser = (HashMap)ds.getValue();
                        if (dbUser == null) {
                            throw new NullPointerException("dbUser is null");
                        }
                        User user = new User(dbUser);
                        _usersDict.put((String)dbUser.get("id"), user);
                        usersList.add(user);
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
        }
        else if (taskName == RELOAD_JESTAS) {
            final TaskCompletionSource<List<Mission>> source = new TaskCompletionSource<>();
            _jestasDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Mission> jestasList = new ArrayList<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        HashMap dbJesta = (HashMap)ds.getValue();
                        if (dbJesta == null) {
                            throw new NullPointerException("dbJesta is null");
                        }
                        Mission jesta = new Mission(dbJesta);
                        // todo: use randomUUID() when storing jestas in db
                        // here: use (String)dbJesta.get("id")
                        _jestasDict.put(UUID.randomUUID().toString(), jesta);
                        jestasList.add(jesta);
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
        }
        return null;
    };

    public FirebaseAuth getFirebaseAuth() {
        return _auth;
    }

    /**
     * Called when user authenticated against firebase auth.
     * Sets the currentUser of the system, with the user's data got from DB.
     * If user isn't on DB yet, it will insert a new entry for him at the DB.
     * @param task
     * @param context
     * @param previousActivity
     */
    public void signInUser(@NonNull Task<AuthResult> task, Context context, Activity previousActivity) throws Exception {
        if (!task.isSuccessful()) {
            throw new Exception(task.getException());
        }

        // Sign in via firebaseAuth success
        FirebaseUser firebaseUser = _auth.getCurrentUser();

        if (firebaseUser == null || firebaseUser.getEmail() == null) {
            throw new NullPointerException("firebaseUser or email is null");
        }

        Toast.makeText(context, "User logged in successfully", Toast.LENGTH_SHORT).show();

        User user = getCurrentUserFromDB();
        if (user != null) {// user exists in db
            // todo: store login time
        } else {// create new user and store in DB
            user = new User(firebaseUser);
            setUserOnDB(user);
        }

        _currentUser = user;
    }

    public void signOutUser(Context context) {
        _auth.signOut();
        _currentUser = null;
        Toast.makeText(context, "User logged out successfully", Toast.LENGTH_LONG).show();
    }

    /**
     * Note: RELOAD_USERS should be called before using this function at the first time
     * Call RELOAD_USERS every time you need the users list to be updated from DB
     * @return
     */
    public User getCurrentUserFromDB() {
        FirebaseUser firebaseUser = _auth.getCurrentUser();
        if (firebaseUser == null) {
            return null;
        }
        return _usersDict.get(firebaseUser.getUid());
    }

    public void setUserOnDB(User user) {
        _usersDatabase.child(user.getId()).setValue(user);
    }

    public void setMissionOnDB(Mission mission) {
        _jestasDatabase.child(mission.getId()).setValue(mission);
    }

    /**
     * Layout, UI and system settings and logs
     */

    public void setTitle(String title) {
        TextView pageNameTv = (TextView) _activity.findViewById(R.id.page_name);
        pageNameTv.setText(title);
    }

    public void showBackButton(Boolean flag) {
        _backButtonTv = (TextView) _activity.findViewById(R.id.back_button);
        if (_backButtonTv == null)
            return;
        _backButtonTv.setText(flag ? _activity.getResources().getString(R.string.leftArrow) : "");
    }

    public void showKeyboardAutomatically(Boolean flag) {
        if (flag) {
            _activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        else {// don't show keyboard
            _activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }

}