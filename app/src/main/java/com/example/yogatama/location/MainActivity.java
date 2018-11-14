package com.example.yogatama.location;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.sql.DatabaseMetaData;

public class MainActivity extends AppCompatActivity implements DapatkanAlamatTaks.onTaskSelesai {

    private Button mLocationButton;
    private TextView mLocationTextView;
    private ImageView mAndroidImageView;
    private AnimatorSet mRotateAnim;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private Location mLastLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean mTrackingLocation;
    private LocationCallback mLocationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocationButton = (Button) findViewById(R.id.button);
        mLocationTextView = (TextView) findViewById(R.id.txtloc);
        mAndroidImageView = (ImageView) findViewById(R.id.imgView);

        mRotateAnim = (AnimatorSet) AnimatorInflater.loadAnimator(this,
                R.animator.rotate);
        mRotateAnim.setTarget(mAndroidImageView);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mTrackingLocation) {
                    mulaiTrackingLokasi();
                } else {
                    stopTrackingLokasi();
                }
            }
        });
        mLocationCallback = new LocationCallback() {
            public void onLocationResult(LocationResult locationResult) {
                if (mTrackingLocation) {
                    new DapatkanAlamatTaks(MainActivity.this, MainActivity.this).execute(locationResult.getLastLocation());
                }
            }
        };
    }

    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(
                    new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
//                                mLastLocation=location;
//                                mLocationTextView.setText(
//                                        getString(R.string.location_text,
//                                                mLastLocation.getLatitude(),
//                                                mLastLocation.getLongitude(),
//                                                mLastLocation.getTime()));
                                new DapatkanAlamatTaks(MainActivity.this, MainActivity.this).execute(location);
                            } else {
                                mLocationTextView.setText("tidak tersedia");
                            }
                        }
                    });
        }
        mLocationTextView.setText(getString(R.string.alamat_text, "sedang mencari alamat", System.currentTimeMillis()));
    }

    private void mulaiTrackingLokasi() {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(
                    new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                mLastLocation = location;
                                mLocationTextView.setText(
                                        getString(R.string.location_text,
                                                "sedang mencari text",
                                                System.currentTimeMillis()));
                                mTrackingLocation = true;

                                new DapatkanAlamatTaks(MainActivity.this, MainActivity.this).execute(location);
                            } else {
                                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                mFusedLocationClient.requestLocationUpdates(getLocationRequest(), mLocationCallback, null);
                                mLocationTextView.setText(getString(R.string.alamat_text,"sedang mencari",System.currentTimeMillis()));
                                mTrackingLocation= true;
                                mLocationButton.setText("stop tracking");
                                mRotateAnim.start();

                            }
                        }
                    });
        }mLocationTextView.setText(getString(R.string.alamat_text,"sedang mencari alamat",System.currentTimeMillis()));
        mTrackingLocation=true;
        mLocationButton.setText("StopTracking");
        mRotateAnim.start();
    }

    private void stopTrackingLokasi(){
        if(mTrackingLocation){
            mTrackingLocation=false;
            mLocationButton.setText("mulai track");
            mLocationTextView.setText("track hentikan");
            mRotateAnim.end();
        }
    }

    private LocationRequest getLocationRequest(){
        LocationRequest locationRequest=new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length>0
                        && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    getLocation();
                } else {
                    Toast.makeText(this, "tdk dpt permission", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }



    public void onTaskCompleted(String result)
    {
        mLocationTextView.setText(getString(R.string.alamat_text,
                result,System.currentTimeMillis()));
    }

}
