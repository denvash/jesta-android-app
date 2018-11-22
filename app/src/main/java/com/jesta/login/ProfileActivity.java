package com.jesta.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.jesta.pathChoose.PathActivity;
import com.jesta.R;

public class ProfileActivity extends AppCompatActivity {
    TextView email;
    TextView displayName;

    SysManager sysManager;

    @Override
    protected void onResume() {
        super.onResume();
        sysManager = new SysManager(this);
        sysManager.setTitle(getString(R.string.profile));
        sysManager.showBackButton(false);
        sysManager.showKeyboardAutomatically(false);

        // don't show keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        String uid = sysManager.getCurrentUser().getId();
        String phoneNumber = sysManager.getCurrentUser().getPhoneNumber();
        Uri photoUrl = sysManager.getCurrentUser().getPhotoUrl();

        email = (TextView)findViewById(R.id.email);
        email.setText(sysManager.getCurrentUser().getEmail());

        displayName = (TextView)findViewById(R.id.displayName);
        displayName.setText(sysManager.getCurrentUser().getDisplayName());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }

    public void signOut(View v) {
        sysManager.signOutUser(getApplicationContext(), ProfileActivity.this);
    }
}
