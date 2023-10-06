package com.example.mossikplay.ui.music;

import android.content.Context;
import android.database.Cursor;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;



import com.example.mossikplay.R;
import com.example.mossikplay.databinding.FragmentMusicBinding;


import java.io.IOException;
import java.util.ArrayList;

public class MusicFragment extends Fragment {
    private OnSongSelectedListener songSelectedListener;
    private ArrayList<String> songList;
     FragmentMusicBinding binding;
private MediaPlayer mediaPlayer;
    public interface OnSongSelectedListener {
        void onSongSelected(String songPath);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSongSelectedListener) {
            songSelectedListener = (OnSongSelectedListener) context;
        } else {
            throw new ClassCastException("Activity must implement OnSongSelectedListener");
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentMusicBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        ListView listView = rootView.findViewById(R.id.listView);
        songList = getAllSongs();


        if (getContext() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, songList);
            listView.setAdapter(adapter);
        }

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedSongPath = songList.get(position);
            songSelectedListener.onSongSelected(selectedSongPath);
            playSong(selectedSongPath);

        });

        mediaPlayer = new MediaPlayer();

        return rootView;
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
    public void playSong(String songPath){
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(songPath);

            mediaPlayer.prepare();
            mediaPlayer.start();
        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(getContext(),"ERROR.",Toast.LENGTH_SHORT).show();
        }
    }

}