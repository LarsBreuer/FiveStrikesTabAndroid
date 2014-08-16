package de.fivestrikes.tab;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

public class FragTickerPlayerMiss extends Fragment {
	
	private View view;
	Cursor model=null;
	SQLHelper helper=null;
	String gameId=null;
	String tickerId=null;
    String torwartTickerId=null;
    String wurfecke=null;
    String wurfposition=null;
    String aktionTeamHeim=null;
    String torwartAktionTeamHeim=null;
    String torwartId=null;
    String aktionInt=null;
    String torwartAktionInt=null;
    String torwartAktionString=null;
    String torwartString=null;
    String zeit=null;
    String realzeit=null;
    String strTeamHeimKurzBySpielID=null;
    String strTeamAuswKurzBySpielID=null;
    Integer intSpielBallbesitz=null;
	FragTickerList fragTickerList;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
/* Grundlayout setzen */

		view = inflater.inflate(R.layout.ticker_miss, container, false);
		fragTickerList = (FragTickerList) getActivity().getSupportFragmentManager().findFragmentById(R.id.frag_ticker_list);
		
/* Radio-Button definieren */
        
        RadioButton radio_ja=(RadioButton)view.findViewById(R.id.radio_ja);
        radio_ja.setChecked(true);
        
/* Daten aus Activity laden */ 
        
		Bundle args = getArguments();
		if(args!=null){
			tickerId = args.getString("TickerID");
			gameId = args.getString("GameID");
			aktionInt = args.getString("StrAktionInt");
			aktionTeamHeim = args.getString("AktionTeamHome");
			zeit = args.getString("Time");
			realzeit = args.getString("RealTime");
		} 
		
/* Datenbank laden */
	       
        helper=new SQLHelper(getActivity());
        
/* Daten aus Datenbank laden */
        
		if(Integer.parseInt(aktionTeamHeim)==1){
			if(helper.getSpielTorwartAuswaerts(gameId)!=null){
				torwartId=helper.getSpielTorwartAuswaerts(gameId);
			}
		}
		if(Integer.parseInt(aktionTeamHeim)==0){
			if(helper.getSpielTorwartHeim(gameId)!=null){
				torwartId=helper.getSpielTorwartHeim(gameId);
			}
		}
        if(torwartId==null){
        	Toast.makeText(getActivity(), getString(R.string.tickerWarnmeldungTorwartEinsatz), Toast.LENGTH_SHORT).show();
        }
        strTeamHeimKurzBySpielID = helper.getTeamHeimKurzBySpielID(gameId);
        strTeamAuswKurzBySpielID = helper.getTeamAuswKurzBySpielID(gameId);
        intSpielBallbesitz = Integer.parseInt(helper.getSpielBallbesitz(gameId));

/* Button definieren */
        
