package edu.mit.haoqili.CameraLaunch;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

// thanks to: http://mobile.tutsplus.com/tutorials/android/android-sdk-quick-tip-launching-the-camera/

public class CameraLaunch extends Activity
{
	final static private String TAG = "##### Main Activity";
	final static String photo_path = Environment.getExternalStorageDirectory().getName() + File.separatorChar + "temp_photo.jpg";

	// for scaling image
	static final int targetBytes = 65000;
	static final int targetShortSide = 400;
	static final int targetLongSide = 480;
	
	private static final int CAMERA_PIC_REQUEST = 1;
	
	Button camera_button;

	public void logm(String line){
    	Log.i(TAG, line);
    }
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        camera_button = (Button) findViewById(R.id.camera_button);
        camera_button.setOnClickListener(camera_button_listener);
    }
    private OnClickListener camera_button_listener = new OnClickListener(){
    	public void onClick(View v){
    		logm("#################");
    		logm("clicked Camera button.");
    		
    		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
    		
    		// credit: http://stackoverflow.com/questions/1910608/android-action-image-capture-intent
            File _photoFile = new File(photo_path);
            try {
                if(_photoFile.exists() == false) {
                    _photoFile.getParentFile().mkdirs();
                    _photoFile.createNewFile();
                }
            } catch (IOException e) {
                Log.e(TAG, "Could not create file.", e);
            }
            Log.i(TAG + " photo path: ", photo_path);

            Uri _fileUri = Uri.fromFile(_photoFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, _fileUri);
    		// start the Intent:
    		startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
    	}
    };
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_PIC_REQUEST) {
            logm("Camera onActivityResult!");
            if (resultCode == Activity.RESULT_OK) {
            	logm("Display on screen:");
                ImageView image = (ImageView) findViewById(R.id.photoResultView);
            	
                /*BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap bitmap = BitmapFactory.decodeFile(photo_path, options);
                */
                Bitmap bitmap = getAndResizeBitmap();
                image.setImageBitmap(bitmap);
                logm("GETANDRESIZE BITMAP SIZE: " + bitmapBytes(bitmap));
                
                // To scale image to a target byte size
                Bitmap resized = scaleToTargetSize(bitmap);
                ImageView image2 = (ImageView) findViewById(R.id.photoResized);
                image2.setImageBitmap(resized);
                logm("Resized SIZE: " + bitmapBytes(resized));
            } else {
            	logm("Taking picture failed. Try again!");
            }
        }
    }
    
    protected Bitmap getAndResizeBitmap(){
        BitmapFactory.Options options =new BitmapFactory.Options();
        // first we don't produce an actual bitmap, but just probe its dimensions
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(photo_path, options);
        int h, w;
        if (options.outHeight > options.outWidth){
        	h = (int) Math.ceil(options.outHeight/(float) targetShortSide);
        	w = (int) Math.ceil(options.outWidth/(float) targetLongSide);
        } else {
        	w = (int) Math.ceil(options.outHeight/(float) targetShortSide);
        	h = (int) Math.ceil(options.outWidth/(float) targetLongSide);
        }
        if(h>1 || w>1){
        	options.inSampleSize = (h > w) ? h : w;
        }
        // now we actually produce the bitmap, resized
        options.inJustDecodeBounds=false;
        bitmap =BitmapFactory.decodeFile(photo_path, options);
        logm("Options height x width: " + options.outHeight + " x " + options.outWidth);
        return bitmap;
    }

    protected int bitmapBytes(Bitmap bitmap){
    	return bitmap.getRowBytes()*bitmap.getHeight();
    }
   
    protected Bitmap scaleToTargetSize(Bitmap thumbnail){
    	// print debugging information:
    	logm("orig total bytes: " + thumbnail.getRowBytes()*thumbnail.getHeight()); // 76800 or 38400 it varies
        // concerned about small size? 
        // see: http://stackoverflow.com/questions/1910608/android-action-image-capture-intent
        logm("orig width: " + thumbnail.getWidth()); //160
        logm("orig height: " + thumbnail.getHeight()); //120 
        
        //resizing:
    	int givenBytes = thumbnail.getRowBytes()*thumbnail.getHeight();
    	int givenPixels = thumbnail.getHeight()*thumbnail.getWidth();
    	int bytes_p_pixel = givenBytes/givenPixels;
    	
    	int targetPixels = (int) (targetBytes/(1.0*bytes_p_pixel));
    	double factor = Math.sqrt(targetPixels/(1.0*givenPixels));
    	
    	int newWidth = (int)(thumbnail.getWidth()*factor);
    	int newHeight = (int)(thumbnail.getHeight()*factor);
    	logm("new width: " + newWidth);
    	logm("new height: " + newHeight);
    	logm("new total bytes: " + newWidth*newHeight*bytes_p_pixel);
    	return Bitmap.createScaledBitmap(thumbnail, newWidth, newHeight, true);
    }
}
