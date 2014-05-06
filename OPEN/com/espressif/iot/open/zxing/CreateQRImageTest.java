package com.espressif.iot.open.zxing;

import java.util.Hashtable;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
/**
 */
public class CreateQRImageTest
{
	/**
	 * Singleton Pattern
	 */
	private static CreateQRImageTest instance = new CreateQRImageTest();
	private CreateQRImageTest(){
	}
	public static CreateQRImageTest getInstance(){
		return instance;
	}
//	private ImageView sweepIV;
	private int QR_WIDTH = 200, QR_HEIGHT = 200;
	/**
	 * @return void
	 * @throws
	 */
	
	// the url need transforming, both English and Chinese are OK
	public Bitmap createQRImage(String url)
	{
		try
		{
			// check the legality of the url
			/*
			if (url == null || "".equals(url) || url.length() < 1)
			{
				return;
			}
			*/
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			// picture-data transform, the matrix is used
			BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			// get the Qr code according the Qr Algorithm
			// 2 for clause means horizontal and vertical
			for (int y = 0; y < QR_HEIGHT; y++)
			{
				for (int x = 0; x < QR_WIDTH; x++)
				{
					if (bitMatrix.get(x, y))
					{
						pixels[y * QR_WIDTH + x] = 0xff000000;
					}
					else
					{
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}
				}
			}
			// generating Qr code using ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
			// display the Qr picture to ImageView
//			sweepIV.setImageBitmap(bitmap);
			return bitmap;
		}
		catch (WriterException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
