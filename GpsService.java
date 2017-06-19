package a_barbu.gps_agenda;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GpsService extends Service implements ResultCallback<Status>, LocationListener {

    private static String TAG;
    private LocationListener listener;
    private LocationManager locationManager;
    static List<MarkerObj> listaMarkere;
    static List<Geofence> listaGeofence;
    static Date start;
    static Date stop;
    static Date date;
    GoogleApiClient googleApiClient;
    SimpleDateFormat inputParser = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public GpsService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

//        SimpleDateFormat time_now = new SimpleDateFormat("HH:mm");
//        Time today = new Time(Time.getCurrentTimezone());
//        today.setToNow();
//        String date= today.format("%H:%M");
//        Date present = null;
//        try{
//            present = (time_now.parse(date));
//        }
//        catch (ParseException e){
//            Log.v("OFFLINE  ", "  EXCEPTIEEEE");
//        }
        start = parseDate(showH(1));
        stop = parseDate(showH(2));

        if (compareDates()){
            Principal.service=false;
           // Log.v("OFFLINE  ", "  OFFLINE");
            Toast.makeText(this, "Service ONLINE", Toast.LENGTH_SHORT).show();}
        else {
            Log.v("ONLINE  ", "  ONLINE");
            Toast.makeText(this, "Service ONLINE", Toast.LENGTH_SHORT).show();
            Principal.service=true;
            readfromFile();
            geoF();
            googleApiClient = Principal.mGoogleApi;
            listener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    startLocationUpdates();
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {
                    Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);

                }
            };
            locationManager = (LocationManager) getApplication().getSystemService(Context.LOCATION_SERVICE);

            //aici se poate schimba pentru distanta parcursa \/ plus timpul de optimizare

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, listener);


            // Geofence g = listaGeofence.get(0);

            GeofencingRequest GeoReq = new GeofencingRequest.Builder()
                    .addGeofences(listaGeofence)
                    .build();

            addGeofence(GeoReq);

            //trebuie DWELL nu ENTER, ca poate sta mai mult acolo (pot folosi si durata)
        }
    }
    private void addGeofence(GeofencingRequest request) {
        LocationServices.GeofencingApi.addGeofences(
                googleApiClient,
                request,
                createGeofencePendingIntent()
        ).setResultCallback(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null)
            locationManager.removeUpdates(listener);
    }

    private LocationRequest locationRequest;
    // Defined in mili seconds.
    // This number in extremely low, and should be used only for debug
    private final int UPDATE_INTERVAL = 1000;
    private final int FASTEST_INTERVAL = 900;

    private void startLocationUpdates() {
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

    }


    private PendingIntent geoFencePendingIntent;
    static Intent makeNotificationIntent(Context geofenceService, String msg)
    {
        Log.d(TAG,msg);
        return new Intent(geofenceService,GpsService.class);
    }
    private final int GEOFENCE_REQ_CODE = 0;

    private PendingIntent createGeofencePendingIntent() {
        if (geoFencePendingIntent != null)
            return geoFencePendingIntent;

        Intent intent = new Intent(this, GeofenceTransition.class);
        return PendingIntent.getService(
                this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    public void readfromFile() {
        ObjectInputStream input;
        String filename = "MarkersList.txt";

        try {
            input = new ObjectInputStream(new FileInputStream(new File(new File(getFilesDir(), "") + File.separator + filename)));

//            MarkerObj mkob = (MarkerObj) input.readObject();
            listaMarkere = (List<MarkerObj>) input.readObject();
            Log.v("serialization_from_file", "lista Marker =" + listaMarkere);
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

    public void geoF() {
        listaGeofence = new ArrayList<>();
        MarkerObj mkob;
        for (int i = 0; i < listaMarkere.size(); i++) {
            mkob = listaMarkere.get(i);
            listaGeofence.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(Integer.toString(mkob.ID))
                    .setExpirationDuration(120000)
                    .setCircularRegion(
                            mkob.lat,
                            mkob.lng,
                            mkob.radius * 100
                    )
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }
    }

    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {

        } else {
            // inform about fail
        }
    }

    @Override
    public void onLocationChanged(Location location) {

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

    private boolean compareDates(){
        Calendar now = Calendar.getInstance();

        int hour = now.get(Calendar.HOUR);
        int minute = now.get(Calendar.MINUTE);

        date = parseDate(hour + ":" + minute);

        if ( stop.before( start ) && date.before(stop))
            return true;

        if (date.before(stop) && date.after(start))
            return true;

        return false;
    }

    private Date parseDate(String date) {

        try {
            return inputParser.parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }
//
//    public void SavePref(String key, int value) {
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putInt(key, value);
//        editor.commit();
//    }

    private String showH(int i) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (i == 1)
            return  sp.getString("hour_start", "08:00");
        else return  sp.getString("hour_stop", "22:00");


    }
}
