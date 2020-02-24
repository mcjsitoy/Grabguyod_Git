package com.example.grabguyod;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.telephony.SmsManager;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillOpacity;

public class requestForm extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {

    DatabaseReference database_requestForm, queryRequest, databaseReference, driverCount, ss;
    ListView listViewRequest;
    List<addRequest> addRequestList;
    FirebaseUser user;
    String offline_BroadcastStatus = "Pending",request_Status = "Pending", uid, req_id;
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd");
    String millisInString  = dateFormat.format(new Date());
    String location = "Davao",tempCount; // LOCATION PLACE HERE
    String no_Riders, requestCode, status, safetycode, tempPhoneNumber,tempName, lat, lng, set,user_destination;
    Random random = new Random();
    EditText  et_noP, et_destination;
    TextView tv_safeCode,tv_userName,tv_driverCount, tv_requestStatus;
    Button bt_submit, bt_logout, bt_cancel,bt_accept,bt_decline;
    double lt, lg;
    View lt_cancel, lt_driverfound, lt_currentdriver;
    final List<String> keyNamelist = new ArrayList<String>();
    public List<String> streetlist = new ArrayList<String>();
    final List<Double> distlist = new ArrayList<Double>();
    final List<Double> latlist = new ArrayList<Double>();
    final List<Double> lnglist = new ArrayList<Double>();
    Boolean hasRequest = false;
    int size, count;

    private static final LatLng BOUND_CORNER_NW = new LatLng(7.165823, 125.646832);
    private static final LatLng BOUND_CORNER_SE = new LatLng(7.161096, 125.657170);
    private static final LatLngBounds RESTRICTED_BOUNDS_AREA = new LatLngBounds.Builder()
            .include(BOUND_CORNER_NW)
            .include(BOUND_CORNER_SE)
            .build();

    private final List<List<Point>> points = new ArrayList<>();
    private final List<Point> outerPoints = new ArrayList<>();
    private FirebaseAuth mauth;


