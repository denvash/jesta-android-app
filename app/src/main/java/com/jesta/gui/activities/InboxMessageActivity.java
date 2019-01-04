package com.jesta.gui.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.jesta.R;
import com.jesta.data.Mission;
import com.jesta.data.Relation;
import com.jesta.data.User;
import com.jesta.utils.db.SysManager;

import java.util.List;
import java.util.UUID;

import static com.jesta.data.ConstantsKt.RELATION_STATUS_INIT;

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


        setContentView(R.layout.activity_notification);

        Bundle b = getIntent().getExtras();

        if (b == null) {
            // todo error
        }

        final String body = b.getString("body");
        final String title = b.getString("statusTitle");
        final String sender = b.getString("sender");
        final String jestaId = b.getString("jesta");


        final SysManager sysManager = new SysManager();
        Activity activity = sysManager.getActivity();
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

        activity = this;
        // 1. Instantiate an <code><a href="/reference/android/app/AlertDialog.Builder.html">AlertDialog.Builder</a></code> with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(body).setTitle(title);


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
                            Toast.makeText(getBaseContext(), "A message was sent to " + senderWhichBecomesReceiver.getDisplayName(), Toast.LENGTH_LONG).show();

                            User userSender = sysManager.getCurrentUserFromDB();
                            User userReceiver = senderWhichBecomesReceiver;

                            TextView senderTextView = findViewById(R.id.jesta_notification_sender);
                            TextView receiverTextView = findViewById(R.id.jesta_notification_receiver);
                            senderTextView.setText(userSender.getDisplayName());
                            receiverTextView.setText(userReceiver.getDisplayName());
                        }
                    });
                }

            });
            builder.setNegativeButton("Deny", null);
        } else if (title.contains("answered to your request")) {
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    final User senderWhichBecomesReceiver = sysManager.getUserByID(sender);

                    User userSender = sysManager.getCurrentUserFromDB();
                    User userReceiver = senderWhichBecomesReceiver;

                    TextView senderTextView = findViewById(R.id.jesta_notification_sender);
                    TextView receiverTextView = findViewById(R.id.jesta_notification_receiver);
                    senderTextView.setText(userSender.getDisplayName());
                    receiverTextView.setText(userReceiver.getDisplayName());
                }

            });
        }


        try {
            // 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } catch (Exception e) {
            throw (e);
        }

    }
}
