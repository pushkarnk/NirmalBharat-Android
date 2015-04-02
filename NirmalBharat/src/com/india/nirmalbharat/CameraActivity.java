package com.india.nirmalbharat;


import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import com.india.nirmalbharat.util.AppContext;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

public class CameraActivity extends Activity {
	Preview preview;
	Camera camera;
	Context context;
	static double latestLatitude;
	static double latestLongitude;
	
	private LocationManager locationManager=null;  
	private LocationListener locationListener=null;
	AtomicBoolean clicked = null;
	
	Toast locListenerToast;
	
	@SuppressLint("RtlHardcoded")
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);		
		context = this;
		clicked = new AtomicBoolean();
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_camera);
		
		locListenerToast = Toast.makeText(context, "*GPS*", Toast.LENGTH_LONG);
		locListenerToast.setGravity(Gravity.TOP | Gravity.RIGHT, 0, 0);
			
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);		
		locationListener = new MyLocationListener();
		
		requestGPSUpdate();
		
		preview = new Preview(this, (SurfaceView)findViewById(R.id.surfaceView));
		preview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		((FrameLayout) findViewById(R.id.layout)).addView(preview);
		preview.setKeepScreenOn(true);
		preview.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(clicked.compareAndSet(false, true))
					camera.takePicture(shutterCallback, rawCallback, jpegCallback);
			}
		});

		Toast.makeText(context, getString(R.string.take_photo_help), Toast.LENGTH_LONG).show();

	}
	
	
	private boolean checkGPSDevice() {  
		  ContentResolver contentResolver = getBaseContext().getContentResolver();  
		  return Settings.Secure.isLocationProviderEnabled(contentResolver,LocationManager.GPS_PROVIDER);  		  
	}  
	
	private void setCameraDisplayOrientation(Activity activity,
	         int cameraId, android.hardware.Camera camera) {
	     android.hardware.Camera.CameraInfo info =
	             new android.hardware.Camera.CameraInfo();
	     android.hardware.Camera.getCameraInfo(cameraId, info);
	     int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
	     int degrees = 0;
	     switch (rotation) {
	         case Surface.ROTATION_0: degrees = 0; break;
	         case Surface.ROTATION_90: degrees = 90; break;
	         case Surface.ROTATION_180: degrees = 180; break;
	         case Surface.ROTATION_270: degrees = 270; break;
	     }

	     int result;
	     if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
	         result = (info.orientation + degrees) % 360;
	         result = (360 - result) % 360;
	     } else {  
	         result = (info.orientation - degrees + 360) % 360;
	     }
	     camera.setDisplayOrientation(result);
	 }

	private void requestGPSUpdate() {
		if(checkGPSDevice()) {					
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0,locationListener);
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0,locationListener);
			locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 100, 0,locationListener);
			
		}				
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		int numCams = Camera.getNumberOfCameras();
		if(numCams > 0){
			try{
				camera = Camera.open(0);				
				camera.startPreview();
				setCameraDisplayOrientation(this, CameraInfo.CAMERA_FACING_BACK, camera);
				requestGPSUpdate();
				Parameters parameters = camera.getParameters();				
				parameters.setPictureFormat(PixelFormat.JPEG);
				parameters.setJpegQuality(100);
				parameters.setPictureSize(1280,768);
				camera.setParameters(parameters);
				preview.setCamera(camera);
			} catch (RuntimeException ex){
				Toast.makeText(context, getString(R.string.camera_not_found), Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	protected void onPause() {
		if(camera != null) {
			camera.stopPreview();
			preview.setCamera(null);
			camera.release();
			camera = null;
		}
		super.onPause();
	}

	private void resetCam() {
		camera.startPreview();
		preview.setCamera(camera);
	}

	private void refreshGallery(File file) {
		Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		mediaScanIntent.setData(Uri.fromFile(file));
		sendBroadcast(mediaScanIntent);
	}

	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {}
	};

	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {}
	};

	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			preview.setOnClickListener(null);
			new SaveImageTask().execute(data);							
			resetCam();			 
		}
	};
	
	

	private class SaveImageTask extends AsyncTask<byte[], Void, Void> {
		@SuppressLint("DefaultLocale")
		@Override
		protected Void doInBackground(byte[]... data) {
			FileOutputStream outStream = null;
			try {
				File sdCard = Environment.getExternalStorageDirectory();
				File dir = new File (sdCard.getAbsolutePath() + "/NirmalBharat");
				dir.mkdirs();								
				String fileName = String.format("%d.jpg", System.currentTimeMillis());				
				File outFile = new File(dir, fileName);
				AppContext.fileURL = outFile.getAbsolutePath();

				outStream = new FileOutputStream(outFile);
				outStream.write(data[0]);
				outStream.flush();			
				outStream.close();
				refreshGallery(outFile);

				Bitmap bmp = BitmapFactory.decodeFile(outFile.getAbsolutePath());
				Matrix matrix = new Matrix();
			    matrix.postRotate(90);
			    bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
			    
			    outStream = new FileOutputStream(outFile);
			    bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
			    outStream.flush();
			    outStream.close();
			    
			} catch (Exception e) {
				e.printStackTrace();
			} finally {}
			asyncExit();
			return null;
		}
		
		private void asyncExit() {
			if(camera != null) {				
				camera.stopPreview();
				preview.setCamera(null);
				camera.release();				
				camera = null;
			}		
			AppContext.latitude = Double.toString(latestLatitude);
			AppContext.longitude = Double.toString(latestLongitude);			
			Intent homeIntent = new Intent(getApplicationContext(),DisplayActivity.class);
			homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(homeIntent);		
			locListenerToast.cancel();
		}
	}

	
	class MyLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			locListenerToast.show();
			CameraActivity.latestLatitude = location.getLatitude();
			CameraActivity.latestLongitude = location.getLongitude();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}

		@Override
		public void onProviderEnabled(String provider) {}

		@Override
		public void onProviderDisabled(String provider) {}
	}
	
}




