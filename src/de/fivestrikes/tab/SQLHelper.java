package de.fivestrikes.tab;

import android.content.Context;
import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.util.Calendar;
import java.util.Date;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.text.SimpleDateFormat;

class SQLHelper extends SQLiteOpenHelper {
  private static final String DATABASE_NAME="fivestrikestab.db";
  private static final int SCHEMA_VERSION=1;
  private String minutes,seconds;
  private long secs,mins;
  private Cursor c=null;
  private String strResult=null;
  
  public SQLHelper(Context context) {
    super(context, DATABASE_NAME, null, SCHEMA_VERSION);
  }

/*
 * 
 * Tabellen definieren 
 *
 */
	
  @Override
  public void onCreate(SQLiteDatabase db) {
	  db.execSQL("CREATE TABLE team (_id INTEGER PRIMARY KEY AUTOINCREMENT, teamName TEXT, teamKuerzel TEXT, spielerAnzahl INTEGER);");
	  db.execSQL("CREATE TABLE spieler (_id INTEGER PRIMARY KEY AUTOINCREMENT, teamID INTEGER, spielerName TEXT, " +
		  		 "spielerNummer Text, spielerPosition TEXT, spielerID INTEGER, spielerAktiv INTEGER);");
	  db.execSQL("CREATE TABLE spiel (_id INTEGER PRIMARY KEY AUTOINCREMENT, teamHeim INTEGER, teamAuswaerts INTEGER, " +
	  			 "aktuelleHalbzeit INTEGER, ballbesitz INTEGER, halbzeitLaenge INTEGER, spielDatum STRING, toreHeim INTEGER, " +
	  			 "toreAuswaerts INTEGER, zeitAktuell DATE, zeitBisher LONG, zeitStart LONG, zeitTicker INTEGER, " +
	  			 "torwartHeim INTEGER, torwartAuswaerts INTEGER, spielerEingabe INTEGER DEFAULT 1, spielerWurfecke INTEGER DEFAULT 1, " +
	  			 "notiz TEXT);");
	  db.execSQL("CREATE TABLE ticker (_id INTEGER PRIMARY KEY AUTOINCREMENT, aktionInteger INTEGER, aktionString STRING, aktionTeamHeim INTEGER, spielerAktion STRING, " +
		  		"spielerID INTEGER, tickerHeimTore INTEGER, tickerAuswaertsTore INTEGER, tickerErgebnisString STRING, tickerID INTEGER, wurfecke STRING, zeitInteger INTEGER, " +
		  		"zeitString STRING, spielID INTEGER, wurfposition STRING, realzeit STRING);");
	  db.execSQL("CREATE TABLE statSpiel (_id INTEGER PRIMARY KEY AUTOINCREMENT, spielHeim STRING, spielBezeichnung STRING, spielAusw STRING);");
	  db.execSQL("CREATE TABLE statTor (_id INTEGER PRIMARY KEY AUTOINCREMENT, torName STRING, torTore STRING, torFehl STRING, torProzent STRING);");
	  db.execSQL("CREATE TABLE statSpieler (_id INTEGER PRIMARY KEY AUTOINCREMENT, spielerBezeichnung STRING, spielerWert STRING);");
	  db.execSQL("CREATE TABLE statTickerTor (_id INTEGER PRIMARY KEY AUTOINCREMENT, tickerTorName STRING, " +
      		"tickerTorTeam STRING, tickerTorTore STRING, tickerTorFehl STRING, tickerTorProzent STRING);");
  }
  
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	  
  }


/*
 * 
 * Mannschaften
 *
 */
  
/* Datenbankabfrage */
  
  public Cursor getAllTeam() {
	  return(getReadableDatabase()
			  .rawQuery("SELECT _id, teamName, teamKuerzel, spielerAnzahl FROM team ORDER BY teamName ASC", null));
  }

  public Cursor getTeamById(String id) {
	  String[] args={id};

	  return(getReadableDatabase()
			  .rawQuery("SELECT _id, teamName, teamKuerzel, spielerAnzahl FROM team WHERE _ID=?", args));
  }
  
  public String getLastTeamId() {
	  
	  SQLiteDatabase db = getReadableDatabase();
	  Cursor c = db.rawQuery("SELECT * FROM team WHERE _ID=(SELECT MAX(_ID) FROM team)", null);
	  c.moveToFirst();
	  if(c.getCount()!=0){
		  strResult = getTeamId(c);
	  }
	  c.close();
	  return(strResult);

  }
  
  public Cursor getTeamCursor(String mannschaftId) {
	  String[] args={mannschaftId};
	  
	  return(getReadableDatabase()
			  .rawQuery("SELECT * FROM team WHERE _ID=?", args));
  }

  public void deleteAllTeam() {

	getWritableDatabase().delete("team", null, null);
		
  }
  
/* Team bearbeiten */
  
  public void insertTeam(String teamName, String teamKuerzel) {
	  ContentValues cv=new ContentValues();
      
	  cv.put("teamName", teamName);
	  cv.put("teamKuerzel", teamKuerzel);
	  cv.put("spielerAnzahl", 0);

	  getWritableDatabase().insert("team", "teamName", cv);
  }
  
  public void insertTeamImport(String teamName, String teamKuerzel, Integer spielerAnzahl) {
	  ContentValues cv=new ContentValues();
      
	  cv.put("teamName", teamName);
	  cv.put("teamKuerzel", teamKuerzel);
	  cv.put("spielerAnzahl", spielerAnzahl);

	  getWritableDatabase().insert("team", "teamName", cv);
  }

  public void updateTeam(String id, String teamName, String teamKuerzel) {
	  ContentValues cv=new ContentValues();
	  String[] args={id};
	  
	  cv.put("teamName", teamName);
	  cv.put("teamKuerzel", teamKuerzel);

	  getWritableDatabase().update("team", cv, "_ID=?", args);
  }

  public void deleteTeam(String id, String teamName, String teamKuerzel) {
	  String[] args={id};

	  getWritableDatabase().delete("team", "_ID=?", args);
	  getWritableDatabase().delete("spieler", "teamID=?", args);
  }
  
/* Teamdaten abfragen */
  
  public String getTeamNameById(String teamId) {
	  return(getTeamName(teamId));
  }
  
  public String getTeamId(Cursor c) {
	  return(c.getString(0));
  }
  
  public String getTeamName(String teamId) {
	  return(getTeamString(teamId, 1));
  }
	  
  public String getTeamKuerzel(String teamId) {
	  return(getTeamString(teamId, 2));
  }
  
  public String getSpielerAnzahl(String teamId) {
	  return(getTeamString(teamId, 3));
  }
  
  public String getTeamString(String teamId, Integer number) {
	  c=getTeamById(teamId);
	  c.moveToFirst();
      if(c.getCount()>0){
    	  strResult=c.getString(number);
	  } else {
		  strResult="";
	  }
      c.close();
	  return(strResult);
  }

/*
 * 
 * Spieler 
 *
 */
  
/* Datenbankabfrage */
  
  public Cursor getAllSpieler(String id) {
	  
	  return(getReadableDatabase()
			  .rawQuery("SELECT * FROM spieler WHERE teamID = ? ORDER BY cast(spielerNummer as integer) ASC", new String[] { id }));
  }
  
  public Cursor getAllSpielerAlle() {
	  return(getReadableDatabase().rawQuery("SELECT * FROM spieler", null));
  }
  
  public Cursor getSpielerById(String id) {
	  String[] args={id};

	  return(getReadableDatabase()
			  .rawQuery("SELECT _id, teamID, spielerName, spielerNummer, spielerPosition FROM spieler WHERE _ID=?", args));
  }
  
  public Cursor getLastSpielerId() {

	  return(getReadableDatabase()
			  .rawQuery("SELECT * FROM spieler WHERE _ID=(SELECT MAX(_ID) FROM spieler)", null));
  }
  
  public void deleteAllSpieler() {

	getWritableDatabase().delete("spieler", null, null);
		
  }
  
/* Spieler bearbeiten */
  
  public void insertSpieler(String spielerName, String spielerNummer, String teamID, String spielerPosition) {
	  ContentValues cv=new ContentValues();
	  ContentValues cvs=new ContentValues();

	  int spielerID = (Integer.parseInt(teamID) * 1000) + Integer.parseInt(spielerNummer);
	  
	  cv.put("TeamID", teamID);
	  cv.put("spielerName", spielerName);
	  cv.put("spielerNummer", spielerNummer);
	  cv.put("spielerID", spielerID);
	  cv.put("spielerPosition", spielerPosition);
	  getWritableDatabase().insert("spieler", "spielerName", cv);
	  
	  cvs.put("spielerAnzahl", Integer.parseInt(spielerNummer));
	  String[] args={teamID};
	  getWritableDatabase().update("team", cvs, "_ID=?", args);
  }
  
  public void insertSpielerImport(Integer teamID, String spielerName, String spielerNummer, String spielerPosition, Integer spielerID) {
	  ContentValues cv=new ContentValues();

	  cv.put("TeamID", teamID);
	  cv.put("spielerName", spielerName);
	  cv.put("spielerNummer", spielerNummer);
	  cv.put("spielerID", spielerID);
	  cv.put("spielerPosition", spielerPosition);
	  getWritableDatabase().insert("spieler", "spielerName", cv);
  }
  
  public void updateSpieler(String id, String spielerName, String spielerNummer, String spielerPosition) {
	  ContentValues cv=new ContentValues();
	  String[] args={id};
	  
	  cv.put("spielerName", spielerName);
	  cv.put("spielerNummer", spielerNummer);
	  cv.put("spielerPosition", spielerPosition);

	  getWritableDatabase().update("spieler", cv, "_ID=?", args);
  }
  
  public void updateSpielerId(String id, String teamId) {
	  ContentValues cv=new ContentValues();
	  String[] args={id};
	  
	  cv.put("teamID", teamId);

	  getWritableDatabase().update("spieler", cv, "_ID=?", args);
  }

  public void deleteSpieler(String id, String spielerName, String spielerNummer) {
	  String[] args={id};

	  getWritableDatabase().delete("spieler", "_ID=?", args);
  }
  
/* Spielerdaten abfragen */
  
  public String getSpielerNummerById(String spielerId) {
	  
	  String spielerNummer=null;
	  if(getSpielerNummer(spielerId)!=null){
		  spielerNummer=getSpielerNummer(spielerId);
	  } else {
		  spielerNummer="";
	  }
	  return(spielerNummer);
	  
  }
  
  public String getSpielerId(Cursor c) {
	  return(c.getString(0));
  }
  
  public String getSpielerTeamId(String spielerId) {
	  return(getSpielerString(spielerId, 1));
  }
  
  public String getSpielerName(String spielerId) {
	  return(getSpielerString(spielerId, 2));
  }
	  
  public String getSpielerNummer(String spielerId) {
	  return(getSpielerString(spielerId, 3));
  }
  
  public String getSpielerPosition(String spielerId) {
	  return(getSpielerString(spielerId, 4));
  }
  
  public String getSpielerString(String spielerId, Integer number) {
	  c=getSpielerById(spielerId);
      c.moveToFirst();
      if(c.getCount()>0){
    	  strResult=c.getString(number);
	  } else {
		  strResult="";
	  }
      c.close();
	  return(strResult);
  }

/*
 * 
 * Spiel 
 *
 */
  
