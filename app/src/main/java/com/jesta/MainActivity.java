package com.jesta;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.facebook.*;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.*;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;
import com.jesta.login.LoginActivity;
import com.jesta.path.PathActivity;
import com.jesta.util.SysManager;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    GoogleSignInClient mGoogleSignInClient;
    CallbackManager callbackManager;
    LoginButton loginButton;

    public SysManager sysManager;

    @Override
    protected void onResume() {
        super.onResume();
        sysManager = new SysManager(this);


        // wait for async dbTask
        Task<Void> allTask = Tasks.whenAll(sysManager.dbTask);
        allTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                DataSnapshot data = dbTask.getResult();

                // user is logged in
                if (sysManager.getCurrentUser() != null) {
                    //todo go to OTPActivity or check for OTP and go to Path
                    Intent i = new Intent(getApplicationContext(),PathActivity.class);
                    finish();
                    startActivity(i);
                    return;
                }

                // otherwise, render ui
                setContentView(R.layout.activity_main);
                sysManager.setTitle(getString(R.string.welcome));
                sysManager.showBackButton(false);

                Button facebookSignInButton = (Button)findViewById(R.id.facebook_sign_in_btn);
                Button googleSignInButton = (Button)findViewById(R.id.google_sign_in_btn);
                Button emailSignInButton = (Button)findViewById(R.id.email_sign_in_btn);

                // Facebook
                callbackManager = CallbackManager.Factory.create();

                LoginManager.getInstance().registerCallback(callbackManager,
                        new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                handleFacebookToken(loginResult.getAccessToken());
                            }

                            @Override
                            public void onCancel() {
                                Toast.makeText(MainActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onError(FacebookException exception) {
                                Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });


                facebookSignInButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile"));
                    }
                });


                // Google
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);


                googleSignInButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                        startActivityForResult(signInIntent, 101);
                    }
                });

                // Email
                emailSignInButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent signInIntent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(signInIntent);
                    }
                });

            }
        });
        allTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // todo: apologize profusely to the user!
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // TODO Google Sign In failed, update UI appropriately
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == 64206) { // facebook
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        sysManager.auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        sysManager.afterLogin(task, getApplicationContext(), MainActivity.this);
                    }
                });
    }

    private void handleFacebookToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        sysManager.auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        sysManager.afterLogin(task, getApplicationContext(), MainActivity.this);
                    }
                });
    }

}
