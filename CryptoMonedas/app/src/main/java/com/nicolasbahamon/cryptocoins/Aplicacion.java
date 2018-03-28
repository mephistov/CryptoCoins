package com.nicolasbahamon.cryptocoins;

import android.app.Application;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.ads.MobileAds;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.nicolasbahamon.cryptocoins.models.Coin;
import com.nicolasbahamon.cryptocoins.data.DataBaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Nicolas Bahamon on 15/12/2017.
 */

public class Aplicacion extends Application {


    public SharedPreferences diskData;
    public SharedPreferences.Editor diskEditor;
    public DataBaseHelper db;
    public Coin coinShow = null;
    public int btcValue = 9000;




    @Override
    public void onCreate() {
        super.onCreate();

        MobileAds.initialize(this, "ca-app-pub-5879820896125814~8581390076");

        Log.e("si","coemnzo");
        diskData = getSharedPreferences("APPINFORMATIONCoins", MODE_PRIVATE);
        diskEditor = diskData.edit();

        btcValue = diskData.getInt("btcValue",9000);
    }

    public DataBaseHelper getDB(){
        if(db == null){
            db = new DataBaseHelper(getApplicationContext());
        }
        return db;
    }


    public String numberFormat(double value){
        DecimalFormat df2 = new DecimalFormat( "#,###,###,##0.00" );
        String dd2dec = df2.format(value);
        return  dd2dec;
    }

    public String numberFormatCOP(double value){
        DecimalFormat df2 = new DecimalFormat( "#,###,###,##0" );
        String dd2dec = df2.format(value);
        return  dd2dec;
    }

