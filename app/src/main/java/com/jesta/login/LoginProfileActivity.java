package com.jesta.login;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.jesta.util.SysManager;
import com.jesta.R;
import com.jesta.util.User;

public class LoginProfileActivity extends AppCompatActivity {
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

        User currentUser = sysManager.getCurrentUserFromDB();
        String uid = currentUser.getId();
        String phoneNumber = currentUser.getPhoneNumber();
        Uri photoUrl = currentUser.getPhotoUrl();

        email = (TextView)findViewById(R.id.email);
        email.setText(currentUser.getEmail());

        displayName = (TextView)findViewById(R.id.displayName);
        displayName.setText(currentUser.getDisplayName());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }

    public void signOut(View v) {
        sysManager.signOutUser(getApplicationContext());
    }
}
