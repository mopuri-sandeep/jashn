package com.jashn.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.jashn.app.wave.WaveformView;
import com.jashn.app.wave.soundfile.SoundFile;
import com.ringdroid.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

public class MainActivity extends Activity {

    private long mLoadingLastUpdateTime;
    private boolean mLoadingKeepGoing;
    private long mRecordingLastUpdateTime;
    private boolean mRecordingKeepGoing;
    private double mRecordingTime;
    private boolean mFinishActivity;
    private TextView mTimerTextView;
    private AlertDialog mAlertDialog;
    private ProgressDialog mProgressDialog;
    private SoundFile mSoundFile;
    private File mFile;
    private String mFilename;
    private String mArtist;
    private String mTitle;
    private int mNewFileKind;
    private boolean mWasGetContentIntent;
    private TextView mStartText;
    private TextView mEndText;
    private TextView mInfo;
    private String mInfoContent;
    private ImageButton mPlayButton;
    private ImageButton mRewindButton;
    private ImageButton mFfwdButton;
    private boolean mKeyDown;
    private String mCaption = "";
    private int mWidth;
    private int mMaxPos;
    private int mStartPos;
    private WaveformView mWaveformView;
    private int mEndPos;
    private boolean mStartVisible;
    private boolean mEndVisible;
    private int mLastDisplayedStartPos;
    private int mLastDisplayedEndPos;
    private int mOffsetGoal;
    private int mFlingVelocity;
    private int mPlayStartMsec;
    private int mPlayEndMsec;
    private Handler mHandler;
    private boolean mIsPlaying;
    private boolean mTouchDragging;
    private float mTouchStart;
    private int mTouchInitialOffset;
    private int mTouchInitialStartPos;
    private int mTouchInitialEndPos;
    private long mWaveformTouchStartMsec;
    private float mDensity;
    private int mMarkerLeftInset;
    private int mMarkerRightInset;
    private int mMarkerTopOffset;
    private int mMarkerBottomOffset;

    private int[] mLenByZoomLevel;
    private double[][] mValuesByZoomLevel;
    private double[] mZoomFactorByZoomLevel;
    private int[] mHeightsAtThisZoomLevel;
    private int mZoomLevel;
    private int mNumZoomLevels;
    private int mSampleRate;
    private int mSamplesPerFrame;
    private int mOffset;

    private Thread mLoadSoundFileThread;
    private Thread mRecordAudioThread;
    private Thread mSaveSoundFileThread;
    private Boolean master = false;
    Button button;
    String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RingdroidSelectActivity.class);
                startActivity(i);
            }
        });

        Intent intent = getIntent();

        if(intent.hasExtra("was_get_content_intent")) {

            filePath = intent.getStringExtra("filename");

            master = true;

            mWasGetContentIntent = intent.getBooleanExtra("was_get_content_intent", false);

            mFilename = intent.getData().toString().replaceFirst("file://", "").replaceAll("%20", " ");
            mSoundFile = null;
            mHandler = new Handler();

            loadFromFile();
        }

        mSocket.on("data", onNewMessage);
        mSocket.connect();
//        mSocket.emit("new message", "hahaahah testign");

