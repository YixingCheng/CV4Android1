package sc.vision.cv4android1;

import android.app.ListFragment;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class horizontalFragments extends ListFragment {
	onFragmentSelectedListener fragmentCallBack;
	private String fragmentHeadings[];
    
	// The container Activity must implement this interface so the frag can deliver messages
    public interface onFragmentSelectedListener {
        /** Called by HeadlinesFragment when a list item is selected */
        public void onFragmentSelected(int position);
    }
	
	public horizontalFragments() {
		// TODO Auto-generated constructor stub
		super();
		fragmentHeadings = new String[]{"Overall Summary", "Java LBP Samples", "Java Haar Samples", "Native LBP Samples", "Native Haar Samples"};
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We need to use a different list item layout for devices older than Honeycomb
        int layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                android.R.layout.simple_list_item_activated_1 : android.R.layout.simple_list_item_1;

        // Create an array adapter for the list view, using the Ipsum headlines array
        setListAdapter(new ArrayAdapter<String>(getActivity(), layout, fragmentHeadings));
    }
	
	@Override
    public void onStart() {
        super.onStart();
   /*     // When in two-pane layout, set the listview to highlight the selected list item
        // (We do this during onStart because at the point the listview is available.)
        if (getFragmentManager().findFragmentById(R.id.article_fragment) != null) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);   
        }  */
    }
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
        	fragmentCallBack = (onFragmentSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onFragmentSelectedListener");
        }
    }
	
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Notify the parent activity of selected item
        fragmentCallBack.onFragmentSelected(position);
        
        // Set the item as checked to be highlighted when in two-pane layout
      //  getListView().setItemChecked(position, true);
    }

}
