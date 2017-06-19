package a_barbu.gps_agenda;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.Serializable;
import java.text.SimpleDateFormat;

// OPTIUNI >>>
// pot sa le selectez dupa model

public class MarkerObj implements Serializable {

    double lat;
    double lng;
    String locality;
    SimpleDateFormat hour = new SimpleDateFormat("HH:mm");
    int model;
    int  ID ;
    String memo;
    int size=0;
    String title="(no_title)";
    String added;
    String last_visit;

    public int getPassed() {
        return passed;
    }

    public void setAdded(String s){this.added=s;}

    public void setPassed(int passed) {
        this.passed = passed;
    }

    public void set_visit(String v){this.last_visit = v;}

    int passed = 0;
    int accuracy;
    int radius;
    public void setSize(int s){
        size=s;
    }

    public int getModel() {
        return model;
    }

    public void setModel(int model) {
        this.model = model;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public void setTitle(String t){
        title=t;
    }
    public MarkerObj(){

    }

    public MarkerObj (double lat, double lng, String loc, int r, int a, SimpleDateFormat hour, String memo, int model, int id){
        this.lat = lat;
        this.lng = lng;
        this.locality= loc;
        this.hour = hour;
        this.radius=r;
        this.accuracy=a;
        this.memo=memo;
        this.model=model;
        this.ID = id;
    }

    public void Passed(int ID){
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

    public MarkerObj ReturnModel(int mod){
        if (this.model == mod)
        return this;
        else return null;
    }


    @Override
    public String toString() {
        return this.locality + ". " + this.ID + " [$]";
    }
}

