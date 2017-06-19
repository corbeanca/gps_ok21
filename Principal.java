package a_barbu.gps_agenda;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.text.format.Time;
import android.util.Log;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Principal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, AdapterView.OnItemClickListener {

    static List<MarkerObj> listaMarkere;
    static List<Itinerary> listaItinerarii;
    static String title;
    static String memo;
    GoogleMap mGoogleMap;
    static GoogleApiClient mGoogleApi;
    String locality;
    double lat;
    double lng;
    static int radius;
    static int accuracy;
    static int model;
    static int pinsize;
    BitmapDrawable bitmapdraw;
    TextView email;
    TextView name;
    ImageView photo;
    ImageView side;
    static boolean lista_display_dreapta = false;
    static FirebaseDatabase database = FirebaseDatabase.getInstance();
    static DatabaseReference Ref = database.getReference();
    ViewGroup l_sec;
    ViewGroup l_col;
    MarkerObj newMark;
    ListView listamarkerside;
    ListView lista_color;
    boolean[] itiswithMk;
    static boolean[] displayModel;
    static boolean showRadiusOnMarkers = false;
    static boolean lista_display_stanga=false;
    static int posI;
    static ArrayList<String> Markers;
    AdapterListaMark adapter;
    ImageView top_gif;
    static boolean service = false;
    static int temp_position;
    static int newID;


    static ArrayList<MarkerObj> displayOnMapList ;
    static ArrayList<CircleOptions> displayOnMapRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //testare conexiune google play services
        if (googleServicesAvailable()) {
            //      Toast.makeText(this, "Exista Google Play Services", Toast.LENGTH_SHORT).show();
            setContentView(R.layout.activity_principal);
            side = (ImageView) findViewById(R.id.sideLayout);
            side.setVisibility(View.INVISIBLE);
            l_sec = (ViewGroup) findViewById(R.id.l_secundar);
            l_col = (ViewGroup) findViewById(R.id.l_color);
            l_col.removeAllViews();
            l_sec.removeAllViews();

            top_gif =  ( pl.droidsonroids.gif.GifImageView) findViewById(R.id.toolbar_working);




            displayModel = new boolean[12];

            getDisplayMarker();

            String markers[] = getResources().getStringArray(R.array.markers);
            Markers = new ArrayList<>();
            for (int i = 0; i < markers.length; i++) {
                Markers.add(markers[i]);
            }

            initMap();

        } else {
            Toast.makeText(this, " Maps Service not supported", Toast.LENGTH_LONG).show();
        }
        model = ShowPref("model");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                writetoFile();
                //      addMarkerOnline(newMark);
                Intent intent = new Intent(Principal.this, GpsService.class);
                startService(intent);
                top_gif();
            }
        });
        importItinerary();
        importMarkers();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        name = (TextView) header.findViewById(R.id.U_name);
        email = (TextView) header.findViewById(R.id.U_email);
        photo = (ImageView) header.findViewById(R.id.U_photo);
        setHeader();

        radius = ShowPref("default_radius");
        accuracy = ShowPref("default_accuracy");
        pinsize = ShowPref("default_pinsize");



    }

    private void setHeader() {
        String x = ShowPref("Photo", 0);
        email.setText(ShowPref("Email", 0));
        name.setText(ShowPref("Name", 0));
        URL myUrl = null;
        try {
            myUrl = new URL(x);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        InputStream inputStream = null;
        try {
            inputStream = (InputStream) myUrl.getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Drawable drawable = Drawable.createFromStream(inputStream, null);
        photo.setImageDrawable(drawable);


    }

    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (marker !=null)
            remove();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // meniu tip harta
        getMenuInflater().inflate(R.menu.principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // map_type
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mapTypeNone:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            case R.id.mapTypeNormal:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mapTypeSatellite:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeTerrain:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.mapTypeHybrid:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    //aici e meniul stanga
    public boolean onNavigationItemSelected(MenuItem item) {
        //navigare pagini
        int id = item.getItemId();

        if (id == R.id.nav_main) {

        } else if (id == R.id.nav_pref) {
            startActivity(new Intent(Principal.this, Preferences.class));
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_config) {
            startActivity(new Intent(Principal.this, Config.class));
        } else if (id == R.id.nav_iti) {
            startActivity(new Intent(this, PopIti.class));
        } else if (id == R.id.nav_pin) {

            ClickOverlay();


        } else if (id == R.id.nav_signout) {
            FirebaseAuth.getInstance().signOut();
            SignIn.mAuth.signOut();
      //      SignIn.mAuth = null;
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor ed = sp.edit();

            ed.clear();
            ed.commit();
            Intent intent = new Intent(this, SignIn.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("Exit me", false);
            startActivity(intent);
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        List<String> strList = null;

        // aici se vor executa toate
        mGoogleMap = googleMap;
        //     if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        //       if (checkSelfPermission(Manifest.permission.ACC))
        if (mGoogleMap != null) {

            mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {
                    circle_radius.remove();

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    Geocoder gc = new Geocoder(Principal.this);
                    LatLng ll = marker.getPosition();
                    double lat = ll.latitude;
                    double lng = ll.longitude;
                    List<Address> list = null;
                    try {
                        list = gc.getFromLocation(lat, lng, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address add = list.get(0);
                    marker.setTitle(add.getLocality());
                    marker.showInfoWindow();
                    circle_radius = drawRadius(ll);
                    updateCoord(lat, lng);
                    // mai trebuie coborat 100 pixeli pe tinta
                }
            });

            mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getLayoutInflater().inflate(R.layout.pin_window, null);
                    TextView tvLocality = (TextView) v.findViewById(R.id.pin_w_locality);
                    TextView tvMemo = (TextView) v.findViewById(R.id.pin_w_memo);
                    if(memo !=null)
                        tvMemo.setText(memo);
                    tvMemo.setMaxWidth(500);

                    TextView tvTitle = (TextView) v.findViewById(R.id.pin_w_title);
                    if(title!=null)
                    tvTitle.setText(title);
                  //  tvTitle.setText("un titlu");

                    tvLocality.setText(marker.getTitle());
                    memo = marker.getSnippet();
                    tvMemo.setText(marker.getSnippet());

                    return v;
                }


            });
            mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {

                    startActivityForResult(new Intent(Principal.this, PopMarker.class), 101);
                }

            });
            mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    if ( lat !=0 && lng !=0){
                    newID = generateID();
                    MarkerObj newMarkerObj = new MarkerObj(lat, lng, locality, radius, accuracy, null, memo, model, newID);
                    newMarkerObj.setSize(pinsize);
                    addMarkerToItis();
                    Time today = new Time(Time.getCurrentTimezone());
                    today.setToNow();
                    String date= today.format("%H:%M:%S")+" on " +(today.monthDay + "/")+(Integer.toString(today.month+1)+"/")+(today.year);

                    newMarkerObj.setAdded(date);
                    Toast.makeText(Principal.this, "Marker added", Toast.LENGTH_LONG).show();
                    addMarkerOnline(newMarkerObj);
                    newMark = newMarkerObj;
                    itiswithMk = null;
                    itiswithMk = new boolean[listaItinerarii.size()];}
                 //   else {
//                        myMap.addMarker(new MarkerOptions()
//                                .position(point)
//                                .title(point.toString())
//                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
//
//                        Toast.makeText(getApplicationContext(),
//                                "New marker added@" + point.toString(), Toast.LENGTH_LONG)
//                                .show();
//                    }

                }
            });

        }

        mGoogleMap.setMyLocationEnabled(true);
        // zoomLocation(39.02, 89.3, 12);
        mGoogleApi = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApi.connect();
    }

    private void addMarkerOnline(MarkerObj newMO) {
        String email1 = ShowPref("Email", 1);
        DatabaseReference R_add = Ref.child(email1).child("Marker").child(String.valueOf(newMO.ID));
        R_add.setValue(newMO);
        // side layout -> add to itinerary also
    }

    private void updateCoord(double lat, double lng) {
        this.lng = lng;
        this.lat = lat;
    }

    private void zoomLocation(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mGoogleMap.moveCamera(update);
    }

    public boolean googleServicesAvailable() {
        //aici se verifica disponibilitate googleservice
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if
                (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else Toast.makeText(this, "Cannot connect to play services", Toast.LENGTH_LONG).show();
        return false;
    }

    Marker marker;

    public void geoLocate(View view) throws IOException {
// pentru butoane pot face view-uri separate, ca mai apoi sa apelez geogoder-ul. eventual un int cu optiunile.

        EditText et = (EditText) findViewById(R.id.search_field);
        String location = et.getText().toString();


        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(location, 1);
        if (list.isEmpty()) {
            Toast.makeText(this, "Invalid Name! Try again", Toast.LENGTH_SHORT).show();

        } else {
            side.setVisibility(View.VISIBLE);
            side.bringToFront();
            Address adress = list.get(0);

            String locality = adress.getLocality();
            double lat = adress.getLatitude();
            double lng = adress.getLongitude();
            zoomLocation(lat, lng, 15);
            this.locality = locality;
            this.lat = lat;
            this.lng = lng;

            setMarker_i(locality, lat, lng, memo);
        }
    }

    private void setMarker_i(String locality, double lat, double lng, String memo) {

        setMarker(locality, lat, lng, memo);
    }

    Circle circle_radius;

    private void setMarker(String locality, double lat, double lng, String memo) {
        if (marker != null) {
            remove();
        }

        setImage(model);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100 + 25 * pinsize, 110 + 25 * pinsize, false);
        //   EditText memo = (EditText) findViewById(R.id.pin_w_memo);
        //   memo.setText("test");
        MarkerOptions optionsMark = new MarkerOptions()
                .title(locality)
                .draggable(true)
                .position(new LatLng(lat, lng))
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                //memo pt snippet

                .snippet(memo);
        marker = mGoogleMap.addMarker(optionsMark);
        circle_radius = drawRadius(new LatLng(lat, lng));
    }

    private Circle drawRadius(LatLng latLng) {
        CircleOptions opt = new CircleOptions()
                .center(latLng)
                .radius(radius * 200)
                .fillColor(0x33BCCBD8)
                .strokeColor(Color.BLUE)
                .strokeWidth(4);
        return mGoogleMap.addCircle(opt);
    }

    private void remove() {
        marker.remove();
        marker = null;
        circle_radius.remove();
        circle_radius = null;
    }

    public void updateRadius() {
        circle_radius.remove();
        circle_radius = null;
        LatLng latLng = new LatLng(lat, lng);
        drawRadius(latLng);
    }

    @Override
    public void onClick(View v) {
        startActivityForResult(new Intent(Principal.this, PopMarker.class), 101);
    }

    LocationRequest mLocationRequest;

    @Override  // aici trebuie introduse preferintele de accuracy etc (sau serviciu)
    public void onConnected(@Nullable Bundle bundle) {
//        mLocationRequest = LocationRequest.create();
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(1000);
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApi, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Suspended", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Cannot connect to lcoation", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null)
            Toast.makeText(this, "can't get current location", Toast.LENGTH_LONG).show();
        else {
            LatLng ll = new LatLng((location.getLatitude()), location.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
            mGoogleMap.animateCamera(update);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//            if (requestCode== 102 &&resultCode==RESULT_OK)
        if (requestCode == 101 && resultCode == RESULT_OK) {
            memo = (data.getStringExtra("memo"));
            model = (data.getIntExtra("model", model));
            accuracy = (data.getIntExtra("accuracy", accuracy));
            radius = (data.getIntExtra("radius", radius));
            pinsize = (data.getIntExtra("pinsize", pinsize));
            //  updateRadius();
        }
        setMarker(this.locality, this.lat, this.lng, memo);
    }

    public int ShowPref(String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        return sp.getInt(key, 0);
    }

    public String ShowPref(String key, int a) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        return sp.getString(key, "var");
    }

    private void setImage(int position) {

        switch (position) {
            case 1://red green blue yell
                bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.mipmap.circle_red);
                break;
            case 2:
                bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.mipmap.circle_green);
                break;
            case 3:
                bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.mipmap.circle_blue);
                break;
            case 4:
                bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.mipmap.circle_yellow);
                break;
            case 5:
                bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.mipmap.square_red);
                break;
            case 6:
                bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.mipmap.square_green);
                break;
            case 7:
                bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.mipmap.square_blue);
                break;
            case 8:
                bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.mipmap.square_yellow);
                break;
            case 9:
                bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.mipmap.shield_red);
                break;
            case 10:
                bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.mipmap.shield_green);
                break;
            case 11:
                bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.mipmap.shield_blue);
                break;
            case 12:
                bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.mipmap.shield_yellow);
                break;
            default:
                bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.mipmap.shield_blue);
                break;

        }
    }

    public void SavePref(String key, int value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void ClickOverlay(){
        if (!lista_display_stanga) {
            lista_display_stanga = true;
            displayOnMapList = new ArrayList<>();
            displayOnMapRadius = new ArrayList<>();
            final View lay = getLayoutInflater().inflate(R.layout.side_marker_color, l_col);
            lista_color = (ListView) lay.findViewById(R.id.side_colorlist);
            Button close = (Button) findViewById(R.id.side_colorhide);
            Switch sw = (Switch) findViewById(R.id.side_color_sw);
            adapter = new AdapterListaMark(this, R.layout.side_list_item_marker, Markers);

            lay.bringToFront();
            lay.setVisibility(View.VISIBLE);

            if (showRadiusOnMarkers)
                sw.setChecked(true);
            else            sw.setChecked(false);

            lista_color.setAdapter(adapter);
            lista_color.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
 //                   Toast.makeText(Principal.this, "Shape changed for  " + position, Toast.LENGTH_SHORT).show();

                    if (displayModel[position])
                        displayModel[position] = false;
                    else displayModel[position] = true;
                    changeDisplayMarker(position);

                    adapter.notifyDataSetChanged();
                    updateView(position);

                }
            });
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lista_display_stanga = false;
                    lay.setVisibility(View.INVISIBLE);
                    l_col.removeAllViews();
                }
            });

        }
    }

    public void DisplayMarkersonMap() {
       if (displayOnMapList.size()!=0) {
            clearMarkersfromMap();


       }
        displayOnMapList = new ArrayList<>();
        displayOnMapRadius = new ArrayList<>();
        MarkerObj tempMk ;

        for (int index=0;index< listaMarkere.size();index++){
            tempMk= listaMarkere.get(index);
            LatLng tempLL = new LatLng(tempMk.lat,tempMk.lng);

            if (displayModel[tempMk.model])
            {CircleOptions tempC = new CircleOptions()
                    .center(tempLL)
                    .radius(200 * tempMk.radius)
                    .fillColor(0x33BCCBD8)
                    .strokeColor(Color.BLUE)
                    .strokeWidth(4);
                displayOnMapList.add(tempMk);
                displayOnMapRadius.add(tempC);
            }

        }
        if (displayOnMapList.size()!=0)
        for (int index=0;index<displayOnMapList.size();index++){
            tempMk = displayOnMapList.get(index);
            setImage(tempMk.model-1);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 40 + 20 * tempMk.size, 50 + 20 *tempMk.size , false);

            MarkerOptions optionsMark = new MarkerOptions()
                    .title(tempMk.locality)
                    .draggable(false)
                    .position(new LatLng(tempMk.lat, tempMk.lng))
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                    .snippet(tempMk.memo);

        mGoogleMap.addMarker(optionsMark);
            if (showRadiusOnMarkers)
                mGoogleMap.addCircle(displayOnMapRadius.get(index));


        }

    }
    public void UpdateDisplay(View v){
        DisplayMarkersonMap();
    }

    private void clearMarkersfromMap() {
        displayOnMapList = null;
        displayOnMapRadius = null;
        mGoogleMap.clear();
    }

    private void updateView(int index){
        View v = lista_color.getChildAt(index -
                lista_color.getFirstVisiblePosition());

        if(v == null)
            return;

    }

    public void ClickSide(View v) {
        readfromFile();

        if (!lista_display_dreapta) {
            itiswithMk = new boolean[listaItinerarii.size()];

            temp_position=0;
            lista_display_dreapta = true;
            final View lay = getLayoutInflater().inflate(R.layout.side_principal, l_sec);
            listamarkerside = (ListView) lay.findViewById(R.id.side_ITIlist);
            final ImageView plus = (ImageView) findViewById(R.id.side_plus);
            final ImageView minus = (ImageView) findViewById(R.id.side_minus);
            final TextView text_info = (TextView) findViewById(R.id.side_principal_text);
            Button save = (Button) findViewById(R.id.side_principal_save);
            plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itiswithMk[temp_position]=true;
                    minus.setClickable(true);
                    plus.setImageResource(R.mipmap.iti_plus_g);
                    minus.setImageResource(R.mipmap.iti_minus);
                    plus.setClickable(false);
                    Toast.makeText(Principal.this, "Plus a mers ", Toast.LENGTH_SHORT).show();
                }
            });
            minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itiswithMk[temp_position]=false;
                    minus.setClickable(false);
                    plus.setClickable(true);
                    plus.setImageResource(R.mipmap.iti_plus);
                    minus.setImageResource(R.mipmap.iti_minus_g);
                    Toast.makeText(Principal.this, "Minus a mers ", Toast.LENGTH_SHORT).show();
                }
            });
      //      Switch sw = (Switch) findViewById(R.id.side_color_sw);
           final ArrayAdapter<Itinerary> adapterSide = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaItinerarii);

            lay.bringToFront();
            lay.setVisibility(View.VISIBLE);
            listamarkerside.setAdapter(adapterSide);
            listamarkerside.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String temp = listaItinerarii.get(position).name;
                    if (listaItinerarii.get(position).date.equals(""))
                        temp=temp+"\n(no date yet)";
                    else temp=temp+"\n on "+listaItinerarii.get(position).date;
                    text_info.setText(temp);
                    temp_position=position;
                    if (itiswithMk[position]){
                        minus.setClickable(true);
                        plus.setImageResource(R.mipmap.iti_plus_g);
                        minus.setImageResource(R.mipmap.iti_minus);
                        plus.setClickable(false);
                    }
                    else{minus.setClickable(false);
                        plus.setClickable(true);
                        plus.setImageResource(R.mipmap.iti_plus);
                        minus.setImageResource(R.mipmap.iti_minus_g);
                    }
                    adapterSide.notifyDataSetChanged();
                }
            });
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lista_display_dreapta = false;
                    lay.setVisibility(View.INVISIBLE);
                    l_sec.removeAllViews();
                    side.setVisibility(View.INVISIBLE);
                }
            });
        }

    }

    public void changeDisplayRadius (View v){
        if (showRadiusOnMarkers)
            showRadiusOnMarkers=false;
        else            showRadiusOnMarkers=true;

    }
