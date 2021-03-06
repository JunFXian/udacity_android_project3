/**
 * Created by Jun Xian for Udacity Android Developer Nanodegree project 3
 * Date: Aug 15, 2017
 * Reference:
 *      https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
 *      https://cloud.google.com/endpoints/docs/frameworks/python/consume_android
 */

package com.udacity.gradle.builditbigger.async;

import android.content.Context;
import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.udacity.gradle.backend.myApi.MyApi;

import java.io.IOException;

public class JokesAsyncTask extends AsyncTask<Void, Void, String> {
    private static MyApi myApiService = null;
    private Context mContext;
    private final AsyncTaskCompleteListener<String> mListener;

    // constructor for the AsyncTask class
    public JokesAsyncTask(Context contextInfo, AsyncTaskCompleteListener<String> listener)
    {
        this.mContext = contextInfo;
        this.mListener = listener;
    }

    @Override
    protected String doInBackground(Void... params) {
        if(myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(
                                AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
                                throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver
            myApiService = builder.build();
        }

        try {
            return myApiService.getJokesFromLibrary().execute().getData();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @Override
    public void onPostExecute(String result) {
        super.onPostExecute(result);
        mListener.onTaskComplete(result);
    }
}
