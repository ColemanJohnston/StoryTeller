package org.dreamitcodeit.storyteller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginManager;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.dreamitcodeit.storyteller.models.User;
import org.json.JSONException;
import org.json.JSONObject;

public class WelcomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";

    private EditText mEmailField;
    private EditText mPasswordField;
    private Button signUpButton;
    private Button signInButton;
    private Button Facebook_sign_in;
    String fbName;
    String fbUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_welcome);

        mAuth = FirebaseAuth.getInstance();

        // Get references to our EditTexts and buttons
        mEmailField = (EditText) findViewById(R.id.email_input);
        mPasswordField = (EditText) findViewById(R.id.password_input);
        signUpButton = (Button) findViewById(R.id.sign_up_button);
        signInButton = (Button) findViewById(R.id.sign_in_button);
        Facebook_sign_in = (Button) findViewById(R.id.Facebook_sign_in);


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
                // create a toast to show that it worked
                //Toast.makeText(WelcomeActivity.this, "WE SIGNED UP.",
                  //      Toast.LENGTH_SHORT).show();
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something when the sign up button is clicked

                signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
                // create a toast to show that it worked
                //Toast.makeText(WelcomeActivity.this, "WE LOGGED IN.",
                  //      Toast.LENGTH_SHORT).show();


            }
        });

        Facebook_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something when the fb sign up button is clicked
                if (AccessToken.getCurrentAccessToken() == null) {
                    goLoginScreen();
                }
                else{
                    gotoMap(null);
                }

            }
        });

    }

    private void goLoginScreen() {
        Intent intent = new Intent(this, FacebookLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void logout(View view) {
        LoginManager.getInstance().logOut();
        goLoginScreen();
    }

    // does this auto-magically get called?
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    //Non-Facebook
    public void createAccount(String email, String password){

        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(WelcomeActivity.this, "createUserWithEmail:success",
                                    Toast.LENGTH_SHORT).show();
                            fetchFacebookUserData();//we might have asynch issue here

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(WelcomeActivity.this, "Authentication failed. Meh.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    //Non-Facebook
    public void signIn(String email, String password){

        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(WelcomeActivity.this, "signInWithEmail:success",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            gotoMap(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(WelcomeActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public FirebaseUser getCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
        }

        return user;
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    // Go to the map activity once we have logged in
    public void gotoMap(FirebaseUser user) {
        Intent i = new Intent(this, MapActivity.class);
        if (user != null) {
            i.putExtra("email", user.getEmail());
            i.putExtra("userName", mEmailField.getText().toString());
            i.putExtra("password", mPasswordField.getText().toString());
            i.putExtra("uID", user.getUid());
        }
        startActivity(i);
    }

    private void signOut() {
        mAuth.signOut();
    }



    public void fetchFacebookUserData()
    {
        // Line to let us see why it's not working
        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);

        // calls the /user/me endpoint to fetch the user data for the given access token.
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(), // TODO - eventually this won't be only our user, but the current user!
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {

                        // get stuff from the JSON object
                        try {
                            Firebase.setAndroidContext(WelcomeActivity.this);
                            Firebase ref = new Firebase(Config.FIREBASE_URl);
                            FirebaseUser user = mAuth.getCurrentUser();
                            String fbName = response.getJSONObject().get("name").toString();
                            String fbUserID = response.getJSONObject().get("id").toString();
                            User newUser = new User(user.getUid(), null, null, fbName, fbUserID);
                            ref.child("users").child(newUser.getUid()).setValue(newUser);
                            gotoMap(user);
                            //fbLocation = response.getJSONObject().get("location").toString();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,location");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
