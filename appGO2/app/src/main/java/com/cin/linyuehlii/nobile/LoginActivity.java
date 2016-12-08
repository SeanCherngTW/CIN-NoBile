package com.cin.linyuehlii.nobile;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.ShareLinkContent;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * Created by Sean on 2016/8/29.
 */


public class LoginActivity extends Activity {

    CallbackManager callbackManager;
    ShareLinkContent linkContent;
    LoginButton loginButton;
    Button nextButton;
    private LinearLayout mLayout;
    Resources res;
    int clickCount = 0;
    //ShareButton shareButton;
    public static LoginActivity INSTANCE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        facebookSDKInitialize();
        INSTANCE = this;
        setContentView(R.layout.login_activity);
        res = this.getResources();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        nextButton = (Button) findViewById(R.id.nextButton);

        /*APP業師教育部計畫*/
        String url = "http://140.115.197.16/?school=ncu&app=im2016_nobile";
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        try{
            HttpResponse response = client.execute(request);
        }catch (Exception e){
            //Exception
        }
        /*APP業師教育部計畫*/

        if (isLoggedIn())
            afterLoggedIn();
        else {
            loginButton.setVisibility(View.INVISIBLE);
            mLayout = (LinearLayout) findViewById(R.id.linearLayout);
            mLayout.setBackground(res.getDrawable(R.mipmap.help1, getTheme()));
        }

        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (clickCount){
                    case 0:
                        mLayout.setBackground(res.getDrawable(R.mipmap.help2, getTheme()));
                        clickCount++;
                        break;
                    case 1:
                        mLayout.setBackground(res.getDrawable(R.mipmap.help3, getTheme()));
                        clickCount++;
                        break;
                    case 2:
                        mLayout.setBackground(res.getDrawable(R.mipmap.help4, getTheme()));
                        clickCount++;
                        break;
                    case 3:
                        mLayout.setBackground(res.getDrawable(R.mipmap.login, getTheme()));
                        nextButton.setVisibility(View.INVISIBLE);
                        loginButton.setVisibility(View.VISIBLE);
                        break;
                }

/*
                Log.d("background",String.valueOf(mLayout.getBackground()));
                if (mLayout.getBackground().equals("android.graphics.drawable.BitmapDrawable@3e069d5"))

                if (mLayout.getBackground().equals(res.getDrawable(R.mipmap.help2, getTheme())))

                if (mLayout.getBackground().equals(res.getDrawable(R.mipmap.help3, getTheme())))

                if (mLayout.getBackground().equals(res.getDrawable(R.mipmap.help4, getTheme()))){

                }*/

            }
        });

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                afterLoggedIn();
                // App code
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

    } // end onCreate()


    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    public void afterLoggedIn() {
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, StartActivity.class);
        startActivity(intent);
        finish();
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
