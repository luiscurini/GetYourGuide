package com.luisrubenrodriguez.getyourguide;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddReviewActivity extends AppCompatActivity {

    public static final String ADDREVIEW_TITLE = "addtitle";
    public static final String ADDREVIEW_MESSAGE = "message";
    public static final String ADDREVIEW_RATING = "rating";

    @BindView(R.id.addreview_rating)
    RatingBar rating;
    @BindView(R.id.addreview_title)
    EditText title;
    @BindView(R.id.addreview_titlelayout)
    TextInputLayout titlewrapper;
    @BindView(R.id.addreview_message)
    EditText message;
    @BindView(R.id.addreview_messagelayout)
    TextInputLayout messagewrapper;
    @BindView(R.id.addreview_savebutton)
    Button savebutton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        ButterKnife.bind(this);

        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndSubmit();
            }
        });
    }

    /**
     * Checks if the fields are not empty and submits the review.
     */
    private void checkAndSubmit() {
        //Todo make this nicer.
        if(title.getText().length() == 0) {
            titlewrapper.setError(getString(R.string.addreview_emptyfield));
        } else if (message.getText().length() == 0) {
            messagewrapper.setError(getString(R.string.addreview_emptyfield));
        } else {
            titlewrapper.setErrorEnabled(false);
            messagewrapper.setErrorEnabled(false);
            String ratingValue = Float.toString(rating.getRating());
            Intent intent = new Intent();
            intent.putExtra(ADDREVIEW_TITLE, title.getText().toString());
            intent.putExtra(ADDREVIEW_MESSAGE, message.getText().toString());
            intent.putExtra(ADDREVIEW_RATING, ratingValue);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
