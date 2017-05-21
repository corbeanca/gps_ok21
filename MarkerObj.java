package a_barbu.gps_agenda;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;

public class MarkerObj {

    double lat;
    double lng;
    String locality;
    SimpleDateFormat hour = new SimpleDateFormat("HH:mm");
    int passed = 0;
    int accuracy;
    int radius;
    int model;
    int  ID ;
    String memo;


    public void setID(int i) {
       ID = i;
    }

    public MarkerObj (double lat, double lng, String loc, int r, int a, SimpleDateFormat hour, String memo, int model){
        this.lat = lat;
        this.lng = lng;
        this.locality= loc;
        this.hour = hour;
        this.radius=r;
        this.accuracy=a;
        this.memo=memo;
        this.model=model;
    }

    public void Passed(){
        passed++;
    }
    public void ChangeHour(SimpleDateFormat newh){
        this.hour = newh;
    }
    public void MarkerUpdate (double lat, double lng, String loc){
        this.lat = lat;
        this.lng = lng;
        this.locality= loc;

    }
    public void ChangeMemo(String m){
        this.memo=m;
    }
}
