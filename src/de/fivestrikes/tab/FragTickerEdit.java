package de.fivestrikes.tab;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;


public class FragTickerEdit extends Fragment {
	
	private View view;
	SQLHelper helper=null;
	String tickerId=null;
	String gameId=null;
	String tickerZeitString=null;
	String tickerAktionString=null;
	String tickerAktionId=null;
	String tickerSpielerString=null;
	String tickerSpielerId=null;
	String tickerWurfecke=null;
	String tickerPosition=null;
	int aktionAnfangID=0;
	private long tickerZeitLng=0;
	Context context = getActivity();
	FragTickerList fragTickerList;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
/* Grundlayout setzen */

		view = inflater.inflate(R.layout.frag_ticker_edit, container, false);
		fragTickerList = (FragTickerList) getActivity().getSupportFragmentManager().findFragmentById(R.id.frag_ticker_list);
		
/* Daten aus Activity laden */ 
        
		Bundle args = getArguments();
		if(args!=null){
			gameId = args.getString("GameID");
			tickerId = args.getString("TickerID");
			tickerAktionString = args.getString("AktionName");
			tickerAktionId = args.getString("AktionId");			
			tickerSpielerString = args.getString("spielerName");
			tickerSpielerId = args.getString("spielerId");			
			tickerWurfecke = args.getString("Wurfecke");
			tickerPosition = args.getString("Wurfposition");			
		}
		
		if(tickerAktionId!=null){
			if(tickerAktionId.equals("14") || tickerAktionId.equals("15") || 
				tickerAktionId.equals("18") || tickerAktionId.equals("19")){
				tickerPosition="3_3";
				((TextView)view.findViewById(R.id.btnEditWurfposition)).setText(R.string.wurfpositionM);
			}
		}

/* Datenbank laden */
	       
        helper=new SQLHelper(getActivity());

/* Daten aus Datenbank laden */
        
        if(tickerZeitString==null){										// Wurde die Editierfunktion neu aufgerufen?
        	Cursor c=helper.getTickerCursor(tickerId);
        	c.moveToFirst();
        	tickerZeitLng=Integer.parseInt(helper.getTickerZeitInt(c));
        	tickerZeitString=helper.updateTimer(tickerZeitLng);
        	((TextView)view.findViewById(R.id.btnEditSpielzeit)).setText(tickerZeitString);
        	tickerAktionString=helper.getTickerAktion(c);
        	tickerAktionId=helper.getTickerAktionInt(c);
        	((TextView)view.findViewById(R.id.btnEditAktion)).setText(tickerAktionString);
        	tickerSpielerString=helper.getTickerSpieler(c);
        	tickerSpielerId=helper.getTickerSpielerId(c);
        	((TextView)view.findViewById(R.id.btnEditSpieler)).setText(tickerSpielerString);
        	aktionAnfangID=Integer.parseInt(helper.getTickerAktionInt(c));
        	tickerWurfecke=helper.getTickerWurfecke(c);
        	tickerPosition=helper.getTickerPosition(c);
        	c.close();
        }

/* Button definieren und beschriften */
	    
	    Button save=(Button)view.findViewById(R.id.save);
	    Button delete=(Button)view.findViewById(R.id.delete);
	    Button btnZeit = (Button) view.findViewById(R.id.btnEditSpielzeit);
	    Button btnAktion = (Button) view.findViewById(R.id.btnEditAktion);
	    Button btnSpieler = (Button) view.findViewById(R.id.btnEditSpieler);
	    Button btnWurfecke = (Button) view.findViewById(R.id.btnEditWurfecke);
	    Button btnPosition = (Button) view.findViewById(R.id.btnEditWurfposition);
		if(tickerWurfecke!=null){
			btnWurfeckeBeschriften();
		} else {
			tickerWurfecke="";
			btnWurfecke.setText(tickerWurfecke);
		}
		if(tickerPosition!=null){
			btnPositionBeschriften();
		} else {
			tickerPosition="";
			btnPosition.setText(tickerPosition);
		}
        
        save.setOnClickListener(onSave);
	    delete.setOnClickListener(onDelete);
        
        /* Button Spielzeit */
        btnZeit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            	// get prompts.xml view
				LayoutInflater li = LayoutInflater.from(context);
				View promptsView = li.inflate(R.layout.ticker_set_watch, null);
            	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

				// set prompts.xml to alertdialog builder
				alertDialogBuilder.setView(promptsView);

