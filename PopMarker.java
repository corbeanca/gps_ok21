package a_barbu.gps_agenda;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PopMarker extends AppCompatActivity
        implements AdapterView.OnItemClickListener {

    EditText memo;
    ListView pinlist;
    ImageView pinView ;
    SeekBar radius;
    SeekBar accuracy;
    SeekBar pinsize;
    int radius_val;
    int accuracy_val;
    int model;
    int pinsize_val;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popinfo);

        accuracy = (SeekBar) findViewById(R.id.pop_accu);
        radius = (SeekBar) findViewById(R.id.pop_radius);
        pinsize = (SeekBar) findViewById(R.id.pop_pinsize);
        model=Principal.model;
        radius_val=Principal.radius;
        accuracy_val=Principal.accuracy;
        pinsize_val=Principal.pinsize;

        memo = (EditText) findViewById(R.id.frag_memo);
        String markers[]= getResources().getStringArray(R.array.markers);
        pinView =(ImageView)findViewById(R.id.new_marker);
        setImage(model-1);
        pinlist= (ListView) findViewById(R.id.list_markerop1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.custom_item,markers);
        pinlist.setAdapter(adapter);
        pinlist.setOnItemClickListener( this);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int w = dm.widthPixels;
        int h = dm.heightPixels;
        getWindow().setLayout((int) (w*.7),(int) (h *.5));

        Button save = (Button) findViewById(R.id.save_pin);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra("memo",memo.getText().toString());
                i.putExtra("model",model);
                i.putExtra("radius",radius_val);
                i.putExtra("accuracy",accuracy_val);
                i.putExtra("pinsize",pinsize_val);
                setResult(RESULT_OK,i);
                finish();
            }
        });
        seekbar("accuracy");
        seekbar("default_radius");
        seekbar("default_pinsize");
    }


    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        i.putExtra("memo",memo.getText().toString());
        setResult(RESULT_OK,i);
        finish();

    }
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TextView temp = (TextView) view;
            int p = position +1;
            model = p;
       //     Toast.makeText(this,temp.getText() +" " +position,Toast.LENGTH_SHORT).show();
            setImage(position);
    }

    private void setImage(int position) {
        switch (position){
            case 0://red green blue yell
                pinView.setImageResource(R.mipmap.circle_red);
                break;
            case 1:
                pinView.setImageResource(R.mipmap.circle_green);
                break;
            case 2:
                pinView.setImageResource(R.mipmap.circle_blue);
                break;
            case 3:
                pinView.setImageResource(R.mipmap.circle_yellow);
                break;
            case 4:
                pinView.setImageResource(R.mipmap.square_red);
                break;
            case 5:
                pinView.setImageResource(R.mipmap.square_green);
                break;
            case 6:
                pinView.setImageResource(R.mipmap.square_blue);
                break;
            case 7:
                pinView.setImageResource(R.mipmap.square_yellow);
                break;
            case 8:
                pinView.setImageResource(R.mipmap.shield_red);
                break;
            case 9:
                pinView.setImageResource(R.mipmap.shield_green);
                break;
            case 10:
                pinView.setImageResource(R.mipmap.shield_blue);
                break;
            case 11:
                pinView.setImageResource(R.mipmap.shield_yellow);
                break;

        }
    }


    public void seekbar(final String that) {
        switch (that) {
            case "accuracy": {
                accuracy.setProgress(accuracy_val);
                accuracy.setOnSeekBarChangeListener(
                        new SeekBar.OnSeekBarChangeListener() {
                            int progress_value;

                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                progress_value = progress;
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
//
                                Toast.makeText(PopMarker.this, "Accuracy updated", Toast.LENGTH_LONG).show();
                               accuracy_val= progress_value;
                            }
                        }
                );
            }
            case "radius": {
                radius.setProgress(radius_val);
                radius.setOnSeekBarChangeListener(
                        new SeekBar.OnSeekBarChangeListener() {
                            int progress_value;

                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                progress_value = progress;
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
//                        text_view.setText("Covered : " + progress_value + " / " +seek_bar.getMax());
                                Toast.makeText(PopMarker.this, "Radius updated", Toast.LENGTH_LONG).show();
                             radius_val= progress_value;
                            }
                        }
                );
            }
            case "pinsize": {
                pinsize.setProgress(pinsize_val);
                pinsize.setOnSeekBarChangeListener(
                        new SeekBar.OnSeekBarChangeListener() {
                            int progress_value;

                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                progress_value = progress;
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
//                        text_view.setText("Covered : " + progress_value + " / " +seek_bar.getMax());
                                Toast.makeText(PopMarker.this, "Size updated", Toast.LENGTH_LONG).show();
                             pinsize_val= progress_value;
                            }
                        }
                );
            }
        }}
}
