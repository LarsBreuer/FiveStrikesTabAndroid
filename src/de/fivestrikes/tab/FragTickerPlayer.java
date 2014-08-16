package de.fivestrikes.tab;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import java.text.DateFormat;
import java.util.Date;

import de.fivestrikes.tab.FragTickerList.TickerAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

public class FragTickerPlayer extends ListFragment {
	
	private View view;
	Cursor model=null;
	Cursor c=null;
	String gameId=null;
	String teamId=null;
	String teamHeimId=null;
	String teamAuswId=null;
	String teamHeimString=null;
	String teamAuswString=null;
    String aktionString=null;
    String aktionInt=null;
    String torwartAktionString=null;
    String torwartAktionInt=null;
    String aktionTeamHeim=null;
    String torwartAktionTeamHeim=null;
    String spielerId=null;
    String torwartId=null;
    String spielerString=null;
    String torwartString=null;
    String spielerPosition=null;
    String zeit=null;
    String realzeit=null;
    String tickerId=null;
    String torwartTickerId=null;
    Integer intSpielBallbesitz=null;
    String strTeamHeimKurzBySpielID=null;
    String strTeamAuswKurzBySpielID=null;
    String strSpielTorwartHeim=null;
    String strSpielTorwartAuswaerts=null;
    String strSpielSpielerWurfecke=null;
	View tabview;
    int zeitZurueck=0;
    int halbzeitlaenge=0;
	SQLHelper helper=null;
	FragTickerList fragTickerList;
	PlayerAdapter adapter=null;
	Button btnTeamHome;
	Button btnTeamAway;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
/* Grundlayout setzen */

		view = inflater.inflate(R.layout.frag_ticker_player, container, false);
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
	        teamHeimId=args.getString("TeamHomeID");
	        teamAuswId=args.getString("TeamAwayID");
	        if(aktionTeamHeim.equals("1")) { teamId=teamHeimId; }
	        if(aktionTeamHeim.equals("0")) { teamId=teamAuswId; }
		} 
		
/* Datenbank laden */
        
        helper=new SQLHelper(getActivity());
        refreshContent();
	    
 /* Button einrichten */
	    
        btnTeamHome=(Button) view.findViewById(R.id.home);
        btnTeamAway=(Button) view.findViewById(R.id.away);
        
/* Button beschriften */
        
        btnTeamHome.setText(helper.getTeamHeimKurzBySpielID(gameId));
        btnTeamAway.setText(helper.getTeamAuswKurzBySpielID(gameId));
		
/* Daten aus Datenbank laden */
        
        intSpielBallbesitz= Integer.parseInt(helper.getSpielBallbesitz(gameId));
        strSpielTorwartHeim = helper.getSpielTorwartHeim(gameId);
    	strSpielTorwartAuswaerts = helper.getSpielTorwartAuswaerts(gameId);
    	strSpielSpielerWurfecke = helper.getSpielSpielerWurfecke(gameId);
	    halbzeitlaenge=Integer.parseInt(helper.getSpielHalbzeitlaenge(gameId))*60*2000;
	    strTeamHeimKurzBySpielID = helper.getTeamHeimKurzBySpielID(gameId);
        strTeamAuswKurzBySpielID = helper.getTeamAuswKurzBySpielID(gameId);
        if(aktionTeamHeim.equals("1")){
        	btnTeamHome.setBackgroundResource(R.drawable.button_green);
        	btnTeamAway.setBackgroundResource(R.drawable.button_grey);
			if(strSpielTorwartAuswaerts!=null){
				torwartId=strSpielTorwartAuswaerts;  
				torwartString=helper.getSpielerName(torwartId);
			}
		}
		if(aktionTeamHeim.equals("0")){
        	btnTeamHome.setBackgroundResource(R.drawable.button_grey);
        	btnTeamAway.setBackgroundResource(R.drawable.button_green);
			if(strSpielTorwartHeim!=null){
				torwartId=strSpielTorwartHeim;    
				torwartString=helper.getSpielerName(torwartId);
			}
		}
		
/* Button Team Heim einrichten*/
        
		btnTeamHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	teamId=teamHeimId;
            	refreshContent();
            	btnTeamHome.setBackgroundResource(R.drawable.button_green);
            	btnTeamAway.setBackgroundResource(R.drawable.button_grey);
            	
            }
            
        });
		
