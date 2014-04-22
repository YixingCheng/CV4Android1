package sc.vision.cv4android1;

import java.io.File;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class sectionFragment extends Fragment {
	final static String ARG_POSITION = "position";
    int mCurrentPosition = -1;
	
	public sectionFragment() {
		// TODO Auto-generated constructor stub
		super();
		Log.d(MainActivity.TAG, "in sectionFragment constructor");
	}
    
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
		Bundle args = getArguments();
		Log.d(MainActivity.TAG, "in sectionFragment onCreateView");
        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
        }
        
        Log.d(MainActivity.TAG, "in sectionFragment onCreateView middle");
        int whichFragment = args.getInt(ARG_POSITION);
        Log.d(MainActivity.TAG, "the value of whichFragment is " + whichFragment);
        if(whichFragment == 0){
        	 Log.d(MainActivity.TAG, "in sectionFragment create summary view");
        	 return inflater.inflate(R.layout.result_summary, container, false);
           }
        else{ 
        	 Log.d(MainActivity.TAG, "in sectionFragment create gallery view");
        	 return inflater.inflate(R.layout.result_scroll, container, false);
        }
        //args = getArguments();
        
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.result_summary, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.
         Bundle args = getArguments();
        if (args != null) {
        	int whichFragment = args.getInt(ARG_POSITION);
        	if( whichFragment == 0){
        		    updateSectionView(0);
        	  }
        	else
        		    updateSectionView(whichFragment);
            // Set article based on argument passed in
            // updateSectionView(args.getInt(ARG_POSITION));
        } else if (mCurrentPosition != -1) {
            // Set article based on saved instance state defined during onCreateView
             updateSectionView(mCurrentPosition);
        }
    }

    public void updateSectionView(int position) {
    	switch(position){
    	          case 0:  TextView article = (TextView) getActivity().findViewById(R.id.article);
    	                   article.setText("debug");
    	                   break;
    	          case 1:  LinearLayout javaLbpGallery = (LinearLayout) getActivity().findViewById(R.id.mygallery);
    	                   String targetPath1 = getTargetPath(1);
    	                   File targetDirector1 = new File(targetPath1);    
    	                   
    	                   File[] files1 = targetDirector1.listFiles();
    	                   for (File file : files1){
    	                        javaLbpGallery.addView(insertPhoto(file.getAbsolutePath()));
    	                     }
    	                   targetDirector1 = null;
    	        	       break;
    	          case 2:  LinearLayout javaHaarGallery = (LinearLayout) getActivity().findViewById(R.id.mygallery);
                           String targetPath2 = getTargetPath(2);
                           File targetDirector2 = new File(targetPath2);    
                  
                           File[] files2 = targetDirector2.listFiles();
                           for (File file : files2){
                                javaHaarGallery.addView(insertPhoto(file.getAbsolutePath()));
                             }
                           targetDirector2 = null;
       	                   break;
    	          case 3:  LinearLayout nativeLbpGallery = (LinearLayout) getActivity().findViewById(R.id.mygallery);
                           String targetPath3 = getTargetPath(3);
                           File targetDirector3 = new File(targetPath3);    
         
                           File[] files3 = targetDirector3.listFiles();
                           for (File file : files3){
                                nativeLbpGallery.addView(insertPhoto(file.getAbsolutePath()));
                             }
                           targetDirector3 = null;
	                       break;
    	          case 4:  LinearLayout nativeHaarGallery = (LinearLayout) getActivity().findViewById(R.id.mygallery);
                           String targetPath4 = getTargetPath(4);
                           File targetDirector4 = new File(targetPath4);    

                           File[] files4 = targetDirector4.listFiles();
                           for (File file : files4){
                                nativeHaarGallery.addView(insertPhoto(file.getAbsolutePath()));
                             }
                           targetDirector4 = null;
                           break;
    	        	       
    	
    	    }
   /* 	if(position == 0){
    		
    	   }
    	else{
			  Gallery gallery = (Gallery) getActivity().findViewById(R.id.result_gallery1);
			  
    	   }
        TableLayout article = (TableLayout) getActivity().findViewById(R.id.result_table);  */
      //  article.setText(Ipsum.Articles[position]);
        mCurrentPosition = position;
    }   

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the current article selection in case we need to recreate the fragment
        outState.putInt(ARG_POSITION, mCurrentPosition);
    }
    
    public View insertPhoto(String path){
	     Bitmap bm = decodeSampledBitmapFromUri(path, 220, 220);
	     
	     LinearLayout layout = new LinearLayout(getActivity());
	     layout.setLayoutParams(new LayoutParams(250, 250));
	     layout.setGravity(Gravity.CENTER);
	     
	     ImageView imageView = new ImageView(getActivity());
	     imageView.setLayoutParams(new LayoutParams(220, 220));
	     imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	     imageView.setImageBitmap(bm);
	     
	     layout.addView(imageView);
	     return layout;
	    }
    
    public String getTargetPath(int i){
	    String ExternalStorageDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();
	    switch(i){
	           case 1:
	        	    return ExternalStorageDirectoryPath + "/javalbpsample/";
	           case 2:
	        	    return  ExternalStorageDirectoryPath + "/javahaarsample/";
	           case 3:
	        	    return ExternalStorageDirectoryPath + "/nativelbpsample/";
	           case 4:
	        	    return ExternalStorageDirectoryPath + "/nativehaarsample/";
	           default:
	        	    return null;
	      }
	}
    
    private Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {
	     Bitmap bm = null;
	     
	     // First decode with inJustDecodeBounds=true to check dimensions
	     final BitmapFactory.Options options = new BitmapFactory.Options();
	     options.inJustDecodeBounds = true;
	     BitmapFactory.decodeFile(path, options);
	     
	     // Calculate inSampleSize
	     options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	     
	     // Decode bitmap with inSampleSize set
	     options.inJustDecodeBounds = false;
	     bm = BitmapFactory.decodeFile(path, options); 
	     
	     return bm;  
	   }
    
    private int calculateInSampleSize(
		      
		     BitmapFactory.Options options, int reqWidth, int reqHeight) {
		     // Raw height and width of image
		     final int height = options.outHeight;
		     final int width = options.outWidth;
		     int inSampleSize = 1;
		        
		     if (height > reqHeight || width > reqWidth) {
		      if (width > height) {
		       inSampleSize = Math.round((float)height / (float)reqHeight);   
		      } else {
		       inSampleSize = Math.round((float)width / (float)reqWidth);   
		      }   
		     }
		     
		     return inSampleSize;   
		 }
}
