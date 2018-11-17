package com.jesta.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.jesta.MainActivity;
import com.jesta.R;

public class RegisterActivity extends MainActivity {

    EditText e1,e2;
    FirebaseAuth auth;

    @Override
    protected void onResume() {
        super.onResume();
        sysManager.getMenuManager().showBackButton();
        sysManager.getMenuManager().setPageName(getString(R.string.register));

        // If the fireBaseUser is logged in, close this activity and go to profile
        if (sysManager.fireBaseUser != null) {
            finish();
            Intent i = new Intent(this,ProfileActivity.class);
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
        String email = e1.getText().toString();
        String password = e2.getText().toString();

        if (email.equals("") || password.equals("")) {
            Toast.makeText(getApplicationContext(), "Blank not allowed", Toast.LENGTH_SHORT).show();
        }
        else {
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "User created successfully", Toast.LENGTH_SHORT).show();
                                finish();
                                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                                startActivity(i);
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
