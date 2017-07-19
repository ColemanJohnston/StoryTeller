package org.dreamitcodeit.storyteller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;


/**
 * Created by neeharmb on 7/18/17.
 */

public class SearchActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvStoryBody;
    String title;
    String body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        tvStoryBody = (TextView) findViewById(R.id.tvStoryBody);
        tvTitle = (TextView) findViewById(R.id.tvTitle);

        title = getIntent().getStringExtra("title");
        body = getIntent().getStringExtra("body");

        tvTitle.setText(title);
        tvStoryBody.setText(body);
        tvStoryBody.setMovementMethod(new ScrollingMovementMethod());
    }

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(SearchActivity.this, MapActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }*/

}
