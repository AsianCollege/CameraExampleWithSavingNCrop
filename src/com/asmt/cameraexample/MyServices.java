package com.asmt.cameraexample;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class MyServices extends Service {
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Toast.makeText(getBaseContext(), "Service destoryed", Toast.LENGTH_LONG).show();
		super.onDestroy();
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		 Toast.makeText(getBaseContext(), "Service started", Toast.LENGTH_LONG).show();	
		 pushNotification("1","this is first notification");
		 
		 return START_STICKY;
	}
	
	public void pushNotification(String id, String msg){
		NotificationManager NM;
		NM=(NotificationManager)getSystemService(MyServices.NOTIFICATION_SERVICE);		
		
		Intent notificationIntent = new Intent(MyServices.this,  MainActivity.class);		
		PendingIntent pending = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,0);			
		
		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		
		NotificationCompat.Builder builder = new NotificationCompat.Builder(MyServices.this);  					
		builder.setContentIntent(pending);
		builder.setSound(alarmSound)	
		.setSmallIcon(R.drawable.ic_launcher)
		.setLights(Color.BLUE, 500, 500)
		.setVibrate(new long[]{500,500,500,500})
		.setContentTitle(getString(R.string.app_name))
		.setContentText(msg);  				
		
		NM.notify(Integer.parseInt(id), builder.build());
		
		Intent dialogIntent = new Intent(this, ActivityB.class);
		dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(dialogIntent);
	}
	

}