/* Datenbanabfrage */

  public Cursor getAllSpiel() {
	  return(getReadableDatabase()
			  .rawQuery("SELECT _id, teamHeim, teamAuswaerts, aktuelleHalbzeit, ballbesitz, halbzeitLaenge, spielDatum, toreHeim, toreAuswaerts, zeitAktuell, zeitBisher, " +
			  		"zeitStart, zeitTicker FROM spiel ORDER BY spielDatum DESC", null));
  }
  
  public Cursor getAllSpielAlle() {
	  return(getReadableDatabase().rawQuery("SELECT * FROM spiel", null));
  }
  
  public Cursor getSpielCursor(String spielId) {
	  String[] args={spielId};
	  
	  return(getReadableDatabase()
			  .rawQuery("SELECT * FROM spiel WHERE _ID=?", args));
  }
  
  public String getLastSpielId() {
	  SQLiteDatabase db = getReadableDatabase();
	  Cursor c = db.rawQuery("SELECT * FROM spiel WHERE _ID=(SELECT MAX(_ID) FROM spiel)", null);
	  c.moveToFirst();
	  strResult = getSpielId(c);
	  c.close();
	  return(strResult);
  }
  
  public void deleteAllSpiel() {

	getWritableDatabase().delete("spiel", null, null);
		
  }
  
/* Spiele zählen */

  public int countSpielTeamId(String id) {
	  SQLiteDatabase db = getReadableDatabase();
	  Cursor mCount= db.rawQuery("SELECT count(*) FROM spiel WHERE teamHeim = ? OR teamAuswaerts = ?", new String[] { id, id });
	  mCount.moveToFirst();
	  int count= mCount.getInt(0);
	  mCount.close();
	  return(count);
  }
  
/* Spiele bearbeiten */

  public void insertSpiel(String spielDatum, Integer halbzeitlaenge, Integer teamHeim, Integer teamAuswaerts) {
	  ContentValues cv=new ContentValues();
	  
	  cv.put("spielDatum", spielDatum);
	  if(halbzeitlaenge!=null){
		  cv.put("halbzeitLaenge", halbzeitlaenge);
	  } else {
		  cv.put("halbzeitLaenge", 30);
	  }
	  cv.put("teamHeim", teamHeim);
	  cv.put("teamAuswaerts", teamAuswaerts);
	  cv.put("aktuelleHalbzeit", 0);
	  cv.put("ballbesitz", 2);
	  cv.put("spielerEingabe", 1);
	  cv.put("spielerWurfecke", 1);
	  
	  getWritableDatabase().insert("spiel", "spielDatum", cv);
  }
  
  public void insertSpielImport(int teamHeim, int teamAuswaerts, int aktuelleHalbzeit, int ballbesitz, 
		  int halbzeitlaenge, String spielDatum, int toreHeim, int toreAuswaerts) {
	  ContentValues cv=new ContentValues();
	  
	  cv.put("spielDatum", spielDatum);
	  cv.put("halbzeitLaenge", halbzeitlaenge);
	  cv.put("teamHeim", teamHeim);
	  cv.put("teamAuswaerts", teamAuswaerts);
	  cv.put("aktuelleHalbzeit", aktuelleHalbzeit);
	  cv.put("ballbesitz", ballbesitz);
	  cv.put("toreHeim", toreHeim);
	  cv.put("toreAuswaerts", toreAuswaerts);
	  
	  getWritableDatabase().insert("spiel", "spielDatum", cv);
  }
  
  public void insertSpielImportOhneTore(int teamHeim, int teamAuswaerts, int aktuelleHalbzeit, int ballbesitz, 
		  int halbzeitlaenge, String spielDatum) {
	  ContentValues cv=new ContentValues();
	  
	  cv.put("spielDatum", spielDatum);
	  cv.put("halbzeitLaenge", halbzeitlaenge);
	  cv.put("teamHeim", teamHeim);
	  cv.put("teamAuswaerts", teamAuswaerts);
	  cv.put("aktuelleHalbzeit", aktuelleHalbzeit);
	  cv.put("ballbesitz", ballbesitz);
	  
	  getWritableDatabase().insert("spiel", "spielDatum", cv);
  }
  
  public void updateSpiel(String id, String spielDatum, Integer halbzeitlaenge, Integer teamHeim, Integer teamAuswaerts) {
	  ContentValues cv=new ContentValues();
	  String[] args={id};
	  
	  cv.put("spielDatum", spielDatum);
	  cv.put("halbzeitLaenge", halbzeitlaenge);
	  cv.put("teamHeim", teamHeim);
	  cv.put("teamAuswaerts", teamAuswaerts);

	  getWritableDatabase().update("spiel", cv, "_ID=?", args);
  }
  
  public void updateSpielTicker(String id, Long zeitBisher, Long zeitStart) {
	  ContentValues cv=new ContentValues();
	  String[] args={id};
	  
	  cv.put("zeitBisher", zeitBisher);
	  cv.put("zeitStart", zeitStart);

	  getWritableDatabase().update("spiel", cv, "_ID=?", args);
  }
  
  public void updateSpielBallbesitz(String id, Integer ballbesitz) {
	  ContentValues cv=new ContentValues();
	  String[] args={id};
	  
	  cv.put("ballbesitz", ballbesitz);

	  getWritableDatabase().update("spiel", cv, "_ID=?", args);
  }
  
  public void updateSpielAktuelleHalbzeit(String id, Integer aktuelleHalbzeit) {
	  ContentValues cv=new ContentValues();
	  String[] args={id};
	  
	  cv.put("aktuelleHalbzeit", aktuelleHalbzeit);

	  getWritableDatabase().update("spiel", cv, "_ID=?", args);
  }
  
  public void updateSpielErgebnis(Integer spielId) {
	  ContentValues cv=new ContentValues();
	  String strSpielId=String.valueOf(spielId);
	  String[] args={strSpielId};
	  int toreHeim = countTickerTore(strSpielId, "1", "9999999");
	  int toreAusw = countTickerTore(strSpielId, "0", "9999999");
	  
	  cv.put("toreHeim", toreHeim);
	  cv.put("toreAuswaerts", toreAusw);
	  
	  getWritableDatabase().update("spiel", cv, "_ID=?", args);
  }
  
  public void updateSpielTorwartHeim(String id, Integer spielerId) {
	  ContentValues cv=new ContentValues();
	  String[] args={id};
	  
	  cv.put("torwartHeim", spielerId);

	  getWritableDatabase().update("spiel", cv, "_ID=?", args);
  }

  public void updateSpielTorwartAuswaerts(String id, Integer spielerId) {
	  ContentValues cv=new ContentValues();
	  String[] args={id};
	  
	  cv.put("torwartAuswaerts", spielerId);

	  getWritableDatabase().update("spiel", cv, "_ID=?", args);
  }
  
  public void updateSpielHeimId(String id, String teamId) {
	  ContentValues cv=new ContentValues();
	  String[] args={id};
	  
	  cv.put("teamHeim", teamId);

	  getWritableDatabase().update("spiel", cv, "_ID=?", args);
  }
  
  public void updateSpielAuswId(String id, String teamId) {
	  ContentValues cv=new ContentValues();
	  String[] args={id};
	  
	  cv.put("teamAuswaerts", teamId);

	  getWritableDatabase().update("spiel", cv, "_ID=?", args);
  }
  
  public void updateSpielSpielerEingeben(String id, Integer wert) {
	  ContentValues cv=new ContentValues();
	  String[] args={id};
	  
	  cv.put("spielerEingabe", wert);

	  getWritableDatabase().update("spiel", cv, "_ID=?", args);
  }
  
  public void updateSpielSpielerWurfecke(String id, Integer wert) {
	  ContentValues cv=new ContentValues();
	  String[] args={id};
	  
	  cv.put("spielerWurfecke", wert);

	  getWritableDatabase().update("spiel", cv, "_ID=?", args);
  }
  
  public void updateSpielNotiz(String id, String notiz) {
	  ContentValues cv=new ContentValues();
	  String[] args={id};
	  
	  cv.put("notiz", notiz);

	  getWritableDatabase().update("spiel", cv, "_ID=?", args);
  }
  
  public void deleteSpiel(String id) {
	  String[] args={id};

	  getWritableDatabase().delete("spiel", "_ID=?", args);
	  getWritableDatabase().delete("ticker", "spielID=?", args);
  }
  
/* Spiel erstellen */

  public void createSchnellesSpiel(Context context) {
	
		Date spielDatum=null;
		int year;
		int month;
		int day;
		int heimID;
		int auswID;
		
		/* Heimmannschaft erstellen */
		insertTeam("Heimmannschaft", "HEIM");
		heimID = Integer.parseInt(getLastTeamId());
		
		/* Auswärtsmannschaft erstellen */
		insertTeam("Auswärtsmannschaft", "AUSW");
		auswID = Integer.parseInt(getLastTeamId());
		
		/* Spieler erstellen */

		Resources res = context.getResources();
		insertSpieler("Spieler 1", "1", String.valueOf(heimID), res.getString(R.string.spielerPositionTorwart));
		insertSpieler("Spieler 2", "2", String.valueOf(heimID), "");
		insertSpieler("Spieler 3", "3", String.valueOf(heimID), "");
		insertSpieler("Spieler 4", "4", String.valueOf(heimID), "");
		insertSpieler("Spieler 5", "5", String.valueOf(heimID), "");
		insertSpieler("Spieler 6", "6", String.valueOf(heimID), "");
		insertSpieler("Spieler 7", "7", String.valueOf(heimID), "");
		insertSpieler("Spieler 8", "8", String.valueOf(heimID), "");
		insertSpieler("Spieler 9", "9", String.valueOf(heimID), "");
		insertSpieler("Spieler 10", "10", String.valueOf(heimID), "");
		insertSpieler("Spieler 11", "11", String.valueOf(heimID), "");
		insertSpieler("Spieler 12", "12", String.valueOf(heimID), "");
		insertSpieler("Spieler 13", "13", String.valueOf(heimID), "");
		insertSpieler("Spieler 14", "14", String.valueOf(heimID), "");
		insertSpieler("Spieler 1", "1", String.valueOf(auswID), res.getString(R.string.spielerPositionTorwart));
		insertSpieler("Spieler 2", "2", String.valueOf(auswID), "");
		insertSpieler("Spieler 3", "3", String.valueOf(auswID), "");
		insertSpieler("Spieler 4", "4", String.valueOf(auswID), "");
		insertSpieler("Spieler 5", "5", String.valueOf(auswID), "");
		insertSpieler("Spieler 6", "6", String.valueOf(auswID), "");
		insertSpieler("Spieler 7", "7", String.valueOf(auswID), "");
		insertSpieler("Spieler 8", "8", String.valueOf(auswID), "");
		insertSpieler("Spieler 9", "9", String.valueOf(auswID), "");
		insertSpieler("Spieler 10", "10", String.valueOf(auswID), "");
		insertSpieler("Spieler 11", "11", String.valueOf(auswID), "");
		insertSpieler("Spieler 12", "12", String.valueOf(auswID), "");
		insertSpieler("Spieler 13", "13", String.valueOf(auswID), "");
		insertSpieler("Spieler 14", "14", String.valueOf(auswID), "");
		
		/* Datum erstellen */
		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		spielDatum=new Date(year-1900, month, day);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String strSpielDatum = dateFormat.format(spielDatum);
		
		/* Spiel erstellen */
		insertSpiel(strSpielDatum, 30, heimID, auswID);
	  
  }
  
