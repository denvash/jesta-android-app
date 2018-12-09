package com.jesta.util;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.jesta.MainActivity;
import com.jesta.R;
import com.jesta.login.ErrorActivity;
import com.jesta.pathChoose.PathActivity;

import java.util.*;

import static com.jesta.util.SysManager.DBTask.*;

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

    // layout and _activity
    private Activity _activity;
    private TextView _backButtonTv;

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

    /**
     * User authentication
     * TODO-MAX: implement updateUserInDB etc'...
     */

    public enum DBTask
    {
        RELOAD_USERS, // update _usersDict
        RELOAD_JESTAS // update _jestasDict
    }

    public Task createDBTask(DBTask taskName) {
        // todo firebase realtime db - change permission from public to private (only for app)
        if (taskName == RELOAD_USERS) {
            final TaskCompletionSource<List<User>> source = new TaskCompletionSource<>();
            _usersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (_usersDict.size() > 0) {
//                        return; // avoid Task already completed exception

                    }

                    List<User> usersList = new ArrayList<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        HashMap dbUser = (HashMap)ds.getValue();

                        // todo create a constructor for User
                        User user = new User((String)dbUser.get("id"), (String)dbUser.get("email"));
                        _usersDict.put((String)dbUser.get("id"), user);
                        usersList.add(user);
                    }
                    source.setResult(usersList);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
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
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (_jestasDict.size() > 0) {
//                        return; // avoid Task already completed exception
                    }
                    List<Mission> jestasList = new ArrayList<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        HashMap dbJesta = (HashMap)ds.getValue();

                        // todo create a constructor for User
                        Mission jesta = new Mission(dbJesta);
                        // todo: use randomUUID() when storing jestas in db
                        // here: use (String)dbJesta.get("id")
                        _jestasDict.put(UUID.randomUUID().toString(), jesta);
                        jestasList.add(jesta);
                    }

                    source.setResult(jestasList);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
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

        // Sign in success, update UI with the signed-in fireBaseUser's information
        FirebaseUser firebaseUser = _auth.getCurrentUser();

        Toast.makeText(context, "User logged in successfully", Toast.LENGTH_SHORT).show();

        User user = getCurrentUserFromDB();
        if (user != null) {// user exists in db

        } else {// create new user and store in DB
            user = new User(firebaseUser.getUid(), firebaseUser.getEmail());
            if (firebaseUser.getDisplayName() != null) {
                user.setDisplayName(firebaseUser.getDisplayName());
            }
            // todo: the following causing a bug (possible too big data to store on db)
//                    if (firebaseUser.getPhotoUrl() != null) {
//                        user.setPhotoUrl(firebaseUser.getPhotoUrl());
//                    }
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

    public void setJestaOnDB(Jesta jesta) {
        _jestasDatabase.child(jesta.getId()).setValue(jesta);
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
