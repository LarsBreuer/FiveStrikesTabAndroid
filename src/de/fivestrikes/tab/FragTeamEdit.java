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
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class FragTeamEdit extends Fragment {
	int mannschaft_ID=0;
	EditText mannschaft_name=null;
	EditText mannschaft_kuerzel=null;
	SQLHelper helper=null;
	String mannschaftId=null;
	String lastID=null;
	FragTeamList fragTeamList;

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.frag_team_edit, container, false);
		fragTeamList = (FragTeamList) getActivity().getSupportFragmentManager()
	            .findFragmentById(R.id.frag_team_list);
		
/* Datenbank laden */
        
		helper=new SQLHelper(getActivity());

/* Daten aus Activity laden */ 
        
		Bundle args = getArguments();
		if(args!=null){
			mannschaftId = args.getString("TeamID");
		}

/* Button definieren */
        
	    mannschaft_name=(EditText) view.findViewById(R.id.mannschaftName);
	    mannschaft_kuerzel=(EditText) view.findViewById(R.id.mannschaftKuerzel);
	    
	    Button save=(Button) view.findViewById(R.id.save);
	    Button delete=(Button) view.findViewById(R.id.delete);
	    Button spielerButton = (Button) view.findViewById(R.id.spieler);
	    Button spielerImportButton = (Button) view.findViewById(R.id.spielerimport);
	    Button spielerInfoButton = (Button) view.findViewById(R.id.spielerimportinfo);
	    Button okButton = (Button) view.findViewById(R.id.okay);
	    
	    save.setOnClickListener(onSave);
	    delete.setOnClickListener(onDelete);
	    
/* Mannschaftsdaten aus Datenbank laden oder Mannschaft neu einrichten*/
        
	    load();

        
/* Button definieren */
        
        /* Okay Button (Tastaturfeld schließen) */
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE); 
            	inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
        
        /* Button Spieler verwalten */
        spielerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	helper.updateTeam(mannschaftId,
	        			  	  mannschaft_name.getText().toString(),
	        			  	  mannschaft_kuerzel.getText().toString());
				Intent newIntent = new Intent(getActivity().getApplicationContext(), PlayerActivity.class);
				newIntent.putExtra("TeamID", mannschaftId);
				startActivity(newIntent);
            }
        });
        
        /* Button Spieler importieren */
        spielerImportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	try{
            		File sdcard = Environment.getExternalStorageDirectory();
            		File file = new File(sdcard,"Download/Player.csv");
            		BufferedReader reader = new BufferedReader(new FileReader(file));
                    String line;
                    String number;
                    String name;
                    String position;
                    while ((line = reader.readLine()) != null) {
                         String[] RowData = line.split(";");
                         number = RowData[0];
                         name = RowData[1];
                         position = RowData[2];
                 		if (position.equals(getString(R.string.spielerPositionTorwartKurz))) {
                			position = getString(R.string.spielerPositionTorwart);
                		}
                		if (position.equals(getString(R.string.spielerPositionLinksaussenKurz))) {
                			position = getString(R.string.spielerPositionLinksaussen);
                		}
                		if (position.equals(getString(R.string.spielerPositionRueckraumLinksKurz))) {
                			position = getString(R.string.spielerPositionRueckraumLinks);
                		}
                		if (position.equals(getString(R.string.spielerPositionRueckraumMitteKurz))) {
                			position = getString(R.string.spielerPositionRueckraumMitte);
                		}
                		if (position.equals(getString(R.string.spielerPositionRueckraumRechtsKurz))) {
                			position = getString(R.string.spielerPositionRueckraumRechts);
                		}
                		if (position.equals(getString(R.string.spielerPositionRechtsaussenKurz))) {
                			position = getString(R.string.spielerPositionRechtsaussen);
                		}
                		if (position.equals(getString(R.string.spielerPositionKreislaeuferKurz))) {
                			position = getString(R.string.spielerPositionKreislaeufer);
                		}
                         helper.insertSpieler(name, number, mannschaftId, position);
                    }
        			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        			alertDialogBuilder
    	    			.setTitle(R.string.teamEditFertigMsgboxTitel)
    	    			.setMessage(R.string.teamEditFertigMsgboxText)
    	    			.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
    	    				public void onClick(DialogInterface dialog,int id) {
    	    					
    	    				}
    	    			});
        			AlertDialog alertDialog = alertDialogBuilder.create();
        			alertDialog.show();
            	}
            	catch (IOException e) {
            		e.printStackTrace();
        			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        			alertDialogBuilder
    	    			.setTitle(R.string.teamEditFehlerMsgboxTitel)
    	    			.setMessage(R.string.teamEditFehlerMsgboxText)
    	    			.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
    	    				public void onClick(DialogInterface dialog,int id) {
    	    					
    	    				}
    	    			});
        			AlertDialog alertDialog = alertDialogBuilder.create();
        			alertDialog.show();
            	}

            }
        });
        
        /* Button Import Info */
        spielerInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
    			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
    			alertDialogBuilder
	    			.setTitle(R.string.teamEditInfoMsgboxTitel)
	    			.setMessage(R.string.teamEditInfoMsgboxText)
	    			.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
	    				public void onClick(DialogInterface dialog,int id) {
	    					
	    				}
	    			});
    			AlertDialog alertDialog = alertDialogBuilder.create();
    			alertDialog.show();
            }
        });
		
		return view;
	}
	

/*
 * 
 * Mannschaftsdaten aus Datenbank laden
 *
 */
	
	private void load() {   
		mannschaft_name.setText(helper.getTeamName(mannschaftId));
		mannschaft_kuerzel.setText(helper.getTeamKuerzel(mannschaftId));		    
	}

/*
 * 
 * Mannschaftsdaten speichern und Mannschaftsübersicht erneuern
 *
 */
	
	private View.OnClickListener onSave=new View.OnClickListener() {
		public void onClick(View v) {
			
		    helper.updateTeam(mannschaftId,
		        		  	  mannschaft_name.getText().toString(),
		        		  	  mannschaft_kuerzel.getText().toString());  
		    fragTeamList.refreshContent();
		    
		}
	};

/*
 * 
 * Mannschaft löschen und zurück zur Mannschaftsübersicht
 *
 */
	
	private View.OnClickListener onDelete=new View.OnClickListener() {
		public void onClick(View v) {
			
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder
			.setTitle(R.string.mannschaftMsgboxTitel)
			.setMessage(R.string.mannschaftMsgboxText)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton(R.string.tickerMSGBoxJa, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {			      	
					
					if(helper.countSpielTeamId(mannschaftId)>0){	// Existiert noch ein Spiel mit dieser Mannschaft?
	        	    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
	        	    	alertDialogBuilder
	        	    		.setTitle(R.string.mannschaftDelMsgboxTitel)
	        	    		.setMessage(R.string.mannschaftDelMsgboxText)
	        	    		.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
	        					public void onClick(DialogInterface dialog,int id) {

	        					}
	        	    		});
	        	    	AlertDialog alertDialog = alertDialogBuilder.create();
	        	    	alertDialog.show();
					}
					else{
						helper.deleteTeam(mannschaftId,
		      			  		  		  mannschaft_name.getText().toString(),
		      			  		  		  mannschaft_kuerzel.getText().toString());
						FragmentManager fragmentManager = getFragmentManager();
						FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				        FragEmpty fragment = new FragEmpty();
				        fragmentTransaction.replace(R.id.frag_team_edit, fragment);
				        fragmentTransaction.commit();
						fragTeamList.refreshContent();
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