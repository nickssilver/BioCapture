package com.example.biocapture;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.morpho.morphosmart.sdk.CallbackMessage;
import com.morpho.morphosmart.sdk.MorphoImage;

import java.nio.ByteBuffer;
import java.util.Observable;
import java.util.Observer;

public class CbmProcessObserver implements Observer {

    private int	currentCaptureBitmapId;
    private Handler mHandler = new Handler();
    String strMessage = new String();
    private View rootView;

    public CbmProcessObserver(View rootView) {
        this.rootView = rootView;
        currentCaptureBitmapId = R.id.iv_fpImg;
    }

    @Override
    public void update(Observable observable, Object data) {

        try
        {
            // convert the object to a callback back message.
            CallbackMessage message = (CallbackMessage) data;
            int type = message.getMessageType();

            switch (type)
            {

                case 1:
                    // message is a command.
                    Integer command = (Integer) message.getMessage();

                    // Analyze the command.
                    switch (command)
                    {
                        case 0:
                            strMessage = "No finger detected";
                            break;
                        case 1:
                            strMessage = "Move finger up";
                            break;
                        case 2:
                            strMessage = "Move finger down";
                            break;
                        case 3:
                            strMessage = "Move finger left";
                            break;
                        case 4:
                            strMessage = "Move finger right";
                            break;
                        case 5:
                            strMessage = "Press harder";
                            break;
                        case 6:
                            strMessage = "Remove finger";
                            break;
                        case 7:
                            strMessage = "Remove finger";
                            break;
                        case 8:
                            strMessage = "Finger detected";
                            break;
                    }

                    mHandler.post(new Runnable()
                    {
                        @Override
                        public synchronized void run()
                        {
                            updateSensorMessage(strMessage);
                        }
                    });

                    break;

                case 2:
                    // message is a low resolution image, display it.
                    byte[] image = (byte[]) message.getMessage();

                    MorphoImage morphoImage = MorphoImage.getMorphoImageFromLive(image);
                    int imageRowNumber = morphoImage.getMorphoImageHeader().getNbRow();
                    int imageColumnNumber = morphoImage.getMorphoImageHeader().getNbColumn();
                    final Bitmap imageBmp = Bitmap.createBitmap(imageColumnNumber, imageRowNumber, Bitmap.Config.ALPHA_8);

                    imageBmp.copyPixelsFromBuffer(ByteBuffer.wrap(morphoImage.getImage(), 0, morphoImage.getImage().length));
                    mHandler.post(new Runnable()
                    {
                        @Override
                        public synchronized void run()
                        {
                            updateImage(imageBmp, currentCaptureBitmapId);
                        }
                    });
                    break;
                case 3:
                    // message is the coded image quality.
                    final Integer quality = (Integer) message.getMessage();
//                    Log.d("ProcessObserver", "update : quality = " + quality);
                    mHandler.post(new Runnable()
                    {
                        @Override
                        public synchronized void run()
                        {
                            updateSensorProgressBar(quality);
                        }
                    });
                    break;
            }

        }
        catch (Exception e)
        {
            Log.e("ProcessObserver", "update : " + e.getMessage());
        }
    }

    private void updateSensorMessage(String sensorMessage)
    {
        try
        {
//            Log.d("ProcessObserver", "updateSensorMessage : sensorMessage = " + sensorMessage);
            TextView tv = (TextView) rootView.findViewById(R.id.tv_status);
            tv.setText(sensorMessage);
        }
        catch (Exception e)
        {
            Log.e("ProcessObserver", "updateSensorMessage : " + e.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    private void updateSensorProgressBar(int level)
    {
        try
        {
            ProgressBar progressBar = rootView.findViewById(R.id.progressbar);

            final float[] roundedCorners = new float[] { 5, 5, 5, 5, 5, 5, 5, 5 };
            ShapeDrawable pgDrawable = new ShapeDrawable(new RoundRectShape(roundedCorners, null, null));

            int color = Color.GREEN;

            if (level <= 25)
            {
                color = Color.RED;
            }
            else if (level <= 75)
            {
                color = Color.YELLOW;
            }
            pgDrawable.getPaint().setColor(color);
            ClipDrawable progress = new ClipDrawable(pgDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
            progressBar.setProgressDrawable(progress);
            progressBar.setBackgroundDrawable(rootView.getResources().getDrawable(android.R.drawable.progress_horizontal));
            progressBar.setProgress(level);
        }
        catch (Exception e)
        {
            Log.e("ProcessObserver", "updateSensorProgressBar : " + e.getMessage());
        }
    }

    private void updateImage(Bitmap bitmap, int id)
    {
        try
        {
            ImageView iv = rootView.findViewById(id);
            iv.setImageBitmap(bitmap);
        }
        catch (Exception e)
        {
            Log.e("ProcessObserver", "updateImage : " + e.getMessage());
        }
    }
}
