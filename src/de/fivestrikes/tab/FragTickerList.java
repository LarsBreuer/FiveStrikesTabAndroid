package de.fivestrikes.tab;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import java.text.DateFormat;
import java.util.Date;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FragTickerList extends ListFragment {
	
	private View view;
	public static Long elapsedTime=Long.parseLong("0");
	Cursor model=null;
	SQLHelper helper=null;
	TickerAdapter adapter=null;
	String gameId=null;
	String teamId=null;
	String strBallbesitz=null;
	String realzeit=null;
	private long zeitStart=0;
	private Handler mHandler = new Handler();
	private long startTime;
	private final int REFRESH_RATE = 100;
	private boolean stopped = false;
	String teamKurz=null;
	final Context context = getActivity();
	Cursor c=null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.frag_ticker_list, container, false);
		
/* Daten aus Activity laden */ 
        
		Bundle args = getArguments();
		if(args!=null){
			gameId = args.getString("GameID");
		} 
		
		/* Datenbank laden */
	       
        helper=new SQLHelper(getActivity());
        refreshContent();
	    
 /* Button einrichten */
	    
        Button btnTeamHeim=(Button) view.findViewById(R.id.btn_heim);
        Button btnTeamAusw=(Button) view.findViewById(R.id.btn_auswaerts);
        Button btnToreHeim=(Button) view.findViewById(R.id.btn_tore_heim);
        Button btnToreAusw=(Button) view.findViewById(R.id.btn_tore_auswaerts);
        Button btn_uhr=(Button) view.findViewById(R.id.btn_uhr);
        
/* Tore eingeben */
        
        btnToreHeim.setText(String.valueOf(helper.countTickerTore(gameId, "1", "9999999")));
        btnToreAusw.setText(String.valueOf(helper.countTickerTore(gameId, "0", "9999999")));
    		
/* Button beschriften */
        
		btnTeamHeim.setText(helper.getTeamHeimKurzBySpielID(gameId));
		btnTeamAusw.setText(helper.getTeamAuswKurzBySpielID(gameId));

/* Daten aus Datenbank laden */
        
		teamKurz=helper.getTeamHeimKurzBySpielID(gameId);
		strBallbesitz=helper.getSpielBallbesitz(gameId);
		
/* Zeit stellen */
		
		if (helper.getSpielZeitStart(gameId)!=null) zeitStart=Long.parseLong(helper.getSpielZeitStart(gameId));
		
		if (helper.getSpielZeitBisher(gameId)!=null){
			elapsedTime=Long.parseLong(helper.getSpielZeitBisher(gameId));
		} else {
			elapsedTime=Long.parseLong("0");
		}
		if(zeitStart==0){		// wenn Zeit beim letzten Verlassen gestoppt war
    		((TextView)view.findViewById(R.id.btn_uhr)).setText(helper.updateTimer(elapsedTime));
    		stopped = true;
    		btn_uhr.setBackgroundResource(R.drawable.button_timer_red);
    	}
    	else{
    		elapsedTime = elapsedTime + System.currentTimeMillis() - zeitStart;
    		startTime = System.currentTimeMillis() - elapsedTime; 
        	mHandler.removeCallbacks(startTimer);
            mHandler.postDelayed(startTimer, 0);
            stopped = false;
            ((Button)view.findViewById(R.id.btn_uhr)).setBackgroundResource(R.drawable.button_timer_green);
    	}
    	
/* Button Ballbesitz stellen */
		
    	switch(Integer.parseInt(strBallbesitz)){
			case 1:
				btnTeamHeim.setBackgroundResource(R.drawable.button_team_blue_active);
				btnTeamAusw.setBackgroundResource(R.drawable.button_team_red_inactive);
				break;
			case 0:
				btnTeamAusw.setBackgroundResource(R.drawable.button_team_red_active);
				btnTeamHeim.setBackgroundResource(R.drawable.button_team_blue_inactive);
				break;
			case 2:
				btnTeamHeim.setBackgroundResource(R.drawable.button_team_blue_inactive);
				btnTeamAusw.setBackgroundResource(R.drawable.button_team_red_inactive);
				break;
    	}
		
        
