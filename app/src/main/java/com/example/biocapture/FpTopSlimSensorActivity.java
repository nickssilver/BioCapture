package com.example.biocapture;

import static com.example.morpholivescan.eCaptureMode.CMODE_AUTO_CAP;
import static com.example.morpholivescan.eCaptureMode.CMODE_FAST_ENROLL;
import static com.example.morpholivescan.eCaptureMode.CMODE_MEDIUM_ENROLL;
import static com.example.morpholivescan.eCaptureMode.CMODE_SLOW_ENROLL;
import static com.example.morpholivescan.eCaptureType.CTYPE_ROLL_LINDEX;
import static com.example.morpholivescan.eCaptureType.CTYPE_ROLL_LLITTLE;
import static com.example.morpholivescan.eCaptureType.CTYPE_ROLL_LMIDDLE;
import static com.example.morpholivescan.eCaptureType.CTYPE_ROLL_LRING;
import static com.example.morpholivescan.eCaptureType.CTYPE_ROLL_LTHUMB;
import static com.example.morpholivescan.eCaptureType.CTYPE_ROLL_RINDEX;
import static com.example.morpholivescan.eCaptureType.CTYPE_ROLL_RLITTLE;
import static com.example.morpholivescan.eCaptureType.CTYPE_ROLL_RMIDDLE;
import static com.example.morpholivescan.eCaptureType.CTYPE_ROLL_RRING;
import static com.example.morpholivescan.eCaptureType.CTYPE_ROLL_RTHUMB;
import static com.example.morpholivescan.eCaptureType.CTYPE_SLAP_LFOUR;
import static com.example.morpholivescan.eCaptureType.CTYPE_SLAP_LTHUMB;
import static com.example.morpholivescan.eCaptureType.CTYPE_SLAP_RFOUR;
import static com.example.morpholivescan.eCaptureType.CTYPE_SLAP_RTHUMB;
import static com.example.morpholivescan.eCaptureType.CTYPE_SLAP_THUMBS;
import static com.example.morpholivescan.eMlsError.MLS_NOERROR;
import static com.example.morpholivescan.eMlsError.MLS_PERMISSION_DENIED;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.example.morpholivescan.CallbackMessage;
import com.example.morpholivescan.CallbackMessage.eMsgType;
import com.example.morpholivescan.MorphoLiveScan;
import com.example.morpholivescan.eCaptureState;
import com.example.morpholivescan.eCaptureType;
import com.example.morpholivescan.eMlsError;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

public class FpTopSlimSensorActivity extends BaseActivity implements Observer {
    private String TAG = "FpTopSlimSensorActivity";
    private String title = "";
    private static final String[] aSpeed = {"FAST", "MEDIUM", "SLOW"};
    private static final String[] cType = {"Slap - Left Four", "Slap - Right Four", "Slap - Left Thumb", "Slap - Right Thumb", "Slap - Thumbs", "Roll - Left Thumb", "Roll - Left Index", "Roll - Left Middle", "Roll - Left Ring", "Roll - Left Little", "Roll - Right Thumb", "Roll - Right Index", "Roll - Right Middle", "Roll - Right Ring", "Roll - Right Little"};
    private AppCompatTextView title_tv = null;

    private AppCompatButton capture_bt = null;
    private AppCompatButton stopCapture_bt = null;
    private AppCompatTextView status_tv = null;

    AppCompatSpinner mSpeed;
    AppCompatSpinner mCapType;
    SwitchCompat mFingerBox;
    SwitchCompat mManualCapture;

    private boolean capturing = false;
    private MorphoLiveScan morphoLiveScan = null;
    private View rootView;
    private ImageView fpImg;

    private boolean  g_finger_box = false;
    private boolean  g_manual_capture = false;

    /* Right four-finger slap Capture */
    eCaptureType capType = eCaptureType.CTYPE_SLAP_RFOUR;

    /** many Capture Mode options can be concatenated */
    int capMode = (CMODE_AUTO_CAP.getValue() | CMODE_MEDIUM_ENROLL.getValue());

    private void enableCapture() {
        capture_bt.setEnabled(true);
        status_tv.setText("Ready");
    }

