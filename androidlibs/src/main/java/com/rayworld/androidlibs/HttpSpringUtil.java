package com.rayworld.androidlibs;

import android.app.Activity;
import android.os.Handler;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Created by 이광열 on 2016-05-09.
 */
public class HttpSpringUtil {
    private static final int TIME_OUT = 1500;

    public static void uploadFile(final int requestCode, final Handler handler, final String url, final Map<String, Object> map) {
        try {
            HttpHeaders fileHeaders = new HttpHeaders();
            fileHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();

            for (String key : map.keySet()) {
                String value = (String) map.get(key);
                if (key.equals("uploaded_file")) {
                    parts.add(key, new FileSystemResource(value));
                } else {
                    parts.add(key, value);
                }
            }
            HttpEntity<MultiValueMap<String, Object>> fileEntity = new HttpEntity<>(parts, fileHeaders);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, fileEntity, String.class);
            handler.obtainMessage(requestCode, Activity.RESULT_OK, 0, responseEntity.getBody()).sendToTarget();

        } catch (RestClientException e) {
            handler.obtainMessage(requestCode, Activity.RESULT_CANCELED, 0, e.getMessage()).sendToTarget();
        }
    }
}
