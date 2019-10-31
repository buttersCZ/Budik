package appdm.example.davidmacak.budak;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

public class MusicGallery extends AppCompatActivity {
    int request;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    RadioGroup RadioGroupOwnSongs;
    Button okSong;
    String songString;
    Context context;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.musicgallery);
        RadioGroupOwnSongs = findViewById(R.id.RadioGroupOwnSongs);
        okSong = findViewById(R.id.okSong);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*.75),(int)(height*.5));
        request = getIntent().getIntExtra("requestCode",0);
        System.out.println("Req "+request);
        preferences = getSharedPreferences(String.valueOf(request+1),MODE_PRIVATE);
        editor = preferences.edit();
        songString = preferences.getString("cestaZvuk","Výchozí");
        context =this;
        mp = new MediaPlayer();
        if(!songString.equals("Výchozí")&& songString.contains("song")){
            switch (songString){
                case "song1":
                    RadioGroupOwnSongs.check(R.id.song1);
                    break;
                case "song2":
                    RadioGroupOwnSongs.check(R.id.song2);
                    break;
                case "song3":
                    RadioGroupOwnSongs.check(R.id.song3);
                    break;
                case "song4":
                    RadioGroupOwnSongs.check(R.id.song4);
                    break;
                case "song5":
                    RadioGroupOwnSongs.check(R.id.song5);
                    break;
                case "song6":
                    RadioGroupOwnSongs.check(R.id.song6);
                    break;
            }
        }

        RadioGroupOwnSongs.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.song1:
                        songString = "song1";
                        if(mp.isPlaying()){
                            mp.stop();
                        }
                        mp = MediaPlayer.create(context, R.raw.alarm2);
                        mp.start();
                        break;
                    case R.id.song2:
                        songString = "song2";
                        if(mp.isPlaying()){
                            mp.stop();
                        }
                        mp = MediaPlayer.create(context, R.raw.alarmbell);
                        mp.start();
                        break;
                    case R.id.song3:
                        songString = "song3";
                        if(mp.isPlaying()){
                            mp.stop();
                        }
                        mp = MediaPlayer.create(context, R.raw.crrr);
                        mp.start();
                        break;
                    case R.id.song4:
                        songString = "song4";
                        if(mp.isPlaying()){
                            mp.stop();
                        }
                        mp = MediaPlayer.create(context, R.raw.police);
                        mp.start();
                        break;
                    case R.id.song5:
                        songString = "song5";
                        if(mp.isPlaying()){
                            mp.stop();
                        }
                        mp = MediaPlayer.create(context, R.raw.jungle);
                        mp.start();
                        break;
                    case R.id.song6:
                        songString = "song6";
                        if(mp.isPlaying()){
                            mp.stop();
                        }
                        mp = MediaPlayer.create(context, R.raw.ocean);
                        mp.start();
                        break;
                }
            }
        });

        okSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("cestaZvuk",songString);
                editor.commit();
                if(mp.isPlaying()){
                    mp.stop();
                }
                finish();
            }
        });


    }


}
