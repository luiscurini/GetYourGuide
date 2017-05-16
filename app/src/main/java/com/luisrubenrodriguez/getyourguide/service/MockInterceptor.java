package com.luisrubenrodriguez.getyourguide.service;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * Created by GamingMonster on 16.05.2017.
 * This is used to intercept the requests and in case that we want to add a review, this will return a predifined JSON with the data entered by the user.
 */

class MockInterceptor implements Interceptor {

    private String hardcodedResponse = "{\"review_id\":150000," +
            "\"rating\":\"%1$s\"," +
            "\"title\":\"%2$s\"," +
            "\"message\":\"%3$s\"," +
            "\"author\":\"Luis Rodriguez\"," +
            "\"foreignLanguage\":true," +
            "\"date\":\"May 16, 2017\"," +
            "\"date_unformatted\":{}," +
            "\"languageCode\":\"de\"," +
            "\"traveler_type\":\"couple\"," +
            "\"reviewerName\":\"Luis Rodriguez\"," +
            "\"reviewerCountry\":\"Berlin, Germany\"},";

    private static final String TAG = "MockInterceptor";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response;
        final Uri uri = Uri.parse(chain.request().url().toString());

        //Only in the case that the url path is "/addreview" the response will be hardcoded.
        if (uri.getPath().equals(GYGApi.MOCKADDREVIEWPATH)) {

            String jsonResponse = hardcodedResponse;
            String jsonData = stringifyRequestBody(chain.request());
            try {
                JSONObject jsonObject = new JSONObject(jsonData);
                String title = jsonObject.getString("title");
                String rating = jsonObject.getString("rating");
                String message = jsonObject.getString("message");
                jsonResponse = String.format(hardcodedResponse, rating, title, message);
            } catch (JSONException exception) {
                Log.e(TAG, "intercept: error while parsing JSON" + exception.getMessage());
            }

            response = new Response.Builder()
                    .code(200)
                    .message(jsonResponse)
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_0)
                    .body(ResponseBody.create(MediaType.parse("application/json"), jsonResponse.getBytes()))
                    .addHeader("content-type", "application/json")
                    .build();

        //Any other case continue the normal execution.
        } else {
            response = chain.proceed(chain.request());
        }

        return response;

    }

    /**
     * Takes the request and extracts its body as String.
     *
     * @param request
     * @return Request body as String.
     */
    private static String stringifyRequestBody(Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "";
        }
    }
}
