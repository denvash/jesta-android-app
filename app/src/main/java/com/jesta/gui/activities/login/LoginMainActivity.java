package com.jesta.gui.activities.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
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
import com.jesta.gui.activities.MainActivity;
import com.jesta.R;
import com.jesta.utils.db.SysManager;
import com.jesta.data.User;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.jesta.data.ConstantsKt.USER_EMPTY_PHOTO;
import static com.jesta.data.ConstantsKt.getAvatarDict;
import static com.jesta.utils.db.SysManager.DBTask.*;

public class LoginMainActivity extends LoginActivitiesWrapper {
    GoogleSignInClient mGoogleSignInClient;
    CallbackManager fbCallbackManager;
    LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sysManager = new SysManager(this);
        sysManager.startLoadingAnim();


        // wait for async dbTask
        // RELOAD_USERS will reload the userList in sysManager
        // and update the currentUser (if logged in)
        Task<List<User>> getAllUsers = sysManager.createDBTask(RELOAD_USERS);

        getAllUsers.addOnCompleteListener(new OnCompleteListener<List<User>>() {
            @Override
            public void onComplete(@NonNull Task<List<User>> task) {
                sysManager.stopLoadingAnim();

                if (!task.isSuccessful()) {
                    // todo some error
                    return;
                }

                // user is logged in
                User currentUser = sysManager.getCurrentUserFromDB();
                if (currentUser != null) {
                    Random rand = new Random();
                    int avatar = rand.nextInt(16);
                    currentUser.setPhotoUrl(Objects.requireNonNull(getAvatarDict().get(avatar)));
                    sysManager.setUserOnDB(currentUser);

                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    return;
                }

                // otherwise (user isn't logged in), render ui
                setContentView(R.layout.activity_login_main);
                sysManager.setTitle(getString(R.string.jesta_login_welcome));
                sysManager.showBackButton(false);

                Button facebookSignInButton = (Button) findViewById(R.id.facebook_sign_in_btn);

                Button googleSignInButton = (Button) findViewById(R.id.google_sign_in_btn);
                Button emailSignInButton = (Button) findViewById(R.id.email_sign_in_btn);

                // Facebook
                fbCallbackManager = CallbackManager.Factory.create();
                LoginManager.getInstance().registerCallback(fbCallbackManager,
                        new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                handleFacebookToken(loginResult.getAccessToken());
                            }

                            @Override
                            public void onCancel() {
                                // todo
                                // there is nothing todo here really...
//                                Toast.makeText(LoginMainActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onError(FacebookException exception) {
                                Intent i = new Intent(getApplicationContext(), ErrorActivity.class);
                                Bundle b = new Bundle();
                                b.putString("exception", exception.getMessage());
                                i.putExtras(b);
                                startActivity(i);
                                return;
                            }
                        });
                facebookSignInButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LoginManager.getInstance().logInWithReadPermissions(LoginMainActivity.this, Arrays.asList("email", "public_profile"));
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
                Toast.makeText(LoginMainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == 64206) { // facebook
            fbCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
}
