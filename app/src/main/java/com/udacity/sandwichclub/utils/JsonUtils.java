package com.udacity.sandwichclub.utils;

import android.util.Log;

import com.udacity.sandwichclub.model.Sandwich;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class JsonUtils {
    private static final String TAG = "JsonUtils";

    /**
     * Parse the Sandwich JSON to Sandwich instance.
     * @param json
     * @return
     */
    public static Sandwich parseSandwichJson(String json) {
        Sandwich temp = null;
        try {
            JSONObject details = new JSONObject(json);
            JSONObject names = details.getJSONObject("name");

            String mainName = names.getString("mainName");
            List<String> alsoKnownAs = getListOfString(names.getJSONArray("alsoKnownAs"));
            String placeOfOrigin = details.getString("placeOfOrigin");
            String description = details.getString("description");
            String imageUrl = details.getString("image");
            List<String> ingredients = getListOfString(details.getJSONArray("ingredients"));

            // We initialise sandwich last, just in case we have problem retrieving any of the
            // json data.
            temp = new Sandwich(mainName, alsoKnownAs, placeOfOrigin, description, imageUrl, ingredients);
            Log.d(TAG, "returning Sandwich object: " + temp);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
        return temp;
    }

    /**
     * Convert JSONArray object to List<String>
     * @param array
     * @return List of String
     */
    private static List<String> getListOfString(JSONArray array) {
        String[] temp = new String[array.length()];
        try {
            for (int i = 0; i < array.length(); i++) {
                temp[i] = array.getString(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,"Unable to parse Json array: " + e.toString());
        }
        return Arrays.asList(temp);
    }
}
