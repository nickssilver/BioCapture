package com.example.biocapture;

import static com.morpho.morphosmart.sdk.Coder.MORPHO_MSO_V9_CODER;
import static com.morpho.morphosmart.sdk.FalseAcceptanceRate.MORPHO_FAR_5;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.morpho.android.usb.USBManager;
import com.morpho.morphosmart.sdk.CallbackMask;
import com.morpho.morphosmart.sdk.Coder;
import com.morpho.morphosmart.sdk.CompressionAlgorithm;
import com.morpho.morphosmart.sdk.CustomInteger;
import com.morpho.morphosmart.sdk.DetectionMode;
import com.morpho.morphosmart.sdk.EnrollmentType;
import com.morpho.morphosmart.sdk.ErrorCodes;
import com.morpho.morphosmart.sdk.LatentDetection;
import com.morpho.morphosmart.sdk.MatchingStrategy;
import com.morpho.morphosmart.sdk.MorphoDevice;
import com.morpho.morphosmart.sdk.ResultMatching;
import com.morpho.morphosmart.sdk.Template;
import com.morpho.morphosmart.sdk.TemplateFVPType;
import com.morpho.morphosmart.sdk.TemplateList;
import com.morpho.morphosmart.sdk.TemplateType;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class FpSensorActivity extends BaseActivity {
    public static final String EXTRA_FINGERPRINTS = "extra_fingerprints";
    private void returnFingerprintData(byte[][] fingerprints) {
       Intent resultIntent = new Intent();
       resultIntent.putExtra(EXTRA_FINGERPRINTS, fingerprints);
        setResult(RESULT_OK, resultIntent);


        // Create a new Intent for the RegisterActivity
        // Intent registerIntent = new Intent(this, RegisterActivity.class);

        // Put the fingerprint data in the Intent as extras
        //registerIntent.putExtra(EXTRA_FINGERPRINTS, fingerprints);

        // Start the RegisterActivity with the Intent
        //startActivity(registerIntent);
   }
    private String TAG = "FpSensorActivity";
    private String title = "";
    private AppCompatTextView title_tv = null;

    private AppCompatButton capture_bt = null;
    private AppCompatButton verify_bt = null;
    private AppCompatTextView status_tv = null;
    private AppCompatTextView result_tv = null;

    private ArrayList<File> fileList = new ArrayList<File>();
    private List<String> list = new ArrayList<String>();
    private File templateFile;
    private Template template;

    private View rootView;
    private ProgressBar progressBar;
    private MorphoDevice morphoDevice;
    CbmProcessObserver processObserver;

    private boolean capturing = false;
    private boolean verifying = false;
    private int captureCount = 0;

    private boolean deviceIsSet = false;
    private Intent resultIntent;

    // Change this to a 2D array to store two fingerprints
    private static byte[][] capturedFingerprints = new byte[2][];
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private PeripheralsPowerInterface mPeripheralsInterface;
    private ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPeripheralsInterface = PeripheralsPowerInterface.Stub.asInterface(service);
            Log.d(TAG,"aidl connect succes");

            if (!getFingerprintSensorState()){
                android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(rootView.getContext()).create();
                alertDialog.setCancelable(false);
                alertDialog.setTitle(R.string.app_name);
                alertDialog.setMessage(getString(R.string.noAccessToDevice));
                alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Ok", (DialogInterface.OnClickListener) null);
                alertDialog.show();
            }
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
        verifyStoragePermissions(this);
        setContentView(R.layout.activity_fp_sensor);
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
        rootView = findViewById(android.R.id.content).getRootView();
        capture_bt = findViewById(R.id.btn_capture);
        verify_bt = findViewById(R.id.btn_verify);
        progressBar = findViewById(R.id.progressbar);
        status_tv = findViewById(R.id.tv_status);
        result_tv = findViewById(R.id.tv_result);

        progressBar.setVisibility(View.INVISIBLE);
        final float[] roundedCorners = new float[] { 5, 5, 5, 5, 5, 5, 5, 5 };
        ShapeDrawable pgDrawable = new ShapeDrawable(new RoundRectShape(roundedCorners, null, null));
        pgDrawable.getPaint().setColor(0);
        ClipDrawable progress = new ClipDrawable(pgDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
        progressBar.setProgressDrawable(progress);

        capturing = false;
        deviceIsSet = false;

        // Initialize the Observer object for the capture's callback
        processObserver = new CbmProcessObserver(rootView);

    }
    //public static byte[][] getFingerprints() {
        //return capturedFingerprints;}
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public void onClickCapture(View v){
        if (!capturing){
            capturing = true;
            rootView.setKeepScreenOn(true);

            progressBar.setVisibility(View.VISIBLE);
            result_tv.setText("");

            // Create a CountDownLatch with a count of 1
            final CountDownLatch latch = new CountDownLatch(1);
            // Capture the first fingerprint
            morphoDeviceCapture();

            // Schedule the second capture after a delay
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Ensure the first finger is removed before capturing the second one
                    morphoDeviceCapture();
                }

            }, 5000); // Delay of 5 seconds
            capture_bt.setText(R.string.stop);
            verify_bt.setVisibility(View.GONE);
        }
        else if (capturing && deviceIsSet) {
            rootView.setKeepScreenOn(false);

            morphoDevice = closeMorphoDevice(morphoDevice);

            capture_bt.setText(R.string.capture);
            verify_bt.setVisibility(View.VISIBLE);

            capturing = false;
            deviceIsSet = false;
        }
        else{
            showToastMessage("Device is being initialized, please try again", Toast.LENGTH_SHORT);
        }
    }

    public void onClickVerify(View v){
        if (!verifying) {
            result_tv.setText(" ");
            verifying = true;

            rootView.setKeepScreenOn(true);

            android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
            alertDialog.setCancelable(false);
            alertDialog.setTitle(R.string.app_name);
            alertDialog.setMessage("Please select a template to verify");
            alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // Here, this filters only iso-fmr template files
                    selectFile();
                }
            });
            alertDialog.show();

            verify_bt.setText(R.string.stop);
            capture_bt.setVisibility(View.GONE);
        }
        else {
            morphoDevice = closeMorphoDevice(morphoDevice);
            verify_bt.setText(R.string.verify);
            capture_bt.setVisibility(View.VISIBLE);

            verifying = false;
            deviceIsSet = false;
//            Toast.makeText(getApplicationContext(), "Please wait until the end of process",Toast.LENGTH_SHORT).show();
        }
    }

    // Initialize and open USB devise
    public MorphoDevice initMorphoDevice(Context context) {
        int ret = 0;
        Log.d(TAG, "initMorphoDevice");

        // On Morphotablet, 3rd parameter (enableWakeLock) must always be true
        USBManager.getInstance().initialize(context, "com.morpho.morphosample.USB_ACTION", true);
        MorphoDevice md = new MorphoDevice();
        CustomInteger nbUsbDevice = new CustomInteger();
        ret = md.initUsbDevicesNameEnum(nbUsbDevice);
        if (ret == ErrorCodes.MORPHO_OK) {
            if (nbUsbDevice.getValueOf() != 1) {

                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(rootView.getContext()).create();
                        alertDialog.setCancelable(false);
                        alertDialog.setTitle(R.string.app_name);
                        alertDialog.setMessage(getString(R.string.noAccessToDevice));
                        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        });
                        alertDialog.show();
                    }
                });

            } else {
                String sensorName = md.getUsbDeviceName(0); // We use first CBM found
                ret = md.openUsbDevice(sensorName, 0);
                if (ret != ErrorCodes.MORPHO_OK){
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToastMessage("Error opening USB device", Toast.LENGTH_SHORT);
                        }
                    });

                    finish();
                }
            }
        }
        else{
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToastMessage("Error initializing USB device", Toast.LENGTH_SHORT);
                }
            });
            finish();
        }

        return md;
    }

    // Close the USB device
    public MorphoDevice closeMorphoDevice(MorphoDevice md){
        if(md != null) {
            Log.d(TAG, "closeMorphoDevice");
            try {
                md.cancelLiveAcquisition();
                md.closeDevice();
                md = null;

            } catch (Exception e) {
                Log.e(TAG, "closeMorphoDevice : " + e.getMessage());
            }
        }
        return md;
    }

    /**************************** CAPTURE *********************************/
    public void morphoDeviceCapture() {

        if (morphoDevice == null){
            morphoDevice = initMorphoDevice(this);
            deviceIsSet = true;
        }

        /********* CAPTURE THREAD *************/
        Thread commandThread = new Thread(new Runnable() {
            @Override
            public void run() {

                int ret = 0;
                int timeout = 30;
                final int acquisitionThreshold = 0;
                int advancedSecurityLevelsRequired = 0;
                int fingerNumber = 1;
                TemplateType templateType = TemplateType.MORPHO_PK_ISO_FMR;
                TemplateFVPType templateFVPType = TemplateFVPType.MORPHO_NO_PK_FVP;
                int maxSizeTemplate = 512;
                EnrollmentType enrollType = EnrollmentType.ONE_ACQUISITIONS;
                LatentDetection latentDetection = LatentDetection.LATENT_DETECT_ENABLE;
                Coder coderChoice = Coder.MORPHO_DEFAULT_CODER;
                int detectModeChoice = DetectionMode.MORPHO_ENROLL_DETECT_MODE.getValue()
                        | DetectionMode.MORPHO_FORCE_FINGER_ON_TOP_DETECT_MODE.getValue();//18;
                TemplateList templateList = new TemplateList();

                // Define the messages sent through the callback
                int callbackCmd = CallbackMask.MORPHO_CALLBACK_COMMAND_CMD.getValue()
                        | CallbackMask.MORPHO_CALLBACK_IMAGE_CMD.getValue()
                        | CallbackMask.MORPHO_CALLBACK_CODEQUALITY.getValue()
                        | CallbackMask.MORPHO_CALLBACK_DETECTQUALITY.getValue();

                /********* CAPTURE *************/
                ret = morphoDevice.capture(timeout, acquisitionThreshold, advancedSecurityLevelsRequired,
                        fingerNumber, templateType, templateFVPType, maxSizeTemplate, enrollType,
                        latentDetection, coderChoice, detectModeChoice,
                        CompressionAlgorithm.MORPHO_NO_COMPRESS, 0, templateList,
                        callbackCmd, processObserver);

                Log.d(TAG, "morphoDeviceCapture ret = " + ret);
                if(ret != ErrorCodes.MORPHO_OK) {
                    String err = "";
                    if ( ret == ErrorCodes.MORPHOERR_TIMEOUT ){
                        err = "Capture failed : timeout";
                    }
                    else if (ret == ErrorCodes.MORPHOERR_CMDE_ABORTED ){
                        err = "Capture aborted";
                    }
                    else if (ret == ErrorCodes.MORPHOERR_UNAVAILABLE) {
                        err = "Device is not available";
                    }
                    else{
                        err = "Error code is " + ret;
                    }

                    final String finalErr = err;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToastMessage(finalErr, Toast.LENGTH_SHORT);
                        }
                    });
                }
                else {
                    // Here fingerNumber = 1, so we will get only one template
                    int nbTemplate = templateList.getNbTemplate();
                    Log.d(TAG, "morphoDeviceCapture nbTemplate = " + nbTemplate);

                    String msg = "";

                    if (nbTemplate == 1) {
                        Template template1 = templateList.getTemplate(0);

                        // Store the captured fingerprints in the class variable
                        if (capturedFingerprints[0] == null) {
                            capturedFingerprints[0] = template1.getData();
                        } else {
                            capturedFingerprints[1] = template1.getData();
                        }
                        // Increment the capture count
                        captureCount++;

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                        String currentDateandTime = sdf.format(new Date());
                        String file_name = "TemplateFP_" + currentDateandTime + "_" + System.currentTimeMillis() + templateType.getExtension();
                        File file = new File(getExternalFilesDir(null), file_name);

                        msg += "Template successfully captured!\n\nThis template is saved as:\n\n" + file;

                        final String alertMessage = msg;
                        final Template t = templateList.getTemplate(0);

                        // Dialog window to save the template
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(rootView.getContext());
                                builder.setTitle(R.string.app_name);
                                builder.setCancelable(false);
                                builder.setMessage(alertMessage);
                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {

                                            FileOutputStream fos = new FileOutputStream(file);
                                            fos.write(t.getData());
                                            fos.close();

                                        } catch (FileNotFoundException e) {
                                            Log.e(TAG, "Cannot write in " + file + " " + e.getMessage());
                                        } catch (IOException e) {
                                            Log.e(TAG, "FileOutputStream : " + e.getMessage());
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                        // If both captures have finished, return the fingerprint data
                        if (captureCount >= 2) {
                            returnFingerprintData(capturedFingerprints);


                        }
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.INVISIBLE);
                        progressBar.setProgress(0);

                        status_tv.setText("");

                        capturing = false;
                        rootView.setKeepScreenOn(false);

                        capture_bt.setText(R.string.capture);
                        verify_bt.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        commandThread.start();

    }


        /**************************** VERIFY **********************************/
    public void morphoDeviceVerify(){

        if (morphoDevice == null){
            morphoDevice = initMorphoDevice(this);
            deviceIsSet = true;
        }

        /********* VERIFY THREAD *************/
        Thread commandThread = new Thread(new Runnable() {
            @Override
            public void run() {

                String match = "";
                int ret = 0;
                int timeOut = 30;
                int far =  MORPHO_FAR_5;
                Coder coder = MORPHO_MSO_V9_CODER;
                int detectModeChoice = DetectionMode.MORPHO_ENROLL_DETECT_MODE.getValue()
                        | DetectionMode.MORPHO_FORCE_FINGER_ON_TOP_DETECT_MODE.getValue();
                int matchingStrategy = MatchingStrategy.MORPHO_STANDARD_MATCHING_STRATEGY.getValue();
                final TemplateList templateList = new TemplateList();

                int callbackCmd = CallbackMask.MORPHO_CALLBACK_IMAGE_CMD.getValue()
                        | CallbackMask.MORPHO_CALLBACK_COMMAND_CMD.getValue();
                final ResultMatching resultMatching = new ResultMatching();

                // Read the template file provided by the user
                byte[] buffer = readFile();

                if (buffer != null) {
                    template = new Template();
                    template.setData(buffer);
                    template.setTemplateType(TemplateType.MORPHO_PK_ISO_FMR);
                    templateList.putTemplate(template);

                    /********* VERIFY *************/
                    ret = morphoDevice.verify(timeOut, far, coder, detectModeChoice, matchingStrategy,
                            templateList, callbackCmd, processObserver, resultMatching);

                    Log.d(TAG, "morphoDeviceVerify ret = " + ret);
                    if (ret != ErrorCodes.MORPHO_OK) {
                        String err = "";
                        if (ret == ErrorCodes.MORPHOERR_TIMEOUT) {
                            err = "Verify process failed : timeout";
                        } else if (ret == ErrorCodes.MORPHOERR_CMDE_ABORTED) {
                            err = "Verify process aborted";
                        } else if (ret == ErrorCodes.MORPHOERR_UNAVAILABLE) {
                            err = "Device is not available";
                        } else if (ret == ErrorCodes.MORPHOERR_INVALID_FINGER || ret == ErrorCodes.MORPHOERR_NO_HIT) {
                            err = "Authentication or Identification failed";
                            match = "Template doesn't match";
                        } else {
                            err = "Error code is " + ret;
                        }
                        final String finalErr = err;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToastMessage(finalErr, Toast.LENGTH_SHORT);
                            }
                        });

                    } else {
                        if (resultMatching != null) {
                            match = "Matching score: " + resultMatching.getMatchingScore();
                        }
                    }
                } else {
                    // Wrong file/template
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToastMessage("Incorrect template", Toast.LENGTH_SHORT);
                            Log.d(TAG, "morphoDeviceVerify : incorrect template");
                        }
                    });

                }

                final String finalMatch = match;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        verifying = false;
                        rootView.setKeepScreenOn(false);

                        status_tv.setText(" ");
                        result_tv.setText(finalMatch);

                        verify_bt.setText(R.string.verify);
                        capture_bt.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        commandThread.start();
    }

    public void selectFile(){
        // Get internal storage path (/sdcard)
        File root = getExternalFilesDir(null);

        list.clear(); // Contains Strings (= filenames)
        fileList.clear(); // Contains File structures

        getfile(root);

        if (list.size() > 0){
            final CharSequence[] cs = list.toArray(new CharSequence[list.size()]);

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(rootView.getContext());
            builder.setTitle("Select Template");
            builder.setCancelable(false);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int pos = ((android.app.AlertDialog)dialog).getListView().getCheckedItemPosition();

                    templateFile = fileList.get(pos);

                    morphoDeviceVerify();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    verifying = false;
                    rootView.setKeepScreenOn(false);

                    verify_bt.setText(R.string.verify);
                    capture_bt.setVisibility(View.VISIBLE);
                }
            });
            builder.setSingleChoiceItems(cs, 0, null);
            builder.show();
        }
        else{
            android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(rootView.getContext()).create();
            alertDialog.setCancelable(false);
            alertDialog.setTitle(R.string.app_name);
            alertDialog.setMessage("No templates found!\n\nPlease capture a fingerprint.");
            alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    verifying = false;
                    rootView.setKeepScreenOn(false);

                    verify_bt.setText(R.string.verify);
                    capture_bt.setVisibility(View.VISIBLE);
                }
            });
            alertDialog.show();
        }
    }

    public ArrayList<File> getfile(File dir) {
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {

                if (!listFile[i].isDirectory()) {
                    if (listFile[i].getName().endsWith(".iso-fmr")) {
                        fileList.add(listFile[i]);
                        list.add(listFile[i].getName());
                    }
                }
            }
        }
        return fileList;
    }

    public byte[] readFile() {
        DataInputStream dis;
        byte[] buffer = null;

        try {
            dis = new DataInputStream(new FileInputStream(templateFile));

            int length = dis.available();
            buffer = new byte[length];
            dis.readFully(buffer);
            dis.close();
        }
        catch (Exception e){
            Log.e(TAG, "ReadFile : " + e.getMessage());
        }

        return buffer;
    }


    @Override
    protected void onPause() {
        super.onPause();
        unbindService(serviceConn);

        morphoDevice = closeMorphoDevice(morphoDevice);
        deviceIsSet = false;
    }

    @Override
    protected void onResume() {
        if(!bindService(getAidlIntent(), serviceConn, Service.BIND_AUTO_CREATE)) {
            Log.e(TAG, "System couldn't find the service");
            Toast.makeText(getApplicationContext(), "System couldn't find peripherals service", Toast.LENGTH_SHORT).show();
        }
        super.onResume();
    }



    private Intent getAidlIntent() {
        Intent aidlIntent = new Intent();
        aidlIntent.setAction("example.intent.action.CONN_PERIPHERALS_SERVICE_AIDL");
        aidlIntent.setPackage("com.android.settings");
        return aidlIntent;
    }

    public boolean getFingerprintSensorState(){
        boolean ret = false;
        int usbRole = -1;

        try {
            if (mPeripheralsInterface != null) {
                ret = mPeripheralsInterface.getFingerPrintSwitch();
                if (!ret){
                    return false;
                }

                usbRole = mPeripheralsInterface.getUSBRole();
                if (usbRole == 2){ // DEVICE mode: PC connection only
                    return false;
                }else if (usbRole == 1){ // HOST mode: Peripherals only
                    return true;
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // Here, fingerprint sensor should be powered on, and USB role set to AUTO
        // Check if tablet is plugged to the computer
        if (!isDevicePluggedToPc()){
            return true;
        }

        return false;
    }

    private boolean isDevicePluggedToPc(){
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);

        // Are we charging / charged?
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

        if (status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL) {
            // How are we charging?
            int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            if (chargePlug == BatteryManager.BATTERY_PLUGGED_USB) {
                Log.d(TAG, "USB plugged");
                return true;
            }
            if (chargePlug == BatteryManager.BATTERY_PLUGGED_AC) {
                Log.d(TAG, "Powered by 3.5mm connector");
                return false;
            }
        }

        return false;
    }

    private void showToastMessage(String msg, int length){
        Toast toast = Toast.makeText(getApplication(), msg, length);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 180);
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