				final EditText editMinutes = (EditText) promptsView.findViewById(R.id.setStopWatchMinutes);
				final EditText editSeconds = (EditText) promptsView.findViewById(R.id.setStopWatchSeconds);

				// set dialog message
				alertDialogBuilder
						.setCancelable(false)
						.setPositiveButton(R.string.tickerMSGBoxEinstellen,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										int min=0;
										int sec=0;
										if(editMinutes.getText().length() != 0){
											min=Integer.parseInt(editMinutes.getText().toString());
										}
										else{
											min=0;
										}
										if(editSeconds.getText().length() != 0){
											sec=Integer.parseInt(editSeconds.getText().toString());
										}
										else{
											sec=0;
										}
										if (sec>59) {
											sec=59;
										}
										tickerZeitLng=(min*60000)+(sec*1000);
										tickerZeitString=helper.updateTimer(tickerZeitLng);
										((TextView)view.findViewById(R.id.btnEditSpielzeit)).setText(tickerZeitString);
									}
								});

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();
            	
            }
            
        });
        
        /* Button Aktion */
        btnAktion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	Cursor c=helper.getTickerCursor(tickerId);
            	c.moveToFirst();
            	if(Integer.parseInt(helper.getTickerAktionInt(c))==1 || 
            			Integer.parseInt(helper.getTickerAktionInt(c))==0){
        	    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        	    	alertDialogBuilder
        	    		.setTitle(R.string.tickerEditMsgboxTitel)
        	    		.setMessage(R.string.tickerEditMsgboxText)
        	    		.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
        					public void onClick(DialogInterface dialog,int id) {

        					}
        	    		});
        	    	AlertDialog alertDialog = alertDialogBuilder.create();
        	    	alertDialog.show();
            	}
            	else{
            		// Intent newIntent = new Intent(getApplicationContext(), TickerEditAktionActivity.class);
    				// startActivityForResult(newIntent, GET_CODE);
            	}
				c.close();
            }
        });
        
        /* Button Spieler */
        btnSpieler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				//Intent newIntent = new Intent(getApplicationContext(), TickerEditSpielerActivity.class);
				Cursor cTicker=helper.getTickerCursor(tickerId);
				cTicker.moveToFirst();
            	if(Integer.parseInt(helper.getTickerAktionInt(cTicker))==1 || 
            			Integer.parseInt(helper.getTickerAktionInt(cTicker))==0){
        	    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        	    	alertDialogBuilder
        	    		.setTitle(R.string.tickerEditMsgboxTitel)
        	    		.setMessage(R.string.tickerEditMsgboxText)
        	    		.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
        					public void onClick(DialogInterface dialog,int id) {

        					}
        	    		});
        	    	AlertDialog alertDialog = alertDialogBuilder.create();
        	    	alertDialog.show();
            	}
            	else{
            		if(Integer.parseInt(helper.getTickerAktionTeamHeim(cTicker))==1){
            			//newIntent.putExtra("TeamID", helper.getSpielHeim(spielId));
            		}
            		else{
            			//newIntent.putExtra("TeamID", helper.getSpielAusw(spielId));
            		}
    				//startActivityForResult(newIntent, GET_CODE);
            	}
				cTicker.close();
            }
        });
        
        /* Button Wurfecke */
        btnWurfecke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				Cursor cTicker=helper.getTickerCursor(tickerId);
				cTicker.moveToFirst();
            	if(Integer.parseInt(tickerAktionId)==1 || 
            			Integer.parseInt(tickerAktionId)==0 || 
            			Integer.parseInt(tickerAktionId)==4 || 
            			Integer.parseInt(tickerAktionId)==5 || 
            			Integer.parseInt(tickerAktionId)==6 || 
            			Integer.parseInt(tickerAktionId)==7 || 
            			Integer.parseInt(tickerAktionId)==8 || 
            			Integer.parseInt(tickerAktionId)==9 || 
            			Integer.parseInt(tickerAktionId)==11 || 
            			Integer.parseInt(tickerAktionId)==24){
            		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        	    	alertDialogBuilder
        	    		.setTitle(R.string.tickerEditWurfeckeMsgboxTitel)
        	    		.setMessage(R.string.tickerEditWurfeckeMsgboxText)
        	    		.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
        	    			public void onClick(DialogInterface dialog,int id) {

        	    			}
        	    		});
        	    	AlertDialog alertDialog = alertDialogBuilder.create();
        	    	alertDialog.show();
            	}
            	else{
            		if(Integer.parseInt(tickerAktionId)==3 || 
                			Integer.parseInt(tickerAktionId)==15 || 
                			Integer.parseInt(tickerAktionId)==21){
            			// Intent newIntent = new Intent(getApplicationContext(), TickerEditFehlwurfActivity.class);
            			// newIntent.putExtra("Wurfecke", tickerWurfecke);
            			// startActivityForResult(newIntent, GET_CODE);
            		} else {
            			// Intent newIntent = new Intent(getApplicationContext(), TickerEditWurfeckeActivity.class);
            			// newIntent.putExtra("Wurfecke", tickerWurfecke);
            			// startActivityForResult(newIntent, GET_CODE);
            		}
            	}
				cTicker.close();
            }
        });
        
        /* Button Wurfposition */
        btnPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				// Intent newIntent = new Intent(getApplicationContext(), TickerEditWurfpositionActivity.class);
				Cursor cTicker=helper.getTickerCursor(tickerId);
				cTicker.moveToFirst();
            	if(Integer.parseInt(tickerAktionId)==1 || 
            			Integer.parseInt(tickerAktionId)==0 || 
            			Integer.parseInt(tickerAktionId)==4 || 
            			Integer.parseInt(tickerAktionId)==5 || 
            			Integer.parseInt(tickerAktionId)==6 || 
            			Integer.parseInt(tickerAktionId)==7 || 
            			Integer.parseInt(tickerAktionId)==8 || 
            			Integer.parseInt(tickerAktionId)==9 || 
            			Integer.parseInt(tickerAktionId)==11 || 
            			Integer.parseInt(tickerAktionId)==24){
            		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        	    	alertDialogBuilder
        	    		.setTitle(R.string.tickerEditWurfeckeMsgboxTitel)
        	    		.setMessage(R.string.tickerEditWurfeckeMsgboxText)
        	    		.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
        	    			public void onClick(DialogInterface dialog,int id) {

        	    			}
        	    		});
        	    	AlertDialog alertDialog = alertDialogBuilder.create();
        	    	alertDialog.show();
            	}
            	else{
            		// newIntent.putExtra("Position", tickerPosition);
    				// startActivityForResult(newIntent, GET_CODE);
            	}
				cTicker.close();
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

