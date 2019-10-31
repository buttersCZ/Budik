package appdm.example.davidmacak.budak;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ebanx.swipebtn.OnStateChangeListener;
import com.ebanx.swipebtn.SwipeButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.nipunbirla.qrreaderview.QRCodeReaderView;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class PopUp extends AppCompatActivity implements SensorEventListener,QRCodeReaderView.OnQRCodeReadListener {
    MediaPlayer mp;
    private CountDownTimer odpocet2;
    int count = 2;
    Context context;
    boolean vypnoutBudik = true;
    SharedPreferences preferences;
    SharedPreferences preferences2;
    SharedPreferences.Editor editor2;
    SharedPreferences.Editor editor;
    SensorEventListener sensorEventListener;
    Intent intent;
    String opakovani;
    int budikID;
    int[] opakovaniINT;
    boolean jizBudikNastaven = false;
    AudioManager audioManager;
    Vibrator vib;
    int interakce;
    int vibrace;
    int urovenHlasitosti;
    int zvyseniHlasitosti;
    int maximumVolume;
    int hlidatPohybem;
    long[] pattern = {0, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000};
    final Handler handler = new Handler();
    private SensorManager sensorMan;
    private SensorManager sensorManShake;
    private Sensor shaking;
    private Sensor accelerometer;
    int pocetpohybu =0;
    int typeOfCancelAlarm;
    SwipeButton swipeButton;
    long casBudikuProNotifikace = 0;
    NotificationManager notificationManager;
    TextView shake;
    ImageView shakeImageView;
    ImageView budikImageView;
    int pocetPrikladu;
    int urovenPrikladu;
    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    Window win;
    TextView cisloPrikladu1;
    TextView cisloPrikladu2;
    TextView znamenkoPrikladu;
    EditText vysledekEditText;
    int vysledekPrikladu;
    ImageButton imageButtonVysledek;
    int vyresenoUloh =0;
    TextView countTextView;
    Button scan;
    TextView textView;
    TextView scanQrTextView;
    boolean result;
    private QRCodeReaderView qrReaderView;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(this, "ca-app-pub-9621446697711460~9021037060");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-9621446697711460/3120530040");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        if (getIntent().getBooleanExtra("vypnoutAlarm", false)) {
            TextView textView = new TextView(this);
            vypniAlarm(textView);
        }
        else {
            nacteniObrazovky();

        }


    }

    private void nacteniObrazovky(){
        context = this;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mp = new MediaPlayer();
        win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
        WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON|WindowManager.LayoutParams.FLAG_FULLSCREEN);
        intent = getIntent();
        budikID = intent.getIntExtra("requestCode", 0);
        preferences = getSharedPreferences(String.valueOf(budikID), MODE_PRIVATE);
        preferences2 = getSharedPreferences("settings", MODE_PRIVATE);
        editor2 = preferences2.edit();
        editor = preferences.edit();
        opakovani = preferences.getString("repeat", "none");
        interakce = preferences2.getInt("notifikace", 0);
        vibrace = preferences.getInt("vibrace", 0);
        urovenHlasitosti = preferences.getInt("urovenHlasitosti", 100);
        zvyseniHlasitosti = preferences.getInt("zvyseniHlasitosti", 0);
        typeOfCancelAlarm = preferences.getInt("typzruseni",1);
        hlidatPohybem = preferences.getInt("hlidatPohybem",0);
        switch (typeOfCancelAlarm){
            case 1:
                setContentView(R.layout.popup);
                swipeButton = findViewById(R.id.swipeButton);
                budikImageView = findViewById(R.id.imageViewBudik);
                Animation animationB = AnimationUtils.loadAnimation(context,R.anim.rotate);
                budikImageView.setAnimation(animationB);
                swipeButton.setOnStateChangeListener(new OnStateChangeListener() {
                    @Override
                    public void onStateChange(boolean active) {
                        vypniAlarm(swipeButton);
                    }
                });
                break;
            case 2:
                setContentView(R.layout.zkusebni);
                shake = findViewById(R.id.textViewShake);
                shakeImageView = findViewById(R.id.shakeImageView);
                Animation animationS = AnimationUtils.loadAnimation(context,R.anim.shake);
                shakeImageView.startAnimation(animationS);
                break;
            case 3:
                pocetPrikladu = preferences.getInt("countcalculate",1);
                urovenPrikladu = preferences.getInt("levelcalculate",1);
                setContentView(R.layout.priklady);
                cisloPrikladu1 = findViewById(R.id.cisloPrikladu1);
                cisloPrikladu2 = findViewById(R.id.cisloPrikladu2);
                znamenkoPrikladu = findViewById(R.id.znamenkoPrikladu);
                vysledekEditText = findViewById(R.id.editTextVysledek);
                imageButtonVysledek = findViewById(R.id.imageButtonVysledek);
                countTextView = findViewById(R.id.textViewCount);
                budikImageView = findViewById(R.id.shakeImageViewCounts);
                Animation animationC = AnimationUtils.loadAnimation(context,R.anim.rotate);
                budikImageView.setAnimation(animationC);
                if(pocetPrikladu>1){
                    countTextView.setText("1 / "+pocetPrikladu);
                    countTextView.setVisibility(View.VISIBLE);
                }
                vytvorPriklad(urovenPrikladu);
                imageButtonVysledek.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int vysledekUzivatel = 0;
                        if(!vysledekEditText.getText().toString().equals("")){
                             vysledekUzivatel = Integer.parseInt(vysledekEditText.getText().toString());
                        }
                        else {
                            Toast.makeText(context,"Neplatná hodnota výsledku",Toast.LENGTH_SHORT).show();
                        }
                        if(vysledekUzivatel==vysledekPrikladu){
                            vysledekEditText.setText("");
                            vyresenoUloh++;
                            if(vyresenoUloh==pocetPrikladu){
                                vypniAlarm(imageButtonVysledek);
                            }
                            else {
                                countTextView.setText((vyresenoUloh+1)+" / "+pocetPrikladu);
                                vytvorPriklad(urovenPrikladu);
                            }
                        }
                        else {
                            Toast.makeText(context,"Špatný výsledek",Toast.LENGTH_SHORT).show();
                            vysledekEditText.setText("");
                        }
                    }
                });
                break;
            case 4:
                Lala();
                break;
        }

        opakovaniINT = new int[8];
        sensorMan = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        if (opakovani.contains("Ne")) {
            opakovaniINT[7] = 1;
        }
        if (opakovani.contains("Po")) {
            opakovaniINT[1] = 1;
        }
        if (opakovani.contains("Út")) {
            opakovaniINT[2] = 1;
        }
        if (opakovani.contains("St")) {
            opakovaniINT[3] = 1;
        }
        if (opakovani.contains("Čt")) {
            opakovaniINT[4] = 1;
        }
        if (opakovani.contains("Pá")) {
            opakovaniINT[5] = 1;
        }
        if (opakovani.contains("So")) {
            opakovaniINT[6] = 1;
        }

        spustitAlarm();
    }

    public void spustitAlarm() {
        final String cestaZvuk = preferences.getString("cestaZvuk", "Výchozí");
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(true);
        if (cestaZvuk.equals("Výchozí") || cestaZvuk.contains("song")) {
            mp = MediaPlayer.create(context, R.raw.alarm2);
            switch (cestaZvuk){
                case "song1":
                    mp = MediaPlayer.create(context, R.raw.alarm2);
                    break;
                case "song2":
                    mp = MediaPlayer.create(context, R.raw.alarmbell);
                    break;
                case "song3":
                    mp = MediaPlayer.create(context, R.raw.crrr);
                    break;
                case "song4":
                    mp = MediaPlayer.create(context, R.raw.police);
                    break;
                case "song5":
                    mp = MediaPlayer.create(context, R.raw.jungle);
                    break;
                case "song6":
                    mp = MediaPlayer.create(context, R.raw.ocean);
                    break;

            }
            if (zvyseniHlasitosti == 0) {
                maximumVolume = audioManager.getStreamMaxVolume(audioManager.STREAM_MUSIC)*100;
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, ((maximumVolume /100)*urovenHlasitosti)/100, 0);
                audioManager.setSpeakerphoneOn(true);
                if (vibrace == 1) {
                    vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vib.vibrate(pattern, 0);
                    System.out.println("Vibrace zapnuty");

                }
                if(mp!=null){
                    audioManager.requestAudioFocus(null, AudioManager.STREAM_VOICE_CALL,
                            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                    mp.start();
                }

                if(mp!=null){
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            if (vypnoutBudik) {
                                spustitAlarm();

                            }

                        }
                    });
                }
            } else {
                maximumVolume = audioManager.getStreamMaxVolume(audioManager.STREAM_MUSIC)*100;
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, ((maximumVolume /100)*urovenHlasitosti)/600, 0);
                audioManager.setSpeakerphoneOn(true);

                if (vibrace == 1) {
                    vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vib.vibrate(pattern, 0);

                }
                audioManager.requestAudioFocus(null, AudioManager.STREAM_VOICE_CALL,
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        if (vypnoutBudik) {
                            handler.removeCallbacksAndMessages(null);
                            zvyseniHlasitosti = 0;
                            spustitAlarm();

                        }

                    }
                });
                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        count++;
                        if (count < 7) {
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, ((maximumVolume /100)*urovenHlasitosti)/600*count, 0);
                            audioManager.setSpeakerphoneOn(true);
                            handler.postDelayed(this, 7000);
                        }

                    }


                };
                handler.post(runnable);


            }

        } else {
            if (zvyseniHlasitosti == 0) {
                maximumVolume = audioManager.getStreamMaxVolume(audioManager.STREAM_MUSIC)*100;
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, ((maximumVolume /100)*urovenHlasitosti)/100, 0);
                audioManager.setSpeakerphoneOn(true);

                if (vibrace == 1) {
                    vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vib.vibrate(pattern, 0);

                }
                Uri uri = Uri.parse("file:" + cestaZvuk);
                mp = MediaPlayer.create(context, uri);
                audioManager.requestAudioFocus(null, AudioManager.STREAM_VOICE_CALL,
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                mp.start();

                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        if (vypnoutBudik) {
                            spustitAlarm();

                        }

                    }
                });
            } else {
                maximumVolume = audioManager.getStreamMaxVolume(audioManager.STREAM_MUSIC)*100;
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, ((maximumVolume /100)*urovenHlasitosti)/600, 0);
                audioManager.setSpeakerphoneOn(true);
                if (vibrace == 1) {
                    vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vib.vibrate(pattern, 0);

                }
                Uri uri = Uri.parse("file:" + cestaZvuk);
                mp = MediaPlayer.create(context, uri);
                audioManager.requestAudioFocus(null, AudioManager.STREAM_VOICE_CALL,
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                mp.start();

                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        if (vypnoutBudik) {
                            handler.removeCallbacksAndMessages(null);
                            zvyseniHlasitosti = 0;
                            spustitAlarm();


                        }

                    }
                });

                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (count < 7) {
                            if (count == 6) {
                                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, ((maximumVolume /100)*urovenHlasitosti)/100, 0);
                                audioManager.setSpeakerphoneOn(true);

                            } else {
                                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, ((maximumVolume /100)*urovenHlasitosti)/600*count, 0);
                                audioManager.setSpeakerphoneOn(true);
                            }
                            handler.postDelayed(this, 20000);
                            count++;

                        }

                    }


                };
                handler.post(runnable);

            }

        }

        if(typeOfCancelAlarm==2){
            sensorMan.registerListener(sensorEventListener = new SensorEventListener() {
                        @Override
                        public void onSensorChanged(SensorEvent sensorEvent) {
                            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                                mGravity = sensorEvent.values.clone();

                                float x = mGravity[0];
                                float y = mGravity[1];
                                float z = mGravity[2];
                                mAccelLast = mAccelCurrent;
                                mAccelCurrent = (float) Math.sqrt(x * x + y * y + z * z);
                                float delta = mAccelCurrent - mAccelLast;
                                mAccel = mAccel * 0.9f + delta;
                                if (mAccel > 4) {
                                    if(pocetpohybu>10){
                                        if(odpocet2!=null){
                                            odpocet2.cancel();
                                        }
                                        sensorMan.unregisterListener(this);
                                        vypniAlarm(shake);
                                    }
                                    pocetpohybu++;
                                }
                            }
                        }

                        @Override
                        public void onAccuracyChanged(Sensor sensor, int i) {

                        }
                    }, accelerometer,
                    SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void vypniAlarm(View view) {
        mInterstitialAd.show();
        vypnoutBudik = false;
        if (mp.isPlaying()) {
            mp.stop();
        }
        if (vib != null) {
            vib.cancel();
        }
        if (opakovani.equals("")) {
            if(hlidatPohybem==1){
                odpocetPohybu();
                startSplashTimer();
            }

            znovuNenastavovat();
            interakce = interakce - 1;
            if (interakce == 0) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(0);
            }
            editor2.putInt("notifikace", interakce);
            editor2.commit();
        } else {
            if(hlidatPohybem==1){
                odpocetPohybu();
                startSplashTimer();
            }
            znovuNastavit();


        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        } else {
            finish();
        }
    }

    public void znovuNenastavovat() {
        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(this, Probuzeni.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), (budikID - 1), myIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        alarmMgr.cancel(pendingIntent);
        pendingIntent.cancel();
        zjistiPocetAktivnichBudiku();


    }

    public void znovuNastavit() {
        Date date = new Date(new Date().getTime());
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        for (int b = 1; b < 8; b++) {
            c.setTime(date);
            c.add(Calendar.DATE, b);
            if (opakovaniINT[(c.get(Calendar.DAY_OF_WEEK)) - 1] == 1 && !jizBudikNastaven) {
                AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(this, Probuzeni.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (budikID - 1), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmMgr.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                editor.putLong("time",c.getTimeInMillis());
                editor.commit();
            }
            jizBudikNastaven = true;
        }
        zjistiPocetAktivnichBudiku();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = sensorEvent.values.clone();

            float x = mGravity[0];
            float y = mGravity[1];
            float z = mGravity[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt(x * x + y * y + z * z);
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;

            if (mAccel > 2) {
                Toast.makeText(this, "HUUUU", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    private void startSplashTimer() {
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Nastavuje se posluchač");
                        sensorMan.registerListener(sensorEventListener = new SensorEventListener() {
                                                       @Override
                                                       public void onSensorChanged(SensorEvent sensorEvent) {
                                                           if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                                                               mGravity = sensorEvent.values.clone();

                                                               float x = mGravity[0];
                                                               float y = mGravity[1];
                                                               float z = mGravity[2];
                                                               mAccelLast = mAccelCurrent;
                                                               mAccelCurrent = (float) Math.sqrt(x * x + y * y + z * z);
                                                               float delta = mAccelCurrent - mAccelLast;
                                                               mAccel = mAccel * 0.9f + delta;
                                                               System.out.println("MAccel = "+mAccel);
                                                               if (mAccel > 1) {
                                                                   if(pocetpohybu>10){
                                                                       odpocet2.cancel();
                                                                       sensorMan.unregisterListener(this);
                                                                   }
                                                                   pocetpohybu++;
                                                               }
                                                           }
                                                       }

                                                       @Override
                                                       public void onAccuracyChanged(Sensor sensor, int i) {

                                                       }
                                                   }, accelerometer,
                                SensorManager.SENSOR_DELAY_UI);
                    }
                }, 30000);
    }

    private void odpocetPohybu() {

        odpocet2 = new CountDownTimer(140000, 1000) {

            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                startActivity(getIntent());
                sensorMan.unregisterListener(sensorEventListener);


            }
        };
        odpocet2.start();
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
                    new Intent(PopUp.this, Probuzeni.class),
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

    private void vytvorPriklad(int uroven){
        int cislo1;
        int cislo2;
        int znamenko;
        switch (uroven){
            case 1:
                cislo1 = new Random().nextInt(20 - 1)+1;
                cislo2 = new Random().nextInt(20 - 1)+1;
                znamenko = new Random().nextInt(3-1)+1;
                cisloPrikladu1.setText(String.valueOf(cislo1));
                cisloPrikladu2.setText(String.valueOf(cislo2));
                switch (znamenko){
                    case 1:
                        znamenkoPrikladu.setText("+");
                        vysledekPrikladu = cislo1+cislo2;
                        break;
                    case 2:
                        znamenkoPrikladu.setText("-");
                        vysledekPrikladu = cislo1-cislo2;
                        break;
                    case 3:
                        znamenkoPrikladu.setText("*");
                        vysledekPrikladu = cislo1*cislo2;
                        break;
                }
                break;
            case 2:
                cislo1 = new Random().nextInt(100 - 20)+1;
                cislo2 = new Random().nextInt(100 - 20)+1;
                znamenko = new Random().nextInt(3-1)+1;
                cisloPrikladu1.setText(String.valueOf(cislo1));
                cisloPrikladu2.setText(String.valueOf(cislo2));
                switch (znamenko){
                    case 1:
                        znamenkoPrikladu.setText("+");
                        vysledekPrikladu = cislo1+cislo2;
                        break;
                    case 2:
                        znamenkoPrikladu.setText("-");
                        vysledekPrikladu = cislo1-cislo2;
                        break;
                    case 3:
                        znamenkoPrikladu.setText("*");
                        vysledekPrikladu = cislo1*cislo2;
                        break;
                }
                break;
            case 3:
                cislo1 = new Random().nextInt(20 - 1)+1;
                cislo2 = new Random().nextInt(20 - 1)+1;
                znamenko = 3;
                cisloPrikladu1.setText(String.valueOf(cislo1));
                cisloPrikladu2.setText(String.valueOf(cislo2));
                switch (znamenko){
                    case 1:
                        znamenkoPrikladu.setText("+");
                        vysledekPrikladu = cislo1+cislo2;
                        break;
                    case 2:
                        znamenkoPrikladu.setText("-");
                        vysledekPrikladu = cislo1-cislo2;
                        break;
                    case 3:
                        znamenkoPrikladu.setText("*");
                        vysledekPrikladu = cislo1*cislo2;
                        break;
                }
                break;
            case 4:
                cislo1 = new Random().nextInt(100 - 20)+1;
                cislo2 = new Random().nextInt(20 - 1)+1;
                znamenko = 3;
                cisloPrikladu1.setText(String.valueOf(cislo1));
                cisloPrikladu2.setText(String.valueOf(cislo2));
                switch (znamenko){
                    case 1:
                        znamenkoPrikladu.setText("+");
                        vysledekPrikladu = cislo1+cislo2;
                        break;
                    case 2:
                        znamenkoPrikladu.setText("-");
                        vysledekPrikladu = cislo1-cislo2;
                        break;
                    case 3:
                        znamenkoPrikladu.setText("*");
                        vysledekPrikladu = cislo1*cislo2;
                        break;
                }
                break;
        }
    }

    public void Lala() {

        setContentView(R.layout.qr);
        scanQrTextView = findViewById(R.id.textViewQR);

        qrReaderView = findViewById(R.id.qrread);
        qrReaderView.setOnQRCodeReadListener(this);
        qrReaderView.setQRDecodingEnabled(true);
        qrReaderView.setAutofocusInterval(2000L);
        qrReaderView.setTorchEnabled(true);
        qrReaderView.setBackCamera();
    }

        @Override
        public void onBackPressed()
        {
        }
    @Override
    protected void onPause() {
        super.onPause();

        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        if(text.equals("BudíkDM59753")){
            vypniAlarm(scanQrTextView);
        }
        else {
            Toast.makeText(context,"Špatný QR kód",Toast.LENGTH_SHORT).show();
        }
    }
}
