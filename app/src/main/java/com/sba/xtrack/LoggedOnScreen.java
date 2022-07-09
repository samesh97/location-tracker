package com.sba.xtrack;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.se.omapi.SEService;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.aviran.cookiebar2.CookieBar;
import org.aviran.cookiebar2.OnActionClickListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.sba.xtrack.R.layout.card_layout;
import static com.sba.xtrack.R.layout.listview;

public class LoggedOnScreen extends AppCompatActivity implements RecyclerViewAdapter.MembersViewHolder.OnItemClickListener
        , NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, LocationListener {

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference,databaseReference2;
    FirebaseUser user;
    DatabaseReference getLatLng;
    ProgressDialog dialog;
    RecyclerView.LayoutManager layoutManager;

    Boolean locationEnabled,checkPermission;
    LocationManager locationManager;
    String cityName;

    int itemCount = 0;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
        {
            checkPermission = false;

        }
        else
        {
            checkPermission = true;
        }
    }

    TextView nameTextView,emailTextView;

    String CurrentUserName,CurrentUserEmail;

    private GoogleMap mMap;
    GoogleApiClient client;
    LocationRequest request;

    double usersLatitude;
    double usersLongitude;

    RecyclerView recyclerView;


    String trackingUsersLatitude,trackingUsersLongitude;

    LatLng userLocation;

    MarkerOptions options;
    Boolean isFirstTime = true;

    final List<String> usersIds = new ArrayList<>();
    final List<String> trackingNames = new ArrayList<>();

    final List<Double> trackingLat = new ArrayList<>();
    final List<Double> trackingLon = new ArrayList<>();
    RecyclerViewAdapter adaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_on_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dialog = new ProgressDialog(LoggedOnScreen.this);
        dialog.setMessage("Getting Things Ready..");
        dialog.setCancelable(false);
        dialog.show();


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        //ecyclerView.setHasFixedSize(true);















        /* Check GPS Enabled or Not  */

        locationEnabled = isLocationEnabled();
        if(!locationEnabled)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoggedOnScreen.this);
            builder.setTitle("GPS is not enabled")
                    .setMessage("Open location settings")
                    .setPositiveButton("Open",
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                    .setNegativeButton("cancel",
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }


            /* OVER */



        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        String UID = firebaseAuth.getUid();



        Intent intent = getIntent();
        String userId = intent.getStringExtra("UsersID");



        /*Google Map */

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);





        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        emailTextView = header.findViewById(R.id.CurrentUserEmail);
        nameTextView = header.findViewById(R.id.CurrentUserName);


        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                CurrentUserName = dataSnapshot.child(user.getUid()).child("name").getValue(String.class);
                CurrentUserEmail = dataSnapshot.child(user.getUid()).child("email").getValue(String.class);

                emailTextView.setText(CurrentUserEmail);
                nameTextView.setText(CurrentUserName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.logged_on_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
         //   return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.addToTrack)
        {
            Intent intent = new Intent(LoggedOnScreen.this,AddToTrack.class);
            startActivity(intent);

        }
        else if (id == R.id.trackingList)
        {
            Intent intent = new Intent(LoggedOnScreen.this,TrackingList.class);
            startActivity(intent);
        }
        else if (id == R.id.whoTracksMe)
        {

        }
        else if (id == R.id.nav_logout)
        {
            FirebaseUser user = firebaseAuth.getCurrentUser();

            if (user != null) {
                firebaseAuth.signOut();
                Intent intent = new Intent(LoggedOnScreen.this, LoginScreen.class);
                startActivity(intent);
                finish();

            }
            else
            {
                Intent intent = new Intent(LoggedOnScreen.this, LoginScreen.class);
                startActivity(intent);
                finish();
            }

        }
        else if (id == R.id.nav_inviteCode)
        {
            Intent intent = new Intent(LoggedOnScreen.this,InviteCodeActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        /*Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        client.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        request = new LocationRequest().create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(1000);



        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }

    @Override
    public void onLocationChanged(Location location)
    {


        if(location == null)
        {
            Toast.makeText(LoggedOnScreen.this,"Unable to get Location!",Toast.LENGTH_SHORT).show();
        }
        else
        {
            if(isFirstTime)
            {
                userLocation = new LatLng(location.getLatitude(),location.getLongitude());
                options = new MarkerOptions();
                options.position(userLocation);
                options.title("Your Location");
                mMap.addMarker(options);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
                mMap.animateCamera( CameraUpdateFactory.zoomTo( 15.0f ));




            }

            usersLatitude = location.getLatitude();
            usersLongitude = location.getLongitude();

            /* UPDATING LOCATION */



           // if(firebaseAuth.getCurrentUser()= null)
            //{
            if(firebaseAuth.getCurrentUser() == null)
            {
                return;
            }
            else
            {
                getLatLng = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
                UpdateLatLng updateLatLng = new UpdateLatLng(usersLatitude);
                final UpdateLatLng updateLatLng1 = new UpdateLatLng(usersLongitude);


                FirebaseDatabase.getInstance().getReference("Users")
                        .child(firebaseAuth.getCurrentUser().getUid()).child("Latitude")
                        .setValue(updateLatLng).addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            if(firebaseAuth.getCurrentUser() == null)
                            {
                                Toast.makeText(LoggedOnScreen.this,"User is null!",Toast.LENGTH_SHORT).show();
                                return;

                            }
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(firebaseAuth.getCurrentUser().getUid()).child("Longitude")
                                    .setValue(updateLatLng1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                   if(task.isCanceled())
                                   {
                                       Toast.makeText(LoggedOnScreen.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                                   }

                                }
                            });
                        }
                    }
                });
            }



            /*UPDATING LOCATION WAS OVER */

            if(firebaseAuth.getCurrentUser() == null)
            {
                Toast.makeText(LoggedOnScreen.this,"Could Not Update Location!",Toast.LENGTH_SHORT).show();
                return;
            }
            else
            {
                if(isFirstTime)
                {
                    isFirstTime = false;
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("JoinedMembers");

                    Query query = databaseReference.orderByKey();

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            if(dataSnapshot.exists())
                            {
                                for(DataSnapshot dss: dataSnapshot.getChildren())
                                {
                                    String ID = dss.child("joinedUserId").getValue(String.class);

                                    databaseReference2 = FirebaseDatabase.getInstance().getReference().child("Users").child(ID);

                                    databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                        {
                                            String Na = dataSnapshot.child("name").getValue(String.class);
                                            trackingNames.add(Na);

                                            adaptor.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                    databaseReference2.child("Latitude").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                        {
                                            if(dataSnapshot.exists())
                                            {
                                                Double lat = dataSnapshot.child("latitude").getValue(Double.class);
                                                trackingLat.add(lat);
                                                adaptor.notifyDataSetChanged();

                                            }
                                            else
                                            {
                                                trackingLat.add(0.0);
                                            }


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    databaseReference2.child("Longitude").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                        {
                                            if(dataSnapshot.exists())
                                            {
                                                Double lon = dataSnapshot.child("latitude").getValue(Double.class);
                                                trackingLon.add(lon);

                                            }
                                            else
                                            {
                                                trackingLon.add(0.0);
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                                    usersIds.add(ID);


                                    for(int y=0; y<=usersIds.size()-1; y++)
                                    {
                                        getUsersLocation(usersIds.get(y));

                                    }
                            /*

                            double latitude =  Double.parseDouble(trackingUsersLatitude);
                            double longitude = Double.parseDouble(trackingUsersLongitude);*/

                                    // create marker
                            /*MarkerOptions marker = new MarkerOptions().position(new LatLng(7.786666, 85.98766));

                            mMap.addMarker(marker);
                            // adding marker


                            /*
                            Toast.makeText(LoggedOnScreen.this,ID,Toast.LENGTH_SHORT).show();*/
                                }


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError)
                        {

                        }
                    });

                }



                //listView.invalidate();



            }




        }
        if(adaptor == null)
        {



                layoutManager = new LinearLayoutManager(this);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setVisibility(View.VISIBLE);
                adaptor = new RecyclerViewAdapter(trackingNames,LoggedOnScreen.this);
                recyclerView.setAdapter(adaptor);
                adaptor.notifyDataSetChanged();
                dialog.dismiss();




        }
        else
        {
            adaptor.notifyDataSetChanged();
            dialog.dismiss();
        }






    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }
    public void getUsersLocation(final String userID)
    {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                double latitude = dataSnapshot.child(userID).child("Latitude").child("latitude").getValue(Double.class);
                double longitude  = dataSnapshot.child(userID).child("Longitude").child("latitude").getValue(Double.class);

                String name = dataSnapshot.child(userID).child("name").getValue(String.class);

                MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude,longitude)).title(name);
                LatLng latLng = new LatLng(latitude,longitude);
                mMap.addMarker(marker);
                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));


                //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                //mMap.animateCamera( CameraUpdateFactory.zoomTo( 15.0f ));



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }
    protected boolean isLocationEnabled()
    {
        String le = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(le);
        if(!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public synchronized void onItemClick(int position)
    {
        try
        {
            if(position >= 0)
            {
                LatLng clickedLocation = new LatLng(trackingLat.get(position),trackingLon.get(position));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(clickedLocation));
                mMap.animateCamera( CameraUpdateFactory.zoomTo( 15.0f ));

                getAddressFromLatitude(clickedLocation);

                CookieBar.build(LoggedOnScreen.this)
                        .setTitle("User Current Location")
                        .setCookiePosition(CookieBar.TOP)
                        .setBackgroundColor(R.color.colorPrimary)
                        .setTitleColor(R.color.white)
                        .setIcon(R.drawable.ic_person)
                        //.setIconAnimation(R.animator.spin)
                        .setMessage(cityName)
                        .setDuration(5000)
                        .show();


            }
        }
        catch(Exception e)
        {

        }


    }
    public synchronized void getAddressFromLatitude(LatLng MyLat) throws IOException
    {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(MyLat.latitude, MyLat.longitude, 1);
        cityName = addresses.get(0).getAddressLine(0);
        String stateName = addresses.get(0).getAddressLine(1);
        String countryName = addresses.get(0).getAddressLine(2);
    }
}

