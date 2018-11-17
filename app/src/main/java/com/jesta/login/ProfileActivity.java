package com.jesta.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.jesta.R;

public class ProfileActivity extends AppCompatActivity {
    TextView email;
    TextView displayName;

    SysManager sysManager;

    @Override
    protected void onResume() {
        super.onResume();
        sysManager.setMenuManager(new MenuManager(getApplicationContext(), this, findViewById(R.id.logo_bar), getString(R.string.profile)));
        sysManager.getMenuManager().hideBackButton();





        // If the fireBaseUser isn't logged in, close this activity and do error
        if (sysManager.getCurrentFireBaseUser() == null) {
            sysManager.logAndGoToErrorActivity(getApplicationContext(), this, "User profile isn't ready");
            finish();
            return;
        }

        // don't show keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        String uid = sysManager.fireBaseUser.getUid();
        String phoneNumber = sysManager.fireBaseUser.getPhoneNumber();
        Uri photoUrl = sysManager.fireBaseUser.getPhotoUrl();

        email = (TextView)findViewById(R.id.email);
        email.setText(sysManager.fireBaseUser.getEmail());

        displayName = (TextView)findViewById(R.id.displayName);
        displayName.setText(sysManager.fireBaseUser.getDisplayName());


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Important: create here a new sysManager so we won't be depended on MainActivity as super class
        sysManager = new SysManager();

        User user = sysManager.getCurrentUser();
        if(user == null || user.getPhone() == null) {
            Intent i = new Intent(this,OTPActivity.class);
//            finish();
            startActivity(i);
            return;
        }


    }

    public void signOut(View v) {
        sysManager.auth.signOut();
        sysManager.afterLogout(getApplicationContext(), ProfileActivity.this);

    }
}
