package de.fivestrikes.tab;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.fivestrikes.tab.FragPlayerList.PlayerListAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import java.util.Calendar;

import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.Toast;

import de.fivestrikes.tab.FragDatePicker;

public class FragGameEdit extends Fragment {
	private static final int FRAGMENT_CODE = 0;
	int spiel_ID=0;
	EditText spiel_halbzeitlaenge=null;
	SQLHelper helper=null;
	String gameId=null;
	String mannschaftId=null;
	String teamHomeId=null;
	String teamAwayId=null;
	String lastID=null;
	String haTeam=null;
	int heimID=0;
	int auswID=0;
	Date spielDatum=null;
	String strSpielDatum = null;
	String strHalbzeit=null;
	private int year;
	private int month;
	private int day;
	static final int DATE_DIALOG_ID = 999;
	private View view;
	FragGameList fragGameList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.frag_game_edit, container, false);
		fragGameList = (FragGameList) getActivity().getSupportFragmentManager()
	            .findFragmentById(R.id.frag_game_list);

/* Button definieren */
        
		spiel_halbzeitlaenge=(EditText)view.findViewById(R.id.spielHalbzeitlaenge);
	    
		Button save=(Button)view.findViewById(R.id.save);
	    Button delete=(Button)view.findViewById(R.id.delete);
	    Button tickerButton=(Button)view.findViewById(R.id.ticker);
	    Button btnDatum=(Button)view.findViewById(R.id.spielDatum);
	    Button btnHeim=(Button)view.findViewById(R.id.spielHeim);
	    Button btnAusw=(Button)view.findViewById(R.id.spielAusw);
	    
	    Spinner spinner = (Spinner)view.findViewById(R.id.spinnerPositionen);

	    save.setOnClickListener(onSave);
	    delete.setOnClickListener(onDelete);
	    
/* Datenbank laden */
        
		helper=new SQLHelper(getActivity());

/* Daten aus Activity laden */ 
        
		Bundle args = getArguments();
		if(args!=null){
			gameId = args.getString("GameID");
			day = args.getInt("Day");
			month = args.getInt("Month");
			year = args.getInt("Year");
			if(day!=0 && month!=0 && year!=0){
				spielDatum = helper.setDate(day, month, year);
			}
			strHalbzeit = args.getString("Halftime");
			heimID = args.getInt("HomeID");
			auswID = args.getInt("AwayID");
			args=null;
		} 
	    
		
/* Spielerdaten aus Datenbank laden oder Spieler neu einrichten*/
        
	    if (gameId!=null && spielDatum==null) {   // Wurde ein bestehendes Spiel ausgewählt, das noch nicht geändert wurde...
	    	load(view);
	    }
	    if (gameId==null && spielDatum==null) {	  // ... falls kein bestehendes Spiel ausgewählt wurde    	
	    	// Halbzeitlänge definieren
	    	strHalbzeit = "30";
	    	
	    	// Aktuelles Datum ermitteln
			final Calendar c = Calendar.getInstance();
			year = c.get(Calendar.YEAR);
			month = c.get(Calendar.MONTH);
			day = c.get(Calendar.DAY_OF_MONTH);
			
			spielDatum=new Date(year-1900, month, day);
	    }
        
