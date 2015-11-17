package com.example.sgarcia.selfieserver.backend.client;

/**
 * Created by sgarcia on 11/3/2015.
 */
import org.springframework.web.multipart.MultipartFile;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

public interface SelfieServerApi {
    public static final String SERVER_URL = "http://10.0.2.2:8080/";
    public static final String IMAGE_SVC_PATH = "/image";

    public static final int FILTER_GRAY = 1;
    public static final int FILTER_SEPIA = 2;

    @GET(IMAGE_SVC_PATH)
    public Response getImage(@Query("filter")int filter, @Part("data") TypedFile file);
}
