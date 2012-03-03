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
            logm("image total bytes: " + thumbnail.getRowBytes()*thumbnail.getHeight()); // 76800
            // concerned about small size? 
            // see: http://stackoverflow.com/questions/1910608/android-action-image-capture-intent
            logm("image width: " + thumbnail.getWidth()); //160
            logm("image height: " + thumbnail.getHeight()); //120
            ImageView image = (ImageView) findViewById(R.id.photoResultView);
            image.setImageBitmap(thumbnail);
        }
    }
}
