package a_barbu.gps_agenda;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

public class Online extends Activity
       implements AdapterView.OnItemClickListener

{   RadioGroup rg_batt;
    RadioButton rb;
    ListView list_color;
    ListView list_shape;
    SeekBar  seek_acc;
    SeekBar  seek_radius;
    static boolean batt;
    int pos=2;
    int sh=1;
    int col=1;
    ImageView pinView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
        rg_batt = (RadioGroup) findViewById(R.id.rg_bat);
        try{
        rb = (RadioButton) findViewById(checkBatt());}
        catch (NullPointerException e){}
        setContentView(R.layout.fragment_online);
        String colors[]= getResources().getStringArray(R.array.online_color);
        String shapes[]= getResources().getStringArray(R.array.online_shape);

        pinView =(ImageView)findViewById(R.id.pin_view);
        //vvv    pin-ul setat deja    vvv
        checkPin();
       // pinView.setImageResource();

        list_shape= (ListView) findViewById(R.id.list_shapes);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,shapes);
        list_shape.setAdapter(adapter);
        list_shape.setOnItemClickListener(this);
        list_color= (ListView) findViewById(R.id.list_color);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,colors);
        list_color.setAdapter(adapter2);
        list_color.setOnItemClickListener(this);

        seek_acc= (SeekBar) findViewById(R.id.seekBar_acc);
        seek_acc.setProgress(show_seek(1));
        seek_radius= (SeekBar) findViewById(R.id.seekBar_radius);
        seekbar(1);
        seekbar(2);
        Button save = (Button) findViewById(R.id.pref_save);
        save.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(Online.this,Principal.class));
                                    }
                                }
        );

    }

    private void checkPin() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String def ;
        def = sp.getString("pin_shape", null);
        if (def !=null){

        }
    }
    public int checkBatt(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        int c = sp.getInt("Battery",-1);
        if (c==0)
            return 0;
        else if (c==1)
            return 1;

        return -1;
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView temp = (TextView) view;
       int p = position +1;

        Toast.makeText(this,temp.getText(),Toast.LENGTH_SHORT).show();

        if (parent == list_shape) {
            switch (p) {
                case 1:
                    pinView.setImageResource(R.drawable.circle);
                    savePin("circle",1);
                    break;
                case 2:
                    pinView.setImageResource(R.drawable.square);
                    savePin("square",1);
                    break;
            case 3:
                pinView.setImageResource(R.drawable.shield);
                savePin("shield",1);
                break;
                            }
        }
        else switch (p){
            case 1:
                pinView.setBackgroundColor(Color.RED);
                savePin("red",0);
                break;
            case 2:
                pinView.setBackgroundColor(Color.GREEN);
                savePin("green",0);
                break;
            case 3:
                pinView.setBackgroundColor(Color.BLUE);
                savePin("blue",0);
                break;
            case 4:
                pinView.setBackgroundColor(Color.YELLOW);
                savePin("yellow",0);
                break;
        }

    }

    public void savePin(String save,int i){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        if(i ==1 )
        editor.putString("pin_shape", save);
        else
        editor.putString("pin_color", save);
        editor.commit();
    }

    public void seekbar (final int that){

        if (that == 1){
       // seek_acc.setProgress(show_seek(that));

        seek_acc.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    int progress_value;
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progress_value = progress;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                      //  Toast.makeText(Online.this,"SeekBar sus",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
//                        text_view.setText("Covered : " + progress_value + " / " +seek_bar.getMax());
                        Toast.makeText(Online.this,"SeekBar salvat sus",Toast.LENGTH_LONG).show();
                        saveSeek(that,progress_value);
                    }
                }
        );}
        else  {      seek_radius.setProgress(show_seek(that));
        seek_radius.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    int progress_value;
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            progress_value = progress;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                       // Toast.makeText(Online.this,"SeekBar jos",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
//                        text_view.setText("Covered : " + progress_value + " / " +seek_bar.getMax());
                        Toast.makeText(Online.this,"SeekBar salvat jos",Toast.LENGTH_LONG).show();
                        saveSeek(that,progress_value);
                    }
                }
        );}
    }

    public void saveSeek(int that, int progress_value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor ed = sp.edit();
        if (that == 1)
           ed.putInt("default_accuracy", progress_value);
        else ed.putInt("default_radius", progress_value);
        ed.commit();
    }

    public void setBatt(int x)
    {   SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor ed = sp.edit();
        if (x==1)
            ed.putInt("Battery", 1);
        else ed.putInt("Battery",0);
        ed.commit();

    }

    public int show_seek( int i) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (i ==1 )
            return i = sp.getInt("default_accuracy", 3) ;
        else return i = sp.getInt("default_radius", 3);


    }

}








