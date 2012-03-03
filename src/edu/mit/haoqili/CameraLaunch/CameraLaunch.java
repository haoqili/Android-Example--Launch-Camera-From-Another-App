package edu.mit.haoqili.CameraLaunch;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

// thanks to: http://mobile.tutsplus.com/tutorials/android/android-sdk-quick-tip-launching-the-camera/

public class CameraLaunch extends Activity
{
	final static private String TAG = "##### Main Activity";

	static final int targetBytes = 65000; // for scaling image
	
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
    		logm("clicked Camera button");
    		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
    		// start the Intent:
    		startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
    	}
    };
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_PIC_REQUEST) {
            logm("Camera Handling results!");
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            ImageView image = (ImageView) findViewById(R.id.photoResultView);
            image.setImageBitmap(thumbnail);
            
            // To scale image to a target byte size
            Bitmap resized = scaleToTargetSize(thumbnail);
            ImageView image2 = (ImageView) findViewById(R.id.photoResized);
            image2.setImageBitmap(resized);
        }
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
