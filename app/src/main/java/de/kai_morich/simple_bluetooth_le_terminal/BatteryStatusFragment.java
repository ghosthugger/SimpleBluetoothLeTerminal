package de.kai_morich.simple_bluetooth_le_terminal;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;

import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BatteryStatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BatteryStatusFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageView arrow;
    public BatteryStatusFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BatteryStatusFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BatteryStatusFragment newInstance(String param1, String param2) {
        BatteryStatusFragment fragment = new BatteryStatusFragment();
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
        View view = inflater.inflate(R.layout.fragment_battery_status, container, false);
        arrow = view.findViewById(R.id.imageViewArrow);

        BatteryStatus batteryStatus = new BatteryStatus();
        batteryStatus.current = (Math.random() - 0.5)*100;
        update(batteryStatus);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_battery_status, container, false);
    }

    public void update(BatteryStatus status) {

        Group group = arrow.findViewById(R.id.arrow_rotation);

        float angle = 0;
        if(status.current>0){
            angle = (float) Math.PI;
        }

//        group.setRotation(angle);
    }
}