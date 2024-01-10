package com.example.biocapture;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

public class UsbManagementActivity extends AppCompatActivity {
    private final String TAG = "UsbManagementActivity";
    private String title = "";
    private AppCompatTextView title_tv;

    private SwitchCompat fp_switch = null;
    private SwitchCompat hostUsb_switch = null;
    private SwitchCompat docking_switch = null;
    private SwitchCompat nfc_switch = null;

    private RadioGroup usbMode_rg = null;
    private AppCompatRadioButton autoMode_rb = null;
    private AppCompatRadioButton hostMode_rb = null;
    private AppCompatRadioButton deviceMode_rb = null;
    private TextView usbModeInfo_tv = null;

    private static final int AUTO = 0x30;
    private static final int HOST = 0x31;
    private static final int DEVICE = 0x32;

    private PeripheralsPowerInterface mPeripheralsInterface;

    private static boolean nfcAvailable;

    private ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPeripheralsInterface = PeripheralsPowerInterface.Stub.asInterface(service);
            Log.d(TAG,"aidl connect succes");
            initializePeripheralsSwitchUi();
            initializeUsbModeUi();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPeripheralsInterface = null;
            Log.d(TAG,"aidl disconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usb_management);
        Toolbar toolbar = findViewById(R.id.toolbar);
        title_tv = toolbar.findViewById(R.id.action_bar_title);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (getIntent() != null && getIntent().getExtras() != null) {
            title = getIntent().getStringExtra("TITLE");
        }
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            title_tv.setText(title);
        }

        // Initialize UI
        fp_switch = findViewById(R.id.switch_fp_sensor);
        hostUsb_switch = findViewById(R.id.switch_host_usb_port);
        docking_switch = findViewById(R.id.switch_docking_usb_port);
        if (!Build.MODEL.equals("MPH-MB003A")) { /* not ID-Screen */
            docking_switch.setVisibility(View.GONE);
        }
        nfc_switch = findViewById(R.id.switch_nfc);

        nfcAvailable = Utility.isNfcAvailable();
        if (!nfcAvailable){
            nfc_switch.setVisibility(View.GONE);
        }

