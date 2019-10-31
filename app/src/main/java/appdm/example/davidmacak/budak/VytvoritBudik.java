package appdm.example.davidmacak.budak;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.ads.InterstitialAd;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class VytvoritBudik extends AppCompatActivity {
    TimePicker timePicker;
    boolean isChecked = false;
    CheckBox workdays;
    CheckBox monday;
    CheckBox tuesday;
    CheckBox wendsday;
    CheckBox thursday;
    CheckBox friday;
    CheckBox saturday;
    CheckBox sunday;
    String cestaZvuk;
    CheckBox hlidatPohyb;
    AlarmManager alarmManager;
    int audiorequest = 1;
    int hours;
    long hoursEdit;
    long minutesEdit;
    int minutes;
    int hlidatPohybInt;
    BroadcastReceiver receiver;
    RadioButton radioButton2;
    Context context;
    MediaPlayer mp = new MediaPlayer();
    CheckBox zvysHlasCheckBox;
    CheckBox vibraceCheckBox;
    TextView textViewSettings;
    TextView textViewAdd;
    SeekBar urovenHlasitosti;
    int vibrace = 0;
    int zvyseniHlasitosti = 0;
    String opakovani = "";
    int d = 0;
    SharedPreferences preferences;
    TableRow tableRow5;
    TableRow tableRow6;
    TableRow tableRow7;
    SharedPreferences.Editor editor;
    SharedPreferences preferences2;
    SharedPreferences preferences3;
    SharedPreferences.Editor editor2;
    SharedPreferences.Editor editor3;
    TextView nazevPisneTextView;
    ImageButton imageButton;
    int notifikace;
    TextView procentaTextView;
    AudioManager audioManager;
    int urovenHlasitostiInt;
    RadioButton alarmOffButtonradioButton;
    RadioButton alarmOffShakenradioButton;
    RadioButton alarmOffCalculateradioButton;
    RadioButton alarmOffQRradioButton;
    RadioGroup alarmOffRadioGroup;
    int typeOfCancelAlarm = 1;
    long casBudikuProNotifikace = 0;
    NotificationManager notificationManager;
    FloatingActionButton floatingActionButton;
    LinearLayout linearPriklady;
    RadioGroup radioGroupLevel;
    RadioGroup radioGroupCount;
    int countCalculate=1;
    int levelCalculate=1;
    RadioButton radioButtonCount1;
    RadioButton radioButtonCount2;
    RadioButton radioButtonCount3;
    RadioButton radioButtonCount4;
    RadioButton radioButtonLevel1;
    RadioButton radioButtonLevel2;
    RadioButton radioButtonLevel3;
    RadioButton radioButtonLevel4;
    RadioButton radioButton9;
    PopupWindow popUp;
    PopupWindow popUpMove;
    ImageView imageViewCalculate;
    LinearLayout linearLayoutMain;
    ImageView imageViewMove;
    Button closeMove;
    boolean nacteniSonguBoolean = true;
    private InterstitialAd mInterstitialAd;
    ImageView imageViewLinkQR;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vytvoritbudik);
        loadItems();
        urovenHlasitosti.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(Color.parseColor("#42a5f5"), PorterDuff.Mode.MULTIPLY));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            urovenHlasitosti.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        }
        context = this;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nazevPisneTextView.setVisibility(View.GONE);
        preferences = getSharedPreferences("settings", MODE_PRIVATE);
        preferences3 = getSharedPreferences("editace", MODE_PRIVATE);
        editor3 = preferences3.edit();
        cestaZvuk = "Výchozí";
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        displayAlarmSettings();
        setListeners();
        editor = preferences.edit();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        nacteniSonguBoolean = false;
        if (data != null) {
            cestaZvuk = _getRealPathFromURI(this, data.getData());
            String nazevPisne = cestaZvuk;
            nazevPisne = nazevPisne.substring(nazevPisne.lastIndexOf("/") + 1, nazevPisne.length());
            nazevPisne = nazevPisne.substring(0, (nazevPisne.lastIndexOf(".") - 1));
            System.out.println("Ze by tady??");
            nazevPisneTextView.setText(nazevPisne);
            nazevPisneTextView.setVisibility(View.VISIBLE);
        }
        else{
            radioButton2.setChecked(false);
        }

    }


    public void nastavBudik(View view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hours = timePicker.getHour();
            minutes = timePicker.getMinute();
        } else {
            hours = timePicker.getCurrentHour();
            minutes = timePicker.getCurrentMinute();
        }


        if (workdays.isChecked()) {
            isChecked = true;
            opakovani = "Po, Út, St, Čt, Pá";
        } else {
            if (monday.isChecked() && !opakovani.contains("Po")) {
                isChecked = true;
                if (opakovani.length() < 1) {
                    opakovani += "Po";
                }
            }
            if (tuesday.isChecked() && !opakovani.contains("Út")) {
                isChecked = true;
                if (opakovani.length() < 1) {
                    opakovani += "Út";
                } else {
                    opakovani += ", Út";

                }
            }
            if (wendsday.isChecked() && !opakovani.contains("St")) {
                isChecked = true;
                if (opakovani.length() < 1) {
                    opakovani += "St";
                } else {
                    opakovani += ", St";

                }
            }
            if (thursday.isChecked() && !opakovani.contains("Čt")) {
                isChecked = true;
                if (opakovani.length() < 1) {
                    opakovani += "Čt";
                } else {
                    opakovani += ", Čt";

                }

            }
            if (friday.isChecked() && !opakovani.contains("Pá")) {
                isChecked = true;
                if (opakovani.length() < 1) {
                    opakovani += "Pá";
                } else {
                    opakovani += ", Pá";

                }
            }
            if (saturday.isChecked() && !opakovani.contains("So")) {
                isChecked = true;
                if (opakovani.length() < 1) {
                    opakovani += "So";
                } else {
                    opakovani += ", So";

                }
            }
            if (sunday.isChecked() && !opakovani.contains("Ne")) {
                isChecked = true;
                if (opakovani.length() < 1) {
                    opakovani += "Ne";
                } else {
                    opakovani += ", Ne";

                }
            }
        }
        if (vibraceCheckBox.isChecked()) {
            vibrace = 1;
        } else {
            vibrace = 0;
        }
        if (zvysHlasCheckBox.isChecked()) {
            zvyseniHlasitosti = 1;
        }
        if (hlidatPohyb.isChecked()) {
            hlidatPohybInt = 1;
        }

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, Probuzeni.class);
        intent.putExtra("requestCode", d + 1);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, d, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Date date = new Date(new Date().getTime());
        Date dateKontrola = new Date(new Date().getTime());
        date.setDate(date.getDate());
        date.setHours(hours);
        date.setMinutes(minutes);
        date.setSeconds(0);

        if (opakovani.length() > 1) {
            for (int den = 0; den < 7; den++) {
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.add(Calendar.DATE, den);
                Date date2 = c.getTime();
                System.out.println("Co je to za den? =" + date2.getDay());
                switch (date2.getDay()) {
                    case 0:
                        if (opakovani.contains("Ne")) {
                            date = c.getTime();
                            den = 7;
                            System.out.println("Den" + date2.getDay());
                        }
                        break;
                    case 1:
                        if (opakovani.contains("Po")) {
                            date = c.getTime();
                            den = 7;
                            System.out.println("Den" + date2.getDay());

                        }
                        break;
                    case 2:
                        if (opakovani.contains("Út")) {
                            date = c.getTime();
                            den = 7;
                            System.out.println("Den" + date2.getDay());

                        }
                        break;
                    case 3:
                        if (opakovani.contains("St")) {
                            date = c.getTime();
                            den = 7;
                            System.out.println("Den" + date2.getDay());

                        }
                        break;
                    case 4:
                        if (opakovani.contains("Čt")) {
                            date = c.getTime();
                            den = 7;
                            System.out.println("Den" + date2.getDay());

                        }
                        break;
                    case 5:
                        if (opakovani.contains("Pá")) {
                            date = c.getTime();
                            den = 7;
                            System.out.println("Den" + date2.getDay());

                        }
                        break;
                    case 6:
                        if (opakovani.contains("So")) {
                            date = c.getTime();
                            den = 7;
                            System.out.println("Den" + date2.getDay());

                        }
                        break;
                }
            }
        }

        if (dateKontrola.getTime() > date.getTime()) {
            date.setDate((date.getDate() + 1));
        }
        alarmMgr.set(AlarmManager.RTC_WAKEUP, date.getTime(), pendingIntent);
        d++;
        preferences2 = getSharedPreferences(String.valueOf(d), MODE_PRIVATE);
        editor2 = preferences2.edit();
        editor2.putString("cestaZvuk", cestaZvuk);
        editor2.putInt("vibrace", vibrace);
        editor2.putInt("zvyseniHlasitosti", zvyseniHlasitosti);
        editor2.putLong("time", date.getTime());
        editor2.putString("repeat", opakovani);
        editor2.putInt("urovenHlasitosti", urovenHlasitosti.getProgress());
        editor2.putInt("typzruseni", typeOfCancelAlarm);
        editor2.putInt("hlidatPohybem", hlidatPohybInt);
        editor2.putInt("levelcalculate",levelCalculate);
        editor2.putInt("countcalculate",countCalculate);
        editor2.commit();
        editor.putInt("countAlarms", d);
        editor.commit();

        Intent intentMain = new Intent(VytvoritBudik.this, MainActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        }
        if (mp.isPlaying()) {
            mp.stop();
        }
        zjistiPocetAktivnichBudiku();
        startActivity(intentMain);
        finish();


    }

    private String _getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @Override
    public void onBackPressed() {
        if (mp.isPlaying()) {
            mp.stop();
        }
        finish();
    }

    private void zjistiPocetAktivnichBudiku() {
        int pocetBudiku = 0;
        casBudikuProNotifikace = 0;
        boolean alarmUp = false;
        for (int poc = 0; poc < 50; poc++) {
            SharedPreferences preferences3 = getSharedPreferences(String.valueOf(poc + 1), MODE_PRIVATE);
            Calendar cal = Calendar.getInstance();
            Date date = new Date();
            date.setTime(preferences3.getLong("time", 0));
            cal.setTime(date);
            alarmUp = (PendingIntent.getBroadcast(context, poc,
                    new Intent(VytvoritBudik.this, Probuzeni.class),
                    PendingIntent.FLAG_NO_CREATE) != null);
            if (alarmUp) {
                System.out.println("Cas buzeni = " + preferences3.getLong("time", 0) + " puvodni = " + casBudikuProNotifikace);
                if (casBudikuProNotifikace < 1 || preferences3.getLong("time", 0) < casBudikuProNotifikace) {
                    casBudikuProNotifikace = preferences3.getLong("time", 0);
                }
                pocetBudiku++;

            }
            if (pocetBudiku > 0 && poc == 49) {
                String hours;
                String minutes;
                Date datumBuzeni = new Date();
                datumBuzeni.setTime(casBudikuProNotifikace);
                if (datumBuzeni.getMinutes() < 10) {
                    minutes = "0" + datumBuzeni.getMinutes();
                } else {
                    minutes = String.valueOf(datumBuzeni.getMinutes());
                }
                if (datumBuzeni.getHours() < 10) {
                    hours = "0" + datumBuzeni.getHours();
                } else {
                    hours = String.valueOf(datumBuzeni.getHours());
                }

                Notification notification = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.bellsmall)
                        .setContentTitle("Budík nastaven")
                        .setContentText("Budík zazvoní v " + hours + ":" + minutes + ", " + datumBuzeni.getDate() + "." + (datumBuzeni.getMonth() + 1) + ".")
                        .build();
                notificationManager.notify(0, notification);
            }
            if (pocetBudiku == 0 && poc == 49) {
                notificationManager.cancel(0);
                casBudikuProNotifikace = 0;
            }
        }
    }

    public void detailniNastaveni(View view) {
        tableRow5.setVisibility(View.VISIBLE);
        tableRow6.setVisibility(View.VISIBLE);
        tableRow7.setVisibility(View.VISIBLE);
        textViewSettings.setVisibility(View.GONE);
        textViewAdd.setVisibility(View.GONE);
        floatingActionButton.setVisibility(View.GONE);
        floatingActionButton.setClickable(false);


    }

    private void loadItems(){
        closeMove = findViewById(R.id.buttonCloseMove);
        imageViewMove = findViewById(R.id.imageViewMove);
        linearLayoutMain = findViewById(R.id.layoutMainSc);
        imageViewCalculate = findViewById(R.id.imageViewCalculate);
        timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        workdays = findViewById(R.id.checkBoxWorkDays);
        monday = findViewById(R.id.checkBoxMonday);
        tuesday = findViewById(R.id.checkBoxTuesday);
        wendsday = findViewById(R.id.checkBoxWendsday);
        thursday = findViewById(R.id.checkBoxThursday);
        friday = findViewById(R.id.checkBoxFriday);
        saturday = findViewById(R.id.checkBoxSaturday);
        radioButton2 = findViewById(R.id.radioButton2);
        tableRow5 = findViewById(R.id.tableRow5);
        tableRow6 = findViewById(R.id.tableRow6);
        tableRow7 = findViewById(R.id.tableRow7);
        sunday = findViewById(R.id.checkBoxSunday);
        nazevPisneTextView = findViewById(R.id.textViewNazevSong);
        imageButton = findViewById(R.id.imageButtonPlay);
        zvysHlasCheckBox = findViewById(R.id.zvysHlasCheckBox);
        vibraceCheckBox = findViewById(R.id.vibracecheckBox);
        urovenHlasitosti = findViewById(R.id.soundLevelSeekBar);
        procentaTextView = findViewById(R.id.textViewProcenta);
        alarmOffButtonradioButton = findViewById(R.id.alarmOffPushButtonRadioButton);
        alarmOffShakenradioButton = findViewById(R.id.alarmOffShakeButtonRadioButton);
        alarmOffCalculateradioButton = findViewById(R.id.alarmOffCalculateButtonRadioButton);
        alarmOffQRradioButton = findViewById(R.id.alarmOffQRButtonRadioButton);
        alarmOffRadioGroup = findViewById(R.id.alarmOffRadioGroup);
        textViewSettings = findViewById(R.id.textViewSettings);
        textViewAdd = findViewById(R.id.textViewAdd);
        floatingActionButton = findViewById(R.id.floatingActionButton2);
        hlidatPohyb = findViewById(R.id.hlidatProbuzeniPohybem);
        linearPriklady = findViewById(R.id.linearLayoutPriklady);
        radioGroupCount = findViewById(R.id.radioGroupCounts);
        radioGroupLevel = findViewById(R.id.radioGroupLevel);
        radioButtonCount1 = findViewById(R.id.radioCounts1);
        radioButtonCount2 = findViewById(R.id.radioCounts2);
        radioButtonCount3 = findViewById(R.id.radioCounts3);
        radioButtonCount4 = findViewById(R.id.radioCounts4);
        radioButtonLevel1 = findViewById(R.id.radioLevel1);
        radioButtonLevel2 = findViewById(R.id.radioLevel2);
        radioButtonLevel3 = findViewById(R.id.radioLevel3);
        radioButtonLevel4 = findViewById(R.id.radioLevel4);
        radioButton9 = findViewById(R.id.radioButton9);
        imageViewLinkQR = findViewById(R.id.imageViewLinkQR);

    }

    private void displayAlarmSettings(){
        int editace = preferences3.getInt("true", -1);
        if (editace > -1) {
            System.out.println("Jedna se o editaci?");
            d = editace;
            detailniNastaveni(nazevPisneTextView);
            preferences2 = getSharedPreferences(String.valueOf(d + 1), MODE_PRIVATE);
            opakovani = preferences2.getString("repeat", "none");
            cestaZvuk = preferences2.getString("cestaZvuk", "Výchozí");
            notifikace = preferences2.getInt("notifikace", 0);
            zvyseniHlasitosti = preferences2.getInt("zvyseniHlasitosti", 0);
            vibrace = preferences2.getInt("vibrace", 0);
            urovenHlasitostiInt = preferences2.getInt("urovenHlasitosti", 100);
            typeOfCancelAlarm = preferences2.getInt("typzruseni", 1);
            countCalculate = preferences2.getInt("countcalculate",1);
            levelCalculate = preferences2.getInt("levelcalculate",1);
            if(preferences2.getString("cestaZvuk", "Výchozí").contains("song") ||preferences2.getString("cestaZvuk", "Výchozí").equals("Výchozí") ){
                String nazevPisne = "";
                radioButton9.setChecked(true);
                switch (preferences2.getString("cestaZvuk", "Výchozí")) {
                    case "song1":
                        nazevPisne = getString(R.string.digitalAlarm);
                        break;
                    case "song2":
                        nazevPisne = getString(R.string.bells);
                        break;
                    case "song3":
                        nazevPisne = getString(R.string.alarmAnalog);
                        break;
                    case "song4":
                        nazevPisne = getString(R.string.alarmPolice);
                        break;
                    case "song5":
                        nazevPisne = getString(R.string.alarmJungle);
                        break;
                    case "song6":
                        nazevPisne = getString(R.string.alarmOcena);
                        break;
                }
                nazevPisneTextView.setVisibility(View.VISIBLE);
                nazevPisneTextView.setText(nazevPisne);
            }

                else {
                radioButton2.setChecked(true);
                nazevPisneTextView.setVisibility(View.VISIBLE);
                String nazevPisne = preferences2.getString("cestaZvuk", "Výchozí");
                    nazevPisne = nazevPisne.substring(nazevPisne.lastIndexOf("/") + 1, nazevPisne.length());
                    nazevPisne = nazevPisne.substring(0, (nazevPisne.lastIndexOf(".") - 1));
                nazevPisneTextView.setText(nazevPisne);

            }
            switch (typeOfCancelAlarm) {
                case 1:
                    alarmOffButtonradioButton.setChecked(true);
                    break;
                case 2:
                    alarmOffShakenradioButton.setChecked(true);
                    break;
                case 3:
                    alarmOffCalculateradioButton.setChecked(true);
                    linearPriklady.setVisibility(View.VISIBLE);
                    switch (countCalculate){
                        case 1:
                            radioButtonCount1.setChecked(true);
                            break;
                        case 2:
                            radioButtonCount2.setChecked(true);
                            break;
                        case 3:
                            radioButtonCount3.setChecked(true);
                            break;
                        case 4:
                            radioButtonCount4.setChecked(true);
                            break;
                    }
                    switch (levelCalculate){
                        case 1:
                            radioButtonLevel1.setChecked(true);
                            break;
                        case 2:
                            radioButtonLevel2.setChecked(true);
                            break;
                        case 3:
                            radioButtonLevel3.setChecked(true);
                            break;
                        case 4:
                            radioButtonLevel4.setChecked(true);
                            break;
                    }
                    break;
                case 4:
                    alarmOffQRradioButton.setChecked(true);
                    break;

            }
            if (zvyseniHlasitosti == 1) {
                zvysHlasCheckBox.setChecked(true);
            }
            if (vibrace == 1) {
                vibraceCheckBox.setChecked(true);
            }
            urovenHlasitosti.setProgress(urovenHlasitostiInt);
            procentaTextView.setText(urovenHlasitostiInt + "%");
            if (opakovani.contains("Po, Út, St, Čt, Pá")) {
                workdays.setChecked(true);
                monday.setChecked(true);
                tuesday.setChecked(true);
                wendsday.setChecked(true);
                thursday.setChecked(true);
                friday.setChecked(true);

            } else {
                if (opakovani.contains("Po")) {
                    monday.setChecked(true);
                }
                if (opakovani.contains("Út")) {
                    tuesday.setChecked(true);
                }
                if (opakovani.contains("St")) {
                    wendsday.setChecked(true);
                }
                if (opakovani.contains("Čt")) {
                    thursday.setChecked(true);
                }
                if (opakovani.contains("Pá")) {
                    friday.setChecked(true);
                }
                if (opakovani.contains("So")) {
                    saturday.setChecked(true);
                }
                if (opakovani.contains("Ne")) {
                    sunday.setChecked(true);
                }

            }
            editor2 = preferences2.edit();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                hoursEdit = preferences2.getLong("time", 0);
                int hoursEditInt = Integer.parseInt(getDate(hoursEdit, "HH"));
                minutesEdit = preferences2.getLong("time", 0);
                int minutesEditInt = Integer.parseInt(getDate(hoursEdit, "mm"));
                timePicker.setHour(hoursEditInt);
                timePicker.setMinute(minutesEditInt);
                editor3.putInt("true", -1);
                editor3.commit();

            }

            editor3.putInt("true", -1);
            editor3.commit();
        } else {
            nazevPisneTextView.setText("Výchozí");
            nazevPisneTextView.setVisibility(View.VISIBLE);
            urovenHlasitosti.setProgress(100);
            d = preferences.getInt("countAlarms", 0);
            preferences2 = getSharedPreferences(String.valueOf(d + 1), MODE_PRIVATE);
            notifikace = preferences.getInt("notifikace", 0);
            editor3.putInt("true", -1);
            editor3.commit();

        }
    }

    private void setListeners(){
        workdays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (workdays.isChecked()) {
                    monday.setChecked(true);
                    tuesday.setChecked(true);
                    wendsday.setChecked(true);
                    thursday.setChecked(true);
                    friday.setChecked(true);

                }
                if (!workdays.isChecked()) {
                    monday.setChecked(false);
                    tuesday.setChecked(false);
                    wendsday.setChecked(false);
                    thursday.setChecked(false);
                    friday.setChecked(false);
                }
            }
        });

        radioButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radioButton2.isChecked()) {
                    Intent videoIntent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(Intent.createChooser(videoIntent, "Select Audio"), audiorequest);
                }
            }

        });

        radioButton9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VytvoritBudik.this, MusicGallery.class);
                intent.putExtra("requestCode", d);
                startActivity(intent);
            }
        });


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mp.isPlaying()) {
                    mp.stop();
                    imageButton.setImageResource(R.drawable.rightplay);

                } else {
                    if (cestaZvuk.equals("Výchozí")) {
                        mp = MediaPlayer.create(context, R.raw.alarm2);
                        mp.start();
                        imageButton.setImageResource(R.drawable.rightpause);
                        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                imageButton.setImageResource(R.drawable.rightplay);
                            }
                        });
                    }
                    else if(cestaZvuk.contains("song")){
                        switch (cestaZvuk){
                            case "song1":
                                mp = MediaPlayer.create(context, R.raw.alarm2);
                                mp.start();
                                imageButton.setImageResource(R.drawable.rightpause);
                                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        imageButton.setImageResource(R.drawable.rightplay);
                                    }
                                });
                                break;
                            case "song2":
                                mp = MediaPlayer.create(context, R.raw.alarmbell);
                                mp.start();
                                imageButton.setImageResource(R.drawable.rightpause);
                                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        imageButton.setImageResource(R.drawable.rightplay);
                                    }
                                });
                                break;
                                case "song3":
                                mp = MediaPlayer.create(context, R.raw.crrr);
                                mp.start();
                                imageButton.setImageResource(R.drawable.rightpause);
                                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        imageButton.setImageResource(R.drawable.rightplay);
                                    }
                                });
                                break;
                            case "song4":
                                mp = MediaPlayer.create(context, R.raw.police);
                                mp.start();
                                imageButton.setImageResource(R.drawable.rightpause);
                                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        imageButton.setImageResource(R.drawable.rightplay);
                                    }
                                });
                                break;
                            case "song5":
                                mp = MediaPlayer.create(context, R.raw.jungle);
                                mp.start();
                                imageButton.setImageResource(R.drawable.rightpause);
                                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        imageButton.setImageResource(R.drawable.rightplay);
                                    }
                                });
                                break;
                            case "song6":
                                mp = MediaPlayer.create(context, R.raw.ocean);
                                mp.start();
                                imageButton.setImageResource(R.drawable.rightpause);
                                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        imageButton.setImageResource(R.drawable.rightplay);
                                    }
                                });
                                break;
                        }

                    }
                    else {
                        Uri uri = Uri.parse("file:" + cestaZvuk);
                        mp = MediaPlayer.create(context, uri);
                        mp.start();
                        imageButton.setImageResource(R.drawable.rightpause);
                        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                imageButton.setImageResource(R.drawable.rightplay);
                            }
                        });

                    }
                }

            }
        });
        alarmOffRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (alarmOffCalculateradioButton.isChecked()) {
                    linearPriklady.setVisibility(View.VISIBLE);
                    imageViewLinkQR.setVisibility(View.GONE);
                } else {
                    linearPriklady.setVisibility(View.GONE);
                    imageViewLinkQR.setVisibility(View.VISIBLE);

                }

                switch (alarmOffRadioGroup.getCheckedRadioButtonId()) {
                    case R.id.alarmOffPushButtonRadioButton:
                        typeOfCancelAlarm = 1;
                        break;
                    case R.id.alarmOffShakeButtonRadioButton:
                        typeOfCancelAlarm = 2;
                        break;
                    case R.id.alarmOffCalculateButtonRadioButton:
                        typeOfCancelAlarm = 3;
                        break;
                    case R.id.alarmOffQRButtonRadioButton:
                        typeOfCancelAlarm = 4;
                        break;

                }
            }
        });
        urovenHlasitosti.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                procentaTextView.setText(i + "%");
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (urovenHlasitosti.getProgress()) / 10, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        radioGroupLevel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                System.out.println("ID level = "+radioGroupLevel.getCheckedRadioButtonId());
                switch (radioGroupLevel.getCheckedRadioButtonId()) {
                    case R.id.radioLevel1:
                        levelCalculate = 1;
                        break;
                    case R.id.radioLevel2:
                        levelCalculate = 2;
                        break;
                    case R.id.radioLevel3:
                        levelCalculate = 3;
                        break;
                    case R.id.radioLevel4:
                        levelCalculate = 4;
                        break;
                }
            }
        });

        radioGroupCount.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radioCounts1:
                        countCalculate=1;
                        break;
                    case R.id.radioCounts2:
                        countCalculate=2;
                        break;
                    case R.id.radioCounts3:
                        countCalculate=3;
                        break;
                    case R.id.radioCounts4:
                        countCalculate=4;
                        break;
                }
            }
        });

        imageViewCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View customView = inflater.inflate(R.layout.popupexplain,null);
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                int width = dm.widthPixels;
                popUp = new PopupWindow(customView, (int)(width*.75), LinearLayout.LayoutParams.WRAP_CONTENT);
                popUp.setContentView(customView);
                popUp.setElevation(4.0f);
                popUp.setOutsideTouchable(true);
                popUp.setFocusable(true);
                popUp.showAtLocation(linearLayoutMain, Gravity.CENTER,0,0);
            }
        });

        imageViewMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View customView = inflater.inflate(R.layout.popmoveexplain,null);
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                int width = dm.widthPixels;
                popUpMove = new PopupWindow(customView, (int)(width*.75), LinearLayout.LayoutParams.WRAP_CONTENT);
                popUpMove.setContentView(customView);
                closeMove = findViewById(R.id.buttonCloseMove);
                popUpMove.setElevation(4.0f);
                popUpMove.setOutsideTouchable(true);
                popUpMove.setFocusable(true);
                popUpMove.showAtLocation(linearLayoutMain, Gravity.CENTER,0,0);

            }
        });

    }
    protected void onResume() {
        super.onResume();
        System.out.println("Pak hned resume");
        if(nacteniSonguBoolean){
            System.out.println("A dal to neslo jo?");
            String nazevPisne=getString(R.string.digitalAlarm);
            cestaZvuk = preferences2.getString("cestaZvuk","song1");
            switch (preferences2.getString("cestaZvuk", "song1")) {
                case "song1":
                    nazevPisne = getString(R.string.digitalAlarm);
                    break;
                case "song2":
                    nazevPisne = getString(R.string.bells);
                    break;
                case "song3":
                    nazevPisne = getString(R.string.alarmAnalog);
                    break;
                case "song4":
                    nazevPisne = getString(R.string.alarmPolice);
                    break;
                case "song5":
                    nazevPisne = getString(R.string.alarmJungle);
                    break;
                case "song6":
                    nazevPisne = getString(R.string.alarmOcena);
                    break;
            }
            nazevPisneTextView.setText(nazevPisne);
        }

        nacteniSonguBoolean = true;

    }

    public void pupUpMoveDismiss(View view){
        popUpMove.dismiss();
    }

    public void popUpDismiss(View view){
        popUp.dismiss();
    }

    public void openQRLink(View view){
        String url = "http://www.qrgenerator.cz/download.php?url=http%3A%2F%2Fchart.apis.google.com%2Fchart%3Fcht%3Dqr%26chs%3D250x250%26chl%3DBud%25C3%25ADkDM59753%26choe%3Dundefined%26chld%3DL";
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

}