/* Spieldaten abfragen */

  public String getSpielId(Cursor c) {
	  return(c.getString(0));
  }
  
  public String getSpielHeim(String spielId) {
	  return(getSpielString(spielId, 1));
  }
	  
  public String getSpielAusw(String spielId) {
	  return(getSpielString(spielId, 2));
  }
  
  public String getSpielAktuelleHalbzeit(String spielId) {
	  return(getSpielString(spielId, 3));
  }
  
  public String getSpielBallbesitz(String spielId) {
	  return(getSpielString(spielId, 4));
  }
  
  public String getSpielHalbzeitlaenge(String spielId) {
	  return(getSpielString(spielId, 5));
  }
  
  public String getSpielDatum(String spielId) {
	  return(getSpielString(spielId, 6));
  }
  
  public String getSpielToreHeim(String spielId) {
	  return(getSpielString(spielId, 7));
  }

  public String getSpielToreAusw(String spielId) {
	  return(getSpielString(spielId, 8));
  }
  
  public String getSpielSpielzeit(String spielId) {
	  return(getSpielString(spielId, 9));
  }

  public String getSpielZeitBisher(String spielId) {
	  return(getSpielString(spielId, 10));
  }
  
  public String getSpielZeitStart(String spielId) {
	  return(getSpielString(spielId, 11));
  }

  public String getSpielZeitTicker(String spielId) {
	  return(getSpielString(spielId, 12));
  }
  
  public String getSpielTorwartHeim(String spielId) {
	  return(getSpielString(spielId, 13));
  }
  
  public String getSpielTorwartAuswaerts(String spielId) {
	  return(getSpielString(spielId, 14));
  }
  
  public String getSpielSpielerEingabe(String spielId) {
	  return(getSpielString(spielId, 15));
  }

  public String getSpielSpielerWurfecke(String spielId) {
	  return(getSpielString(spielId, 16));
  }
  
  public String getSpielNotiz(String spielId) {
	  return(getSpielString(spielId, 17));
  }
  
  public String getSpielString(String spielId, Integer number) {
	  c=getSpielCursor(spielId);
      c.moveToFirst();
      strResult=c.getString(number);
      c.close();
	  return(strResult);
  }
  
  public String getTeamHeimNameBySpielID(String spielId) {
	  c=getSpielCursor(spielId);
	  c.moveToFirst();
	  strResult=getTeamName(c.getString(1));
	  c.close();
	  return(strResult);
  }
  
  public String getTeamAuswNameBySpielID(String spielId) {
	  c=getSpielCursor(spielId);
	  c.moveToFirst();
	  strResult=getTeamName(c.getString(2));
	  c.close();
	  return(strResult);
  }
  
  public String getTeamHeimKurzBySpielID(String spielId) {
	  c=getSpielCursor(spielId);
	  c.moveToFirst();
	  strResult=getTeamKuerzel(c.getString(1));
	  c.close();
	  return(strResult);
  }
  
  public String getTeamAuswKurzBySpielID(String spielId) {
	  c=getSpielCursor(spielId);
	  c.moveToFirst();
	  strResult=getTeamKuerzel(c.getString(2));
	  c.close();
	  return(strResult);
  }

/*
 * 
 * Mannschaftauswahl 
 *
 */
  
/* Datenbankabfrage */

  public Cursor getAllTicker(String id) {
	  
	  return(getReadableDatabase().rawQuery("SELECT * FROM ticker WHERE spielID = ? ORDER BY zeitInteger DESC", new String[] { id }));
  }
  
  public Cursor getAllTickerAlle() {
	  return(getReadableDatabase().rawQuery("SELECT * FROM ticker", null));
  }
  
  public Cursor getTickerCursor(String tickerId) {
	  String[] args={tickerId};
	  
	  return(getReadableDatabase()
			  .rawQuery("SELECT * FROM ticker WHERE _ID=?", args));
  }
  
  public void deleteAllTicker() {

	getWritableDatabase().delete("ticker", null, null);
		
  }
  
  public int maxTickerZeit(String spielId) {
	  String[] args={spielId};
	  SQLiteDatabase db = getReadableDatabase();
	  Cursor mZeit= db.rawQuery("SELECT * FROM ticker WHERE zeitInteger=(SELECT MAX(zeitInteger) FROM ticker) AND spielID = ?", args);
	  mZeit.moveToFirst();
	  int maxZeit= 0;
	  if(mZeit.getCount()>0){
		  maxZeit= mZeit.getInt(11);  
	  } 
	  mZeit.close();
	  return(maxZeit);
  }
  
  public Cursor getLastTickerId() {

	  return(getReadableDatabase()
			  .rawQuery("SELECT * FROM ticker WHERE _ID=(SELECT MAX(_ID) FROM ticker)", null));
  }
 
/* Ticker zählen */

  public int countTickerAktion(String id, String aktionInteger, String aktionTeamHeim, String spielzeit) {
	  SQLiteDatabase db = getReadableDatabase();
	  Cursor mCount= db.rawQuery("SELECT count(*) FROM ticker WHERE spielID = ? AND aktionInteger= ? AND aktionTeamHeim= ? AND zeitInteger<= ?", new String[] { id, aktionInteger, aktionTeamHeim, spielzeit});
	  mCount.moveToFirst();
	  int count= mCount.getInt(0);
	  mCount.close();
	  return(count);
  }
  
  public int countTickerTore(String id, String aktionTeamHeim, String spielzeit) {
	  SQLiteDatabase db = getReadableDatabase();
	  Cursor mCount= db.rawQuery("SELECT count(*) FROM ticker WHERE spielID = ? AND aktionTeamHeim= ? AND zeitInteger<= ?" +
	  		"AND (aktionInteger= 2 or aktionInteger= 14 or aktionInteger= 20)", new String[] { id, aktionTeamHeim, spielzeit});
	  mCount.moveToFirst();
	  int count= mCount.getInt(0);
	  mCount.close();
	  return(count);
  }
  
  public int countTickerSpielerAktionen(String spielerId, String spielId, String aktionId) {
	  SQLiteDatabase db = getReadableDatabase();
	  Cursor mCount= db.rawQuery("SELECT count(*) FROM ticker WHERE spielerID = ? AND spielID= ? AND aktionInteger= ?", 
			  new String[] { spielerId, spielId, aktionId});
	  mCount.moveToFirst();
	  int count= mCount.getInt(0);
	  mCount.close();
	  return(count);
  }
  
  public int countTickerAktionGesamt(String id) {
	  SQLiteDatabase db = getReadableDatabase();
	  Cursor mCount= db.rawQuery("SELECT count(*) FROM ticker WHERE spielID = ?", new String[] { id});
	  mCount.moveToFirst();
	  int count= mCount.getInt(0);
	  mCount.close();
	  return(count);
  }

  public int countTickerSpielerId(String id) {
	  SQLiteDatabase db = getReadableDatabase();
	  Cursor mCount= db.rawQuery("SELECT count(*) FROM ticker WHERE spielerID = ?", new String[] { id });
	  mCount.moveToFirst();
	  int count= mCount.getInt(0);
	  mCount.close();
	  return(count);
  }
  
  public int countTickerTorWurfeckeGesamt(String spielId, String spielerId, String wurfecke, String statWurfposition) {
	  SQLiteDatabase db = getReadableDatabase();
	  Cursor mCount = null;
	  if(statWurfposition==null){
		  mCount= db.rawQuery("SELECT count(*) FROM ticker WHERE spielerID = ? AND spielID =? " +
			  				  "AND wurfecke = ?" +	 
			  				  "AND (aktionInteger=2 OR aktionInteger=14 or aktionInteger=20)", 
			  				  new String[] { spielerId, spielId, wurfecke });
	  }
	  if(statWurfposition!=null){
		  mCount= db.rawQuery("SELECT count(*) FROM ticker WHERE spielerID = ? AND spielID =? " +
  				  			  "AND wurfecke = ?" +	
  				  			  "AND wurfposition = ?" +
  				  			  "AND (aktionInteger=2 OR aktionInteger=14 or aktionInteger=20)", 
  				  			  new String[] { spielerId, spielId, wurfecke, statWurfposition });
	  }
	  mCount.moveToFirst();
	  int count= mCount.getInt(0);
	  mCount.close();
	  return(count);
  }
  
  public int countTickerGehaltenWurfeckeGesamt(String spielId, String spielerId, String wurfecke, String statWurfposition) {
	  SQLiteDatabase db = getReadableDatabase();
	  Cursor mCount=null;
	  if(statWurfposition==null){
		  mCount= db.rawQuery("SELECT count(*) FROM ticker WHERE spielerID = ? AND spielID =? " +
			  				  "AND wurfecke = ?" +
			  				  "AND (aktionInteger=16 OR aktionInteger=18 or aktionInteger=22)", 
			  				  new String[] { spielerId, spielId, wurfecke });
	  }
	  if(statWurfposition!=null){
		  mCount= db.rawQuery("SELECT count(*) FROM ticker WHERE spielerID = ? AND spielID =? " +
  				  			  "AND wurfecke = ?" +	
  				  			  "AND wurfposition = ?" +
  				  			  "AND (aktionInteger=16 OR aktionInteger=18 or aktionInteger=22)", 
  				  			  new String[] { spielerId, spielId, wurfecke, statWurfposition });
	  }
	  mCount.moveToFirst();
	  int count= mCount.getInt(0);
	  mCount.close();
	  return(count);
  }
  
  public int countTickerFehlwurfWurfeckeGesamt(String spielId, String spielerId, String wurfecke, String statWurfposition) {
	  SQLiteDatabase db = getReadableDatabase();
	  Cursor mCount=null;
	  if(statWurfposition==null){
		  mCount= db.rawQuery("SELECT count(*) FROM ticker WHERE spielerID = ? AND spielID = ? " +
					 		  "AND wurfecke = ?" +
					 		  "AND (aktionInteger=3 OR aktionInteger=15 or aktionInteger=21)", 
					 		  new String[] { spielerId, spielId, wurfecke });
	  }
	  if(statWurfposition!=null){
		  mCount= db.rawQuery("SELECT count(*) FROM ticker WHERE spielerID = ? AND spielID =? " +
  				  			  "AND wurfecke = ?" +	
  				  			  "AND wurfposition = ?" +
  				  			  "AND (aktionInteger=3 OR aktionInteger=15 or aktionInteger=21)", 
  				  			  new String[] { spielerId, spielId, wurfecke, statWurfposition });
	  }
	  mCount.moveToFirst();
	  int count= mCount.getInt(0);
	  mCount.close();
	  return(count);
  }
  
  public int countTickerGegentorWurfeckeGesamt(String spielId, String spielerId, String wurfecke, String statWurfposition) {
	  SQLiteDatabase db = getReadableDatabase();
	  Cursor mCount=null;
	  if(statWurfposition==null){
		  mCount= db.rawQuery("SELECT count(*) FROM ticker WHERE spielerID = ? AND spielID =? " +
			  				  "AND wurfecke = ?" +
			  				  "AND (aktionInteger=17 OR aktionInteger=19 or aktionInteger=23)", 
			  				  new String[] { spielerId, spielId, wurfecke });
	  }
	  if(statWurfposition!=null){
		  mCount= db.rawQuery("SELECT count(*) FROM ticker WHERE spielerID = ? AND spielID =? " +
  				  			  "AND wurfecke = ?" +	
  				  			  "AND wurfposition = ?" +
  				  			  "AND (aktionInteger=17 OR aktionInteger=19 or aktionInteger=23)", 
  				  			  new String[] { spielerId, spielId, wurfecke, statWurfposition });
	  }
	  mCount.moveToFirst();
	  int count= mCount.getInt(0);
	  mCount.close();
	  return(count);
  }

  public int countTickerTorPositionGesamt(String spielId, String spielerId, String position, String statWurfecke) {
	  SQLiteDatabase db = getReadableDatabase();
	  Cursor mCount=null;
	  if(statWurfecke==null){
		  mCount= db.rawQuery("SELECT count(*) FROM ticker WHERE spielerID = ? AND spielID =? " +
				  			  "AND wurfposition = ?" +	 
				  			  "AND (aktionInteger=2 OR aktionInteger=14 or aktionInteger=20)", 
				  			  new String[] { spielerId, spielId, position });
	  }
	  if(statWurfecke!=null){
		  mCount= db.rawQuery("SELECT count(*) FROM ticker WHERE spielerID = ? AND spielID =? " +
				  			  "AND wurfposition = ?" +	
				  			  "AND wurfecke = ?" +	
				  			  "AND (aktionInteger=2 OR aktionInteger=14 or aktionInteger=20)", 
				  			  new String[] { spielerId, spielId, position, statWurfecke });
	  }
	  mCount.moveToFirst();
	  int count= mCount.getInt(0);
	  mCount.close();
	  return(count);
  }
  
  public int countTickerGehaltenPositionGesamt(String spielId, String spielerId, String position, String statWurfecke) {
	  SQLiteDatabase db = getReadableDatabase();
	  Cursor mCount=null;
	  if(statWurfecke==null){
		  mCount= db.rawQuery("SELECT count(*) FROM ticker WHERE spielerID = ? AND spielID =? " +
				  			  "AND wurfposition = ?" +
				  			  "AND (aktionInteger=16 OR aktionInteger=18 or aktionInteger=22)", 
				  			  new String[] { spielerId, spielId, position });
	  }
	  if(statWurfecke!=null){
		  mCount= db.rawQuery("SELECT count(*) FROM ticker WHERE spielerID = ? AND spielID =? " +
				  			  "AND wurfposition = ?" +	
				  			  "AND wurfecke = ?" +	
				  			  "AND (aktionInteger=16 OR aktionInteger=18 or aktionInteger=22)", 
				  			  new String[] { spielerId, spielId, position, statWurfecke });
	  }
	  mCount.moveToFirst();
	  int count= mCount.getInt(0);
	  mCount.close();
	  return(count);
  }
  
  public int countTickerFehlwurfPositionGesamt(String spielId, String spielerId, String position, String statWurfecke) {
	  SQLiteDatabase db = getReadableDatabase();
	  Cursor mCount=null;
	  if(statWurfecke==null){
		  mCount= db.rawQuery("SELECT count(*) FROM ticker WHERE spielerID = ? AND spielID = ? " +
				  			  "AND wurfposition = ?" +
				  			  "AND (aktionInteger=3 OR aktionInteger=15 or aktionInteger=21)", 
				  			  new String[] { spielerId, spielId, position });
	  }
	  if(statWurfecke!=null){
		  mCount= db.rawQuery("SELECT count(*) FROM ticker WHERE spielerID = ? AND spielID =? " +
				  			  "AND wurfposition = ?" +	
				  			  "AND wurfecke = ?" +	
				  			  "AND (aktionInteger=3 OR aktionInteger=15 or aktionInteger=21)", 
				  			  new String[] { spielerId, spielId, position, statWurfecke });
	  }
	  mCount.moveToFirst();
	  int count= mCount.getInt(0);
	  mCount.close();
	  return(count);
  }
  
  public int countTickerGegentorPositionGesamt(String spielId, String spielerId, String position, String statWurfecke) {
	  SQLiteDatabase db = getReadableDatabase();
	  Cursor mCount=null;
	  if(statWurfecke==null){
		  mCount= db.rawQuery("SELECT count(*) FROM ticker WHERE spielerID = ? AND spielID =? " +
				  			  "AND wurfposition = ?" +
				  			  "AND (aktionInteger=17 OR aktionInteger=19 or aktionInteger=23)", 
				  			  new String[] { spielerId, spielId, position });
	  }
	  if(statWurfecke!=null){
		  mCount= db.rawQuery("SELECT count(*) FROM ticker WHERE spielerID = ? AND spielID =? " +
				  			  "AND wurfposition = ?" +	
				  			  "AND wurfecke = ?" +	
				  			  "AND (aktionInteger=17 OR aktionInteger=19 or aktionInteger=23)", 
				  			  new String[] { spielerId, spielId, position, statWurfecke });
	  }
	  mCount.moveToFirst();
	  int count= mCount.getInt(0);
	  mCount.close();
	  return(count);
  }
  
  /* ToDo countStatSpielerId und countTickerSpielerId zusammenlegen */
  public int countStatSpielerId(String id, String spielId) {
	  SQLiteDatabase db = getReadableDatabase();
	  Cursor mCount= db.rawQuery("SELECT count(*) FROM ticker WHERE spielerID = ? AND spielID = ?", new String[] { id, spielId });
	  mCount.moveToFirst();
	  int count= mCount.getInt(0);
	  mCount.close();
	  return(count);
  }
  