/* Button Team Auswärts einrichten*/
        
		btnTeamAway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	teamId=teamAuswId;
            	refreshContent();
            	btnTeamHome.setBackgroundResource(R.drawable.button_grey);
            	btnTeamAway.setBackgroundResource(R.drawable.button_green);
            	
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
	
    public void refreshContent() {
    	
        model=helper.getAllSpieler(teamId);
        getActivity().startManagingCursor(model);
        adapter=new PlayerAdapter(model);
        setListAdapter(adapter);
    	 
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
 * Abfrage, welcher Spieler ausgewählt wurde 
 *
 */
	
	@Override
	public void onListItemClick(ListView list, View view,
            int position, long id) {
		
/* Spielernamen anhand Spieler ID erhalten und neuen Ticker einfügen */
		
		int maxZeit = helper.maxTickerZeit(gameId); 	// Zeit der letzten Spielaktion des Spiel ermitteln
		spielerId=String.valueOf(id);   
		spielerString=helper.getSpielerName(spielerId);
		spielerPosition=helper.getSpielerPosition(spielerId);
		helper.insertTicker(Integer.parseInt(aktionInt), aktionString, Integer.parseInt(aktionTeamHeim), spielerString, 
				Integer.parseInt(spielerId), Integer.parseInt(gameId), Integer.parseInt(zeit), realzeit);
		c=helper.getLastTickerId();
		c.moveToFirst();
		tickerId = helper.getTickerId(c);
		c.close();
		helper.updateSpielErgebnis(Integer.parseInt(gameId));
	    
/* Aktion Tor einfügen */
	    
    	if(Integer.parseInt(aktionInt)==2 || 
    			Integer.parseInt(aktionInt)==14 || 
    			Integer.parseInt(aktionInt)==20){
    		
    		/* Änderung des Ballbesitzes */
    		helper.changeBallbesitz(aktionTeamHeim, intSpielBallbesitz, strTeamHeimKurzBySpielID, strTeamAuswKurzBySpielID, 
    				gameId, zeit, realzeit);   		

   			/* Überprüfen, ob bei gegnerischer Mannschaft ein Torwart angegeben wurde. */
   			/* Falls ja, für den Torwart ein Gegentor eintragen. */
    		if(torwartId!=null){
				if(aktionInt.equals("2")){
					torwartAktionInt="17";
					torwartAktionString=getString(R.string.tickerAktionTorwartGegentor);
				}
				if(aktionInt.equals("14")){
					torwartAktionInt="19";
					torwartAktionString=getString(R.string.tickerAktionTorwart7mGegentor);
				}
				if(aktionInt.equals("20")){
					torwartAktionInt="23";
					torwartAktionString=getString(R.string.tickerAktionTorwartTGGegentor);
				}
				if(aktionTeamHeim.equals("0")){
					torwartAktionTeamHeim="1";
				} else {
					torwartAktionTeamHeim="0";
				}
				helper.insertTicker(Integer.parseInt(torwartAktionInt), torwartAktionString, Integer.parseInt(torwartAktionTeamHeim), torwartString, 
						Integer.parseInt(torwartId), Integer.parseInt(gameId), Integer.parseInt(zeit)-1, realzeit);
				Cursor lastTickTorwartC=helper.getLastTickerId();
				lastTickTorwartC.moveToFirst();
				torwartTickerId = helper.getTickerId(lastTickTorwartC);
				lastTickTorwartC.close();
    		} 

    		/* Spielstand in Tickereinträge schreiben falls Tor geworfen wurde */
   			/* Wenn es Tickereinträge nach dem aktuellen Eintrag gibt, ändere die Torfolge bei den Einträgen */
			String[] args={gameId};
			SQLiteDatabase db=helper.getWritableDatabase();
			Cursor cTicker=db.rawQuery("SELECT * FROM ticker WHERE spielID=? ORDER BY zeitInteger ASC", args);
   			cTicker.moveToFirst(); 
			String strErgebnis=null;
   			if(Integer.parseInt(zeit)<=maxZeit){	
   				int toreHeim=0;
   				int toreAusw=0;
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
   					if(Integer.parseInt(helper.getTickerZeitInt(cTicker))>=Integer.parseInt(zeit)){	// Wenn die Zeit der Schleifeneintrags größer ist als der aktuelle Eintrag, dann hat sich die Torfolge geändert und der Eintrag muss geändert werden
   						strErgebnis=String.valueOf(toreHeim)+":"+String.valueOf(toreAusw);
   						helper.updateTickerErgebnis(helper.getTickerId(cTicker), toreHeim, toreAusw, strErgebnis);
   					}
   				}
   			} else {
   				strErgebnis=String.valueOf(helper.countTickerTore(gameId, "1", "9999999"))+
   							":"+
   							String.valueOf(helper.countTickerTore(gameId, "0", "9999999"));
   				helper.updateTickerErgebnis(tickerId, 
   											helper.countTickerTore(gameId, "1", "9999999"), 
   											helper.countTickerTore(gameId, "0", "9999999"), 
   											strErgebnis);
   			}
   			cTicker.close();
   			
   			if(strSpielSpielerWurfecke!=null){
   				if(strSpielSpielerWurfecke.equals("1")){
   					
   					Bundle goalArgs = new Bundle();
   					goalArgs.putString("TickerID", tickerId);
   					if(torwartTickerId!=null){
   						goalArgs.putString("TickerTorwartID", torwartTickerId);
   					}
   					FragmentManager fragmentManager = getFragmentManager();
   					FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
   			        FragTickerPlayerGoalArea fragment = new FragTickerPlayerGoalArea();
   			        fragment.setArguments(goalArgs);
   			        fragmentTransaction.replace(R.id.frag_ticker_player, fragment);
   			        fragmentTransaction.commit();

   				}
   			}	
    	}
    	
/* Aktion Fehlwurf einfügen */
    	
    	if(Integer.parseInt(aktionInt)== 3 || 
    			Integer.parseInt(aktionInt)==15 || 
    			Integer.parseInt(aktionInt)==21){
    		if(strSpielSpielerWurfecke!=null){
   				if(strSpielSpielerWurfecke.equals("1")){
   					
   					Bundle goalArgs = new Bundle();
   					goalArgs.putString("TickerID", tickerId);
   					goalArgs.putString("StrAktionInt", aktionInt);
   					goalArgs.putString("GameID", gameId);
   					goalArgs.putString("AktionTeamHome", aktionTeamHeim);
   					goalArgs.putString("Time", zeit);
   					goalArgs.putString("RealTime", realzeit);
   					FragmentManager fragmentManager = getFragmentManager();
   					FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
   			        FragTickerPlayerMiss fragment = new FragTickerPlayerMiss();
   			        fragment.setArguments(goalArgs);
   			        fragmentTransaction.replace(R.id.frag_ticker_player, fragment);
   			        fragmentTransaction.commit();
    			
   				} else {
   		    		AlertDialog.Builder tfBuilder = new AlertDialog.Builder(getActivity());
   		   			tfBuilder
   		   			.setTitle(R.string.tickerMSGBoxAktionTitel)
   		   			.setMessage(R.string.tickerMSGBoxAktionNachricht)
   		   			.setIcon(android.R.drawable.ic_dialog_alert)
   		   			.setPositiveButton(R.string.tickerMSGBoxJa, new DialogInterface.OnClickListener() {
   		   				public void onClick(DialogInterface dialog, int which) {			      	
   		   					/* Änderung des Ballbesitzes */
   		   					helper.changeBallbesitz(aktionTeamHeim, intSpielBallbesitz, strTeamHeimKurzBySpielID, strTeamAuswKurzBySpielID, 
   		   	    				gameId, zeit, realzeit); 
   		   				}
   		   			})
   		   			.setNegativeButton(R.string.tickerMSGBoxNein, new DialogInterface.OnClickListener() {
   		   				public void onClick(DialogInterface dialog, int which) {

   		   				}
   		   			})
   		   			.show();
   				}
    		}
    	}
    	
/* Technischer Fehler einfügen */
    	
    	if(Integer.parseInt(aktionInt)== 4){ 
    		AlertDialog.Builder tfBuilder = new AlertDialog.Builder(getActivity());
   			tfBuilder
   			.setTitle(R.string.tickerMSGBoxAktionTitel)
   			.setMessage(R.string.tickerMSGBoxAktionNachricht)
   			.setIcon(android.R.drawable.ic_dialog_alert)
   			.setPositiveButton(R.string.tickerMSGBoxJa, new DialogInterface.OnClickListener() {
   				public void onClick(DialogInterface dialog, int which) {	
   					
   					/* Änderung des Ballbesitzes */
   		    		helper.changeBallbesitz(aktionTeamHeim, intSpielBallbesitz, strTeamHeimKurzBySpielID, strTeamAuswKurzBySpielID, 
   		    				gameId, zeit, realzeit);
   		    		finish();
   		    		
   				}
   			})
   			.setNegativeButton(R.string.tickerMSGBoxNein, new DialogInterface.OnClickListener() {
   				public void onClick(DialogInterface dialog, int which) {
   					
   					finish();

   				}
   			})
   			.show();
    	}
    	
/* Zeitstrafen einfügen */
    	
    	if(Integer.parseInt(aktionInt)== 5){
    	    zeitZurueck=(int)Integer.parseInt(zeit)+2*60000;
    	    /** Hinweis: Nach Spiellänge 2 Minuten auf Spiellänge setzen */
    	    if(zeitZurueck>halbzeitlaenge){
    	    	zeitZurueck=halbzeitlaenge;
    	    }
    	    helper.insertTicker(10, spielerString+" "+getString(R.string.tickerAktionZurueck), Integer.parseInt(aktionTeamHeim), 
    	    		spielerString, Integer.parseInt(spielerId), Integer.parseInt(gameId), zeitZurueck, realzeit);
    	    finish();
    	}
    	
/* Einwechselung einfügen */
    	
    	if(Integer.parseInt(aktionInt)== 7){		// Bei Einwechselung auf den Auswechselungs-Bildschirm springen
    		/* Torwart eintragen, falls ein Torwart ausgewählt wurde */
    		if(spielerPosition.equals(getString(R.string.spielerPositionTorwart))){
    			if(Integer.parseInt(aktionTeamHeim)==1){
    				helper.updateSpielTorwartHeim(gameId, Integer.parseInt(spielerId));
    			}
    			if(Integer.parseInt(aktionTeamHeim)==0){
    				helper.updateSpielTorwartAuswaerts(gameId, Integer.parseInt(spielerId));
    			}
    		}

    		/* Activity Auswechselung aufrufen */
    		fragTickerList.refreshContent();
    		Bundle goalArgs = new Bundle();
    		goalArgs.putString("TeamID", teamId);
			goalArgs.putString("GameID", gameId);
			goalArgs.putString("AktionTeamHome", aktionTeamHeim);
			goalArgs.putString("Time", zeit);
			goalArgs.putString("RealTime", realzeit);
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		    FragTickerPlayerChange fragment = new FragTickerPlayerChange();
		    fragment.setArguments(goalArgs);
		    fragmentTransaction.replace(R.id.frag_ticker_player, fragment);
		    fragmentTransaction.commit();
    			
    	}
    	
/* Zeitstrafen einfügen */
    	
    	if(Integer.parseInt(aktionInt)== 9){
    	    zeitZurueck=(int)Integer.parseInt(zeit)+2*60000;
    	    /** Hinweis: Nach Spiellänge 2 Minuten auf Spiellänge setzen */
    	    helper.insertTicker(10, spielerString+" "+getString(R.string.tickerAktionZurueck), Integer.parseInt(aktionTeamHeim), 
    	    		spielerString, Integer.parseInt(spielerId), Integer.parseInt(gameId), zeitZurueck, realzeit);
    	    finish();
    	}
    	
/* Bei Zeitstrafen eintragen, wann Spieler zurück */
    	
    	if(Integer.parseInt(aktionInt)== 11){
    	    zeitZurueck=(int)Integer.parseInt(zeit)+4*60000;
    	    /** Hinweis: Nach Spiellänge 2 Minuten auf Spiellänge setzen */
    	    helper.insertTicker(10, spielerString+" "+getString(R.string.tickerAktionZurueck), Integer.parseInt(aktionTeamHeim), 
    	    		spielerString, Integer.parseInt(spielerId), Integer.parseInt(gameId), zeitZurueck, realzeit);
    	    finish();
    	}
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