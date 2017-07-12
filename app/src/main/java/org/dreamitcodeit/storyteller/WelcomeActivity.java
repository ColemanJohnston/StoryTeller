package org.dreamitcodeit.storyteller;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";

    private EditText mEmailField;
    private EditText mPasswordField;
    private Button signUpButton;
    private Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mAuth = FirebaseAuth.getInstance();

        // Get references to our EditTexts and buttons
        mEmailField = (EditText) findViewById(R.id.email_input);
        mPasswordField = (EditText) findViewById(R.id.password_input);
        signUpButton = (Button) findViewById(R.id.sign_up_button);
        signInButton = (Button) findViewById(R.id.sign_in_button);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
                // create a toast to show that it worked
                Toast.makeText(WelcomeActivity.this, "WE SIGNED UP.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something when the sign up button is clicked

                signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
                // create a toast to show that it worked
                Toast.makeText(WelcomeActivity.this, "WE SIGNED UP.",
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(WelcomeActivity.this, ComposeStoryActivity.class);
                intent.putExtra("userName", mEmailField.getText().toString());
                startActivity(intent);
            }
        });

    }

    // does this auto-magically get called?
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

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
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            gotoWelcome(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(WelcomeActivity.this, "Authentication failed. Meh.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });

    }

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
                            updateUI(user);
                            gotoWelcome(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(WelcomeActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
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


    // TODO - after we have a UI
    public void updateUI(FirebaseUser user) {
//        if (user != null) {
//        }
    }

    // Go to the map activity once we have logged in
    public void gotoWelcome(FirebaseUser user) {
        if (user != null) {
            Intent i = new Intent(this, MapActivity.class);
            i.putExtra("email", user.getEmail());
            startActivity(i);
        }
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

}