/* Ticker bearbeiten */

  public void insertTicker(int aktionInt, String aktionString, int aktionTeamHeim, String spielerString, 
		  Integer spielerId, Integer spielId, Integer zeit, String realzeit) {
	  ContentValues cv=new ContentValues();
	  
	  String zeitString=updateTimer(zeit);
	  cv.put("aktionInteger", aktionInt);
	  cv.put("aktionString", aktionString);
	  cv.put("aktionTeamHeim", aktionTeamHeim);
	  cv.put("spielerAktion", spielerString);
	  cv.put("spielerID", spielerId);	  
	  cv.put("zeitInteger", zeit);
	  cv.put("zeitString", zeitString);
	  cv.put("spielId", spielId);
	  cv.put("spielId", spielId);
	  cv.put("wurfecke", "");
	  cv.put("wurfposition", "");
	  cv.put("realzeit", realzeit);

	  getWritableDatabase().insert("ticker", "aktionString", cv);
  }
  
  public void insertTickerImport(int aktionInt, String aktionString, int aktionTeamHeim, String spielerString, 
		  Integer spielerId, int tickerHeimTore, int tickerAuswaertsTore, String tickerErgebnisString, Integer zeit, Integer spielId) {
	  ContentValues cv=new ContentValues();
	  
	  String zeitString=updateTimer(zeit);
	  cv.put("aktionInteger", aktionInt);
	  cv.put("aktionString", aktionString);
	  cv.put("aktionTeamHeim", aktionTeamHeim);
	  cv.put("spielerAktion", spielerString);
	  cv.put("spielerID", spielerId);	  
	  cv.put("zeitInteger", zeit);
	  cv.put("zeitString", zeitString);
	  cv.put("spielId", spielId);
	  cv.put("tickerHeimTore", tickerHeimTore);
	  cv.put("tickerAuswaertsTore", tickerAuswaertsTore);
	  cv.put("tickerErgebnisString", tickerErgebnisString);
	  
	  getWritableDatabase().insert("ticker", "aktionString", cv);
  }
  
  public void insertTickerImportOhneTore(int aktionInt, String aktionString, int aktionTeamHeim, String spielerString, 
		  Integer spielerId, Integer zeit, Integer spielId) {
	  ContentValues cv=new ContentValues();
	  
	  String zeitString=updateTimer(zeit);
	  cv.put("aktionInteger", aktionInt);
	  cv.put("aktionString", aktionString);
	  cv.put("aktionTeamHeim", aktionTeamHeim);
	  cv.put("spielerAktion", spielerString);
	  cv.put("spielerID", spielerId);	  
	  cv.put("zeitInteger", zeit);
	  cv.put("zeitString", zeitString);
	  cv.put("spielId", spielId);
	  
	  getWritableDatabase().insert("ticker", "aktionString", cv);
  }

  public void updateTickerErgebnis(String id, Integer tickerHeimTore, Integer tickerAuswaertsTore, String tickerErgebnisString) {
	  ContentValues cv=new ContentValues();
	  String[] args={id};
	  
	  cv.put("tickerHeimTore", tickerHeimTore);
	  cv.put("tickerAuswaertsTore", tickerAuswaertsTore);
	  cv.put("tickerErgebnisString", tickerErgebnisString);

	  getWritableDatabase().update("ticker", cv, "_ID=?", args);
  }
  
  public void updateTickerEdit(String spielId, String id, String tickerZeitString, int zeit, String tickerAktionString, String tickerAktionId, 
		  					   String tickerSpielerString, String tickerSpielerId, String wurfecke, String position) {
	  /** Hinweis: Unterschied von Integer und int */
	  ContentValues cv=new ContentValues();
	  ContentValues cvs=new ContentValues();
	  String[] args={id};
	  
	  if(tickerZeitString!=null){
		  cv.put("zeitInteger", zeit);
		  cv.put("zeitString", tickerZeitString);
	  }
	  if(tickerAktionString!=null){
		  cv.put("aktionInteger", Integer.parseInt(tickerAktionId));
		  cv.put("aktionString", tickerAktionString);
	  }
	  if(tickerSpielerString!=null){
		  cv.put("spielerID", Integer.parseInt(tickerSpielerId));
		  cv.put("spielerAktion", tickerSpielerString);
	  }
	  cv.put("wurfecke", wurfecke);
	  cv.put("wurfposition", position);
	  getWritableDatabase().update("ticker", cv, "_ID=?", args);
	  
	  cvs.put("toreHeim", countTickerTore(spielId, "1", "9999999"));
	  cvs.put("toreAuswaerts", countTickerTore(spielId, "0", "9999999"));

	  String[] argse={spielId};
	  getWritableDatabase().update("spiel", cvs, "_ID=?", argse);
  }
  
  public void updateTickerSpieler(String spielId){
	  /* Aktuelle Spielernamen in Tickermeldungen schreiben */
	  String[] args={spielId};
	  SQLiteDatabase db=getWritableDatabase();
	  Cursor c=db.rawQuery("SELECT * FROM ticker WHERE spielID=? ORDER BY zeitInteger ASC", args);
	  c.moveToFirst();
	  int toreHeim=0;
	  int toreAusw=0;
	  String strErgebnis=null;
	  for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
		  /* Spielernamen neu einfügen */
		  if(Integer.parseInt(getTickerSpielerId(c))!=0){
			  ContentValues cv=new ContentValues();
			  String[] argse={getTickerId(c)};
			  
			  String spieler_name=getSpielerName(getTickerSpielerId(c));
			  
			  cv.put("spielerAktion", spieler_name);
			  getWritableDatabase().update("ticker", cv, "_ID=?", argse);
		  }
		  
		  /* Spielergebnis neu einfügen */
		  if(Integer.parseInt(getTickerAktionInt(c))==2 || 
				  Integer.parseInt(getTickerAktionInt(c))==14 || 
				  Integer.parseInt(getTickerAktionInt(c))==20){
			  if(Integer.parseInt(getTickerAktionTeamHeim(c))==1){
				  toreHeim=toreHeim+1;
			  }
			  if(Integer.parseInt(getTickerAktionTeamHeim(c))==0){
				  toreAusw=toreAusw+1;
			  }
		  }
		  strErgebnis=String.valueOf(toreHeim)+":"+String.valueOf(toreAusw);
		  updateTickerErgebnis(getTickerId(c), toreHeim, toreAusw, strErgebnis);
	  }
	  c.close();
  }
  
  public void updateTickerWurfecke(String id, String wurfecke) {
	  ContentValues cv=new ContentValues();
	  String[] args={id};
	  
	  cv.put("wurfecke", wurfecke);

	  getWritableDatabase().update("ticker", cv, "_ID=?", args);
  }

  public void updateTickerWurfposition(String id, String position) {
	  ContentValues cv=new ContentValues();
	  String[] args={id};
	  
	  cv.put("wurfposition", position);

	  getWritableDatabase().update("ticker", cv, "_ID=?", args);
  }
  
  public void updateTickerSpielId(String id, String spielId) {
	  ContentValues cv=new ContentValues();
	  String[] args={id};
	  
	  cv.put("spielID", spielId);

	  getWritableDatabase().update("ticker", cv, "_ID=?", args);
  }
  
  public void updateTickerSpielerId(String id, String spielerId) {
	  ContentValues cv=new ContentValues();
	  String[] args={id};
	  
	  cv.put("spielerID", spielerId);

	  getWritableDatabase().update("ticker", cv, "_ID=?", args);
  }
  
  public void deleteTicker(String spielId, String id) {
	  String[] args={id};

	  getWritableDatabase().delete("ticker", "_ID=?", args);
	  
	  ContentValues cv=new ContentValues();
	  cv.put("toreHeim", countTickerTore(spielId, "1", "9999999"));
	  cv.put("toreAuswaerts", countTickerTore(spielId, "0", "9999999"));
	  String[] argse={spielId};
	  getWritableDatabase().update("spiel", cv, "_ID=?", argse);
	  
  }
  
  public void changeBallbesitz(String aktionTeamHeim, Integer intSpielBallbesitz, String strTeamHeimKurzBySpielID, 
		  String strTeamAuswKurzBySpielID, String spielId, String zeit, String realzeit) {
	  if(Integer.parseInt(aktionTeamHeim)==1 && intSpielBallbesitz==1){  
			// ... und Heimmannschaft Tor geworfen, dann trage Ballbesitz Auswärtsmannschaft ein
		  String strBallbesitz="Ballbesitz " + strTeamAuswKurzBySpielID;
		  insertTicker(1, strBallbesitz, 0, "", 0, Integer.parseInt(spielId), (int) Integer.parseInt(zeit) - 2, realzeit);
		  updateSpielBallbesitz(spielId, 0);  // aktuellen Ballbesitz in Spiel eintragen
	  }
	  if(Integer.parseInt(aktionTeamHeim)==0 && intSpielBallbesitz==0){
		  String strBallbesitz="Ballbesitz " + strTeamHeimKurzBySpielID;
		  insertTicker(0, strBallbesitz, 1, "", 0, Integer.parseInt(spielId), (int) Integer.parseInt(zeit) - 2, realzeit);
		  updateSpielBallbesitz(spielId, 1);  // aktuellen Ballbesitz in Spiel eintragen
	  }
  }

  public String updateTimer (float time){
	secs = (long)(time/1000);
	mins = (long)((time/1000)/60);

	secs = secs % 60;
	seconds=String.valueOf(secs);
	if(secs == 0){
		seconds = "00";
	}
	if(secs <10 && secs > 0){
		seconds = "0"+seconds;
	}
    	
	/* Convert the minutes to String and format the String */
    	
	minutes=String.valueOf(mins);
	if(mins == 0){
		minutes = "00";
	}
	if(mins <10 && mins > 0){
		minutes = "0"+minutes;
	}
    	
	/* Setting the timer text to the elapsed time */
	return(minutes + ":" + seconds);
  }
	
	public String getMinutes (float time){
		mins = (long)((time/1000)/60);

		minutes=String.valueOf(mins);
    	if(mins == 0){
    		minutes = "00";
    	}
    	if(mins <10 && mins > 0){
    		minutes = "0"+minutes;
    	}
		
    	return(minutes);
	}
	
	public String getSeconds (float time){
		secs = (long)(time/1000);

		secs = secs % 60;
		seconds=String.valueOf(secs);
    	if(secs == 0){
    		seconds = "00";
    	}
    	if(secs <10 && secs > 0){
    		seconds = "0"+seconds;
    	}
    	
    	return(seconds);
	}

