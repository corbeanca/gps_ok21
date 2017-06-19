package a_barbu.gps_agenda;


import android.app.TimePickerDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.app.DialogFragment;
import android.app.Dialog;
import java.util.Calendar;
import android.widget.TimePicker;


public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //Use the current time as the default values for the time picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        //Create and return a new instance of TimePickerDialog
        return new TimePickerDialog(getActivity(),this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    //onTimeSet() callback method
    public void onTimeSet(TimePicker view, int hourOfDay, int minute){
        if (Config.startstop) {
            TextView tv = (TextView) getActivity().findViewById(R.id.hour_start);
            if (minute<10)
            tv.setText(String.valueOf(hourOfDay) + ":0" + String.valueOf(minute) );
           else tv.setText(String.valueOf(hourOfDay) + ":" + String.valueOf(minute) );
            Config.startstop = false;
        } else{
            TextView tv = (TextView) getActivity().findViewById(R.id.hour_stop);
            if (minute<10)
            tv.setText(String.valueOf(hourOfDay) + ":0" + String.valueOf(minute) );
           else tv.setText(String.valueOf(hourOfDay) + ":" + String.valueOf(minute) );
            Config.startstop = true;
        }
    }
}