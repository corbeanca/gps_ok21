package a_barbu.gps_agenda;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class Preferences extends AppCompatActivity
       implements AdapterView.OnItemClickListener

{
    RadioGroup rg_batt;
    RadioButton rb;
    ListView list_color;
    ListView list_shape;
    SeekBar seek_acc;
    SeekBar seek_radius;
    SeekBar seek_pinsize;
    ImageView pinView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_pref);
        rg_batt = (RadioGroup) findViewById(R.id.rg_bat);
        String colors[] = getResources().getStringArray(R.array.online_color);
        String shapes[] = getResources().getStringArray(R.array.online_shape);

        pinView = (ImageView) findViewById(R.id.pin_view);
        //vvv    pin-ul setat deja    vvv
      //  checkPin();
        // pinView.setImageResource();
        checkBatt();


        list_shape = (ListView) findViewById(R.id.list_shapes);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, shapes);
        list_shape.setAdapter(adapter);
        list_shape.setOnItemClickListener(this);
        list_color = (ListView) findViewById(R.id.list_color);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, colors);
        list_color.setAdapter(adapter2);
        list_color.setOnItemClickListener(this);

        seek_acc = (SeekBar) findViewById(R.id.seekBar_acc);
        seek_radius = (SeekBar) findViewById(R.id.seekBar_radius);
        seek_pinsize = (SeekBar) findViewById(R.id.seekBar_size);
        seekbar("default_accuracy");
        seekbar("default_radius");
        seekbar("default_pinsize");
        Button save = (Button) findViewById(R.id.pref_save);
        save.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(Preferences.this, Principal.class));
                                    }
                                }
        );

    }
    public void checkBatt() {
        int opt = ShowPref("Battery");
        if (opt ==1)
            rb = (RadioButton) findViewById(R.id.but_eco) ;

        else rb = (RadioButton) findViewById(R.id.but_off) ;

            rb.setChecked(true);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView temp = (TextView) view;
        int p = position + 1;
        int col=ShowPref("col");
        if (col==1) col = 4;
        Toast.makeText(this, temp.getText(), Toast.LENGTH_SHORT).show();

        if (parent == list_shape) {
            switch (p) {
                case 1:
                    pinView.setImageResource(R.drawable.circle);
                    SavePref("col", 4);

                    break;
                case 2:
                    pinView.setImageResource(R.drawable.square);
                    SavePref("col", 8);

                    break;
                case 3:
                    pinView.setImageResource(R.drawable.shield);
                    SavePref("col", 12);

                    break;
            }
        } else switch (p) {
            case 1:
                pinView.setBackgroundColor(Color.RED);
                SavePref("model", col-4);
                break;
            case 2:
                pinView.setBackgroundColor(Color.GREEN);
                SavePref("model", col-3);
                break;
            case 3:
                pinView.setBackgroundColor(Color.BLUE);
                SavePref("model", col-2);
                break;
            case 4:
                pinView.setBackgroundColor(Color.YELLOW);
                SavePref("model", col-1);
                break;
        }

    }

    public void select_batt(View v) {
        int radiobuttonid = rg_batt.getCheckedRadioButtonId();
        rb = (RadioButton) findViewById(radiobuttonid);
        String a = rb.getText().toString();
        String c1= "Battery Saving Mode";
        String c2= "Mod eco";
        if (a.equals(c1)){
            Toast.makeText(getApplicationContext(), a, Toast.LENGTH_LONG).show();
        SavePref("Battery",1);}
            else if ( a.equals(c2)){
            Toast.makeText(getApplicationContext(), a, Toast.LENGTH_LONG).show();
        SavePref("Battery",1);}
        else
        {Toast.makeText(getApplicationContext(), a, Toast.LENGTH_LONG).show();
        SavePref("Battery",0);}
    }

    public void seekbar(final String that) {
    switch (that){
        case  "default_accuracy":{
            seek_acc.setProgress(ShowPref(that));
            seek_acc.setOnSeekBarChangeListener(
                    new SeekBar.OnSeekBarChangeListener() {
                        int progress_value;
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            progress_value = progress;
                        }
                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                            //  Toast.makeText(Preferences.this,"SeekBar sus",Toast.LENGTH_LONG).show();
                        }
                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
//                        text_view.setText("Covered : " + progress_value + " / " +seek_bar.getMax());
                            Toast.makeText(Preferences.this, "Default accuracy updated", Toast.LENGTH_LONG).show();
                            SavePref("default_accuracy", progress_value);
                        }
                    }
            );}
        case  "default_radius": {
            seek_radius.setProgress(ShowPref(that));
            seek_radius.setOnSeekBarChangeListener(
                    new SeekBar.OnSeekBarChangeListener() {
                        int progress_value;
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            progress_value = progress;
                        }
                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                            // Toast.makeText(Preferences.this,"SeekBar jos",Toast.LENGTH_LONG).show();
                        }
                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
//                        text_view.setText("Covered : " + progress_value + " / " +seek_bar.getMax());
                            Toast.makeText(Preferences.this, "Default radius updated", Toast.LENGTH_LONG).show();
                            SavePref("default_radius", progress_value);
                        }
                    }
            );}
        case  "default_pinsize": {
            seek_pinsize.setProgress(ShowPref(that));
            seek_pinsize.setOnSeekBarChangeListener(
                    new SeekBar.OnSeekBarChangeListener() {
                        int progress_value;
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            progress_value = progress;
                        }
                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                            // Toast.makeText(Preferences.this,"SeekBar jos",Toast.LENGTH_LONG).show();
                        }
                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
//                        text_view.setText("Covered : " + progress_value + " / " +seek_bar.getMax());
                            Toast.makeText(Preferences.this, "Default Size updated", Toast.LENGTH_LONG).show();
                            SavePref("default_pinsize", progress_value);
                        }
                    }
            );
        }
    }
    }

    public void SavePref(String key, int value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public int ShowPref(String key){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        return sp.getInt(key,1);
    }

}