        final Button fehl_ooll_Button = (Button) view.findViewById(R.id.fehl_ooll);
        final Button fehl_ool_Button = (Button) view.findViewById(R.id.fehl_ool);
        final Button fehl_oom_Button = (Button) view.findViewById(R.id.fehl_oom);
        final Button fehl_oor_Button = (Button) view.findViewById(R.id.fehl_oor);
        final Button fehl_oorr_Button = (Button) view.findViewById(R.id.fehl_oorr);
        final Button fehl_oll_Button = (Button) view.findViewById(R.id.fehl_oll);
        final Button fehl_ol_Button = (Button) view.findViewById(R.id.fehl_ol);
        final Button fehl_om_Button = (Button) view.findViewById(R.id.fehl_om);
        final Button fehl_or_Button = (Button) view.findViewById(R.id.fehl_or);
        final Button fehl_orr_Button = (Button) view.findViewById(R.id.fehl_orr);
        final Button fehl_mll_Button = (Button) view.findViewById(R.id.fehl_mll);
        final Button fehl_ml_Button = (Button) view.findViewById(R.id.fehl_ml);
        final Button fehl_mm_Button = (Button) view.findViewById(R.id.fehl_mm);
        final Button fehl_mr_Button = (Button) view.findViewById(R.id.fehl_mr);
        final Button fehl_mrr_Button = (Button) view.findViewById(R.id.fehl_mrr);
        final Button fehl_ull_Button = (Button) view.findViewById(R.id.fehl_ull);
        final Button fehl_ul_Button = (Button) view.findViewById(R.id.fehl_ul);
        final Button fehl_um_Button = (Button) view.findViewById(R.id.fehl_um);
        final Button fehl_ur_Button = (Button) view.findViewById(R.id.fehl_ur);
        final Button fehl_urr_Button = (Button) view.findViewById(R.id.fehl_urr);
        final Button feld_1_1_Button = (Button) view.findViewById(R.id.feld_1_1);
        final Button feld_2_1_Button = (Button) view.findViewById(R.id.feld_2_1);
        final Button feld_3_1_Button = (Button) view.findViewById(R.id.feld_3_1);
        final Button feld_4_1_Button = (Button) view.findViewById(R.id.feld_4_1);
        final Button feld_5_1_Button = (Button) view.findViewById(R.id.feld_5_1);
        final Button feld_1_2_Button = (Button) view.findViewById(R.id.feld_1_2);
        final Button feld_2_2_Button = (Button) view.findViewById(R.id.feld_2_2);
        final Button feld_3_2_Button = (Button) view.findViewById(R.id.feld_3_2);
        final Button feld_4_2_Button = (Button) view.findViewById(R.id.feld_4_2);
        final Button feld_5_2_Button = (Button) view.findViewById(R.id.feld_5_2);
        final Button feld_1_3_Button = (Button) view.findViewById(R.id.feld_1_3);
        final Button feld_2_3_Button = (Button) view.findViewById(R.id.feld_2_3);
        final Button feld_3_3_Button = (Button) view.findViewById(R.id.feld_3_3);
        final Button feld_4_3_Button = (Button) view.findViewById(R.id.feld_4_3);
        final Button feld_5_3_Button = (Button) view.findViewById(R.id.feld_5_3);
        final Button feld_1_4_Button = (Button) view.findViewById(R.id.feld_1_4);
        final Button feld_2_4_Button = (Button) view.findViewById(R.id.feld_2_4);
        final Button feld_3_4_Button = (Button) view.findViewById(R.id.feld_3_4);
        final Button feld_4_4_Button = (Button) view.findViewById(R.id.feld_4_4);
        final Button feld_5_4_Button = (Button) view.findViewById(R.id.feld_5_4);
        
		/* Bei Siebenmeter, den Siebenmeterpunkt einrichten **/
		Cursor cTicker=helper.getTickerCursor(tickerId);
		cTicker.moveToFirst();
		if(Integer.parseInt(helper.getTickerAktionInt(cTicker))==15){
			wurfposition="3_3";
			wurf_button_leeren();
			feld_3_3_Button.setText("X");
		}
		cTicker.close();
		
