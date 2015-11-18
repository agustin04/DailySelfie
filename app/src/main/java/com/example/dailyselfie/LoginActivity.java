package com.example.dailyselfie;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
// Add this to the header of your file:
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class LoginActivity extends Activity {
    LoginButton loginButton;
    Button loginGuest;
    CallbackManager callbackManager;
    public static final int LOGIN_TYPE_FACEBOOK = 0;
    public static final int LOGIN_TYPE_GUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login);

        if(isLoggedIn()) {
            startDailySelfie(getFacebookLoggedUserId(), LOGIN_TYPE_FACEBOOK);
            return;
        }

        loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");

        loginGuest = (Button) findViewById(R.id.login_guest);

        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String userId = loginResult.getAccessToken().getUserId();
                startDailySelfie(userId, LOGIN_TYPE_FACEBOOK);
            }

            @Override
            public void onCancel() {
                // App code
                setResult(Activity.RESULT_CANCELED);
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

        loginGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = "guest";
                startDailySelfie(userId, LOGIN_TYPE_GUEST);
            }
        });
    }

    public static final String EXTRA_USER_ID = "user_id";
    public static final String EXTRA_LOGIN_TYPE = "login_type";

    private void startDailySelfie(String userId, int type) {
        Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
        loginIntent.putExtra(EXTRA_USER_ID, userId);
        loginIntent.putExtra(EXTRA_LOGIN_TYPE, type);
        startActivity(loginIntent);
        finish();
    }
    public static boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    public static String getFacebookLoggedUserId() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null )
            return accessToken.getUserId();
        else
            return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
