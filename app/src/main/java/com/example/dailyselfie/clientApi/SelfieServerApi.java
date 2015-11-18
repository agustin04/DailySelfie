package com.example.dailyselfie.clientApi;

/**
 * Created by sgarcia on 11/3/2015.
 */

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

public interface SelfieServerApi {
    public static final String SERVER_URL = "http://1.dailyserver-1116.appspot.com";
    public static final String IMAGE_SVC_PATH = "/image";

    public static final int FILTER_GRAY = 1;
    public static final int FILTER_SEPIA = 2;

    @POST(IMAGE_SVC_PATH)
    public SelfieBean getImage(@Body SelfieBean bean);

    @GET(IMAGE_SVC_PATH)
    public String getImage();
}
