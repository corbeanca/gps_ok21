package a_barbu.gps_agenda;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class Online extends Activity
       implements AdapterView.OnItemClickListener
{
    ListView list_color;
    ListView list_shape;
    int pos=2;
    int sh=1;
    int col=1;
    ImageView pinView ;//= (ImageView)findViewById(R.id.pin_view);
   // for (int i = 0; i < colors.length; i++) {
   // Toast.makeText(getBaseContext(),colors[i], Toast.LENGTH_LONG).show(); }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

         super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_online);
        String colors[]= getResources().getStringArray(R.array.online_color);
        String shapes[]= getResources().getStringArray(R.array.online_shape);
        pinView =(ImageView)findViewById(R.id.pin_view);
        list_shape= (ListView) findViewById(R.id.list_shapes);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,shapes);
        list_shape.setAdapter(adapter);
        list_shape.setOnItemClickListener(this);
        list_color= (ListView) findViewById(R.id.list_color);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,colors);
        list_color.setAdapter(adapter2);
        list_color.setOnItemClickListener(this);


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
        // vreau cu commit in loc de apply pentru ca sa mi modifice instant programu
        editor.commit();
    }
    public void savePref (int i,int s){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        if(i ==1 )
            editor.putInt("accuracy", s);
        else
            editor.putInt("radius", s);
        // vreau cu commit in loc de apply pentru ca sa mi modifice instant programu
        editor.commit();
    }
}








