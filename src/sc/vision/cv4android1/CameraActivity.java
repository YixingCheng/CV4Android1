package sc.vision.cv4android1;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;



import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.view.Display;
import android.graphics.Point;

public class CameraActivity extends Activity implements CvCameraViewListener2, OnTouchListener{
	
	private Camera myCamera;
	//private CameraPreview myPreview;
	public Display display;
	public Point size;
	public static int screen_width;
	public static int screen_height;
	
	private static final int       VIEW_MODE_RGBA     = 0;
    private static final int       VIEW_MODE_GRAY     = 1;
    private static final int       VIEW_MODE_CANNY    = 2;
    private static final int       VIEW_MODE_FEATURES = 5;
    
    private MenuItem               ItemPreviewRGBA;
    private MenuItem               ItemPreviewGray;
    private MenuItem               ItemPreviewCanny;
    private MenuItem               ItemPreviewFeatures;
    private List<Size>             mResolutionList;
    private MenuItem[]             mEffectMenuItems;
    private SubMenu                mColorEffectsMenu;
    private MenuItem[]             mResolutionMenuItems;
    private SubMenu                mResolutionMenu;

    private int                    mViewMode;
    private Mat                    mRgba;
    private Mat                    mIntermediateMat;
    private Mat                    mGray;
    
    private cvCameraPreview      cvPreviewInst1;
	//private CameraBridgeViewBase cvCameraPreview;
    //private boolean              IsJavaCamera = true;
    //private MenuItem             ItemSwitchCamera = null;
    
    private BaseLoaderCallback cvLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(MainActivity.TAG, "OpenCV loaded successfully");
                    
                    try{
                    	System.loadLibrary("cv4android1_native");     //load native library
                    }catch (UnsatisfiedLinkError e) {
                    	 Log.i(MainActivity.TAG, "Library load failed" + e.getMessage());
                      }
                    
                    cvPreviewInst1.enableView();
                    cvPreviewInst1.setOnTouchListener(CameraActivity.this);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public CameraActivity() {
        Log.i(MainActivity.TAG, "Instantiated new " + this.getClass());
    }
    
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		Log.d(MainActivity.TAG, "called onCreate");
		
		super.onCreate(savedInstanceState);
		
		display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		screen_width = size.x; screen_height = size.y;
		
