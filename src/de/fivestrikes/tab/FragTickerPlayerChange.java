package de.fivestrikes.tab;

import de.fivestrikes.tab.FragTickerPlayer.PlayerAdapter;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Button;


public class FragTickerPlayerChange extends ListFragment {
	
	private View view;
	Cursor model=null;
	PlayerAdapter adapter=null;
	SQLHelper helper=null;
	String teamId=null;
    String gameId=null;
    String aktionTeamHeim=null;
    String spielerId=null;
    String spielerString=null;
    String zeit=null;
    String realzeit=null;
	FragTickerList fragTickerList;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
/* Grundlayout setzen */

		view = inflater.inflate(R.layout.frag_ticker_player, container, false);
		fragTickerList = (FragTickerList) getActivity().getSupportFragmentManager().findFragmentById(R.id.frag_ticker_list);
        
/* Daten aus Activity laden */ 
        
		Bundle args = getArguments();
		if(args!=null){
			teamId = args.getString("TeamID");
			gameId = args.getString("GameID");
	        aktionTeamHeim=args.getString("AktionTeamHome"); 
	        zeit=args.getString("Time");
	        realzeit=args.getString("RealTime");
		} 
		
/* Datenbank laden */
	       
        helper=new SQLHelper(getActivity());
        model=helper.getAllSpieler(teamId);
        getActivity().startManagingCursor(model);
        adapter=new PlayerAdapter(model);
        setListAdapter(adapter);

		return view;
		
	}

/*
 * 
 * Abfrage, wenn die Sub-Activity das Resultat an die Main-Activty übergibt
 *
 */
	
	@Override
	public void onResume() {
	    super.onResume();  // Always call the superclass method first
	
	}
	
	@Override
	public void onDestroy() {
	  super.onDestroy();


	}


/*
 * 
 * Auswahl des Spielers => dann zurück zu Übersicht Spiel
 *
 */
	
	@Override
	public void onListItemClick(ListView list, View view,
            int position, long id) {
		
		/* Spielernamen anhand Spieler ID erhalten und neuen Ticker einfügen */
		
		spielerId=String.valueOf(id);
		spielerString=helper.getSpielerName(spielerId);
		helper.insertTicker(8, getString(R.string.tickerAktionAuswechselung), Integer.parseInt(aktionTeamHeim), spielerString, 
				Integer.parseInt(spielerId), Integer.parseInt(gameId), Integer.parseInt(zeit) + 1, realzeit);

		/* Ticker aktualisieren und Fragment schließen */
    	fragTickerList.refreshContent();
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        FragEmpty fragment = new FragEmpty();
        fragmentTransaction.replace(R.id.frag_ticker_player, fragment);
        fragmentTransaction.commit();
        
	}

/*
 * 
 * Spielerliste definieren 
 *
 */
	
	class PlayerAdapter extends CursorAdapter {
		PlayerAdapter(Cursor c) {
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