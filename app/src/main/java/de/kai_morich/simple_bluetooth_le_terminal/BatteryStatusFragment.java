package de.kai_morich.simple_bluetooth_le_terminal;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.IBinder;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BatteryStatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BatteryStatusFragment extends Fragment implements ServiceConnection, SerialListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private enum Connected { False, Pending, True }

    private String deviceAddress;
    private SerialService service;
    private Connected connected = Connected.False;
    private boolean initialStart = true;
    private String newline = TextUtil.newline_crlf;
    private boolean pendingNewline = false;
    private String receiveBuffer;

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
        setRetainInstance(true);
        deviceAddress = getArguments().getString("device");
    }

    @Override
    public void onDestroy() {
        if (connected != Connected.False)
            disconnect();
        getActivity().stopService(new Intent(getActivity(), SerialService.class));
        super.onDestroy();
        stopRepeatingTask();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(service != null)
            service.attach(this);
        else
            getActivity().startService(new Intent(getActivity(), SerialService.class)); // prevents service destroy on unbind from recreated activity caused by orientation change
    }

    @Override
    public void onStop() {
        if(service != null && !getActivity().isChangingConfigurations())
            service.detach();
        super.onStop();
    }

    @SuppressWarnings("deprecation") // onAttach(context) was added with API 23. onAttach(activity) works for all API versions
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        getActivity().bindService(new Intent(getActivity(), SerialService.class), this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDetach() {
        try { getActivity().unbindService(this); } catch(Exception ignored) {}
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(initialStart && service != null) {
            initialStart = false;
            getActivity().runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((SerialService.SerialBinder) binder).getService();
        service.attach(this);
        if(initialStart && isResumed()) {
            initialStart = false;
            getActivity().runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
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

    /*
     * Serial + UI
     */
    private void connect() {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            status("connecting...");
            connected = Connected.Pending;
            SerialSocket socket = new SerialSocket(getActivity().getApplicationContext(), device);
            service.connect(socket);
        } catch (Exception e) {
            onSerialConnectError(e);
        }
    }

    private void disconnect() {
        connected = Connected.False;
        service.disconnect();
    }

    private void send(String str) {
        if(connected != Connected.True) {
            Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            byte[] data;
            data = (str + newline).getBytes();

            service.write(data);
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }

    private void receive(byte[] data) {
        String msg = new String(data);

        receiveBuffer = receiveBuffer + msg;

        if(receiveBuffer.contains(newline)) {
            BufferedReader bufReader = new BufferedReader(new StringReader(receiveBuffer));
            List<String> lines = new ArrayList<>();
            String line=null;
            try {
                while ((line = bufReader.readLine()) != null) {
                    lines.add(line);
                }
            }
            catch(IOException e) {
            }

            if(receiveBuffer.lastIndexOf(newline) == receiveBuffer.length()-2) {
                // received a full line - no spillover to next package
                receiveBuffer = "";
                for(int i=0; i<lines.size(); i++) {
                    BatteryStatus status = BMS4SUtil.parseLine(lines.get(i));
                    update(status);
                }
            } else {
                // keep the chars of the last line for next time
                receiveBuffer = lines.get(lines.size() - 1);
                for(int i=0; i<lines.size()-1; i++) {
                    BatteryStatus status = BMS4SUtil.parseLine(lines.get(i));
                    update(status);
                }
            }
        }
    }

    private void status(String str) {
        SpannableStringBuilder spn = new SpannableStringBuilder(str + '\n');
        spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorStatusText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //receiveText.append(spn);
    }

    /*
     * SerialListener
     */
    @Override
    public void onSerialConnect() {
        status("connected");
        connected = Connected.True;
    }

    @Override
    public void onSerialConnectError(Exception e) {
        status("connection failed: " + e.getMessage());
        disconnect();
    }

    @Override
    public void onSerialRead(byte[] data) {
        receive(data);
    }

    @Override
    public void onSerialIoError(Exception e) {
        status("connection lost: " + e.getMessage());
        disconnect();
    }

}