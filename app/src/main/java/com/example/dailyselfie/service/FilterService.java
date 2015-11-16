package com.example.dailyselfie.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.example.dailyselfie.MainActivity;
import com.example.dailyselfie.clientApi.SelfieServerApi;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class FilterService extends Service {

    private static final String TAG = "FilterService";
    private final IBinder mBinder = new LocalBinder();
    private static final Matrix sMatrix = new Matrix();

    private Handler mServiceHandler;
    private Handler mUIHandler;
    private SelfieServerApi mSelfieServerApi;
    public FilterService() {
        super();

        sMatrix.preScale(1, -1);
        // Start up the thread running the service.

        //Setting OkHTTPClient
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(15, TimeUnit.SECONDS);

        //Setting Collaboration Service for REST communication
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(new OkClient(okHttpClient))
                        //.setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(SelfieServerApi.SERVER_URL)
                .build();

        mSelfieServerApi = restAdapter.create(SelfieServerApi.class);
        ServiceThread serviceThread = new ServiceThread();
        serviceThread.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "SERVICE onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // Ending Service Thread
        Message msg = mServiceHandler.obtainMessage(ServiceThread.END_SERVICE);
        mServiceHandler.sendMessageAtFrontOfQueue(msg);
        return super.onUnbind(intent);
    }

    public void registerHandler(Handler handler){
        Log.d(TAG, "Setting UI Handler in Service");
        mUIHandler = handler;

    }

    public void applyFilter(File picturePath, int filter){
        Message msg = mServiceHandler.obtainMessage(ServiceThread.FILTER_PIC);
        msg.obj = picturePath;
        msg.arg1 = filter;
        mServiceHandler.sendMessage(msg);
    }

    public class LocalBinder extends Binder{
        public FilterService getService(){
            return FilterService.this;
        }
    }

    //Thread to manage the reception of Video Frames and Audio chunks.
    private class ServiceThread extends HandlerThread implements Handler.Callback {
        public static final int FILTER_PIC = 1;
        public static final int END_SERVICE = 2;

        public ServiceThread() {
            super("FilterService");
        }

        @Override
        protected void onLooperPrepared() {
            mServiceHandler = new Handler(this);
            super.onLooperPrepared();
        }

        private void sendMessageToMyself(int what) {
            Message msg = mServiceHandler.obtainMessage(what);
            mServiceHandler.sendMessage(msg);
        }

        @Override
        public boolean handleMessage(Message msg) {

            Response response = null;
            switch(msg.what){
                case FILTER_PIC:

                    if(mUIHandler == null)
                        break;

                    try {
                        int filter = msg.arg1;
                        File picFile = (File)msg.obj;
                        response = mSelfieServerApi.getImage("filter", new TypedFile("image/jpeg", picFile));
                        if(response != null && response.getBody() != null) {
                            Bitmap bitmap = BitmapFactory.decodeStream(response.getBody().in());
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), sMatrix, true);
                            Message uiMsg = mUIHandler.obtainMessage(MainActivity.PICTURE_FILTERED, bitmap);
                            mUIHandler.sendMessage(uiMsg);
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "IOException Frame:"+e);
                        e.printStackTrace();
                    }catch(NullPointerException e){
                        Log.e(TAG, "NullPointerException Frame:"+e);
                    }
                    break;
                case END_SERVICE:
                    mServiceHandler.getLooper().quit();
                    break;
            }
            return true;
        }
    }
}
