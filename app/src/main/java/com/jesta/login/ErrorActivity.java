package com.jesta.login;

import android.os.Bundle;
import android.widget.TextView;
import com.jesta.MainActivity;
import com.jesta.R;

public class ErrorActivity extends MainActivity {
    @Override
    protected void onResume() {
        super.onResume();
        sysManager.getMenuManager().showBackButton();
        sysManager.getMenuManager().setPageName(getString(R.string.error));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        Bundle b = getIntent().getExtras();

        String errorMessage;
        if (b != null) {
            errorMessage = b.getString("exception");
        }
        else {
            errorMessage = "Oops..";
        }

        TextView errMsgTV = findViewById(R.id.errorMessage);
        errMsgTV.setText(errorMessage);

    }
}
