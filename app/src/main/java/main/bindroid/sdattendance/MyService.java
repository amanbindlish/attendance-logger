package main.bindroid.sdattendance;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.parse.ParseObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import main.bindroid.sdattendance.utills.CommonUtils;

public class MyService extends Service {

	private static final String TAG = MyService.class.getSimpleName();

	private static final Integer WHOS_FANCY_SERVICE_NOTIFICATION_ID = 100;
	private static final int WHOS_FANCY_NOTIFICATION_ID = WHOS_FANCY_SERVICE_NOTIFICATION_ID + 1;

	private BeaconManager mBeaconManager;
	private NotificationManager mNotificationManager;
	private Region mRegion;
	private SharedPreferences mPreferences;

	public MyService() {
	}

	@Override
	public void onCreate() {
		// Configure verbose debug logging, enable this to debugging
		// L.enableDebugLogging(true);

		mRegion = new Region(Globals.REGION, Globals.PROXIMITY_UUID,
				Globals.MAJOR, Globals.MINOR);

		mPreferences = getApplicationContext().getSharedPreferences(
				"preferences", Activity.MODE_PRIVATE);

		// User this to receive notification from all iBeacons
		// mRegion = new Region(Globals.WHOS_FANCY_REGION, PROXIMITY_UUID, null,
		// null);

		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mBeaconManager = new BeaconManager(this);

		// Default values are 5s of scanning and 25s of waiting time to save CPU
		// cycles.
		// In order for this demo to be more responsive and immediate we lower
		// down those values.
		mBeaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1), 0);

		mBeaconManager
				.setMonitoringListener(new BeaconManager.MonitoringListener() {

					@Override
					public void onEnteredRegion(final Region region,
							List<Beacon> beacons) {
						postNotification(getString(R.string.status_entered_region));
						ParseObject loginData = new ParseObject("SDLoginData");
						loginData.put(
								"EmpCode",
								CommonUtils.getLoggedInUser(
										getApplicationContext()).getEmpCode());
						loginData.put("LoginDate",
								CommonUtils.getDate(System.currentTimeMillis()));
						loginData.saveInBackground();
					}

					@Override
					public void onExitedRegion(final Region region) {
//						postNotification(getString(R.string.status_exited_region));
					}
				});
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		// setNotification();

		mNotificationManager.cancel(WHOS_FANCY_NOTIFICATION_ID);
		mBeaconManager.connect(new BeaconManager.ServiceReadyCallback() {

			@Override
			public void onServiceReady() {
				try {
					mBeaconManager.startMonitoring(mRegion);
				} catch (RemoteException e) {
					Log.d(TAG, "Error while starting monitoring");
				}
			}
		});

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mNotificationManager.cancel(WHOS_FANCY_NOTIFICATION_ID);
		mBeaconManager.disconnect();
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.putBoolean(Globals.PREFERENCE_SERVICE_STARTED, false);
		editor.commit();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void postNotification(String msg) {
		PendingIntent pendingIntent = PendingIntent.getActivities(
				MyService.this, 0, null, PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationCompat.Builder notification = new NotificationCompat.Builder(
				MyService.this).setSmallIcon(R.drawable.beacon_gray)
				.setContentTitle(getString(R.string.last_post_notification))
				.setContentText(msg).setAutoCancel(true)
				.setContentIntent(pendingIntent)
				.setDefaults(Notification.DEFAULT_ALL);
		mNotificationManager.notify(WHOS_FANCY_NOTIFICATION_ID,
				notification.build());
	}

	// private void setNotification() {
	// Intent notificationIntent = new Intent(this, MainActivity.class);
	// PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
	// notificationIntent, 0);
	//
	// NotificationCompat.Builder notification = new NotificationCompat.Builder(
	// MyService.this).setSmallIcon(R.drawable.beacon_gray)
	// .setContentTitle(getString(R.string.app_name))
	// .setContentText(getString(R.string.waiting_notification))
	// .setContentIntent(pendingIntent);
	//
	// startForeground(WHOS_FANCY_SERVICE_NOTIFICATION_ID,
	// notification.build());
	// }

}
