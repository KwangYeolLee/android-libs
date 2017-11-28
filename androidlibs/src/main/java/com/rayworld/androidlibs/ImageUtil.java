package com.rayworld.androidlibs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Locale;

public class ImageUtil {
	private static final String JPEG = "jpeg";
	private static final String JPG = "jpg";
	private static final String PNG = "png";
	private static final String GIF = "gif";
	private static final String[] EXT_LIST = {JPEG, JPG, PNG, GIF};

	public static boolean isImage(String ext) {
		ext = ext.toLowerCase(Locale.getDefault());
		for (String aEXT_LIST : EXT_LIST) {
			if (ext.equals(aEXT_LIST)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * uri 의 실제 로컬 저장경로 반환
	 * @param ctx : context
	 * @param uri : url
     * @return : 로컬 저장경로
     */
	public static String getPath(Context ctx, Uri uri) {
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		
		Cursor cursor = ctx.getContentResolver().query(uri, filePathColumn, null, null, null);
		cursor.moveToFirst();

		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String filePath = cursor.getString(columnIndex);
		cursor.close();
		return filePath;
	}

	/**
	 * 로컬 이미지의 가로크기 반환
	 * @param fileName : 로컬파일 절대경로
	 * @return : 가로크기(px)
	 */
	public static int getBitmapOfWidth( String fileName ){
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(fileName, options);
			return options.outWidth;
		} catch(Exception e) {
			return 0;
		}
	}

	/**
	 * 로컬 이미지의 세로크기 반환
	 * @param fileName : 로컬파일 절대경로
	 * @return : 세로크기(px)
     */
	public static int getBitmapOfHeight( String fileName ){

		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(fileName, options);

			return options.outHeight;
		} catch(Exception e) {
			return 0;
		}
	}
	
	/**
	 * 
	 * @param root : SD CARD 경로
	 * @param srcFile : 이미지 저장경로 
	 * @param width : 이미지 가로크기
	 * @param height : 이미지 세로크기
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public static String resizeImage(File root, File srcFile, int width, int height) {
		File tmFile = new File(root, srcFile.getName());
		String fileName = srcFile.getName();
		int pos = fileName.lastIndexOf( "." );
		String ext = fileName.substring( pos + 1 );
		int imgRatio = 1;
		
		while(true) {
			if(width>1920 || height>1080) {
				width /= 2;
				height /= 2;
				imgRatio++;
			} else  {
				break;
			}
		}
		if(imgRatio>1 && (imgRatio%2)==1) {	//비율이 홀수인 경우 1을 더한다.(inSampleSize 홀수 X)
			imgRatio++;
		}
		
		Log.d("ImageUtil", "width : " + width);
		Log.d("ImageUtil", "height : " + height);
		Log.d("ImageUtil", "imgRatio : " + imgRatio);

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = imgRatio;
		Bitmap uploadBitmap = BitmapFactory.decodeFile(srcFile.getAbsolutePath(), options);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(tmFile);
			if(ext.toLowerCase().equals("png")) {
				uploadBitmap.compress(CompressFormat.PNG, 100, out);
			} else  {
				uploadBitmap.compress(CompressFormat.JPEG, 100, out);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            try {
            	out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
		return tmFile.getAbsolutePath();
	}

	public static Bitmap resizeImage(Bitmap bitmap, float aspectRatio) {
		int width = (int)(bitmap.getWidth() * aspectRatio);
		int height = (int)(bitmap.getHeight() * aspectRatio);

		return Bitmap.createScaledBitmap(bitmap, width, height, false);
	}


	/**
	 * 비트맵을 바이트 배열로 변환
	 * @param bitmap : 비트맵 소스
	 * @param format : 압축포멧(PNG, JPEG)
	 * @param quality : 이미지 품질(0~100)
	 * @return : 비트맵 바이트 배열
     */
	public static byte[] bitmapToByteArray( Bitmap bitmap, Bitmap.CompressFormat format, int quality ) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
		bitmap.compress( format, quality, stream) ;
		return stream.toByteArray();
	}

	/**
	 * 스크린샷 이미지를 Bitmap 으로 반환
	 * @param window : 화면(Activity)
	 * @param aspectRatio : 이미지 비율(0.0 ~ 1.0)
     * @return : Bitmap 이미지
     */
	public static Bitmap takeScreenshot(Window window, float aspectRatio) {
		Bitmap bitmap = null;
		try {
			View v1 = window.getDecorView().getRootView();
			v1.setDrawingCacheEnabled(true);
			Bitmap tm = v1.getDrawingCache();
			int width = (int)(tm.getWidth() * aspectRatio);
			int height = (int)(tm.getHeight() * aspectRatio);

			bitmap = Bitmap.createScaledBitmap(tm, width, height, false);
			v1.setDrawingCacheEnabled(false);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 기본 갤러리 앱을 통해 이미지를 오픈한다.
	 * @param context : Application Context
	 * @param imageFile : 이미지 파일
     */
	public static void openScreenshot(Context context, File imageFile) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		Uri uri = Uri.fromFile(imageFile);
		intent.setDataAndType(uri, "image/*");
		context.startActivity(intent);
	}

	public static Bitmap viewToBitmap(View view) {
		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
//		Canvas canvas = new Canvas(bitmap);
		Canvas canvas =  null;
		if (view instanceof SurfaceView) {
			canvas = ((SurfaceView) view).getHolder().lockCanvas();
			SurfaceView surfaceView = (SurfaceView) view;
			surfaceView.setZOrderOnTop(true);
			surfaceView.draw(canvas);
			surfaceView.setZOrderOnTop(false);
			((SurfaceView) view).getHolder().unlockCanvasAndPost(canvas);
			return bitmap;
		} else {
			view.draw(canvas);
			return bitmap;
		}
	}

}
