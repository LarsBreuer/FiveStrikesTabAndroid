package de.fivestrikes.tab;

import de.fivestrikes.pro.SQLHelper;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

public class FragTickerStat extends Fragment {
	
	private View view;
	public static Long elapsedTime=Long.parseLong("0");
	private static final int GET_CODE = 0;
	Cursor model=null;
	SQLHelper helper=null;
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
	final Context context = FragTickerStat.this;
	View tabview;
	private TabHost mTabHost;
	Cursor c=null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.frag_ticker_stat, container, false);
		
/* Daten aus Activity laden */ 
        
		Bundle args = getArguments();
		if(args!=null){
			gameId = args.getString("GameID");
		} 
		
		return view;
		
	}
	
}