package a_barbu.gps_agenda;
import android.app.AlarmManager;
        import android.app.PendingIntent;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.preference.PreferenceManager;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.RadioButton;
        import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.Calendar;


public class Config extends AppCompatActivity {

    RadioGroup rg;
    RadioButton rb;
    String hour_s1;
    String hour_s2;
    String st;
    SimpleDateFormat set1 = new SimpleDateFormat("HH:mm");
    SimpleDateFormat set2 = new SimpleDateFormat("HH:mm");
    ImageView default_move;
    static FirebaseDatabase database= FirebaseDatabase.getInstance();
    DatabaseReference Ref = database.getInstance().getReference();
    int lang=0;
    Switch langSW;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);
        langSW = (Switch) findViewById(R.id.config_lang);
        rg = (RadioGroup) findViewById(R.id.rgroup);
        default_move = (ImageView) findViewById(R.id.default_photo);
        langSW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){TextView tl = (TextView) findViewById(R.id.config_tagL);
                    tl.setText("Română");
                    lang=1;}
                else {lang =0;
                    TextView tl = (TextView) findViewById(R.id.config_tagL);
                    tl.setText("English");
            }}
        });


        if (ShowPrefB("first_use")){
        ImportSettings();
            SavePrefb("first_use");
            SavePrefb("go_main");}
        else {setInfo();}
        showMove();
        setupSaveButton();
    }

    private void setInfo() {
        EditText h1 = (EditText) findViewById(R.id.hour_start);
        EditText h2 = (EditText) findViewById(R.id.hour_stop);

        st = showH(1);
        h1.setText(st);
        st = showH(2);
        h2.setText(st);
        showMove();
    }

    private void setupSaveButton() {
        Button save = (Button) findViewById(R.id.button2);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Config.this, "Settings saved!", Toast.LENGTH_SHORT).show();
                EditText h1 = (EditText) findViewById(R.id.hour_start);
                hour_s1 = h1.getText().toString();
                EditText h2 = (EditText) findViewById(R.id.hour_stop);
                hour_s2 = h2.getText().toString();
                //adaugat conditie parsare timp
                try {if (hour_s1  != null && hour_s2 != null){
                    set1.parse(hour_s1);
                    set2.parse(hour_s2);
                   // Toast.makeText(Config.this, "Parsare timp OK", Toast.LENGTH_LONG).show();
                    }
                } catch (ParseException e) {
                    Toast.makeText(Config.this, "Nu a mers parsarea timpului, introduceti din nou respectand formatul " , Toast.LENGTH_LONG).show();
                }
                setTime();
                startService();
                int m = ShowPrefInt("Movement");
                SaveOnline(hour_s1,hour_s2,m,lang);


                // spre urmatoarea pagina
                Intent intent = new Intent(Config.this, Principal.class);
                startActivity(intent);
            }
        });
    }

    public void ImportSettings() {
        DatabaseReference Ref3= Ref.child(ShowPref("Email"));
        DatabaseReference Rstart= Ref3.child("h_start");
        DatabaseReference Rstop= Ref3.child("h_stop");
        DatabaseReference Rmove= Ref3.child("Movement");


        Rstart.addListenerForSingleValueEvent(new ValueEventListener() {
                                                   @Override
                                                   public void onDataChange(DataSnapshot dataSnapshot) {
                                                       hour_s1 = dataSnapshot.getValue(String.class);
                                                       EditText h1 = (EditText) findViewById(R.id.hour_start);
                                                       h1.setText(hour_s1);
                                                   }
                                                  @Override
                                                  public void onCancelled(DatabaseError databaseError) {
                                                  }
                                              });
        Rstop.addListenerForSingleValueEvent(new ValueEventListener() {
                                                   @Override
                                                   public void onDataChange(DataSnapshot dataSnapshot) {
                                                       hour_s2 = dataSnapshot.getValue(String.class);
                                                       EditText h2 = (EditText) findViewById(R.id.hour_stop);
                                                       h2.setText(hour_s2);

                                                   }
                                                  @Override
                                                  public void onCancelled(DatabaseError databaseError) {
                                                  }
                                              });
        Rmove.addListenerForSingleValueEvent(new ValueEventListener() {
                                                   @Override
                                                   public void onDataChange(DataSnapshot dataSnapshot) {
                                                      long m = (long) dataSnapshot.getValue();
                                                       int x=((int) m);
                                                       SavePref("Movement",x);
                                                       switch (x) {
                                                           case 1:
                                                               default_move.setImageResource(R.mipmap.car);
                                                               rb = (RadioButton) findViewById(R.id.config_car) ;
                                                               rb.setChecked(true);

                                                               break;
                                                           case 3:
                                                               default_move.setImageResource(R.mipmap.bicycle);
                                                               rb = (RadioButton) findViewById(R.id.config_bicycle) ;
                                                               rb.setChecked(true);

                                                               break;
                                                           case 4:
                                                               default_move.setImageResource(R.mipmap.transit);
                                                               rb = (RadioButton) findViewById(R.id.config_transit) ;
                                                               rb.setChecked(true);

                                                               break;
                                                           case 2:
                                                               default_move.setImageResource(R.mipmap.walk);
                                                               rb = (RadioButton) findViewById(R.id.config_walk) ;
                                                               rb.setChecked(true);

                                                               break;
                                                       }

                                                   }
                                                  @Override
                                                  public void onCancelled(DatabaseError databaseError) {
                                                  }
                                              });



            Toast.makeText(Config.this, "Settings have been imported ", Toast.LENGTH_LONG).show();

    }

    private void SaveOnline(String hour_s1, String hour_s2, int mov, int lang) {
        DatabaseReference Ref2= Ref.child(ShowPref("Email"));
        Ref2.child("h_start").setValue(hour_s1);
        Ref2.child("h_stop").setValue(hour_s2);
        Ref2.child("Movement").setValue(mov);
        Ref2.child("Language").setValue(lang);

    }

    private void startService() {
        Calendar calendar = Calendar.getInstance();

        Intent intent = new Intent(getApplicationContext(), Notif_receiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        //  testare --- merge !

       //  calendar.set(Calendar.HOUR_OF_DAY, 11);
       //  calendar.set(Calendar.MINUTE,31);

    }

    public void select_def_transp(View v) {
        int radiobuttonid = rg.getCheckedRadioButtonId();
        rb = (RadioButton) findViewById(radiobuttonid);

        switch (v.getId()) {
            case R.id.config_car:
                default_move.setImageResource(R.mipmap.car);
                SavePref("Movement",1);
                break;
            case R.id.config_bicycle:
                default_move.setImageResource(R.mipmap.bicycle);
                SavePref("Movement",3);
                break;
            case R.id.config_transit:
                default_move.setImageResource(R.mipmap.transit);
                SavePref("Movement",4);
                break;
            case R.id.config_walk:
                default_move.setImageResource(R.mipmap.walk);
                SavePref("Movement",2);

                break;
        }

    }

    private void setTime() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("hour_start", hour_s1);
        editor.putString("hour_stop", hour_s2);
        // vreau cu commit in loc de apply ca sa mi modifice instant programul
        editor.commit();
    }

    private String showH(int i) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (i == 1)
            return  sp.getString("hour_start", "08:00");
        else return  sp.getString("hour_stop", "22:00");


    }

    public void showMove() {
        int move = ShowPrefInt("Movement");
        switch (move) {
            case 1:
                default_move.setImageResource(R.mipmap.car);
                rb = (RadioButton) findViewById(R.id.config_car) ;
                rb.setChecked(true);
                break;
            case 3:
                default_move.setImageResource(R.mipmap.bicycle);
                rb = (RadioButton) findViewById(R.id.config_bicycle) ;
                rb.setChecked(true);
                break;
            case 4:
                default_move.setImageResource(R.mipmap.transit);
                rb = (RadioButton) findViewById(R.id.config_transit) ;
                rb.setChecked(true);
                break;
            case 2:
                default_move.setImageResource(R.mipmap.walk);
                rb = (RadioButton) findViewById(R.id.config_walk) ;
                rb.setChecked(true);
                break;
        }


    }


    public void Sync(View v){

   // if (flag_1)
        Toast.makeText(Config.this, "Settings uploaded" , Toast.LENGTH_LONG).show();
    //    else
            Toast.makeText(Config.this, "Do you want to override settings on cloud?" , Toast.LENGTH_LONG).show();
            Toast.makeText(Config.this, "Settings downloaded" , Toast.LENGTH_LONG).show();
   // if (flag_2)
        Toast.makeText(Config.this, "Nothing present on cloud " , Toast.LENGTH_LONG).show();
   // if (flag_3)
        Toast.makeText(Config.this, "Succesfully retrieved data " , Toast.LENGTH_LONG).show();

    }

    public String ShowPref(String key){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        return sp.getString(key,"var");
    }

    public int ShowPrefInt(String key){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        return sp.getInt(key,0);
    }

    public void SavePref(String key, int value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();

    }

    private boolean ShowPrefB(String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        return sp.getBoolean(key,true);
    }

    private void SavePrefb(String key){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, false);
        editor.commit();

    }
}