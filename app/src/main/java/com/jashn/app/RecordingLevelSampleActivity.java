package com.jashn.app;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ringdroid.R;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;

public class RecordingLevelSampleActivity extends Activity {

	public static final int SAMPLE_RATE = 16000;

	private AudioRecord mRecorder;
	private File mRecording;
	private short[] mBuffer;
	private final String startRecordingLabel = "Connect";
	private final String stopRecordingLabel = "Disconnect";
	private boolean mIsRecording = false;
//	private ProgressBar mProgressBar;
    private double aDouble = 0;
    private int deviceId = 1;
    private LinearLayout background;
    private int[] colors = {
			Color.rgb(224, 124, 0),
            Color.rgb(42, 212, 0),
            Color.rgb(0, 207, 44),
			Color.rgb(229, 31, 0),
			Color.rgb(220, 213, 0),
			Color.rgb(0, 105, 195),
			Color.rgb(133, 216, 0),
            Color.rgb(0, 203, 128),
            Color.rgb(0, 190, 199),
            Color.rgb(0, 105, 195),
            Color.rgb(0, 24, 191)};
    private int[] colors_narrow = {Color.parseColor("#E5E4E2"),
            Color.parseColor("#000080"),
            Color.parseColor("#E4ED61"),
            Color.parseColor("#F0FFFF"),
            Color.parseColor("#43C6DB"),
            Color.parseColor("#667C26"),
            Color.parseColor("#52D017"),
            Color.parseColor("#9617D1"),
            Color.parseColor("#FFFF00"),
            Color.parseColor("#FFD801"),
            Color.parseColor("#AF7817"),
            Color.parseColor("#173180"),
            Color.parseColor("#6F4E37"),
            Color.parseColor("#2D6580"),
            Color.parseColor("#FF8040"),
            Color.parseColor("#40BFFF"),
            Color.parseColor("#FF0000"),
            Color.parseColor("#8C001A"),
            Color.parseColor("#058246"),
            Color.parseColor("#F6358A"),
            Color.parseColor("#4B0082")
            };

    private HashMap<Integer,Integer> colorMap = new HashMap<>();
    private HashMap<Integer,Integer> colorMap1 = new HashMap<>();


    @Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

        background = (LinearLayout)findViewById(R.id.background);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = (float)1.0;
        getWindow().setAttributes(lp);

//        deviceId = 1+(int)(Math.random() * 10);

        for (int i=0;i<10;i++)
            colorMap.put(i, colors[i]);

        for (int i=0;i<20;i++)
            colorMap1.put(i, colors_narrow[i]);

        initRecorder();

//		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

		final Button button = (Button) findViewById(R.id.button);
		button.setText(startRecordingLabel);

