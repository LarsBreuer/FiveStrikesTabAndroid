package de.fivestrikes.tab;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import java.text.DateFormat;
import java.util.Date;
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
import android.widget.TextView;
import android.widget.Toast;

public class FragTickerAction extends Fragment {
	
	private View view;
	SQLHelper helper=null;
	String gameId=null;
	String aktion=null;
	String teamId=null;
	String teamHeimId=null;
	String teamAuswId=null;
	String strTeamHeimKurzBySpielID=null;
	String strTeamAuswKurzBySpielID=null;
	String aktionTeamHeim=null;
	String strAktion=null;
	String strAktionInt=null;
	String strBallbesitz=null;
	String spielerEingabe=null;
	String realzeit=null;
	Integer zeit=null;
	Integer intSpielBallbesitz=null;
	FragTickerList fragTickerList;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
/* Grundlayout setzen */

		view = inflater.inflate(R.layout.frag_ticker_action, container, false);
		fragTickerList = (FragTickerList) getActivity().getSupportFragmentManager().findFragmentById(R.id.frag_ticker_list);
		
/* Daten aus Activity laden */ 
        
		Bundle args = getArguments();
		if(args!=null){
			gameId = args.getString("GameID");
		} 
		
/* Datenbank laden */
	       
        helper=new SQLHelper(getActivity());

/* Daten aus Datenbank laden */
        
		teamHeimId = helper.getSpielHeim(gameId);
		teamAuswId = helper.getSpielAusw(gameId);
		strTeamHeimKurzBySpielID=helper.getTeamHeimKurzBySpielID(gameId);
		strTeamAuswKurzBySpielID=helper.getTeamAuswKurzBySpielID(gameId);

 /* Button einrichten */
	    
        ImageButton btnTor=(ImageButton) view.findViewById(R.id.menu_tor);
        btnTor.setImageResource(R.drawable.aktion_torwurf);
        helper.scaleImageLinear(btnTor, 50);
        ImageButton btn7mTor = (ImageButton) view.findViewById(R.id.menu_7mTor);
        btn7mTor.setImageResource(R.drawable.aktion_7m_tor);
        helper.scaleImageLinear(btn7mTor, 50);
        ImageButton btnTGTor=(ImageButton) view.findViewById(R.id.menu_TGTor);
        btnTGTor.setImageResource(R.drawable.aktion_tg_tor);
        helper.scaleImageLinear(btnTGTor, 50);
        ImageButton btnGelbe=(ImageButton) view.findViewById(R.id.menu_gelbe);
        btnGelbe.setImageResource(R.drawable.aktion_gelbekarte);
        helper.scaleImageLinear(btnGelbe, 50);
        ImageButton btnFehl=(ImageButton) view.findViewById(R.id.menu_fehl);
        btnFehl.setImageResource(R.drawable.aktion_fehlwurf);
        helper.scaleImageLinear(btnFehl, 50);
        ImageButton btn7mFehl=(ImageButton) view.findViewById(R.id.menu_7mFehl);
        btn7mFehl.setImageResource(R.drawable.aktion_7m_fehlwurf);
        helper.scaleImageLinear(btn7mFehl, 50);
        ImageButton btnTGFehl=(ImageButton) view.findViewById(R.id.menu_TGFehl);
        btnTGFehl.setImageResource(R.drawable.aktion_tg_fehlwurf);
        helper.scaleImageLinear(btnTGFehl, 50);
        ImageButton btnZwei=(ImageButton) view.findViewById(R.id.menu_zwei);
        btnZwei.setImageResource(R.drawable.aktion_zweimin);
        helper.scaleImageLinear(btnZwei, 50);
        ImageButton btnTechFehl = (ImageButton) view.findViewById(R.id.menu_techfehl);
        btnTechFehl.setImageResource(R.drawable.aktion_techfehl);
        helper.scaleImageLinear(btnTechFehl, 50);
        ImageButton btnAufstellheim=(ImageButton) view.findViewById(R.id.menu_aufstellheim);
        btnAufstellheim.setImageResource(R.drawable.aktion_aufstellung_heim);
        helper.scaleImageLinear(btnAufstellheim, 50);
        ImageButton btnAuszeitHeim=(ImageButton) view.findViewById(R.id.menu_AuszeitHeim);
        btnAuszeitHeim.setImageResource(R.drawable.aktion_auszeit_heim);
        helper.scaleImageLinear(btnAuszeitHeim, 50);
        ImageButton btnZweiPlusZwei=(ImageButton) view.findViewById(R.id.menu_zweipluszwei);
        btnZweiPlusZwei.setImageResource(R.drawable.aktion_zweipluszwei);
        helper.scaleImageLinear(btnZweiPlusZwei, 50);
        ImageButton btnWechsel=(ImageButton) view.findViewById(R.id.menu_wechsel);
        btnWechsel.setImageResource(R.drawable.aktion_wechsel);
        helper.scaleImageLinear(btnWechsel, 50);
        ImageButton btnAufstellAusw=(ImageButton) view.findViewById(R.id.menu_aufstellausw);
        btnAufstellAusw.setImageResource(R.drawable.aktion_aufstellung_auswaerts);
        helper.scaleImageLinear(btnAufstellAusw, 50);
        ImageButton btnAuszeitAuswaerts=(ImageButton) view.findViewById(R.id.menu_auszeitauswaerts);
        btnAuszeitAuswaerts.setImageResource(R.drawable.aktion_auszeit_auswaerts);
        helper.scaleImageLinear(btnAuszeitAuswaerts, 50);
        ImageButton btnRote = (ImageButton) view.findViewById(R.id.menu_rote);
        btnRote.setImageResource(R.drawable.aktion_rotekarte);
        helper.scaleImageLinear(btnRote, 50);
        