    private MapView mapView;
    private MapboxMap mapbox;
    private PermissionsManager permissionsManager;
    private LocationEngine locationengine;
    private long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private requestForm.requestFormLocationCallback callback = new requestForm.requestFormLocationCallback(this);
    private  boolean isloggingout = false;
    public static String locs;
    Double counterDist;
    String nearLoc;
    int counterPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_request_form);
        mapView = findViewById(R.id.mapView2);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        mauth = FirebaseAuth.getInstance();

        database_requestForm = FirebaseDatabase.getInstance().getReference("requestForm");

        //Setting The Widgets
        bt_cancel = (Button) findViewById(R.id.button_cancel);
        et_noP = (EditText) findViewById(R.id.tb_id);
        bt_submit = (Button) findViewById(R.id.bt_request);
        bt_logout = (Button) findViewById(R.id.button_offDuty);
        bt_accept = findViewById(R.id.button_accept);
        bt_decline = findViewById(R.id.button_decline);
        listViewRequest = (ListView) findViewById(R.id.listView_Request);
        lt_cancel = findViewById(R.id.layout_pendingReq);
        lt_driverfound = findViewById(R.id.layout_driverFound);
        lt_currentdriver = findViewById(R.id.layout_currentdriver);
        tv_safeCode = findViewById(R.id.textView_passengerCode);
        et_destination = findViewById(R.id.editText_Destination);
        tv_requestStatus = findViewById(R.id.textView25);
        /*s_dest = findViewById(R.id.spinner2);*/

        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        addRequestList = new ArrayList<>();

        //HIDE LAYOUT
        lt_cancel.setVisibility(View.INVISIBLE);
        lt_driverfound.setVisibility(View.INVISIBLE);
        lt_currentdriver.setVisibility(View.INVISIBLE);


        getStreets();
        getUserLocation();


        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (int y = 0; y < count; y++) {

                    double earthRadius = 6371 ; // in miles, change to 6371 for kilometer output

                    double dLat = Math.toRadians(latlist.get(y)-lt);
                    double dLng = Math.toRadians(lnglist.get(y)-lg);

                    double sindLat = Math.sin(dLat / 2);
                    double sindLng = Math.sin(dLng / 2);

                    double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                            * Math.cos(Math.toRadians(lt)) * Math.cos(Math.toRadians(latlist.get(y)));

                    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

                    double dist = earthRadius * c;

                    distlist.add(dist);
                }


                for (int x = 0; x < distlist.size(); x++){
                    if (x == 0){
                         counterDist = distlist.get(x);
                         nearLoc = streetlist.get(x);
                         counterPosition = x;

                    } else {
                        if (counterDist > distlist.get(x)){
                            counterDist = distlist.get(x);
                            nearLoc = streetlist.get(x);
                            counterPosition = x;
                        }
                    }

                }

                for (int y = 0; y < count; y++){
                    queryRequest =  FirebaseDatabase.getInstance().getReference("ShortestDistance");
                    queryRequest.child(streetlist.get(y)).setValue(distlist.get(y));
                }
                //Check location
                Toast.makeText(requestForm.this, "Shortest Distance " + counterDist + " Position: " + counterPosition + " Location: " + nearLoc, Toast.LENGTH_SHORT).show();
                addRequest();
            }


        });


        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelRequest();
            }
        });

        //USER LOGOUT
        bt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public   void onClick(View v) {
                disconnect();
            }
        });


        //DRIVER FOUND & USER OPTION
        bt_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptDriver();
            }
        });

        bt_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declineDriver();
            }
        });


    }

    public void onMapReady(@NonNull final MapboxMap mapbox) {
        this.mapbox = mapbox;
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mapbox.setStyle(new Style.Builder().fromUri("mapbox://styles/mcjsitoy/ck08cyj3p125l1cp6x5dj7veq"),
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        enableLocationComponent(style);
                        mapbox.setLatLngBoundsForCameraTarget(RESTRICTED_BOUNDS_AREA);

                        mapbox.setMinZoomPreference(10);
                        showBoundsArea(style);
                        //MARKER ADD//
                        /*SymbolManager symbolManager = new SymbolManager(mapView, mapbox, style);
                        symbolManager.setIconAllowOverlap(true);
                        symbolManager.setIconIgnorePlacement(true);*/
                        //MARKER ADD//
                        showCrosshair();
                        initLocationEngine();

                    }

                });
    }

    private void showBoundsArea(@NonNull Style loadedMapStyle) {
        outerPoints.add(Point.fromLngLat(RESTRICTED_BOUNDS_AREA.getNorthWest().getLongitude(),
                RESTRICTED_BOUNDS_AREA.getNorthWest().getLatitude()));
        outerPoints.add(Point.fromLngLat(RESTRICTED_BOUNDS_AREA.getNorthEast().getLongitude(),
                RESTRICTED_BOUNDS_AREA.getNorthEast().getLatitude()));
        outerPoints.add(Point.fromLngLat(RESTRICTED_BOUNDS_AREA.getSouthEast().getLongitude(),
                RESTRICTED_BOUNDS_AREA.getSouthEast().getLatitude()));
        outerPoints.add(Point.fromLngLat(RESTRICTED_BOUNDS_AREA.getSouthWest().getLongitude(),
                RESTRICTED_BOUNDS_AREA.getSouthWest().getLatitude()));
        outerPoints.add(Point.fromLngLat(RESTRICTED_BOUNDS_AREA.getNorthWest().getLongitude(),
                RESTRICTED_BOUNDS_AREA.getNorthWest().getLatitude()));
        points.add(outerPoints);

        loadedMapStyle.addSource(new GeoJsonSource("source-id",
                Polygon.fromLngLats(points)));

        loadedMapStyle.addLayer(new FillLayer("layer-id", "source-id").withProperties(
                fillColor(Color.RED ),
                fillOpacity(.25f)
        ));
    }

    private void showCrosshair() {
        View crosshair = new View(this);
        crosshair.setLayoutParams(new FrameLayout.LayoutParams(15, 15, Gravity.CENTER ));
        crosshair.setBackgroundColor(Color.GREEN );
        mapView.addView(crosshair);
    }

    private void enableLocationComponent(@NonNull Style loadedMapStyle){



        if(PermissionsManager.areLocationPermissionsGranted(this)){
            LocationComponent locationComponent = mapbox.getLocationComponent();

            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);

        }else{
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }


    }
    @SuppressWarnings("MissingPermission")
    public void initLocationEngine(){
        locationengine = LocationEngineProvider.getBestLocationEngine(this);

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();
        locationengine.requestLocationUpdates(request, callback, getMainLooper());
        locationengine.getLastLocation(callback);


    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapbox.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }


            });


        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private static class requestFormLocationCallback  implements LocationEngineCallback<LocationEngineResult> {

        static boolean streetfound = false;
        static String streetname;
        double radius= 1;
        private final WeakReference<requestForm> activityWeakReference;

        requestFormLocationCallback(requestForm activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }



        public void onSuccess(final LocationEngineResult  result) {
            requestForm activity = activityWeakReference.get();



            if (activity != null) {

                Location location = result.getLastLocation();

                if (location == null) {
                    return;
                }/*
                Toast.makeText(activity, String.format(activity.getString(R.string.new_location),
                        String.valueOf(result.getLastLocation().getLatitude()),
                        String.valueOf(result.getLastLocation().getLongitude())),
                        Toast.LENGTH_SHORT).show();*/

            }


            if (activity.mapbox != null && result.getLastLocation() != null  ){
                Location location = result.getLastLocation();
                activity.mapbox.getLocationComponent().forceLocationUpdate(result.getLastLocation());

                //FEED RIDER DATA TO FIREBASE//

                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CustomerRequest");
                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));




                /*
                String lat = String.valueOf()*/

                //FEED DATA TO FIREBASE//
                //getdriver info//
                //getdriverinfo//


                //GETTING STREET NAME//
                DatabaseReference dlocation = FirebaseDatabase.getInstance().getReference("Streetname");
                GeoFire custfire = new GeoFire(dlocation);
                GeoQuery geoQuery = custfire.queryAtLocation(new GeoLocation(location.getLatitude(), location.getLongitude()), radius);
                geoQuery.removeAllListeners();


                    geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                        @Override
                        public void onKeyEntered(String key, GeoLocation location) {
                            streetfound = true;
                            streetname = key;

                                locs = String.valueOf(streetname);




                        }

                        @Override
                        public void onKeyExited(String key) {

                        }

                        @Override
                        public void onKeyMoved(String key, GeoLocation location) {

                        }

                        @Override
                        public void onGeoQueryReady() {

                            if(!streetfound){
                                radius++;

                                DatabaseReference dlocation = FirebaseDatabase.getInstance().getReference("Streetname");

                                GeoFire custfire = new GeoFire(dlocation);
                                Location location1 = result.getLastLocation();
                                GeoQuery geoQuery = custfire.queryAtLocation(new GeoLocation(location1.getLatitude(), location1.getLongitude()), radius);
                                geoQuery.removeAllListeners();


                            }

                        }

                        @Override
                        public void onGeoQueryError(DatabaseError error) {

                        }
                    });

                //CHECK IF ANY AVAILABLE DRIVERS NEARBY//

            }




        }

        public void onFailure(@NonNull Exception exception) {
            Log.d("LocationChangeActivity", exception.getLocalizedMessage());
            requestForm activity = activityWeakReference.get();
            if (activity != null) {
                Toast.makeText(activity, exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressWarnings("MissingPermmisions")
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(!isloggingout){
            disconnect();

        }
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    private void disconnect(){
        locationengine.removeLocationUpdates(callback);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CustomerRequest");

        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    public void getUserLocation(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CustomerRequest");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lt = dataSnapshot.child(uid).child("l").child("0").getValue(Double.class);
                lg = dataSnapshot.child(uid).child("l").child("1").getValue(Double.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //<---------------------------------------------------------Saving Data ----------------------------------------------->

    //Add Request To Database.
    private void addRequest(){

        no_Riders = et_noP.getText().toString().trim();
        user_destination = et_destination.getText().toString().trim();
        requestCode = String.format("%04d", random.nextInt(10000));

        if(!TextUtils.isEmpty(no_Riders)){

            req_id = database_requestForm.push().getKey();
            addRequest add_req = new addRequest(req_id, uid, offline_BroadcastStatus, request_Status,no_Riders,millisInString, nearLoc, requestCode, lt, lg,user_destination);
            database_requestForm.child(req_id).setValue(add_req);/*
            Toast.makeText(this, "Request Made", Toast.LENGTH_SHORT).show();*/
            bt_submit.setEnabled(false);
            lt_cancel.setVisibility(View.VISIBLE);
            hasRequest = true;
            getdriver();
        } else {
            Toast.makeText(this, "Enter Details", Toast.LENGTH_SHORT).show();
        }
    }

    private void cancelRequest(){
        addRequest add_req = new addRequest(req_id, uid, no_Riders, "Cancelled","Cancelled",millisInString, nearLoc, requestCode);
        database_requestForm.child(req_id).setValue(add_req);
        Toast.makeText(this, "Cancelled Request", Toast.LENGTH_SHORT).show();
        bt_submit.setEnabled(true);
        et_noP.setText("");
        lt_cancel.setVisibility(View.INVISIBLE);
        hasRequest = true;
    }



    private void getdriver(){

        queryRequest = FirebaseDatabase.getInstance().getReference("requestForm");

            //get The REQUEST KEY OR ID
            queryRequest.orderByChild("request_Status").equalTo("Waiting Confirmation").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        status = dataSnapshot.child(req_id).child("request_Status").getValue(String.class);
                        tempPhoneNumber = dataSnapshot.child(req_id).child("driver_number").getValue(String.class);
                        goopen();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

    }

    private void acceptDriver(){

        safetycode = String.format("%04d", random.nextInt(10000));
        addRequest add_req = new addRequest(req_id, uid, "Accepted", "Accepted", no_Riders,millisInString, nearLoc, requestCode, tempPhoneNumber,  safetycode);
        database_requestForm.child(req_id).setValue(add_req);
        Toast.makeText(requestForm.this, "Driver Accepted", Toast.LENGTH_SHORT).show();
        lt_driverfound.setVisibility(View.INVISIBLE);
        lt_cancel.setVisibility(View.INVISIBLE);
        lt_currentdriver.setVisibility(View.VISIBLE);
        showPassengerCode();
        bt_submit.setEnabled(false);
    }

    private void declineDriver(){
        addRequest add_req = new addRequest(req_id, uid, no_Riders, "Pending","Pending",millisInString, nearLoc, requestCode);
        database_requestForm.child(req_id).setValue(add_req);
        Toast.makeText(requestForm.this, "Driver Declined", Toast.LENGTH_SHORT).show();
        lt_driverfound.setVisibility(View.INVISIBLE);
        bt_submit.setEnabled(true);
    }

    private void goopen(){
        if (status != null){
            lt_driverfound.setVisibility(View.VISIBLE);
            Toast.makeText(requestForm.this, "Driver Found", Toast.LENGTH_SHORT).show();
            tv_requestStatus.setText("Driver Found!");
        }

    }

    private void showPassengerCode(){
        databaseReference = FirebaseDatabase.getInstance().getReference("requestForm");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String safeCode = dataSnapshot.child(req_id).child("safety_Code").getValue(String.class);
                tv_safeCode.setText(safeCode);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void getStreets() {
        ss = FirebaseDatabase.getInstance().getReference("Streetname");

        //get The REQUEST KEY OR ID
        ss.orderByChild("g").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                streetlist.clear();
                distlist.clear();
                latlist.clear();
                lnglist.clear();
                for (DataSnapshot areaSnap : dataSnapshot.getChildren()) {
                    lat = areaSnap.getKey();
                    streetlist.add(lat);
                    Double tplt = areaSnap.child("l").child("0").getValue(Double.class);
                    latlist.add(tplt);
                    Double tplg = areaSnap.child("l").child("1").getValue(Double.class);
                    lnglist.add(tplg);
                    count = streetlist.size();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }


}




