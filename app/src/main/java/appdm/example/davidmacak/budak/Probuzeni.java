package appdm.example.davidmacak.budak;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Probuzeni extends BroadcastReceiver {
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Intent intent1 = new Intent(context,PopUp.class);
        intent1.putExtra("requestCode",intent.getIntExtra("requestCode",0));
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);



    }


    }

