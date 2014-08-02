package de.fivestrikes.tab;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class FragPlayerEdit extends Fragment {
	EditText spieler_name=null;
	EditText spieler_nummer=null;
	String spieler_position= null;
	SQLHelper helper=null;
	String mannschaftId=null;
	String spielerId=null;
	String lastID=null;
	FragPlayerList fragPlayerList;

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.frag_player_edit, container, false);
		fragPlayerList = (FragPlayerList) getActivity().getSupportFragmentManager().findFragmentById(R.id.frag_player_list);
		
/* Datenbank laden */
        
		helper=new SQLHelper(getActivity());

/* Daten aus Activity laden */ 
        
		Bundle args = getArguments();
		if(args!=null){
			mannschaftId = args.getString("TeamID");
			spielerId = args.getString("PlayerID");
		}

/* Button definieren */
        
		spieler_name=(EditText)view.findViewById(R.id.spielerName);
		spieler_nummer=(EditText)view.findViewById(R.id.spielerNummer);
	    
	    Button save=(Button)view.findViewById(R.id.savePlayer);
	    Button delete=(Button)view.findViewById(R.id.deletePlayer);
	    Button okButton = (Button) view.findViewById(R.id.okay);
	    
	    Spinner spinner = (Spinner)view.findViewById(R.id.spinnerPositionen);

	    save.setOnClickListener(onSave);
	    delete.setOnClickListener(onDelete);
	    
/* Spielerdaten aus Datenbank laden oder Spieler neu einrichten*/
        
	    if (spielerId!=null) {
	    	load(spinner);
	    }
	    else {
	    	Log.v("FragPlayerEdit - Neuer Spieler", mannschaftId);
	    	lastID = String.valueOf(Integer.parseInt(helper.getSpielerAnzahl(mannschaftId)) + 1);
	    	spieler_position = "";
	    	helper.insertSpieler("Spieler " + lastID, lastID, mannschaftId, spieler_position);
	    	spieler_name.setText("Spieler " + lastID);
	    	spieler_nummer.setText(lastID);
	    	
			Cursor newC=helper.getLastSpielerId();
			newC.moveToFirst();
			spielerId = helper.getSpielerId(newC);
			newC.close();
		}
        
/* Button beschriften */
        
        /* Okay Button */
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE); 
            	inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
        
        /* Spinner mit Inhalt füllen */
	    spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
	        @Override
	        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
	        	switch(position) {
        			case 0:
        				spieler_position = "";
        				break;
        			case 1:
        				spieler_position = getString(R.string.spielerPositionTorwart);
        				break;
        			case 2:
        				spieler_position = getString(R.string.spielerPositionLinksaussen);
        				break;
        			case 3:
        				spieler_position = getString(R.string.spielerPositionRueckraumLinks);
        				break;
        			case 4:
        				spieler_position = getString(R.string.spielerPositionRueckraumMitte);
        				break;
        			case 5:
        				spieler_position = getString(R.string.spielerPositionRueckraumRechts);
        				break;
        			case 6:
        				spieler_position = getString(R.string.spielerPositionRechtsaussen);
        				break;
        			case 7:
        				spieler_position = getString(R.string.spielerPositionKreislaeufer);
        				break;
	        	}
	        }

	        @Override
	        public void onNothingSelected(AdapterView<?> parentView) {
	            // vorübergehend freigelassen
	        }

	    });
		
		return view;
	}

	
/*
 * 
 * Textfelder und Spinner einrichten 
 *
 */
		
	
	public void load(Spinner spinner) {   
		spieler_name.setText(helper.getSpielerName(spielerId));
		spieler_nummer.setText(helper.getSpielerNummer(spielerId));
		String position = helper.getSpielerPosition(spielerId);
		Resources res = getResources();
		
		spinner.setSelection(0);
		spieler_position = "";
		if (position.equals(res.getString(R.string.spielerPositionTorwart))) {
			spinner.setSelection(1);
			spieler_position = position;
		}
		if (position.equals(res.getString(R.string.spielerPositionLinksaussen))) {
			spinner.setSelection(2);
			spieler_position = position;
		}
		if (position.equals(res.getString(R.string.spielerPositionRueckraumLinks))) {
			spinner.setSelection(3);
			spieler_position = position;
		}
		if (position.equals(res.getString(R.string.spielerPositionRueckraumMitte))) {
			spinner.setSelection(4);
			spieler_position = position;
		}
		if (position.equals(res.getString(R.string.spielerPositionRueckraumRechts))) {
			spinner.setSelection(5);
			spieler_position = position;
		}
		if (position.equals(res.getString(R.string.spielerPositionRechtsaussen))) {
			spinner.setSelection(6);
			spieler_position = position;
		}
		if (position.equals(res.getString(R.string.spielerPositionKreislaeufer))) {
			spinner.setSelection(7);
			spieler_position = position;
		}
	}


/*
 * 
 * Mannschaftsdaten speichern und Mannschaftsübersicht erneuern
 *
 */
	
	private View.OnClickListener onSave=new View.OnClickListener() {
		public void onClick(View v) {
			
		    helper.updateSpieler(spielerId,
   		  		 				 spieler_name.getText().toString(),
   		  		 				 spieler_nummer.getText().toString(),
   		  		 				 spieler_position); 
		    fragPlayerList.refreshContent();
		    
		}
	};

/*
 * 
 * Spieler löschen und zurück zur Spielerübersicht
 *
 */
	
	private View.OnClickListener onDelete=new View.OnClickListener() {
		public void onClick(View v) {
			
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder
			.setTitle(R.string.spielerMsgboxTitel)
			.setMessage(R.string.spielerMsgboxText)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton(R.string.tickerMSGBoxJa, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {			      	
					
					if(helper.countTickerSpielerId(spielerId)>0){	// Existiert noch ein Spiel mit dieser Mannschaft?
	        	    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
	        	    	alertDialogBuilder
        	    			.setTitle(R.string.spielerDelMsgboxTitel)
        	    			.setMessage(R.string.spielerDelMsgboxText)
	        	    		.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
	        					public void onClick(DialogInterface dialog,int id) {

	        					}
	        	    		});
	        	    	AlertDialog alertDialog = alertDialogBuilder.create();
	        	    	alertDialog.show();
					}
					else{
						helper.deleteSpieler(spielerId,
			        			  			 spieler_name.getText().toString(),
			        			  			 spieler_nummer.getText().toString());
						
						
						FragmentManager fragmentManager = getFragmentManager();
						FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				        FragEmpty fragment = new FragEmpty();
				        fragmentTransaction.replace(R.id.frag_player_edit, fragment);
				        fragmentTransaction.commit();
				        
						
						/*
						FragmentManager fragmentManager = getFragmentManager();
						Fragment currentFragment = (Fragment) fragmentManager.findFragmentById(R.id.frag_player_edit);
						FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				        fragmentTransaction.remove(currentFragment);
				        fragmentTransaction.commit();
				        */
						
						
						fragPlayerList.refreshContent();
					}
				}
			})
			.setNegativeButton(R.string.tickerMSGBoxNein, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {			      	
					
				}
			})
			.show();
		}
	};
}