package moe_nya.findcheers;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import moe_nya.findcheers.artifacts.Events;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventMapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener {
    //callback 这是fragment之间相互通信的方法
    //当override之后，会告诉google后端，会把下面的内容加入到代码中去（create，置顶，显示）
    private MapView mMapView;
    private View mView;//whole view

    private DatabaseReference database;
    private List<Events> events;//show a list of events
    private GoogleMap mGoogleMap;
    private Marker lastClicked;

    public EventMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_event_map, container, false);

        database = FirebaseDatabase.getInstance().getReference();
        events = new ArrayList<Events>();

        return mView;

        //从xml中提取出来再返回回去
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) mView.findViewById(R.id.event_map_view);
        //先调用mapview，当里面是空的时候就创建一个view
        if(mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();//needed to get the map to display immediately，走到一个正在运行的状态
            mMapView.getMapAsync(this);//根据操作显示出来，call这个方法的时候自动调用onMapReady
        }
    }

    //find the events that less or equals to tem miles away from current location
    private void setUpMarkersCloseToCurLocation(final GoogleMap googleMap,
                                                final double curLatitude, final double curLongitude) {
        events.clear();
        database.child("events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get all available events
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    Events event = noteDataSnapshot.getValue(Events.class);
                    double destLatitude = event.getLatitude();
                    double destLongitude = event.getLongitude();
                    int distance = Utils.distanceBetweenTwoLocations(curLatitude, curLongitude, destLatitude, destLongitude);

                    //判断距离
                    if (distance <= 50) {
                        events.add(event);
                    }
                }

                //set up every events
                for (Events event : events) {
                    //create marker
                    MarkerOptions marker = new MarkerOptions().position(new LatLng(event.getLatitude(), event.getLongitude())).title(event.getTitle());

                    //change marker icon
                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

                    //adding marker
                    Marker mker = googleMap.addMarker(marker);
                    mker.setTag(event);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), ":( Events fetch failed", Toast.LENGTH_SHORT).show();
            }
        });
    }





    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();//跟fragment的生命周期相关
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    //当更新map的时候会调用这个方法，是最重要的方法
    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        mGoogleMap.setOnInfoWindowClickListener(this);
        mGoogleMap.setOnMarkerClickListener(this);

        final LocationTracker locationTracker = new LocationTracker(getActivity());
        locationTracker.getLocation();
        double curLatitude = locationTracker.getLatitude();
        double curLongitude = locationTracker.getLongitude();

        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(curLatitude, curLongitude)).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        setUpMarkersCloseToCurLocation(googleMap, curLatitude, curLongitude);

        //how to show a marker in the map sample
        //broadcast for the system
//        double latitude = 17.385044;
//        double longitude = 78.486671;
//        //create marker on google map
//        MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Here some cheers around you!");
//
//        //change marker icon on google map
//        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
//
//        //add marker to google map
//        googleMap.addMarker(marker);
//
//        //set up camera configuration, set camera to latitude = 17.385044, longitude = 78.486671 and zoom to 12
//        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(12).build();
//        //animate the zoom process
//        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    //add google map instance into the class, implement oninfowindow click listener interface which will add click listener to infor window on marker

    @Override
    public void onInfoWindowClick(Marker marker) {
        Events event = (Events) marker.getTag();
        Intent intent = new Intent(getContext(), CommentActivity.class);
        String eventId = event.getId();
        intent.putExtra("EventID", eventId);
        getContext().startActivity(intent);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        final Events event = (Events) marker.getTag();
        if (lastClicked != null && lastClicked.equals(marker)) {
            lastClicked = null;
            marker.hideInfoWindow();
            marker.setIcon(null);
            return true;
        }  else {
            lastClicked = null;
            new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground( Void... voids) {
                    Bitmap bitmap = Utils.getBitmapFromURL(event.getImgUri());
                    return bitmap;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    super.onPostExecute(bitmap);
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                    marker.setTitle(event.getTitle());
                }
            }.execute();
            return false;
        }
    }

}