/* Button Team Heim einrichten*/
        
        btnTeamHeim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Button btnTeamHeim=(Button) view.findViewById(R.id.btn_heim);
                Button btnTeamAusw=(Button) view.findViewById(R.id.btn_auswaerts);
                Resources res = getResources(); 
        		if (Integer.parseInt(strBallbesitz)!=1){
        			btnTeamHeim.setBackgroundResource(R.drawable.button_team_blue_active);
        			btnTeamAusw.setBackgroundResource(R.drawable.button_team_red_inactive);
        			String strBallbesitzText="Ballbesitz " + teamKurz;
        			realzeit = DateFormat.getDateTimeInstance().format(new Date());
        			helper.insertTicker(0, strBallbesitzText, 1, "", 0, Integer.parseInt(gameId), (int) (long) elapsedTime, realzeit);
        			helper.updateSpielBallbesitz(gameId, 1);  // aktuellen Ballbesitz in Spiel eintragen
					strBallbesitz="1";
        		}

        		refreshContent();
        		
            }
        });
        
/* Button Team Auswärts einrichten*/
        
        btnTeamAusw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Button btnTeamHeim=(Button) view.findViewById(R.id.btn_heim);
                Button btnTeamAusw=(Button) view.findViewById(R.id.btn_auswaerts);
                Resources res = getResources(); 
        		if (Integer.parseInt(strBallbesitz)!=0){
        			btnTeamHeim.setBackgroundResource(R.drawable.button_team_blue_inactive);
        			btnTeamAusw.setBackgroundResource(R.drawable.button_team_red_active);
    				String strBallbesitzText="Ballbesitz " + teamKurz;
    				realzeit = DateFormat.getDateTimeInstance().format(new Date());
    				helper.insertTicker(1, strBallbesitzText, 0, "", 0, Integer.parseInt(gameId), (int) (long) elapsedTime, realzeit);
    				helper.updateSpielBallbesitz(gameId, 0);  // aktuellen Ballbesitz in Spiel eintragen
					strBallbesitz="0";

					refreshContent();
					
        		}				
            }
        });
        
/* Uhr stellen */
        
        btn_uhr.setOnLongClickListener(new OnLongClickListener() { 
            @Override
            public boolean onLongClick(View v) {
				// Stopuhr stoppen 
        		startTime = System.currentTimeMillis();
            	mHandler.removeCallbacks(startTimer);
            	stopped = true;
            	((Button)view.findViewById(R.id.btn_uhr)).setBackgroundResource(R.drawable.button_timer_red);
            	helper.updateSpielTicker(gameId, elapsedTime, zeitStart);
            	
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
								Resources res = getResources();
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
								elapsedTime=(long) (min*60000)+(sec*1000);
								((TextView)view.findViewById(R.id.btn_uhr)).setText(helper.updateTimer(elapsedTime));
						        /* Schreibe in die Datenbank, in welcher Halbzeit man sich befindet */
						        if(min<Integer.parseInt(helper.getSpielHalbzeitlaenge(gameId))){
						        	helper.updateSpielAktuelleHalbzeit(gameId, 0);
						        }
						        if(min<=Integer.parseInt(helper.getSpielHalbzeitlaenge(gameId))*2 && min>=30){
						        	helper.updateSpielAktuelleHalbzeit(gameId, 1);
						        }
						        if(min>Integer.parseInt(helper.getSpielHalbzeitlaenge(gameId))*2){
						        	helper.updateSpielAktuelleHalbzeit(gameId, 0);
						        }

						        refreshContent();
						        
						}
					});

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();
                return true;
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
	    
/* Spielergebnis auf die Button schreiben */
		
	    Button btnToreHeim=(Button)view.findViewById(R.id.btn_tore_heim);
		Button btnToreAusw=(Button)view.findViewById(R.id.btn_tore_auswaerts);
		if(helper.getSpielToreHeim(gameId)!=null){
			btnToreHeim.setText(helper.getSpielToreHeim(gameId));
		}else{
			btnToreHeim.setText("0");
		}
		if(helper.getSpielToreAusw(gameId)!=null){
			btnToreAusw.setText(helper.getSpielToreAusw(gameId));
		}else{
			btnToreAusw.setText("0");
		}
		
/* Button Ballbesitz stellen */
		
		Button btnTeamHeim=(Button) view.findViewById(R.id.btn_heim);
		Button btnTeamAusw=(Button) view.findViewById(R.id.btn_auswaerts);
		strBallbesitz=helper.getSpielBallbesitz(gameId);
		switch(Integer.parseInt(strBallbesitz)){
			case 1:
				btnTeamHeim.setBackgroundResource(R.drawable.button_team_blue_active);
		    	btnTeamAusw.setBackgroundResource(R.drawable.button_team_red_inactive);
				break;
			case 0:
		    	btnTeamAusw.setBackgroundResource(R.drawable.button_team_red_active);
		    	btnTeamHeim.setBackgroundResource(R.drawable.button_team_blue_inactive);
		    	break;
			case 2:
		    	btnTeamHeim.setBackgroundResource(R.drawable.button_team_blue_inactive);
		    	btnTeamAusw.setBackgroundResource(R.drawable.button_team_red_inactive);
		    	break;
		}
		
