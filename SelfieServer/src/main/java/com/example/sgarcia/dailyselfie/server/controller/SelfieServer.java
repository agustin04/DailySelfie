package com.example.sgarcia.dailyselfie.server.controller;

    import com.example.sgarcia.dailyselfie.server.client.SelfieServerApi;
import com.example.sgarcia.dailyselfie.server.util.ColorAlteration;
import com.google.common.io.ByteStreams;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sgarcia on 11/3/2015.
 */
@Controller
public class SelfieServer{


    @RequestMapping(value=SelfieServerApi.IMAGE_SVC_PATH, method= RequestMethod.POST)
    public @ResponseBody byte[] getImage(@RequestParam("filter") String name,
                           @RequestParam("picture") String encodedFile){
        System.out.println("ENTERING to get Image filter:"+name);
        BufferedImage filterImage = null;
        /*if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                filterImage = ColorAlteration.toBufferedImage(bytes);

                filterImage = ColorAlteration.toGrayScale(filterImage);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println( "You failed to upload " + name
                    + " because the file was empty.");
        }*/

        byte[] pictureOut = null;

        //pictureOut = ColorAlteration.convertToBytes(filterImage);

        return pictureOut;
    }
}

//Get multiparn on Spring
//http://stackoverflow.com/questions/20162474/how-do-i-receive-a-file-upload-in-spring-mvc-using-both-multipart-form-and-chunk

//Return multipart image file
//http://stackoverflow.com/questions/28408271/how-to-send-multipart-form-data-with-resttemplate-spring-mvc

//Seding with retrofit
//http://stackoverflow.com/questions/27209319/sending-an-image-file-post-with-retrofit