/* Tickerdaten abfragen */

  public String getTickerId(Cursor c) {
	  return(c.getString(0));
  }
  
  public String getTickerAktionInt(Cursor c) {
	  return(c.getString(1));
  }
  
  public String getTickerAktion(Cursor c) {
	  return(c.getString(2));
  }

  public String getTickerAktionTeamHeim(Cursor c) {
	  return(c.getString(3));
  }
  
  public String getTickerSpieler(Cursor c) {
	  return(c.getString(4));
  }
  
  public String getTickerSpielerId(Cursor c) {
	  return(c.getString(5));
  }

  public String getTickerErgebnis(Cursor c) {
	  return(c.getString(8));
  }
  
  public String getTickerWurfecke(Cursor c) {
	  return(c.getString(10));
  }
  
  public String getTickerZeitInt(Cursor c) {
	  return(c.getString(11));
  }
  
  public String getTickerZeit(Cursor c) {
	  return(c.getString(12));
  }
  
  public String getTickerPosition(Cursor c) {
	  return(c.getString(14));
  }

/*
 * 
 * Mannschaftauswahl 
 *
 */
	
/* Start Statistik Spiel */
  
/* Datenbankabfrage */

	  public Cursor getAllStatSpiel() {
		  return(getReadableDatabase()
				  .rawQuery("SELECT * FROM statSpiel", null));
	  }
	  
	  public Cursor getAllStatTore() {
		  return(getReadableDatabase()
				  .rawQuery("SELECT * FROM statTor ORDER BY torTore DESC", null));
	  }
	  
	  public Cursor getAllStatTickerTore() {
		  return(getReadableDatabase()
				  .rawQuery("SELECT * FROM statTickerTor ORDER BY tickerTorTore DESC", null));
	  }
	  
	  /** Hinweis: statTor missbraucht für Statistik Spieler */ 
	  public Cursor getAllStatSpieler() {
		  return(getReadableDatabase()
				  .rawQuery("SELECT * FROM statTor ORDER BY torTore ASC", null));
	  }
	  
	  public Cursor getAllStatSpielerStat() {
		  return(getReadableDatabase()
				  .rawQuery("SELECT * FROM statSpieler", null));
	  }
	  
	  public Cursor getStatTorById(String id) {
		  String[] args={id};

		  return(getReadableDatabase()
				  .rawQuery("SELECT _id, torName, torTore, torFehl, torProzent FROM statTor WHERE _ID=?", args));
	  }
      
/* Statistik Spiel bearbeiten */
    
	  public void insertStatSpiel(String spielHeim, String spielBezeichnung, String spielAusw) {
		  ContentValues cv=new ContentValues();
	      
		  cv.put("spielHeim", spielHeim);
		  cv.put("spielBezeichnung", spielBezeichnung);
		  cv.put("spielAusw", spielAusw);

		  getWritableDatabase().insert("statSpiel", "spielBezeichnung", cv);
	  }
	  
	public void deleteStatSpiel() {

		getWritableDatabase().delete("statSpiel", null, null);
		
	}
	
	  public void insertStatTor(String name, String tore, String fehl, String prozent) {
		  ContentValues cv=new ContentValues();
	      
		  cv.put("torName", name);
		  cv.put("torTore", tore);
		  cv.put("torFehl", fehl);
		  cv.put("torProzent", prozent);

		  getWritableDatabase().insert("statTor", "torName", cv);
	  }
	  
	  public void insertStatTickerTor(String name, String team, String tore, String fehl, String prozent) {
		  ContentValues cv=new ContentValues();
	      
		  cv.put("tickerTorName", name);
		  cv.put("tickerTorTeam", team);
		  cv.put("tickerTorTore", tore);
		  cv.put("tickerTorFehl", fehl);
		  cv.put("tickerTorProzent", prozent);

		  getWritableDatabase().insert("statTickerTor", "tickerTorName", cv);
	  }
	  
	  public void insertStatSpieler(String spielerBezeichnung, String spielerWert) {
		  ContentValues cv=new ContentValues();

		  cv.put("spielerBezeichnung", spielerBezeichnung);
		  cv.put("spielerWert", spielerWert);

		  getWritableDatabase().insert("statSpieler", "spielerBezeichnung", cv);
	  }
	  
		public void deleteStatTor() {

			getWritableDatabase().delete("statTor", null, null);
			
		}

		public void deleteStatTickerTor() {

			getWritableDatabase().delete("statTickerTor", null, null);
			
		}
		
		public void deleteStatSpieler() {

			getWritableDatabase().delete("statSpieler", null, null);
			
		}
        