		button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!mIsRecording) {
                    button.setText(stopRecordingLabel);
                    mIsRecording = true;
                    mRecorder.startRecording();
                    mRecording = getFile("raw");
                    startBufferedWrite(mRecording);
                } else {
                    button.setText(startRecordingLabel);
                    mIsRecording = false;
                    mRecorder.stop();
                    Log.e("aDouble", aDouble + "");
//					File waveFile = getFile("wav");
//					try {
//						rawToWave(mRecording, waveFile);
//					} catch (IOException e) {
//						Toast.makeText(RecordingLevelSampleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//					}
//					Toast.makeText(RecordingLevelSampleActivity.this, "Recorded to " + waveFile.getName(),
//							Toast.LENGTH_SHORT).show();
                }
            }
        });
	}

	@Override
	public void onDestroy() {
		mRecorder.release();
		super.onDestroy();
	}

	private void initRecorder() {
		int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		mBuffer = new short[bufferSize];
		mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT, bufferSize);
	}

	private void startBufferedWrite(final File file) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				DataOutputStream output = null;
				try {
					output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
					while (mIsRecording) {
						double sum = 0;
						int readSize = mRecorder.read(mBuffer, 0, mBuffer.length);
						for (int i = 0; i < readSize; i++) {
							output.writeShort(mBuffer[i]);
							sum += mBuffer[i] * mBuffer[i];
						}
						if (readSize > 0) {
							final double amplitude = sum / readSize;
							int a = (int) Math.sqrt(amplitude);
							if(a > 2000) {
                                if(a > aDouble)
                                    aDouble = a;
								Log.e("dekho", amplitude + " " + a + "");
                                final int colorId, colorMapVal;
                                if(a>8000 && a<16000)
                                {
                                    colorId = (deviceId + (a / 400)) % 20;
                                    colorMapVal = colorMap1.get(colorId);
                                } else{
                                    colorId = (deviceId + (a / 2000)) % 10;
                                    colorMapVal = colorMap.get(colorId);
                                }

                                Log.e("hhh", colorId+","+deviceId);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        background.setBackgroundColor(colorMapVal);
                                    }
                                });
//								mProgressBar.setProgress((int) (Math.sqrt(amplitude)));
							}
							else {
//								mProgressBar.setProgress((int) (0));
							}
						}
					}
				} catch (IOException e) {
					Toast.makeText(RecordingLevelSampleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
				} finally {
//					mProgressBar.setProgress(0);
					if (output != null) {
						try {
							output.flush();
						} catch (IOException e) {
							Toast.makeText(RecordingLevelSampleActivity.this, e.getMessage(), Toast.LENGTH_SHORT)
									.show();
						} finally {
							try {
								output.close();
							} catch (IOException e) {
								Toast.makeText(RecordingLevelSampleActivity.this, e.getMessage(), Toast.LENGTH_SHORT)
										.show();
							}
						}
					}
				}
			}
		}).start();
	}

    public void showBezier(View v, int initX, int limitX, int initY, int limitY) {
        ArcTranslateAnimation animation = new ArcTranslateAnimation(
                initX, limitX, initY, limitY);
        animation
                .setInterpolator(new LinearInterpolator());
        animation.setDuration(200);
        animation.setFillAfter(true);

        findViewById(R.id.my_circle1).startAnimation(animation);
    }

	private void rawToWave(final File rawFile, final File waveFile) throws IOException {

		byte[] rawData = new byte[(int) rawFile.length()];
		DataInputStream input = null;
		try {
			input = new DataInputStream(new FileInputStream(rawFile));
			input.read(rawData);
		} finally {
			if (input != null) {
				input.close();
			}
		}

		DataOutputStream output = null;
		try {
			output = new DataOutputStream(new FileOutputStream(waveFile));
			// WAVE header
			// see http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
			writeString(output, "RIFF"); // chunk id
			writeInt(output, 36 + rawData.length); // chunk size
			writeString(output, "WAVE"); // format
			writeString(output, "fmt "); // subchunk 1 id
			writeInt(output, 16); // subchunk 1 size
			writeShort(output, (short) 1); // audio format (1 = PCM)
			writeShort(output, (short) 1); // number of channels
			writeInt(output, SAMPLE_RATE); // sample rate
			writeInt(output, SAMPLE_RATE * 2); // byte rate
			writeShort(output, (short) 2); // block align
			writeShort(output, (short) 16); // bits per sample
			writeString(output, "data"); // subchunk 2 id
			writeInt(output, rawData.length); // subchunk 2 size
			// Audio data (conversion big endian -> little endian)
			short[] shorts = new short[rawData.length / 2];
			ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
			ByteBuffer bytes = ByteBuffer.allocate(shorts.length * 2);
			for (short s : shorts) {
				bytes.putShort(s);
			}
			output.write(bytes.array());
		} finally {
			if (output != null) {
				output.close();
			}
		}
	}

	private File getFile(final String suffix) {
		Time time = new Time();
		time.setToNow();
		return new File(Environment.getExternalStorageDirectory(), time.format("%Y%m%d%H%M%S") + "." + suffix);
	}

	private void writeInt(final DataOutputStream output, final int value) throws IOException {
		output.write(value >> 0);
		output.write(value >> 8);
		output.write(value >> 16);
		output.write(value >> 24);
	}

	private void writeShort(final DataOutputStream output, final short value) throws IOException {
		output.write(value >> 0);
		output.write(value >> 8);
	}

	private void writeString(final DataOutputStream output, final String value) throws IOException {
		for (int i = 0; i < value.length(); i++) {
			output.write(value.charAt(i));
		}
	}
}