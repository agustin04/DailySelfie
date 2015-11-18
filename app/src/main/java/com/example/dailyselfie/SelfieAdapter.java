package com.example.dailyselfie;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dailyselfie.clientApi.SelfieServerApi;

public class SelfieAdapter extends BaseAdapter{

	private ArrayList<Selfie> list = new ArrayList<Selfie>();
	private static LayoutInflater inflater = null;
	private SparseBooleanArray mSelectedItemsIds;
	private Context mContext;
	
	public static final String NAME_FORMAT = "JPEG_yyyyMMdd_HHmmss";
	
	public SelfieAdapter(Context context, File selfieDir){
		mContext = context;
		inflater = LayoutInflater.from(mContext);
		mSelectedItemsIds = new SparseBooleanArray();
		
		getListFiles(selfieDir);
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Selfie getItem(int position) {
		return list.get(position);
	}


	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View newView = convertView;
		ViewHolder holder;

		final Selfie curr = list.get(position);

		if (null == convertView) {
			holder = new ViewHolder();
			newView = inflater.inflate(R.layout.picture_item, null);
			holder.imageView = (ImageView) newView.findViewById(R.id.pic_image);
			holder.textView = (TextView) newView.findViewById(R.id.pic_name);
			newView.setTag(holder);
			
		} else {
			holder = (ViewHolder) newView.getTag();
		}
		
		//holder.imageView
		setPic(holder.imageView, curr);
		holder.textView.setText(getNameFromFile(curr.getSelfieFile()));
		
		/*newView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Intent intent = new Intent(mContext, FullPictureActivity.class);
				//intent.putExtra(FullPictureActivity.PICTURE_PATH_EXTRA, curr.getSelfieFile().getAbsolutePath());
				((MainActivity)mContext).applyFilter(curr.getSelfieFile().getAbsolutePath(), SelfieServerApi.FILTER_GRAY);
				
				//mContext.startActivity(intent);
			}
		});*/
		
		return newView;
	}
	
	private String getNameFromFile(File file){
		if(file.isFile()){
			return file.getName().substring(0,NAME_FORMAT.length());
		}
		else
			return null;
	}
	
	private void setPic(ImageView imageView, Selfie selfie) {
	    // Get the dimensions of the View
	    int targetW = imageView.getWidth();
	    int targetH = imageView.getHeight();
	    
	    if(targetH == 0 || targetW == 0)
	    	targetH = targetW = 100;
	    String currentPath = selfie.getSelfieFile().getAbsolutePath();

	    if(selfie.getSelfiePicture() == null){
	    	// Get the dimensions of the bitmap
	    	BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    	bmOptions.inJustDecodeBounds = true;
	    	BitmapFactory.decodeFile(currentPath, bmOptions);
	    	int photoW = bmOptions.outWidth;
	    	int photoH = bmOptions.outHeight;

	    	// Determine how much to scale down the image
	    	int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

	    	// Decode the image file into a Bitmap sized to fill the View
	    	bmOptions.inJustDecodeBounds = false;
	    	bmOptions.inSampleSize = scaleFactor;
	    	bmOptions.inPurgeable = true;

	    	Bitmap bitmap = BitmapFactory.decodeFile(currentPath, bmOptions);
	    	selfie.setSelfiePicture(bitmap);
	    }
	    
	    imageView.setImageBitmap(selfie.getSelfiePicture());
	}
	
	private void getListFiles(File parentDir) {
	    File[] files = parentDir.listFiles();
        if(files != null && files.length > 0)
	    for (File file : files) {
	        if (file.isFile()) {
	        	if(file.getName().endsWith(".jpg")){
	                list.add(0,new Selfie(file));
	            }
	        }
	    }
	}
	
	public void add(File listItem) {
		list.add(0,new Selfie(listItem));
		
		notifyDataSetChanged();
	}
	
	public ArrayList<Selfie> getList(){
		return list;
	}

	public void remove(Selfie object) {
		list.remove(object);
		notifyDataSetChanged();
	}

	public void removeAllViews(){
		list.clear();
		this.notifyDataSetChanged();
	}

	public void toggleSelection(int position) {
		selectView(position, !mSelectedItemsIds.get(position));
	}

	public void removeSelection() {
		mSelectedItemsIds = new SparseBooleanArray();
		notifyDataSetChanged();
	}

	public void selectView(int position, boolean value) {
		if (value)
			mSelectedItemsIds.put(position, value);
		else
			mSelectedItemsIds.delete(position);
		notifyDataSetChanged();
	}

	public int getSelectedCount() {
		return mSelectedItemsIds.size();
	}

	public SparseBooleanArray getSelectedIds() {
		return mSelectedItemsIds;
	}

	static class ViewHolder{
		private ImageView imageView;
		private TextView textView;
	}
	
}
