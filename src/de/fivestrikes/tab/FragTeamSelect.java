package de.fivestrikes.tab;

import android.support.v4.app.Fragment;
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

public class FragTeamSelect extends ListFragment {
	
	Cursor model=null;
	TeamListAdapter adapter=null;
	SQLHelper helper=null;
	String mannschaftId=null;
	Integer countRow=0;
	String haTeam;
	String gameId;
	Integer intDay;
	Integer intMonth;
	Integer intYear;
	String spiel_halbzeitlaenge;
	Integer heimID;
	Integer auswID;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		
/* Datenbank laden */
        
        helper=new SQLHelper(getActivity());
    	countRow=0;
    	model=helper.getAllTeam();
    	getActivity().startManagingCursor(model);
    	adapter=new TeamListAdapter(model);
        setListAdapter(adapter); 
        
    	Bundle args = getArguments();
		if(args!=null){
			haTeam = args.getString("haTeam");
			gameId = args.getString("GameID");
			intDay = args.getInt("Day");
			intMonth = args.getInt("Month");
			intYear = args.getInt("Year");
			spiel_halbzeitlaenge = args.getString("Halftime");
			heimID = args.getInt("HomeID");
			auswID = args.getInt("AwayID");
		}
		
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.list, container, false);
	}
	
/*
 * 
 * Mannschaftauswahl 
 *
 */
	
	@Override
	public void onListItemClick(ListView list, View view,
            int position, long id) {
		
        Bundle args = new Bundle();
        if(haTeam.equals("Heim")){
        	heimID = (int) id;
        }
        if(haTeam.equals("Auswaerts")){
        	auswID = (int) id;
        }
        args.putInt("HomeID", heimID);
        args.putInt("AwayID", auswID);
        args.putString("haTeam", haTeam);
        args.putString("GameID", gameId);
        args.putInt("Day", intDay);
        args.putInt("Month", intMonth);
        args.putInt("Year", intYear);
        args.putString("Halftime", spiel_halbzeitlaenge);
		//Intent intent = new Intent();
        //intent.putExtra("TeamID", mannschaftId);
        //getTargetFragment().onActivityResult(getTargetRequestCode(), GameActivity.RESULT_OK, intent);
        FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        FragGameEdit fragment = new FragGameEdit();
        fragment.setArguments(args);
        //fragmentManager.popBackStack();
        //fragment.setTargetFragment(FragGameEdit.this, FRAGMENT_CODE);
        fragmentTransaction.replace(R.id.frag_game_edit, fragment);
        //fragmentTransaction.addToBackStack(null);
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
