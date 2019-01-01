package com.jesta.gui.activities.login;
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
import com.jesta.R;
import com.jesta.utils.db.SysManager;

public class RegisterActivity extends LoginActivitiesWrapper {

    EditText e1,e2;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sysManager = new SysManager(this);

        setContentView(R.layout.activity_register);

        e1 = (EditText)findViewById(R.id.email);
        e2 = (EditText)findViewById(R.id.password);
        auth = FirebaseAuth.getInstance();
    }

    public void createUser(View v) {
        final String email = e1.getText().toString();
        final String password = e2.getText().toString();

        // todo-optional: do some email and password validation on client's side
        try {
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
                            // user created in firebase only
                            Toast.makeText(getApplicationContext(), "User created successfully", Toast.LENGTH_SHORT).show();
                            // TODO jesta_loading-animation here
                            loginWithCredentials(email, password);
                        }
                    });
        }
        catch (Exception e) {
            Intent i = new Intent(getApplicationContext(), ErrorActivity.class);
            Bundle b = new Bundle();
            b.putString("exception", e.getMessage());
            i.putExtras(b);
            startActivity(i);
            return;
        }

    }
}
