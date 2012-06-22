package br.com.condesales.tasks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import br.com.condesales.listeners.ImageRequestListener;

public class UserImageRequest extends AsyncTask<String, Integer, Bitmap> {

	private ImageRequestListener mListener;
	private Activity mActivity;

	public UserImageRequest(Activity activity, ImageRequestListener listener) {
		mListener = listener;
		mActivity = activity;
	}

	@Override
	protected Bitmap doInBackground(String... params) {

		String userPhoto = params[0];
		Bitmap bmp = null;
		try {
			URL url = new URL(userPhoto); // you can write here any link
			/* Open a connection to that URL. */
			URLConnection ucon = url.openConnection();
			/*
			 * Define InputStreams to read from the URLConnection.
			 */
			InputStream is = ucon.getInputStream();

			bmp = BitmapFactory.decodeStream(is);
		} catch (Exception e) {
			Log.e("Getting user image", e.toString());
			mListener.onError(e.toString());
		}
		return bmp;
	}

	@Override
	protected void onPostExecute(Bitmap bmp) {
		saveFileInCache("foursquareUser", bmp);
		mListener.onImageFetched(bmp);
		super.onPostExecute(bmp);
	}

	/**
	 * Saves the file into device cache memory.
	 * 
	 * @param fileName
	 *            the name of the file to be saved
	 * @param bitmap
	 *            the bitmap to be saved
	 */
	private void saveFileInCache(String fileName, Bitmap bitmap) {
		String path = mActivity.getCacheDir().toString();
		OutputStream fOut = null;
		File file = new File(path, fileName + ".jpg");
		try {
			fOut = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			mListener.onError(e.toString());
		}
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
		try {
			fOut.flush();
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
			mListener.onError(e.toString());
		}
	}
}
