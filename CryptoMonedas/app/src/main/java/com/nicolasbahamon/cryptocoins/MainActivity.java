package com.nicolasbahamon.cryptocoins;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.nicolasbahamon.cryptocoins.models.Coin;
import com.nicolasbahamon.cryptocoins.network.HttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private HttpClient httpCLient;
    private Button coins, settings, closeSettings, polucy, contactus;
    RelativeLayout masternode ,trackNodes, calculator;
    private RelativeLayout loadingFirst;
    private TextView updating,textViewBTCVALUE, versionTxt;
    private LinearLayout menuZone;

    private FirebaseAnalytics mFirebaseAnalytics;
    private AdView mAdView;

    private ArrayList<Coin> coinsArray;
    private CoinsAdapter adapter;
    private ListView listCoins;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //buugs
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Main");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        String version = BuildConfig.VERSION_NAME;
        httpCLient = new HttpClient(getApplicationContext(), ((Aplicacion) getApplication()));
        new RedeemTask().execute();

        //adds
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);



        loadingFirst =(RelativeLayout)findViewById(R.id.loadingFirst);
        updating = (TextView)findViewById(R.id.textView57);
        textViewBTCVALUE = (TextView)findViewById(R.id.textViewBTCVALUE);
        versionTxt = (TextView)findViewById(R.id.textView49);

        settings = (Button)findViewById(R.id.button18);
        closeSettings = (Button)findViewById(R.id.button15);
        menuZone = (LinearLayout)findViewById(R.id.menuopc);
        polucy = (Button)findViewById(R.id.button16);
        contactus = (Button)findViewById(R.id.button17);

        listCoins = (ListView)findViewById(R.id.favoritesCoinList);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuZone.setVisibility(View.VISIBLE);
            }
        });
        closeSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuZone.setVisibility(View.GONE);
            }
        });
        polucy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Aplicacion) getApplication()).loadUrl = "http://deskoff.com/crypto/privacy_policy.html";
                startActivity(new Intent(getApplicationContext(),ShowInfo.class));
            }
        });
        contactus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Aplicacion) getApplication()).loadUrl = "https://goo.gl/forms/qzLcoqE5C3pp32bY2";
                startActivity(new Intent(getApplicationContext(),ShowInfo.class));
            }
        });


        versionTxt.setText("Ver: "+version);

       // textViewBTCVALUE.setText("BTC: $ "+((Aplicacion) getApplication()).btcValue);

        if(((Aplicacion) getApplication()).diskData.getBoolean("first",true)) {
            loadingFirst.setVisibility(View.VISIBLE);

        }
        startAllData();

        masternode = (RelativeLayout)findViewById(R.id.btnMN);
        trackNodes = (RelativeLayout) findViewById(R.id.trackBtn);
        calculator = (RelativeLayout)findViewById(R.id.button3);
        RelativeLayout allcoinfInfo = (RelativeLayout)findViewById(R.id.coinsBtn);


        masternode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MasterNodes.class));
            }
        });
        trackNodes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),TrackMasterNodes.class));
            }
        });
        allcoinfInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),AllCoins.class));
            }
        });
        calculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),FinancialResume.class));
            }
        });

        coinsArray = ((Aplicacion) getApplication()).getDB().sortGeneric();
        listCoins = (ListView)findViewById(R.id.favoritesCoinList);
        adapter = new CoinsAdapter(getApplicationContext());
        listCoins.setAdapter(adapter);


    }

    private void startAllData() {
        (new Thread() {
            public void run() {//llamar rewards cada vez que entra


                //obtener los datos del masternode
                String temp = httpCLient.HttpConnectGet("https://www.crypto-coinz.net/master-node-calculator/",null);
                htmlToJsonCryptoCompare(temp);
                temp = httpCLient.HttpConnectGet("https://masternodes.online/",null);
                htmlToJsonMastenodeOnline(temp);

                //obtener datos Crypto bridge
                String resp = httpCLient.HttpConnectGet("https://api.crypto-bridge.org/api/v1/ticker",null);//CryptoBridge
                ((Aplicacion) getApplication()).getValuesCryptoBridge(resp);

                //obtener datos Graviex
                resp = httpCLient.HttpConnectGet("https://graviex.net/api/v2/tickers",null);//Graviex
                ((Aplicacion) getApplication()).getValuesGraviex(resp);

                //obtener datos southXchange
                resp = httpCLient.HttpConnectGet("https://www.southxchange.com/api/prices",null);//southXchange
                ((Aplicacion) getApplication()).getValuesSouthXchange(resp);

                //obtener datos coinexchangeio
                resp = httpCLient.HttpConnectGet("https://www.coinexchange.io/api/v1/getmarkets",null);//coinexchangeio
                String resp2 = httpCLient.HttpConnectGet("https://www.coinexchange.io/api/v1/getmarketsummaries",null);
                ((Aplicacion) getApplication()).getValuesCoinexchangeio(resp,resp2);

                //obtener datos tradeSatoshi
                resp = httpCLient.HttpConnectGet("https://tradesatoshi.com/api/public/getmarketsummaries",null);//southXchange
                ((Aplicacion) getApplication()).getValuesTradeSatoshi(resp);

                //obtener datos StockExchange
                resp = httpCLient.HttpConnectGet("https://stocks.exchange/api2/ticker",null);//StockExchange
                ((Aplicacion) getApplication()).getValuesStockExchange(resp);

                //https://cryptohub.online/api/market/ticker/
                resp = httpCLient.HttpConnectGet("https://cryptohub.online/api/market/ticker/",null);//cryptohub.online
                ((Aplicacion) getApplication()).getValuescryptohub(resp);

                //cryptopia

                //coinmarketCap
                String resHrl = httpCLient.HttpConnectGet("https://api.coinmarketcap.com/v1/ticker/",null);
                ((Aplicacion) getApplication()).coinMarketCapAllcoins(resHrl);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingFirst.setVisibility(View.GONE);
                        ((Aplicacion) getApplication()).diskEditor.putBoolean("first",false).commit();
                        updating.setText(R.string.updated);
                        updating.setBackgroundColor(Color.GREEN);
                       // textViewBTCVALUE.setText("BTC: $ "+((Aplicacion) getApplication()).btcValue);
                        coinsArray = ((Aplicacion) getApplication()).getDB().sortGeneric();
                        adapter.notifyDataSetChanged();
                    }
                });

            }

        }).start();
    }




    public String htmlToJsonCryptoCompare(String HTML){
        JSONObject jsonObj = null;
        String myresult = "";

        Document document = Jsoup.parse(HTML);
        Element table = document.select("table").first();
        // String arrayName = table.select("th").first().text();
        jsonObj = new JSONObject();
        JSONArray jsonArr = new JSONArray();
        // Elements nfos = table.getElementsByTag("td");
        Elements ttls = table.getElementsByTag("tr");
        String key = "";
        String value = "";
        ArrayList<String> titulos = new ArrayList<>();
        titulos.add("logo");
        titulos.add("name");
        titulos.add("nodes");
        titulos.add("price");
        titulos.add("price_btc");
        titulos.add("basura1");
        titulos.add("roi");
        titulos.add("roi_years");
        titulos.add("coins_node");
        titulos.add("node_worth");
        titulos.add("coins_daily");
        titulos.add("daily_usd");
        titulos.add("shortname");



        for(int i=0;i<ttls.size();i++){

            ContentValues values = new ContentValues();


            Elements nfos = ttls.get(i).getElementsByTag("td");

            if(i==0){
                /*for (int j = 0; j < nfos.size(); j++) {
                    key = nfos.get(j).text();
                    titulos.add(key);
                }*/
            }else {

                for (int j = 0; j < nfos.size() && j<titulos.size()-1; j++) {
                    value = nfos.get(j).text();

                    if(j == 0){
                        value = nfos.get(j).toString();

                        int pos = value.indexOf("src=\"");
                        int posend = value.indexOf("png");
                        String temp = value.substring(pos+8,posend+3);
                        values.put("logo","https://www.crypto-coinz.net/"+temp);
                    }
                    else if(j == 1){//name
                        int pos = value.indexOf("(");
                        int posend = value.indexOf(")");
                        String temp = value.substring(pos+1,posend);
                        String nameD = value.substring(0,pos-1);
                        values.put("shortname",temp);
                        values.put("rank",999999);
                        values.put(titulos.get(j),nameD);

                    }
                    else if(j== 2){//nodes
                        value = nfos.get(j).text();
                        int vvd = Integer.parseInt(value);
                        values.put(titulos.get(j),vvd);
                    }
                    else if(j== 3){//precio price=$ 0.5628
                        value = value.replace("$ ","");
                        double vvd = Double.parseDouble(value);
                        values.put(titulos.get(j),vvd);
                    }
                    else if(j== 4){//precio btc
                        if(value.indexOf("No data") == -1) {
                            //int pos = value.indexOf("(");
                           // String temp = value.substring(0, pos - 1);
                            //temp = temp.replace(".",",");
                            double vvd = 0;
                            values.put(titulos.get(j), vvd);
                        }

                    }
                    else if(j== 6){//roi day

                        value = nfos.get(j).text();
                        int vvd = Integer.parseInt(value);
                        values.put(titulos.get(j),vvd);

                    }
                    else if(j== 7){//roi year
                        value = value.replace(" %","");
                        value = value.replace(",",".");
                        value = value.replace(" ","");
                        double vvd = Double.parseDouble(value);
                        values.put(titulos.get(j),vvd);


                    }
                    else if(j == 8){//number coins
                        String[] splitN = value.split(" ");
                        int vvd = Integer.parseInt(splitN[0]);
                        values.put(titulos.get(j),vvd);

                    }
                    else if(j == 9){//Node Worth
                        value = value.replace("$ ","");
                        value = value.replace(",",".");
                        value = value.replace(" ","");
                        double vvd = Double.parseDouble(value);
                        values.put(titulos.get(j),vvd);
                    }
                    else if(j == 10){//Coins Daily
                        value = value.replace(",",".");
                        double vvd = Double.parseDouble(value);
                        values.put(titulos.get(j),vvd);

                    }
                    else if(j == 11){//Coins Daily
                        value = value.replace("$ ","");
                        value = value.replace(",",".");
                        String[] splitN = value.split(" ");
                        double vvd = Double.parseDouble(splitN[0]);
                        values.put(titulos.get(j),vvd);
                    }
                    values.put("has_node",1);
                }
                if(value.indexOf("No data") == -1) {
                    ((Aplicacion) getApplication()).getDB().procesInformation(values);
                }

            }



        }


        return myresult;
    }
    public String htmlToJsonMastenodeOnline(String HTML){
        JSONObject jsonObj = null;
        String myresult = "";

        Document document = Jsoup.parse(HTML);
        Element table = document.getElementById("masternodes_table");// select("table").first();
       if(table != null) {
           jsonObj = new JSONObject();
           JSONArray jsonArr = new JSONArray();
           // Elements nfos = table.getElementsByTag("td");
           Elements ttls = table.getElementsByTag("tr");
           String key = "";
           String value = "";

           for (int i = 0; i < ttls.size(); i++) {

               ContentValues values = new ContentValues();


               Elements nfos = ttls.get(i).getElementsByTag("td");

               if (i == 0) {
                /*for (int j = 0; j < nfos.size(); j++) {
                    key = nfos.get(j).text();
                    titulos.add(key);
                }*/
               } else {

                   for (int j = 1; j < nfos.size(); j++) {
                       value = nfos.get(j).text();


                       if (j == 1) {//imagen moneda
                           value = nfos.get(j).toString();

                           int pos = value.indexOf("img src=\"");
                           int posend = value.indexOf("png");
                           //if(posend > 0) {
                               //String temp = value.substring(pos + 9, posend + 3);
                             //  values.put("logo", "https://masternodes.online/" + temp);
                          // }
                           values.put("has_node", 1);
                       } else if (j == 2) { //name
                           int pos = value.indexOf("(");
                           int posend = value.indexOf(")");
                           String temp = value.substring(pos + 1, posend);
                           String nameD = value.substring(0, pos - 1);
                           values.put("shortname", temp);
                           if(temp.equals("SLC"))
                               Log.e("aqui","");
                           values.put("rank", 999999);
                           values.put("name", nameD);
                           values.put("logo", "https://masternodes.online/coin_image/" + temp+".png");

                       } else if (j == 3) {
                           value = value.replace("$", "");
                           value = value.replace(",", ".");
                           double vvd = Double.parseDouble(value);
                           values.put("price", vvd);
                       } else if (j == 7) {
                           value = value.replace("%", "");
                           value = value.replace(",", "");
                           //value = value.replace(".", ",");
                           value = value.replace(" ", "");
                           double vvd = 0;
                           try {
                               vvd = Double.parseDouble(value);
                           }catch (Exception ex){
                                Log.e("errorNum",ex.toString());
                           }
                           values.put("roi_years", vvd);


                       } else if (j == 8) {
                           value = value.replace(",", "");
                           int vvd = Integer.parseInt(value);
                           values.put("nodes", vvd);
                       } else if (j == 9) {
                           value = value.replace(",", "");
                           value = value.replace(" ", "");
                           int vvd = Integer.parseInt(value);
                           values.put("coins_node", vvd);
                       } else if (j == 10) {
                           value = value.replace("$", "");
                           value = value.replace(",", "");
                           value = value.replace(" ", "");
                           double vvd = Double.parseDouble(value);
                           values.put("node_worth", vvd);

                           double porcent = values.getAsDouble("roi_years") / 100;
                           double coinsN = values.getAsDouble("coins_node");
                           double coinday = (porcent * coinsN) / 360;
                           values.put("coins_daily", coinday);

                           double price = values.getAsDouble("price");
                           double dayUsd = price * coinday;
                           values.put("daily_usd", dayUsd);

                           double royday = coinsN / coinday;
                           int tt = (int) royday;
                           if (values.getAsString("shortname").equals("MOUSE"))
                               Log.e("error", tt + "");
                           values.put("roi", tt);


                       }

                   }
                   ((Aplicacion) getApplication()).getDB().procesInformation(values);

               }


           }
       }


        return myresult;
    }

    public class CoinsAdapter extends BaseAdapter {

        private Context MyContext;
        private int page = 2;


        public CoinsAdapter(Context context){
            MyContext = context;

        }

        private void addBTC(){
            Coin tcoiun = ((Aplicacion) getApplication()).getDB().getCoinByName("BTC");
            if(tcoiun != null)
                coinsArray.add(tcoiun );
        }


        @Override
        public int getCount() {

            //pre add Bitcoin if not added
            if(coinsArray.size() == 0){
                addBTC();
            }else if(!coinsArray.get(0).shortname.equals("BTC")){
                addBTC();
            }

            return coinsArray.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View grid;

            if (convertView == null) {
                grid = new View(MyContext);
                LayoutInflater inflater = (LayoutInflater)MyContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                grid = inflater.inflate(R.layout.row_coins_man_fav, parent, false);

            } else {
                grid = (View) convertView;
            }

            TextView name = (TextView)grid.findViewById(R.id.textView91);
            ImageView logo = (ImageView)grid.findViewById(R.id.imageView11);

            name.setText("$ "+coinsArray.get(position).price+" USD");
            Glide.with(getApplicationContext())
                    .load(coinsArray.get(position).logo)
                    .into(logo);


            return grid;
        }
    }

    // ******** background task ******

    private class RedeemTask extends AsyncTask<Void, Void, Void> {

        private String respuestas;

        @Override
        protected void onPreExecute() {

        }

        protected Void doInBackground(Void... bounds) {


            respuestas = httpCLient.HttpConnectGet("http://190.26.134.117/cryptocoins/explorer.json",null);//190.26.134.117/cryptocoins/explorer.json

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if(respuestas != null) {
                try {
                    ((Aplicacion) getApplication()).apiExplorer = new JSONObject(respuestas);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }



        }


    }


}