		Log.d(MainActivity.TAG, "screen size is: " + size.x + " * " + size.y);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);      //let the preview window all always on
		setContentView(R.layout.activity_camera);
		
		//Log.d(MainActivity.TAG, "ContentView set");
		
	/*********************************************************************************************************
	*	if (IsJavaCamera){                                                                                   *
	*		//cvCameraPreview.setMaxFrameSize(width, height);                                                *
	*		//Log.d(MainActivity.TAG, "in if");                                                              *
	*		cvPreviewInst1 = (cvCameraPreview) findViewById(R.id.tutorial3_activity_java_surface_view);      *
	*		//Log.d(MainActivity.TAG, "after cast");                                                         *
	*	  }                                                                                                  *
    *    else {                                                                                              *
    *    	//cvCameraPreview.setMaxFrameSize(width, height);                                                *
    *    	cvPreviewInst1 = (cvCameraPreview) findViewById(R.id.tutorial1_activity_native_surface_view);    *
	*	                                                                                                     *
    *      }                                                                                                 *
	*********************************************************************************************************/
		
		cvPreviewInst1 = (cvCameraPreview) findViewById(R.id.tutorial3_activity_java_surface_view);
		
		//Log.d(MainActivity.TAG, "debug");
		cvPreviewInst1.setVisibility(SurfaceView.VISIBLE);
		
		cvPreviewInst1.setCvCameraViewListener(this);

		//Log.d(MainActivity.TAG, "debug2");
		//myCamera = MainActivity.getCameraInstance();
		//Log.d(MainActivity.TAG, "CV4Android1 gets Camera!");
		
		//myPreview = new CameraPreview(this, myCamera);
		//FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		//preview.addView(myPreview);
		                                                                      // Show the Up button in the action bar.
       	//setupActionBar();
       	//Log.d(MainActivity.TAG, "debug3");
       	
	}

	@Override
    public void onPause()
    {
        super.onPause();
        if (cvPreviewInst1 != null)
        	cvPreviewInst1.disableView();
    }
	
	@Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, cvLoaderCallback);
    }
	
	@Override
	public void onDestroy() {
        super.onDestroy();
        if (cvPreviewInst1 != null)
        	cvPreviewInst1.disableView();
    }
	
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			//Log.d(MainActivity.TAG, "debug4");
			getActionBar().hide();                         //disable the Action Bar
			//Log.d(MainActivity.TAG, "debug5");
			getActionBar().setDisplayHomeAsUpEnabled(false);
		}
	}  
       // Since this activity will be used to hold camera preview,
       // so we don't need the action bar
 
	@Override
     	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.camera, menu);
		     Log.i(MainActivity.TAG, "called onCreateOptionsMenu");
		     
		     List<String> effects = cvPreviewInst1.getEffectList();
		     
		     if (effects == null) {
		            Log.e(MainActivity.TAG, "Color effects are not supported by device!");
		            return true;
		        }
		     
		     mColorEffectsMenu = menu.addSubMenu("Color Effect");
		     mEffectMenuItems = new MenuItem[effects.size()];
		     
		     int idx = 0;
		     ListIterator<String> effectItr = effects.listIterator();
		     while(effectItr.hasNext()) {
		        String element = effectItr.next();
		        mEffectMenuItems[idx] = mColorEffectsMenu.add(1, idx, Menu.NONE, element);
		        idx++;
		      }
		     
		     mResolutionMenu = menu.addSubMenu("Resolution");
		     mResolutionList = cvPreviewInst1.getResolutionList();
		     mResolutionMenuItems = new MenuItem[mResolutionList.size()];
		     
		     ListIterator<Size> resolutionItr = mResolutionList.listIterator();
		     idx = 0;
		       while(resolutionItr.hasNext()) {
		          Size element = resolutionItr.next();
		           mResolutionMenuItems[idx] = mResolutionMenu.add(2, idx, Menu.NONE,
		                    Integer.valueOf(element.width).toString() + "x" + Integer.valueOf(element.height).toString());
		          idx++;
		      }
		     
             //ItemSwitchCamera = menu.add("Toggle Native/Java camera");
             ItemPreviewRGBA = menu.add("Preview RGBA");
             ItemPreviewGray = menu.add("Preview GRAY");
             ItemPreviewCanny = menu.add("Canny");
             ItemPreviewFeatures = menu.add("Find features");
		
		   return true;
	   }    

	@Override
     	public boolean onOptionsItemSelected(MenuItem item) {
		    
		    String toastMesage = new String();
            Log.i(MainActivity.TAG, "called onOptionsItemSelected; selected item: " + item);
            
          /*  if ( item == ItemSwitchCamera ){
            	cvPreviewInst1.setVisibility(SurfaceView.GONE);
                IsJavaCamera = !IsJavaCamera;
                
                if (IsJavaCamera) {
                	cvPreviewInst1 = (sc.vision.cv4android1.cvCameraPreview) findViewById(R.id.tutorial1_activity_java_surface_view);
                    toastMesage = "Java Camera";
                  } else {
                	cvPreviewInst1 = (sc.vision.cv4android1.cvCameraPreview) findViewById(R.id.tutorial1_activity_native_surface_view);
                    toastMesage = "Native Camera";
                  }
            	
                cvPreviewInst1.setVisibility(SurfaceView.VISIBLE);
                cvPreviewInst1.setCvCameraViewListener(this);
                cvPreviewInst1.enableView();
                Toast toast = Toast.makeText(this, toastMesage, Toast.LENGTH_LONG);
                toast.show();    
               }   */
             if (item == ItemPreviewRGBA){
            	mViewMode = VIEW_MODE_RGBA;
            	toastMesage = "RGB MODE";
            	Toast toast = Toast.makeText(this, toastMesage, Toast.LENGTH_LONG);
                toast.show();
               }
            else if (item == ItemPreviewGray){
            	mViewMode = VIEW_MODE_GRAY;
            	toastMesage = "GREY MODE";
            	Toast toast = Toast.makeText(this, toastMesage, Toast.LENGTH_LONG);
                toast.show();
               }
            else if (item == ItemPreviewCanny){
            	 mViewMode = VIEW_MODE_CANNY;
            	 toastMesage = "CANNY MODE";
             	 Toast toast = Toast.makeText(this, toastMesage, Toast.LENGTH_LONG);
                 toast.show();
               }
            else if (item == ItemPreviewFeatures){
            	mViewMode = VIEW_MODE_FEATURES;
            	toastMesage = "FEATURES MODE";
            	Toast toast = Toast.makeText(this, toastMesage, Toast.LENGTH_LONG);
                toast.show();
               }  
             if ( item.getGroupId() == 1){
            	cvPreviewInst1.setEffect((String) item.getTitle());
                Toast.makeText(this, cvPreviewInst1.getEffect(), Toast.LENGTH_SHORT).show();
               }
            else if ( item.getGroupId() == 2){
            	int id = item.getItemId();
                Size resolution = mResolutionList.get(id);
                cvPreviewInst1.setResolution(resolution);
                resolution = cvPreviewInst1.getResolution();
                String caption = Integer.valueOf(resolution.width).toString() + "x" + Integer.valueOf(resolution.height).toString();
                Toast.makeText(this, caption, Toast.LENGTH_SHORT).show();
               }
          
            
	//	switch (item.getItemId()) {
	//	case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
	//		NavUtils.navigateUpFromSameTask(this);
	//		return true;
	//	}
	//	return super.onOptionsItemSelected(item);
	    
	    return true;
	}

	@Override
	public void onCameraViewStarted(int width, int height) {
		// TODO Auto-generated method stub
		mRgba = new Mat(height, width, CvType.CV_8UC4);
        mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);
	}

	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub
		mRgba.release();
        mGray.release();
        mIntermediateMat.release();
	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		// TODO Auto-generated method stub
		final int viewMode = mViewMode;
		switch (viewMode) {
        case VIEW_MODE_GRAY:
            // input frame has gray scale format
            Imgproc.cvtColor(inputFrame.gray(), mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
            break;
        case VIEW_MODE_RGBA:
            // input frame has RBGA format
            mRgba = inputFrame.rgba();
            break;
        case VIEW_MODE_CANNY:
            // input frame has gray scale format
            mRgba = inputFrame.rgba();
            Imgproc.Canny(inputFrame.gray(), mIntermediateMat, 80, 100);
            Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
            break;
        case VIEW_MODE_FEATURES:
            // input frame has RGBA format
        	//Log.d(MainActivity.TAG, "debug1");
            mRgba = inputFrame.rgba();
            mGray = inputFrame.gray();
            //Log.d(MainActivity.TAG, "debug2");
            FindFeatures(mGray.getNativeObjAddr(), mRgba.getNativeObjAddr());
            //Log.d(MainActivity.TAG, "debug3");
            break;
        }
		   
		return mRgba;  
		
		//return inputFrame.rgba();
	}   

	
	public native void FindFeatures(long matAddrGr, long matAddrRgba);

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		Log.i(MainActivity.TAG,"onTouch event");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateandTime = sdf.format(new Date());
        String fileName = Environment.getExternalStorageDirectory().getPath() +
                               "/sample_picture_" + currentDateandTime + ".jpg";
        cvPreviewInst1.takePicture(fileName);
        Toast.makeText(this, fileName + " saved", Toast.LENGTH_SHORT).show();
        return false;
	}
  }
