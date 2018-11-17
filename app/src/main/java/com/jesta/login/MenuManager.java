package com.jesta.login;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jesta.R;

public class MenuManager {

    TextView backButtonView;
    TextView pageNameTextView;

    Context context;


    public MenuManager(Context context, final Activity activity, View view, String pageName) {
        // TODO check input
        try {
            this.context = context;

//            LinearLayout layout = (LinearLayout)view;
            LinearLayout fatherLayout = (LinearLayout)view;



            pageNameTextView = (TextView)fatherLayout.getChildAt(1); // page name section
            backButtonView = (TextView)(((LinearLayout)fatherLayout.getChildAt(0)).getChildAt(0)); // TextView belongs to the back button

            pageNameTextView.setText(pageName);
            backButtonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.finish();
                }
            });
        }
        catch (Exception e) {
            // todo something notgood
        }
    }

    public void setPageName(String string) {
        pageNameTextView.setText(string);
    }

    private void setBackButton(String string) {
        backButtonView.setText(string);
    }

    public void hideBackButton() {
        setBackButton("");
    }
    void showBackButton() {
        setBackButton(context.getResources().getString(R.string.leftArrow));
    }

    public void goBack(Activity activity) {
        // todo check input
        activity.finish();
    }
}
