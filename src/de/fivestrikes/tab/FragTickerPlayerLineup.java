package de.fivestrikes.tab;

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
import android.widget.TextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.view.View.OnClickListener;

public class FragTickerPlayerLineup extends ListFragment {
	
	private View view;
	Cursor model=null;
	PlayerAdapter adapter=null;
	SQLHelper helper=null;
	String teamId=null;
    String gameId=null;
    String aktionString=null;
    String aktionInt=null;
    String aktionTeamHeim=null;
    String spielerId=null;
    String spielerString=null;
    String spielerPosition=null;
    String zeit=null;
    String realzeit=null;
    static boolean[] checkBoxState;
    int spielerZaehler;
    int spielerCount;
    int halbzeitlaenge;
    FragTickerList fragTickerList;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
/* Grundlayout setzen */

		view = inflater.inflate(R.layout.frag_ticker_player_lineup, container, false);
		fragTickerList = (FragTickerList) getActivity().getSupportFragmentManager().findFragmentById(R.id.frag_ticker_list);
		
/* Daten aus Activity laden */ 
        
		Bundle args = getArguments();
		if(args!=null){
			gameId = args.getString("GameID");
			aktionInt=args.getString("StrAktionInt");
	        aktionString=args.getString("StrAktion");
	        aktionTeamHeim=args.getString("AktionTeamHome"); 
	        zeit=args.getString("Time");
	        realzeit=args.getString("RealTime");
	        if(aktionTeamHeim.equals("1")) { teamId=args.getString("TeamHomeID"); }
	        if(aktionTeamHeim.equals("0")) { teamId=args.getString("TeamAwayID"); }
		} 
		
/* Datenbank laden */
        
        helper=new SQLHelper(getActivity());
        model=helper.getAllSpieler(teamId);
        getActivity().startManagingCursor(model);
        adapter=new PlayerAdapter(model);
        setListAdapter(adapter);
        model.moveToFirst();
        spielerCount = model.getCount();
        checkBoxState=new boolean[spielerCount];
	    
 /* Button einrichten */
	    
        Button okButton = (Button) view.findViewById(R.id.ok_button);
		
/* Daten aus Datenbank laden */
        
	    halbzeitlaenge=Integer.parseInt(helper.getSpielHalbzeitlaenge(gameId))*60*1000;
	    
/* OK-Button */
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	uebertragen(spielerCount, halbzeitlaenge);
            }
        });
		
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
	
    
    public void finish() {
    	
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
		public void bindView(View row, Context ctxt, Cursor c) {
			final int position = c.getPosition();
			final CheckBox cbListCheck;
			SpielerHolder holder=(SpielerHolder)row.getTag();
			cbListCheck = (CheckBox)row.findViewById(R.id.rowCheckbox);
		    cbListCheck.setOnClickListener(new OnClickListener() {
		    	public void onClick(View v) {
		    		if (cbListCheck.isChecked()) {
		    			checkBoxState[position]=true;
		    			spielerZaehler = spielerZaehler + 1;
		    			if(spielerZaehler == 7) {
		    				uebertragen(spielerCount, halbzeitlaenge);
		    			}
		    		} else if (!cbListCheck.isChecked()) {
		    			checkBoxState[position]=false;
		    			spielerZaehler = spielerZaehler - 1;
		    		}
		    	}
		    });
			holder.populateFrom(c, helper);
		}

		@Override
		public View newView(Context ctxt, Cursor c, ViewGroup parent) {
			LayoutInflater inflater=getActivity().getLayoutInflater();
			View row=inflater.inflate(R.layout.row_player_checkbox, parent, false);
			SpielerHolder holder=new SpielerHolder(row);
			row.setTag(holder);
			return(row);
		}
	}
	
	static class SpielerHolder {
	    private TextView name=null;
	    private TextView nummer=null;
	    private CheckBox cbListCheck=null;
		    
	    SpielerHolder(View row) {
	      name=(TextView)row.findViewById(R.id.rowMannschaftName);
	      nummer=(TextView)row.findViewById(R.id.rowMannschaftNummer);
	      cbListCheck = (CheckBox)row.findViewById(R.id.rowCheckbox);
	    }
	    
	    void populateFrom(Cursor c, SQLHelper helper) {
	      int position = c.getPosition();
	      String spielerId=helper.getSpielerId(c);
	      name.setText(helper.getSpielerName(spielerId));
	      nummer.setText(helper.getSpielerNummer(spielerId));
	      cbListCheck.setChecked(checkBoxState[position]);
	    }
	}

/*
 * 
 * Zurück zu Spielübersicht und Ergebnis übertragen
 *
 */
	
	public void uebertragen(int spielerCount, int halbzeitlaenge) {
    	int zeitAufstellung;
    	model.moveToFirst();
    	for(int i=0;i<spielerCount;i++){
    		if(checkBoxState[i]==true){
    			String spielerId=helper.getSpielerId(model);
    			spielerString = helper.getSpielerName(spielerId);
    			spielerPosition = helper.getSpielerPosition(spielerId);
    			if(Integer.parseInt(zeit)<halbzeitlaenge){
    				zeitAufstellung = 0;
    			} else {
    				zeitAufstellung = halbzeitlaenge;
    			}
    			helper.insertTicker(7, getString(R.string.tickerAktionEinwechselung), Integer.parseInt(aktionTeamHeim), 
    								spielerString, Integer.parseInt(spielerId), Integer.parseInt(gameId), 
    								zeitAufstellung, realzeit);
    			if(spielerPosition.equals(getString(R.string.spielerPositionTorwart))){
        			if(Integer.parseInt(aktionTeamHeim)==1){
        				helper.updateSpielTorwartHeim(gameId, Integer.parseInt(gameId));
        			}
        			if(Integer.parseInt(aktionTeamHeim)==0){
        				helper.updateSpielTorwartAuswaerts(gameId, Integer.parseInt(gameId));
        			}
        		}
    		}
    		model.moveToNext();
    	}
    	finish();
	}
	
}