        /* Button Wurfecke*/
        fehl_ooll_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfecke="OOLL";
            	fehl_button_leeren();
            	fehl_ooll_Button.setText("X");
            	uebertragen();
            }
        });
        fehl_ool_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfecke="OOL";
            	fehl_button_leeren();
            	fehl_ool_Button.setText("X");
            	uebertragen();
            }
        });
        fehl_oom_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfecke="OOM";
            	fehl_button_leeren();
            	fehl_oom_Button.setText("X");
            	uebertragen();
            }
        });
        fehl_oor_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfecke="OOR";
            	fehl_button_leeren();
            	fehl_oor_Button.setText("X");
            	uebertragen();
            }
        });
        fehl_oorr_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfecke="OORR";
            	fehl_button_leeren();
            	fehl_oorr_Button.setText("X");
            	uebertragen();
            }
        });
        fehl_oll_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfecke="OLL";
            	fehl_button_leeren();
            	fehl_oll_Button.setText("X");
            	uebertragen();
            }
        });
        fehl_ol_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfecke="OL";
            	fehl_button_leeren();
            	fehl_ol_Button.setText("X");
            	uebertragen();
            }
        });
        fehl_om_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfecke="OM";
            	fehl_button_leeren();
            	fehl_om_Button.setText("X");
            	uebertragen();
            }
        });
        fehl_or_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfecke="OR";
            	fehl_button_leeren();
            	fehl_or_Button.setText("X");
            	uebertragen();
            }
        });
        fehl_orr_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfecke="ORR";
            	fehl_button_leeren();
            	fehl_orr_Button.setText("X");
            	uebertragen();
            }
        });
        fehl_mll_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfecke="MLL";
            	fehl_button_leeren();
            	fehl_mll_Button.setText("X");
            	uebertragen();
            }
        });
        fehl_ml_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfecke="ML";
            	fehl_button_leeren();
            	fehl_ml_Button.setText("X");
            	uebertragen();
            }
        });
        fehl_mm_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfecke="MM";
            	fehl_button_leeren();
            	fehl_mm_Button.setText("X");
            	uebertragen();
            }
        });
        fehl_mr_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfecke="MR";
            	fehl_button_leeren();
            	fehl_mr_Button.setText("X");
            	uebertragen();
            }
        });
        fehl_mrr_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfecke="MRR";
            	fehl_button_leeren();
            	fehl_mrr_Button.setText("X");
            	uebertragen();
            }
        });
        fehl_ull_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfecke="ULL";
            	fehl_button_leeren();
            	fehl_ull_Button.setText("X");
            	uebertragen();
            }
        });
        fehl_ul_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfecke="UL";
            	fehl_button_leeren();
            	fehl_ul_Button.setText("X");
            	uebertragen();
            }
        });
        fehl_um_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfecke="UM";
            	fehl_button_leeren();
            	fehl_um_Button.setText("X");
            	uebertragen();
            }
        });
        fehl_ur_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfecke="UR";
            	fehl_button_leeren();
            	fehl_ur_Button.setText("X");
            	uebertragen();
            }
        });
        fehl_urr_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfecke="URR";
            	fehl_button_leeren();
            	fehl_urr_Button.setText("X");
            	uebertragen();
            }
        });
        
        /* Button Wurfposition*/
        feld_1_1_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfposition="1_1";
            	wurf_button_leeren();
            	feld_1_1_Button.setText("X");
            	uebertragen();
            }
        });
        feld_2_1_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfposition="2_1";
            	wurf_button_leeren();
            	feld_2_1_Button.setText("X");
            	uebertragen();
            }
        });
        feld_3_1_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfposition="3_1";
            	wurf_button_leeren();
            	feld_3_1_Button.setText("X");
            	uebertragen();
            }
        });
        feld_4_1_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfposition="4_1";
            	wurf_button_leeren();
            	feld_4_1_Button.setText("X");
            	uebertragen();
            }
        });
        feld_5_1_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfposition="5_1";
            	wurf_button_leeren();
            	feld_5_1_Button.setText("X");
            	uebertragen();
            }
        });
        feld_1_2_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfposition="1_2";
            	wurf_button_leeren();
            	feld_1_2_Button.setText("X");
            	uebertragen();
            }
        });
        feld_2_2_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfposition="2_2";
            	wurf_button_leeren();
            	feld_2_2_Button.setText("X");
            	uebertragen();
            }
        });
        feld_3_2_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfposition="3_2";
            	wurf_button_leeren();
            	feld_3_2_Button.setText("X");
            	uebertragen();
            }
        });
        feld_4_2_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfposition="4_2";
            	wurf_button_leeren();
            	feld_4_2_Button.setText("X");
            	uebertragen();
            }
        });
        feld_5_2_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfposition="5_2";
            	wurf_button_leeren();
            	feld_5_2_Button.setText("X");
            	uebertragen();
            }
        });
        feld_1_3_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfposition="1_3";
            	wurf_button_leeren();
            	feld_1_3_Button.setText("X");
            	uebertragen();
            }
        });
        feld_2_3_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfposition="2_3";
            	wurf_button_leeren();
            	feld_2_3_Button.setText("X");
            	uebertragen();
            }
        });
        feld_3_3_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfposition="3_3";
            	wurf_button_leeren();
            	feld_3_3_Button.setText("X");
            	uebertragen();
            }
        });
        feld_4_3_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfposition="4_3";
            	wurf_button_leeren();
            	feld_4_3_Button.setText("X");
            	uebertragen();
            }
        });
        feld_5_3_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfposition="5_3";
            	wurf_button_leeren();
            	feld_5_3_Button.setText("X");
            	uebertragen();
            }
        });
        feld_1_4_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfposition="1_4";
            	wurf_button_leeren();
            	feld_1_4_Button.setText("X");
            	uebertragen();
            }
        });
        feld_2_4_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfposition="2_4";
            	wurf_button_leeren();
            	feld_2_4_Button.setText("X");
            	uebertragen();
            }
        });
        feld_3_4_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfposition="3_4";
            	wurf_button_leeren();
            	feld_3_4_Button.setText("X");
            	uebertragen();
            }
        });
        feld_4_4_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfposition="4_4";
            	wurf_button_leeren();
            	feld_4_4_Button.setText("X");
            	uebertragen();
            }
        });
        feld_5_4_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfposition="5_4";
            	wurf_button_leeren();
            	feld_5_4_Button.setText("X");
            	uebertragen();
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
 * Button leeren 
 *
 */
	
	public void fehl_button_leeren() {

        final Button fehl_ooll_Button = (Button) view.findViewById(R.id.fehl_ooll);
        final Button fehl_ool_Button = (Button) view.findViewById(R.id.fehl_ool);
        final Button fehl_oom_Button = (Button) view.findViewById(R.id.fehl_oom);
        final Button fehl_oor_Button = (Button) view.findViewById(R.id.fehl_oor);
        final Button fehl_oorr_Button = (Button) view.findViewById(R.id.fehl_oorr);
        final Button fehl_oll_Button = (Button) view.findViewById(R.id.fehl_oll);
        final Button fehl_ol_Button = (Button) view.findViewById(R.id.fehl_ol);
        final Button fehl_om_Button = (Button) view.findViewById(R.id.fehl_om);
        final Button fehl_or_Button = (Button) view.findViewById(R.id.fehl_or);
        final Button fehl_orr_Button = (Button) view.findViewById(R.id.fehl_orr);
        final Button fehl_mll_Button = (Button) view.findViewById(R.id.fehl_mll);
        final Button fehl_ml_Button = (Button) view.findViewById(R.id.fehl_ml);
        final Button fehl_mm_Button = (Button) view.findViewById(R.id.fehl_mm);
        final Button fehl_mr_Button = (Button) view.findViewById(R.id.fehl_mr);
        final Button fehl_mrr_Button = (Button) view.findViewById(R.id.fehl_mrr);
        final Button fehl_ull_Button = (Button) view.findViewById(R.id.fehl_ull);
        final Button fehl_ul_Button = (Button) view.findViewById(R.id.fehl_ul);
        final Button fehl_um_Button = (Button) view.findViewById(R.id.fehl_um);
        final Button fehl_ur_Button = (Button) view.findViewById(R.id.fehl_ur);
        final Button fehl_urr_Button = (Button) view.findViewById(R.id.fehl_urr);
        fehl_ooll_Button.setText("");
        fehl_ool_Button.setText("");
        fehl_oom_Button.setText("");
        fehl_oor_Button.setText("");
        fehl_oorr_Button.setText("");
        fehl_oll_Button.setText("");
        fehl_ol_Button.setText("");
        fehl_om_Button.setText("");
        fehl_or_Button.setText("");
        fehl_orr_Button.setText("");
        fehl_mll_Button.setText("");
        fehl_ml_Button.setText("");
        fehl_mm_Button.setText("");
        fehl_mr_Button.setText("");
        fehl_mrr_Button.setText("");
        fehl_ull_Button.setText("");
        fehl_ul_Button.setText("");
        fehl_um_Button.setText("");
        fehl_ur_Button.setText("");
        fehl_urr_Button.setText("");

	}

	public void wurf_button_leeren() {

        final Button feld_1_1_Button = (Button) view.findViewById(R.id.feld_1_1);
        final Button feld_2_1_Button = (Button) view.findViewById(R.id.feld_2_1);
        final Button feld_3_1_Button = (Button) view.findViewById(R.id.feld_3_1);
        final Button feld_4_1_Button = (Button) view.findViewById(R.id.feld_4_1);
        final Button feld_5_1_Button = (Button) view.findViewById(R.id.feld_5_1);
        final Button feld_1_2_Button = (Button) view.findViewById(R.id.feld_1_2);
        final Button feld_2_2_Button = (Button) view.findViewById(R.id.feld_2_2);
        final Button feld_3_2_Button = (Button) view.findViewById(R.id.feld_3_2);
        final Button feld_4_2_Button = (Button) view.findViewById(R.id.feld_4_2);
        final Button feld_5_2_Button = (Button) view.findViewById(R.id.feld_5_2);
        final Button feld_1_3_Button = (Button) view.findViewById(R.id.feld_1_3);
        final Button feld_2_3_Button = (Button) view.findViewById(R.id.feld_2_3);
        final Button feld_3_3_Button = (Button) view.findViewById(R.id.feld_3_3);
        final Button feld_4_3_Button = (Button) view.findViewById(R.id.feld_4_3);
        final Button feld_5_3_Button = (Button) view.findViewById(R.id.feld_5_3);
        final Button feld_1_4_Button = (Button) view.findViewById(R.id.feld_1_4);
        final Button feld_2_4_Button = (Button) view.findViewById(R.id.feld_2_4);
        final Button feld_3_4_Button = (Button) view.findViewById(R.id.feld_3_4);
        final Button feld_4_4_Button = (Button) view.findViewById(R.id.feld_4_4);
        final Button feld_5_4_Button = (Button) view.findViewById(R.id.feld_5_4);
        feld_1_1_Button.setText("");
        feld_2_1_Button.setText("");
        feld_3_1_Button.setText("");
        feld_4_1_Button.setText("");
        feld_5_1_Button.setText("");
        feld_1_2_Button.setText("");
        feld_2_2_Button.setText("");
        feld_3_2_Button.setText("");
        feld_4_2_Button.setText("");
        feld_5_2_Button.setText("");
        feld_1_3_Button.setText("");
        feld_2_3_Button.setText("");
        feld_3_3_Button.setText("");
        feld_4_3_Button.setText("");
        feld_5_3_Button.setText("");
        feld_1_4_Button.setText("");
        feld_2_4_Button.setText("");
        feld_3_4_Button.setText("");
        feld_4_4_Button.setText("");
        feld_5_4_Button.setText("");
        

	}

