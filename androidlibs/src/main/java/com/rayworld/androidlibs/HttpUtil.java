package com.rayworld.androidlibs;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;

/**
 * Created by 이광열 on 2016-05-09.
 */
public class HttpUtil {
    private static final int TIME_OUT = 1500;

    /**
     * PHP 이미지 업로드
     * @param ctx : 액티비티 Context
     * @param handler : 이미지 업로드 완료 통보를 받을 핸들러
     * @param strUrl : 서버 URL
     * @param srcFilePath : 로컬파일 절대경로
     * @param fileName : 서버에 저장 될 파일명
     * @return
     */
    public static int uploadFile(final Activity ctx, final Handler handler, final String strUrl
            , final String srcFilePath, final String fileName, final int successCode) {
        int serverResponseCode = 0;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;
        final File sourceFile = new File(srcFilePath);

        if (!sourceFile.isFile()) {
            Log.e("uploadFile", "Source File not exist :" + sourceFile.getAbsolutePath());
            ctx.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(ctx, "Source File not exist :" + sourceFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                }
            });
            return -1;
        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(strUrl);

                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", srcFilePath);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necessary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {
                    ctx.runOnUiThread(new Runnable() {
                        public void run() {
                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n" + sourceFile.getName();
                            Log.d("uploadFile", msg);

                            File f = new File(srcFilePath);
                            handler.obtainMessage(successCode).sendToTarget();
                            if(f.getAbsolutePath().contains(ctx.getString(R.string.app_name))) {
                                f.delete();
                            }
                        }
                    });
                }
                fileInputStream.close();
                dos.flush();
                dos.close();
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                ctx.runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d("uploadFile", "MalformedURLException Exception : check script url.");
                        Toast.makeText(ctx, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                e.printStackTrace();
                ctx.runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d("uploadFile", "Got Exception : see logcat ");
                        Toast.makeText(ctx, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("FileUtil", "Upload file to server Exception : " + e.getMessage());
            }
            return serverResponseCode;
        }
    }

    public static boolean downloadFile(URL url, File destFile, File tempFile, long fileSize) {
        HttpURLConnection urlConn = null;
        InputStream is = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        FileOutputStream os = null;
        byte[] buf = new byte[16384];

        try {
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setConnectTimeout(TIME_OUT);
            urlConn.setReadTimeout(TIME_OUT);

            is = urlConn.getInputStream();
            bis = new BufferedInputStream(is);
            os = new FileOutputStream(tempFile);
            bos = new BufferedOutputStream(os);

            for (int bytes; (bytes = bis.read(buf) ) != -1;) {
                bos.write(buf, 0, bytes);
            }
            return true;
        } catch (SocketTimeoutException e2) {
            e2.printStackTrace();
        } catch (MalformedURLException e2) {
            e2.printStackTrace();
        } catch (IOException e3) {
            e3.printStackTrace();
        } finally {
            try {
                if(bos!=null) {
                    bos.flush();
                    bos.close();
                }
                if(bis!=null) {
                    bis.close();
                }
                if(tempFile.length() == fileSize) {
                    tempFile.renameTo(destFile);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return false;
    }

    public static String GET(String url) {
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
            inputStream = httpResponse.getEntity().getContent();
            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);
            } else {
                result = "Did not work!";
            }
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    public static String POST(String url, List<NameValuePair> mParams) {
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            post.setEntity(new UrlEncodedFormEntity(mParams, "UTF-8"));
            HttpResponse httpResponse = httpclient.execute(post);

            inputStream = httpResponse.getEntity().getContent();
            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);
            } else {
                result = "Did not work!";
            }
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;

    }

    public static String POST(String url, JSONObject obj) {
        InputStream inputStream;
        String result = "";
        HttpClient httpclient = new DefaultHttpClient();

        try {
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-type", "application/json");

            StringEntity se = new StringEntity(obj.toString());
            se.setContentEncoding(new BasicHeader(org.apache.http.protocol.HTTP.CONTENT_TYPE, "application/json"));
            post.setEntity(se);

            HttpResponse httpResponse = httpclient.execute(post);
            inputStream = httpResponse.getEntity().getContent();
            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);
            } else {
                result = "Did not work!";
            }
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    public static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "EUC-KR"));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

}
