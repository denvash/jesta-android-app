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
import com.jesta.MainActivity;
import com.jesta.R;
import com.jesta.util.SysManager;

public class LoginActivity extends LoginActivitiesWrapper {

    @Override
    protected void onResume()
    {
        super.onResume();
        sysManager = new SysManager(this);
        sysManager.setTitle(getString(R.string.login));
        sysManager.showBackButton(true);
        sysManager.showKeyboardAutomatically(false);

//        // If the fireBaseUser is logged in, close this activity and go to profile
//        if (sysManager.getCurrentFireBaseUser() != null) {
//            finish();
//            Intent i = new Intent(this,SettingsActivity.class);
//            startActivity(i);
//            return;
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }


    public void openRegister(View v) {
        Intent i = new Intent(this,RegisterActivity.class);
        startActivity(i);
    }

    public void loginUser(View v) {
        EditText e1 = (EditText)findViewById(R.id.email);
        EditText e2 = (EditText)findViewById(R.id.password);

        String email = e1.getText().toString();
        String password = e2.getText().toString();

        if (email.equals("") || password.equals("")) {
            Toast.makeText(getApplicationContext(), "Blank not allowed", Toast.LENGTH_SHORT).show();
        }
        else {
            loginWithCredentials(email, password);
        }
    }
}
