package a_barbu.gps_agenda;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class Notif_receiver extends BroadcastReceiver {
    public Notif_receiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notifMan = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        // legatura catre posibila lista PIN -uri
        Intent repeating_intent = new Intent(context, Principal.class);
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,100,repeating_intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
            .setContentIntent(pendingIntent)
          .setSmallIcon(android.R.drawable.alert_light_frame)
           .setContentTitle("Buna dimineata! Primul task este")
          .setContentText("TASK")
           .setAutoCancel(true);

       notifMan.notify(100,builder.build());



       // throw new UnsupportedOperationException("Not yet implemented");
    }
}
