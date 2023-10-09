package com.example.mossikplay;


import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MusicplayerActivity extends AppCompatActivity  {
    private MediaPlayer mediaPlayer;
     ImageView playButton, pauseButton, stopButton, nextSongButton, lastSongButton;
    private SeekBar seekBar;

    private int currentSongIndex = 0;
    TextView durationTextView, songTitleTextView;
    List<String> songList = new ArrayList<>();
    private final Handler handler = new Handler();
    private boolean isPaused = false;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_musicplayer);
            setupUIComponents();
            setupMediaPlayer();
            scanMusic();

            String selectedSongPath = getIntent().getStringExtra("songPath");
            if (selectedSongPath != null) {
                playSong(songList.indexOf(selectedSongPath));
            }
        }

        private void setupUIComponents() {
            nextSongButton = findViewById(R.id.nextSo);
            lastSongButton = findViewById(R.id.lastSo);
            playButton = findViewById(R.id.playButton);
            pauseButton = findViewById(R.id.pauseButton);
            stopButton = findViewById(R.id.stopButton);
            songTitleTextView = findViewById(R.id.songTitleTextView);
            seekBar = findViewById(R.id.seekBar);
            durationTextView = findViewById(R.id.durationTextView);
            playButton.setOnClickListener(v -> {
                if (isPaused) {

                    mediaPlayer.start();
                    isPaused = false;
                } else {
                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
                }
                updateSeekBar();

            });
            nextSongButton = findViewById(R.id.nextSo);
            nextSongButton.setOnClickListener(v ->
            {
                if (currentSongIndex < songList.size() - 1) {
                    currentSongIndex++;
                    playSong(currentSongIndex);
                }
            });
            lastSongButton = findViewById(R.id.lastSo);
            lastSongButton.setOnClickListener(v ->
            {
                if (currentSongIndex > 0) {
                    currentSongIndex--;
                    playSong(currentSongIndex);
                }
            });


            pauseButton.setOnClickListener(v -> {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    isPaused = true;
                }
            });
            stopButton.setOnClickListener(v -> {
                mediaPlayer.stop();
                mediaPlayer.reset();
                try {

                    mediaPlayer.setDataSource(songList.get(currentSongIndex));
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                isPaused = true;
            });
        }
        private void setupMediaPlayer() {
            mediaPlayer = new MediaPlayer();


            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.e("MediaPlayerError", "Error occurred: " + what);
                    return true;
                }
            });



        scanMusic();
            String selectedSongPath = getIntent().getStringExtra("songPath");
            if (selectedSongPath != null) {
                playSong(songList.indexOf(selectedSongPath));
            }
    }

    private void scanMusic() {
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE
        };
        String selection = MediaStore.Audio.Media.IS_MUSIC + " !=0";
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(musicUri, projection, selection, null, null);
        if (cursor != null) {
            String[] columnNames = cursor.getColumnNames();
            for (String columnName : columnNames) {
                Log.d("Column Name", columnName);
            }
            while (cursor.moveToNext()) {
                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                int titleColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);

                if (dataColumnIndex != -1 && titleColumnIndex != -1) {
                    String filePath = cursor.getString(dataColumnIndex);

                    songList.add(filePath);
                } else {
                    if (dataColumnIndex == -1) {
                        Log.e("Column Error", "MediaStore.Audio.Media.DATA column not found in cursor");
                    }
                    if (titleColumnIndex == -1) {
                        Log.e("Column Error", "MediaStore.Audio.Media.TITLE column not found in cursor");
                    }

                }
            }

            cursor.close();
        }

        mediaPlayer.setOnCompletionListener((MediaPlayer mp) -> stopButton.performClick());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                    durationTextView.setText(formatDuration(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void playSong(int index) {
        if (index >= 0 && index < songList.size()) {
            String songPath = songList.get(index);
            String songTitle = songPath.substring(songPath.lastIndexOf("/") + 1, songPath.lastIndexOf("."));
            songTitleTextView.setText(songTitle);
            Log.d("MusicplayerActivity", "Attempting to play: " + songPath);
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.reset();
                mediaPlayer.setDataSource(songPath);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();

            }
            Log.d("MusicplayerActivity", "Song started playing.");
        }
    }

    private void updateSeekBar() {
        int duration = mediaPlayer.getDuration();
        seekBar.setMax(duration);
        Runnable runnable = () -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(currentPosition);
                durationTextView.setText(formatDuration(currentPosition));
                handler.postDelayed(this::updateSeekBar, 1000);
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    private String formatDuration(int duration) {
        int minutes = duration / 60000;
        int seconds = (duration % 60000) / 1000;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);







    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null);
    }
}