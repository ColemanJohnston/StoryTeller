package org.dreamitcodeit.storyteller;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by mariadeangelis on 7/12/17.
 */

public class AuthorFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_author, container, false);
        getDialog().setTitle("Author new story");

        Button cancel = (Button) rootView.findViewById(R.id.cancel);
        Button submit = (Button) rootView.findViewById(R.id.submit);

        // save this story and close the window
        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // TODO - grab the data from the edit text, create a new story object
                // TODO - populate the story object with location (passed in from the MapActivity which launches this fragment)
                // TODO - and send to our cloud based database

                dismiss();
            }
        });

        // discard this story and close the window
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // TODO - alert dialogue comes up saying, "Are you sure?".

                dismiss();
            }
        });

        return rootView;
    }

}