/* Statistik Spiel erstellen */
      
	public void createStatSpiel(String spielId, Context context) {
		
		String strSpielHeim=null;
		String strSpielBezeichnung=null;
		String strSpielAusw=null;
		int ballbesitzHeim=0;
    	int ballbesitzAusw=0;
    	int zeitBallbesitzHeim=0;
    	int zeitBallbesitzAusw=0;
    	int zeitBallbesitzAngriffHeim=0;
    	int zeitBallbesitzAngriffAusw=0;
    	int zeitBallbesitzWechsel=0;
    	int ballbesitz=2;
    	int intTickerAktion=0;
    	int intTickerAktionTeam=2;
    	int intTickerZeit=0;
    	int torverhaeltnis=0;
    	int zeitFuehrungHeim=0;
    	int zeitFuehrungAusw=0;
    	int zeitUnentschieden=0;
    	int zeitFuehrungswechsel=0;
    	int ueberzahl=0;
    	int toreUeberzahlHeim=0;
    	int toreUnterzahlHeim=0;
    	int toreUeberzahlAusw=0;
    	int toreUnterzahlAusw=0;
    	int zeitUeberzahlHeim=0;
    	int zeitUeberzahlAusw=0;
    	int zeitUeberzahlWechsel=0;
    	
		deleteStatSpiel();
		
		Cursor c=getSpielCursor(spielId);
		c.moveToFirst();
		
		int halbzeitlaenge=Integer.parseInt(getSpielHalbzeitlaenge(spielId))*60;
		
		/* Relative Statistiken ermitteln */
		
		String[] args={spielId};
		SQLiteDatabase db=getWritableDatabase();
		Cursor cTicker=db.rawQuery("SELECT * FROM ticker WHERE spielID=? ORDER BY zeitInteger ASC", args);
    	cTicker.moveToFirst();

    	Resources res = context.getResources();
    	for (cTicker.moveToFirst(); !cTicker.isAfterLast(); cTicker.moveToNext()) {
    		intTickerAktion=Integer.parseInt(getTickerAktionInt(cTicker));
    		intTickerZeit=Integer.parseInt(getTickerZeitInt(cTicker));
    		intTickerAktionTeam=Integer.parseInt(getTickerAktionTeamHeim(cTicker));
    		/* Ballbesitz eintragen */
    		if(intTickerAktion==0){	
    			if(ballbesitz!=1){	// Wenn Aktion Ballbesitz Heim und Ballbesitz aktuell ist nicht Heim
    				ballbesitzHeim=ballbesitzHeim+1;
    				if(ballbesitz==0){	// Wenn vorher die andere Mannschaft im Ballbesitz war, trage Zeit im Ballbesitz ein
    					zeitBallbesitzAusw=zeitBallbesitzAusw+intTickerZeit-zeitBallbesitzWechsel;
    					zeitBallbesitzWechsel=intTickerZeit;
    					ballbesitz=1;
    				}
    				if(ballbesitz==2){	// Wenn vorher keine andere Mannschaft im Ballbesitz, dann nur Wechsel eintragen
    					zeitBallbesitzWechsel=intTickerZeit;
    					ballbesitz=1;
    				}
    			}
    		}
    		if(intTickerAktion==1){	
    			if(ballbesitz!=0){	// Wenn Aktion Ballbesitz Heim und Ballbesitz aktuell ist nicht Heim
    				ballbesitzAusw=ballbesitzAusw+1;
    				if(ballbesitz==1){	// Wenn vorher die andere Mannschaft im Ballbesitz war, trage Zeit im Ballbesitz ein
    					zeitBallbesitzHeim=zeitBallbesitzHeim+intTickerZeit-zeitBallbesitzWechsel;
    					zeitBallbesitzWechsel=intTickerZeit;
    					ballbesitz=0;
    				}
    				if(ballbesitz==2){	// Wenn vorher keine andere Mannschaft im Ballbesitz, dann nur Wechsel eintragen
    					zeitBallbesitzWechsel=intTickerZeit;
    					ballbesitz=0;
    				}
    			}
    		}
    		/* Führung / Unentschieden + Tore Überzahl / Unterzahl eintragen */
    		if(intTickerAktion==2 || intTickerAktion==14 || intTickerAktion==20){
    			if(intTickerAktionTeam==1){  // Hat das Heimteam das Tor geschossen?
    				torverhaeltnis=torverhaeltnis+1;
    				if(torverhaeltnis==0){	 // Kommt es durch das Tor zum Unentschieden?
    					zeitFuehrungAusw=zeitFuehrungAusw+intTickerZeit-zeitFuehrungswechsel;
    					zeitFuehrungswechsel=intTickerZeit;
    				}
    				if(torverhaeltnis==1){	 // Geht die Heimmannschaft durch das Tor in Führung?
    					zeitUnentschieden=zeitUnentschieden+intTickerZeit-zeitFuehrungswechsel;
    					zeitFuehrungswechsel=intTickerZeit;
    				}
    				if(ueberzahl>0){
    					toreUeberzahlHeim=toreUeberzahlHeim+1;
    				}
    				if(ueberzahl<0){
    					toreUnterzahlHeim=toreUnterzahlHeim+1;
    				}
    			}
    			if(intTickerAktionTeam==0){  // Hat das Auswärtsteam das Tor geschossen?
    				torverhaeltnis=torverhaeltnis-1;
    				if(torverhaeltnis==0){	 // Kommt es durch das Tor zum Unentschieden?
    					zeitFuehrungHeim=zeitFuehrungHeim+intTickerZeit-zeitFuehrungswechsel;
    					zeitFuehrungswechsel=intTickerZeit;
    				}
    				if(torverhaeltnis==-1){	 // Geht die Auswärtsmannschaft durch das Tor in Führung?
    					zeitUnentschieden=zeitUnentschieden+intTickerZeit-zeitFuehrungswechsel;
    					zeitFuehrungswechsel=intTickerZeit;
    				}
    				if(ueberzahl>0){
    					toreUnterzahlAusw=toreUnterzahlAusw+1;
    				}
    				if(ueberzahl<0){
    					toreUeberzahlAusw=toreUeberzahlAusw+1;
    				}
    			}  			
    		}
    		/* Überzahl / Unterzahl eintragen */
    		if(intTickerAktion==5 || intTickerAktion==9 || intTickerAktion==11){		// Falls eine Zeitstrafe gegeben wurde
    			if(intTickerAktionTeam==1){
    				ueberzahl=ueberzahl-1;
    				if(ueberzahl==-1){		// Ist Auswärtsmannschaft in Überzahl gekommen?
    					zeitUeberzahlWechsel=intTickerZeit;
    				}
    				if(ueberzahl==0){		// Wurde Überzahl der Heimmannschaft durch Zeitstrafe beendet
    					zeitUeberzahlHeim=zeitUeberzahlHeim+intTickerZeit-zeitUeberzahlWechsel;
    				}
    				
    			}
    			if(intTickerAktionTeam==0){
    				ueberzahl=ueberzahl+1;
    				if(ueberzahl==1){		// Ist Heimmannschaft in Überzahl gekommen?
    					zeitUeberzahlWechsel=intTickerZeit;
    				}
    				if(ueberzahl==0){		// Wurde Überzahl der Auswärtsmannschaft durch Zeitstrafe beendet
    					zeitUeberzahlAusw=zeitUeberzahlAusw+intTickerZeit-zeitUeberzahlWechsel;
    				}
    			}
    		}
			if(intTickerAktion==10){	// Falls Spieler wieder zurück kommt
    			if(intTickerAktionTeam==1){
    				ueberzahl=ueberzahl+1;
    				if(ueberzahl==1){		// Ist Heimmannschaft in Überzahl gekommen?
    					zeitUeberzahlWechsel=intTickerZeit;
    				}
    				if(ueberzahl==0){		// Wurde Überzahl der Auswärtsmannschaft durch Zeitstrafe beendet
    					zeitUeberzahlAusw=zeitUeberzahlAusw+intTickerZeit-zeitUeberzahlWechsel;
    				}
    			}
    			if(intTickerAktionTeam==0){
    				ueberzahl=ueberzahl-1;
    				if(ueberzahl==-1){		// Ist Auswärtsmannschaft in Überzahl gekommen?
    					zeitUeberzahlWechsel=intTickerZeit;
    				}
    				if(ueberzahl==0){		// Wurde Überzahl der Heimmannschaft durch Zeitstrafe beendet
    					zeitUeberzahlHeim=zeitUeberzahlHeim+intTickerZeit-zeitUeberzahlWechsel;
    				}
    			}
			}
    	}
    	/* Wenn zum Spielende eine Mannschaft in Ballbesitz, dann Restzeit eintragen */
    	if(ballbesitz==1){
			zeitBallbesitzHeim=zeitBallbesitzHeim+(halbzeitlaenge*2000)-zeitBallbesitzWechsel;
		}
    	if(ballbesitz==0){
			zeitBallbesitzAusw=zeitBallbesitzAusw+(halbzeitlaenge*2000)-zeitBallbesitzWechsel;
		}
    	if(ballbesitzHeim!=0){
    		zeitBallbesitzAngriffHeim=zeitBallbesitzHeim/ballbesitzHeim;
    	}
    	if(ballbesitzAusw!=0){
    		zeitBallbesitzAngriffAusw=zeitBallbesitzAusw/ballbesitzAusw;
    	}
    	/* Führung oder Unentschieden zum Spielende eintragen */
    	if(torverhaeltnis>0){
    		zeitFuehrungHeim=zeitFuehrungHeim+(halbzeitlaenge*2000)-zeitFuehrungswechsel;
    	}
    	if(torverhaeltnis<0){
    		zeitFuehrungAusw=zeitFuehrungAusw+(halbzeitlaenge*2000)-zeitFuehrungswechsel;
    	}
    	if(torverhaeltnis==0){
    		zeitUnentschieden=zeitUnentschieden+(halbzeitlaenge*2000)-zeitFuehrungswechsel;
    	}
    	/* Falls eine Mannschaft zum Ende des Spiels in Überzahl ist */
    	if(ueberzahl>0){
    		zeitUeberzahlHeim=zeitUeberzahlHeim+(halbzeitlaenge*2000)-zeitUeberzahlWechsel;
    	}
    	if(ueberzahl<0){
    		zeitUeberzahlAusw=zeitUeberzahlAusw+(halbzeitlaenge*2000)-zeitUeberzahlWechsel;
    	}
    	cTicker.close();
    	
		/* Datenbank löschen */
        getWritableDatabase().delete("statSpiel", null, null);
        
        /* Überschrift */
        strSpielHeim=getTeamHeimKurzBySpielID(spielId);
        strSpielBezeichnung="";
        strSpielAusw=getTeamAuswKurzBySpielID(spielId);
        insertStatSpiel(strSpielHeim, strSpielBezeichnung, strSpielAusw);
        
        /* Tore eintragen */
        strSpielHeim=String.valueOf(countTickerTore(spielId, "1", "9999999"));
        strSpielBezeichnung=res.getString(R.string.statBezeichnungTore);
        strSpielAusw=String.valueOf(countTickerTore(spielId, "0", "9999999"));
        insertStatSpiel(strSpielHeim, strSpielBezeichnung, strSpielAusw);
        
        /* Halbzeit eintragen */
        strSpielHeim=String.valueOf(countTickerTore(spielId, "1", String.valueOf(halbzeitlaenge*1000)));
        strSpielBezeichnung=res.getString(R.string.statBezeichnungHalbzeit);
        strSpielAusw=String.valueOf(countTickerTore(spielId, "0", String.valueOf(halbzeitlaenge*1000)));
        insertStatSpiel(strSpielHeim, strSpielBezeichnung, strSpielAusw);
        
        /* Chancen eintragen */
        strSpielHeim=String.valueOf(countTickerTore(spielId, "1", "9999999")+countTickerAktion(spielId, "3", "1", "9999999"));
        strSpielBezeichnung=res.getString(R.string.statBezeichnungChancen);
        strSpielAusw=String.valueOf(countTickerTore(spielId, "0", "9999999")+countTickerAktion(spielId, "3", "0", "9999999"));
        insertStatSpiel(strSpielHeim, strSpielBezeichnung, strSpielAusw);
        
        /* Effektivität eintragen */
        if(countTickerTore(spielId, "1", "9999999")>0 || countTickerAktion(spielId, "3", "1", "9999999")>0){
        	strSpielHeim=String.valueOf((countTickerTore(spielId, "1", "9999999")*100/
        			(countTickerTore(spielId, "1", "9999999")+countTickerAktion(spielId, "3", "1", "9999999")))+"%");
        }
        else{
        	strSpielHeim="0";
        }
        strSpielBezeichnung=res.getString(R.string.statBezeichnungEffektivitaet);
        if(countTickerTore(spielId, "0", "9999999")>0 || countTickerAktion(spielId, "3", "0", "9999999")>0){
        	strSpielAusw=String.valueOf((countTickerTore(spielId, "0", "9999999")*100/
        							(countTickerTore(spielId, "0", "9999999")+countTickerAktion(spielId, "3", "0", "9999999")))+"%");
        }
        else{
        	strSpielAusw="0";
        }
        
        insertStatSpiel(strSpielHeim, strSpielBezeichnung, strSpielAusw);
        
        /* Technischer Fehler eintragen */
        strSpielHeim=String.valueOf(countTickerAktion(spielId, "4", "1", "9999999"));
        strSpielBezeichnung=res.getString(R.string.statBezeichnungTechFehler);
        strSpielAusw=String.valueOf(countTickerAktion(spielId, "4", "0", "9999999"));
        insertStatSpiel(strSpielHeim, strSpielBezeichnung, strSpielAusw);

        /* Angriffe eintragen */
        strSpielHeim=String.valueOf(ballbesitzHeim);
        strSpielBezeichnung=res.getString(R.string.statBezeichnungAngriffe);
        strSpielAusw=String.valueOf(ballbesitzAusw);
        insertStatSpiel(strSpielHeim, strSpielBezeichnung, strSpielAusw);

        /* Zeit Ballbesitz eintragen */
        strSpielHeim=updateTimer(zeitBallbesitzHeim);
        strSpielBezeichnung=res.getString(R.string.statBezeichnungBallbesitz);
        strSpielAusw=updateTimer(zeitBallbesitzAusw);
        insertStatSpiel(strSpielHeim, strSpielBezeichnung, strSpielAusw);
        
        /* Zeit Ballbesitz Angriff eintragen */
        strSpielHeim=updateTimer(zeitBallbesitzAngriffHeim);
        strSpielBezeichnung=res.getString(R.string.statBezeichnungZeitAngriff);
        strSpielAusw=updateTimer(zeitBallbesitzAngriffAusw);
        insertStatSpiel(strSpielHeim, strSpielBezeichnung, strSpielAusw);
        
        /* Überschrift Spielfilm */
        strSpielHeim="";
        strSpielBezeichnung=res.getString(R.string.statBezeichnungSpielfilm);
        strSpielAusw="";
        insertStatSpiel(strSpielHeim, strSpielBezeichnung, strSpielAusw);
        
        /* Spielfilm 1 */
        strSpielHeim=String.valueOf(countTickerTore(spielId, "1", String.valueOf(halbzeitlaenge*1000/3)));
        strSpielBezeichnung=String.valueOf(halbzeitlaenge/60/3)+" "+res.getString(R.string.statBezeichnungMinuten);
        strSpielAusw=String.valueOf(countTickerTore(spielId, "0", String.valueOf(halbzeitlaenge*1000/3)));
        insertStatSpiel(strSpielHeim, strSpielBezeichnung, strSpielAusw);
        
        /* Spielfilm 2 */
        strSpielHeim=String.valueOf(countTickerTore(spielId, "1", String.valueOf(halbzeitlaenge*1000*2/3)));
        strSpielBezeichnung=String.valueOf(halbzeitlaenge/60*2/3)+" "+res.getString(R.string.statBezeichnungMinuten);
        strSpielAusw=String.valueOf(countTickerTore(spielId, "0", String.valueOf(halbzeitlaenge*1000*2/3)));
        insertStatSpiel(strSpielHeim, strSpielBezeichnung, strSpielAusw);
        
        /* Spielfilm 3 */
        strSpielHeim=String.valueOf(countTickerTore(spielId , "1", String.valueOf(halbzeitlaenge*1000)));
        strSpielBezeichnung=String.valueOf(halbzeitlaenge/60)+" "+res.getString(R.string.statBezeichnungMinuten);
        strSpielAusw=String.valueOf(countTickerTore(spielId, "0", String.valueOf(halbzeitlaenge*1000)));
        insertStatSpiel(strSpielHeim, strSpielBezeichnung, strSpielAusw);
        
        /* Spielfilm 4 */
        strSpielHeim=String.valueOf(countTickerTore(spielId, "1", String.valueOf(halbzeitlaenge*1000*4/3)));
        strSpielBezeichnung=String.valueOf(halbzeitlaenge/60*4/3)+" "+res.getString(R.string.statBezeichnungMinuten);
        strSpielAusw=String.valueOf(countTickerTore(spielId, "0", String.valueOf(halbzeitlaenge*1000*4/3)));
        insertStatSpiel(strSpielHeim, strSpielBezeichnung, strSpielAusw);
        
        /* Spielfilm 5 */
        strSpielHeim=String.valueOf(countTickerTore(spielId, "1", String.valueOf(halbzeitlaenge*1000*5/3)));
        strSpielBezeichnung=String.valueOf(halbzeitlaenge/60*5/3)+" "+res.getString(R.string.statBezeichnungMinuten);
        strSpielAusw=String.valueOf(countTickerTore(spielId, "0", String.valueOf(halbzeitlaenge*1000*5/3)));
        insertStatSpiel(strSpielHeim, strSpielBezeichnung, strSpielAusw);
        
        /* Spielfilm 6 */
        strSpielHeim=String.valueOf(countTickerTore(spielId, "1", String.valueOf(halbzeitlaenge*1000*6/3)));
        strSpielBezeichnung=String.valueOf(halbzeitlaenge/60*6/3)+" "+res.getString(R.string.statBezeichnungMinuten);
        strSpielAusw=String.valueOf(countTickerTore(spielId, "0", String.valueOf(halbzeitlaenge*1000*6/3)));
        insertStatSpiel(strSpielHeim, strSpielBezeichnung, strSpielAusw);
        /** Hinweis: Abfragen, ob Spiel länger als normale Spielzeit */

        /* Zeit Fuehrung */
        strSpielHeim=updateTimer(zeitFuehrungHeim);
        strSpielBezeichnung=res.getString(R.string.statBezeichnungFuehrung);
        strSpielAusw=updateTimer(zeitFuehrungAusw);
        insertStatSpiel(strSpielHeim, strSpielBezeichnung, strSpielAusw);

        /* Zeit Unentschieden */
        strSpielHeim=updateTimer(zeitUnentschieden);
        strSpielBezeichnung=res.getString(R.string.statBezeichnungUnentschieden);
        strSpielAusw=updateTimer(zeitUnentschieden);
        insertStatSpiel(strSpielHeim, strSpielBezeichnung, strSpielAusw);
        
        /* Überschrift Strafen */
        strSpielHeim="";
        strSpielBezeichnung=res.getString(R.string.statBezeichnungStrafen);
        strSpielAusw="";
        insertStatSpiel(strSpielHeim, strSpielBezeichnung, strSpielAusw);
        
        /* Gelbe Karten eintragen */
        strSpielHeim=String.valueOf(countTickerAktion(spielId, "6", "1", "9999999"));
        strSpielBezeichnung=res.getString(R.string.statBezeichnungGelbeKarten);
        strSpielAusw=String.valueOf(countTickerAktion(spielId, "6", "0", "9999999"));
        insertStatSpiel(strSpielHeim, strSpielBezeichnung, strSpielAusw);
        
        /* Zwei Minuten eintragen */
        strSpielHeim=String.valueOf(countTickerAktion(spielId, "5", "1", "9999999"));
        strSpielBezeichnung=res.getString(R.string.statBezeichnungZweiMinuten);
        strSpielAusw=String.valueOf(countTickerAktion(spielId, "5", "0", "9999999"));
        insertStatSpiel(strSpielHeim, strSpielBezeichnung, strSpielAusw);
        
        /* Zwei mal zwei Minuten eintragen */
        if(countTickerAktion(spielId, "11", "1", "9999999")>0 ||
        	countTickerAktion(spielId, "11", "0", "9999999")>0){
        	strSpielHeim=String.valueOf(countTickerAktion(spielId, "11", "1", "9999999"));
        	strSpielBezeichnung=res.getString(R.string.statBezeichnungZweiMalZwei);
        	strSpielAusw=String.valueOf(countTickerAktion(spielId, "11", "0", "9999999"));
        	insertStatSpiel(strSpielHeim, strSpielBezeichnung, strSpielAusw);
        }

        /* Rote Karten eintragen */
        if(countTickerAktion(spielId, "9", "1", "9999999")>0 ||
        	countTickerAktion(spielId, "9", "0", "9999999")>0){
        	strSpielHeim=String.valueOf(countTickerAktion(spielId, "9", "1", "9999999"));
        	strSpielBezeichnung=res.getString(R.string.statBezeichnungRoteKarten);
        	strSpielAusw=String.valueOf(countTickerAktion(spielId, "9", "0", "9999999"));
        	insertStatSpiel(strSpielHeim, strSpielBezeichnung, strSpielAusw);
        }
        
        /* Zeit Überzahl eintragen */
        strSpielHeim=updateTimer(zeitUeberzahlHeim);
        strSpielBezeichnung=res.getString(R.string.statBezeichnungUeberzahl);
        strSpielAusw=updateTimer(zeitUeberzahlAusw);
        insertStatSpiel(strSpielHeim, strSpielBezeichnung, strSpielAusw);
        
        /* Tore Überzahl eintragen */
        strSpielHeim=String.valueOf(toreUeberzahlHeim);
        strSpielBezeichnung=res.getString(R.string.statBezeichnungToreUeberzahl);
        strSpielAusw=String.valueOf(toreUeberzahlAusw);
        insertStatSpiel(strSpielHeim, strSpielBezeichnung, strSpielAusw);
        
        /* Tore Unterzahl eintragen */
        strSpielHeim=String.valueOf(toreUnterzahlHeim);
        strSpielBezeichnung=res.getString(R.string.statBezeichnungToreUnterzahl);
        strSpielAusw=String.valueOf(toreUnterzahlAusw);
        insertStatSpiel(strSpielHeim, strSpielBezeichnung, strSpielAusw);
        
        c.close();
        
	}
    
