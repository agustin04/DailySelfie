package com.example.dailyselfie;

import java.io.File;

import android.graphics.Bitmap;

public class Selfie {

	private File selfieFile;
	private Bitmap selfiePicture;
	
	public Selfie(File selfieFile){
		this.selfieFile = selfieFile;
	}
	
	public File getSelfieFile() {
		return selfieFile;
	}
	public void setSelfieFile(File selfieFile) {
		this.selfieFile = selfieFile;
	}
	public Bitmap getSelfiePicture() {
		return selfiePicture;
	}
	public void setSelfiePicture(Bitmap selfiePicture) {
		this.selfiePicture = selfiePicture;
	}
	
	
}
