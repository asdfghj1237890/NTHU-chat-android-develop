package com.ilmsplus.demo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import android.widget.Toast;
import android.graphics.Typeface;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.loopj.android.http.*;
import cz.msebera.android.httpclient.*;
import cz.msebera.android.httpclient.cookie.Cookie;
import android.os.Handler;
import com.unstoppable.submitbuttonview.SubmitButton;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    //private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mLoginFormView;
    //private VideoView mVideoView;
    private TextView mTextView;
    private final String TAG = "LoginActivity";
    private SubmitButton mSubmitView;
    private SubmitButton.OnResultEndListener listener;

    public FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Toast.makeText(LoginActivity.this, "Login Access, cookie=" + getCookieText(), Toast.LENGTH_SHORT).show();
        if(!TextUtils.isEmpty(getCookieText())){
            Toast.makeText(LoginActivity.this,"Using Your Cookie",Toast.LENGTH_LONG).show();
            finish();
            Intent myIntent = new Intent(LoginActivity.this,NavigationActivity.class);
            LoginActivity.this.startActivity(myIntent);
        }
        setContentView(R.layout.activity_login);
        mTextView = (TextView)findViewById(R.id.textView);
        Typeface otfFace_chosence = Typeface.createFromAsset(getAssets(), "fonts/Chosence.otf");
        mTextView.setTypeface(otfFace_chosence);
        //mVideoView = (VideoView) findViewById(R.id.videoView);
        //Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.bg);
        //mVideoView.setVideoURI(uri);
        //mVideoView.start();
        /*mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
            @Override
            public void onPrepared(MediaPlayer mediaPlayer){
                mediaPlayer.setLooping(true);
            }
        });*/
        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mEmailView.setHintTextColor(Color.WHITE);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setHintTextColor(Color.WHITE);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        /*Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });*/

        mLoginFormView = findViewById(R.id.login_form);

        mSubmitView = (SubmitButton) findViewById(R.id.submitbutton);
        mSubmitView.reset();
        mSubmitView.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view){
                attemptLogin();
            }

        });
        mSubmitView.setVisibility(View.VISIBLE);
        mSubmitView.setOnResultEndListener(listener);

        //Initial Firebase auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart(){
        super.onStart();
        //Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    private void createAccount(final String email, final String password) {
        Log.d(TAG, "createAccount:" + email);

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Create AC Success",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            signIn(email, password);
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                    }
                });
        // [END create_user_with_email]
    }


    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Login Firebase Success.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
        // [END sign_in_with_email]
    }

    private void signOut() {
        mAuth.signOut();
        //updateUI(null);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        /*if (mAuthTask != null) {
            return;
        }*/

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mSubmitView.setProgress(50);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
            mSubmitView.doResult(false);
            new Handler().postDelayed(new Runnable() {
                public void run(){
                    mSubmitView.reset();
                }
            },2000);
        }
        if(TextUtils.isEmpty(password)){
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
            mSubmitView.doResult(false);
            new Handler().postDelayed(new Runnable() {
                public void run(){
                    mSubmitView.reset();
                }
            },2000);
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
            mSubmitView.doResult(false);
            new Handler().postDelayed(new Runnable() {
                public void run(){
                    mSubmitView.reset();
                }
            },2000);
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
            mSubmitView.doResult(false);
            new Handler().postDelayed(new Runnable() {
                public void run(){
                    mSubmitView.reset();
                }
            },2000);
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);
            //mAuthTask = new UserLoginTask(email, password);
            //mAuthTask.execute();
            login(email,password);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("1");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }



    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public void login(String email,String passwd){
        RequestParams params=new RequestParams();
        FinalAsyncHttpClient finalAsyncHttpClient = new FinalAsyncHttpClient();
        AsyncHttpClient client = finalAsyncHttpClient.getAsyncHttpClient();
        CookieUtils.saveCookie(client,this);
        PersistentCookieStore myCookieStore = new PersistentCookieStore(LoginActivity.this);
        client.setCookieStore(myCookieStore);
        params.put("account",email);
        params.put("password",passwd);
        params.put("secCode","na");
        params.put("stay", "0");
        client.post("http://lms.nthu.edu.tw/sys/lib/ajax/login_submit.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result=new String(responseBody);
                if (result!=null){
                    //Toast.makeText(LoginActivity.this,result,Toast.LENGTH_LONG).show();
                    String test = result.substring(18,22);
                    String fire_email = "";
                    String fire_passwd = "";
                    if (test.equals("true")){
                        try{
                            JSONObject jsonObj = new JSONObject(result);
                            fire_email = jsonObj.getJSONObject("ret").getString("email");
                            fire_passwd = jsonObj.getJSONObject("ret").getString("name")+"_ilmschat";
                            //Toast.makeText(LoginActivity.this,jsonObj.getString("ret"), Toast.LENGTH_SHORT).show();
                            //Toast.makeText(LoginActivity.this,jsonObj.getJSONObject("ret").getString("email"), Toast.LENGTH_SHORT).show();
                            //Log.d(TAG, jsonObj.getString("email"));
                            //Log.d(TAG, jsonObj.getString("status"));
                                createAccount(fire_email, fire_passwd);
                        }catch (Exception e){
                            Log.d(TAG,"Json login Firebase Fail");
                        }
                        //Toast.makeText(LoginActivity.this, "Login Access, cookie=" + getCookieText(), Toast.LENGTH_SHORT).show();
                        CookieUtils.setCookies(CookieUtils.getCookie(LoginActivity.this));
                        if(mAuth != null) {
                            finish();
                            Intent myIntent = new Intent(LoginActivity.this, NavigationActivity.class);
                            LoginActivity.this.startActivity(myIntent);
                        }
                    } else {
                        mPasswordView.setError(getString(R.string.error_incorrect_password));
                        mPasswordView.requestFocus();
                        mSubmitView.doResult(false);
                        new Handler().postDelayed(new Runnable() {
                            public void run(){
                                mSubmitView.reset();
                            }
                        },2000);
                    }
                }
                else {
                    Toast.makeText(LoginActivity.this,"Access Fail",Toast.LENGTH_LONG).show();
                    mSubmitView.doResult(false);
                    new Handler().postDelayed(new Runnable() {
                        public void run(){
                            mSubmitView.reset();
                        }
                    },2000);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }
    private String getCookieText() {
        PersistentCookieStore myCookieStore = new PersistentCookieStore(LoginActivity.this);
        List<Cookie> cookies = myCookieStore.getCookies();
        Log.d(TAG, "cookies.size() = " + cookies.size());
        CookieUtils.setCookies(cookies);
        for (Cookie cookie : cookies) {
            Log.d(TAG, cookie.getName() + " = " + cookie.getValue());
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < cookies.size(); i++) {
            Cookie cookie = cookies.get(i);
            String cookieName = cookie.getName();
            String cookieValue = cookie.getValue();
            if (!TextUtils.isEmpty(cookieName)
                    && !TextUtils.isEmpty(cookieValue)) {
                sb.append(cookieName + "=");
                sb.append(cookieValue + ";");
            }
        }
        Log.e("cookie", sb.toString());
        return sb.toString();
    }


}

