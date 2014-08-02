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


public class TeamActivity extends FragmentActivity {

	SQLHelper helper=null;
	String mannschaftId=null;
	String lastID=null;
	FragTeamList fragTeamList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.mannschaft);
		fragTeamList = (FragTeamList) getSupportFragmentManager()
	            .findFragmentById(R.id.frag_team_list);
		
/* Datenbank laden */
        
		helper=new SQLHelper(this);

/* Action Bar konfigurieren */
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		//actionBar.hide();
		actionBar.show(); //---show it again---
	
/* Fragments einrichten */
	
		
		// Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.frag_team_list) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            FragTeamList firstFragment = new FragTeamList();
            
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frag_team_list, firstFragment).commit();
        }
        
        if (findViewById(R.id.frag_team_edit) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            FragEmpty secondFragment = new FragEmpty();
            
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            secondFragment.setArguments(getIntent().getExtras());
            
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
            		.add(R.id.frag_team_edit, secondFragment).commit();
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
			
			// Füge neue Mannschaft ein
			
			if (helper.getLastTeamId()!=null) {		// Bereits Mannschaften vorhanden...
		    	lastID = String.valueOf(Integer.parseInt(helper.getLastTeamId()) + 1);
	        }
	        else {								// ... oder erste Mannschaft?
	        	lastID = "1";
	        }
	    	helper.insertTeam("Mannschaft " + lastID, "M" + lastID);
			mannschaftId = helper.getLastTeamId();
			Bundle args = new Bundle();
	        args.putString("TeamID", mannschaftId);
			
			// Erneuere Fragments
			
			FragmentManager fragmentManager = getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			
	        FragTeamEdit fragment = new FragTeamEdit();
	        fragmentTransaction.replace(R.id.frag_team_edit, fragment);
	        fragment.setArguments(args);
	        
	        FragTeamList fragment2 = new FragTeamList();
	        fragmentTransaction.replace(R.id.frag_team_list, fragment2);
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
		