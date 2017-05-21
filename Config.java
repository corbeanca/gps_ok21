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
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.RadioButton;
        import android.widget.RadioGroup;
        import android.widget.Toast;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);

        rg = (RadioGroup) findViewById(R.id.rgroup);
        default_move = (ImageView) findViewById(R.id.default_photo);
        showMove();

        setupSaveButton();
        checkNewUser();
    }

    private void checkNewUser() {

    }

    private void setupSaveButton() {
        Button save = (Button) findViewById(R.id.button2);
        EditText h1 = (EditText) findViewById(R.id.hour_start);
        EditText h2 = (EditText) findViewById(R.id.hour_stop);
        st = showH(1);
        h1.setText(st);
        st = showH(2);
        h2.setText(st);
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
                    Toast.makeText(Config.this, "Parsare timp OK", Toast.LENGTH_LONG).show();}
                } catch (ParseException e) {
                    Toast.makeText(Config.this, "Nu a mers parsarea timpului, introduceti din nou respectand formatul " , Toast.LENGTH_LONG).show();
                }
                setTime();
                startOnline();
                // spre urmatoarea pagina
                Intent intent = new Intent(Config.this, Principal.class);
                startActivity(intent);
            }
        });
    }

    private void startOnline() {
        Calendar calendar = Calendar.getInstance();

        Intent intent = new Intent(getApplicationContext(), Notif_receiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        //  testare --- merge !
        // calendar.set(Calendar.HOUR_OF_DAY, 11);
        //  calendar.set(Calendar.MINUTE,31);

    }

    public void select_def_transp(View v) {
        int radiobuttonid = rg.getCheckedRadioButtonId();
        rb = (RadioButton) findViewById(radiobuttonid);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("Movement", radiobuttonid);
        editor.commit();

        switch (v.getId()) {
            case R.id.config_car:
                default_move.setImageResource(R.mipmap.car);
                break;
            case R.id.config_bicycle:
                default_move.setImageResource(R.mipmap.bicycle);
                break;
            case R.id.config_transit:
                default_move.setImageResource(R.mipmap.transit);
                break;
            case R.id.config_walk:
                default_move.setImageResource(R.mipmap.walk);
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
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int move = sharedPref.getInt("Movement", 0);
        switch (move) {
            case R.id.config_car:
                default_move.setImageResource(R.mipmap.car);
                rb = (RadioButton) findViewById(R.id.config_car) ;
                rb.setChecked(true);
                break;
            case R.id.config_bicycle:
                default_move.setImageResource(R.mipmap.bicycle);
                rb = (RadioButton) findViewById(R.id.config_bicycle) ;
                rb.setChecked(true);
                break;
            case R.id.config_transit:
                default_move.setImageResource(R.mipmap.transit);
                rb = (RadioButton) findViewById(R.id.config_transit) ;
                rb.setChecked(true);
                break;
            case R.id.config_walk:
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




}