/* Wurde während der Eingabe die Halbzeit oder das Spielende erreicht?  */
		
		if(!stopped){	// Überprüfung nur notwenidg, wenn Zeit nicht gestoppt
			/* Wenn noch 1. Halbzeit und Zeit über der Halbzeitlänge */
			if(Integer.parseInt(helper.getSpielAktuelleHalbzeit(gameId))==0 &&
					((System.currentTimeMillis() - startTime)/60000)>Integer.parseInt(helper.getSpielHalbzeitlaenge(gameId))){
				/* Dann Uhrzeit stoppen */
		        startTime = System.currentTimeMillis();
		        mHandler.removeCallbacks(startTimer);
		        
		        ((Button)view.findViewById(R.id.btn_uhr)).setBackgroundResource(R.drawable.button_timer_red);
		        /* Stoppuhr auf Halbzeit stellen */
				elapsedTime=(long) (Integer.parseInt(helper.getSpielHalbzeitlaenge(gameId))*60000);
				((TextView)view.findViewById(R.id.btn_uhr)).setText(helper.updateTimer(elapsedTime));
		            
				/** Hinweis: Stoppen der Uhr in Funktion ausgliedern */
			}
			/* Wenn noch 2. Halbzeit und Zeit über Spielende */
			if(Integer.parseInt(helper.getSpielAktuelleHalbzeit(gameId))==1 &&
					((System.currentTimeMillis() - startTime)/60000)>(Integer.parseInt(helper.getSpielHalbzeitlaenge(gameId))*2)){
				/* Dann Uhrzeit stoppen */
		        startTime = System.currentTimeMillis();
		        mHandler.removeCallbacks(startTimer);
		        stopped = true;
		        ((Button)view.findViewById(R.id.btn_uhr)).setBackgroundResource(R.drawable.button_timer_red);
		        /* Stoppuhr auf Halbzeit stellen */
				elapsedTime=(long) (Integer.parseInt(helper.getSpielHalbzeitlaenge(gameId))*2*60000);
				((TextView)view.findViewById(R.id.btn_uhr)).setText(helper.updateTimer(elapsedTime));
		        helper.updateSpielTicker(gameId, elapsedTime, zeitStart);
				/** Hinweis: Stoppen der Uhr in Funktion ausgliedern */
			}
		}
		
		refreshContent();
		
	}
	
    public void refreshContent() {

        if(helper.getAllTicker(gameId)!=null){
        	model=helper.getAllTicker(gameId);
        	getActivity().startManagingCursor(model);
        	adapter=new TickerAdapter(model);
        	setListAdapter(adapter); 
        }
    	 
    }
	
	@Override
	public void onDestroy() {
	  super.onDestroy();
	  /** Aktuelle Zeit auf der Stoppuhr in Datenbank übertragen */
	  if(stopped){
		  zeitStart=0;
	  }
	  else{
		  uhrStartStopp();
	  }
	  helper.updateSpielTicker(gameId, elapsedTime, zeitStart);
	  helper.close();
	}

