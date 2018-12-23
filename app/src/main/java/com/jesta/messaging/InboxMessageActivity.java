package com.jesta.messaging;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.jesta.R;
import com.jesta.util.SysManager;

public class InboxMessageActivity extends AppCompatActivity {
    @Override
    protected void onResume() {
        super.onResume();
        SysManager sysManager = new SysManager(this);
        sysManager.setTitle(getString(R.string.jesta_inbox_message));
        sysManager.showBackButton(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        Bundle b = getIntent().getExtras();

        String body;
        if (b != null) {
            body = b.getString("title");
        }
        else {
            body = "Error: Oops..";
        }

        TextView errMsgTV = findViewById(R.id.errorMessage);
        errMsgTV.setText(body);

    }
}
