package app.vitamiin.com.common;

import java.io.InputStream;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class BitmapUtils {

	public static Bitmap getRoundedTopCornersBitmap(Context context,
			Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = convertDipsToPixels(context, pixels);

		// final Rect topRightRect = new Rect(bitmap.getWidth() / 2, 0,
		// bitmap.getWidth(), bitmap.getHeight() / 2);
		final Rect bottomRect = new Rect(0, bitmap.getHeight() / 2,
				bitmap.getWidth(), bitmap.getHeight());

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		// Fill in upper right corner
		// canvas.drawRect(topRightRect, paint);
		// Fill in bottom corners
		canvas.drawRect(bottomRect, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public static Bitmap getRoundedBottomCornersBitmap(Context context,
			Bitmap bitmap, int pixels) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = convertDipsToPixels(context, pixels);

		final Rect topRect = new Rect(0, 0, bitmap.getWidth(),
				bitmap.getHeight() / 2);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		canvas.drawRect(topRect, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public static Bitmap getRoundedCornerBitmap(Context context, Bitmap bitmap,
			int roundDips) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = convertDipsToPixels(context, roundDips);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * Bitmap 을 만들어 리턴한다.
	 */
	public static Bitmap create_image(Context context, int id) {

		Bitmap bitmap = null;

		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPurgeable = true; // declare as purgeable to disk
			bitmap = BitmapFactory.decodeResource(context.getResources(), id,
					options);
		} catch (RuntimeException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	public static int convertDipsToPixels(Context context, int dips) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dips * scale + 0.5f);
	}

	public static Bitmap LoadRoundedImageFromWebUrl(Context context,
			String strUrl, int sample) {

		try {

			BitmapFactory.Options option = new BitmapFactory.Options();
			option.inSampleSize = sample;
			option.inPurgeable = true;
			option.inDither = true;

			URL url = new URL(strUrl);
			HttpGet httpRequest = null;

			httpRequest = new HttpGet(url.toURI());

			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = (HttpResponse) httpclient
					.execute(httpRequest);

			HttpEntity entity = response.getEntity();
			BufferedHttpEntity b_entity = new BufferedHttpEntity(entity);
			InputStream input = b_entity.getContent();

			Bitmap bitmap = BitmapFactory.decodeStream(input, null, option);

			if (input != null)
				input.close();

			return bitmap;
		} catch (Exception e) {
			return null;
		}
	}
}