/* Statistik Tore erstellen */
  
	public void createStatTore(String spielId, String teamId, Context context) {
		
		deleteStatTor();
		
		int spielerTore=0;
		int spielerFehlwurf=0;
		int spielerProzent=0;
		String spielerId=null;
		String strSpielerName=null;
		Resources res = context.getResources();
		
		String[] args={teamId};
		SQLiteDatabase db=getWritableDatabase();
		Cursor cSpieler=db.rawQuery("SELECT * FROM spieler WHERE teamID=?", args);
		
		insertStatTor(res.getString(R.string.statBezeichnungName), 
					  res.getString(R.string.statBezeichnungToreKurz), 
					  res.getString(R.string.statBezeichnungWuerfeKurz), 
					  res.getString(R.string.statBezeichnungEffektivitaetKurz));
		
		for (cSpieler.moveToFirst(); !cSpieler.isAfterLast(); cSpieler.moveToNext()) {
			spielerId=getSpielerId(cSpieler);
			if(countStatSpielerId(spielerId, spielId)>0){	// Hat der Spieler im Spiel gespielt?
				spielerTore=countTickerSpielerAktionen(spielerId, spielId, "2")+
							countTickerSpielerAktionen(spielerId, spielId, "14")+
							countTickerSpielerAktionen(spielerId, spielId, "20");	// Wie viele Tore hat der Spieler geworfen?
				spielerFehlwurf=countTickerSpielerAktionen(spielerId, spielId, "3")+
								countTickerSpielerAktionen(spielerId, spielId, "15")+
								countTickerSpielerAktionen(spielerId, spielId, "21")+spielerTore;	// Wie viele Tore hat der Spieler geworfen?
				if(spielerTore>0 || spielerFehlwurf>0){
					spielerProzent=spielerTore*100/spielerFehlwurf;
				} else {
					spielerProzent=0;
				}
				strSpielerName=getSpielerName(spielerId);
				insertStatTor(strSpielerName, String.valueOf(spielerTore), String.valueOf(spielerFehlwurf), 
						String.valueOf(spielerProzent)+"%");
			}
		}
		cSpieler.close();
	}
	
	public void createStatTickerTore(String spielId, String teamHeimId, String teamAuswId, Context context) {
		
		deleteStatTickerTor();
		
		int spielerTore=0;
		int spielerFehlwurf=0;
		int spielerProzent=0;
		String spielerId=null;
		String strSpielerName=null;
		String strTeamName=null;
		Resources res = context.getResources();
		
		SQLiteDatabase db=getWritableDatabase();
		Cursor cSpieler=db.rawQuery("SELECT * FROM spieler WHERE teamID=? OR teamID=?", new String[] {teamHeimId, teamAuswId});
		
		insertStatTickerTor("",
							"",
							res.getString(R.string.statBezeichnungToreKurz), 
							res.getString(R.string.statBezeichnungWuerfeKurz), 
							res.getString(R.string.statBezeichnungEffektivitaetKurz));
		
		for (cSpieler.moveToFirst(); !cSpieler.isAfterLast(); cSpieler.moveToNext()) {
			spielerId=getSpielerId(cSpieler);
			if(countStatSpielerId(spielerId, spielId)>0){	// Hat der Spieler im Spiel gespielt?
				spielerTore=countTickerSpielerAktionen(spielerId, spielId, "2")+
							countTickerSpielerAktionen(spielerId, spielId, "14")+
							countTickerSpielerAktionen(spielerId, spielId, "20");	// Wie viele Tore hat der Spieler geworfen?
				spielerFehlwurf=countTickerSpielerAktionen(spielerId, spielId, "3")+
								countTickerSpielerAktionen(spielerId, spielId, "15")+
								countTickerSpielerAktionen(spielerId, spielId, "21")+spielerTore;	// Wie viele Tore hat der Spieler geworfen?
				if(spielerTore>0 || spielerFehlwurf>0){
					spielerProzent=spielerTore*100/spielerFehlwurf;
				} else {
					spielerProzent=0;
				}
				strSpielerName=getSpielerName(spielerId);
				strTeamName=getTeamNameById(getSpielerTeamId(spielerId));
				if (spielerTore>0 || spielerFehlwurf>0){
					insertStatTickerTor(strSpielerName, strTeamName, String.valueOf(spielerTore), String.valueOf(spielerFehlwurf), 
							String.valueOf(spielerProzent)+"%");
				}
			}
		}
		cSpieler.close();
	}
    
