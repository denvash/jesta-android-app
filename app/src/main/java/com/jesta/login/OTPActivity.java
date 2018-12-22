package com.jesta.login;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.jesta.R;
import com.jesta.util.SysManager;

public class OTPActivity extends AppCompatActivity {
    SysManager sysManager;

    @Override
    protected void onResume() {
        super.onResume();
        sysManager.setTitle(getString(R.string.jesta_login_request_otp));
        sysManager.showBackButton(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        sysManager = new SysManager(this);

        // don't show keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        TextView displayNameTV = (TextView)findViewById(R.id.displayName);
//        String displayNameText = displayNameTV.getText().toString();
        displayNameTV.setText("Welcome, " + sysManager.getCurrentUserFromDB().getDisplayName() + "!\n\n" +
                "Please verify your phone number before start using our app:");


    }
}
