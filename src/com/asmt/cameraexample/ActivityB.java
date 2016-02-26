package com.asmt.cameraexample;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TimePicker;

public class ActivityB extends Activity {
	TimePicker tim;
	private MediaPlayer mMediaPlayer;
	private Vibrator vibrator;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		setContentView(R.layout.activity_activityb);
		tim = (TimePicker)findViewById(R.id.timePicker);
		
		playSound(this, getAlarmUri());
	}
	private void playSound(Context context, Uri alert) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);

                long[] pattern = {0, 100, 1000};
                vibrator.vibrate(pattern, 0);
            }
        } catch (IOException e) {
            System.out.println("OOPS");
        }
    }
	private Uri getAlarmUri() {
        Uri alert = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            alert = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null) {
                alert = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        return alert;
    }
	public void stopAlarm(){
		mMediaPlayer.stop();
		vibrator.cancel();
	}
	public void returnFromThis(View v){
		stopAlarm();
		Intent data = new Intent();
		data.putExtra("name",tim.getCurrentHour()+":"+tim.getCurrentMinute());
		setResult(RESULT_OK,data);
		finish();
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		stopAlarm();
		super.onBackPressed();
	}
	
}