    public Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE, 500, 500, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.colorAccent):getResources().getColor(R.color.colorPrimary);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }
    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + "/PolinexNic");
        // have the object build the directory structure, if needed.

        if (!wallpaperDirectory.exists()) {
            Log.d("dirrrrrr", "" + wallpaperDirectory.mkdirs());
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();   //give read write permission
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";

    }

    public void getValuesGraviex(String resp){
        if(resp != null) {
            try {

                JSONObject objecto = new JSONObject(resp);

                ContentValues values = new ContentValues();
                values.put("name", "Graviex");
                values.put("url", "https://graviex.net");
                values.put("sell_api", "https://graviex.net/api/v2/order_book?market=");
                values.put("buy_api", "https://graviex.net/api/v2/order_book?market=");
                values.put("api", "https://graviex.net/markets/");//zacabtc
                long idchange = getDB().saveXchange(values);

                ArrayList<Coin> monedas = getDB().getAllCoins();

                for (Coin moneda : monedas) {

                    String toSearch = moneda.shortname + "btc";
                    if (objecto.has(toSearch.toLowerCase())) {
                        JSONObject objCoin = objecto.getJSONObject(toSearch.toLowerCase());

                        values = new ContentValues();

                        values.put("id_xchange", idchange);
                        values.put("shortname", moneda.shortname);
                        values.put("name_exchange", "Graviex");
                        values.put("has_order_book", 1);
                        values.put("id_coin", moneda.idCoin);
                        values.put("volume", objCoin.getJSONObject("ticker").getDouble("vol"));
                        values.put("ask", objCoin.getJSONObject("ticker").getDouble("sell"));
                        values.put("bid", objCoin.getJSONObject("ticker").getDouble("buy"));
                        values.put("last", objCoin.getJSONObject("ticker").getDouble("last"));

                        getDB().procesInformationExchanges(values);
                    }

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public ContentValues getValuesCryptoBridge(String resp) {

        if(resp != null) {
            try {
                JSONArray objecto = new JSONArray(resp);

                ContentValues values = new ContentValues();
                values.put("name", "CryptoBridge");
                values.put("url", "https://crypto-bridge.org/");
                values.put("sell_api", "");
                values.put("buy_api", "");
                values.put("api", "https://wallet.crypto-bridge.org/market/");//BRIDGE.CROP_BRIDGE.BTC
                long idchange = getDB().saveXchange(values);
                if (idchange > 0) {

                    for (int i = 0; i < objecto.length(); i++) {

                        JSONObject alcoin = objecto.getJSONObject(i);
                        String[] splitD = alcoin.getString("id").split("_");
                        String shortname = splitD[0];

                        values = new ContentValues();
                        values.put("id_xchange", idchange);
                        values.put("shortname", shortname);
                        values.put("name_exchange", "CryptoBridge");
                        values.put("has_order_book", 0);
                        Coin coin = getDB().existCoin(shortname);
                        if (coin == null) {
                            values.put("id_coin", 0);
                            values.put("volume", 0);
                            values.put("ask", 0);
                            values.put("bid", 0);
                            values.put("last", 0);
                        } else {
                            values.put("id_coin", coin.idCoin);
                            values.put("volume", alcoin.getDouble("volume"));
                            values.put("ask", alcoin.getDouble("ask"));
                            values.put("bid", alcoin.getDouble("bid"));
                            values.put("last", alcoin.getDouble("last"));


                        }

                        getDB().procesInformationExchanges(values);
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public void getValuesSouthXchange(String resp) {
        if(resp != null) {
            try {

                JSONArray objecto = new JSONArray(resp);

                ContentValues values = new ContentValues();
                values.put("name", "SouthXchange");
                values.put("url", "https://www.southxchange.com");
                values.put("sell_api", "https://www.southxchange.com/api/book/");//ZACA/BTC
                values.put("buy_api", "https://www.southxchange.com/api/book/");
                values.put("api", "https://www.southxchange.com/api/prices");
                long idchange = getDB().saveXchange(values);
                if (idchange > 0) {
                    for (int i = 0; i < objecto.length(); i++) {
                        JSONObject alcoin = objecto.getJSONObject(i);
                        String[] splitD = alcoin.getString("Market").split("/");
                        String shortname = splitD[0];

                        if(splitD[1].equals("BTC")){
                            values = new ContentValues();
                            values.put("id_xchange", idchange);
                            values.put("shortname", shortname);
                            values.put("name_exchange", "SouthXchange");
                            values.put("has_order_book", 1);

                            Coin coin = getDB().existCoin(shortname);

                            if (coin == null) {
                                values.put("id_coin", 0);
                                values.put("volume", 0);
                                values.put("ask", 0);
                                values.put("bid", 0);
                                values.put("last", 0);
                            } else {
                                values.put("id_coin", coin.idCoin);
                                values.put("volume", alcoin.getDouble("Volume24Hr"));
                                values.put("ask", alcoin.getDouble("Ask"));
                                values.put("bid", alcoin.getDouble("Bid"));
                                values.put("last", alcoin.getDouble("Last"));


                            }

                            getDB().procesInformationExchanges(values);
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void getValuesCoinexchangeio(String resp, String resp2) {
        if(resp != null) {
            try {

                JSONObject data = new JSONObject(resp);
                JSONObject data2 = new JSONObject(resp2);

                JSONArray objecto = data.getJSONArray("result");
                JSONArray objecto2 = data2.getJSONArray("result");

                ContentValues values = new ContentValues();
                values.put("name", "CoinExchangeIO");
                values.put("url", "https://www.coinexchange.io/");
                values.put("sell_api", "https://www.coinexchange.io/api/v1/getorderbook?market_id=");//market_id=1
                values.put("buy_api", "https://www.coinexchange.io/api/v1/getorderbook?market_id=");
                values.put("api", "https://www.coinexchange.io/api/v1/getmarkets");
                long idchange = getDB().saveXchange(values);
                if (idchange > 0) {
                    for (int i = 0; i < objecto.length(); i++) {
                        JSONObject alcoin = objecto.getJSONObject(i);

                        String splitD = alcoin.getString("BaseCurrency");


                        if(splitD.equals("Bitcoin")){
                            JSONObject alcoin2 = getIdCoin(objecto2,alcoin.getInt("MarketID"));

                            values = new ContentValues();
                            values.put("id_xchange", idchange);
                            values.put("shortname", alcoin.getString("MarketAssetCode"));
                            values.put("name_exchange", "CoinExchangeIO");
                            values.put("has_order_book", 1);

                            Coin coin = getDB().existCoin(alcoin.getString("MarketAssetCode"));

                            if (coin == null) {
                                values.put("id_coin", 0);
                                values.put("volume", 0);
                                values.put("ask", 0);
                                values.put("bid", 0);
                                values.put("last", 0);
                            } else {
                                if(alcoin2 != null) {

                                    values.put("id_coin", coin.idCoin);
                                    values.put("volume", alcoin2.getDouble("Volume"));
                                    values.put("ask", alcoin2.getDouble("AskPrice"));
                                    values.put("coinexchangeio_id", alcoin2.getInt("MarketID"));
                                    values.put("bid", alcoin2.getDouble("BidPrice"));
                                    values.put("last", alcoin2.getDouble("LastPrice"));
                                }

                            }

                            getDB().procesInformationExchanges(values);
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private JSONObject getIdCoin(JSONArray all, int  idCoin){
        JSONObject resp = null;

        try {
            for (int i = 0; i < all.length(); i++) {
            JSONObject alcoin = all.getJSONObject(i);
                if(alcoin.getInt("MarketID") == idCoin){
                    resp = alcoin;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



        return resp;
    }

    public void getValuesTradeSatoshi(String resp) {
        if(resp != null) {
            try {

                JSONObject resOb = new JSONObject(resp);
                JSONArray objecto = resOb.getJSONArray("result");

                ContentValues values = new ContentValues();
                values.put("name", "TradeSatoshi");
                values.put("url", "https://tradesatoshi.com");
                values.put("sell_api", "https://tradesatoshi.com/api/public/getorderbook?depth=200&market=");//LTC_BTC
                values.put("buy_api", "https://tradesatoshi.com/api/public/getorderbook?depth=200&market=");
                values.put("api", "https://tradesatoshi.com/api/public/getmarketsummaries");
                long idchange = getDB().saveXchange(values);
                if (idchange > 0) {
                    for (int i = 0; i < objecto.length(); i++) {
                        JSONObject alcoin = objecto.getJSONObject(i);
                        String[] splitD = alcoin.getString("market").split("_");
                        String shortname = splitD[0];

                        if(splitD[1].equals("BTC")){
                            values = new ContentValues();
                            values.put("id_xchange", idchange);
                            values.put("shortname", shortname);
                            values.put("name_exchange", "TradeSatoshi");
                            values.put("has_order_book", 1);

                            if(shortname.equals("STAKE")){
                                Log.e("moneda","stake");
                            }

                            Coin coin = getDB().existCoin(shortname);

                            if (coin == null) {
                                values.put("id_coin", 0);
                                values.put("volume", 0);
                                values.put("ask", 0);
                                values.put("bid", 0);
                                values.put("last", 0);
                            } else {
                                values.put("id_coin", coin.idCoin);
                                values.put("volume", alcoin.getDouble("volume"));
                                values.put("ask", alcoin.getDouble("ask"));
                                values.put("bid", alcoin.getDouble("bid"));
                                values.put("last", alcoin.getDouble("last"));


                            }

                            getDB().procesInformationExchanges(values);
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void getValuesStockExchange(String resp) {
        if(resp != null) {
            try {

                JSONArray objecto = new JSONArray(resp);

                ContentValues values = new ContentValues();
                values.put("name", "StockExchange");
                values.put("url", "https://stocks.exchange/");
                values.put("sell_api", "https://stocks.exchange/trade/");// LIZ/BTC
                values.put("buy_api", "https://stocks.exchange/trade/");
                values.put("api", "https://stocks.exchange/trade/");
                long idchange = getDB().saveXchange(values);
                if (idchange > 0) {
                    for (int i = 0; i < objecto.length(); i++) {
                        JSONObject alcoin = objecto.getJSONObject(i);
                        String[] splitD = alcoin.getString("market_name").split("_");
                        String shortname = splitD[0];

                        if(splitD[1].equals("BTC")){
                            values = new ContentValues();
                            values.put("id_xchange", idchange);
                            values.put("shortname", shortname);
                            values.put("name_exchange", "StockExchange");
                            values.put("has_order_book", 0);

                            Coin coin = getDB().existCoin(shortname);

                            if (coin == null) {
                                values.put("id_coin", 0);
                                values.put("volume", 0);
                                values.put("ask", 0);
                                values.put("bid", 0);
                                values.put("last", 0);
                            } else {
                                values.put("id_coin", coin.idCoin);
                                values.put("volume", alcoin.getDouble("vol"));
                                values.put("ask", alcoin.getDouble("ask"));
                                values.put("bid", alcoin.getDouble("bid"));
                                values.put("last", alcoin.getDouble("last"));


                            }

                            getDB().procesInformationExchanges(values);
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void coinMarketCapAllcoins(String resHrl) {

        if(resHrl !=null){
            try {

                JSONArray allcoins = new JSONArray(resHrl);


                for(int i =0; i<allcoins.length();i++){
                    ContentValues values = new ContentValues();
                    JSONObject moneda = allcoins.getJSONObject(i);

                    values.put("name",moneda.getString("name"));
                    values.put("shortname",moneda.getString("symbol"));
                    String urllogo = "https://raw.githubusercontent.com/dziungles/cryptocurrency-logos/master/coins/32x32/";
                    urllogo += moneda.getString("id")+".png";
                    values.put("logo",urllogo);
                    values.put("porcentage_hour",moneda.getString("percent_change_1h"));
                    values.put("porcentage_day",moneda.getString("percent_change_24h"));
                    values.put("porcentage_week",moneda.getString("percent_change_7d"));
                    values.put("has_node",0);
                    values.put("price",moneda.getDouble("price_usd"));
                    values.put("rank",moneda.getInt("rank"));

                    if(moneda.getString("symbol").equals("BTC")){
                        btcValue = moneda.getInt("price_usd");
                        diskEditor.putInt("btcValue",btcValue);
                        Log.e("BTC actualizado",btcValue+"");
                    }


                    getDB().procesInformation(values);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }




    }

    public void getValuescryptohub(String resp) {
        if(resp != null) {
            try {

                JSONObject objecto = new JSONObject(resp);

                ContentValues values = new ContentValues();
                values.put("name", "CriptoHub");
                values.put("url", "https://cryptohub.online");
                values.put("sell_api", "");
                values.put("buy_api", "");
                values.put("api", "https://cryptohub.online/api/market/ticker/");//zacabtc
                long idchange = getDB().saveXchange(values);

                ArrayList<Coin> monedas = getDB().getAllCoins();

                for (Coin moneda : monedas) {

                    String toSearch = "BTC_"+moneda.shortname;
                    if (objecto.has(toSearch.toLowerCase())) {
                        JSONObject objCoin = objecto.getJSONObject(toSearch.toLowerCase());

                        values = new ContentValues();

                        values.put("id_xchange", idchange);
                        values.put("shortname", moneda.shortname);
                        values.put("name_exchange", "CriptoHub");
                        values.put("has_order_book", 1);
                        values.put("id_coin", moneda.idCoin);
                        values.put("volume", objCoin.getDouble("baseVolume"));
                        values.put("ask", objCoin.getDouble("lowestAsk"));
                        values.put("bid", objCoin.getDouble("highestBid"));
                        values.put("last", objCoin.getDouble("last"));

                        getDB().procesInformationExchanges(values);
                    }else{
                        //TODO agregar a la lista de monedas
                    }

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