/* Button beschriften */
        
	    if(heimID!=0){
	    	btnHeim.setText(helper.getTeamName(String.valueOf(heimID)));
	    }
	    if(auswID!=0){
	    	btnAusw.setText(helper.getTeamName(String.valueOf(auswID)));
	    }
	    
	    btnDatum.setText(new StringBuilder()
				// Month is 0 based, just add 1
				.append(day).append(".").append(month + 1).append(".")
				.append(year).append(" "));
	    
		spiel_halbzeitlaenge.setText(strHalbzeit);

        /* Button Datum */
        btnDatum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	
            	showDatePicker();
            	
            }
        });
        
        /* Button Ticker aufrufen */
        tickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        		
        	    if (heimID==0 || auswID==0 || spielDatum==null) {
        	    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        	    	alertDialogBuilder
        	    		.setTitle(R.string.tickerMsgboxTitel)
        	    		.setMessage(R.string.tickerMsgboxText)
        	    		.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
        					public void onClick(DialogInterface dialog,int id) {

        					}
        	    		});
        	    	AlertDialog alertDialog = alertDialogBuilder.create();
        	    	alertDialog.show();
        	    }
        	    else {
            		if (gameId!=null) {   // Wurde ein bestehendes Spiel ausgewählt...
            			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            			String strSpielDatum = dateFormat.format(spielDatum);
            			helper.updateSpiel(gameId,
            							   strSpielDatum,
            							   Integer.parseInt(spiel_halbzeitlaenge.getText().toString()),
            							   heimID,
            							   auswID);
        				helper.updateTickerSpieler(gameId);	// Spielernamen in Tickermeldungen schreiben
        				
            			Intent i = new Intent(getActivity().getApplicationContext(), TickerActivity.class);
        				i.putExtra("GameID", gameId);
        				startActivity(i);

            		}
            		else {				  // ... falls kein bestehendes Spiel ausgewählt wurde
            			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            			String strSpielDatum = dateFormat.format(spielDatum);
            			helper.insertSpiel(strSpielDatum,
            							   Integer.parseInt(spiel_halbzeitlaenge.getText().toString()),
        	    					   	   heimID,
        	    					   	   auswID);
            			
            			Intent i = new Intent(getActivity().getApplicationContext(), TickerActivity.class);
        				i.putExtra("GameID", gameId);
        				startActivity(i);
            			
            		}
        	    }
            }
        });
        
        /* Button Heimmannschaft aufrufen */
        btnHeim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	
            	// Abfragen, ob schon eine Aktion im Spiel eingegeben wurde
            	// Falls ja, kann die Mannschaft nicht mehr geändert werden
            	if(gameId!=null){
            		if(helper.countTickerAktionGesamt(gameId)>0){
            			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            			alertDialogBuilder
        	    			.setTitle(R.string.spielTeamauswahlMsgboxTitel)
        	    			.setMessage(R.string.spielTeamauswahlMsgboxText)
        	    			.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
        	    				public void onClick(DialogInterface dialog,int id) {

        	    				}
        	    			});
            			AlertDialog alertDialog = alertDialogBuilder.create();
            			alertDialog.show();
            		} else {
            			
            			haTeam="Heim";
            			
            			FragmentManager fragmentManager = getFragmentManager();
            			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            	        FragTeamSelect fragment = new FragTeamSelect();
            	        
            	        Bundle args = new Bundle();
            	        args.putString("haTeam", haTeam);
            	        args.putString("GameID", gameId);
            	        args.putInt("Day", helper.getDay(spielDatum));
            	        args.putInt("Month", helper.getMonth(spielDatum));
            	        args.putInt("Year", helper.getYear(spielDatum));
            	        args.putString("Halftime", spiel_halbzeitlaenge.getText().toString());
            	        args.putInt("HomeID", heimID);
            	        args.putInt("AwayID", auswID);
            	        
            	        fragment.setArguments(args);
            	        //fragment.setTargetFragment(FragGameEdit.this, FRAGMENT_CODE);
            	        fragmentTransaction.replace(R.id.frag_game_edit, fragment);
            	        //fragmentTransaction.addToBackStack(null);
            	        fragmentTransaction.commit();

            		}
            	} else{
            		
        			haTeam="Heim";
        			
        			FragmentManager fragmentManager = getFragmentManager();
        			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        	        FragTeamSelect fragment = new FragTeamSelect();
        	        
        	        Bundle args = new Bundle();
        	        args.putString("haTeam", haTeam);
        	        args.putString("GameID", gameId);
        	        args.putInt("Day", helper.getDay(spielDatum));
        	        args.putInt("Month", helper.getMonth(spielDatum));
        	        args.putInt("Year", helper.getYear(spielDatum));
        	        args.putString("Halftime", spiel_halbzeitlaenge.getText().toString());
        	        args.putInt("HomeID", heimID);
        	        args.putInt("AwayID", auswID);
        	        
        	        fragment.setArguments(args);
        	        //fragment.setTargetFragment(FragGameEdit.this, FRAGMENT_CODE);
        	        fragmentTransaction.replace(R.id.frag_game_edit, fragment);
        	        //fragmentTransaction.addToBackStack(null);
        	        fragmentTransaction.commit();
        	        
            	}
            }
        });
        
        /* Button Auswärtsmannschaft aufrufen */
        btnAusw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	
            	// Abfragen, ob schon eine Aktion im Spiel eingegeben wurde
            	// Falls ja, kann die Mannschaft nicht mehr geändert werden
            	if(gameId!=null){
            		if(helper.countTickerAktionGesamt(gameId)>0){
            			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            			alertDialogBuilder
        	    			.setTitle(R.string.spielTeamauswahlMsgboxTitel)
        	    			.setMessage(R.string.spielGegnerauswahlMsgboxText)
        	    			.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
        	    				public void onClick(DialogInterface dialog,int id) {
        	    					
        	    				}
        	    			});
            			AlertDialog alertDialog = alertDialogBuilder.create();
            			alertDialog.show();
            		} else {
            			
            			haTeam="Auswaerts";
            			
            			FragmentManager fragmentManager = getFragmentManager();
            			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            	        FragTeamSelect fragment = new FragTeamSelect();
            	        
            	        Bundle args = new Bundle();
            	        args.putString("haTeam", haTeam);
            	        args.putString("GameID", gameId);
            	        args.putInt("Day", helper.getDay(spielDatum));
            	        args.putInt("Month", helper.getMonth(spielDatum));
            	        args.putInt("Year", helper.getYear(spielDatum));
            	        args.putString("Halftime", spiel_halbzeitlaenge.getText().toString());
            	        args.putInt("HomeID", heimID);
            	        args.putInt("AwayID", auswID);
            	        
            	        fragment.setArguments(args);
            	        //fragment.setTargetFragment(FragGameEdit.this, FRAGMENT_CODE);
            	        fragmentTransaction.replace(R.id.frag_game_edit, fragment);
            	        //fragmentTransaction.addToBackStack(null);
            	        fragmentTransaction.commit();

            		}
            	} else{
            		
        			haTeam="Auswaerts";
        			
        			FragmentManager fragmentManager = getFragmentManager();
        			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        	        FragTeamSelect fragment = new FragTeamSelect();
        	        
        	        Bundle args = new Bundle();
        	        args.putString("haTeam", haTeam);
        	        args.putString("GameID", gameId);
        	        args.putInt("Day", helper.getDay(spielDatum));
        	        args.putInt("Month", helper.getMonth(spielDatum));
        	        args.putInt("Year", helper.getYear(spielDatum));
        	        args.putString("Halftime", spiel_halbzeitlaenge.getText().toString());
        	        args.putInt("HomeID", heimID);
        	        args.putInt("AwayID", auswID);
        	        
        	        fragment.setArguments(args);
        	        //fragment.setTargetFragment(FragGameEdit.this, FRAGMENT_CODE);
        	        fragmentTransaction.replace(R.id.frag_game_edit, fragment);
        	        //fragmentTransaction.addToBackStack(null);
        	        fragmentTransaction.commit();
        			
            	}
            }
        });
		
		return view;
	}

