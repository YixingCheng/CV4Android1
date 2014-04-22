package sc.vision.cv4android1;


import sc.vision.cv4android1.horizontalFragments.onFragmentSelectedListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class ResultActivity extends Activity implements onFragmentSelectedListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		
		if (findViewById(R.id.container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            
            horizontalFragments resultsFragment = new horizontalFragments();

            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            resultsFragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction()
                    .add(R.id.container, resultsFragment).commit();
        }
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.result, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}  

	/**
	 * A placeholder fragment containing a simple view.
	 */
 /*	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_result,
					container, false);
			return rootView;
		}
	}    */

	@Override
	public void onFragmentSelected(int position) {
		 // TODO Auto-generated method stub
		 Log.d(MainActivity.TAG, "section Fragment Clicked");
		 sectionFragment resultSection = new sectionFragment();
		 Bundle args = new Bundle();
         args.putInt(sectionFragment.ARG_POSITION, position);
         resultSection.setArguments(args);
         Log.d(MainActivity.TAG, "after set Arguments");
         FragmentTransaction transaction = getFragmentManager().beginTransaction();

         // Replace whatever is in the fragment_container view with this fragment,
         // and add the transaction to the back stack so the user can navigate back
         transaction.replace(R.id.container, resultSection);
         transaction.addToBackStack(null);
         Log.d(MainActivity.TAG, "after transaction");
         // Commit the transaction
         transaction.commit();
         Log.d(MainActivity.TAG, "after transaction commit");
		
	}

}   