        /* Button zurück definieren*/

        btnTor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				strAktionInt="2";
				strAktion=getResources().getString(R.string.tickerAktionTor);
				realzeit = DateFormat.getDateTimeInstance().format(new Date());
				startAktion();
            }
        });
        
        btn7mTor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				strAktionInt="14";
				strAktion=getResources().getString(R.string.tickerAktion7mTor);
				realzeit = DateFormat.getDateTimeInstance().format(new Date());
				startAktion();
            }
        });
        
        btnTGTor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				strAktionInt="20";
				strAktion=getResources().getString(R.string.tickerAktionTempogegenstossTor);
				realzeit = DateFormat.getDateTimeInstance().format(new Date());
				startAktion();
            }
        });
        
        btnGelbe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				strAktionInt="6";
				strAktion=getResources().getString(R.string.tickerAktionGelbeKarte);
				realzeit = DateFormat.getDateTimeInstance().format(new Date());
				startAktion();
            }
        });
        
        btnFehl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				strAktionInt="3";
				strAktion=getResources().getString(R.string.tickerAktionFehlwurf);
				realzeit = DateFormat.getDateTimeInstance().format(new Date());
				startAktion();
            }
        });
        
        btn7mFehl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				strAktionInt="15";
				strAktion=getResources().getString(R.string.tickerAktion7mFehlwurf);
				realzeit = DateFormat.getDateTimeInstance().format(new Date());
				startAktion();
            }
        });
        
        btnTGFehl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				strAktionInt="21";
				strAktion=getResources().getString(R.string.tickerAktionTempogegenstossFehlwurf);
				realzeit = DateFormat.getDateTimeInstance().format(new Date());
				startAktion();
            }
        });
        
        btnZwei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				strAktionInt="5";
				strAktion=getResources().getString(R.string.tickerAktionZweiMinuten);
				realzeit = DateFormat.getDateTimeInstance().format(new Date());
				startAktion();
            }
        });
        
        btnTechFehl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				strAktionInt="4";
				strAktion=getResources().getString(R.string.tickerAktionTechnischerFehler);
				realzeit = DateFormat.getDateTimeInstance().format(new Date());
				startAktion();
            }
        });
        
        btnAufstellheim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				strAktionInt="12";
				aktionTeamHeim = "1";
				strAktion=getResources().getString(R.string.tickerAktionStartaufstellung);
				realzeit = DateFormat.getDateTimeInstance().format(new Date());
				startAktion();
            }
        });
        
        btnAuszeitHeim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				strAktionInt="24";
				aktionTeamHeim = "1";
				strAktion=getResources().getString(R.string.tickerAktionAuszeit);
				realzeit = DateFormat.getDateTimeInstance().format(new Date());
				aktionAuszeit();
            }
        });
        
        btnZweiPlusZwei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				strAktionInt="11";
				strAktion=getResources().getString(R.string.tickerAktionZweiMalZwei);
				realzeit = DateFormat.getDateTimeInstance().format(new Date());
				startAktion();
            }
        });
        
        btnWechsel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				strAktionInt="7";
				strAktion=getResources().getString(R.string.tickerAktionEinwechselung);
				realzeit = DateFormat.getDateTimeInstance().format(new Date());
				startAktion();
            }
        });
        
        btnAufstellAusw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				strAktionInt="12";
				aktionTeamHeim = "0";
				strAktion=getResources().getString(R.string.tickerAktionStartaufstellung);
				realzeit = DateFormat.getDateTimeInstance().format(new Date());
				startAktion();
            }
        });
        
        btnAuszeitAuswaerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				strAktionInt="24";
				aktionTeamHeim = "0";
				strAktion=getResources().getString(R.string.tickerAktionAuszeit);
				realzeit = DateFormat.getDateTimeInstance().format(new Date());
				aktionAuszeit();
            }
        });
        
        btnRote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				strAktionInt="9";
				strAktion=getResources().getString(R.string.tickerAktionRoteKarte);
				realzeit = DateFormat.getDateTimeInstance().format(new Date());
				startAktion();
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
 * Auswahl einer Aktion 
 *
 */
	
    public void startAktion() {
    	
    	if (helper.getSpielBallbesitz(gameId)!=null){
			strBallbesitz = String.valueOf(helper.getSpielBallbesitz(gameId));
		} else {
			strBallbesitz = "1";
		}
	
		if(helper.getSpielSpielerEingabe(gameId)!=null){
			spielerEingabe=helper.getSpielSpielerEingabe(gameId);
		} else {
			spielerEingabe="1";
		}
		if(strAktionInt.equals("2") || strAktionInt.equals("14") || strAktionInt.equals("20") || strAktionInt.equals("3") ||
				strAktionInt.equals("15") || strAktionInt.equals("21") || strAktionInt.equals("4")){
			aktionTeamHeim=strBallbesitz;
		}
		if(strAktionInt.equals("6") || strAktionInt.equals("5") || strAktionInt.equals("11") || 
				strAktionInt.equals("7") || strAktionInt.equals("9")){
			if (strBallbesitz.equals("1")){
				aktionTeamHeim = "0";
			} else {
				aktionTeamHeim = "1";
			}
		}
		if(spielerEingabe.equals("0")){
			if (!strAktionInt.equals("12") && !strAktionInt.equals("7")){
				aktionDirekt();
			} 
		}else {
			if (strAktionInt.equals("12")){
				Bundle args = new Bundle();
				args.putString("StrAktionInt", strAktionInt);
				args.putString("StrAktion", strAktion);
				args.putString("AktionTeamHome", aktionTeamHeim);
				args.putString("GameID", gameId);
				args.putString("Time", String.valueOf(fragTickerList.elapsedTime));
				args.putString("RealTime", String.valueOf(realzeit));
				args.putString("TeamHomeID", teamHeimId);
				args.putString("TeamAwayID", teamAuswId);
				FragmentManager fragmentManager = getFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		        FragTickerPlayerLineup fragment = new FragTickerPlayerLineup();
		        fragment.setArguments(args);
		        fragmentTransaction.replace(R.id.frag_ticker_player, fragment);
		        fragmentTransaction.commit();
			} else {
				Bundle args = new Bundle();
				args.putString("StrAktionInt", strAktionInt);
				args.putString("StrAktion", strAktion);
				args.putString("AktionTeamHome", aktionTeamHeim);
				args.putString("GameID", gameId);
				args.putString("Time", String.valueOf(fragTickerList.elapsedTime));
				args.putString("RealTime", String.valueOf(realzeit));
				args.putString("TeamHomeID", teamHeimId);
				args.putString("TeamAwayID", teamAuswId);
				FragmentManager fragmentManager = getFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		        FragTickerPlayer fragment = new FragTickerPlayer();
		        fragment.setArguments(args);
		        fragmentTransaction.replace(R.id.frag_ticker_player, fragment);
		        fragmentTransaction.commit();
			}
    	}
		
    }

