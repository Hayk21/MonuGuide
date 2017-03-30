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
import android.support.design.widget.FloatingActionButton;
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

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
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
    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Location mLocation;
    private double mLatitude;
    private double mLongitude;
    private Marker mMarker;
    private FireHelper fireHelper = new FireHelper();
    private DetailsFragment mDetailsFragment;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    List<Monument> listOfMonument;
    private FloatingActionButton mCurrentLocationBtn;

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
        mCurrentLocationBtn = (FloatingActionButton) view.findViewById(R.id.fab);
        mCurrentLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMyLocation();
            }
        });

        mFragmentManager = getFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mLocationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        setLocationListener();
        mLocationManager.requestLocationUpdates(mLocationManager.GPS_PROVIDER, 1000, 1, mLocationListener);
        mLocationManager.requestLocationUpdates(mLocationManager.NETWORK_PROVIDER, 1000 * 10, 10, mLocationListener);
        System.out.println("Latitude = " + mLatitude);
        initMap();

    }

    private void setMyLocation() {
        LatLng currentLL = new LatLng(mLatitude, mLongitude);
        CameraUpdate center = CameraUpdateFactory.newLatLng(currentLL);
        mMap.addMarker(new MarkerOptions().position(currentLL).title("My loaction"));
        float zoomLevel = (float) 16.0; //This goes up to 21
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(zoomLevel);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom, 4000, null);
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



    private void initMap(){
        getMapFragment().getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        //setMyLocation();
                    }
                });
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Monument monument = (Monument) marker.getTag();
                        Intent intent = new Intent(getActivity(), StartActivity.class);
                        intent.putExtra(LocationService.SHOWING_MONUMENT,monument);
                        startActivity(intent);

                    }
                });
            }

        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void setLocationListener() {
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLongitude = location.getLongitude();
                mLatitude = location.getLatitude();
                setMyLocation();
                fireHelper.getMonuments(location.getLatitude(), location.getLongitude(), 50);
                getMonumentList();
                mapMove();
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
    }

    private void mapMove(){

        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                System.out.println("camera move started = " + mMap.getCameraPosition().zoom);
            }
        });
        mMap.setOnCameraMoveCanceledListener(new GoogleMap.OnCameraMoveCanceledListener() {
            @Override
            public void onCameraMoveCanceled() {
                System.out.println("camera move canceled");
            }
        });
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                System.out.println("onCameraMove = " + mMap);
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                System.out.println("onMapClickListener = " + latLng.latitude);
            }
        });

        
    }


    private FireHelper.IOnSuccessListener onSuccessListener = new FireHelper.IOnSuccessListener() {
        @Override
        public void onSuccess(HashMap<String, Monument> mMap) {
            listOfMonument.clear();
            listOfMonument.addAll(mMap.values());
            getMonumentList();
        }
    };

    private void getMonumentList(){
        mMap.clear();
        LatLng currentLL = new LatLng(mLatitude, mLongitude);
        mMap.addMarker(new MarkerOptions().position(currentLL).title("Marker in Armenia"));
        for (Monument monument : listOfMonument) {
            mMarker = mMap.addMarker((new MarkerOptions().position(new LatLng(monument.getLatitude(), monument.getLongitude()))
                    .title(monument.getName()).snippet(monument.getDesc()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_monument_marker))));

            mMarker.setTag(monument);
        }
    }


    public void setMonumentFromSearch(Monument monument){
        LatLng currentLL = new LatLng(monument.getLatitude(), monument.getLongitude());
        CameraUpdate center = CameraUpdateFactory.newLatLng(currentLL);
        mMarker = mMap.addMarker((new MarkerOptions().position(new LatLng(monument.getLatitude(), monument.getLongitude()))
                .title(monument.getName()).snippet(monument.getDesc()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_monument_marker))));
        float zoomLevel = (float) 16.0; //This goes up to 21
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(zoomLevel);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom, 9000, null);
    }

}