/* Statistik Spieler erstellen */
  
	public void createStatSpieler(String spielId, String teamId) {
		
		deleteStatTor();
		
		Cursor c=getSpielCursor(spielId);
		c.moveToFirst();
		
		String spielerId=null;
		String strSpielerName=null;
		String strSpielerNummer=null;
		
		String[] args={teamId};
		SQLiteDatabase db=getWritableDatabase();
		Cursor cSpieler=db.rawQuery("SELECT * FROM spieler WHERE teamID=?", args);
		
		for (cSpieler.moveToFirst(); !cSpieler.isAfterLast(); cSpieler.moveToNext()) {
			spielerId=getSpielerId(cSpieler);
			if(countStatSpielerId(spielerId, spielId)>0){	// Hat der Spieler im Spiel gespielt?
				strSpielerName=getSpielerName(spielerId);
				strSpielerNummer=getSpielerNummer(spielerId);
				/** Hinweis: StatTor hier missbraucht um Spielername und Rückennummer einzutragen */ 
				insertStatTor(strSpielerName, strSpielerNummer, spielerId, "");
			}
		}
	}
	
	public void createStatSpielerStat(String spielId, String spielerId, Context context) {
		
		String spielerBezeichnung=null;
		String strSpielerWert=null;
		int intSpielerTore=0;
		int intSpielerTore7m=0;
		int intSpielerToreTG=0;
		int intSpielerToreGesamt=0;
		int intSpielerFehlwurf=0;
		int intSpielerFehlwurf7m=0;
		int intSpielerFehlwurfTG=0;
		int intSpielerFehlwurfGesamt=0;
		int intSpielerProzent=0;
		Resources res = context.getResources();
		
		String spielerPosition=getSpielerPosition(spielerId);
        
		deleteStatSpieler();
		
		if(spielerPosition.equals(res.getString(R.string.spielerPositionTorwart))){
			intSpielerTore=countTickerSpielerAktionen(spielerId, spielId, "16");
			intSpielerTore7m=countTickerSpielerAktionen(spielerId, spielId, "18");
			intSpielerToreTG=countTickerSpielerAktionen(spielerId, spielId, "22");
		
			intSpielerFehlwurf=countTickerSpielerAktionen(spielerId, spielId, "17");
			intSpielerFehlwurf7m=countTickerSpielerAktionen(spielerId, spielId, "19");
			intSpielerFehlwurfTG=countTickerSpielerAktionen(spielerId, spielId, "23");
		} else {
			intSpielerTore=countTickerSpielerAktionen(spielerId, spielId, "2");
			intSpielerTore7m=countTickerSpielerAktionen(spielerId, spielId, "14");
			intSpielerToreTG=countTickerSpielerAktionen(spielerId, spielId, "20");
			
			intSpielerFehlwurf=countTickerSpielerAktionen(spielerId, spielId, "3");
			intSpielerFehlwurf7m=countTickerSpielerAktionen(spielerId, spielId, "15");
			intSpielerFehlwurfTG=countTickerSpielerAktionen(spielerId, spielId, "21");
		}
		intSpielerToreGesamt=intSpielerTore+intSpielerTore7m+intSpielerToreTG;
		intSpielerFehlwurfGesamt=intSpielerFehlwurf+intSpielerFehlwurf7m+intSpielerFehlwurfTG;
		
		spielerBezeichnung=res.getString(R.string.statBezeichnungTorEffektivitaet)+":";
			if(intSpielerToreGesamt+intSpielerFehlwurfGesamt>0){
				intSpielerProzent=intSpielerToreGesamt*100/(intSpielerToreGesamt+intSpielerFehlwurfGesamt);
			}
				
			strSpielerWert=String.valueOf(intSpielerToreGesamt)+
					" / " + String.valueOf(intSpielerToreGesamt+intSpielerFehlwurfGesamt)+
					" / " + String.valueOf(intSpielerProzent)+" %";
			insertStatSpieler(spielerBezeichnung, strSpielerWert);
				
		spielerBezeichnung=res.getString(R.string.statBezeichnung7mEffektivitaet)+":";
			
			if(intSpielerTore7m+intSpielerFehlwurf7m>0){
				intSpielerProzent=intSpielerTore7m*100/(intSpielerTore7m+intSpielerFehlwurf7m);
			} else {
				intSpielerProzent=0;
			}

			strSpielerWert=String.valueOf(intSpielerTore7m)+
					" / " + String.valueOf(intSpielerTore7m+intSpielerFehlwurf7m)+
					" / " + String.valueOf(intSpielerProzent)+" %";
			insertStatSpieler(spielerBezeichnung, strSpielerWert);
				
		spielerBezeichnung=res.getString(R.string.statBezeichnungTGEffektivitaet)+":";
				
			if(intSpielerToreTG+intSpielerFehlwurfTG>0){
				intSpielerProzent=intSpielerToreTG*100/(intSpielerToreTG+intSpielerFehlwurfTG);
			} else {
				intSpielerProzent=0;
			}

			strSpielerWert=String.valueOf(intSpielerToreTG)+
				" / " + String.valueOf(intSpielerToreTG+intSpielerFehlwurfTG)+
				" / " + String.valueOf(intSpielerProzent)+" %";
			insertStatSpieler(spielerBezeichnung, strSpielerWert);

		spielerBezeichnung=res.getString(R.string.statBezeichnungTechnischeFehler)+":";
		strSpielerWert=String.valueOf(countTickerSpielerAktionen(spielerId, spielId, "4"));
		insertStatSpieler(spielerBezeichnung, strSpielerWert);
		
		if((countTickerSpielerAktionen(spielerId, spielId, "6"))>0){
			spielerBezeichnung=res.getString(R.string.statBezeichnungGelbeKarte)+":";
			strSpielerWert=String.valueOf(countTickerSpielerAktionen(spielerId, spielId, "6"));
			insertStatSpieler(spielerBezeichnung, strSpielerWert);
		}
		
		if((countTickerSpielerAktionen(spielerId, spielId, "5"))>0){
			spielerBezeichnung=res.getString(R.string.statBezeichnungZweiMinuten)+":";
			strSpielerWert=String.valueOf(countTickerSpielerAktionen(spielerId, spielId, "5"));
			insertStatSpieler(spielerBezeichnung, strSpielerWert);
		}
		
		if((countTickerSpielerAktionen(spielerId, spielId, "11"))>0){
			spielerBezeichnung=res.getString(R.string.statBezeichnungZweiMalZwei)+":";
			strSpielerWert=String.valueOf(countTickerSpielerAktionen(spielerId, spielId, "11"));
			insertStatSpieler(spielerBezeichnung, strSpielerWert);
		}
		
		if((countTickerSpielerAktionen(spielerId, spielId, "9"))>0){
			spielerBezeichnung=res.getString(R.string.statBezeichnungRoteKarte)+":";
			strSpielerWert=String.valueOf(countTickerSpielerAktionen(spielerId, spielId, "9"));
			insertStatSpieler(spielerBezeichnung, strSpielerWert);
		}
		
	}
    
/* Statistikdaten abfragen */
  
	  public String getStatSpielTeamHeim(Cursor c) {
		  return(c.getString(1));
	  }
	  
	  public String getStatSpielBezeichnung(Cursor c) {
		  return(c.getString(2));
	  }
	  
	  public String getStatSpielTeamAusw(Cursor c) {
		  return(c.getString(3));
	  }
	  
	  public String getStatTickerTorSpieler(Cursor c) {
		  return(c.getString(1));
	  }
	  
	  public String getStatTickerTorTeam(Cursor c) {
		  return(c.getString(2));
	  }
	  
	  public String getStatTickerTorTore(Cursor c) {
		  return(c.getString(3));
	  }
	  
	  public String getStatTickerTorChancen(Cursor c) {
		  return(c.getString(4));
	  }
	  
	  public String getStatTickerTorQuote(Cursor c) {
		  return(c.getString(5));
	  }
	  
	  public String getStatTorBezeichnung(Cursor c) {
		  return(c.getString(1));
	  }
	  
	  public String getStatTorTore(Cursor c) {
		  return(c.getString(2));
	  }
	  
	  public String getStatTorChancen(Cursor c) {
		  return(c.getString(3));
	  }
	  
	  public String getStatTorQuote(Cursor c) {
		  return(c.getString(4));
	  }
	  
	  public String getStatSpielerBezeichnung(Cursor c) {
		  return(c.getString(1));
	  }
	  
	  public String getStatSpielerWert(Cursor c) {
		  return(c.getString(2));
	  }
	  
	    
/* Bilder skalieren */
	  
	  /* Bilder Relative Layout skalieren */
	  public void scaleImageRelative(ImageView view, int boundBoxInDp)
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
	        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
	        params.width = width;
	        params.height = height;
	        view.setLayoutParams(params);
	    }
	  	
	  	/* Bilder Linear Layout skalieren */
	    public void scaleImageLinear(ImageButton view, int boundBoxInDp)
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
	    
	    /* Bilder Linear Layout skalieren */
	    public void scaleImageViewLinear(ImageView view, int boundBoxInDp)
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
	    
	    public int getDay(Date date) 
	    {
	    	Calendar cal = Calendar.getInstance();
	        cal.setTime(date);
	        return cal.get(Calendar.DATE);
	    }
	    
	    public int getMonth(Date date) 
	    {
	    	Calendar cal = Calendar.getInstance();
	        cal.setTime(date);
	        return cal.get(Calendar.MONTH);
	    }
	    
	    public int getYear(Date date) 
	    {
	    	Calendar cal = Calendar.getInstance();
	        cal.setTime(date);
	        return cal.get(Calendar.YEAR);
	    }
	    
	    public Date setDate(Integer intDay, Integer intMonth, Integer intYear) 
	    {
	    	Date spielDatum=new Date(intYear-1900, intMonth, intDay);
	    	return spielDatum;
	    }
	    
}

