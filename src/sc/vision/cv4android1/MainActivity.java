package sc.vision.cv4android1;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.widget.Button;
import android.content.Intent;

public class MainActivity extends Activity {
	public static final int MEDIA_TYPE_IMAGE = 1;       //Numeric constant form media type
	public static final String TAG = "CV4Android1";     //TAG for the output of log file

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//add listener to the button
		addListenerToButton();
		
	}
	
	public void addListenerToButton(){
		
		final Button takeVideo = (Button) findViewById(R.id.take_video);
		final Button takeImage = (Button) findViewById(R.id.take_image);
		
		takeVideo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				 /* when you are writing in the parentheses of setOnClickListener,
				  *  you have to use FromActivity. to qualify the this*/
				 Intent videoIntent = new Intent(MainActivity.this, CameraActivity.class);   
                 startActivity(videoIntent); 
			  }			
		   });
		
		takeImage.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 Intent videoIntent = new Intent(MainActivity.this, CameraActivity.class);   
                 startActivity(videoIntent); 
			}
			
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean checkCameraHardware(Context context){
		if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
			return true;
		  }
		else {
			return false;
		}
	}
	
	public static Camera getCameraInstance(){
		Camera c = null;
		try {
			 c = Camera.open();
		  }
		catch (Exception e){
			
		  }
		return c;
	}
	
	
	
}
