package sg.edu.np.mad.EfficenZ;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


import java.util.ArrayList;

public class MusicPlayer extends AppCompatActivity implements RecyclerViewInterface {

    ArrayList<song> songs = new ArrayList<>();
    ImageButton ppbtn, rightskipbtn, leftskipbtn;
    MediaPlayer mediaPlayer;
    int[] songMp3Array = {R.raw.lofi_song_1, R.raw.lofi_song_2, R.raw.lofi_song_3, R.raw.lofi_song_4, R.raw.lofi_song_5, R.raw.lofi_song_6, R.raw.lofi_song_7, R.raw.cafe_ambience, R.raw.rain_and_storm, R.raw.rain_rainforest,
    };
    int currentSongIndex = 0;

    TextView songTitleTextView;
    String currentSongName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        // Implementing recycler view adapter
        RecyclerView recyclerView = findViewById(R.id.mRecyclerView);
        recycler_view_adapter adapter = new recycler_view_adapter(this, songs, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        songTitleTextView = findViewById(R.id.textView_songTitle);

        setUpSongsList();

        for (int i = 0; i < songs.size(); i++) {
            song temp = songs.get(i);
            String mp3 = Integer.toString(temp.songMp3);

            Log.d("SONG NAME", temp.songName);
            Log.d("MP3", mp3);
        }

        mediaPlayer = MediaPlayer.create(getApplicationContext(), songMp3Array[currentSongIndex]);

        ppbtn = findViewById(R.id.ppbtn);
        leftskipbtn = findViewById(R.id.leftskipbtn);
        rightskipbtn = findViewById(R.id.rightskipbtn);

        if (mediaPlayer.isPlaying()){
            ppbtn.setBackgroundResource(R.drawable.pause_button);
            songTitleTextView.setText(currentSongName);
        } else {
            ppbtn.setBackgroundResource(R.drawable.play_button);
        }

        //calling methods for skipping, pause and play
        ppbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleMediaPlayer();
            }
        });

        leftskipbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skipToPreviousSong();
            }
        });

        rightskipbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skipToNextSong();
            }
        });

    }

    // method for pause/play button. when pressed, background resource will be switched from pause to play button drawable
    private void toggleMediaPlayer() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            ppbtn.setBackgroundResource(R.drawable.play_button);
        } else {
            mediaPlayer.start();
            ppbtn.setBackgroundResource(R.drawable.pause_button);
        }
    }


    //method to skip to previous song
    private void skipToPreviousSong() {
        if (currentSongIndex > 0) {
            currentSongIndex--;
            mediaPlayer.reset();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), songMp3Array[currentSongIndex]);
            mediaPlayer.start();
            ppbtn.setBackgroundResource(R.drawable.pause_button);
            currentSongName = songs.get(currentSongIndex).getSongName();
        }
    }

    //method to skip to next song
    private void skipToNextSong() {
        if (currentSongIndex < songMp3Array.length - 1) {
            currentSongIndex++;
            mediaPlayer.reset();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), songMp3Array[currentSongIndex]);
            mediaPlayer.start();
            ppbtn.setBackgroundResource(R.drawable.pause_button);
            currentSongName = songs.get(currentSongIndex).getSongName();
        }
    }

    //method to create song objects and store in songNames array
    private void setUpSongsList() {
        String[] songNames = getResources().getStringArray(R.array.song_titles);

        for (int i = 0; i < songNames.length; i++) {
            songs.add(new song(songNames[i], songMp3Array[i]));
        }
    }

    //method to pause and play song
    @Override
    public void onItemClick(int position) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }

        else {
            song temp = songs.get(position);
            currentSongIndex = position;
            mediaPlayer.reset();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), temp.songMp3);
            mediaPlayer.start();
            ppbtn.setBackgroundResource(R.drawable.pause_button);
            currentSongName = temp.getSongName();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (mediaPlayer.isPlaying()) {

            for (int i = 0; i < songs.size(); i++) {

                song temp = songs.get(i);
                int tempSongMp3 = temp.songMp3;

                if (tempSongMp3 == songMp3Array[currentSongIndex]) {
                    currentSongName = temp.songName;
                }
            }


            Intent MainActivity = new Intent(MusicPlayer.this, sg.edu.np.mad.EfficenZ.MainActivity.class);
            MainActivity.putExtra("Song title", currentSongName);
            startActivity(MainActivity);
        }

        else {
            Intent MainActivity = new Intent(MusicPlayer.this, sg.edu.np.mad.EfficenZ.MainActivity.class);
            MainActivity.putExtra("Song title", "");
            startActivity(MainActivity);
        }
    }
}
