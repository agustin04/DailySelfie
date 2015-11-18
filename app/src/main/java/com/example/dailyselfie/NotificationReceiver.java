package com.example.dailyselfie;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver{

	// Notification ID to allow for future updates
	private static final int MY_NOTIFICATION_ID = 1;
	
	// Notification Text Elements
	private final CharSequence tickerText = "Time for another selfie!";
	private final CharSequence contentTitle = "Daily SelfieBean";
	private final CharSequence contentText = "Time for another selfie";
	
	private long[] mVibratePattern = { 0, 200, 200, 300 };
		
		// Notification Action Elements
	private Intent mNotificationIntent;
	private PendingIntent mContentIntent;

	@Override
	public void onReceive(Context context, Intent intent) {
		// The Intent to be used when the user clicks on the Notification View
				mNotificationIntent = new Intent(context, LoginActivity.class);

				// The PendingIntent that wraps the underlying Intent
				mContentIntent = PendingIntent.getActivity(context, 0,
						mNotificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

				// Build the Notification
				Notification.Builder notificationBuilder = new Notification.Builder(
						context).setTicker(tickerText)
						.setSmallIcon(R.drawable.ic_menu_camera)
						.setAutoCancel(true).setContentTitle(contentTitle)
						.setContentText(contentText).setContentIntent(mContentIntent)
						.setVibrate(mVibratePattern);

				// Get the NotificationManager
				NotificationManager mNotificationManager = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);

				// Pass the Notification to the NotificationManager:
				mNotificationManager.notify(MY_NOTIFICATION_ID,
						notificationBuilder.build());
		
	}

}
