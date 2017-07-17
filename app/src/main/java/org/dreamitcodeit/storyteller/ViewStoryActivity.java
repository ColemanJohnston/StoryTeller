package org.dreamitcodeit.storyteller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ViewStoryActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvStoryBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_story);

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvStoryBody = (TextView) findViewById(R.id.tvStoryBody);

        tvTitle.setText(getIntent().getStringExtra("title"));
        tvStoryBody.setText(getIntent().getStringExtra("storyBody"));
    }
}
