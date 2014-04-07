package sc.vision.cv4android1;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.hardware.Camera;
import java.io.IOException;
import android.util.Log;


public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	
	private SurfaceHolder previewHolder;    //declare a interface reference, then can access the members by the interface reference
	private Camera myCamera;

	
	public CameraPreview(Context context, Camera camera){
		super(context);
		myCamera = camera;
		
		previewHolder = getHolder();     //getHolder() is a member function of SurfaceView class
		previewHolder.addCallback(this);  //parameter here is a callbackinterface which is itself in this case
	        
                //depractaed setting, but required on Android version prior to 3.0	
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		if (previewHolder.getSurface() == null){
			return;
		  }
      
		try{
		   myCamera.stopPreview();	
		} catch (Exception e) {
		    //ignore for this time being	
		 }
		
		//need to fill some more here
		
                //start preview with new settings
		try{
		   myCamera.setPreviewDisplay(previewHolder);
		   myCamera.startPreview();
		} catch (Exception e){
			 Log.d(MainActivity.TAG, "Error starting camera preview: " + e.getMessage());
		  }
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
        try{
        	myCamera.setPreviewDisplay(holder);
        	myCamera.startPreview();
          }
        catch(IOException e) {
        	Log.d(MainActivity.TAG, "Error setting camera preview: " + e.getMessage());
        }
        
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
        /* Tacke care of releasing the Camera Preview in your activity*/
	}

}
