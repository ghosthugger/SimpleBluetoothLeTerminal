package de.kai_morich.simple_bluetooth_le_terminal;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

    private String deviceAddress;

    private ImageView arrow;
    private TextView stateOfCharge;
    private TextView cell1Voltage;
    private TextView cell2Voltage;
    private TextView cell3Voltage;
    private TextView cell4Voltage;

    private TextView currentFlowArrow;

    private int mInterval = 5000; // 5 seconds by default, can be changed later
    private Handler mHandler;
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                updateRandom();
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

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

        mHandler = new Handler();
        setHasOptionsMenu(true);

        deviceAddress = getArguments().getString("device");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_battery_status, container, false);
//        arrow = view.findViewById(R.id.currentFlowArrow);

        stateOfCharge = (TextView)view.findViewById(R.id.stateOfCharge);
        cell1Voltage = (TextView)view.findViewById(R.id.cell1);
        cell2Voltage = (TextView)view.findViewById(R.id.cell2);
        cell3Voltage = (TextView)view.findViewById(R.id.cell3);
        cell4Voltage = (TextView)view.findViewById(R.id.cell4);
        currentFlowArrow = (TextView)view.findViewById(R.id.currentFlowArrow);

        startRepeatingTask();

        // Inflate the layout for this fragment
        return view;
    }

    public void updateRandom() {
        update(BatteryStatus.getRandomState());
    }

    public void update(BatteryStatus status) {

/*        Group group = arrow.findViewById(R.id.arrow_rotation);

        float angle = 0;
        if(status.current>0){
            angle = (float) Math.PI;
        }

//        group.setRotation(angle);

*/

        String stateOfChargeText=String.format("%3.0f%%",status.chargeState);
        stateOfCharge.setText(stateOfChargeText);

        String cell1VoltageText=String.format("%1.2fv",status.cell1Voltage);
        cell1Voltage.setText(cell1VoltageText);
        String cell2VoltageText=String.format("%1.2fv",status.cell2Voltage);
        cell2Voltage.setText(cell2VoltageText);
        String cell3VoltageText=String.format("%1.2fv",status.cell3Voltage);
        cell3Voltage.setText(cell3VoltageText);
        String cell4VoltageText=String.format("%1.2fv",status.cell4Voltage);
        cell4Voltage.setText(cell4VoltageText);

        String currentFlowArrowText=String.format("%1.2fA",status.currentFlow);
        currentFlowArrow.setText(currentFlowArrowText);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_battery_status, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.open_terminal) {
            startTerminal();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void startTerminal() {
        Bundle args = new Bundle();
        args.putString("device", deviceAddress);
        Fragment newFragment = new TerminalFragment();
        newFragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.fragment, newFragment, "Terminal").addToBackStack(null).commit();
    }
}