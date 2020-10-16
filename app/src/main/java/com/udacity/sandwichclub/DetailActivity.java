package com.udacity.sandwichclub;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.udacity.sandwichclub.model.Sandwich;
import com.udacity.sandwichclub.utils.JsonUtils;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "extra_position";
    private static final int DEFAULT_POSITION = -1;
    // For Logging
    private final String TAG = this.getClass().toString();

    private ImageView ingredientsIv;
    private TextView mPlaceOfOriginTextView;
    private TextView mAlsoKnownAsTextView;
    private TextView mIngredientsTextView;
    private TextView mDescriptionTextView;
    private TextView mImgErrorMsgTextView;
    private ProgressBar mLoadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Detail Activity has been launched!!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initialiseViews();

        Intent intent = getIntent();
        if (intent == null) {
            Log.e(this.getClass().toString(), "Intent is null!");
            closeOnError();
        }

        int position = intent.getIntExtra(EXTRA_POSITION, DEFAULT_POSITION);
        if (position == DEFAULT_POSITION) {
            // EXTRA_POSITION not found in intent
            Log.e(TAG, "EXTRA_POSITION is not found in intent. This is the DEFAULT_POSITION: " + DEFAULT_POSITION);
            closeOnError();
            return;
        }

        String[] sandwiches = getResources().getStringArray(R.array.sandwich_details);
        Log.d(TAG, "Sandwich details: " + sandwiches.length);

        String json = sandwiches[position];
        Log.v(TAG, "Matched sandwich json: \n" + json);

        Sandwich sandwich = JsonUtils.parseSandwichJson(json);
        if (sandwich == null) {
            // Sandwich data unavailable
            Log.e(TAG, "Unable to parse the Sandwich Json");
            closeOnError();
            return;
        }

        // Put the sandwich to the screen;
        populateUI(sandwich);
    }

    /**
     * Show generic error
     */
    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Showing image loading error.
     */
    private void showImageLoadingError() {
        Log.d(TAG, "Showing Image Loading Error");
        mLoadingProgressBar.setVisibility(View.INVISIBLE);
        mImgErrorMsgTextView.setVisibility(View.VISIBLE);
        Toast.makeText(this, R.string.image_error_message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Showing Image Loading Progress Bar while hide the other UI elements.
     */
    private void showImageLoadingProgressBar() {
        Log.d(TAG, "Showing Image Loading Progress Bar");
        mLoadingProgressBar.setVisibility(View.VISIBLE);
        mImgErrorMsgTextView.setVisibility(View.INVISIBLE);
    }


    private void populateUI(Sandwich sandwich) {
        // Image Loading
        String imageUrl = sandwich.getImage();
        Log.d(TAG, "Loading sandwich image from: " + imageUrl);
        Picasso.with(this)
                .load(imageUrl)
                .into(target);

        // Set Sandwich Title
        setTitle(sandwich.getMainName());

        // Also Known As
        for (String i: sandwich.getAlsoKnownAs()) {
            mAlsoKnownAsTextView.append( " - " + i + "\n");
        }

        // Place of Origin
        mPlaceOfOriginTextView.setText(sandwich.getPlaceOfOrigin());

        // Description
        mDescriptionTextView.setText(sandwich.getDescription());

        // Ingredients
        for(String i: sandwich.getIngredients()) {
            mIngredientsTextView.append(" - " + i + "\n");
        }
    }

    /**
     * Initialise all the views.
     */
    private void initialiseViews() {
        ingredientsIv = findViewById(R.id.image_iv);
        mPlaceOfOriginTextView = findViewById(R.id.tv_origin);
        mAlsoKnownAsTextView = findViewById(R.id.tv_also_known);
        mIngredientsTextView =  findViewById(R.id.tv_ingredients);
        mDescriptionTextView =  findViewById(R.id.tv_description);
        mImgErrorMsgTextView = findViewById(R.id.tv_error_msg);
        mLoadingProgressBar = findViewById(R.id.pb_loading);

        Log.d(TAG, "All views in details have been initialised. ");
    }

    /**
     * Allow us to use the callback to set the UI elements properly.
     * Referred the sample from: https://futurestud.io/tutorials/picasso-callbacks-remoteviews-and-notifications
     */
    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            //Hide image loading progress bar
            mLoadingProgressBar.setVisibility(View.INVISIBLE);
            ingredientsIv.setImageBitmap(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            showImageLoadingError();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            showImageLoadingProgressBar();
        }
    };
}
