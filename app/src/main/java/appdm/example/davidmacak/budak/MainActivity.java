package appdm.example.davidmacak.budak;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//import com.google.zxing.integration.android.IntentIntegrator;

public class MainActivity extends AppCompatActivity {

    LinearLayout linearLayoutMain;
    NotificationManager notificationManager;
    ImageView VibrationimageView;
    LinearLayout linearLayoutTop;
    Context context;
    int d = 0;
    int c = 0;
    SharedPreferences preferences;
    SharedPreferences preferences2;
    SharedPreferences preferences3;
    SharedPreferences.Editor editor;
    SharedPreferences.Editor editor2;
    SharedPreferences.Editor editor3;
    ImageView moveImageView;
    String den = "";
    TextView[] textViewDays = new TextView[50];
    int notifikace;
    FloatingActionButton fab;
    long casBudikuProNotifikace = 0;
    private InterstitialAd mInterstitialAd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linearLayoutMain = (LinearLayout) findViewById(R.id.LinearLayoutMain);
        linearLayoutTop = findViewById(R.id.LinearLayoutTOP);
        fab = findViewById(R.id.fab);
        System.out.println("Jsi tu dobre");
        MobileAds.initialize(this, "ca-app-pub-9621446697711460~9021037060");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-9621446697711460/3120530040");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.show();
        znovuNacteni();


    }


    public static String getDate(long milliSeconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    private void smazatBudik(int q) {
        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(this, Probuzeni.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), (q - 1), myIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        alarmMgr.cancel(pendingIntent);
        pendingIntent.cancel();
        preferences2 = getSharedPreferences(String.valueOf(q), MODE_PRIVATE);
        editor2 = preferences2.edit();
        editor2.remove("cestaZvuk");
        editor2.remove("vibrace");
        editor2.remove("zvyseniHlasitosti");
        editor2.remove("time");
        editor2.remove("repeat");
        editor2.remove("urovenHlasitosti");
        editor2.remove("typzruseni");
        editor2.remove("hlidatPohybem");
        editor2.remove("levelcalculate");
        editor2.remove("countcalculate");
        editor2.commit();
        c = preferences.getInt("countAlarms", 0);
        editor = preferences.edit();
        if (c == 1) {
            TextView zadnyBudik = new TextView(context);
            zadnyBudik.setText("Nemáš nastaven žádný budík");
            zadnyBudik.setTextColor(getResources().getColor(R.color.textall));
            LinearLayout.LayoutParams zadnybudikLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            zadnybudikLp.setMargins(0, 0, 0, 25);
            zadnyBudik.setLayoutParams(zadnybudikLp);
            zadnyBudik.setTextSize(20);
            zadnyBudik.setGravity(Gravity.CENTER);
            linearLayoutTop.addView(zadnyBudik, 1);
            editor.remove("countAlarms");
            editor.commit();

        } else {
            c = c - 1;
            editor.putInt("countAlarms", c);
            editor.commit();
        }


        editor.putInt("notifikace", notifikace);
        editor.commit();
        zjistiPocetAktivnichBudiku();


    }

    @SuppressLint("ResourceAsColor")
    public void znovuNacteni() {
        context = this;


        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        preferences = getSharedPreferences("settings", MODE_PRIVATE);
        c = preferences.getInt("countAlarms", 0);
        notifikace = preferences.getInt("notifikace",0);
        preferences3 = getSharedPreferences("editace",MODE_PRIVATE);
        editor3 = preferences3.edit();
        editor = preferences.edit();
        if(c==0){
            TextView zadnyBudik = new TextView(context);
            zadnyBudik.setText("Žádný budík nemáš nastaven");
            preferences2 = getSharedPreferences(String.valueOf(c+1), MODE_PRIVATE);
            editor2 = preferences2.edit();
            editor2.remove("cestaZvuk");
            editor2.commit();
            zadnyBudik.setTextColor(getResources().getColor(R.color.textall));
            LinearLayout.LayoutParams zadnybudikLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            zadnybudikLp.setMargins(0,0,0,25);
            zadnyBudik.setLayoutParams(zadnybudikLp);
            zadnyBudik.setTextSize(20);
            zadnyBudik.setGravity(Gravity.CENTER);
            linearLayoutTop.addView(zadnyBudik,1);

        }
        for (int q = 1; q <= c; q++) {

            //Getting date of this moment and getting time of clock alarm from Shared Preferences
            final Date date = new Date(new Date().getTime());
            Calendar c = Calendar.getInstance();
            preferences2 = getSharedPreferences(String.valueOf(q), MODE_PRIVATE);
            editor2 = preferences2.edit();
            final long time = preferences2.getLong("time", 0);
            //

            d++; // This constant is used like ID for each component of row


            //Getting width of display for splitting each section
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;
            int height = dm.widthPixels;
            //

            //Creating components and and substituting values
            LinearLayout linearLayout = new LinearLayout(context);
            LinearLayout linearLayout2 = new LinearLayout(context);
            final TableRow row = new TableRow(context);
            final TableRow row2 = new TableRow(context);
            final TableRow row3 = new TableRow(context);
            final TextView textView = new TextView(context);
            textViewDays[d] = new TextView(context);
            final Switch swicth = new Switch(context);
            TextView textView3 = new TextView(context);
            ImageView budik = new ImageView(context);
            final ImageView imageButton = new ImageView(context);
            VibrationimageView = new ImageView(context);
            moveImageView = new ImageView(context);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    row.setBackground(getDrawable(R.color.background));
                    row2.setBackground(getDrawable(R.color.background));
                    row3.setBackground(getDrawable(R.color.textall));
                  //  budik.setImageDrawable(getDrawable(R.drawable.icon157349640));
                    imageButton.setBackground(getDrawable(R.drawable.cancel96));
                    VibrationimageView.setBackground(getDrawable(R.drawable.vibration));
                    moveImageView.setBackground(getDrawable(R.drawable.move));
                                        //  imageButton.setImageDrawable(getDrawable(R.drawable.icons8remove64));
                }
            }

            textView.setText(getDate(time, "HH:mm"));
            textView.setTextSize(20);
            textView.setTextColor(R.color.textall);
            textView.setTextColor(Color.parseColor("#ffffff"));
            textViewDays[q].setTextColor(Color.parseColor("#ffffff"));
            //
            boolean alarmUp = (PendingIntent.getBroadcast(context, q - 1,
                    new Intent(MainActivity.this, Probuzeni.class),
                    PendingIntent.FLAG_NO_CREATE) != null);

            if (alarmUp) {
                swicth.setChecked(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    swicth.getThumbDrawable().setColorFilter(Color.rgb(66,165,245), PorterDuff.Mode.MULTIPLY);
                    swicth.getTrackDrawable().setColorFilter(Color.rgb(255,255,255), PorterDuff.Mode.MULTIPLY);

                }

            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    swicth.getThumbDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                    swicth.getTrackDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

                }
            }


            //Getting date of alarm, and this date is used for getting day of the week
            c.set(Integer.parseInt(getDate(time, "yyyyy")), (Integer.parseInt(getDate(time, "MM")) - 1), Integer.parseInt(getDate(time, "dd")));
            switch (c.get(Calendar.DAY_OF_WEEK)) {
                case 1:
                    den = "Ne";
                    break;
                case 2:
                    den = "Po";
                    break;
                case 3:
                    den = "Út";
                    break;
                case 4:
                    den = "St";
                    break;
                case 5:
                    den = "Čt";
                    break;
                case 6:
                    den = "Pá";
                    break;
                case 7:
                    den = "So";
                    break;
            }
            //

            //Getting information about repeating alarm and this information is used for view
            if (swicth.isChecked()) {

                if (preferences2.getString("repeat", "none") == "") {
                    if (time < date.getTime()) {

                        textViewDays[d].setText("");
                    } else {
                        textViewDays[d].setText(den + " " + ", " + getDate(time, "dd.MM"));
                    }
                } else {
                    textViewDays[d].setText(preferences2.getString("repeat", "none"));

                }
            } else {
                textViewDays[d].setText("");
            }
            textViewDays[d].setTextSize(12);
            //

            // Setup id for each row component and for the row itself
            swicth.setId(d);
            textView.setId(d);
            budik.setId(d);
            row.setId(d);
            row2.setId(d);
            imageButton.setId(d);
            //

            //Layout parameters setup
            LinearLayout.LayoutParams hodinyLp = new LinearLayout.LayoutParams((width / 3)-40, (height / 7));
            LinearLayout.LayoutParams buttonLp = new LinearLayout.LayoutParams((width / 3), (height / 7));
            LinearLayout.LayoutParams imageViewLp = new LinearLayout.LayoutParams(((width / 6) / 2)-10, (height / 14)-10);
            LinearLayout.LayoutParams imageButtonLp = new LinearLayout.LayoutParams(((width / 7) / 2)-5, (height / 14)-5);
            LinearLayout.LayoutParams mezeraLp = new LinearLayout.LayoutParams((width / 4)-10   , (height / 7));
            LinearLayout.LayoutParams textViewDaysLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            LinearLayout.LayoutParams row2Lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams row3Lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
            LinearLayout.LayoutParams imageViewLpVibration = new LinearLayout.LayoutParams(((width / 6) / 3)-10, (height / 15)-10);



            //

            //Setup margins and padding
            rowLp.setMargins(0, 0, 0, 0);
            row2Lp.setMargins(0,0,0,0);
            linearLayout.setPadding(0, 0, 0, 0);
            textViewDays[d].setPadding(0, 0, 20, 20);
            imageButtonLp.setMargins(40,0,0,0);
            imageViewLp.setMargins(40,0,0,0);
            buttonLp.setMargins(0,0,0,0);
            imageButton.setPadding(0, 0, 0, 20);
            //

            //Setting parametrs of each component
            swicth.setLayoutParams(buttonLp);
            row3.setLayoutParams(row3Lp);
            textView.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            imageViewLp.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
            imageViewLpVibration.gravity = Gravity.CENTER;
            textViewDays[d].setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
            imageButtonLp.gravity = Gravity.BOTTOM;
            swicth.setGravity(Gravity.BOTTOM);
            textViewDays[d].setLayoutParams(textViewDaysLp);
            budik.setLayoutParams(imageViewLp);
            moveImageView.setLayoutParams(imageViewLpVibration);
            VibrationimageView.setLayoutParams(imageViewLpVibration);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                imageButton.setForegroundGravity(Gravity.CENTER_VERTICAL);
            }
            imageButton.setLayoutParams(imageButtonLp);
            textView.setLayoutParams(hodinyLp);
            textView3.setLayoutParams(mezeraLp);
            row.setLayoutParams(rowLp);
            row2.setLayoutParams(row2Lp);
            linearLayout.setGravity(Gravity.CENTER_VERTICAL);
            //

            //Adding views into layout and adding row into main Layout
            linearLayout.addView(imageButton);
            linearLayout.addView(textView3);
            linearLayout.addView(textView);
            linearLayout.addView(swicth);
            linearLayout2.addView(textViewDays[d]);
            row.addView(linearLayout);
            row2.addView(linearLayout2);
            linearLayoutMain.addView(row);
            linearLayoutMain.addView(row2);
            linearLayoutMain.addView(row3);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                row.setElevation(4);
                row2.setElevation(4);
                row3.setElevation(4);
            }
            //

            //Listening switch button and getting its id
            swicth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!swicth.isChecked()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            swicth.getThumbDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                            swicth.getTrackDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                        }
                        vypniBudik(row.getId());
                        textViewDays[row.getId()].setText("");

                    }
                    if (swicth.isChecked()) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            swicth.getThumbDrawable().setColorFilter(Color.rgb(66,165,245), PorterDuff.Mode.MULTIPLY);

                            swicth.getTrackDrawable().setColorFilter(Color.rgb(255,255,255), PorterDuff.Mode.MULTIPLY);
                        }
                        znovuNastavBudik(row.getId());

                    }

                    boolean alarmUp = (PendingIntent.getBroadcast(context, (d-1),
                            new Intent(MainActivity.this, Probuzeni.class),
                            PendingIntent.FLAG_NO_CREATE) != null);
                }
            });
            //

            //This button is to picture of trash which delete alarm
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    smazatBudik(imageButton.getId());
                    linearLayoutMain.removeView(row);
                    linearLayoutMain.removeView(row2);
                    linearLayoutMain.removeView(row3);

                }
            });
            //

            //This is is textview with time of alarm. Click on that run editaion of alarm.
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, VytvoritBudik.class);
                    editor3.putInt("true",(textView.getId())-1);
                    editor3.commit();
                    startActivity(intent);
                }
            });
            //

        }
        //Floating action butten which add alarm
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent uplneNovyBudik = new Intent(MainActivity.this, VytvoritBudik.class);
                startActivity(uplneNovyBudik);

            }

        });
        //

    }

    //Function which is running if you are editing alarm
    private void znovuNastavBudik(int id) {
        mInterstitialAd.show();

        Calendar calendar1 = Calendar.getInstance();
        preferences2 = getSharedPreferences(String.valueOf(id), MODE_PRIVATE);
        editor2 = preferences2.edit();
        long time = preferences2.getLong("time", 0);
        calendar1.setTimeInMillis(time);
        // Date datum = getDate(time,"dd-MMM-yyyy hh:mm");
        Date datum = calendar1.getTime();
        Date datumDnes = new Date(new Date().getTime());
        if (datum.getTime() < datumDnes.getTime()) {
            datum.setDate(datumDnes.getDate() + 1);
        }
        editor2 = preferences2.edit();
        editor2.putLong("time", datum.getTime());
        editor2.commit();


        time = datum.getTime();

        Calendar c = Calendar.getInstance();
        c.set(Integer.parseInt(getDate(time, "yyyyy")), (Integer.parseInt(getDate(time, "MM")) - 1), Integer.parseInt(getDate(time, "dd")));
        switch (c.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                den = "Ne";
                break;
            case 2:
                den = "Po";
                break;
            case 3:
                den = "Út";
                break;
            case 4:
                den = "St";
                break;
            case 5:
                den = "Čt";
                break;
            case 6:
                den = "Pá";
                break;
            case 7:
                den = "So";
                break;
        }
        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, Probuzeni.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (id - 1), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, datum.getTime(), pendingIntent);
        if (preferences2.getString("repeat", "none") == "") {
                textViewDays[id].setText(den + " " + ", " + getDate(time, "dd.MM."));
        } else {
            textViewDays[id].setText(preferences2.getString("repeat", "none"));

        }

        notifikace++;
        editor.putInt("notifikace",notifikace);
        editor.commit();
       zjistiPocetAktivnichBudiku();



    }

    private void vypniBudik(int q){

        AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(this, Probuzeni.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), (q-1), myIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        alarmMgr.cancel(pendingIntent);
        pendingIntent.cancel();
        editor.putInt("notifikace",notifikace);
        editor.commit();
        zjistiPocetAktivnichBudiku();


    }

    private void zjistiPocetAktivnichBudiku(){
        int pocetBudiku = 0;
        casBudikuProNotifikace = 0;
        boolean alarmUp = false;
        for(int poc=0;poc<50;poc++){
            SharedPreferences preferences3 = getSharedPreferences(String.valueOf(poc+1),MODE_PRIVATE);
            Calendar cal = Calendar.getInstance();
            Date date = new Date();
            date.setTime(preferences3.getLong("time",0));
            cal.setTime(date);
            alarmUp = (PendingIntent.getBroadcast(context, poc,
                    new Intent(MainActivity.this, Probuzeni.class),
                    PendingIntent.FLAG_NO_CREATE) != null);
            if(alarmUp){
                System.out.println("Cas buzeni = "+preferences3.getLong("time",0)+" puvodni = "+casBudikuProNotifikace);
                if(casBudikuProNotifikace<1 || preferences3.getLong("time",0)<casBudikuProNotifikace){
                    casBudikuProNotifikace =preferences3.getLong("time",0);
                }
                pocetBudiku++;

            }
            if(pocetBudiku>0 && poc==49){
                String hours;
                String minutes;
                Date datumBuzeni = new Date();
                datumBuzeni.setTime(casBudikuProNotifikace);
                if(datumBuzeni.getMinutes()<10){
                    minutes ="0"+datumBuzeni.getMinutes();
                }
                else {
                    minutes = String.valueOf(datumBuzeni.getMinutes());
                }
                if(datumBuzeni.getHours()<10){
                    hours ="0"+datumBuzeni.getHours();
                }
                else {
                    hours = String.valueOf(datumBuzeni.getHours());
                }

                Notification notification = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.bellsmall)
                        .setContentTitle("Budík nastaven")
                        .setContentText("Budík zazvoní v "+hours+":"+minutes+", "+datumBuzeni.getDate()+"."+(datumBuzeni.getMonth()+1)+".")
                        .build();
                notificationManager.notify(0, notification);
            }
            if(pocetBudiku==0 && poc==49){
                notificationManager.cancel(0);
                casBudikuProNotifikace = 0;
            }
        }
    }


}
