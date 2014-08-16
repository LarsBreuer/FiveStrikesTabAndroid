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
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

public class FragTickerPlayerGoalArea extends Fragment {
	
	private View view;
	Cursor model=null;
	SQLHelper helper=null;
	String tickerId=null;
    String torwartTickerId=null;
    String wurfecke=null;
    String wurfposition=null;
	FragTickerList fragTickerList;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
/* Grundlayout setzen */

		view = inflater.inflate(R.layout.ticker_goalarea, container, false);
		fragTickerList = (FragTickerList) getActivity().getSupportFragmentManager().findFragmentById(R.id.frag_ticker_list);
		
/* Daten aus Activity laden */ 
        
		Bundle args = getArguments();
		if(args!=null){
			tickerId = args.getString("TickerID");
			torwartTickerId = args.getString("TickerTorwartID");
		} 
		
/* Torwartabfrage */
        
        if(torwartTickerId==null){
        	Toast.makeText(getActivity(), getString(R.string.tickerWarnmeldungTorwartEinsatz), Toast.LENGTH_SHORT).show();
        }
		
/* Datenbank laden */
	       
        helper=new SQLHelper(getActivity());

/* Button definieren */
        
        final Button tor_ol_Button = (Button) view.findViewById(R.id.tor_ol);
        final Button tor_om_Button = (Button) view.findViewById(R.id.tor_om);
        final Button tor_or_Button = (Button) view.findViewById(R.id.tor_or);
        final Button tor_ml_Button = (Button) view.findViewById(R.id.tor_ml);
        final Button tor_mm_Button = (Button) view.findViewById(R.id.tor_mm);
        final Button tor_mr_Button = (Button) view.findViewById(R.id.tor_mr);
        final Button tor_ul_Button = (Button) view.findViewById(R.id.tor_ul);
        final Button tor_um_Button = (Button) view.findViewById(R.id.tor_um);
        final Button tor_ur_Button = (Button) view.findViewById(R.id.tor_ur);
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
        
		/* Bei Siebenmeter, den Siebenmeterpunkt einrichten */
		Cursor c=helper.getTickerCursor(tickerId);
		c.moveToFirst();
		if(Integer.parseInt(helper.getTickerAktionInt(c))==14){
			wurfposition="3_3";
			wurf_button_leeren();
			feld_3_3_Button.setText("X");
		}
		c.close();
		
        /* Button Wurfecke */
        tor_ol_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfecke="OL";
            	tor_button_leeren();
            	tor_ol_Button.setText("X");
            	uebertragen();
            }
        });
        tor_om_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfecke="OM";
            	tor_button_leeren();
            	tor_om_Button.setText("X");
            	uebertragen();
            }
        });
        tor_or_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfecke="OR";
            	tor_button_leeren();
            	tor_or_Button.setText("X");
            	uebertragen();
            }
        });
        tor_ml_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfecke="ML";
            	tor_button_leeren();
            	tor_ml_Button.setText("X");
            	uebertragen();
            }
        });
        tor_mm_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfecke="MM";
            	tor_button_leeren();
            	tor_mm_Button.setText("X");
            	uebertragen();
            }
        });
        tor_mr_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfecke="MR";
            	tor_button_leeren();
            	tor_mr_Button.setText("X");
            	uebertragen();
            }
        });
        tor_ul_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfecke="UL";
            	tor_button_leeren();
            	tor_ul_Button.setText("X");
            	uebertragen();
            }
        });
        tor_um_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfecke="UM";
            	tor_button_leeren();
            	tor_um_Button.setText("X");
            	uebertragen();
            }
        });
        tor_ur_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	wurfecke="UR";
            	tor_button_leeren();
            	tor_ur_Button.setText("X");
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
 * Abfrage, wenn die Sub-Activity das Resultat an die Main-Activty �bergibt
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
	
	public void tor_button_leeren() {

        final Button tor_ol_Button = (Button) view.findViewById(R.id.tor_ol);
        final Button tor_om_Button = (Button) view.findViewById(R.id.tor_om);
        final Button tor_or_Button = (Button) view.findViewById(R.id.tor_or);
        final Button tor_ml_Button = (Button) view.findViewById(R.id.tor_ml);
        final Button tor_mm_Button = (Button) view.findViewById(R.id.tor_mm);
        final Button tor_mr_Button = (Button) view.findViewById(R.id.tor_mr);
        final Button tor_ul_Button = (Button) view.findViewById(R.id.tor_ul);
        final Button tor_um_Button = (Button) view.findViewById(R.id.tor_um);
        final Button tor_ur_Button = (Button) view.findViewById(R.id.tor_ur);
	    tor_ol_Button.setText("");
	    tor_om_Button.setText("");
	    tor_or_Button.setText("");
	    tor_ml_Button.setText("");
	    tor_mm_Button.setText("");
	    tor_mr_Button.setText("");
	    tor_ul_Button.setText("");
	    tor_um_Button.setText("");
	    tor_ur_Button.setText("");

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
 * Zur�ck zu Spiel�bersicht und Ergebnis �bertragen
 *
 */
	
	public void uebertragen() {

		if(wurfecke!=null && wurfposition!=null){
			helper.updateTickerWurfecke(tickerId, wurfecke);
			helper.updateTickerWurfposition(tickerId, wurfposition);
			if(torwartTickerId!=null){
				helper.updateTickerWurfecke(torwartTickerId, wurfecke);
				helper.updateTickerWurfposition(torwartTickerId, wurfposition);	
			}
			
			/* Ticker aktualisieren und Fragment schlie�en */
	    	fragTickerList.refreshContent();
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
	        FragEmpty fragment = new FragEmpty();
	        fragmentTransaction.replace(R.id.frag_ticker_player, fragment);
	        fragmentTransaction.commit();
		}
	}

}