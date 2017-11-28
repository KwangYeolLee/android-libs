package com.rayworld.androidlibs;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

/**
 * Created by 이광열 on 2016-07-05.
 */
public class BarCodeUtil {

    /**
     * QR CODE 생성
     * @param data : QR CODE 생성 데이터
     * @param width : 가로크기
     * @param height : 세로크기
     * @return : QR CODE 생성된 비트맵 이미지
     */
    public static Bitmap createQrCode(String data, int width, int height) {
        MultiFormatWriter gen = new MultiFormatWriter();
        Bitmap bitmap = null;

        try {
            BitMatrix byteMap = gen.encode(data, BarcodeFormat.QR_CODE, width, height);
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            for (int i = 0 ; i < width ; ++i) {
                for (int j = 0 ; j < height ; ++j) {
                    bitmap.setPixel(i, j, byteMap.get(i,j) ? Color.BLACK : Color.argb(200, 255, 255, 255));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getRoundedCornerBitmap(bitmap, 20);
    }

    /**
     * 비트맵 이미지 라운드 처리
     * @param bitmap : 비트맵 이미지
     * @param pixels : 라운드 크기
     * @return : 비트맵
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