/*
 * 
 * Tickerdaten speichern und zurück zur Spielübersicht
 *
 */
	
	private View.OnClickListener onSave=new View.OnClickListener() {
		public void onClick(View v) {
			
		    helper.updateTickerEdit(gameId, tickerId, tickerZeitString, (int) tickerZeitLng, tickerAktionString, tickerAktionId, tickerSpielerString, 
		    						tickerSpielerId, tickerWurfecke, tickerPosition);
		    // TODO: Brauche ich Long bei tickerZeit?
		    
		    /* Beschriftung der Ticker ändern, falls Aktion Ticker vor oder nach der Änderung ein Tor war */
		    if(aktionAnfangID==2 || Integer.parseInt(tickerAktionId)==2 || 
		    		aktionAnfangID==14 || Integer.parseInt(tickerAktionId)==14 || 
		    		aktionAnfangID==20 || Integer.parseInt(tickerAktionId)==20){
    			String[] args={gameId};
    			SQLiteDatabase db=helper.getWritableDatabase();
    			Cursor cTicker=db.rawQuery("SELECT * FROM ticker WHERE spielID=? ORDER BY zeitInteger ASC", args);
    	    	cTicker.moveToFirst();
    	    	int toreHeim=0;
    	    	int toreAusw=0;
    	    	String strErgebnis=null;
    	    	for (cTicker.moveToFirst(); !cTicker.isAfterLast(); cTicker.moveToNext()) {
    	    		if(Integer.parseInt(helper.getTickerAktionInt(cTicker))==2 || 
    	    				Integer.parseInt(helper.getTickerAktionInt(cTicker))==14 || 
    	    				Integer.parseInt(helper.getTickerAktionInt(cTicker))==20){
    	    			if(Integer.parseInt(helper.getTickerAktionTeamHeim(cTicker))==1){
    	    				toreHeim=toreHeim+1;
    	    			}
    	    			if(Integer.parseInt(helper.getTickerAktionTeamHeim(cTicker))==0){
    	    				toreAusw=toreAusw+1;
    	    			}
    	    		}
    	    		if(Integer.parseInt(helper.getTickerZeitInt(cTicker))>=(int) tickerZeitLng){	// Wenn die Zeit der Schleifeneintrags größer ist als der aktuelle Eintrag, dann hat sich die Torfolge geändert und der Eintrag muss geändert werden
    	    			strErgebnis=String.valueOf(toreHeim)+":"+String.valueOf(toreAusw);
    	    			helper.updateTickerErgebnis(helper.getTickerId(cTicker), toreHeim, toreAusw, strErgebnis);
    	    		}
    	    	}
    	    	cTicker.close();
    	    	// TODO: Eventuell in eigene Funktion ausgliedern
		    }
			/* Ticker aktualisieren und Fragment schließen */
	    	fragTickerList.refreshContent();
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
	        FragTickerAction fragment = new FragTickerAction();
	        fragmentTransaction.replace(R.id.frag_ticker_action, fragment);
	        fragmentTransaction.commit();
		}
	};

