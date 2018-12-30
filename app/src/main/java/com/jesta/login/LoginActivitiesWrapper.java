package com.jesta.login;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;
import com.jesta.util.SysManager;

public class LoginActivitiesWrapper extends AppCompatActivity {
    public SysManager sysManager;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public class onTaskCompletion implements OnCompleteListener<AuthResult> {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (!task.isSuccessful()) {
                Intent i = new Intent(getApplicationContext(), ErrorActivity.class);
                Bundle b = new Bundle();
                b.putString("exception", task.getException().getMessage());
                i.putExtras(b);
                startActivity(i);
                return;
            }
            try {
                sysManager.signInUser(task, getApplicationContext(), LoginActivitiesWrapper.this);

                // redirect to main activity and clear activity stack
                Intent i = new Intent(getApplicationContext(), LoginMainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            } catch (Exception e) {
                Intent i = new Intent(getApplicationContext(), ErrorActivity.class);
                Bundle b = new Bundle();
                b.putString("exception", e.getMessage());
                i.putExtras(b);
                startActivity(i);
            }
        }
    }

    protected void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        sysManager.getFirebaseAuth().signInWithCredential(credential)
                .addOnCompleteListener(this, new onTaskCompletion());
    }

    protected void handleFacebookToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        sysManager.getFirebaseAuth().signInWithCredential(credential)
                .addOnCompleteListener(this, new onTaskCompletion());
    }

    protected void loginWithCredentials(String email, String password) {
        sysManager.getFirebaseAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new onTaskCompletion());
    }
}
