package de.fivestrikes.tab;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.ListView;

public class FragTeamList extends ListFragment {
	
	Cursor model=null;
	TeamListAdapter adapter=null;
	SQLHelper helper=null;
	String mannschaftId=null;
	Integer countRow=0;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		       
/* Datenbank laden */
        
        helper=new SQLHelper(getActivity());
        refreshContent();
		
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.list, container, false);
	}

/*
 * 
 * Inhalt neu laden, wenn Activity ernuet aufgerufen wird 
 *
 */
	
    @Override
	public void onResume() {
    	super.onResume();  // Always call the superclass method first
    	
    	Bundle args = getArguments();
		if(args!=null){
			mannschaftId = args.getString("TeamID");
		}
    	refreshContent();
    	
    }
    
    public void refreshContent() {
    	
    	countRow=0;
    	model=helper.getAllTeam();
    	getActivity().startManagingCursor(model);
    	adapter=new TeamListAdapter(model);
        setListAdapter(adapter);  
        
    }
    
/*
 * 
 * Mannschaftauswahl 
 *
 */
	
	@Override
	public void onListItemClick(ListView list, View view,
            int position, long id) {
	
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        FragTeamEdit fragment = new FragTeamEdit();
        Bundle args = new Bundle();
        mannschaftId = String.valueOf(id);
        args.putString("TeamID", mannschaftId);
        fragment.setArguments(args);
        refreshContent();
        fragmentTransaction.replace(R.id.frag_team_edit, fragment);
        fragmentTransaction.commit();
        
	}
	
/*
 * 
 * Mannschaftsliste definieren 
 *
 */
	
	class TeamListAdapter extends CursorAdapter {
		TeamListAdapter(Cursor c) {
			super(getActivity(), c);
		}
    
		@Override
		public void bindView(View row, Context ctxt,
				Cursor c) {
			
			MannschaftHolder holder=(MannschaftHolder)row.getTag();
			holder.populateFrom(c, helper);
			
		}

		@Override
		public View newView(Context ctxt, Cursor c,
				ViewGroup parent) {
			
			LayoutInflater inflater=getActivity().getLayoutInflater();
			View row=inflater.inflate(R.layout.row_team, parent, false);
			countRow = countRow + 1;
			if ((countRow % 2) == 0) {
				row.setBackgroundColor(getResources().getColor(R.color.rowdark));
			} else {
				row.setBackgroundColor(getResources().getColor(R.color.rowlight));
			}
			if(mannschaftId!=null){
				if(mannschaftId.equals(helper.getTeamId(c))){
					row.setBackgroundColor(getResources().getColor(R.color.rowactive));
				} 
			}
			MannschaftHolder holder=new MannschaftHolder(row);
			row.setTag(holder);

			return(row);
		}
	}
	
	static class MannschaftHolder {
	    private TextView name=null;
	    
	    MannschaftHolder(View row) {
	      
	    	name=(TextView)row.findViewById(R.id.rowMannschaftName);
	    	
	    }
	    
	    void populateFrom(Cursor c, SQLHelper helper) {
	    	
	    	String teamId = helper.getTeamId(c);
	    	name.setText(helper.getTeamName(teamId));
	  
	    }
	}
}
