package a_barbu.gps_agenda;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class Principal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {



    String memo;
    GoogleMap mGoogleMap ;
    GoogleApiClient mGoogleApi;
    String locality;
    double lat;
    double lng;
    int radius;
    int accuracy;
    int model;
    int pinsize;
    BitmapDrawable bitmapdraw;
    TextView email;
    TextView name;
    ImageView photo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    //testare conexiune google play services
    if (googleServicesAvailable()) {
  //      Toast.makeText(this, "Exista Google Play Services", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_principal);
        initMap();
    }
        else {
        Toast.makeText(this, " Maps Service not supported",Toast.LENGTH_LONG).show();
    }
        model = ShowPref("model");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "PIN Saved", Snackbar.LENGTH_SHORT)
//                        .setAction("Action", null).show();
                startActivityForResult(new Intent(Principal.this,PopMarker.class),101);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header=navigationView.getHeaderView(0);
        name = (TextView) header.findViewById(R.id.U_name);
        email = (TextView) header.findViewById(R.id.U_email);
        photo = (ImageView) header.findViewById(R.id.U_photo);
        setHeader();

        radius=ShowPref("default_radius");
        accuracy=ShowPref("default_accuracy");
        pinsize=ShowPref("default_pinsize");

           }

    private void setHeader() {
        String x=ShowPref("Photo",0);
        email.setText(ShowPref("Email",0));
        name.setText(ShowPref("Name",0));
        URL myUrl = null;
        try {
            myUrl = new URL(x);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        InputStream inputStream = null;
        try {
            inputStream = (InputStream)myUrl.getContent();
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
        switch (item.getItemId()){
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
    public boolean onNavigationItemSelected(MenuItem item) {
        //navigare pagini
        int id = item.getItemId();

        if (id == R.id.nav_main) {

        } else if (id == R.id.nav_pref) {
            startActivity(new Intent(Principal.this,Preferences.class));
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_config) {
            startActivity(new Intent(Principal.this,Config.class));
        } else if (id == R.id.nav_iti) {

        } else if (id == R.id.nav_pin) {

        }
        else if (id== R.id.nav_signout){
            FirebaseAuth.getInstance().signOut();
            SignIn.mAuth=null;

            SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor ed = sp.edit();

            ed.clear();
            ed.commit();

            startActivity(new Intent(Principal.this, SignIn.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // aici se vor executa toate
        mGoogleMap = googleMap;
   //     if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
     //       if (checkSelfPermission(Manifest.permission.ACC))
        if (mGoogleMap !=null){

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
                        list = gc.getFromLocation(lat,lng,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address add = list.get(0);
                    marker.setTitle(add.getLocality());
                    marker.showInfoWindow();
                    circle_radius=drawRadius(ll);
                    updateCoord(lat,lng);
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
                    View v = getLayoutInflater().inflate(R.layout.pin_window,null);
                    TextView tvLocality = (TextView) v.findViewById(R.id.pin_w_locality);
                    EditText tvSnippet = (EditText) v.findViewById(R.id.pin_w_memo);
                    // if(memo !=null)

                    tvLocality.setText(marker.getTitle());
                    tvSnippet.setText(marker.getSnippet());

                    return v;
                }


            });
            mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {

                          startActivityForResult(new Intent(Principal.this,PopMarker.class),101);
                       }

            });
            mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {

                    MarkerObj newMarkerObj = new MarkerObj(lat,lng,locality,radius,accuracy,null,memo,model);
                    int id = ShowPref("ID");
                    newMarkerObj.setID(id);
                    id=id+1;
                    SavePref("ID",id);
                    Toast.makeText(Principal.this, "Marker added" , Toast.LENGTH_LONG).show();
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

    private void updateCoord(double lat, double lng) {
        this.lng=lng;
        this.lat=lat;
    }

    private void zoomLocation(double lat, double lng, float zoom){
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
            }
            else Toast.makeText(this, "Cannot connect to play services", Toast.LENGTH_LONG).show();
            return false;
        }

        Marker marker;

    public void geoLocate(View view) throws IOException {
        EditText et = (EditText) findViewById(R.id.search_field);
        String location = et.getText().toString();

        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(location,1);
        Address adress = list.get(0);
        String locality = adress.getLocality();

        double lat = adress.getLatitude();
        double lng = adress.getLongitude();
        zoomLocation(lat, lng, 15);
        this.locality = locality;
        this.lat= lat;
        this.lng = lng;

        setMarker_i(locality, lat, lng,memo);
    }

    private void setMarker_i(String locality, double lat, double lng, String memo) {

        setMarker(locality, lat, lng,memo);
    }

    Circle circle_radius;
    private void setMarker(String locality, double lat, double lng,String memo) {
        if (marker != null){
            remove();
        }

        setImage(model);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100 + 100*pinsize, 100 +100*pinsize, false);
     //   EditText memo = (EditText) findViewById(R.id.pin_w_memo);
     //   memo.setText("test");

        MarkerOptions optionsMark = new MarkerOptions()
                .title(locality)
                .draggable(true)
                .position(new LatLng( lat, lng))
               .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                //memo pt snippet

                .snippet(memo)
        ;
        marker = mGoogleMap.addMarker(optionsMark);
        circle_radius = drawRadius(new LatLng( lat, lng));
    }

    private Circle drawRadius(LatLng latLng) {
        CircleOptions opt = new CircleOptions()
                .center(latLng)
                .radius(radius*200)
                .fillColor(0x33BCCBD8)
                .strokeColor(Color.BLUE)
                .strokeWidth(4);
        return mGoogleMap.addCircle(opt);
    }

    private void remove(){
        marker.remove();
        marker = null;
        circle_radius.remove();
        circle_radius = null;
    }

    public void updateRadius(){
        circle_radius.remove();
        circle_radius = null;
        LatLng latLng = new LatLng(lat,lng);
        drawRadius(latLng);
        }
    @Override
    public void onClick(View v) {
        startActivityForResult(new Intent(Principal.this,PopMarker.class),101);
    }
LocationRequest mLocationRequest;

    @Override  // aici trebuie introduse preferintele de accuracy etc (sau serviciu)
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        LocationServices.FusedLocationApi.requestLocationUpdates( mGoogleApi,mLocationRequest, this);
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
            if ( location == null )
                Toast.makeText(this, "can't get current location", Toast.LENGTH_LONG).show();
        else {
                LatLng ll = new LatLng((location.getLatitude()),location.getLongitude());
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
                mGoogleMap.animateCamera(update);
            }


    }

//    public Bitmap getBitmap(int drawableRes) {
//        Drawable drawable = getResources().getDrawable(drawableRes);
//        Canvas canvas = new Canvas();
//        Bitmap bitmap = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);
//        canvas.setBitmap(bitmap);
//        drawable.setBounds(0,0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//
//        drawable.draw(canvas);
//       return bitmap;
//
//    }

//    public static Bitmap scaleBitmap(Bitmap bitmap, int wantedWidth, int wantedHeight) {
//        Bitmap output = Bitmap.createBitmap(wantedWidth, wantedHeight, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(output);
//        Matrix m = new Matrix();
//        m.setScale((float) wantedWidth / bitmap.getWidth(), (float) wantedHeight / bitmap.getHeight());
//        canvas.drawBitmap(bitmap, m, new Paint());
//
//        return output;
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//            if (requestCode== 102 &&resultCode==RESULT_OK)
//
//            if (requestCode== 103 &&resultCode==RESULT_OK)
//            if (requestCode== 104 &&resultCode==RESULT_OK)
//            if (requestCode== 105 &&resultCode==RESULT_OK)
//            if (requestCode== 106 &&resultCode==RESULT_OK)

            if(requestCode== 101 &&resultCode == RESULT_OK) {
                memo = (data.getStringExtra("memo"));
                model= (data.getIntExtra("model",model));
                accuracy=(data.getIntExtra("accuracy",accuracy));
                radius=(data.getIntExtra("radius",radius));
                pinsize=(data.getIntExtra("pinsize",pinsize));
              //  updateRadius();
            }

        setMarker(this.locality, this.lat, this.lng, memo);


        //super.onActivityResult(requestCode, resultCode, data);
    }
    public int ShowPref(String key){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        return sp.getInt(key,1);
    }
    public String ShowPref(String key,int a){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        return sp.getString(key,"var");
    }

    private void setImage(int position) {

        switch (position){
            case 1://red green blue yell
                bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.mipmap.circle_red);
                break;
            case 2:
                bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.mipmap.circle_green);
                break;
            case 3:
                bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.mipmap.circle_blue);
                break;
            case 4:
                bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.mipmap.circle_yellow);
                break;
            case 5:
                bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.mipmap.square_red);
                break;
            case 6:
                bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.mipmap.square_green);
                break;
            case 7:
                bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.mipmap.square_blue);
                break;
            case 8:
                bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.mipmap.square_yellow);
                break;
            case 9:
                bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.mipmap.shield_red);
                break;
            case 10:
                bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.mipmap.shield_green);
                break;
            case 11:
                bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.mipmap.shield_blue);
                break;
            case 12:
                bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.mipmap.shield_yellow);
                break;
            default:
                bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.mipmap.shield_blue);
                break;

        }
    }

    public void SavePref(String key, int value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }
}