//de pus email citit
    public void importMarkers() {
        listaMarkere = new ArrayList<>();
        Ref.child("alexandrubarbu93@gmail,com").child("Marker")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) {
                            MarkerObj value = child.getValue(MarkerObj.class);
                            listaMarkere.add(value);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
        Toast.makeText(this, "Markers imported", Toast.LENGTH_SHORT).show();
    }

    public void DisplayMarkers(List markerlist) {
        for (int i = 1; i < 5; i++) {
            MarkerObj temp = (MarkerObj) markerlist.get(i);
            Toast.makeText(this, "un obiect" + temp.locality, Toast.LENGTH_SHORT).show();
        }
    }

    // on click pentru lista de (itinerii)
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    // markerul e adaugat. apare butonul de layout dreapta cu itinerarii existente. in layoutul din dreapta click pe un itinerariu, este marcat
    // apoi click pe plus si-l adauga la iti respectiv. cand dau click pe linia din lista verifica daca e prezent. daca este, apare minusul
    //si dispare plusul.
    private boolean checkifpresent(int pos, MarkerObj mk) {
        Itinerary it = listaItinerarii.get(pos);
        if (it.HasMk(it, mk))
            return true;
        return false;
    }

    public void importItinerary() {
        listaItinerarii = new ArrayList<>();
        Ref.child("alexandrubarbu93@gmail,com").child("Itinerary")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) {
                            Itinerary value = child.getValue(Itinerary.class);
                            listaItinerarii.add(value);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
        //    Toast.makeText(this, "Itineraries imported", Toast.LENGTH_LONG).show();

    }

    public static void removeIti(int pos) {
        listaItinerarii.remove(pos);
        posI = pos;
        //static DatabaseReference Ref = FirebaseDatabase.getInstance();
        Ref.child("alexandurbarbu93@gmail,com").child("Itinerary").child(Integer.toString(pos)).removeValue();
        //  Toast.makeText(this,"deleted itinerary from DB",Toast.LENGTH_SHORT).show();
    }

    public void writetoFile() {
        String filename = "MarkersList.txt";
        ObjectOutput out = null;

        try {
            out = new ObjectOutputStream(new FileOutputStream(new File(getFilesDir(), "") + File.separator + filename));
            out.writeObject(listaMarkere);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "Done Writing objects to file", Toast.LENGTH_SHORT).show();
//        Log.v("Mkto_file_from_online", "lista Marker =" + listaMarkere);
    }

    public void readfromFile() {
        ObjectInputStream input;
        String filename = "MarkersList.txt";

        try {
            input = new ObjectInputStream(new FileInputStream(new File(new File(getFilesDir(), "") + File.separator + filename)));

//            MarkerObj mkob = (MarkerObj) input.readObject();
            listaMarkere = (List<MarkerObj>) input.readObject();
 //           Log.v("serialization", "lista Marker =" + listaMarkere);
            input.close();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void getDisplayMarker(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();

        for (int i=0;i<12;i++){
            boolean x = sp.getBoolean("display_by_type"+(Integer.toString(i)), false);
        if (x) {
          //    editor.putBoolean("display_by_type"+(Integer.toString(i)),true);
              displayModel[i]=true;
        }
       else {
            displayModel[i]=false;  }
 //           Log.v("Marker "+i + " - ", Boolean.toString(x)   );
        }

editor.commit();

    }
    //se apeleaza la fiecare check in lista marker color
    public void changeDisplayMarker(int i){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        if ( displayModel[i]) {
            editor.putBoolean("display_by_type" + (Integer.toString(i)), true);
        }
        else if (!displayModel[i]){
        editor.putBoolean("display_by_type"+(Integer.toString(i)),false);
           }
        Log.v("display changed >", (Boolean.toString (sp.getBoolean("display_by_type"+(Integer.toString(i)) , false) )      ));
        editor.commit();
    }

    public void addMarkerToItis(){
        for(int i=0;i<listaItinerarii.size();i++){
            if (itiswithMk[i])
                listaItinerarii.get(i).addID(newID);
        }
    }
//modificat email
    public int generateID(){

        String ID = Ref.child("alexandrubarbu93@gmail,com").child("Last ID").getKey();
        int id = Integer.parseInt(ID);
        Ref.child("alexandrubarbu93@gmail,com").child("Last ID").setValue(id+1);
        return id;


    }

    public static class AdapterListaMark extends ArrayAdapter<String>{

        private int layout;

        private AdapterListaMark(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            layout = resource;

        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder mainViewholder = null;
            if(convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.side_list_marker_img);

                viewHolder.title = (TextView) convertView.findViewById(R.id.side_list_text);
                viewHolder.check = (CheckBox) convertView.findViewById(R.id.side_list_check);


                if (displayModel[position])
                    viewHolder.check.setChecked(true);
                    else viewHolder.check.setChecked(false);

                convertView.setTag(viewHolder);
            }

            mainViewholder = (ViewHolder) convertView.getTag();
            mainViewholder.title.setText(getItem(position));

            if (displayModel[position])
                mainViewholder.check.setChecked(true);
            else mainViewholder.check.setChecked(false);



            switch (position){
                case 0://red green blue yell
                    mainViewholder.thumbnail.setImageResource(R.mipmap.circle_red);
                    break;
                case 1:
                    mainViewholder.thumbnail.setImageResource(R.mipmap.circle_green);
                    break;
                case 2:
                    mainViewholder.thumbnail.setImageResource(R.mipmap.circle_blue);
                    break;
                case 3:
                    mainViewholder.thumbnail.setImageResource(R.mipmap.circle_yellow);
                    break;
                case 4:
                    mainViewholder.thumbnail.setImageResource(R.mipmap.square_red);
                    break;
                case 5:
                    mainViewholder.thumbnail.setImageResource(R.mipmap.square_green);
                    break;
                case 6:
                    mainViewholder.thumbnail.setImageResource(R.mipmap.square_blue);
                    break;
                case 7:
                    mainViewholder.thumbnail.setImageResource(R.mipmap.square_yellow);
                    break;
                case 8:
                    mainViewholder.thumbnail.setImageResource(R.mipmap.shield_red);
                    break;
                case 9:
                    mainViewholder.thumbnail.setImageResource(R.mipmap.shield_green);
                    break;
                case 10:
                    mainViewholder.thumbnail.setImageResource(R.mipmap.shield_blue);
                    break;
                case 11:
                    mainViewholder.thumbnail.setImageResource(R.mipmap.shield_yellow);
                    break;

            }
            return convertView;
        }
//        public void changeCheck(int position){
//            if (displayModel[position])
//        }

    }

    public static class ViewHolder {
        ImageView thumbnail;
        TextView title;
        CheckBox check;
    }

    public void top_gif(){
     //   top_gif=new  pl.droidsonroids.gif.GifImageView (R.id.toolbar_working);
        if (service)
            top_gif.setImageResource(R.mipmap.stop);
        else top_gif.setImageResource(R.mipmap.load4);
    }
}
















