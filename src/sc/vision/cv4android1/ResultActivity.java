package sc.vision.cv4android1;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
//import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Button;
import android.content.Intent;
import android.annotation.TargetApi;

public class ResultActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(MainActivity.TAG, "ResultActivity initialized!");
		setContentView(R.layout.activity_result);
		setupActionBar();
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)                                        //take the action bar out
	private void setupActionBar() {
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		  
		    getActionBar().setDisplayHomeAsUpEnabled(true);
	      }
      }       
 	
	@Override
    public void onPause()
    {
        super.onPause();
        
    }
	
	@Override
    public void onResume()
    {
        super.onResume();
     
    }
	
	@Override
	public void onDestroy() {
        super.onDestroy();
        
    }
	

}
