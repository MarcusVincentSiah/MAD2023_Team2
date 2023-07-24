package sg.edu.np.mad.EfficenZ;

import android.content.Intent;
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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ToolsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment toolsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ToolsFragment newInstance(String param1, String param2) {
        ToolsFragment fragment = new ToolsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tools, container, false);
    }

    CardView task, notes, timer, music, calendar, chat, settings;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        calendar = getView().findViewById(R.id.calendarBtn);
        calendar.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CalendarTask.class);
            startActivity(intent);
        });

        chat = getView().findViewById(R.id.chatBtn);
        chat.setOnClickListener(v -> {
            // TODO: CHAT ACTIVITY
        });

        settings = getView().findViewById(R.id.settingsBtn);
        settings.setOnClickListener(v -> {
            // TODO: SETTINGS ACTIVITY
        });
    }
}