    private void disableCapture(int error) {
        capture_bt.setEnabled(false);

        if (error == 0) {
            status_tv.setText("Sensor disabled, enable sensor or unplug PC");
        }
        else {
            status_tv.setText("Error " + error);
        }
    }

    private void enableButtons() {
        stopCapture_bt.setEnabled(false);
        stopCapture_bt.setVisibility(View.GONE);
        mManualCapture.setEnabled(true);
        mFingerBox.setEnabled(true);
        mCapType.setEnabled(true);
        mSpeed.setEnabled(true);
    }

    private void disableButtons() {
        // Only visible in manual mode
        if (g_manual_capture) {
            stopCapture_bt.setEnabled(true);
            stopCapture_bt.setVisibility(View.VISIBLE);
        }
        mManualCapture.setEnabled(false);
        mFingerBox.setEnabled(false);
        mCapType.setEnabled(false);
        mSpeed.setEnabled(false);
    }

    @Override
    public void update(Observable observable, Object data) {
        try
        {
            final CallbackMessage message = (CallbackMessage) data;
            final eMsgType type = message.getMessageType();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (type) {
                        case MSG_INVALID_CAPTURE_STATE: {
                            Integer msg = (Integer) message.getMessage();
                            Log.w(TAG, "Invalid Capture State " + msg.intValue() + " received !");
                        }
                        case MSG_DEVICE_INIT_FAILED: {
                            eMlsError msg = (eMlsError) message.getMessage();
                            disableCapture(msg.getValue());
                            Log.e(TAG, "Initializing fingerprint sensor failed (error = " + msg + ") !");
                            break;
                        }
                        case MSG_DEVICE_TERM_FAILED: {
                            eMlsError msg = (eMlsError) message.getMessage();
                            disableCapture(msg.getValue());
                            Log.e(TAG, "Terminating fingerprint sensor failed (error = " + msg + ") !");
                            break;
                        }
                        case MSG_DEVICE_OPEN_FAILED: {
                            eMlsError msg = (eMlsError) message.getMessage();
                            disableCapture(msg.getValue());
                            Log.e(TAG, "opening Fingerprint sensor failed (error = " + msg + ") !");
                            break;
                        }
                        case MSG_DEVICE_CLOSE_FAILED: {
                            eMlsError msg = (eMlsError) message.getMessage();
                            Log.w(TAG, "closing Fingerprint sensor failed (error = " + msg + ") !");
                            break;
                        }
                        case MSG_DEVICE_CONNECTED: {
                            Object devInfo = message.getMessage();
                            if (devInfo != null) {
                                String msg = (String) message.getMessage();
                                Log.i(TAG, "Fingerprint sensor " + msg + " Connected");
                            }
                            else {
                                Log.i(TAG, "Fingerprint sensor Connected");
                            }
                            enableCapture();
                            break;
                        }
                        case MSG_DEVICE_DISCONNECTED:
                            disableCapture(0);
                            Log.w(TAG, "Fingerprint sensor is Disconnected !");
                            break;
                        case MSG_DEVICE_RECONNECTING:
                            status_tv.setText(getString(R.string.sensorInitiliazing));
                            Log.v(TAG, "Fingerprint sensor is Reconnecting ...");
                            break;
                        case MSG_DEVICE_NOT_FOUND:
                            disableCapture(0);
                            Log.w(TAG, "Fingerprint sensor not found !");
                            break;
                        case MSG_DEVICE_PERMISSION_DENIED:
                            disableCapture(MLS_PERMISSION_DENIED.getValue());
                            Log.w(TAG, "Fingerprint sensor usb permission denied !");
                            break;
                        case MSG_RESULT_UPDATE: {
                            eCaptureState msg = (eCaptureState) message.getMessage();

                            switch (msg) {
                                case CSTATE_IDLE:
                                case CSTATE_STARTED:
                                case CSTATE_PREVIEW:
                                    break;
                                case CSTATE_ABORTED:
                                    capturing = false;
                                    status_tv.append("\n\nCapture Aborted");
                                    capture_bt.setText(R.string.capture);
                                    enableButtons();
                                    break;
                                case CSTATE_FAILED:
                                    capturing = false;
                                    status_tv.setText("Capture Failed");
                                    capture_bt.setText(R.string.capture);
                                    enableButtons();
                                    break;
                                case CSTATE_REJECTED:
                                    capturing = false;
                                    status_tv.setText("Capture Rejected");
                                    capture_bt.setText(R.string.capture);
                                    enableButtons();
                                    break;
                                case CSTATE_CANCELED:
                                    capturing = false;
                                    status_tv.setText("Capture Canceled");
                                    capture_bt.setText(R.string.capture);
                                    enableButtons();
                                    break;
                                case CSTATE_CAPTURED: {
                                    capturing = false;
                                    status_tv.setText("Ready");
                                    enableButtons();
                                    break;
                                }
                                case CSTATE_FINISHED: {
                                    capturing = false;
                                    status_tv.setText("Ready");
                                    capture_bt.setText(R.string.capture);
                                    enableButtons();
                                    SaveCapture();
                                    break;
                                }
                                default:
                                    Log.w(TAG, "Unhandled Capture State: " + msg + " received !");
                                    break;
                            }

                            break;
                        }
                        case MSG_UPDATE_IMAGE: {
                            Bitmap msg = (Bitmap) message.getMessage();
                            try {
                                fpImg.setImageBitmap(msg);
                            }
                            catch (Exception e) {
                                Log.e(TAG, "updateImage: " + e.getMessage());
                            }
                            break;
                        }
                        default:
                            Log.d(TAG, "Unhandled message type '" + type + "' received !");
                            break;
                    }
                }
            });
        }
        catch (Exception e) {
            Log.e(TAG, "update : " + e.getMessage());
        }
    }

    public void onFingerBoxClicked(View view) {
        boolean checked = ((SwitchCompat) view).isChecked();

        if (view.getId() == R.id.FingerBox) {
            if (checked) {
                g_finger_box = true;
            }
            else {
                g_finger_box = false;
            }
        }
    }

    public void onManualCaptureClicked(View view)
    {
        boolean checked = ((SwitchCompat)view).isChecked();

        if (view.getId() == R.id.ManualCapture) {
            g_manual_capture = checked;
        } else {
            Log.w(TAG, "Bad switch checked !");
            return;
        }

        capMode = g_manual_capture ? 0 : CMODE_AUTO_CAP.getValue();

        switch (mSpeed.getSelectedItemPosition()) {
            case 0:
                capMode |= CMODE_FAST_ENROLL.getValue();
                break;
            case 1:
                capMode |= CMODE_MEDIUM_ENROLL.getValue();
                break;
            case 2:
                capMode |= CMODE_SLOW_ENROLL.getValue();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fp_topslim_sensor);
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
        capture_bt.setEnabled(false);
        stopCapture_bt = findViewById(R.id.btn_manual_capture);
        stopCapture_bt.setEnabled(false);
        stopCapture_bt.setVisibility(View.GONE);
        status_tv = findViewById(R.id.tv_status);
        status_tv.setText(getString(R.string.sensorInitiliazing));
        fpImg = (ImageView) findViewById(R.id.iv_fpImg);

        mSpeed =  (AppCompatSpinner)findViewById(R.id.AcquisitionSpeed);
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(this.getApplicationContext(), R.layout.colored_spinner_layout, aSpeed);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        mSpeed.setAdapter(adapter);
        mSpeed.setSelection(0);

        mSpeed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (g_manual_capture)
                    capMode = 0;
                else
                    capMode = CMODE_AUTO_CAP.getValue();

                switch (position) {
                    case 0:
                        capMode |= CMODE_FAST_ENROLL.getValue();
                        break;
                    case 1:
                        capMode |= CMODE_MEDIUM_ENROLL.getValue();
                        break;
                    case 2:
                        capMode |= CMODE_SLOW_ENROLL.getValue();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
         });

        mCapType = (AppCompatSpinner)findViewById(R.id.CaptureType);
        ArrayAdapter<String>captureType = new ArrayAdapter<String>(this.getApplicationContext(), R.layout.colored_spinner_layout, cType);
        captureType.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        mCapType.setAdapter(captureType);
        mCapType.setSelection(1);

        mCapType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        capType = CTYPE_SLAP_LFOUR;
                        break;
                    case 1:
                        capType = CTYPE_SLAP_RFOUR;
                        break;
                    case 2 :
                        capType = CTYPE_SLAP_LTHUMB;
                        break;
                    case 3:
                        capType = CTYPE_SLAP_RTHUMB;
                        break;
                    case 4:
                        capType = CTYPE_SLAP_THUMBS;
                        break;
                    case 5:
                        capType = CTYPE_ROLL_LTHUMB;
                        break;
                    case 6:
                        capType = CTYPE_ROLL_LINDEX;
                        break;
                    case 7:
                        capType = CTYPE_ROLL_LMIDDLE;
                        break;
                    case 8:
                        capType = CTYPE_ROLL_LRING;
                        break;
                    case 9:
                        capType = CTYPE_ROLL_LLITTLE;
                        break;
                    case 10:
                        capType = CTYPE_ROLL_RTHUMB;
                        break;
                    case 11:
                        capType = CTYPE_ROLL_RINDEX;
                        break;
                    case 12:
                        capType = CTYPE_ROLL_RMIDDLE;
                        break;
                    case 13:
                        capType = CTYPE_ROLL_RRING;
                        break;
                    case 14:
                        capType = CTYPE_ROLL_RLITTLE;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mFingerBox = (SwitchCompat)findViewById(R.id.FingerBox);
        mManualCapture = (SwitchCompat)findViewById(R.id.ManualCapture);

        capturing = false;
        morphoLiveScan = new MorphoLiveScan(this.getApplicationContext());
        morphoLiveScan.addObserver(this);
        morphoLiveScan.start();
    }

    @Override
    protected void onDestroy() {
        if (morphoLiveScan != null) {
            morphoLiveScan.destroy();
        }

        super.onDestroy();
    }

    public void onClickCapture(View v) {
        if (v.getId() == R.id.btn_capture) {
            if (capturing) {
                eMlsError error = morphoLiveScan.stopCapture();

                if (error == MLS_NOERROR) {
                    status_tv.setText("");
                    capturing = false;
                } else {
                    Toast.makeText(this, "Error " + error.getValue(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Stopping Capture failed (error = " + error + ") !");
                }
            } else {
                eMlsError error;

                status_tv.setText("Capturing ...");
                capture_bt.setText(R.string.stop);
                disableButtons();

                error = morphoLiveScan.startCapture(capType, capMode, g_finger_box);

                if (error == MLS_NOERROR) {
                    capturing = true;
                } else {
                    capturing = false;
                    Toast.makeText(this, "Error " + error.getValue(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Capturing Fingerprint failed (error = " + error + ") !");
                }
            }
        }
        else if (v.getId() == R.id.btn_manual_capture) {
            if (capturing) {
                morphoLiveScan.acceptCapture();
                enableButtons();
            }
        }
    }

    private void SaveCapture()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateAndTime = sdf.format(new Date());
        String file_name = "Capture_" + currentDateAndTime + ".png";
        File file = new File(getExternalFilesDir(null), file_name);
        final String alertMessage = "Fingerprint successfully captured!\n\nSave as:\n\n" + file + " ?";

        Log.i(TAG, "Saving Captured image as: " + file_name + " ?");

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(rootView.getContext());
        builder.setTitle(R.string.app_name);
        builder.setCancelable(false);
        builder.setMessage(alertMessage);
        builder.setNegativeButton("No", null);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                morphoLiveScan.saveCapture(file);
            }
        });
        builder.show();
    }

    @Override
    protected void onPause() {
        Log.v(TAG, "================= onPause =================");
        super.onPause();

        if (capturing) {
            eMlsError error = morphoLiveScan.stopCapture();

            if (error == MLS_NOERROR) {
                capturing = false;
            }
            else {
                status_tv.setText("Error " + error.getValue());
                Log.e(TAG, "Stopping Capture failed (error = " + error + ") !");
            }
        }
    }

    @Override
    protected void onResume() {
        Log.v(TAG, "================= onResume =================");
        if (morphoLiveScan.waitInit()) {
            status_tv.setText(getString(R.string.sensorInitiliazing));
        }

        super.onResume();
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