//        mWaveformView = (WaveformView)findViewById(R.id.waveform);
//        mWaveformView.setListener(this);
//
//        mInfo = (TextView)findViewById(R.id.info);
//        mInfo.setText(mCaption);
//
//        mMaxPos = 0;
//        mLastDisplayedStartPos = -1;
//        mLastDisplayedEndPos = -1;
//
//        if (mSoundFile != null && !mWaveformView.hasSoundFile()) {
//            mWaveformView.setSoundFile(mSoundFile);
//            mWaveformView.recomputeHeights(mDensity);
//            mMaxPos = mWaveformView.maxPos();
//        }
    }

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://10.1.2.25:3000/");
        } catch (URISyntaxException e) {}
    }

    private void attemptSend(String event,String message) {
        mSocket.emit(event, message);
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
//                    String username;
//                    String message;
//                    try {
//                        username = data.getString("username");
//                        message = data.getString("message");
//                    } catch (JSONException e) {
//                        return;
//                    }

//                    if(master){
//                        button.setText("Play in sync");
//                        button.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                attemptSend("play", "play");
//                            }
//                        });
//                    }
                    // add the message to view
                    System.out.println("the data back is: " + data.toString());
                }
            });
        }
    };

    private void loadFromFile() {
        mFile = new File(mFilename);

        mLoadingLastUpdateTime = getCurrentTime();
        mLoadingKeepGoing = true;
        mFinishActivity = false;
        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setTitle(R.string.progress_dialog_loading);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(
                new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        mLoadingKeepGoing = false;
                        mFinishActivity = true;
                    }
                });
        mProgressDialog.show();

        final SoundFile.ProgressListener listener =
                new SoundFile.ProgressListener() {
                    public boolean reportProgress(double fractionComplete) {
                        long now = getCurrentTime();
                        if (now - mLoadingLastUpdateTime > 100) {
                            mProgressDialog.setProgress(
                                    (int) (mProgressDialog.getMax() * fractionComplete));
                            mLoadingLastUpdateTime = now;
                        }
                        return mLoadingKeepGoing;
                    }
                };

        // Load the sound file in a background thread
        mLoadSoundFileThread = new Thread() {
            public void run() {
                try {
                    Log.e("path", mFile.getAbsolutePath());
                    mSoundFile = SoundFile.create(mFile.getAbsolutePath(), listener);

                    if (mSoundFile == null) {
                        mProgressDialog.dismiss();
                        String name = mFile.getName().toLowerCase();
                        String[] components = name.split("\\.");
                        String err;
                        if (components.length < 2) {
                            err = getResources().getString(
                                    R.string.no_extension_error);
                        } else {
                            err = getResources().getString(
                                    R.string.bad_extension_error) + " " +
                                    components[components.length - 1];
                        }
                        final String finalErr = err;
                        Runnable runnable = new Runnable() {
                            public void run() {
//                                showFinalAlert(new Exception(), finalErr);
                            }
                        };
                        mHandler.post(runnable);
                        return;
                    }
//                    mPlayer = new SamplePlayer(mSoundFile);
                } catch (final Exception e) {
                    mProgressDialog.dismiss();
                    e.printStackTrace();
                    mInfoContent = e.toString();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mInfo.setText(mInfoContent);
                        }
                    });

                    Runnable runnable = new Runnable() {
                        public void run() {
//                            showFinalAlert(e, getResources().getText(R.string.read_error));
                        }
                    };
                    mHandler.post(runnable);
                    return;
                }
                mProgressDialog.dismiss();
                if (mLoadingKeepGoing) {
                    Runnable runnable = new Runnable() {
                        public void run() {
                            finishOpeningSoundFile();
                        }
                    };
                    mHandler.post(runnable);
                } else if (mFinishActivity){
//                    finish();
                }
            }
        };
        mLoadSoundFileThread.start();
    }

    private void finishOpeningSoundFile() {
        ByteBuffer buf = mSoundFile.getmDecodedBytes();
        byte[] bytes = new byte[buf.capacity()];
        computeDoublesForAllZoomLevels();
        computeIntsForThisZoomLevel();
//        Log.e("array", mHeightsAtThisZoomLevel.toString());
//        computeFFT(bytes);
    }

    private void computeDoublesForAllZoomLevels() {
        int numFrames = mSoundFile.getNumFrames();
        int[] frameGains = mSoundFile.getFrameGains();
        double[] smoothedGains = new double[numFrames];
        if (numFrames == 1) {
            smoothedGains[0] = frameGains[0];
        } else if (numFrames == 2) {
            smoothedGains[0] = frameGains[0];
            smoothedGains[1] = frameGains[1];
        } else if (numFrames > 2) {
            smoothedGains[0] = (double)(
                    (frameGains[0] / 2.0) +
                            (frameGains[1] / 2.0));
            for (int i = 1; i < numFrames - 1; i++) {
                smoothedGains[i] = (double)(
                        (frameGains[i - 1] / 3.0) +
                                (frameGains[i    ] / 3.0) +
                                (frameGains[i + 1] / 3.0));
            }
            smoothedGains[numFrames - 1] = (double)(
                    (frameGains[numFrames - 2] / 2.0) +
                            (frameGains[numFrames - 1] / 2.0));
        }

        // Make sure the range is no more than 0 - 255
        double maxGain = 1.0;
        for (int i = 0; i < numFrames; i++) {
            if (smoothedGains[i] > maxGain) {
                maxGain = smoothedGains[i];
            }
        }
        double scaleFactor = 1.0;
        if (maxGain > 255.0) {
            scaleFactor = 255 / maxGain;
        }

        // Build histogram of 256 bins and figure out the new scaled max
        maxGain = 0;
        int gainHist[] = new int[256];
        for (int i = 0; i < numFrames; i++) {
            int smoothedGain = (int)(smoothedGains[i] * scaleFactor);
            if (smoothedGain < 0)
                smoothedGain = 0;
            if (smoothedGain > 255)
                smoothedGain = 255;

            if (smoothedGain > maxGain)
                maxGain = smoothedGain;

            gainHist[smoothedGain]++;
        }

        // Re-calibrate the min to be 5%
        double minGain = 0;
        int sum = 0;
        while (minGain < 255 && sum < numFrames / 20) {
            sum += gainHist[(int)minGain];
            minGain++;
        }

        // Re-calibrate the max to be 99%
        sum = 0;
        while (maxGain > 2 && sum < numFrames / 100) {
            sum += gainHist[(int)maxGain];
            maxGain--;
        }

        // Compute the heights
        double[] heights = new double[numFrames];
        double range = maxGain - minGain;
        for (int i = 0; i < numFrames; i++) {
            double value = (smoothedGains[i] * scaleFactor - minGain) / range;
            if (value < 0.0)
                value = 0.0;
            if (value > 1.0)
                value = 1.0;
            heights[i] = value * value;
        }

        mNumZoomLevels = 1;
        mLenByZoomLevel = new int[mNumZoomLevels];
        mZoomFactorByZoomLevel = new double[mNumZoomLevels];
        mValuesByZoomLevel = new double[mNumZoomLevels][];

        mZoomLevel = 0;

        // Level 0 is doubled, with interpolated values
        mLenByZoomLevel[0] = numFrames * 2;
        mZoomFactorByZoomLevel[0] = 2.0;
        mValuesByZoomLevel[0] = new double[mLenByZoomLevel[0]];
        if (numFrames > 0) {
            mValuesByZoomLevel[0][0] = 0.5 * heights[0];
            mValuesByZoomLevel[0][1] = heights[0];
        }
        for (int i = 1; i < numFrames; i++) {
            mValuesByZoomLevel[0][2 * i] = 0.5 * (heights[i - 1] + heights[i]);
            mValuesByZoomLevel[0][2 * i + 1] = heights[i];
        }
    }

    private void computeIntsForThisZoomLevel() {
        int halfHeight = 100;
        JSONArray jsonArray = new JSONArray();
//        mHeightsAtThisZoomLevel = new int[mLenByZoomLevel[mZoomLevel]];
        for (int i = 0; i < mLenByZoomLevel[mZoomLevel]-1; i++) {
            int a  =
                    (int)(mValuesByZoomLevel[mZoomLevel][i] * halfHeight);
            try {
                jsonArray.put(i, a);
            }
            catch (JSONException e){

            }
        }

        attemptSend("data",jsonArray.toString());
    }

    private long getCurrentTime() {
        return System.nanoTime() / 1000000;
    }

    public void computeFFT(byte[] audio) {
        //Log.e("path11111", Integer.toString(1));
        int totalSize = audio.length;
        int chunkSize = 128;
        int sampledChunkSize = totalSize / 128;
        Complex[][] result = new Complex[sampledChunkSize][];
        float sampleRate = 44100;
        int sampleSizeInBits = 16;
        int channels = 1;          //mono
        boolean signed = true;     //Indicates whether the data is signed or unsigned
        boolean bigEndian = true;
        //Log.e("path222", Integer.toString(2));

        for (int j = 0; j < sampledChunkSize; j++) {
            Complex[] complexArray = new Complex[chunkSize];

            for (int i = 0; i < chunkSize; i++) {
                complexArray[i] = new Complex(audio[(j * chunkSize) + i], 0.0);
            }
            //Log.d("pathfft", "FFT crash");
            Log.d("path_in_funct",""+complexArray.length);

            result[j] = FFT.fft1(complexArray);
            //Log.d("path_next", "fft after");
        }
        Complex[][] points = new Complex[result.length][5];
        Complex[][] highscores = new Complex[result.length][5];
        //Log.e("path333", Integer.toString(3));

        // result is complex matrix obtained in previous step
        for (int t = 0; t < result.length; t++) {
            for (int freq = 40; freq < 300 ; freq++) {
                // Get the magnitude:
                double mag = Math.log(result[t][freq].abs() + 1);

                // Find out which range we are in:
                int index = getIndex(freq);

                // Save the highest magnitude and corresponding frequency:
                if (mag > highscores[t][index].abs()) {
                    points[t][index] = new Complex(freq, 0.0);
                }
            }

            // form hash tag
            long h = hash((long) points[t][0].getRe(),(long) points[t][1].getRe(), (long) points[t][2].getRe(), (long) points[t][3].getRe());
        }
        //Log.e("path444", Integer.toString(3));

        for(int l=0;l<result.length; l++){
            for (int h=0; h<4;h++){
                Log.e("path", Double.toString(points[l][h].getRe()));

            }
        }
    }
        public final int[] RANGE = new int[] { 40, 80, 120, 180, 300 };

    // find out in which range is frequency
    public int getIndex(int freq) {
        int i = 0;
        while (RANGE[i] < freq)
            i++;
        return i;
    }


    private static final int FUZ_FACTOR = 2;

    private long hash(long p1, long p2, long p3, long p4) {
        return (p4 - (p4 % FUZ_FACTOR)) * 100000000 + (p3 - (p3 % FUZ_FACTOR))
                * 100000 + (p2 - (p2 % FUZ_FACTOR)) * 100
                + (p1 - (p1 % FUZ_FACTOR));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off("new message", onNewMessage);
    }

    void play(String path){
        MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource(filePath);
            mp.prepare();
            mp.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}