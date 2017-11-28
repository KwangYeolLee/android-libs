package com.rayworld.androidlibs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StatFs;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class CommonUtil {
	private static final String TAG = "NUtill";
	
	/**
	 * 네트워크 연결상태
	 * @param context
	 * @return true : 연결, false : 연결안됨
	 */
	public static boolean IsNetWorkConnected(Context context) {
		ConnectivityManager mgr = (ConnectivityManager)context.getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo ni = mgr.getActiveNetworkInfo();
		if(ni!=null) {
			Log.d(TAG, ni.toString());
			return true;
		}
		else {
			return false;
		}
	}
	

    /**
     * CPU 사용량 계산
     * @return CPU 사용량을 백분율로 반환한다
     */
    public static int getCpuUsage() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();

            String[] toks = load.split(" ");

            long idle1 = Long.parseLong(toks[5]);
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                  + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            try {
                Thread.sleep(360);
            } catch (Exception e) {}

            reader.seek(0);
            load = reader.readLine();
            reader.close();

            toks = load.split(" ");

            long idle2 = Long.parseLong(toks[5]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            float cpuUsage = (float)(cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1)); 
            return (int)(cpuUsage*100);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return 0;
    } 
    
    /**
     * HDD 전체 용량을 GB 단위로 반환
     * @return
     */
    public static int getHddTotalMemory(File root) {
    	if(!root.exists()) {
    		root = Environment.getDataDirectory();
    	}
    	StatFs stat = new StatFs(root.getPath());
    	long blockSize = stat.getBlockSize();
    	long availableBlocks = stat.getBlockCount();
    	long totalByte = availableBlocks * blockSize;
    	int totalGB = (int)(totalByte / 1024.0 / 1024.0 / 1024.0);	//GB
    	return totalGB;
    }
    
    /*
     * 하드용량 byte 단위로
     */    
    public static long getTotalInternalMemorySize() {
    	File path = Environment.getDataDirectory();
    	
    	StatFs stat = new StatFs(path.getPath());
    	long blockSize = stat.getBlockSize();
    	long availableBlocks = stat.getBlockCount();
    	long totalByte = availableBlocks * blockSize;
    	
    	return totalByte;
    }
    
    /*
     * 하드용량 byte 단위로 남은용량
     */
    public static long getAvailableInternalMemorySize() {
    	File path = Environment.getDataDirectory();
    	StatFs stat = new StatFs(path.getPath());
    	return (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
    }
    
    /**
     * 사용중인 Main Memory
     * @return 전체 메모리
     */
    @SuppressLint("NewApi")
	public static long getMainMemoryTotal(ActivityManager activityManager) {
    	
    	MemoryInfo memoryInfo = new MemoryInfo();
    	activityManager.getMemoryInfo(memoryInfo);
    	long total = memoryInfo.totalMem;
   
        return total;
    }
    
    @SuppressLint("NewApi")
  	public static long getMainMemoryAvailable(ActivityManager activityManager) {
      	
      	MemoryInfo memoryInfo = new MemoryInfo();
      	activityManager.getMemoryInfo(memoryInfo);
      	long total = memoryInfo.availMem;

        return total;
      }
    
    /**
     * root경로에서 사용가능한 HDD 용량을 GB 단위로 반환
     * @param root : 루트경로(ex. /mnt/sdcard)
     * @return
     */
    public static int getHddFreeMemory(File root) {
    	if(!root.exists()) {
    		root = Environment.getDataDirectory();
    	}
        StatFs stat = new StatFs(root.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        long freeByte = availableBlocks * blockSize;
    	int freeGB = (int)(freeByte / 1024.0 / 1024.0 / 1024.0);	//GB
    	return freeGB;
    }
    
    /**
     * root경로에서 사용가능한 HDD 용량을 byte 단위로 반환
     * @param root : 루트경로(ex. /mnt/sdcard)
     * @return
     */
    public static long getHddFreeMemoryBytes(File root) {
    	if(!root.exists()) {
    		root = Environment.getDataDirectory();
    	}
        StatFs stat = new StatFs(root.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        long freeByte = availableBlocks * blockSize;
    	return freeByte;
    }
    
    /**
     * 사용중인 Main Memory
     * @return 메모리 사용량을 백분율로 반환한다
     */
    public static int getMainMemoryUsage(ActivityManager activityManager) {
    	int usagePer = -1;
    	RandomAccessFile reader = null;
        String load = null;
        try {
            reader = new RandomAccessFile("/proc/meminfo", "r");
            load = reader.readLine();
            Scanner sc = new Scanner(load);
            sc.next();
            long totalMemKB = Long.parseLong(sc.next());
            sc.close();
            
        	MemoryInfo memoryInfo = new MemoryInfo();
        	activityManager.getMemoryInfo(memoryInfo);
        	
        	long availMemKB = (long)(memoryInfo.availMem/1024.0);
        	
        	long usageMemKB = totalMemKB - availMemKB;
        	usagePer = (int)(((double)usageMemKB/totalMemKB) * 100);
        	
        	return usagePer;
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
             try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        return usagePer;
    }

	/**
	 * 외부저장소(mnt/exteranl_sd) 총 공간을 byte 단위로 변환
	 * @param root
	 * @return
	 */
	public static long getTotalExternalMemorySize(File root) {
		if(!root.exists()) {
			return 0;
		}
		StatFs stat = new StatFs(root.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getBlockCount();
		long totalByte = availableBlocks * blockSize;
		return totalByte;
	}

	/**
	 * 외부저장소(mnt/exteranl_sd) 남은 공간 byte 단위로 변환
	 * @param root
	 * @return
	 */
	public static long getAvailableExternalMemorySize(File root) {
		if(!root.exists()) {
			return 0;
		}
		StatFs stat = new StatFs(root.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		long freeByte = availableBlocks * blockSize;
		return freeByte;
	}

	/**
	 * 외부저장소(mnt/exteranl_sd) 사용 공간 byte 단위로 변환
	 * @param root
	 * @return
	 */
	public static long getUsedExternalMemorySize(File root) {
		if(!root.exists()) {
			return 0;
		}
		long freeByte = getTotalExternalMemorySize(root) - getAvailableExternalMemorySize(root);
		return freeByte;
	}


//	public static void rebootSystem(Context context) {
//		try {
////			Process p = Runtime.getRuntime().exec("su");
//			Process p = Runtime.getRuntime().exec("adb reboot");
//
//			OutputStream os = p.getOutputStream();
//			os.write("reboot\n\r".getBytes());
//			os.flush();
//			os.close();
//
//		} catch(IOException e) {
//			Intent intent = new Intent("com.eumtech.action.REBOOT_OR_POWER_SAVE");
//			intent.putExtra("type", 0);
//			intent.putExtra("timer_on", true);
//			intent.putExtra("duration_time", "00:00");
//			context.sendBroadcast(intent);
//
//			Log.e(TAG, e.getMessage());
//			NLog.write(e.getMessage());
//		}
//	}

	public static void rebootSystem(Context context) {
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		pm.reboot(null);
	}
	
	/**
	 * 전원을 끈다
	 */
	public static void powerOffSystem(Context context) {
		Intent intent = new Intent("com.eumtech.action.REBOOT_OR_POWER_SAVE");
		intent.putExtra("type", 1);
		intent.putExtra("timer_on", true);
		intent.putExtra("duration_time", "00:00");
		context.sendBroadcast(intent);
		
		//Process p = Runtime.getRuntime().exec("su");
		//Process p = Runtime.getRuntime().exec("adb reboot -p");
		/*
		OutputStream os = p.getOutputStream();                                       
		os.write("reboot -p\n\r".getBytes());
		os.flush();
		os.close();
		*/
	}
	
	/**
	 * 내일 날짜를 계산하여 반환한다
	 */
	public static Calendar getTomorrow() {
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.add(Calendar.DATE, 1);
		int tomorrowYear = tomorrow.get(Calendar.YEAR);
		int tomorrowMonth = tomorrow.get(Calendar.MONTH);
		int tomorrowDay = tomorrow.get(Calendar.DAY_OF_MONTH);
		tomorrow.set(tomorrowYear, tomorrowMonth, tomorrowDay, 0, 0, 0);
		return tomorrow;
	}
	
	/**
	 * 디바이스 가로크기를 반환한다
	 * @return
	 */
	public static int getLcdSIzeWidth(Activity context) {
		Point p = new Point();
		context.getWindowManager().getDefaultDisplay().getSize(p);
		return p.x;
	}
	 
	/**
	 * 디바이스 세로크기를 반환한다
	 * @return
	 */
	public static int getLcdSIzeHeight(Activity context) {
		Point p = new Point();
		context.getWindowManager().getDefaultDisplay().getSize(p);
		return p.y;
	}
	
	/**
	 * 마운트된 스토리지 리스트를 반환한다
	 */
	public static List<String> getMountedList() {
		List<String> mountedList = new ArrayList<String>();
		List<String> mMounts = readMountsFile();
	    List<String> mVold = readVoldFile();
	    
	    System.out.println("mounts : " + mMounts);
	    System.out.println("mVold : " + mVold);
	    
	    for (int i = 0; i < mMounts.size(); i++) {
			if(mVold.contains(mMounts.get(i))) {
				mountedList.add(mMounts.get(i));
			}
		}
	    
	    if(!mountedList.contains("/mnt/sdcard")) {
	    	mountedList.add(0, "/mnt/sdcard");
	    }
	    
	    return mountedList;
	}
	
	/**
	 * Scan the /proc/mounts file and look for lines like this:
	 * /dev/block/vold/179:1 /mnt/sdcard vfat rw,dirsync,nosuid,nodev,noexec,relatime,uid=1000,gid=1015,fmask=0602,dmask=0602,allow_utime=0020,codepage=cp437,iocharset=iso8859-1,shortname=mixed,utf8,errors=remount-ro 0 0
	 *  
	 * When one is found, split it into its elements
	 * and then pull out the path to the that mount point
	 * and add it to the arraylist
	 */
	private static List<String> readMountsFile() {
	    List<String> mMounts = new ArrayList<String>();
	 
	    try {
	        Scanner scanner = new Scanner(new File("/proc/mounts"));
	         
	        while (scanner.hasNext()) {
	            String line = scanner.nextLine();
	             
	            if (line.startsWith("/dev/block/vold/")) {
	                String[] lineElements = line.split("[ \t]+");
	                String element = lineElements[1];
	                                     
	                mMounts.add(element);
	            }
	        }
	    } catch (Exception e) {
	        // Auto-generated catch block
	        e.printStackTrace();
	    }
	    return mMounts;
	}
	
	/**
	 * Scan the /system/etc/vold.fstab file and look for lines like this:
	 * dev_mount sdcard /mnt/sdcard 1 /devices/platform/s3c-sdhci.0/mmc_host/mmc0
	 * 
	 * When one is found, split it into its elements
	 * and then pull out the path to the that mount point
	 * and add it to the arraylist
	 */
	private static List<String> readVoldFile() {
	     
	    List<String> mVold = new ArrayList<String>();
	     
	    try {
	        Scanner scanner = new Scanner(new File("/system/etc/vold.fstab"));
	         
	        while (scanner.hasNext()) {
	            String line = scanner.nextLine();
	             
	            if (line.startsWith("dev_mount")) {
	                String[] lineElements = line.split("[ \t]+");
	                String element = lineElements[2];
	                 
	                if (element.contains(":")) {
	                    element = element.substring(0, element.indexOf(":"));
	                }
	 
	                mVold.add(element);
	            }
	        }
	    } catch (Exception e) {
	        // Auto-generated catch block
	        e.printStackTrace();
	    }
	     
	    return mVold;
	}
	
	public static float pixelsToSp(Context context, Float px) {
	    float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
	    return px/scaledDensity;
	}
	
	/**
	 * inputstream을 byte 배열로
	 */
	public static byte[] getBytesFromInputStream(InputStream is)
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
	    try {
	        byte[] buffer = new byte[0xFFFF];

	        for (int len; (len = is.read(buffer)) != -1;) {
				os.write(buffer, 0, len);
			}
	        os.flush();
	        return os.toByteArray();
	    }
	    catch (IOException e) {
	        return null;
	    }
	}
	
	/**
	 * 시분을 초로 변환
	 */
	public static int getTimeToSec(int hour, int min)
	{
		int sec = 0;
		sec += hour * 3600;
		sec += min * 60;
		return sec;
	}
	
//	public static String getMacAddress(Activity ctx) {
//		WifiManager mng = (WifiManager)ctx.getSystemService(Activity.WIFI_SERVICE);
//		WifiInfo info = mng.getConnectionInfo();
//		return info.getMacAddress();
//	}
	
	/**
	 * "|" 구분자를 기준으로 문자열을 잘라서 반환
	 * @param data
	 * @return
	 */
	public static ArrayList<String> getSeparationData(String data, byte separator) {
    	byte[] seperator = ByteUtil.byte2ByteBig(separator);
    	ArrayList<String> dataList = new ArrayList<String>();
    	StringTokenizer st = new StringTokenizer(data, new String(seperator));
		while(st.hasMoreTokens()) {
			dataList.add(st.nextToken());
		}
		return dataList;
    }
	
	/**
	 * 
	 * 현재 볼륨값 반환
	 * @param ctx
	 * @return
	 */
	public static int getStreamMusicVolume(Activity ctx) {
    	AudioManager audio = (AudioManager)ctx.getSystemService(Context.AUDIO_SERVICE);
		int volume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
		return volume;
    }
	
	/**
	 * pixel 값을 dp 로 변환
	 * @param ctx
	 * @param pixel
	 * @return
	 */
	public static int pixelToDp(Context ctx, int pixel) {
		return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, ctx.getResources().getDisplayMetrics());
	}
	
	/**
	 * 앱 버전 반환
	 * @param ctx
	 * @return
	 */
	public static String getVersion(Context ctx) {
		PackageInfo pi;
		String version = "";
		try {
			pi = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
			version = pi.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return version;
	}

	/**
	 * 앱 재실행
	 * @param context
	 * @param cls
     */
	public static void restartApplication(Context context, Class<?> cls) {
		Intent i = new Intent(context, cls);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}

	/**
	 * 볼륨 조정
	 * @param context
	 * @param volume
     */
	public static void setVolumeControl(Context context, int volume) {
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		double maxVolumePer = maxVolume * 0.01;
		int transVolume = (int)(volume * maxVolumePer);

		Log.e(TAG, "Volume : " + volume);
		Log.e(TAG, "maxVolume Volume : " + maxVolume);
		Log.e(TAG, "transVolume : " + transVolume);

		if (transVolume > maxVolume) {
			transVolume = maxVolume;
		} else if (transVolume < 0) {
			transVolume = 0;
		}
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, transVolume, AudioManager.FLAG_SHOW_UI);
	}

	/**
	 * 절대경로의 영상 재싱시간을 밀리초로 반환
	 * @param path
	 * @return
     */
	public static long getPlayTime(String path) {
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		retriever.setDataSource(path);
		String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
		long timeInmillisec = Long.parseLong( time );
		return timeInmillisec;
	}

//	public static boolean isRunning(Context context, String packageName) {
//		ActivityManager am = (ActivityManager)context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
//		List<ActivityManager.RunningAppProcessInfo> proceses = am.getRunningAppProcesses();
//
//		for(ActivityManager.RunningAppProcessInfo process : proceses) {
//			if(process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//				if(process.processName.equals(packageName)) {
//					return false;
//				}
//			}
//		}
//		return true;
//	}

	/**
	 * 앱 설치 여부를 반환
	 * @param context : 앱 Context
	 * @param packageName : 패키지명
     * @return : 설치여부
     */
	public static boolean isInstalled(Context context, String packageName) {
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
		if(intent != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 가상키보드 숨기기
	 * @param context : 앱 Context
	 * @param editText : EditText
     */
	public static void hideKeyboard(Context context, EditText editText) {
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	/**
	 * 메시지를 토스트로 출력한다
	 * @param msg
	 */
	public static void showToast(Context context, String msg) {
		Toast tMsg = Toast.makeText(context, msg, Toast.LENGTH_LONG);
		tMsg.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		tMsg.show();
	}

}
