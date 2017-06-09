package a_barbu.gps_agenda;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.locks.Lock;

public class PopIti extends AppCompatActivity
        implements AdapterView.OnItemClickListener {

    Itinerary newIti;
    DatabaseReference Ref = Principal.database.getReference();
    int pos=0;
    int year1, month1, day1;
    static final int DIALOG_ID = 0;
    EditText name;
    EditText desc;
    TextView exists;
    ImageView calendar;
    List<Itinerary> itineraries;
    ListView lw;
    ArrayAdapter<Itinerary> adapter;
    String cal_date="";
    TextView date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popiti);
        final Calendar cal = Calendar.getInstance();
        year1 = cal.get(Calendar.YEAR);
        month1 = cal.get(Calendar.MONTH);
        day1 = cal.get(Calendar.DAY_OF_MONTH);
        lw = (ListView) findViewById(R.id.popiti_list);
        ImageView plus = (ImageView) findViewById(R.id.popiti_plus);
        ImageView minus = (ImageView) findViewById(R.id.popiti_minus);
        ImageView update = (ImageView) findViewById(R.id.popiti_update);
        calendar = (ImageView) findViewById(R.id.popiti_calendar);
        exists = (TextView) findViewById(R.id.popiti_exists);
        date = (TextView) findViewById(R.id.popiti_date);

//        final ImageView img = (ImageView)findViewById(R.id.popiti_calendar);
//        img.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//
//                Animation an = new RotateAnimation(0.0f, 360.0f, img.getWidth() / 2, img.getHeight() / 2);
//                an.reset();
//
//                an.setDuration(1000); // duration in ms
//                an.setRepeatCount(0); // -1 = infinite repeated
//                an.setRepeatMode(Animation.REVERSE); // reverses each repeat
//                an.setFillAfter(true); // keep rotation after animation
//                //an.start();
//                img.setAnimation(an);
//                img.invalidate(); //IMPORTANT: force image refresh
//            }
//        });
        gocalendar();
        itineraries = importItinerary();


//        if (Principal.listaItinener != null) {
//            itineraries = Principal.listaItinener;
//        } else {
//            itineraries = new ArrayList<>();
//
//        }

//        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itineraries);
//        lw.setAdapter(adapter);
//        lw.setOnItemClickListener(this);
        adapter_iti(itineraries);
        name = (EditText) findViewById(R.id.popiti_name);
        desc = (EditText) findViewById(R.id.popiti_desc);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int w = dm.widthPixels;
        int h = dm.heightPixels;
        getWindow().setLayout((int) (w * .8), (int) (h * .6));
        //getWindow().set

    }

    private void adapter_iti(List<Itinerary> lista) {
    //    adapter = null;
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lista);
        lw.setAdapter(adapter);
        lw.setOnItemClickListener(this);
    }

    public void gocalendar() {
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
       // lw.requestLayout();
        name.setText(itineraries.get(position).getName());
        desc.setText(itineraries.get(position).getDesc());
        setDate(itineraries.get(position).date);
        newIti = itineraries.get(position);
        pos = position;
    }

    public void updateItionline(Itinerary it, String name) {
        DatabaseReference R_add = Ref.child(ShowPref("Email")).child("Itinerary").child(name);
        R_add.setValue(newIti);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_ID) {
            return new DatePickerDialog(this, dpickListner, year1, month1, day1);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener dpickListner = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            year1 = year;
            month1 = month+1;
            day1 = dayOfMonth;
            Toast.makeText(PopIti.this, "Date set !", Toast.LENGTH_SHORT).show();
            cal_date= new Integer(dayOfMonth).toString();
            cal_date= cal_date+" / "+new Integer(month+1).toString();
            cal_date=cal_date+" / "+new Integer(year).toString();
            setDate(cal_date);

        }

    };

    private void setDate(String cal_date) {
        date.setText(cal_date);
    }

    public void clickIPlus(View v) {
        newIti = new Itinerary();
        String n = name.getText().toString();
        if (n.equals("")) {
            Toast.makeText(this, "Insert name first", Toast.LENGTH_SHORT).show();
            return;
        }

        looper:{
        for (int i=0;i<itineraries.size();i++) {
            Itinerary x = itineraries.get(i);
            if (n.equals(x.getName())) {
                exists.bringToFront();
                exists.setVisibility(View.VISIBLE);
                Toast.makeText(this, "nu s-a inserat", Toast.LENGTH_SHORT).show();
                break looper;
            }

        }
            exists.setVisibility(View.INVISIBLE);
            newIti.setName(n);
            newIti.setDesc(desc.getText().toString());
            newIti.date=cal_date;

            itineraries.add(newIti);
            updateItionline(newIti, name.getText().toString());
            Toast.makeText(this, "Added!", Toast.LENGTH_SHORT).show();
            adapter.add(newIti);
            adapter.notifyDataSetChanged();
             itineraries.clear();
        }
    }

    public void clickIMinus(View v) {

        itineraries.remove(pos);

        DatabaseReference R_del = Ref.child(ShowPref("Email")).child("Itinerary").child(newIti.getName());
        R_del.removeValue();
        Toast.makeText(this, "Deleted!", Toast.LENGTH_SHORT).show();


        adapter.clear();
        adapter_iti(itineraries);
        adapter.notifyDataSetChanged();
       // itineraries.clear();
    }

    public void clickUpdate(View v) {
        newIti.date=cal_date;
        newIti.desc=desc.getText().toString();
        updateItionline(newIti,newIti.getName());

        itineraries.get(pos).desc=newIti.getDesc();
        itineraries.get(pos).date=newIti.date;
        adapter_iti(itineraries);
        adapter.notifyDataSetChanged();

        Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();

    }

    public String ShowPref(String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        return sp.getString(key, "var");
    }

    public ArrayList<Itinerary> importItinerary(){
        final ArrayList<Itinerary> iti = new ArrayList<>();
        Ref.child("alexandrubarbu93@gmail,com").child("Itinerary")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) {
                            Itinerary value = child.getValue(Itinerary.class);
                            iti.add(value);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
      //  Toast.makeText(this, "Itineraries imported", Toast.LENGTH_LONG).show();
return iti;
    }
}
