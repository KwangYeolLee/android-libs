package com.rayworld.androidlibs;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;

/**
 * Created by 이광열 on 2016-05-18.
 */
public class FileUtil {

    public static boolean saveImageFile(File desFile, Bitmap bitmap) {
        try {
            FileOutputStream outputStream = new FileOutputStream(desFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean saveObjectFile(File srcFile, Object obj) {
        boolean isSuccess = false;
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        try {
            fos = new FileOutputStream(srcFile);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
            isSuccess = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fos != null) try{fos.close();}catch(IOException e){}
            if(oos != null) try{oos.close();}catch(IOException e){}
            return isSuccess;
        }
    }

    public static Object loadObjectFile(File srcFile) {
        Object obj = null;

        if(!srcFile.exists()) {
            return null;
        }

        FileInputStream fis = null;
        ObjectInputStream ois = null;

        try {
            fis = new FileInputStream(srcFile);
            ois = new ObjectInputStream(fis);
            obj = ois.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(fis != null) try{fis.close();}catch(IOException e){}
            if(ois != null) try{ois.close();}catch(IOException e){}

            return obj;
        }
    }

    public static Properties loadProperties(File srcFile) {
        Properties props = null;
        FileInputStream fis = null;

        if(!srcFile.exists()) {
            return null;
        }

        try {
            fis = new FileInputStream(srcFile);
            props = new Properties();
            props.load(fis);
        }  catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fis != null) try{fis.close();}catch(IOException e){}
            return props;
        }
    }

    /**
     * path 경로의 모든 폴더/파일 삭제
     * @param path : 폴더/파일 절대경로
     * @return : 삭제여부
     */
    public static boolean deleteDirectory(File path) {
        if(!path.exists()) {
            return false;
        }

        File[] files = path.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                deleteDirectory(file);
            } else {
                file.delete();
            }
        }

        return path.delete();
    }

    /**
     * 파일 확장자를 반환
     * @param fileName 파일명
     * @return 확장자
     */
    public static String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

}
