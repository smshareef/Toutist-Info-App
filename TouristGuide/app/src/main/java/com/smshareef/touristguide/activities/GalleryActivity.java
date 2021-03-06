package com.smshareef.touristguide.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.smshareef.touristguide.R;
import com.smshareef.touristguide.adapters.GalleryRecyclerAdapter;
import com.smshareef.touristguide.manager.PlaceDataManager;
import com.smshareef.touristguide.model.Place;
import com.smshareef.touristguide.utils.OnRecyclerViewItemClickListener;
import com.smshareef.touristguide.utils.PlaceDataListener;

import java.util.ArrayList;

import static com.smshareef.touristguide.utils.AppConstants.KEY_POSITION;
import static com.smshareef.touristguide.utils.AppConstants.RESOURCES_DIR;

public class GalleryActivity extends AppCompatActivity implements OnRecyclerViewItemClickListener, PlaceDataListener {

    //----------------------------------------------------------------------------------------------
    //Views
    //----------------------------------------------------------------------------------------------
    private FloatingActionButton fab;

    private RecyclerView recyclerView;

    //----------------------------------------------------------------------------------------------
    //Other fields
    //----------------------------------------------------------------------------------------------

    private GalleryRecyclerAdapter galleryRecyclerAdapter;
    private static final int GRID_COLUMN_COUNT = 2;
    private static final int REQUEST_CODE = 102;
    private String placeName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        //To add new images to gallery

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GalleryActivity.this, AddImageActivity.class);
                intent.putExtra("place_name", placeName);
                intent.putExtra("image_count", galleryRecyclerAdapter.getItemCount());
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        galleryRecyclerAdapter = new GalleryRecyclerAdapter(this);

        recyclerView.setHasFixedSize(true);

        galleryRecyclerAdapter.setOnRecyclerViewItemClickListener(this);
        recyclerView.setAdapter(galleryRecyclerAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, GRID_COLUMN_COUNT);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);

        placeName = getIntent().getStringExtra("Place_Name");
        setTitle(placeName + " Gallery");

        loadData();
    }

    private void loadData() {
        PlaceDataManager placeDataManager = new PlaceDataManager(this);
        placeDataManager.setDir(RESOURCES_DIR + "/" + placeName + "/");
        placeDataManager.fetchPlaceData();
    }

    @Override
    public void onPlaceLoaded(ArrayList<Place> galleryArrayList) {
        galleryRecyclerAdapter.setData(galleryArrayList);
    }

    @Override
    public void onPlaceLoadingFailed() {

    }

    @Override
    public void onPlaceLoadingCancelled() {

    }

    //Shows the image in fullscreen when clicked

    @Override
    public void onItemClicked(Bundle bundle) {
        Intent intent = new Intent(GalleryActivity.this, GalleryFullScreenActivity.class);
        intent.putExtra("position", bundle.getInt(KEY_POSITION));
        intent.putExtra("dir", RESOURCES_DIR + "/" + placeName + "/");
        GalleryActivity.this.startActivity(intent);

    }

    //Adds image to gallery once available from called activity

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                Bundle b = data.getExtras();
                if(b != null) {
                    Place gallery = b.getParcelable("gallery");
                    galleryRecyclerAdapter.addData(gallery);
                }
            } else if (resultCode == 0) {
                Toast.makeText(this, "Request Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.logout_button) {
            Intent intent = new Intent(GalleryActivity.this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
