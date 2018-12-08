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

import static com.jesta.util.SysManager.DBTask.INIT_USERS_LIST;

/**
 * System manager
 */
public class SysManager {
    // authentication
    private FirebaseAuth _auth = FirebaseAuth.getInstance();

    // users db and management
    private DatabaseReference _usersDatabase = FirebaseDatabase.getInstance().getReference("users");
    private List<User> _usersList = new ArrayList<>();
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
        INIT_USERS_LIST // init usersList and currentUser
    }

    public Task createDBTask(DBTask taskName) {
        // todo firebase realtime db - change permission from public to private (only for app)
        if (taskName == INIT_USERS_LIST) {
            final TaskCompletionSource<List<User>> source = new TaskCompletionSource<>();

            // Set listeners
            // Update currentUser and usersList
            _usersDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<User> usersList = new ArrayList<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        HashMap dbUser = (HashMap)ds.getValue();

                        // todo create a constructor for User
                        User user = new User((String)dbUser.get("id"), (String)dbUser.get("email"));
                        usersList.add(user);
                        _usersList.add(user);
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
        return null;
    };

    public FirebaseAuth getFirebaseAuth() {
        return _auth;
    }

    public void afterLogin(@NonNull Task<AuthResult> task, Context context, Activity previousActivity) {
        if (task.isSuccessful()) {
            try {
                // Sign in success, update UI with the signed-in fireBaseUser's information
                FirebaseUser firebaseUser = _auth.getCurrentUser();

                Toast.makeText(context, "User logged in successfully", Toast.LENGTH_SHORT).show();

                // Note: after we do finish() there is no activities waiting to pop
                // Therefore we should end the next piece of code with startActivity!
                previousActivity.finish();

                User user = getCurrentUserFromDB();
                if (user != null) {// user exists in db

                }
                else {// create new user and store in DB
                    user = new User(firebaseUser.getUid(), firebaseUser.getEmail());
                    if (firebaseUser.getDisplayName() != null) {
                        user.setDisplayName(firebaseUser.getDisplayName());
                    }
                    if (firebaseUser.getPhotoUrl() != null) {
                        user.setPhotoUrl(firebaseUser.getPhotoUrl());
                    }
                    _usersDatabase.child(user.getId()).setValue(user);
                }

                _currentUser = user;
                Intent i = new Intent(context, PathActivity.class);
                context.startActivity(i);
            } catch (Exception e) {
                logAndGoToErrorActivity(context, previousActivity, e.getMessage());
            }
        } else {
            String reason = task.getException().getMessage();
            logAndGoToErrorActivity(context, previousActivity, "Failed to login: " + reason);
        }
    }

    public void signOutUser(Context context, Activity previousActivity) {
        _auth.signOut();
        _currentUser = null;
        previousActivity.finish();
        Intent i = new Intent(context, MainActivity.class);
        context.startActivity(i);
        Toast.makeText(context, "User logged out successfully", Toast.LENGTH_LONG).show();
    }

    /**
     *
     * @return the currentUser (From DB) which is logged in.
     */
    public User getCurrentUserFromDB() {
        if (_currentUser != null) {
            return _currentUser;
        }

        FirebaseUser firebaseUser = _auth.getCurrentUser();
        User user = null;
        for (int i = 0; i < _usersList.size() && firebaseUser != null; ++i) {
            if (_usersList.get(i).getId().equals(firebaseUser.getUid())) {
                user = _usersList.get(i);
                break;
            }
        }
        _currentUser = user;
        return user;
    }

    public void setUserOnDB(User user) {
        _usersDatabase.child(user.getId()).setValue(user);
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

    public void logAndGoToErrorActivity(Context context, Activity previousActivity, String errorMessage) {
        Intent i = new Intent(context, ErrorActivity.class);
        Bundle b = new Bundle();
        b.putString("exception", errorMessage);
        i.putExtras(b);
        context.startActivity(i);
        // todo write to errorlog
//        previousActivity.finish(); // Uncomment if can't return back after error
    }

}
