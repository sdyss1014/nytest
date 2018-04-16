package com.andro.school;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.andro.school.ScreenOne.listDataItems;

public class ScreenTwo extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    private GoogleMap gmap;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.screen_two);
        setTitle("School Details");
        Button sat_data = (Button) findViewById(R.id.sat_data);
        if (getIntent().getStringExtra("sat_data").length() == 0)
            sat_data.setVisibility(View.GONE);
        else
            sat_data.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setMessage(getIntent().getStringExtra("sat_data"));
                    dialog.setNeutralButton("OK", null);
                    dialog.create().show();
                }
            });
        TextView textView1 = (TextView) findViewById(R.id.textView1);
        textView1.setText(listDataItems.get(getIntent().getIntExtra("index", 0)).get("school_name"));

        TextView textView2 = (TextView) findViewById(R.id.textView2);
        textView2.setText(listDataItems.get(getIntent().getIntExtra("index", 0)).get("phone_number"));

        TextView textView3 = (TextView) findViewById(R.id.textView3);
        textView3.setText(listDataItems.get(getIntent().getIntExtra("index", 0)).get("school_email"));

        TextView textView4 = (TextView) findViewById(R.id.textView4);
        textView4.setText("Timing: " + listDataItems.get(getIntent().getIntExtra("index", 0)).get("start_time")
                + " - " + listDataItems.get(getIntent().getIntExtra("index", 0)).get("end_time")
        +"\n\nTotal Students: "+ listDataItems.get(getIntent().getIntExtra("index", 0)).get("total_students")
        +"\n\nLocation: "+ listDataItems.get(getIntent().getIntExtra("index", 0)).get("location"));

        TextView textView5 = (TextView) findViewById(R.id.textView5);
        textView5.setText(listDataItems.get(getIntent().getIntExtra("index", 0)).get("website"));

        TextView textView6 = (TextView) findViewById(R.id.textView6);
        textView6.setText(listDataItems.get(getIntent().getIntExtra("index", 0)).get("overview_paragraph"));

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.map);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        LatLng ny = new LatLng(Float.parseFloat(listDataItems.get(getIntent().getIntExtra("index", 0)).get("latitude")),
                Float.parseFloat(listDataItems.get(getIntent().getIntExtra("index", 0)).get("longitude")));
        gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(ny, 12.0f));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(ny);
        gmap.addMarker(markerOptions);

    }
}
