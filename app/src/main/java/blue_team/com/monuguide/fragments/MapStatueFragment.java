package blue_team.com.monuguide.fragments;


import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.location.LocationListener;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.Services.LocationService;
import blue_team.com.monuguide.activities.MainActivity;
import blue_team.com.monuguide.activities.SettingsActivity;
import blue_team.com.monuguide.activities.StartActivity;
import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.models.Monument;

public class MapStatueFragment extends Fragment implements OnMapReadyCallback{

    private static View view;
    private MapFragment mapFragment;
    private GoogleMap mMap;

    private LocationManager mLocationManager;
    private Location mLocation;
    private double mLatitude;
    private double mLongitude;
    private Marker mMarker;
    private FireHelper fireHelper = new FireHelper();
    private DetailsFragment mDetailsFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    List<Monument> listOfMonument, listOfFindedMonuments, showMonuments;


    private FireHelper.IOnSuccessListener onSuccessListener = new FireHelper.IOnSuccessListener() {
        @Override
        public void onSuccess(HashMap<String, Monument> mMap) {
            listOfMonument.clear();
            listOfMonument.addAll(mMap.values());
            getMonumentList();
            //testForNotification();
            // listi het gorcoxutyunner@ anel aystex
        }
    };

    private void getMonumentList(){
        for (Monument monument : listOfMonument) {
            mMarker = mMap.addMarker((new MarkerOptions().position(new LatLng(monument.getLatitude(), monument.getLongitude()))
                    .title(monument.getName()).snippet(monument.getDesc()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_monument_marker))));

            mMarker.setTag(monument);
        }
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listOfMonument = new ArrayList<>();
        fireHelper.setOnSuccessListener(onSuccessListener);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_map, container, false);
        } catch (InflateException e) {
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        mLocationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            System.out.println("oyoy");
            return;
        }

        mLocationManager.requestLocationUpdates(mLocationManager.GPS_PROVIDER, 1000, 1, locationListener);
        mLocationManager.requestLocationUpdates(mLocationManager.NETWORK_PROVIDER, 1000 * 10, 10, locationListener);


        getMapFragment().getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        Monument monument = (Monument) marker.getTag();
                        Intent intent = new Intent(getActivity(), StartActivity.class);
                        intent.putExtra(LocationService.SHOWING_MONUMENT,monument);
                        startActivity(intent);

                        return false;
                    }
                });
                //initMarkers();
            }

        });



        /*mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                System.out.println("marker = " + marker);
                return false;
            }
        });*/

    }



    private void setMyLocation(Location location) {
        mMap.clear();
        LatLng currentLL = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(currentLL).title("Marker in Armenia"));
        float zoomLevel = (float) 16.0; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLL, zoomLevel));

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLL) );
    }



    private MapFragment getMapFragment() {
        FragmentManager fm = null;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            fm = getFragmentManager();
        } else {
            fm = getChildFragmentManager();
        }

        return (MapFragment) fm.findFragmentById(R.id.map);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void initMarkers() {
        mMarker = mMap.addMarker((new MarkerOptions().position(new LatLng(mLatitude, mLongitude))
                .title("Hello world").snippet("Additional text").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_monument_marker))));
    }




    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mLongitude = location.getLongitude();
            mLatitude = location.getLatitude();
            //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LocationService.this);
            fireHelper.getMonuments(location.getLatitude(), location.getLongitude(), 50/*Double.valueOf(sharedPreferences.getString(SettingsActivity.KEY_OF_LIST_RADIUS, "0"))*/);

            setMyLocation(location);
            getMonumentList();
            //initMarkers();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    public void setMonumentFromSearch(Monument monument){
        System.out.println("setMonumentFromSearch");
        mMarker = mMap.addMarker((new MarkerOptions().position(new LatLng(monument.getLatitude(), monument.getLongitude()))
                .title(monument.getName()).snippet(monument.getDesc()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_monument_marker))));
        LatLng currentLL = new LatLng(monument.getLatitude(), monument.getLongitude());
        float zoomLevel = (float) 16.0; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLL, zoomLevel));
    }

}
