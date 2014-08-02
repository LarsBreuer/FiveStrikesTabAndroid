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

public class FragPlayerList extends ListFragment {
	
	Cursor model=null;
	PlayerListAdapter adapter=null;
	SQLHelper helper=null;
	String mannschaftId=null;
	String spielerId=null;
	Integer countRow=0;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {

/* Daten aus Activity laden */ 
        
		Bundle args = getArguments();
		if(args!=null && args.getString("TeamID")!=null){
			mannschaftId = args.getString("TeamID");
			Log.v("FragPlayerList", mannschaftId);
		}
		
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
			spielerId = args.getString("PlayerID");
		}
    	refreshContent();
    	
    }
    
    public void refreshContent() {
    	
    	Log.v("FragPlayerList", "refresh content");
        if(mannschaftId!=null){
        	Log.v("FragPlayerList - refresh content", mannschaftId);
        	model=helper.getAllSpieler(mannschaftId);
        	getActivity().startManagingCursor(model);
        	adapter=new PlayerListAdapter(model);
        	setListAdapter(adapter);
        }
        
    }
    
/*
 * 
 * Spielerauswahl 
 *
 */
	
	@Override
	public void onListItemClick(ListView list, View view,
            int position, long id) {
	
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        FragPlayerEdit fragment = new FragPlayerEdit();
        // Werte übermitteln
        Bundle args = new Bundle();
        spielerId = String.valueOf(id);
        args.putString("TeamID", mannschaftId);
        args.putString("PlayerID", spielerId);
        fragment.setArguments(args);
        // Ende Werte übermitteln
        fragmentTransaction.replace(R.id.frag_player_edit, fragment);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        refreshContent();
        
	}

/*
 * 
 * Spielerliste definieren 
 *
 */
	
	class PlayerListAdapter extends CursorAdapter {
		PlayerListAdapter(Cursor c) {
			super(getActivity(), c);
		}
    
		@Override
		public void bindView(View row, Context ctxt,
				Cursor c) {
			SpielerHolder holder=(SpielerHolder)row.getTag();
			      
			holder.populateFrom(c, helper);
		}

		@Override
		public View newView(Context ctxt, Cursor c,
				ViewGroup parent) {
			LayoutInflater inflater=getActivity().getLayoutInflater();
			View row=inflater.inflate(R.layout.row_player, parent, false);
			countRow = countRow + 1;
			if ((countRow % 2) == 0) {
				row.setBackgroundColor(getResources().getColor(R.color.rowdark));
			} else {
				row.setBackgroundColor(getResources().getColor(R.color.rowlight));
			}
			if(spielerId!=null){
				if(spielerId.equals(helper.getSpielerId(c))){
					row.setBackgroundColor(getResources().getColor(R.color.rowactive));
				} 
			}
			SpielerHolder holder=new SpielerHolder(row);

			row.setTag(holder);

			return(row);
		}
	}
	
	static class SpielerHolder {
	    private TextView name=null;
	    private TextView nummer=null;
	    
	    SpielerHolder(View row) {
	    	name=(TextView)row.findViewById(R.id.rowMannschaftName);
	    	nummer=(TextView)row.findViewById(R.id.rowMannschaftNummer);
	    }
	    
	    void populateFrom(Cursor c, SQLHelper helper) {
	    	String spielerId=helper.getSpielerId(c);
	    	name.setText(helper.getSpielerName(spielerId));
	      	nummer.setText(helper.getSpielerNummer(spielerId));
	  
	    }
	}
}
