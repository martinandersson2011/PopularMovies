package com.martinandersson.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.martinandersson.popularmovies.events.SelectedMovieEvent;
import com.martinandersson.popularmovies.events.SortOrderEvent;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_most_popular) {
            EventBus.getDefault().postSticky(new SortOrderEvent(Constants.SORT_ORDER_MOST_POPULAR));
            return true;
        } else if (id == R.id.action_highest_rated) {
            EventBus.getDefault().postSticky(new SortOrderEvent(Constants.SORT_ORDER_HIGHEST_RATED));
            return true;
        } else if (id == R.id.action_favorites) {
            EventBus.getDefault().postSticky(new SortOrderEvent(Constants.SORT_ORDER_FAVORITES));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onEvent(SelectedMovieEvent event) {
        // Only start a detail activity if we are not using a two pane layout
        boolean isTwoPaneLayout = getResources().getBoolean(R.bool.isTwoPaneLayout);
        if (!isTwoPaneLayout) {
            Intent intent = new Intent(this, DetailActivity.class);
            startActivity(intent);
        }
    }
}
