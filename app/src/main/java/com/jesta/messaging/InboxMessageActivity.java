package com.jesta.messaging;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.jesta.R;
import com.jesta.chat.ChatActivity;
import com.jesta.doJesta.DoJestaActivity;
import com.jesta.login.ErrorActivity;
import com.jesta.login.LoginActivity;
import com.jesta.login.MainActivity;
import com.jesta.util.Mission;
import com.jesta.util.SysManager;
import com.jesta.util.User;

import java.util.Arrays;
import java.util.List;

public class InboxMessageActivity extends AppCompatActivity {
//    @Override
//    protected void onResume() {
//        super.onResume();
//        SysManager sysManager = new SysManager(this);
//        sysManager.setTitle(getString(R.string.jesta_inbox_message));
//        sysManager.showBackButton(true);
//
//
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        setContentView(R.layout.activity_error);

        Bundle b = getIntent().getExtras();

        if (b == null) {
            // todo error
        }

        final String body = b.getString("body");
        final String title = b.getString("title");
        final String sender = b.getString("sender");
        final String jestaId = b.getString("jesta");


        final SysManager sysManager = new SysManager();
        Activity activity = sysManager.getActivity();
//        activity = null;
        if (activity == null) {
//            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_error);
            TextView errMsgTV = findViewById(R.id.errorMessage);
            errMsgTV.setText(title + "\n" + body);
            return;
        }


        final Mission mission = sysManager.getMissionByID(jestaId);
        if (mission == null) {
            return;
        }

        if (activity == null) {
            System.out.println("SHITTTTTT");
        }

        activity = this;
        // 1. Instantiate an <code><a href="/reference/android/app/AlertDialog.Builder.html">AlertDialog.Builder</a></code> with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(body)
                .setTitle(title);

        if (title.contains("asked to do a jesta")) {
            builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    final User senderWhichBecomesReceiver = sysManager.getUserByID(sender);
                    sysManager.answerTodoJestaForUser(senderWhichBecomesReceiver, mission).addOnCompleteListener(new OnCompleteListener<List<User>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<User>> task) {
                            if (!task.isSuccessful()) {
                                // todo some error
                                return;
                            }
                            Toast.makeText(getBaseContext(),"A message was sent to " + senderWhichBecomesReceiver.getDisplayName(), Toast.LENGTH_LONG).show();

                            // currentUser asked to do a jesta TODO DENNIS
//                            User userSender = sysManager.getCurrentUserFromDB();
//                            User userReceiver = senderWhichBecomesReceiver;

//                            finish();
//                            Intent i = new Intent(getBaseContext(), ChatActivity.class);
//                            startActivity(i);

                        }
                    });
                }

            });
            builder.setNegativeButton("Deny", null);
        }
        else if (title.contains("answered to your request")) {
            builder.setPositiveButton("OK", null);
        }


        try {
            // 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
//            finish();
        }
        catch(Exception e) {
            throw(e);
        }

//
//        TextView errMsgTV = findViewById(R.id.errorMessage);
//        errMsgTV.setText(body);

    }
}
