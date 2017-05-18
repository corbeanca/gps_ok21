package a_barbu.gps_agenda;

import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PopMarker extends AppCompatActivity
        implements AdapterView.OnItemClickListener {

    EditText memo;
    ListView pinlist;
    ImageView pinView ;
    static String list[] = new String[20];
    static boolean batt;
    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popinfo);

        memo = (EditText) findViewById(R.id.frag_memo);
        String markers[]= getResources().getStringArray(R.array.markers);
        pinView =(ImageView)findViewById(R.id.new_marker);
        pinlist= (ListView) findViewById(R.id.list_markerop1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,markers);
        pinlist.setAdapter(adapter);
        pinlist.setOnItemClickListener( this);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int w = dm.widthPixels;
        int h = dm.heightPixels;
        getWindow().setLayout((int) (w*.8),(int) (h *.6));
       // getWindow().setLayout(w,h);
        Button save = (Button) findViewById(R.id.save_pin);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra("memo",memo.getText().toString());
                setResult(RESULT_OK,i);
                finish();
            }
        });



    }

//    public void combine(String[] shapes, String[] colors) {
//        for (int i=1;i<=3;i++)
//            for (int j=1;j<=4;j++)
//                PopMarker.list[i*j]=shapes[i-1] +" "+ colors[j-1];
//    }


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

            Toast.makeText(this,temp.getText(),Toast.LENGTH_SHORT).show();
    }
}