/*
 * 
 * Zurück zu Spielübersicht und Ergebnis übertragen
 *
 */
	
	public void uebertragen() {

		if(wurfecke!=null && wurfposition!=null){
			
			/* Wurfecke und -position des Spielers eingeben */ 
			helper.updateTickerWurfecke(tickerId, wurfecke);
			helper.updateTickerWurfposition(tickerId, wurfposition);

			/* Wurfecke und -position bei Torwart eintragen */
			if(wurfecke.equals("OL") || wurfecke.equals("OM") || wurfecke.equals("OR") 
					|| wurfecke.equals("ML") || wurfecke.equals("MM") || wurfecke.equals("MR")
					|| wurfecke.equals("UL") || wurfecke.equals("UM") || wurfecke.equals("UR")){
	    		if(torwartId!=null){  
					torwartString=helper.getSpielerName(torwartId);
					if(aktionInt.equals("3")){
						torwartAktionInt="16";
						torwartAktionString=getString(R.string.tickerAktionTorwartGehalten);
					}
					if(aktionInt.equals("15")){
						torwartAktionInt="18";
						torwartAktionString=getString(R.string.tickerAktionTorwart7mGehalten);
					}
					if(aktionInt.equals("21")){
						torwartAktionInt="22";
						torwartAktionString=getString(R.string.tickerAktionTorwartTGGehalten);
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
					helper.updateTickerWurfecke(torwartTickerId, wurfecke);
					helper.updateTickerWurfposition(torwartTickerId, wurfposition);
					lastTickTorwartC.close();
	    		}
			}
    		
    		/* Ballbesitzwechsel eintragen, falls Ballverlust */
    		RadioGroup rBallverlust=(RadioGroup)view.findViewById(R.id.radio_ballverlust);
    		switch(rBallverlust.getCheckedRadioButtonId()) {
    			case R.id.radio_ja:
    				/* Änderung des Ballbesitzes */
    	    		helper.changeBallbesitz(aktionTeamHeim, intSpielBallbesitz, strTeamHeimKurzBySpielID, strTeamAuswKurzBySpielID, 
    	    				gameId, zeit, realzeit); 
    				break;
    			case R.id.radio_nein:
    				
    				break;
    		}
    		
			/* Ticker aktualisieren und Fragment schließen */
	    	fragTickerList.refreshContent();
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
	        FragEmpty fragment = new FragEmpty();
	        fragmentTransaction.replace(R.id.frag_ticker_player, fragment);
	        fragmentTransaction.commit();
	        
		}
	}
	
}