/*
 * 
 * Direkte Eingabe einer Aktion 
 *
 */
	
    public void aktionDirekt() {
    	
    	intSpielBallbesitz = Integer.parseInt(helper.getSpielBallbesitz(gameId));
    	
    	if (helper.getSpielBallbesitz (gameId)!=null){
			strBallbesitz = String.valueOf(intSpielBallbesitz);
		} else {
			strBallbesitz = "1";
		}
		
    	if(strAktionInt.equals("2") || strAktionInt.equals("14") || strAktionInt.equals("20") || strAktionInt.equals("3") ||
				strAktionInt.equals("15") || strAktionInt.equals("21")){
			aktionTeamHeim=strBallbesitz;
		}
		if(strAktionInt.equals("6") || strAktionInt.equals("5") || strAktionInt.equals("11") || 
				strAktionInt.equals("7") || strAktionInt.equals("9")){
			if (strBallbesitz.equals("1")){
				aktionTeamHeim = "0";
			} else {
				aktionTeamHeim = "1";
			}
		}
		
		zeit=(int) (long) fragTickerList.elapsedTime;
		int zeitZurueck=zeit;
		int halbzeitlaenge=Integer.parseInt(helper.getSpielHalbzeitlaenge(gameId))*60*2000;
		
    	helper.insertTicker(Integer.parseInt(strAktionInt), strAktion, Integer.parseInt(aktionTeamHeim), "", 
				0, Integer.parseInt(gameId), zeit, realzeit);
    	
    	Toast.makeText(getActivity(), strAktion, Toast.LENGTH_SHORT).show();
    	
    	/* Spielstand in Tickereinträge schreiben falls Tor geworfen wurde */
		/* Wenn es Tickereinträge nach dem aktuellen Eintrag gibt, ändere die Torfolge bei den Einträgen */
    	int maxZeit = helper.maxTickerZeit(gameId);Cursor lastTickC=helper.getLastTickerId();
		lastTickC.moveToFirst();
		String tickerId = helper.getTickerId(lastTickC);
		lastTickC.close();
    	if(Integer.parseInt(strAktionInt)==2 || 
    			Integer.parseInt(strAktionInt)==14 || 
    			Integer.parseInt(strAktionInt)==20){
    		String[] args={gameId};
    		SQLiteDatabase db=helper.getWritableDatabase();
    		Cursor cTicker=db.rawQuery("SELECT * FROM ticker WHERE spielID=? ORDER BY zeitInteger ASC", args);
			cTicker.moveToFirst(); 
			String strErgebnis=null;
			if(zeit<=maxZeit){	
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
					if(Integer.parseInt(helper.getTickerZeitInt(cTicker))>=zeit){	// Wenn die Zeit der Schleifeneintrags größer ist als der aktuelle Eintrag, dann hat sich die Torfolge geändert und der Eintrag muss geändert werden
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
			/* Änderung des Ballbesitzes */
			helper.changeBallbesitz(aktionTeamHeim, intSpielBallbesitz, strTeamHeimKurzBySpielID, strTeamAuswKurzBySpielID, 
	    				gameId, String.valueOf(zeit), realzeit); 
    	}
    	
    	// Bei Fehlwurf nach Ballbesitzwechsel fragen
    	if(Integer.parseInt(strAktionInt)==3 || 
    			Integer.parseInt(strAktionInt)==15 || 
    			Integer.parseInt(strAktionInt)==21 || 
    			Integer.parseInt(strAktionInt)==4){
    		AlertDialog.Builder tfBuilder = new AlertDialog.Builder(getActivity());
   			tfBuilder
   			.setTitle(R.string.tickerMSGBoxAktionTitel)
   			.setMessage(R.string.tickerMSGBoxAktionNachricht)
   			.setIcon(android.R.drawable.ic_dialog_alert)
   			.setPositiveButton(R.string.tickerMSGBoxJa, new DialogInterface.OnClickListener() {
   				public void onClick(DialogInterface dialog, int which) {
   					/* Änderung des Ballbesitzes */
   					helper.changeBallbesitz(aktionTeamHeim, intSpielBallbesitz, strTeamHeimKurzBySpielID, strTeamAuswKurzBySpielID, 
   		    				gameId, String.valueOf(zeit), realzeit); 
   	    			fragTickerList.onResume();
   				}
   			})
   			.setNegativeButton(R.string.tickerMSGBoxNein, new DialogInterface.OnClickListener() {
   				public void onClick(DialogInterface dialog, int which) {

   				}
   			})
   			.show();
    	}
		
		// Bei Zeitstrafen Spieler zurück eintragen
		if(Integer.parseInt(strAktionInt)==5 || 
    			Integer.parseInt(strAktionInt)==9 || 
    			Integer.parseInt(strAktionInt)==11){
    	    /** Hinweis: Nach Spiellänge 2 Minuten auf Spiellänge setzen */
    	    if(zeitZurueck>halbzeitlaenge){
    	    	zeitZurueck=halbzeitlaenge;
    	    }
    	    if(Integer.parseInt(strAktionInt)==5 || 
        			Integer.parseInt(strAktionInt)==9){
    	    	zeitZurueck=zeitZurueck+2*60000;
    	    }
    	    if(Integer.parseInt(strAktionInt)==11){
    	    	zeitZurueck=zeitZurueck+4*60000;
    	    }
    	    helper.insertTicker(10, getString(R.string.tickerAktionZurueck), Integer.parseInt(aktionTeamHeim), 
    	    		"", 0, Integer.parseInt(gameId), zeitZurueck, realzeit);
		}
		
		
    	helper.updateSpielErgebnis(Integer.parseInt(gameId));
    	fragTickerList.onResume();
    }

/*
 * 
 * Eingabe der Auszeit
 *
 */
	 
    public void aktionAuszeit() {
		
    	helper.insertTicker(Integer.parseInt(strAktionInt), strAktion, Integer.parseInt(aktionTeamHeim), "", 
				0, Integer.parseInt(gameId), (int) (long) fragTickerList.elapsedTime, realzeit);
  	 	
    	fragTickerList.uhrStartStopp();
    }

/*
 * 
 * Bilder verkleinern
 *
 */
	 
    private void scaleImagen(ImageButton view, int boundBoxInDp)
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
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }



}