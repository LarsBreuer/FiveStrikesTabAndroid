package de.fivestrikes.tab;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

public class FragGameList extends ListFragment {
	
	Cursor model=null;
	GameListAdapter adapter=null;
	SQLHelper helper=null;
	String spielId=null;
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
			spielId = args.getString("GameID");
			Log.v("FragGameList onResume If-Abfrage", String.valueOf(spielId));
			args = null;
		}
    	refreshContent();
    	
    }
    
    public void refreshContent() {
    	
		Log.v("FragGameList refreshContent", String.valueOf(spielId));
    	countRow=0;
    	model=null;
    	model=helper.getAllSpiel();
    	getActivity().startManagingCursor(model);
    	adapter=new GameListAdapter(model);
        setListAdapter(adapter);  
        
    }
    
/*
 * 
 * Auswahl des Spiels 
 *
 */
	
	@Override
	public void onListItemClick(ListView list, View view,
            int position, long id) {
	
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        FragGameEdit fragment = new FragGameEdit();
        Bundle args = new Bundle();
        spielId = String.valueOf(id);
        args.putString("GameID", spielId);
        fragment.setArguments(args);
        refreshContent();
        fragmentTransaction.replace(R.id.frag_game_edit, fragment);
        fragmentTransaction.commit();
        
	}
	
/*
 * 
 * Spielliste definieren 
 *
 */
	
	class GameListAdapter extends CursorAdapter {
		GameListAdapter(Cursor c) {
			super(getActivity(), c);
		}
    
		@Override
		public void bindView(View row, Context ctxt,
				Cursor c) {
			
			SpielHolder holder=(SpielHolder)row.getTag();
			holder.populateFrom(c, helper);
			
		}

		@Override
		public View newView(Context ctxt, Cursor c,
				ViewGroup parent) {
			
			LayoutInflater inflater=getActivity().getLayoutInflater();
			View row=inflater.inflate(R.layout.row_game, parent, false);
			countRow = countRow + 1;
			if ((countRow % 2) == 0) {
				row.setBackgroundColor(getResources().getColor(R.color.rowdark));
			} else {
				row.setBackgroundColor(getResources().getColor(R.color.rowlight));
			}
			if(spielId!=null){
				if(spielId.equals(helper.getTeamId(c))){
					row.setBackgroundColor(getResources().getColor(R.color.rowactive));
				} 
			}
			SpielHolder holder=new SpielHolder(row);
			row.setTag(holder);

			return(row);
		}
	}
	
	static class SpielHolder {
	    private TextView datum=null;
	    private TextView spielTeam=null;
	    private int day;
	    private int month;
	    private int year;
	    
	    SpielHolder(View row) {
	      
	    	datum=(TextView)row.findViewById(R.id.rowSpielDatum);
	    	spielTeam=(TextView)row.findViewById(R.id.rowSpielTeam);
	    	
	    }
	    
	    void populateFrom(Cursor c, SQLHelper helper) {
	    	
	    	String cursorSpielId=helper.getSpielId(c);
	    	String strSpielDatum = helper.getSpielDatum(cursorSpielId);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			try
			{
				Date spielDatum = sdf.parse(strSpielDatum);
				day = spielDatum.getDate();    // getDate => Day of month; getDay => day of week
				month = spielDatum.getMonth();
				year = spielDatum.getYear()+1900;
			}
			catch (ParseException e) {}
			datum.setText(new StringBuilder()
			// Month is 0 based, just add 1
				.append(day).append(".").append(month + 1).append(".")
				.append(year).append(" "));
	    	spielTeam.setText(helper.getTeamHeimNameBySpielID(cursorSpielId) + " - " + helper.getTeamAuswNameBySpielID(cursorSpielId));
	  
	    }
	}
}