/*
 * 
 * Stoppuhr einrichten
 *
 */
	
    public void uhrClick (View view){
    	uhrStartStopp();
    }
	
    public void uhrStartStopp() {
    	Log.v("TickerActivity", "uhrStartStopp");
    	if(stopped){
    		startTime = System.currentTimeMillis() - elapsedTime; 
        	mHandler.removeCallbacks(startTimer);
            mHandler.postDelayed(startTimer, 0);
            stopped = false;
            Resources res = getResources(); 
            helper.updateSpielTicker(gameId, elapsedTime, zeitStart);
            ((Button)view.findViewById(R.id.btn_uhr)).setBackgroundResource(R.drawable.button_timer_green);
			/** Falls noch keine Mannschaft im Ballbesitz: Â¨Einstellen, welche Mannschaft im Ballbesitz istÂ¨ **/
			if(strBallbesitz.equals("2")){
				AlertDialog.Builder builderWebside = new AlertDialog.Builder(getActivity());
				builderWebside
	        		.setTitle(R.string.anwurfMsgboxTitel)
	        		.setMessage(R.string.anwurfMsgboxText)
	        		.setIcon(android.R.drawable.ic_dialog_alert)
	        		.setPositiveButton(R.string.anwurfHeim, new DialogInterface.OnClickListener() {
	        			public void onClick(DialogInterface dialog, int which) {
	        				Button btnTeamHeim=(Button) view.findViewById(R.id.btn_heim);
	                        Button btnTeamAusw=(Button) view.findViewById(R.id.btn_auswaerts);
	                		if (Integer.parseInt(helper.getSpielBallbesitz(gameId))!=1){
	                			btnTeamHeim.setBackgroundResource(R.drawable.button_team_blue_active);
	                			btnTeamAusw.setBackgroundResource(R.drawable.button_team_red_inactive);
	                			String strBallbesitzText="Ballbesitz " + helper.getTeamHeimKurzBySpielID(gameId);
	                			realzeit = DateFormat.getDateTimeInstance().format(new Date());
	                			helper.insertTicker(0, strBallbesitzText, 1, "", 0, Integer.parseInt(gameId), 0, realzeit);
	                			// LISTVIEW => adapter.getCursor().requery();  // ListView aktualisieren
	                			helper.updateSpielBallbesitz(gameId, 1);  // aktuellen Ballbesitz in Spiel eintragen
	        					strBallbesitz="1";
	                		}
	        			}
	        		})
	        		.setNegativeButton(R.string.anwurfAusw, new DialogInterface.OnClickListener() {
	        			public void onClick(DialogInterface dialog, int which) {			      	
	        				Button btnTeamHeim=(Button) view.findViewById(R.id.btn_heim);
	                        Button btnTeamAusw=(Button) view.findViewById(R.id.btn_auswaerts);
	                		if (Integer.parseInt(helper.getSpielBallbesitz(gameId))!=0){
	                			btnTeamHeim.setBackgroundResource(R.drawable.button_team_blue_inactive);
	                			btnTeamAusw.setBackgroundResource(R.drawable.button_team_red_active);
	            				String strBallbesitzText="Ballbesitz " + helper.getTeamAuswKurzBySpielID(gameId);
	            				realzeit = DateFormat.getDateTimeInstance().format(new Date());
	            				helper.insertTicker(1, strBallbesitzText, 0, "", 0, Integer.parseInt(gameId), 0, realzeit);
	            				// LISTVIEW => adapter.getCursor().requery();   // ListView aktualisieren
	            				helper.updateSpielBallbesitz(gameId, 0);  // aktuellen Ballbesitz in Spiel eintragen
	        					strBallbesitz="0";
	                		}
	        			}
	        		})
	        		.show();
				refreshContent();

			}
    	}else{
    		startTime = System.currentTimeMillis();
        	mHandler.removeCallbacks(startTimer);
        	stopped = true;
        	helper.updateSpielTicker(gameId, elapsedTime, zeitStart);
        	((Button)view.findViewById(R.id.btn_uhr)).setBackgroundResource(R.drawable.button_timer_red);
    	}
    }
    
    private Runnable startTimer = new Runnable() {
    	public void run() {
    		Log.v("TickerActivity", "run");
    		elapsedTime = System.currentTimeMillis() - startTime;
    		((TextView)view.findViewById(R.id.btn_uhr)).setText(helper.updateTimer(elapsedTime));
    		mHandler.postDelayed(this,REFRESH_RATE);
    		/* Uhr stoppen bei Halbzeitpause */
    		if(elapsedTime>Integer.parseInt(helper.getSpielHalbzeitlaenge(gameId))*60*1000 &&
    				Integer.parseInt(helper.getSpielAktuelleHalbzeit(gameId))==0){
        		/* Stoppe die Zeit */
    			startTime = System.currentTimeMillis();
            	mHandler.removeCallbacks(startTimer);
            	stopped = true;
            	helper.updateSpielTicker(gameId, elapsedTime, zeitStart);
            	/* Schreibe in die Datenbank, dass die zweite Halbzeit begonnen hat */
            	helper.updateSpielAktuelleHalbzeit(gameId, 1);
            	((Button)view.findViewById(R.id.btn_uhr)).setBackgroundResource(R.drawable.button_timer_red);
            	/** Hinweis: Stopp-Funktion eventuell in eigene Methode ausgliedern*/
    		}
    		/* Uhr stoppen bei Spielende */
    		if(elapsedTime>Integer.parseInt(helper.getSpielHalbzeitlaenge(gameId))*2*60*1000 &&
    				Integer.parseInt(helper.getSpielAktuelleHalbzeit(gameId))==1){
        		/* Stoppe die Zeit */
    			startTime = System.currentTimeMillis();
            	mHandler.removeCallbacks(startTimer);
            	stopped = true;
            	helper.updateSpielTicker(gameId, elapsedTime, zeitStart);
            	/* Schreibe in die Datenbank, dass die zweite Halbzeit begonnen hat */
            	helper.updateSpielAktuelleHalbzeit(gameId, 2);
            	((Button)view.findViewById(R.id.btn_uhr)).setBackgroundResource(R.drawable.button_timer_red);
            	/** Hinweis: Stopp-Funktion eventuell in eigene Methode ausgliedern*/
    		}
    	}
	};
	