/*
 * 
 * Ticker löschen und zurück zur Spielerübersicht
 *
 */
	
	private View.OnClickListener onDelete=new View.OnClickListener() {
		public void onClick(View v) {
			
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder
			.setTitle(R.string.tickerDelMsgboxTitel)
			.setMessage(R.string.tickerDelMsgboxText)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton(R.string.tickerMSGBoxJa, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {			      	
					helper.deleteTicker(gameId, tickerId);
					
				    /* Beschriftung der Ticker ändern, falls Aktion Ticker vor oder nach der Änderung ein Tor war */
				    if(aktionAnfangID==2 || Integer.parseInt(tickerAktionId)==2 ||
				    		aktionAnfangID==14 || Integer.parseInt(tickerAktionId)==14 || 
				    		aktionAnfangID==20 || Integer.parseInt(tickerAktionId)==20){
		    			String[] args={gameId};
		    			SQLiteDatabase db=helper.getWritableDatabase();
		    			Cursor cTicker=db.rawQuery("SELECT * FROM ticker WHERE spielID=? ORDER BY zeitInteger ASC", args);
		    	    	cTicker.moveToFirst();
		    	    	int toreHeim=0;
		    	    	int toreAusw=0;
		    	    	String strErgebnis=null;
		    	    	for (cTicker.moveToFirst(); !cTicker.isAfterLast(); cTicker.moveToNext()) {
		    	    		if(Integer.parseInt(helper.getTickerAktionInt(cTicker))==2 || 
		    	    				Integer.parseInt(helper.getTickerAktionInt(cTicker))==14 || 
		    	    				Integer.parseInt(helper.getTickerAktionInt(cTicker))==20){
		    	    			if(Integer.parseInt(helper.getTickerAktionTeamHeim(cTicker))==1){
		    	    				toreHeim=toreHeim+1;
		    	    			}
		    	    			if(Integer.parseInt(helper.getTickerAktionTeamHeim(cTicker))==0){
		    	    				toreAusw=toreAusw+1;
		    	    			}
		    	    		}
		    				strErgebnis=String.valueOf(toreHeim)+":"+String.valueOf(toreAusw);
		    				helper.updateTickerErgebnis(helper.getTickerId(cTicker), toreHeim, toreAusw, strErgebnis);
		    	    	}
		    	    	cTicker.close();
		    	    	/** Hinweis: Eventuell in eigene Funktion ausgliedern */
				    }
				    
					/* Ticker aktualisieren und Fragment schließen */
			    	fragTickerList.refreshContent();
					FragmentManager fragmentManager = getFragmentManager();
					FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			        FragTickerAction fragment = new FragTickerAction();
			        fragmentTransaction.replace(R.id.frag_ticker_action, fragment);
			        fragmentTransaction.commit();
				}
			})
			.setNegativeButton(R.string.tickerMSGBoxNein, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {			      	
					
				}
			})
			.show();
		}
	};

