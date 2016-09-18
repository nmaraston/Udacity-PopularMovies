package com.iopho.android.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.iopho.android.dataAccess.tmdb.TMDBClient;
import com.iopho.android.dataAccess.tmdb.TMDBClientImpl;
import com.iopho.android.dataAccess.tmdb.model.DataPage;
import com.iopho.android.dataAccess.tmdb.model.Movie;
import com.iopho.android.util.HttpURLDownloader;

public class MovieGalleryActivity extends AppCompatActivity {

    private static final String LOG_TAG = MovieGalleryActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
                fetchMoviesTask.execute();
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class FetchMoviesTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            String apiKey = "b70e10dcfb049ab5616c62edb2946e9e";
            TMDBClient tmdbClient = new TMDBClientImpl(apiKey, new HttpURLDownloader(10000, 10000));
            try {
                DataPage<Movie> moviesPage = tmdbClient.getTopRatedMovies(1);
                Log.d(LOG_TAG, moviesPage.getResults().get(0).toString());
            } catch (Exception ex) {
                Log.e(LOG_TAG, "Error", ex);
            }

            return true;
        }
    }
}
