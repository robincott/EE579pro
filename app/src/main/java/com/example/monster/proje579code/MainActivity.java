package com.example.monster.proje579code;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.GraphResponse;
import com.facebook.GraphRequest;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "blApp";
    private final int REQUEST_ENABLE_BT = 1;
    public static BluetoothAdapter mBluetoothAdapter;
    private static final String EMAIL = "email";
    private static int flag = 0;
    AccessTokenTracker accessTokenTracker;
    CallbackManager callbackManager;
    LoginButton loginButton;
    TextView FacebookDataTextView;
    ImageView imageViewprofile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(MainActivity.this);
        FacebookSdk.setIsDebugEnabled(true);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        FacebookDataTextView = (TextView)findViewById(R.id.TextView1);
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        final Button fb_bind_button = findViewById(R.id.fb_bind);
        imageViewprofile=findViewById(R.id.imageview);
        imageViewprofile.setImageDrawable(null);
        FacebookDataTextView.setText("");
        loginButton.setReadPermissions(Arrays.asList(EMAIL,"user_status","public_profile","user_friends"));
        if(AccessToken.getCurrentAccessToken()!=null){
            GraphLoginRequest(AccessToken.getCurrentAccessToken());
            // If already login in then show the Toast.
            fb_bind_button.setText("Log Out");
            Toast.makeText(MainActivity.this,"Already logged in",Toast.LENGTH_SHORT).show();
        }else {
            // If not login in then show the Toast.
            fb_bind_button.setText("Log In");
            Toast.makeText(MainActivity.this,"User not logged in",Toast.LENGTH_SHORT).show();

        }
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.e("DD","**************************");
                Log.e("DD",loginResult.getAccessToken().toString());
                Log.e("DD",AccessToken.getCurrentAccessToken().toString());
                GraphLoginRequest(loginResult.getAccessToken());
                Log.e("DD","**********************___********");
                GraphFriendRequest(loginResult.getAccessToken());
                Log.e("DD:",loginResult.toString());
                //getBluetoothMacAddress();
                fb_bind_button.setText("Log Out");
            }


            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                exception.printStackTrace();
                // App code
            }
        });
        // Detect user is login or not. If logout then clear the TextView and delete all the user info from TextView.
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken accessToken, AccessToken accessToken2) {
                if (accessToken2 == null){

                    // Clear the TextView after logout.
                    fb_bind_button.setText("Log In");
                    imageViewprofile.setImageDrawable(null);
                    FacebookDataTextView.setText("");

                }
            }
        };


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtlntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtlntent, REQUEST_ENABLE_BT);
        }


    }

    // Method to access Facebook User Data.
    protected void GraphLoginRequest(AccessToken accessToken){
        GraphRequest graphRequest = GraphRequest.newMeRequest(accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {

                        try {
                            Log.e("DD","trying ------------------------");
                            Log.e("DD",graphResponse.toString());
                            Log.e("DD",jsonObject.getString("id"));
                            Log.e("DD", jsonObject.getString("first_name"));
                            Log.e("DD", jsonObject.toString());

                            // Adding all user info one by one into TextView.
                            FacebookDataTextView.setText("ID: " + jsonObject.getString("id"));
                            Picasso.with(MainActivity.this).load("https://graph.facebook.com/" + jsonObject.getString("id")+ "/picture?type=large").into(imageViewprofile);
                            //Picasso.with(MainActivity.this).load("https://graph.facebook.com/" + jsonObject.getString("id")+ "/picture?type=large").into();

                            FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nName : " + jsonObject.getString("name"));

                            FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nFirst name : " + jsonObject.getString("first_name"));

                            FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nLast name : " + jsonObject.getString("last_name"));

                            //FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nEmail : " + jsonObject.getString("email"));

                            FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nGender : " + jsonObject.getString("gender"));

                            FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nLink : " + jsonObject.getString("link"));

                            FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nTime zone : " + jsonObject.getString("timezone"));

                            FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nLocale : " + jsonObject.getString("locale"));

                            FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nUpdated time : " + jsonObject.getString("updated_time"));

                            FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nVerified : " + jsonObject.getString("verified"));
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle bundle = new Bundle();
        bundle.putString(
                "fields",
                "id,name,link,email,gender,last_name,first_name,locale,timezone,updated_time,verified"
        );
        graphRequest.setParameters(bundle);
        graphRequest.executeAsync();

    }





    protected void GraphFriendRequest(AccessToken accessToken){

        GraphRequest request = GraphRequest.newGraphPathRequest(
                accessToken,
                "/10212102718065430/friends",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        Log.e("DD","tyring_+_+_+_+_+_+_+_+");
                        Log.e("DD", response.toString());
                    }
                });


        Bundle bundle = new Bundle();
        bundle.putString(
                "fields",
                "data"
        );
        request.setParameters(bundle);
        request.executeAsync();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        // unregisterReceiver(mReceiver);
    }

    //setting login, logout button and AlertDialog
    public void judge(View view) {
        if (flag == 0){
            showdialog();
        }
        else{
            com.facebook.login.widget.LoginButton btn = new com.facebook.login.widget.LoginButton(MainActivity.this);
            btn.performClick();
        }
    }

    public void showdialog() {
            AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(this);
            alertdialogbuilder.setMessage("This app wants to access your Facebook account.");
            alertdialogbuilder.setPositiveButton("ALLOW", click1);
            alertdialogbuilder.setNegativeButton("DENY", click2);
            AlertDialog alertDialog1 = alertdialogbuilder.create();
            alertDialog1.show();
    }

    private DialogInterface.OnClickListener click1 = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            com.facebook.login.widget.LoginButton btn = new com.facebook.login.widget.LoginButton(MainActivity.this);
            btn.performClick();
            flag = 1;
        }
    };

    private DialogInterface.OnClickListener click2 = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            arg0.cancel();
        }
    };


}

