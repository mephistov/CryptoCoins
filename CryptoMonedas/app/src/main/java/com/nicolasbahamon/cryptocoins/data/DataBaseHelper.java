package com.nicolasbahamon.cryptocoins.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.nicolasbahamon.cryptocoins.models.Coin;
import com.nicolasbahamon.cryptocoins.models.Exchange;
import com.nicolasbahamon.cryptocoins.models.TrackingN;

import java.util.ArrayList;

/**
 * Created by Nicolas Bahamon on 10/31/16.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    //database version
    private static final int DATABASE_VERSION = 3;
    //database name
    private static final String DATABASE_NAME = "CryptoMoney.db";
    //table names
    private Context context_;

    //creat databases
    private static final String CREATE_TABLE_1 = "CREATE TABLE coins (id_coin INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT," +
            "shortname TEXT," +
            "explorer TEXT," +
            "web TEXT," +
            "nodes INTEGER," +
            "price REAL," +
            "price_btc REAL," +
            "roi INTEGER," +
            "roi_years REAL," +
            "coins_node INTEGER," +
            "node_worth REAL," +
            "coins_daily REAL," +
            "daily_usd REAL," +
            "logo_interno INTEGER," +
            "oldPrice INTEGER," +
            "favorite INTEGER," +
            "rank INTEGER," +
            "porcentage_hour TEXT," +
            "porcentage_day TEXT," +
            "porcentage_week TEXT," +
            "track_node INTEGER," +
            "has_node INTEGER NOT NULL," +
            "logo TEXT )";
    private static final String CREATE_TABLE_2= "CREATE TABLE xchange (id_xchange INTEGER PRIMARY KEY AUTOINCREMENT," +
           "name TEXT," +
            "url TEXT," +
            "sell_api TEXT," +
            "api TEXT," +
            "buy_api TEXT)" ;
    private static final String CREATE_TABLE_3 = "CREATE TABLE coins_xchanges (id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "id_coin INTEGER," +
            "shortname TEXT," +
            "name_exchange TEXT," +
            "volume REAL," +
            "ask REAL," +
            "has_order_book INTEGER," +
            "bid REAL," +
            "change REAL," +
            "coinexchangeio_id INTEGER," +
            "last REAL," +
            "id_xchange INTEGER)";
    private static final String CREATE_TABLE_4 = "CREATE TABLE track_nodes (id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "id_coin INTEGER," +
            "order_show INTEGER," +
            "shortname TEXT," +
            "name_coin TEXT," +
            "extra_name TEXT," +
            "ip_node TEXT," +
            "explorer TEXT," +
            "wallet TEXT," +
            "current_amount REAL," +
            "last_amount REAL," +
            "coin_value REAL," +
            "mn_cost REAL," +
            "usd_cost REAL," +
            "node_cant_coins INTEGER)";



    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        context_ = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_1);
        db.execSQL(CREATE_TABLE_2);
        db.execSQL(CREATE_TABLE_3);
        db.execSQL(CREATE_TABLE_4);

        //fill xchange data
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if(oldVersion == 1){
            String aquery="ALTER TABLE track_nodes ADD COLUMN 'last_amount' REAL;";
            db.execSQL(aquery);
        }
        if(oldVersion <= 2){
            String aquery="ALTER TABLE track_nodes ADD COLUMN 'order_show' INTEGER;";
            db.execSQL(aquery);
            aquery="UPDATE track_nodes set order_show = id";
            db.execSQL(aquery);
            Log.e("Updated","db v3");
        }

    }



    //*********************************** Implementation ************************************

    public void procesInformation(ContentValues values){

        Coin tempC = getCoinByName((String) values.get("shortname"));
        if(tempC== null){
            insertActivity(values);
            Log.e("Insert",values.getAsString("shortname"));
        }else{
            values.put("oldPrice",tempC.price);
            updateCoinInfo(values);
            Log.e("update",values.getAsString("shortname"));
        }
    }


    public void procesInformationExchanges(ContentValues values){

        Exchange dato = existExchange( values.getAsInteger("id_coin"),values.getAsInteger("id_xchange"));
        if(dato == null){
            insertCoingXchange(values);
            Log.e("Insert exchange coin",values.getAsString("shortname"));
        }else{
            updateExchangeInfo(values,dato.id);
            Log.e("update exchange coin",values.getAsString("shortname"));
        }
    }


    private void updateExchangeInfo(ContentValues values, long id) {
        SQLiteDatabase db = getReadableDatabase();

        String selection = "id = ?";
        String name = id+"";
        String[] selectionArgs = { name };

        int count = db.update(
                "coins_xchanges",
                values,
                selection,
                selectionArgs);

    }


    public Exchange existExchange(Integer id_coin, Integer id_xchange) {
        Exchange dato = null;

        SQLiteDatabase db = getReadableDatabase();
        String query="SELECT * FROM coins_xchanges WHERE id_coin = "+id_coin+" and id_xchange="+id_xchange;
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            dato = returnExchangeDB(c);
        }
        c.close();

        return dato;
    }


    //*************** INSERT*********************
   public long insertActivity(ContentValues values){
        SQLiteDatabase db = getWritableDatabase();

        long newRowId = db.insert("coins", null, values);

        return newRowId;
    }

    public long insertTracking(ContentValues values){
        SQLiteDatabase db = getWritableDatabase();
        long newRowId = db.insert("track_nodes", null, values);
        return newRowId;
    }

    public long saveXchange(ContentValues values) {

        SQLiteDatabase db = getReadableDatabase();
        String query="SELECT * FROM xchange WHERE name = '"+values.getAsString("name")+"'";
        Cursor c = db.rawQuery(query, null);
        long id = 0;
        if (c.moveToFirst()) {
            id = c.getLong(c.getColumnIndex("id_xchange"));
        }
        c.close();

        if(id == 0 ) {
            db = getWritableDatabase();
            id = db.insert("xchange", null, values);
        }

        return id;


    }

    public long insertCoingXchange(ContentValues values) {

        SQLiteDatabase db = getWritableDatabase();

        long newRowId = db.insert("coins_xchanges", null, values);

        return newRowId;


    }


    // *********************** GET ***********************


    public ArrayList<TrackingN> getAllTracking() {

        ArrayList<TrackingN> list = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String query="SELECT * FROM track_nodes ORDER BY name_coin";
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                TrackingN exchan = returnTrackingDB(c);
                list.add(exchan);
            }while(c.moveToNext());
        }
        c.close();

        return list;
    }


    public Coin getCoinByName(String name){
       Coin moneda = null;

        SQLiteDatabase db = getReadableDatabase();
        String query="SELECT * FROM coins WHERE shortname = '"+name+"'";
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            moneda = returnCoingDB(c);
        }
        c.close();

       return moneda;
    }

   public ArrayList<Exchange> getListExchangesByCoinId(String shortname){
       ArrayList<Exchange> lista = new ArrayList<>();

       SQLiteDatabase db = getReadableDatabase();
       String query="SELECT * FROM coins_xchanges WHERE shortname = '"+shortname+"'";
       Cursor c = db.rawQuery(query, null);
       if (c.moveToFirst()) {
           do {
                Exchange exchan = returnExchangeDB(c);
                lista.add(exchan);


           }while(c.moveToNext());

       }
       c.close();
       return lista;
   }
    public String getExchangeApi(long id) {
        String apiresp ="";
        SQLiteDatabase db = getReadableDatabase();
        String query="SELECT buy_api FROM xchange WHERE id_xchange = "+id;
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            apiresp = c.getString(c.getColumnIndex("buy_api"));
        }
        return apiresp;
    }


    // *********************** UPDATE ***********************

    public int updateTrackingInfo(ContentValues values) {
        SQLiteDatabase db = getReadableDatabase();

        String selection = "id = ?";
        String name = values.getAsString("id");
        String[] selectionArgs = { name };

        int val =  db.update(
                "track_nodes",
                values,
                selection,
                selectionArgs);

       // TrackingN quecarajos = getTackById(values.getAsString("id"));

        return val;
    }

    public TrackingN getTackById(String id){
        ArrayList<TrackingN> list = new ArrayList<>();
        TrackingN exchan = null;
        SQLiteDatabase db = getReadableDatabase();
        String query="SELECT * FROM track_nodes WHERE id ="+id;
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                exchan = returnTrackingDB(c);
                list.add(exchan);
            }while(c.moveToNext());
        }
        c.close();

        return exchan;
    }

    public void updateCoinInfo(ContentValues values){
        SQLiteDatabase db = getReadableDatabase();

        String selection = "shortname = ?";
        String name = values.getAsString("shortname");
        String[] selectionArgs = { name };

        int count = db.update(
                "coins",
                values,
                selection,
                selectionArgs);


    }

    private Coin returnCoingDB(Cursor c){
        Coin coin = new Coin();
        coin.idCoin = c.getLong(c.getColumnIndex("id_coin"));
        coin.name = c.getString(c.getColumnIndex("name"));
        coin.logo = c.getString(c.getColumnIndex("logo"));
        coin.price = c.getDouble(c.getColumnIndex("price"));
        coin.oldPrice = c.getDouble(c.getColumnIndex("oldPrice"));
        coin.price_btc = c.getDouble(c.getColumnIndex("price_btc"));
        coin.shortname = c.getString(c.getColumnIndex("shortname"));
        coin.roi_years = c.getDouble(c.getColumnIndex("roi_years"));
        coin.roi = c.getInt(c.getColumnIndex("roi"));
        coin.coins_node = c.getInt(c.getColumnIndex("coins_node"));
        coin.nodes = c.getInt(c.getColumnIndex("nodes"));
        coin.node_worth = c.getDouble(c.getColumnIndex("node_worth"));
        coin.coins_daily = c.getDouble(c.getColumnIndex("coins_daily"));
        coin.daily_usd = c.getDouble(c.getColumnIndex("daily_usd"));
        coin.favorite = c.getInt(c.getColumnIndex("favorite"));
        coin.track_node = c.getInt(c.getColumnIndex("track_node"));
        coin.porcentage_hour = c.getDouble(c.getColumnIndex("porcentage_hour"));
        coin.porcentage_day = c.getDouble(c.getColumnIndex("porcentage_day"));
        coin.porcentage_week = c.getDouble(c.getColumnIndex("porcentage_week"));

        return coin;
    }
    private Exchange returnExchangeDB(Cursor c){
        Exchange exchange = new Exchange();

        exchange.name = c.getString(c.getColumnIndex("name_exchange"));
        exchange.id = c.getLong(c.getColumnIndex("id"));
        exchange.id_exchange = c.getLong(c.getColumnIndex("id_xchange"));
        exchange.ask = c.getDouble(c.getColumnIndex("ask"));
        exchange.orderBook = c.getInt(c.getColumnIndex("has_order_book"));
        exchange.coinexchangeio_id = c.getInt(c.getColumnIndex("coinexchangeio_id"));
        exchange.bid = c.getDouble(c.getColumnIndex("bid"));

        return exchange;
    }
    private TrackingN returnTrackingDB(Cursor c){
        TrackingN exchange = new TrackingN();

        exchange.name = c.getString(c.getColumnIndex("name_coin"));
        exchange.id = c.getLong(c.getColumnIndex("id"));
        exchange.explorer = c.getString(c.getColumnIndex("explorer"));
        exchange.shortname = c.getString(c.getColumnIndex("shortname"));
        exchange.address = c.getString(c.getColumnIndex("wallet"));
        exchange.balance = c.getDouble(c.getColumnIndex("current_amount"));
        exchange.extra_name = c.getString(c.getColumnIndex("extra_name"));
        exchange.mncost = c.getDouble(c.getColumnIndex("mn_cost"));
        exchange.lastBalance = c.getDouble(c.getColumnIndex("last_amount"));

        return exchange;
    }

    public ArrayList<Coin> getAllCoinsMN() {

        ArrayList<Coin> allCoins = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String query="SELECT * FROM coins WHERE has_node = 1 ORDER BY roi ASC";
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {

                allCoins.add(returnCoingDB(c));

            }while(c.moveToNext());
        }
        c.close();



        return allCoins;
    }

    public ArrayList<Coin> getAllCoins() {

        ArrayList<Coin> allCoins = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String query="SELECT * FROM coins ORDER BY rank ASC";
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {

                allCoins.add(returnCoingDB(c));

            }while(c.moveToNext());
        }
        c.close();



        return allCoins;
    }

    public ArrayList<Coin> getByNameASC() {

        ArrayList<Coin> allCoins = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String query="SELECT * FROM coins ORDER BY shortname DESC";
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {

                allCoins.add(returnCoingDB(c));

            }while(c.moveToNext());
        }
        c.close();



        return allCoins;

    }

    public ArrayList<Coin> getByNameDSC() {
        ArrayList<Coin> allCoins = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query="SELECT * FROM coins ORDER BY shortname DESC";
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {

                allCoins.add(returnCoingDB(c));

            }while(c.moveToNext());
        }
        c.close();
        return allCoins;
    }

    public ArrayList<Coin> getByValueDSC() {

        ArrayList<Coin> allCoins = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query="SELECT * FROM coins ORDER BY price DESC";
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {

                allCoins.add(returnCoingDB(c));

            }while(c.moveToNext());
        }
        c.close();
        return allCoins;

    }

    public ArrayList<Coin> getByValueASC() {

        ArrayList<Coin> allCoins = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query="SELECT * FROM coins ORDER BY price ASC";
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {

                allCoins.add(returnCoingDB(c));

            }while(c.moveToNext());
        }
        c.close();
        return allCoins;

    }

    public ArrayList<Coin> sortGenericMn(String byWhat, int direction, boolean favorite) {

        String dir = "ASC";
        if(direction == 1)
            dir = "DESC";
        String extrada = "";
        if(favorite){
            extrada = " and favorite = 1";
        }

        ArrayList<Coin> allCoins = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query="SELECT * FROM coins Where has_node = 1"+extrada+" ORDER BY "+byWhat+" "+dir;
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {

                allCoins.add(returnCoingDB(c));

            }while(c.moveToNext());
        }
        c.close();
        return allCoins;

    }

    public ArrayList<Coin> sortGeneric() {

        ArrayList<Coin> allCoins = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query="SELECT * FROM coins Where favorite = 1 ORDER BY rank ASC";
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {

                allCoins.add(returnCoingDB(c));

            }while(c.moveToNext());
        }
        c.close();
        return allCoins;

    }

    public void deleteTrack(long id) {
        SQLiteDatabase db = getReadableDatabase();
        String selection =  "id = ?";
        String[] selectionArgs = { id+"" };
        db.delete("track_nodes", selection, selectionArgs);
    }











 /*   public int updateRewardItem(ContentValues values, String idRew) {
        SQLiteDatabase db = getReadableDatabase();

        // Which row to update, based on the title
        String selection = "sku = ?";
        String[] selectionArgs = { idRew+"" };

        int count = db.update(
                "rewards",
                values,
                selection,
                selectionArgs);

        return count;
    }
    */

    // ***************************** DELETE INFORMATION *******************************


  /*  public void deleteActivityById(long idActivity){
        SQLiteDatabase db = getReadableDatabase();
        String selection = Constants.ACTIVITY.IDACTIVITY + " = ?";
        String[] selectionArgs = { idActivity+"" };
        db.delete("activities", selection, selectionArgs);

    }


    public void deleteRewards() {
        SQLiteDatabase db = getReadableDatabase();
        db.delete("rewards", null, null);
    }

*/


}