/*
 * 
 * Date Picker definieren
 *
 */
	
	private void showDatePicker() {
		FragDatePicker date = new FragDatePicker();

		Calendar calender = Calendar.getInstance();
		Bundle args = new Bundle();
		args.putInt("year", calender.get(Calendar.YEAR));
		args.putInt("month", calender.get(Calendar.MONTH));
		args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
		date.setArguments(args);

		date.setCallBack(ondate);
		date.show(getActivity().getSupportFragmentManager(), "Date Picker");
	
	}

	OnDateSetListener ondate = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
			  
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;
			
			// set current date to buttontext
			
			Button btnDatum=(Button) getView().findViewById(R.id.spielDatum);
			btnDatum.setText(new StringBuilder()
				// Month is 0 based, just add 1
				.append(day).append(".").append(month + 1).append(".")
				.append(year).append(" "));
			
			spielDatum=new Date(year-1900, month, day);

		}
	};

	
	@Override
	public void onDestroy() {
		super.onDestroy();

	}

/*
 * 
 * Textfelder und Button einrichten 
 *
 */
			
	public void load(View view) {   

		strHalbzeit = helper.getSpielHalbzeitlaenge(gameId);
		heimID = Integer.parseInt(helper.getSpielHeim(gameId));
		auswID = Integer.parseInt(helper.getSpielAusw(gameId));	
		strSpielDatum = helper.getSpielDatum(gameId);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try
		{
			spielDatum = sdf.parse(strSpielDatum);
			day = spielDatum.getDate();    // getDate => Day of month; getDay => day of week
			month = spielDatum.getMonth();
			year = spielDatum.getYear()+1900;
		}
		catch (ParseException e) {}
		
	}

	

