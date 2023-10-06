package com.example.mossikplay.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.example.mossikplay.R;
import com.example.mossikplay.databinding.FragmentHomeBinding;
import com.example.mossikplay.ui.music.MusicFragment;
import com.example.mossikplay.ui.playlists.PlaylistFragment;

public class HomeFragment extends Fragment {
    Button musicButton;
     Button playlistButton;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        musicButton = root.findViewById(R.id.music);
        playlistButton = root.findViewById(R.id.playlist);
        musicButton.setOnClickListener(view -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new MusicFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });
        playlistButton.setOnClickListener(view -> {
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new PlaylistFragment());
                transaction.addToBackStack(null);
                transaction.commit();


    });
        return root;
}


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}