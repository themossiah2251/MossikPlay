package com.example.mossikplay.ui.music;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mossikplay.MusicplayerActivity;
import com.example.mossikplay.databinding.FragmentMusicBinding;

import java.util.ArrayList;

public class MusicFragment extends Fragment {
    private OnSongSelectedListener songSelectedListener;
   ArrayList<String> songList;
    FragmentMusicBinding binding;
    MediaPlayer mediaPlayer;

    public interface OnSongSelectedListener {
        void onSongSelected(String songPath);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMusicBinding.inflate(inflater, container, false);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("MusicFragment", "Song completed.");
            }
        });
        songList = getAllSongs();
        SongsAdapter adapter = new SongsAdapter(songList);
        RecyclerView recyclerView = binding.listView;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        return binding.getRoot();
    }
    class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongViewHolder> {
        private final ArrayList<String> songs;

        SongsAdapter(ArrayList<String> songs) {
            this.songs = songs;
        }

        @NonNull
        @Override
        public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new SongViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
            String song = songs.get(position);

            holder.bind(song);
        }

        @Override
        public int getItemCount() {
            return songs.size();
        }

        class SongViewHolder extends RecyclerView.ViewHolder {
            TextView songName;

            SongViewHolder(@NonNull View itemView) {
                super(itemView);
                songName = itemView.findViewById(android.R.id.text1);
            }

            void bind(final String songPath) {
                songName.setText(songPath);
                Log.d("MusicFragment", "Song clicked: " + songPath);
                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), MusicplayerActivity.class);
                    intent.putExtra("songPath", songPath);
                    startActivity(intent);
                });

            }
        }
    }

    @NonNull
    private ArrayList<String> getAllSongs() {
        ArrayList<String> songList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;


        if (getActivity() != null) {
            String[] projection = {MediaStore.Audio.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);

            if (cursor != null) {
                try {
                    int columnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                    if (columnIndex != -1) {
                        while (cursor.moveToNext()) {
                            String songPath = cursor.getString(columnIndex);
                            if (songPath != null) {
                                songList.add(songPath);
                            }
                        }
                    } else {
                        Toast.makeText(getContext(), "Column not found.", Toast.LENGTH_SHORT).show();
                    }
                } finally {
                    cursor.close();
                }
            } else {
                Toast.makeText(getContext(), "Unable to retrieve songs.", Toast.LENGTH_SHORT).show();
            }
        }



        for (String path : songList) {
            Log.d("SongPath", path);
        }

        return songList;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}