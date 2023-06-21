package sg.edu.np.mad.EfficenZ;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class newTimeManagementFrag extends Fragment {

    private TextView task_title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        if (bundle != null) {
            String task = bundle.getString("KEY_TEXT");

            // Update the fragment UI with the received text
            task_title = task_title.findViewById(R.id.task);
            task_title.setText(task);
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_time_management, container, false);
    }
}