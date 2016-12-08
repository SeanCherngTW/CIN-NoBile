package com.cin.linyuehlii.nobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;

/**
 * Created by Sean on 2016/8/29.
 */
public class PostFBActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    ShareLinkContent linkContent;
    //LoginButton loginButton;
    ShareButton shareButton;
    public static PostFBActivity INSTANCE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        facebookSDKInitialize();


        SharedPreferences ScorePref = getSharedPreferences("totalScore", MODE_PRIVATE);
        SharedPreferences.Editor editor = ScorePref.edit();
        int currentScore = ScorePref.getInt("KEY_INT", 0);
        currentScore = currentScore + 10;
        editor.putInt("KEY_INT", currentScore);
        editor.commit();


        INSTANCE = this;
        setContentView(R.layout.postfb_activity);
        createLinkContent();

        Intent intent = getIntent();
        String AppDescription = intent.getStringExtra("AppDescription");

        linkContent = new ShareLinkContent.Builder()
                .setContentTitle("【手機戒癮】- 好友的時間不能等")
                .setContentDescription(AppDescription)
                .setContentUrl(Uri.parse("https://www.facebook.com/nobileApp/?fref=ts"))
                .build();

        //loginButton = (LoginButton) findViewById(R.id.login_button);
        shareButton = (ShareButton) findViewById(R.id.share_button);
        shareButton.setShareContent(linkContent);

        shareButton.performClick();
        finish();

    } // end onCreate()

    public void createLinkContent() {


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    protected void facebookSDKInitialize() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }
}