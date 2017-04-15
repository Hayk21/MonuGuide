package blue_team.com.monuguide.fragments;


import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.location.LocationListener;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import blue_team.com.monuguide.R;
import blue_team.com.monuguide.service.LocationService;
import blue_team.com.monuguide.activities.StartActivity;
import blue_team.com.monuguide.firebase.FireHelper;
import blue_team.com.monuguide.models.Monument;

import static android.content.Context.LOCATION_SERVICE;
import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;
import static blue_team.com.monuguide.activities.SettingsActivity.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;

public class MapStatueFragment extends Fragment implements OnMapReadyCallback{

    private static final String TAG = "MapFragment";

    private static View view;
    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Location mLocation;
    private double mLatitude;
    private double mLongitude;
    private Marker mMarker;
    private FireHelper fireHelper = new FireHelper();
    List<Monument> listOfMonument;
    private FloatingActionButton mCurrentLocationBtn;
    public boolean mSetMyLocation = false;
    boolean b = false;

    private double mRadius = 0.047685;
    private float mDefaultZoom = (float) 13.0;
    private double mLatStart;
    private double mLatEnd;
    private double mLongStart;
    private double mLongEnd;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listOfMonument = new ArrayList<>();
        fireHelper.setOnGetMonumentListSuccessListener(onGetMonumentListSuccessListener);
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

        mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }else {

            setLocationListener();
            mLocationManager.requestLocationUpdates(GPS_PROVIDER, 1000, 1, mLocationListener);
            mLocationManager.requestLocationUpdates(NETWORK_PROVIDER, 1000 * 10, 10, mLocationListener);

            initMap();
        }

    }

    private void setMyLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        else {
            LatLng currentLL = new LatLng(mLatitude, mLongitude);
            CameraUpdate center = CameraUpdateFactory.newLatLng(currentLL);
            mMap.addMarker(new MarkerOptions().position(currentLL).title("My loaction"));
            float zoomLevel = mDefaultZoom; //This goes up to 21
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(zoomLevel);
            mMap.moveCamera(center);
            mMap.animateCamera(zoom, 4000, null);
        }
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

                LatLng defaultLatLng = new LatLng(40.177626, 44.512458);
                CameraUpdate center = CameraUpdateFactory.newLatLng(defaultLatLng);
                mMarker = mMap.addMarker((new MarkerOptions().position(defaultLatLng)
                        .title("Yerevan")));
                float zoomLevel = mDefaultZoom; //This goes up to 21
                CameraUpdate zoom=CameraUpdateFactory.zoomTo(zoomLevel);
                mMap.moveCamera(center);
                mMap.animateCamera(zoom, 9000, null);

                mapMove();
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        //setMyLocation();
                    }
                });
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        if(mCurrentLocationBtn.getVisibility() == View.GONE) {
                            Animation animation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_down);
                            mCurrentLocationBtn.setVisibility(View.VISIBLE);
                            mCurrentLocationBtn.startAnimation(animation1);
                        }
                    }
                });
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        Animation animation = AnimationUtils.loadAnimation(getActivity(),R.anim.translate_up);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                mCurrentLocationBtn.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                            mCurrentLocationBtn.startAnimation(animation);
                        return false;
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
                if(!mSetMyLocation){
                    setMyLocation();
                    mSetMyLocation = true;
                }
                fireHelper.getMonuments(location.getLatitude(), location.getLongitude(), mRadius);
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
                mLatStart = mMap.getCameraPosition().target.latitude;
                mLongStart = mMap.getCameraPosition().target.longitude;
            }
        });

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                mLatEnd = mMap.getCameraPosition().target.latitude;
                mLongEnd = mMap.getCameraPosition().target.longitude;
            }
        });

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (cameraPosition.zoom >= mDefaultZoom){
                    //getMonumentList();
                }
                //else mMap.clear();
                Log.v(TAG, "Latitude!! = " + (mLatStart - mLatEnd) + "    Longitude!! = " + (mLongEnd - mLongStart));
            }
        });

    }


    private FireHelper.IOnGetMonumentListSuccessListener onGetMonumentListSuccessListener = new FireHelper.IOnGetMonumentListSuccessListener() {
        @Override
        public void onSuccess(HashMap<String, Monument> mMap) {
            listOfMonument.clear();
            listOfMonument.addAll(mMap.values());
            getMonumentList();
        }
    };

    private void getMonumentList(){
        mMap.clear();
        listOfMonument.clear();
        LatLng currentLL = new LatLng(mLatitude, mLongitude);
        mMap.addMarker(new MarkerOptions().position(currentLL).title("Marker in Armenia"));
        for (Monument monument : listOfMonument) {
            mMarker = mMap.addMarker((new MarkerOptions().position(new LatLng(monument.getLatitude(), monument.getLongitude()))
                    .title(monument.getName()).snippet(monument.getDesc())));
            setMarkerType((int) monument.getType());
            mMarker.setTag(monument);
        }
    }

    private void setMonumentsInMoveMap(CameraPosition cameraPosition){

        if (cameraPosition.zoom >= mDefaultZoom) {
            fireHelper.getMonuments(cameraPosition.target.latitude, cameraPosition.target.longitude, mRadius);
            getMonumentList();
            Log.v(TAG, "setMonumentsInMove");
        }
        else
            mMap.clear();

    }

    private void setMarkerType(int monumentType){
        switch (monumentType){
            case 1:
                mMarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_monument_marker));
                break;
            case 2:
                mMarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_monument_statue));
                break;
            case 3:
                mMarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_monument_building));
                break;
        }
    }


    public void setMonumentFromSearch(Monument monument){
        LatLng currentLL = new LatLng(monument.getLatitude(), monument.getLongitude());
        CameraUpdate center = CameraUpdateFactory.newLatLng(currentLL);
        mMarker = mMap.addMarker((new MarkerOptions().position(new LatLng(monument.getLatitude(), monument.getLongitude()))
                .title(monument.getName()).snippet(monument.getDesc())));
        setMarkerType((int) monument.getType());
        mMarker.setTag(monument);
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(mDefaultZoom);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom, 9000, null);
    }
}
