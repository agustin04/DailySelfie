package com.example.dailyselfie;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.dailyselfie.service.FilterService;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

public class MainActivity extends ListActivity {

	private static final String TAG = "DailySelfie";
	static final int REQUEST_IMAGE_CAPTURE = 1;
	static final int REQUEST_LOGIN = 2;
	
	public File SELFIE_DIR = null;
    private String userId;
    private int loginType;

	private SelfieAdapter mSelfieAdapter;

	private File mLastPictureFile; 

	private static final long INITIAL_ALARM_DELAY = 2 * 60 * 1000L;
	private AlarmManager mAlarmManager;
	private Intent mNotificationReceiverIntent;
	private PendingIntent mNotificationReceiverPendingIntent;
    private ImageView logoutBtn;

    public static final int PICTURE_FILTERED = 1;
	//Bindings with the communication service class
	private FilterService mService = null;
	private boolean mIsBound;
	private static UIHandler mUIHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");
		mUIHandler = new UIHandler();
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        init();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindCollaborationService();
	}

	private void init() {
		ListView listView = getListView();

		bindCollaborationService();

        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getStringExtra(LoginActivity.EXTRA_USER_ID);
            loginType = intent.getIntExtra(LoginActivity.EXTRA_LOGIN_TYPE, -1);
        }
        SELFIE_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+"/DailySelfie/"+userId);

		mSelfieAdapter = new SelfieAdapter(this, SELFIE_DIR);
		listView.setAdapter(mSelfieAdapter);
        logoutBtn = (ImageView) findViewById(R.id.logout_btn);
		logoutBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				LoginManager.getInstance().logOut();
				Intent login = new Intent(MainActivity.this, LoginActivity.class);
				startActivity(login);
				finish();
            }
		});
		registerForContextMenu(listView);
		setAlarms();

	}

	public void setAlarms(){
		// Get the AlarmManager Service
		mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		
		// Create an Intent to broadcast to the AlarmNotificationReceiver
				mNotificationReceiverIntent = new Intent(this,
						NotificationReceiver.class);
		
				// Create an PendingIntent that holds the NotificationReceiverIntent
		mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
				this, 0, mNotificationReceiverIntent, 0);
				
		// Set single alarm
		mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + INITIAL_ALARM_DELAY, INITIAL_ALARM_DELAY,
                mNotificationReceiverPendingIntent);
	}

	private File createImageFile() throws IOException {
	    // Create an image file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "JPEG_" + timeStamp + "_";
	    //File storageDir = Environment.getExternalStoragePublicDirectory(
	     //       Environment.DIRECTORY_PICTURES+"/DailySelfie");
	    SELFIE_DIR.mkdirs();
	    
	    File image = File.createTempFile(
	        imageFileName,  /* prefix */
	        ".jpg",         /* suffix */
	        SELFIE_DIR      /* directory */
	    );

	    return image;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
				
		if (requestCode == REQUEST_IMAGE_CAPTURE) {
			if(resultCode == RESULT_OK) {
				Log.i(TAG, "On Activity Result - all was good");
				mSelfieAdapter.add(mLastPictureFile);
			} else {
				Log.i(TAG, "On Activity Result - User cancelled");
				if(mLastPictureFile != null){
					Log.i(TAG,"On Activity Result - deleting picture");
					mLastPictureFile.delete();
				}
			}
		} /*else if (requestCode == REQUEST_LOGIN && resultCode == RESULT_OK) {
			if(resultCode == RESULT_OK) {
                Log.i(TAG, "login successful");

			} else {
                Log.i(TAG, "Error login");
                finish();
			}
		}*/
	}

    public void applyFilter(String picturePath, int filter){
        File picFile = new File(picturePath);
        mService.applyFilter(picFile, filter);
    }

	private void dispatchTakePictureIntent() {
		Log.i(TAG,"Taking picture");
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    // Ensure that there's a camera activity to handle the intent
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        // Create the File where the photo should go
	        File photoFile = null;
	        try {
	        	Log.i(TAG,"Creating image file");
	            photoFile = createImageFile();
	            Log.i(TAG,"Image file:"+photoFile.toString());
	        } catch (IOException ex) {
	            // Error occurred while creating the File

	        }
	        // Continue only if the File was successfully created
	        if (photoFile != null) {
	        	
	        	mLastPictureFile = photoFile;
	        	
	        	Log.i(TAG,"Starting camera intent");
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
	                    Uri.fromFile(photoFile));
	            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
	            //startActivity(takePictureIntent);
	        }
	    }

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		if(id == R.id.action_camera){
			
			dispatchTakePictureIntent();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.context_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case R.id.context_delete:
			
			return true;
		default:
			return super.onContextItemSelected(item);
		}

	}

	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className,
									   IBinder service) {

			// This is called when the connection with the service has been
			// established
			FilterService.LocalBinder binder = (FilterService.LocalBinder) service;
			mService = binder.getService();
			mService.registerHandler(mUIHandler);
			Log.i(TAG,"Service Attached.");

			mIsBound = true;
		}

		public void onServiceDisconnected(ComponentName className) {

			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			mService = null;
			mIsBound = false;

			Log.i(TAG,"Service Disconnected");
		}
	};

	private void bindCollaborationService(){
		// Establish a couple connections with the service
		Log.e(TAG,"Binding Service");
		if (!mIsBound) {
			Intent intent = new Intent(this, FilterService.class);
			bindService(intent,
					mConnection, Context.BIND_AUTO_CREATE);
		}
	}

	private void unbindCollaborationService(){
		if (mIsBound) {
			Log.e(TAG,"UNBinding Service");
			unbindService(mConnection);
		}
	}

	private static class UIHandler extends Handler{

		public UIHandler(){

		}
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
                case PICTURE_FILTERED:
                    Bitmap picture = (Bitmap) msg.obj;
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String imageFileName = "JPEG_" + timeStamp + "_";
                    File storageDir = Environment.getExternalStoragePublicDirectory(
                           Environment.DIRECTORY_PICTURES+"/DailySelfie");
                    File image = null;
                    try {
                        image = File.createTempFile(
                                imageFileName,  /* prefix */
                                ".jpg",         /* suffix */
                                storageDir      /* directory */
                        );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(image != null) {
                        FileOutputStream out = null;
                        try {
                            out = new FileOutputStream(image);
                            picture.compress(Bitmap.CompressFormat.JPEG, 60, out); // bmp is your Bitmap instance
                            // PNG is a lossless format, the compression factor (100) is ignored
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (out != null) {
                                    out.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
			}
		};
	};
}
