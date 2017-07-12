package org.dreamitcodeit.storyteller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        String email = getIntent().getExtras().getString("email");

        TextView tvWelcome = (TextView) findViewById(R.id.tvWelcome);
        tvWelcome.setText("Welcome user " + email);
    }
}
