package sc.vision.cv4android1;


import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.io.*;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
//import org.opencv.core.Size;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
//import org.opencv.samples.facedetect.DetectionBasedTracker;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
//import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
//import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class CameraActivity extends Activity implements CvCameraViewListener2, OnTouchListener{
	
	private Camera myCamera;
	//private CameraPreview myPreview;
	//public Display display;
	//public Point size;
	//public static int screen_width;
	//public static int screen_height;
	
	private static final Scalar    FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);
	private static final int       VIEW_MODE_RGBA      = 2;
    //private static final int       VIEW_MODE_GRAY      = 1;
    //private static final int       VIEW_MODE_CANNY     = 2;
    private static final int       VIEW_MODE_FEATURES  = 5;
    // private static final int       VIEW_MODE_HIST      = 6;
    private static final int        JAVA_DETECTOR       = 0;
    private static final int        JAVA_HAAR_DETECTOR  = 4;
    private static final int        NATIVE_DETECTOR     = 1;
    private static final int        NATIVE_HAAR_DETECTOR = 3;
    
    private MenuItem               ItemPreviewRGBA;
    //private MenuItem               ItemPreviewGray;
    //private MenuItem               ItemPreviewCanny;
 //   private MenuItem               ItemPreviewFeatures;
    //private MenuItem               ItemPreviewHist;
    private List<Size>             mResolutionList;
 //   private MenuItem[]             mEffectMenuItems;
 //   private SubMenu                mColorEffectsMenu;
    private MenuItem[]             mResolutionMenuItems;
    private SubMenu                mResolutionMenu;
    private MenuItem               javaDetectorMenu;
    private MenuItem               javaHaarDetectorMenu;
    private MenuItem               nativeDetectorMenu;
    private MenuItem               nativeHaarDetectorMenu;
    
    private int                    mViewMode;
    private Mat                    mRgba;
    private Mat                    mIntermediateMat;
    private Mat                    mGray;
    private float                  relativeFaceSize   = 0.2f;
    private int                    absoluteFaceSize   = 0;
 //   private int                    histSizeNum = 25;
    private  Rect[]                 javaLbpArray;
    private  Rect[]                 nativeLbpArray;
    private  Rect[]                 javaHaarArray;
    private  Rect[]                 nativeHaarArray;
    public ArrayList<Mat>         javaLbpMats;
    public ArrayList<Mat>         nativeLbpMats;
    public ArrayList<Mat>         javaHaarMats;
    public ArrayList<Mat>         nativeHaarMats;
    
    private File                   cascadeFile;
    private File                   haarFile;
    private cvCameraPreview        cvPreviewInst1;
    private CascadeClassifier      javaDetector;
    private CascadeClassifier      javaHaarDetector;
    private DetectionBasedTracker  nativeDetector;
    private DetectionBasedTracker  haarDetector;
  //  private String[]               detectorName;
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
                    
                    try{
                    	InputStream inputFile = getResources().openRawResource(R.raw.cv4android1_lbpcascade);  //filename in raw folder has to be lower case
                    	InputStream inputHaar = getResources().openRawResource(R.raw.cv4android1_haarcascade);
                    	File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);                      // create/retrieve a directory where App save it's own data
                    	cascadeFile = new File(cascadeDir, "cv4android1_lbpcascade.xml");               // the file where the trained cascade data will be saved
                    	haarFile    = new File(cascadeDir, "cv4android1_haarcascade.xml");
                    	FileOutputStream outputFile = new FileOutputStream(cascadeFile);
                    	FileOutputStream outputHaar = new FileOutputStream(haarFile);
                    	
                    	byte[] buffer = new byte[4096];
                    	int bytesRead;                                                 //basically transfer data from one file to another
                    	while ((bytesRead = inputFile.read(buffer)) != -1) {
                            outputFile.write(buffer, 0, bytesRead);
                        }
                    	inputFile.close();
                        outputFile.close();
                        
                        byte[] buffer1 = new byte[4096];
                    	int bytesRead1;                                                 //basically transfer data from one file to another
                    	while ((bytesRead1 = inputHaar.read(buffer1)) != -1) {
                            outputHaar.write(buffer1, 0, bytesRead1);
                        }
                    	inputHaar.close();
                        outputHaar.close();
                        
                        javaDetector = new CascadeClassifier(cascadeFile.getAbsolutePath());
                        javaHaarDetector = new CascadeClassifier(haarFile.getAbsolutePath());
                        if(javaDetector.empty()){
                        	Log.e(MainActivity.TAG, "Failed to load cascade classifier");
                            javaDetector = null;
                          } 
                        else{
                            Log.i(MainActivity.TAG, "Loaded cascade classifier from " + cascadeFile.getAbsolutePath());
                          }
                        
                        if(javaHaarDetector.empty()){
                        	Log.e(MainActivity.TAG, "Failed to load haar cascade classifier");
                            javaHaarDetector = null;
                          } 
                        else{
                            Log.i(MainActivity.TAG, "Loaded haarcascade classifier from " + haarFile.getAbsolutePath());
                          }
                        
                        nativeDetector = new DetectionBasedTracker(cascadeFile.getAbsolutePath(), 0);
                        haarDetector   = new DetectionBasedTracker(haarFile.getAbsolutePath(), 0);
                        
                        cascadeDir.delete();
                                              
                    }catch(IOException e){
                    	e.printStackTrace();
                        Log.e(MainActivity.TAG, "Failed to load cascade. Exception thrown: " + e);
                    }   
                    
                    cvPreviewInst1.enableView();
                    cvPreviewInst1.setOnTouchListener(CameraActivity.this);
                }   break;  
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public CameraActivity() {
        Log.i(MainActivity.TAG, "Instantiated new " + this.getClass());
     //   detectorName = new String[2];
     //   detectorName[JAVA_DETECTOR] = "Java";
     //   detectorName[NATIVE_DETECTOR] = "Native";
    }
    
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		Log.d(MainActivity.TAG, "called onCreate");
		
		super.onCreate(savedInstanceState);
		
		//display = getWindowManager().getDefaultDisplay();
		//Point size = new Point();
		//display.getSize(size);
		//screen_width = size.x; screen_height = size.y;
		
		//Log.d(MainActivity.TAG, "screen size is: " + size.x + " * " + size.y);
		
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
		
		addListenerToButton();

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
	/*    @TargetApi(Build.VERSION_CODES.HONEYCOMB)                                        //take the action bar out
    	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			//Log.d(MainActivity.TAG, "debug4");
			getActionBar().hide();                         //disable the Action Bar
			//Log.d(MainActivity.TAG, "debug5");
			getActionBar().setDisplayHomeAsUpEnabled(false);
		}
	}  */ 
       // Since this activity will be used to hold camera preview,
       // so we don't need the action bar
 
	@Override
     	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.camera, menu);
		     Log.i(MainActivity.TAG, "called onCreateOptionsMenu");
		     
		/*   List<String> effects = cvPreviewInst1.getEffectList();
		     
		     if(effects == null) {
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
		      }       */
		     
		     
		     mResolutionMenu = menu.addSubMenu("Resolution");
		     mResolutionList = cvPreviewInst1.getResolutionList();
		     mResolutionMenuItems = new MenuItem[mResolutionList.size()];    
		     
		     ListIterator<Size> resolutionItr = mResolutionList.listIterator();
		     int idx = 0;
		       while(resolutionItr.hasNext()) {
		          Size element = resolutionItr.next();
		           mResolutionMenuItems[idx] = mResolutionMenu.add(2, idx, Menu.NONE,
		                    Integer.valueOf(element.width).toString() + "x" + Integer.valueOf(element.height).toString());
		          idx++;
		      }
		     
             //ItemSwitchCamera = menu.add("Toggle Native/Java camera");
             ItemPreviewRGBA = menu.add("Preview RGBA");
          //   ItemPreviewGray = menu.add("Preview GRAY");
          //     ItemPreviewHist = menu.add("Preview Histogram");
           //  ItemPreviewCanny = menu.add("Canny");
        //     ItemPreviewFeatures = menu.add("Find features");
             javaDetectorMenu = menu.add("Java LBP");
             nativeDetectorMenu = menu.add("Native LBP");
             javaHaarDetectorMenu = menu.add("Java Haar");
             nativeHaarDetectorMenu = menu.add("Native Haar");
             
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
         /*    else if( item == ItemPreviewHist){
            	mViewMode =  VIEW_MODE_HIST;
            	toastMesage = "Histogram";
            	Toast toast = Toast.makeText(this, toastMesage, Toast.LENGTH_LONG);
            	toast.show();
              }    */
         /*    else if (item == ItemPreviewGray){
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
               }   */
            else if (item == javaDetectorMenu){
            	mViewMode = JAVA_DETECTOR;
            	setDetectorType(JAVA_DETECTOR);
            	toastMesage = "JAVA LBP DETECTOR";
            	Toast toast = Toast.makeText(this, toastMesage, Toast.LENGTH_LONG);
                toast.show();
               }
            else if (item == nativeDetectorMenu){
            	mViewMode = NATIVE_DETECTOR;
            	setDetectorType(NATIVE_DETECTOR);
            	toastMesage = "NATIVE LBP DETECTOR";
            	Toast toast = Toast.makeText(this, toastMesage, Toast.LENGTH_LONG);
                toast.show();
               }
            else if (item == nativeHaarDetectorMenu){
            	mViewMode = NATIVE_HAAR_DETECTOR;
            	setDetectorType(NATIVE_HAAR_DETECTOR);
            	toastMesage = "NATIVE HAAR DETECTOR";
            	Toast toast = Toast.makeText(this, toastMesage, Toast.LENGTH_LONG);
                toast.show();
               }
            else if (item == javaHaarDetectorMenu){
            	mViewMode = JAVA_HAAR_DETECTOR;
            	setDetectorType(JAVA_HAAR_DETECTOR);
            	toastMesage = "JAVA HAAR DETECTOR";
            	Toast toast = Toast.makeText(this, toastMesage, Toast.LENGTH_LONG);
                toast.show();
               }
        /*     if ( item.getGroupId() == 1){
            	cvPreviewInst1.setEffect((String) item.getTitle());
                Toast.makeText(this, cvPreviewInst1.getEffect(), Toast.LENGTH_SHORT).show();
               }
            else  */
                if ( item.getGroupId() == 1){
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
		//mRgba = new Mat(height, width, CvType.CV_8UC4);
        //mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
        //mGray = new Mat(height, width, CvType.CV_8UC1);
		mRgba = new Mat();
		mIntermediateMat = new Mat();
		mGray = new Mat();
		javaLbpMats = new ArrayList<Mat>();
	    nativeLbpMats = new ArrayList<Mat>();
	    javaHaarMats = new ArrayList<Mat>();
	    nativeHaarMats = new ArrayList<Mat>();
        
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
	    mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        if (absoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * relativeFaceSize) > 0) {
                absoluteFaceSize = Math.round(height * relativeFaceSize);
            }
            nativeDetector.setMinFaceSize(absoluteFaceSize);
         }

        MatOfRect faces = new MatOfRect();
  //    Rect[] facesArray;
	    
	/*	Mat innerWindow;
		org.opencv.core.Size sizeRGBA = mRgba.size();
		
		int rows = (int) sizeRGBA.height;
		int cols = (int) sizeRGBA.width;
		
		int leftMargin = cols / 8;
		int topMargin = rows / 8;
		
		int innerWindowHeight = rows * 3 / 4;
		int innerWindowWidth = cols * 3 / 4;  */
		
		final int viewMode = mViewMode;

		switch (viewMode) {
	/*	case VIEW_MODE_HIST:
		    // overlay the histogram on the input frame
			Mat hist = new Mat();
			int thikness = (int) (sizeRGBA.width / (histSizeNum + 10) / 5);
	        if(thikness > 5) thikness = 5;
	        int offset = (int) ((sizeRGBA.width - (5*histSizeNum + 4*10)*thikness)/2);
	     // RGB
            for(int c=0; c<3; c++) {
                Imgproc.calcHist(Arrays.asList(localRGBA), mChannels[c], mMat0, hist, mHistSize, mRanges);
                Core.normalize(hist, hist, sizeRgba.height/2, 0, Core.NORM_INF);
                hist.get(0, 0, mBuff);
                for(int h=0; h<mHistSizeNum; h++) {
                    mP1.x = mP2.x = offset + (c * (mHistSizeNum + 10) + h) * thikness;
                    mP1.y = sizeRgba.height-1;
                    mP2.y = mP1.y - 2 - (int)mBuff[h];
                    Core.line(rgba, mP1, mP2, mColorsRGB[c], thikness);
                }
            }
     			
        case VIEW_MODE_GRAY:
            // input frame has gray scale format
            Imgproc.cvtColor(inputFrame.gray(), mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
            break;    */
        case VIEW_MODE_RGBA:
            // input frame has RBGA format
            // mRgba = inputFrame.rgba();
            break;
    /*    case VIEW_MODE_CANNY:
            // input frame has gray scale format
        	innerWindow = mRgba.submat(topMargin, topMargin + innerWindowHeight, leftMargin, leftMargin + innerWindowWidth);
           // mRgba = inputFrame.rgba();
            Imgproc.Canny(innerWindow, mIntermediateMat, 80, 100);
            Imgproc.cvtColor(mIntermediateMat, innerWindow, Imgproc.COLOR_GRAY2RGBA, 4);
            innerWindow.release();
            break;    */
   /*     case VIEW_MODE_FEATURES:
            // input frame has RGBA format
        	//Log.d(MainActivity.TAG, "debug1");
            //mRgba = inputFrame.rgba();
            mGray = inputFrame.gray();
            //Log.d(MainActivity.TAG, "debug2");
            FindFeatures(mGray.getNativeObjAddr(), mRgba.getNativeObjAddr());
            //Log.d(MainActivity.TAG, "debug3");
            break;     */
        case JAVA_DETECTOR:
        	if (javaDetector != null)
                javaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                        new org.opencv.core.Size(absoluteFaceSize, absoluteFaceSize), new org.opencv.core.Size());
        	
        	javaLbpArray = faces.toArray();
            for (int i = 0; i < javaLbpArray.length; i++){
                  Core.rectangle(mRgba, javaLbpArray[i].tl(), javaLbpArray[i].br(), FACE_RECT_COLOR, 3);
                  javaLbpMats.add(mRgba.submat(javaLbpArray[i]));
              }
            break;
        case NATIVE_DETECTOR:
        	 if (nativeDetector != null)
                 nativeDetector.detect(mGray, faces);
        	 
        	 nativeLbpArray = faces.toArray();
             for (int i = 0; i < nativeLbpArray.length; i++)
                 Core.rectangle(mRgba, nativeLbpArray[i].tl(), nativeLbpArray[i].br(), FACE_RECT_COLOR, 3);
        	 
        	 break;
        case NATIVE_HAAR_DETECTOR:
        	if (haarDetector != null)
                haarDetector.detect(mGray, faces);
       	 
       	    nativeHaarArray = faces.toArray();
            for (int i = 0; i < nativeHaarArray.length; i++)
                Core.rectangle(mRgba, nativeHaarArray[i].tl(), nativeHaarArray[i].br(), FACE_RECT_COLOR, 3);
       	 
       	     break;
        case JAVA_HAAR_DETECTOR:
        	if (javaHaarDetector != null)
                javaHaarDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                        new org.opencv.core.Size(absoluteFaceSize, absoluteFaceSize), new org.opencv.core.Size());
        	
        	javaHaarArray = faces.toArray();
            for (int i = 0; i < javaHaarArray.length; i++)
                Core.rectangle(mRgba, javaHaarArray[i].tl(), javaHaarArray[i].br(), FACE_RECT_COLOR, 3);
            
            break;
       	 
       }
		   
		return mRgba; 
		//return inputFrame.rgba();
	}   

	private void setDetectorType(int type) {
            switch(type){
                 case NATIVE_DETECTOR:
                	 Log.i(MainActivity.TAG, "Detection Based Tracker enabled");
                	 haarDetector.stop();
                     nativeDetector.start();
                     break;
                 case JAVA_DETECTOR:
                	 Log.i(MainActivity.TAG, "Cascade detector enabled");
                     nativeDetector.stop();
                     haarDetector.stop();
                     break;
                 case NATIVE_HAAR_DETECTOR:
                	 Log.i(MainActivity.TAG, "Haar cascade detector enabled");
                	 nativeDetector.stop();
                	 haarDetector.start();
                	 break;
                 case JAVA_HAAR_DETECTOR:
                	 Log.i(MainActivity.TAG, "Cascade detector enabled");
                     nativeDetector.stop();
                     haarDetector.stop();
                     break;
              }
        
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
	
  private void addListenerToButton(){
		
		final Button getResult = (Button) findViewById(R.id.get_results);
		
		getResult.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				 /* when you are writing in the parentheses of setOnClickListener,
				  *  you have to use FromActivity. to qualify the this*/
				 
				 haarDetector.stop();                      //stop the two detectors
				 nativeDetector.stop();
				 
				 Log.i(MainActivity.TAG, "Get Results Clicked!");
				 Intent resultIntent = new Intent(CameraActivity.this, ResultActivity.class);   
                 startActivity(resultIntent); 
			  }			
		   });
		
	}
  
  private void writeImages(){
	     
    }
}

