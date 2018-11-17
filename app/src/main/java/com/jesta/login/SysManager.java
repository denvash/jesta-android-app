package com.jesta.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.jesta.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * System manager
 */
public class SysManager {

    // authentication
    public FirebaseAuth auth;
    public FirebaseUser fireBaseUser;
    GoogleSignInClient mGoogleSignInClient;

    public DatabaseReference usersDatabase;

    List<User> usersList;
    User currentUser;

    public MenuManager menuManager;

    // database
    public SysManager() {
        auth = FirebaseAuth.getInstance();
        usersList = new ArrayList<>();
        fireBaseUser = auth.getCurrentUser();
        usersDatabase = FirebaseDatabase.getInstance().getReference("users");

        // todo firebase realtime db - change permission from public to private (only for app)
        // listen on data changes and updates the userList
        usersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for(DataSnapshot usersSnapShot : dataSnapshot.getChildren()) {
                    User user = usersSnapShot.getValue(User.class);
                    usersList.add(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // todo something
            }


        });


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

                // user exists
                if (user != null) {
                    currentUser = user;
                    Intent i = new Intent(context, ProfileActivity.class);
                    context.startActivity(i);
                    return;
                }

                // create new user an store in DB
                User newUser = new User(firebaseUser.getUid(), firebaseUser.getEmail());
                if (firebaseUser.getDisplayName() != null) {
                    newUser.setDisplayName(firebaseUser.getDisplayName());
                }
                if (firebaseUser.getPhotoUrl() != null) {
                    newUser.setPhotoUrl(firebaseUser.getPhotoUrl().toString());
                }
                currentUser = newUser;
                usersDatabase.child(newUser.getId()).setValue(newUser);

            } catch (Exception e) {
                logAndGoToErrorActivity(context, previousActivity, e.getMessage());
                return;
            }

            Intent i = new Intent(context, ProfileActivity.class);
//            Intent i = new Intent(context, OTPActivity.class);
            context.startActivity(i);

        } else {
            String reason = task.getException().getMessage();
            logAndGoToErrorActivity(context, previousActivity, "Failed to login: " + reason);
        }
    }

    public FirebaseUser getCurrentFireBaseUser() {
        return auth.getCurrentUser();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
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

    public void afterLogout(Context context, Activity previousActivity) {
        previousActivity.finish();
        Intent i = new Intent(context, MainActivity.class);
        context.startActivity(i);
        Toast.makeText(context, "User logged out successfully", Toast.LENGTH_LONG).show();
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    public void setMenuManager(MenuManager menuManager) {
        this.menuManager = menuManager;
    }
}
