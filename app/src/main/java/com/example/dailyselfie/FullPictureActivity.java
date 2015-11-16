package com.example.dailyselfie;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class FullPictureActivity extends Activity {
	public static final String PICTURE_PATH_EXTRA = "picture_path_extra";
	
	private static final String TAG = "DailySelfie";
	
	private Intent mIntent;
	private ImageView mImageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_full_picture);
		
		mIntent = getIntent();
		
		Log.i(TAG,"Full Picture");
		if(mIntent == null){
			finish();
		}
		
		mImageView = (ImageView) findViewById(R.id.full_picture);

	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus){
			openScaledPicture();		
		}
	
	}
	
	private void openScaledPicture(){
		Log.i(TAG,"Full Picture- intent not NULL");
		Bundle extras = mIntent.getExtras();
		String path = extras.getString(PICTURE_PATH_EXTRA);
		if(!path.isEmpty()){
			Log.i(TAG,"Full Picture - path:"+path);
			
			int targetW = mImageView.getWidth();
		    int targetH = mImageView.getHeight();
		    
		    //if(targetH == 0 || targetW == 0)
		    //	targetH = targetW = 50;

		    // Get the dimensions of the bitmap
		    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		    bmOptions.inJustDecodeBounds = true;
		    BitmapFactory.decodeFile(path, bmOptions);
		    int photoW = bmOptions.outWidth;
		    int photoH = bmOptions.outHeight;

		    // Determine how much to scale down the image
		    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

		    // Decode the image file into a Bitmap sized to fill the View
		    bmOptions.inJustDecodeBounds = false;
		    bmOptions.inSampleSize = scaleFactor;
		    bmOptions.inPurgeable = true;

		    Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
		
			mImageView.setImageBitmap(bitmap);
		}else{
			finish();
		}
	}
}
