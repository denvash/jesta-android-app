package com.jesta.login;
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
import com.jesta.pathChoose.PathActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * System manager
 */
public class SysManager {

    // authentication
    public FirebaseAuth auth; // used only by MainActivity
    private static FirebaseUser fireBaseUser;
    public DatabaseReference usersDatabase;
    private static List<User> usersList;
    private static DataSnapshot usersListDataSnapshot;
    private static User currentUser;
    private static TaskCompletionSource<DataSnapshot> dbSource = new TaskCompletionSource<>();
    public Task dbTask = dbSource.getTask();

    // layout and activity
    private Activity activity;
    private TextView backButtonTv;

    public SysManager(Activity currentActivity) {
        activity = currentActivity;
        auth = FirebaseAuth.getInstance();
        usersList = new ArrayList<>();
        fireBaseUser = auth.getCurrentUser();
        usersDatabase = FirebaseDatabase.getInstance().getReference("users");

        // Get current user from DB
        usersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (usersListDataSnapshot != null) {
                    return;
                }
                usersListDataSnapshot = dataSnapshot;
                for (DataSnapshot ds : usersListDataSnapshot.getChildren()) {
                    HashMap dbUser = (HashMap)ds.getValue();

                    // todo create a constructor for User
                    User user = new User((String)dbUser.get("id"), (String)dbUser.get("email"));
                    usersList.add(user);

                    if (fireBaseUser != null && ds.getKey().equals(fireBaseUser.getUid())) {
                        currentUser = user;
                    }
                }
                dbSource.setResult(usersListDataSnapshot);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                dbSource.setException(databaseError.toException());
            }
        });

        // Set listener if back button is available
        backButtonTv = (TextView)activity.findViewById(R.id.back_button);
        if (backButtonTv != null) {
            backButtonTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.finish();
                }
            });
        }


        // todo firebase realtime db - change permission from public to private (only for app)
    }

    /**
     * User authentication
     * TODO-MAX: implement updateUserInDB etc'...
     */

    private User userExistsInDB(FirebaseUser firebaseUser) {
        User user = null;
        for (int i = 0; i < usersList.size(); ++i) {
            if (usersList.get(i).getId().equals(firebaseUser.getUid())) {
                user = usersList.get(i);
                break;
            }
        }
        return user;
    }

    public void afterLogin(@NonNull Task<AuthResult> task, Context context, Activity previousActivity) {
        if (task.isSuccessful()) {
            try {
                // Sign in success, update UI with the signed-in fireBaseUser's information
                FirebaseUser firebaseUser = auth.getCurrentUser();

                Toast.makeText(context, "User logged in successfully", Toast.LENGTH_SHORT).show();

                // Note: after we do finish() there is no activities waiting to pop
                // Therefore we should end the next piece of code with startActivity!
                previousActivity.finish();

                User user = userExistsInDB(firebaseUser);
                if (user != null) {// user exists in db

                }
                else {// create new user and store in DB
                    user = new User(firebaseUser.getUid(), firebaseUser.getEmail());
                    if (firebaseUser.getDisplayName() != null) {
                        user.setDisplayName(firebaseUser.getDisplayName());
                    }
                    if (firebaseUser.getPhotoUrl() != null) {
//                        user.setPhotoUrl(firebaseUser.getPhotoUrl().toString());
                    }
                    usersDatabase.child(user.getId()).setValue(user);
                }

                currentUser = user;
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
        auth.signOut();
        currentUser = null;
        previousActivity.finish();
        Intent i = new Intent(context, MainActivity.class);
        context.startActivity(i);
        Toast.makeText(context, "User logged out successfully", Toast.LENGTH_LONG).show();
    }

    public User getCurrentUser() {
        return currentUser;
    }


    /**
     * Layout, UI and system settings and logs
     */

    public void setTitle(String title) {
        TextView pageNameTv = (TextView)activity.findViewById(R.id.page_name);
        pageNameTv.setText(title);
    }

    public void showBackButton(Boolean flag) {
        backButtonTv = (TextView)activity.findViewById(R.id.back_button);
        if (backButtonTv == null)
            return;
        backButtonTv.setText(flag ? activity.getResources().getString(R.string.leftArrow) : "");
    }

    public void showKeyboardAutomatically(Boolean flag) {

        if (flag) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        else {// don't show keyboard
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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
