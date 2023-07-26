package sg.edu.np.mad.EfficenZ;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sg.edu.np.mad.EfficenZ.ui.notes.NotesList;

public class ToolsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tools, container, false);
    }

    CardView task, notes, timer, music, calendar, chat, achievement, settings;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags){
            case Configuration.UI_MODE_NIGHT_YES:
                getActivity().getWindow().setStatusBarColor(getActivity().getColor(R.color.bg_color_dark));
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                getActivity().getWindow().setStatusBarColor(getActivity().getColor(R.color.bg_color));
                break;

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                break;
        }

        task = getView().findViewById(R.id.taskListBtn);
        task.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), TaskManagement.class);
            startActivity(intent);
        });

        notes = getView().findViewById(R.id.notesBtn);
        notes.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NotesList.class);
            startActivity(intent);
        });

        // TODO: DARK MODE TIMER
        timer = getView().findViewById(R.id.timerBtn);
        timer.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), TimeManagement.class);
            startActivity(intent);
        });

        music = getView().findViewById(R.id.musicBtn);
        music.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MusicPlayer.class);
            startActivity(intent);
        });

        // TODO: MARCUS WHY DOES YOUR CALENDAR NOT DARK MODE
        calendar = getView().findViewById(R.id.calendarBtn);
        calendar.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CalendarTask.class);
            startActivity(intent);
        });

        chat = getView().findViewById(R.id.chatBtn);
        chat.setOnClickListener(v -> {
            // TODO: CHAT ACTIVITY
        });

        achievement = getView().findViewById(R.id.achievementBtn);
        achievement.setOnClickListener(v -> {
            // TODO: ACHIEVEMENT ACTIVITY
        });

        settings = getView().findViewById(R.id.settingsBtn);
        settings.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            startActivity(intent);
        });
    }
}