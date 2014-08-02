package de.fivestrikes.tab;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.app.ActionBar;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class PlayerActivity extends FragmentActivity {

	SQLHelper helper=null;
	String mannschaftId=null;
	String spielerId=null;
	String lastID=null;
	FragPlayerList fragPlayerList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.spieler);
		fragPlayerList = (FragPlayerList) getSupportFragmentManager()
	            .findFragmentById(R.id.frag_player_list);
		mannschaftId=getIntent().getStringExtra("TeamID");
		Log.v("ID on create", mannschaftId);
		
/* Datenbank laden */
        
		helper=new SQLHelper(this);

/* Action Bar konfigurieren */
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		//actionBar.hide();
		actionBar.show(); //---show it again---
	
/* Fragments einrichten */
	

        if (findViewById(R.id.frag_player_list) != null) {

            if (savedInstanceState != null) {
                return;
            }

            FragPlayerList firstFragment = new FragPlayerList();
            
            Bundle args = new Bundle();
            args.putString("TeamID", mannschaftId);
            firstFragment.setArguments(args);
            
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frag_player_list, firstFragment).commit();
        }
        
        if (findViewById(R.id.frag_player_edit) != null) {

            if (savedInstanceState != null) {
                return;
            }

            FragEmpty secondFragment = new FragEmpty();
            getSupportFragmentManager().beginTransaction()
            		.add(R.id.frag_player_edit, secondFragment).commit();
        }

	}

/* Action Bar einrichten */
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		super.onCreateOptionsMenu(menu);
		CreateMenu(menu);
		return true;
		
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) 
        {
        case android.R.id.home: 
            onBackPressed();
            break;
            
            
		case 0:
			
			// Füge neuen Spieler ein
			
	    	lastID = String.valueOf(Integer.parseInt(helper.getSpielerAnzahl(mannschaftId)) + 1);
	    	helper.insertSpieler("Spieler " + lastID, lastID, mannschaftId, "");
	    	
			Cursor newC=helper.getLastSpielerId();
			newC.moveToFirst();
			spielerId = helper.getSpielerId(newC);
			newC.close();
			Bundle args = new Bundle();
	        args.putString("PlayerID", spielerId);
	        args.putString("TeamID", mannschaftId);
			
			// Erneuere Fragments
			
			FragmentManager fragmentManager = getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			
	        FragPlayerEdit fragment = new FragPlayerEdit();
	        fragmentTransaction.replace(R.id.frag_player_edit, fragment);
	        fragment.setArguments(args);
	        
	        FragPlayerList fragment2 = new FragPlayerList();
	        fragmentTransaction.replace(R.id.frag_player_list, fragment2);
	        fragment2.setArguments(args);
	        
	        fragmentTransaction.commit();
			return true;


        default:
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
	
	private void CreateMenu(Menu menu) {
	
		MenuItem mnu1 = menu.add(0, 0, 0, "Item 1"); {

			mnu1.setAlphabeticShortcut('a');
			mnu1.setIcon(R.drawable.add);
			mnu1.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			
		}
		
	}
	
}
		