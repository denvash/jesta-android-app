package com.jesta.login;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.jesta.MainActivity;
import com.jesta.R;
import com.jesta.util.SysManager;

public class RegisterActivity extends AppCompatActivity {

    EditText e1,e2;
    FirebaseAuth auth;
    SysManager sysManager;

    @Override
    protected void onResume() {
        super.onResume();

        sysManager = new SysManager(this);
        sysManager.setTitle(getString(R.string.register));
        sysManager.showBackButton(true);

        // If the fireBaseUser is logged in, close this activity and go to profile
        if (sysManager.getCurrentUser() != null) {
            finish();
            Intent i = new Intent(this,LoginProfileActivity.class);
            startActivity(i);
            return;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        e1 = (EditText)findViewById(R.id.email);
        e2 = (EditText)findViewById(R.id.password);
        auth = FirebaseAuth.getInstance();
    }

    public void createUser(View v) {
        final String email = e1.getText().toString();
        final String password = e2.getText().toString();

        if (email.equals("") || password.equals("")) {
            Toast.makeText(getApplicationContext(), "Blank not allowed", Toast.LENGTH_SHORT).show();
        }
        else {
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                // user created in firebase only
                                Toast.makeText(getApplicationContext(), "User created successfully", Toast.LENGTH_SHORT).show();

                                // TODO loading-animation here
                                // sign in the user with firebase
                                sysManager.auth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                sysManager.afterLogin(task, getApplicationContext(), RegisterActivity.this);
                                                finish();
                                                // redirect to main activity, so we'll have the new user in db
                                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(i);
                                            }
                                        });


                            }
                            else {
                                String reason = task.getException().getMessage();
                                Toast.makeText(getApplicationContext(), "User couldn't be created: " + reason, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
}