/*
 * 
 * Spiel speichern
 *
 */
	
	
	private View.OnClickListener onSave=new View.OnClickListener() {
		public void onClick(View v) {
    	    if (heimID==0 || auswID==0) {
    	    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
    	    	alertDialogBuilder
    	    		.setTitle(R.string.spielMsgboxTitel)
    	    		.setMessage(R.string.spielMsgboxText)
    	    		.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog,int id) {

    					}
    	    		});
    	    	AlertDialog alertDialog = alertDialogBuilder.create();
    	    	alertDialog.show();
    	    }
    	    else {
    	    	if (gameId!=null) {   // Wurde ein bestehendes Spiel ausgewählt...
    	    		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	    		strSpielDatum = dateFormat.format(spielDatum);
    	    		helper.updateSpiel(gameId,
    	    						   strSpielDatum,
    	    						   Integer.parseInt(strHalbzeit),
    	    						   heimID,
    	    						   auswID);
    	    		
    	    		fragGameList.refreshContent();
    	    		
    	    	}
    	    	else {				  // ... falls kein bestehendes Spiel ausgewhält wurde
    	    		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	    		strSpielDatum = dateFormat.format(spielDatum);
    	    		helper.insertSpiel(strSpielDatum,
    	    						   Integer.parseInt(strHalbzeit),
    	    						   heimID,
    	    						   auswID);
    	    		gameId = helper.getLastSpielId();
    	    		
    	    		Bundle args = new Bundle();
    	    		args.putString("GameID", gameId);
    	    		
    	    		FragmentManager fragmentManager = getFragmentManager();
    	    		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    	    		FragGameList fragment = new FragGameList();
    	    		fragment.setArguments(args);
    		        fragmentTransaction.replace(R.id.frag_game_list, fragment);
    		        fragmentTransaction.commit();
    	    		
    	    		Log.v("FragGameEdit onSave", String.valueOf(gameId));
    	    		
    	    		fragGameList.refreshContent();
    	    		
    	    		
    	    		/*
    	    		finish();
    	    		*/
    	    		
    	    		
    	    		
    	    	}
    	    }

		}
	};

/*
 * 
 * Spiel löschen
 *
 */
	
	private View.OnClickListener onDelete=new View.OnClickListener() {
		public void onClick(View v) {
			
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder
			.setTitle(R.string.spielDelMsgboxTitel)
			.setMessage(R.string.spielDelMsgboxText)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton(R.string.tickerMSGBoxJa, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {			      	
					if (gameId!=null) {
						helper.deleteSpiel(gameId);
						Log.v("FragGameEdit onDelete", "Spiel " + String.valueOf(gameId) + " gelöscht");
					}
					
					FragmentManager fragmentManager = getFragmentManager();
					FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			        FragEmpty fragment = new FragEmpty();
			        fragmentTransaction.replace(R.id.frag_game_edit, fragment);
			        fragmentTransaction.commit();
			        
					fragGameList.refreshContent();
		
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