        usbMode_rg = findViewById(R.id.rg_usbMode);
        autoMode_rb = findViewById(R.id.rb_auto);
        hostMode_rb = findViewById(R.id.rb_host);
        deviceMode_rb = findViewById(R.id.rb_device);
        usbModeInfo_tv = findViewById(R.id.tv_usbModeInfo);
    }

    public void onClickFingerprintSwitch(View v){
        boolean isChecked = fp_switch.isChecked();
        String state = isChecked ? "enabled" : "disabled";
        Log.d(TAG, "onClick fingerprint sensor " + state);

        if(mPeripheralsInterface != null) {
            try {
                if (!mPeripheralsInterface.setFingerPrintSwitch(isChecked)) {
                    Log.e(TAG, "Issue changing fingerprint sensor's state");
                    showToastMessage("There was an issue with the fingerprint sensor");
                } else {
                    showToastMessage("Fingerprint sensor " + state);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else{
            showToastMessage("There was an issue initializing the interface");
        }
    }

    public void onClickHostUsbSwitch(View v){
        boolean isChecked = hostUsb_switch.isChecked();
        String state = isChecked ? "enabled" : "disabled";
        Log.d(TAG, "onClick host usb port " + state);
        if(mPeripheralsInterface != null) {
            try {
                if (!mPeripheralsInterface.setHostUsbPortSwitch(isChecked)) {
                    Log.e(TAG, "Issue changing host USB port's state");
                    showToastMessage("There was an issue with the host USB port");
                } else {
                    showToastMessage("Host USB port " + state);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else{
            showToastMessage("There was an issue initializing the interface");
        }
    }

    public void onClickDockingUsbSwitch(View v){
        boolean isChecked = docking_switch.isChecked();
        String state = isChecked ? "enabled" : "disabled";
        Log.d(TAG, "onClick docking station usb port " + state);

        if(mPeripheralsInterface != null) {
            try {
                if (!mPeripheralsInterface.setDockingStationUsbPortSwitch(isChecked)) {
                    Log.e(TAG, "Issue changing docking station USB port's state");
                    showToastMessage("There was an issue with the docking station USB port");
                } else {
                    showToastMessage("Docking station USB port " + state);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else{
            showToastMessage("There was an issue initializing the interface");
        }
    }

    public void onClickNfcSwitch(View v){
        boolean isChecked = nfc_switch.isChecked();
        String state = isChecked ? "enabled" : "disabled";
        Log.d(TAG, "onClick nfc " + state);

        if(mPeripheralsInterface != null) {
            try {
                if (!mPeripheralsInterface.setNfcSwitch(isChecked)) {
                    Log.e(TAG, "Issue changing NFC reader's state");
                    showToastMessage("There was an issue with the NFC reader");
                } else {
                    showToastMessage("NFC reader " + state);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else{
            showToastMessage("There was an issue initializing the interface");
        }
    }

    public void onClickAutoMode(View v){
        Log.d(TAG, "onClick Auto mode");
        usbModeInfo_tv.setText(getResources().getString(R.string.usb_mode_0));

        if (mPeripheralsInterface != null) {
            try {
                mPeripheralsInterface.setUSBRole(AUTO);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getApplicationContext(), "There was an issue initializing the interface", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickHostMode(View v){
        Log.d(TAG, "onClick Host mode");
        usbModeInfo_tv.setText(getResources().getString(R.string.usb_mode_1));

        if (mPeripheralsInterface != null) {
            try {
                mPeripheralsInterface.setUSBRole(HOST);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getApplicationContext(), "There was an issue initializing the interface", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickDeviceMode(View v){
        Log.d(TAG, "onClick Device mode");
        usbModeInfo_tv.setText(getResources().getString(R.string.usb_mode_2));

        if (mPeripheralsInterface != null) {
            try {
                mPeripheralsInterface.setUSBRole(DEVICE);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getApplicationContext(), "There was an issue initializing the interface", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializePeripheralsSwitchUi(){
        if (mPeripheralsInterface != null){
            try {
                fp_switch.setChecked(mPeripheralsInterface.getFingerPrintSwitch());
                hostUsb_switch.setChecked(mPeripheralsInterface.getHostUsbPortSwitch());
                docking_switch.setChecked(mPeripheralsInterface.getDockingStationUsbPortSwitch());

                if (nfcAvailable){
                    nfc_switch.setChecked(mPeripheralsInterface.getNfcSwitch());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getApplicationContext(), "There was an issue initializing the interface", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeUsbModeUi(){
        if(mPeripheralsInterface != null){
            int mode = -1;
            try {
                mode = mPeripheralsInterface.getUSBRole();
                Log.d(TAG, "getUSBRole: " + mode);

                if (mode == 0){ // AUTO
                    usbMode_rg.check(R.id.rb_auto);
                    usbModeInfo_tv.setText(R.string.usb_mode_0);

                } else if (mode == 1){ // HOST
                    usbMode_rg.check(R.id.rb_host);
                    usbModeInfo_tv.setText(R.string.usb_mode_1);

                } else if (mode == 2){ // DEVICE
                    usbMode_rg.check(R.id.rb_device);
                    usbModeInfo_tv.setText(R.string.usb_mode_2);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getApplicationContext(), "There was an issue initializing the interface", Toast.LENGTH_SHORT).show();
        }
    }

    private Intent getAidlIntent() {
        Intent aidlIntent = new Intent();
        aidlIntent.setAction("idemia.intent.action.CONN_PERIPHERALS_SERVICE_AIDL");
        aidlIntent.setPackage("com.android.settings");
        return aidlIntent;
    }

    @Override
    protected void onResume() {
        if(!bindService(getAidlIntent(), serviceConn, Service.BIND_AUTO_CREATE)) {
            Log.e(TAG, "System couldn't find the service");
            Toast.makeText(getApplicationContext(), "System couldn't find peripherals service", Toast.LENGTH_SHORT).show();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(serviceConn);
    }

    private void showToastMessage(String msg){
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, -120);
        toast.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