/*
 * 
 * Tickerauswahl 
 *
 */
	   
	@Override
	public void onListItemClick(ListView list, View view,
            int position, long id) {
		
		// Ticker Edit aufrufen
		
	}

/*
 * 
 * Liste Ticker definieren 
 *
 */
	
	class TickerAdapter extends CursorAdapter {
		TickerAdapter(Cursor c) {
			super(getActivity(), c);
		}
    
		@Override
		public void bindView(View row, Context ctxt,
				Cursor c) {
			TickerHolder holder=(TickerHolder)row.getTag();      
			holder.populateFrom(c, helper);
		}

		@Override
		public View newView(Context ctxt, Cursor c,
				ViewGroup parent) {
			LayoutInflater inflater=getActivity().getLayoutInflater();
			View row=inflater.inflate(R.layout.row_ticker, parent, false);
			TickerHolder holder=new TickerHolder(row);
			row.setTag(holder);
			return(row);
		}
	}
	
	static class TickerHolder {
	    private TextView aktion=null;
	    private TextView zeit=null;
	    private TextView spieler=null;
	    private TextView iconText=null;
	    private ImageView icon=null;
	    private View teamColor=null;
	    
	    TickerHolder(View row) {
	      aktion=(TextView)row.findViewById(R.id.rowTickerAktion);
	      zeit=(TextView)row.findViewById(R.id.rowTickerZeit);
	      spieler=(TextView)row.findViewById(R.id.rowTickerSpieler);
	      iconText=(TextView)row.findViewById(R.id.rowTickerText);
	      icon=(ImageView)row.findViewById(R.id.rowTickerIcon);
	      teamColor=(View)row.findViewById(R.id.rowTickerTeamColor);
	    }
	    
	    void populateFrom(Cursor c, SQLHelper helper) {
	      aktion.setText(helper.getTickerAktion(c));
	      zeit.setText(helper.getTickerZeit(c) + " Min.");
	      if(!helper.getTickerSpielerId(c).equals("0") ){
	    	  spieler.setText(helper.getSpielerNummerById(helper.getTickerSpielerId(c)) + " - " + helper.getTickerSpieler(c));
	      } else {
			  spieler.setText("");
		  }
	      if (Integer.parseInt(helper.getTickerAktionTeamHeim(c))==1){
	    	  teamColor.setBackgroundColor(0xFF404895);   	  
	      }
	      if (Integer.parseInt(helper.getTickerAktionTeamHeim(c))==0){
	    	  teamColor.setBackgroundColor(0xFFCB061D);   	  
	      }
	      if (Integer.parseInt(helper.getTickerAktionInt(c))==2 || Integer.parseInt(helper.getTickerAktionInt(c))==14 || 
	    		  Integer.parseInt(helper.getTickerAktionInt(c))==20){
	    	  icon.setImageResource(R.drawable.ticker_symbol_none);
	    	  iconText.setText(helper.getTickerErgebnis(c));   	  
	      }   
	      if (Integer.parseInt(helper.getTickerAktionInt(c))==3) {
	    	  icon.setImageResource(R.drawable.ticker_symbol_fehlwurf);
	    	  iconText.setText("");
	      } 
	      if (Integer.parseInt(helper.getTickerAktionInt(c))==4) {
	    	  icon.setImageResource(R.drawable.ticker_symbol_techfehl);
	    	  iconText.setText("");
	      } 
	      if (Integer.parseInt(helper.getTickerAktionInt(c))==15) {
	    	  icon.setImageResource(R.drawable.ticker_symbol_7m_fehlwurf);
	    	  iconText.setText("");
	      } 
	      if (Integer.parseInt(helper.getTickerAktionInt(c))==21) {
	    	  icon.setImageResource(R.drawable.ticker_symbol_tg_fehlwurf);
	    	  iconText.setText("");
	      } 
	      if (Integer.parseInt(helper.getTickerAktionInt(c))==6) {
	    	  icon.setImageResource(R.drawable.ticker_symbol_gelbekarte);
	    	  iconText.setText("");
	      }	      
	      if (Integer.parseInt(helper.getTickerAktionInt(c))==9) {
	    	  icon.setImageResource(R.drawable.ticker_symbol_rotekarte);
	    	  iconText.setText("");
	      }
	      if (Integer.parseInt(helper.getTickerAktionInt(c))==5 || Integer.parseInt(helper.getTickerAktionInt(c))==11) {
	    	  icon.setImageResource(R.drawable.ticker_symbol_zwei_raus);
	    	  iconText.setText("");
	      }
	      if (Integer.parseInt(helper.getTickerAktionInt(c))==0 || Integer.parseInt(helper.getTickerAktionInt(c))==1) {
	    	  icon.setImageResource(R.drawable.ticker_symbol_ballbesitz);
	    	  iconText.setText("");
	      }
	      if (Integer.parseInt(helper.getTickerAktionInt(c))==8) {
	    	  icon.setImageResource(R.drawable.ticker_symbol_auswechsel);
	    	  iconText.setText("");
	      }
	      if (Integer.parseInt(helper.getTickerAktionInt(c))==10) {
	    	  icon.setImageResource(R.drawable.ticker_symbol_zwei_rein);
	    	  iconText.setText("");
	      }
	      if (Integer.parseInt(helper.getTickerAktionInt(c))==7) {
	    	  icon.setImageResource(R.drawable.ticker_symbol_einwechsel);
	    	  iconText.setText("");
	      }
	      if (Integer.parseInt(helper.getTickerAktionInt(c))==16 || Integer.parseInt(helper.getTickerAktionInt(c))==22) {
	    	  icon.setImageResource(R.drawable.ticker_symbol_torwart_gehalten);
	    	  iconText.setText("");
	      }
	      if (Integer.parseInt(helper.getTickerAktionInt(c))==17 || Integer.parseInt(helper.getTickerAktionInt(c))==23) {
	    	  icon.setImageResource(R.drawable.ticker_symbol_torwart_tor);
	    	  iconText.setText("");
	      }
	      if (Integer.parseInt(helper.getTickerAktionInt(c))==18) {
	    	  icon.setImageResource(R.drawable.ticker_symbol_torwart_7m_gehalten);
	    	  iconText.setText("");
	      }
	      if (Integer.parseInt(helper.getTickerAktionInt(c))==19) {
	    	  icon.setImageResource(R.drawable.ticker_symbol_torwart_7m_tor);
	    	  iconText.setText("");
	      }
	      if (Integer.parseInt(helper.getTickerAktionInt(c))==24) {
	    	  icon.setImageResource(R.drawable.ticker_symbol_auszeit);
	    	  iconText.setText("");
	      }
	      helper.scaleImageRelative(icon, 45);   	
	    }
	    
	    private void scaleImagen(ImageView view, int boundBoxInDp)
	    {
	        // Get the ImageView and its bitmap
	        Drawable drawing = view.getDrawable();
	        Bitmap bitmap = ((BitmapDrawable)drawing).getBitmap();

	        // Get current dimensions
	        int width = bitmap.getWidth();
	        int height = bitmap.getHeight();

	        // Determine how much to scale: the dimension requiring less scaling is
	        // closer to the its side. This way the image always stays inside your
	        // bounding box AND either x/y axis touches it.
	        float xScale = ((float) boundBoxInDp) / width;
	        float yScale = ((float) boundBoxInDp) / height;
	        float scale = (xScale <= yScale) ? xScale : yScale;

	        // Create a matrix for the scaling and add the scaling data
	        Matrix matrix = new Matrix();
	        matrix.postScale(scale, scale);

	        // Create a new bitmap and convert it to a format understood by the ImageView
	        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	        BitmapDrawable result = new BitmapDrawable(scaledBitmap);
	        width = scaledBitmap.getWidth();
	        height = scaledBitmap.getHeight();

	        // Apply the scaled bitmap
	        view.setImageDrawable(result);

	        // Now change ImageView's dimensions to match the scaled image
	        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
	        params.width = width;
	        params.height = height;
	        view.setLayoutParams(params);
	    }
	}
	


}