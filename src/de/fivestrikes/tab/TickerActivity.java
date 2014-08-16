package de.fivestrikes.tab;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class TickerActivity extends FragmentActivity {

	SQLHelper helper=null;
	String spielId=null;
	String lastID=null;
	FragTickerList fragTickerlist;
	//FragTickerAction fragTickerAction;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.ticker);
		spielId=getIntent().getStringExtra("GameID");
		Log.v("TickerActivity GameId", spielId);
		
/* Datenbank laden */
        
		helper=new SQLHelper(this);

/* Action Bar konfigurieren */
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		//actionBar.hide();
		actionBar.show(); //---show it again---
	
/* Fragments einrichten */
	
		
        if (findViewById(R.id.frag_ticker_list) != null) {

            if (savedInstanceState != null) {
                return;
            }
            
            FragTickerList firstFragment = new FragTickerList();
            
            Bundle args = new Bundle();
            args.putString("GameID", spielId);
            firstFragment.setArguments(args);
            
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frag_ticker_list, firstFragment).commit();
        }
        
        if (findViewById(R.id.frag_ticker_action) != null) {

            if (savedInstanceState != null) {
                return;
            }

            FragTickerAction secondFragment = new FragTickerAction();
            
            Bundle args = new Bundle();
            args.putString("GameID", spielId);
            secondFragment.setArguments(args);
            
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction().add(R.id.frag_ticker_action, secondFragment).commit();
        }
        
        if (findViewById(R.id.frag_ticker_player) != null) {

            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            FragEmpty thirdFragment = new FragEmpty();
            
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            thirdFragment.setArguments(getIntent().getExtras());
            
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
            		.add(R.id.frag_ticker_player, thirdFragment).commit();
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
			
			// Aktion für das option-menu einfügen

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
		