/*
 * 
 * Button Wurfecke beschriften
 *
 */
	
	public void btnWurfeckeBeschriften() {

		if(tickerWurfecke.equals("OOLL")){
			((Button)view.findViewById(R.id.btnEditWurfecke)).setText(R.string.wurfeckeOOLL);
		}
		if(tickerWurfecke.equals("OOL")){
			((Button)view.findViewById(R.id.btnEditWurfecke)).setText(R.string.wurfeckeOOL);
		}
		if(tickerWurfecke.equals("OOM")){
			((Button)view.findViewById(R.id.btnEditWurfecke)).setText(R.string.wurfeckeOOM);
		}
		if(tickerWurfecke.equals("OOR")){
			((Button)view.findViewById(R.id.btnEditWurfecke)).setText(R.string.wurfeckeOOR);
		}
		if(tickerWurfecke.equals("OORR")){
			((Button)view.findViewById(R.id.btnEditWurfecke)).setText(R.string.wurfeckeOORR);
		}
		if(tickerWurfecke.equals("OLL")){
			((Button)view.findViewById(R.id.btnEditWurfecke)).setText(R.string.wurfeckeOLL);
		}
		if(tickerWurfecke.equals("OL")){
			((Button)view.findViewById(R.id.btnEditWurfecke)).setText(R.string.wurfeckeOL);
		}
		if(tickerWurfecke.equals("OM")){
			((Button)view.findViewById(R.id.btnEditWurfecke)).setText(R.string.wurfeckeOM);
		}
		if(tickerWurfecke.equals("OR")){
			((Button)view.findViewById(R.id.btnEditWurfecke)).setText(R.string.wurfeckeOR);
		}
		if(tickerWurfecke.equals("ORR")){
			((Button)view.findViewById(R.id.btnEditWurfecke)).setText(R.string.wurfeckeORR);
		}
		if(tickerWurfecke.equals("MLL")){
			((Button)view.findViewById(R.id.btnEditWurfecke)).setText(R.string.wurfeckeMLL);
		}
		if(tickerWurfecke.equals("ML")){
			((Button)view.findViewById(R.id.btnEditWurfecke)).setText(R.string.wurfeckeML);
		}
		if(tickerWurfecke.equals("MM")){
			((Button)view.findViewById(R.id.btnEditWurfecke)).setText(R.string.wurfeckeMM);
		}
		if(tickerWurfecke.equals("MR")){
			((Button)view.findViewById(R.id.btnEditWurfecke)).setText(R.string.wurfeckeMR);
		}
		if(tickerWurfecke.equals("MRR")){
			((Button)view.findViewById(R.id.btnEditWurfecke)).setText(R.string.wurfeckeMRR);
		}
		if(tickerWurfecke.equals("ULL")){
			((Button)view.findViewById(R.id.btnEditWurfecke)).setText(R.string.wurfeckeULL);
		}
		if(tickerWurfecke.equals("UL")){
			((Button)view.findViewById(R.id.btnEditWurfecke)).setText(R.string.wurfeckeUL);
		}
		if(tickerWurfecke.equals("UM")){
			((Button)view.findViewById(R.id.btnEditWurfecke)).setText(R.string.wurfeckeUM);
		}
		if(tickerWurfecke.equals("UR")){
			((Button)view.findViewById(R.id.btnEditWurfecke)).setText(R.string.wurfeckeUR);
		}
		if(tickerWurfecke.equals("URR")){
			((Button)view.findViewById(R.id.btnEditWurfecke)).setText(R.string.wurfeckeURR);
		}
	}



/*
 * 
 * Button Wurfposition beschriften
 *
 */
	
	public void btnPositionBeschriften() {

		if(tickerPosition.equals("1_1") || tickerPosition.equals("1_2") || tickerPosition.equals("2_1")){
			((Button)view.findViewById(R.id.btnEditWurfposition)).setText(R.string.wurfpositionLA);
		}
		if(tickerPosition.equals("2_2") || tickerPosition.equals("2_3")){
			((Button)view.findViewById(R.id.btnEditWurfposition)).setText(R.string.wurfpositionHL);
		}
		if(tickerPosition.equals("1_3") || tickerPosition.equals("1_4") || tickerPosition.equals("2_4")){
			((Button)view.findViewById(R.id.btnEditWurfposition)).setText(R.string.wurfpositionRHL);
		}
		if(tickerPosition.equals("3_2") || tickerPosition.equals("3_3") || tickerPosition.equals("3_4")){
			((Button)view.findViewById(R.id.btnEditWurfposition)).setText(R.string.wurfpositionM);
		}
		if(tickerPosition.equals("4_2") || tickerPosition.equals("4_3")){
			((Button)view.findViewById(R.id.btnEditWurfposition)).setText(R.string.wurfpositionHR);
		}
		if(tickerPosition.equals("4_4") || tickerPosition.equals("5_3") || tickerPosition.equals("5_4")){
			((Button)view.findViewById(R.id.btnEditWurfposition)).setText(R.string.wurfpositionRHR);
		}
		if(tickerPosition.equals("4_1") || tickerPosition.equals("5_1") || tickerPosition.equals("5_2")){
			((Button)view.findViewById(R.id.btnEditWurfposition)).setText(R.string.wurfpositionRA);
		}
	}
}