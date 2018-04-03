package com.nicolasbahamon.cryptocoins;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nicolasbahamon.cryptocoins.models.Coin;
import com.nicolasbahamon.cryptocoins.models.Exchange;
import com.nicolasbahamon.cryptocoins.network.HttpClient;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class MasterNodes extends Activity {

    private HttpClient httpCLient;
    private ListView listaMonedas, listaFavoritos;
    private ArrayList<Coin> coins, favCoins;
    private CoinsAdapter adapter;
    private CoinsFavAdapter adapterFav;
    private ProgressBar loading;

    private TextView nameC,value,coinMN,nodes, roiD,roiY, mnW, coinD, dayUSD;
    private boolean flag1,flag2,flag3,flag4,flag5,flag6,flag7,flag8,flag9;

    private boolean favSelected = false;

    private RelativeLayout allData,favData;

   // private CheckBox favorite, trackMN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_nodes);

        httpCLient = new HttpClient(getApplicationContext(), ((Aplicacion) getApplication()));
        listaMonedas = (ListView) findViewById(R.id.listView);
        listaFavoritos = (ListView)findViewById(R.id.listViewFavorite);
        loading = (ProgressBar) findViewById(R.id.progressBar);

        nameC = (TextView) findViewById(R.id.textView2);
        value = (TextView) findViewById(R.id.textView3);
        coinMN = (TextView) findViewById(R.id.textView5);
        nodes = (TextView) findViewById(R.id.textView4);
        roiD = (TextView) findViewById(R.id.textView6);
        roiY = (TextView) findViewById(R.id.textView9);
        mnW = (TextView) findViewById(R.id.textView10);
        coinD = (TextView) findViewById(R.id.textView7);
        dayUSD = (TextView) findViewById(R.id.textView11);

        allData = (RelativeLayout)findViewById(R.id.allLayout);
        favData = (RelativeLayout)findViewById(R.id.favLayout);

        favCoins = ((Aplicacion) getApplication()).getDB().sortGenericMn("roi_years", 0, true);
        adapterFav = new CoinsFavAdapter(getApplicationContext());

        allData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favSelected = false;
                listaFavoritos.setVisibility(View.GONE);
                listaMonedas.setVisibility(View.VISIBLE);
            }
        });
        favData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favSelected = true;
                favCoins = ((Aplicacion) getApplication()).getDB().sortGenericMn("roi_years", 0, true);
                listaFavoritos.setVisibility(View.VISIBLE);
                listaMonedas.setVisibility(View.GONE);
                adapterFav = new CoinsFavAdapter(getApplicationContext());
                listaFavoritos.setAdapter(adapterFav);


            }
        });


        nameC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!flag1) {
                    coins = ((Aplicacion) getApplication()).getDB().sortGenericMn("name", 0,false);
                    favCoins = ((Aplicacion) getApplication()).getDB().sortGenericMn("name", 0, true);
                    adapter.notifyDataSetChanged();
                    adapterFav.notifyDataSetChanged();
                    flag1 = true;
                } else {
                    coins = ((Aplicacion) getApplication()).getDB().sortGenericMn("name", 1,false);
                    favCoins = ((Aplicacion) getApplication()).getDB().sortGenericMn("name", 1, true);
                    adapter.notifyDataSetChanged();
                    adapterFav.notifyDataSetChanged();
                    flag1 = false;
                }

            }
        });
        value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!flag2) {
                    coins = ((Aplicacion) getApplication()).getDB().sortGenericMn("price", 0,false);
                    favCoins = ((Aplicacion) getApplication()).getDB().sortGenericMn("price", 0, true);
                    adapter.notifyDataSetChanged();
                    adapterFav.notifyDataSetChanged();
                    flag2 = true;
                } else {
                    coins = ((Aplicacion) getApplication()).getDB().sortGenericMn("price", 1,false);
                    favCoins = ((Aplicacion) getApplication()).getDB().sortGenericMn("price", 1, true);
                    adapter.notifyDataSetChanged();
                    adapterFav.notifyDataSetChanged();
                    flag2 = false;
                }

            }
        });
        roiD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!flag5) {
                    coins = ((Aplicacion) getApplication()).getDB().sortGenericMn("roi", 0,false);
                    favCoins = ((Aplicacion) getApplication()).getDB().sortGenericMn("roi", 0, true);
                    adapter.notifyDataSetChanged();
                    adapterFav.notifyDataSetChanged();
                    flag5 = true;
                } else {
                    coins = ((Aplicacion) getApplication()).getDB().sortGenericMn("roi", 1,false);
                    favCoins = ((Aplicacion) getApplication()).getDB().sortGenericMn("roi", 1, true);
                    adapter.notifyDataSetChanged();
                    adapterFav.notifyDataSetChanged();
                    flag5 = false;
                }

            }
        });
        roiY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!flag6) {
                    coins = ((Aplicacion) getApplication()).getDB().sortGenericMn("roi_years", 0,false);
                    favCoins = ((Aplicacion) getApplication()).getDB().sortGenericMn("roi_years", 0, true);
                    adapter.notifyDataSetChanged();
                    adapterFav.notifyDataSetChanged();
                    flag6 = true;
                } else {
                    coins = ((Aplicacion) getApplication()).getDB().sortGenericMn("roi_years", 1,false);
                    favCoins = ((Aplicacion) getApplication()).getDB().sortGenericMn("roi_years", 1, true);
                    adapter.notifyDataSetChanged();
                    adapterFav.notifyDataSetChanged();
                    flag6 = false;
                }

            }
        });


        coins = ((Aplicacion) getApplication()).getDB().getAllCoinsMN();
        if (coins.size() > 0) {
            adapter = new CoinsAdapter(getApplicationContext());
            listaMonedas.setAdapter(adapter);
        }

        loading.setVisibility(View.GONE);
/*
             (new Thread() {
               public void run() {//llamar rewards cada vez que entra

                    loading.setVisibility(View.VISIBLE);
                    String temp = httpCLient.HttpConnectGet("https://www.crypto-coinz.net/master-node-calculator/",null);
                    temp = htmlToJsonCryptoCompare(temp);

                   temp = httpCLient.HttpConnectGet("https://masternodes.online/",null);
                    temp = htmlToJsonMastenodeOnline(temp);

                    coins = ((Aplicacion)getApplication()).getDB().getAllCoins();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new CoinsAdapter(getApplicationContext());
                            listaMonedas.setAdapter(adapter);
                            loading.setVisibility(View.INVISIBLE);
                        }
                    });
        //  String temp = httpCLient.HttpConnectGet("https://wallet.crypto-bridge.org/market/BRIDGE.OLMP_BRIDGE.BTC",null);
        //  temp = htmlToJsonCryptoBridge(temp);

            }

         }).start();*/


    /*    listaMonedas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ((Aplicacion)getApplication()).coinShow = coins.get(i);
                startActivity(new Intent(getApplicationContext(),CoinInfo.class));
            }
        });*/

    }


    private String htmlToJsonCryptoBridge(String HTML) {

        Document document = Jsoup.parse(HTML);
        Elements table = document.getElementsByClass("small-12 medium-6 middle-content order-2");// select("table"). first();
        // Elements ttls = table.getElementsByTag("tr");
        //  for(int i=0;i<ttls.size();i++){
        //      Elements nfos = ttls.get(i).getElementsByTag("td");
        Log.e("stop","sime");//small-12 medium-6 middle-content order-2
        // }

        return null;
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
                        int pos = value.indexOf("(");
                        String temp = value.substring(0,pos-1);
                        //temp = temp.replace(".",",");
                        double vvd = Double.parseDouble(temp);
                        values.put(titulos.get(j),vvd);

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
                }
                ((Aplicacion)getApplication()).getDB().procesInformation(values);

            }



        }


        return myresult;
    }
    public String htmlToJsonMastenodeOnline(String HTML){
        JSONObject jsonObj = null;
        String myresult = "";

        Document document = Jsoup.parse(HTML);
        Element table = document.getElementById("masternodes_table");// select("table").first();
        // String arrayName = table.select("th").first().text();
        jsonObj = new JSONObject();
        JSONArray jsonArr = new JSONArray();
        // Elements nfos = table.getElementsByTag("td");
        Elements ttls = table.getElementsByTag("tr");
        String key = "";
        String value = "";

        for(int i=0;i<ttls.size();i++){

            ContentValues values = new ContentValues();


            Elements nfos = ttls.get(i).getElementsByTag("td");

            if(i==0){
                /*for (int j = 0; j < nfos.size(); j++) {
                    key = nfos.get(j).text();
                    titulos.add(key);
                }*/
            }else {

                for (int j = 1; j < nfos.size(); j++) {
                    value = nfos.get(j).text();


                    if( j == 1){//imagen moneda
                        value = nfos.get(j).toString();

                        int pos = value.indexOf("img src=\"");
                        int posend = value.indexOf("png");
                        String temp = value.substring(pos+9,posend+3);
                        values.put("logo","https://masternodes.online/"+temp);
                    }
                    else if(j == 2){ //name
                        int pos = value.indexOf("(");
                        int posend = value.indexOf(")");
                        String temp = value.substring(pos+1,posend);
                        String nameD = value.substring(0,pos-1);
                        values.put("shortname",temp);

                        values.put("name",nameD);

                    }
                    else if(j == 3){
                        value = value.replace("$","");
                        value = value.replace(",",".");
                        double vvd = Double.parseDouble(value);
                        values.put("price",vvd);
                    }
                    else if(j == 7){
                        value = value.replace("%","");
                        value = value.replace(",",".");
                        value = value.replace(" ","");
                        double vvd = Double.parseDouble(value);
                        values.put("roi_years",vvd);


                    }
                    else if(j == 8){
                        value = value.replace(",","");
                        int vvd = Integer.parseInt(value);
                        values.put("nodes",vvd);
                    }
                    else if(j == 9){
                        value = value.replace(",","");
                        value = value.replace(" ","");
                        int vvd = Integer.parseInt(value);
                        values.put("coins_node",vvd);
                    }
                    else if (j == 10){
                        value = value.replace("$","");
                        value = value.replace(",","");
                        value = value.replace(" ","");
                        double vvd = Double.parseDouble(value);
                        values.put("node_worth",vvd);

                        double porcent = values.getAsDouble("roi_years")/100;
                        double coinsN = values.getAsDouble("coins_node");
                        double coinday = (porcent*coinsN)/360;
                        values.put("coins_daily",coinday);

                        double price = values.getAsDouble("price");
                        double dayUsd = price*coinday;
                        values.put("daily_usd",dayUsd);

                        double royday = coinsN/coinday;
                        int tt = (int)royday;
                        if(values.getAsString("shortname").equals("MOUSE"))
                            Log.e("error",tt+"");
                        values.put("roi",tt);


                    }

                }
                ((Aplicacion)getApplication()).getDB().procesInformation(values);

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


        @Override
        public int getCount() {
            return coins.size();
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

                LayoutInflater inflater = (LayoutInflater)MyContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                grid = inflater.inflate(R.layout.row_coins, parent, false);

            } else {
                grid = (View) convertView;
            }

            TextView name = (TextView)grid.findViewById(R.id.textView);
            TextView value = (TextView)grid.findViewById(R.id.textView12);
            TextView shortN = (TextView)grid.findViewById(R.id.textView13);

            TextView roi = (TextView)grid.findViewById(R.id.textView16);
            TextView roiy = (TextView)grid.findViewById(R.id.textView17);

            TextView nodes = (TextView)grid.findViewById(R.id.textView14);
            TextView coinMN = (TextView)grid.findViewById(R.id.textView15);

            TextView nmnUSD = (TextView)grid.findViewById(R.id.textView18);
            TextView coinDay = (TextView)grid.findViewById(R.id.textView19);

            TextView earday = (TextView)grid.findViewById(R.id.textView21);
            TextView aernMont = (TextView)grid.findViewById(R.id.textView20);

            TextView textExchanges = (TextView)grid.findViewById(R.id.textView25);

            ImageView logo = (ImageView)grid.findViewById(R.id.imageView);

            CheckBox favorite = (CheckBox)grid.findViewById(R.id.checkBox2);
            final CheckBox trackMN = (CheckBox)grid.findViewById(R.id.checkBox);

            RelativeLayout rowP= (RelativeLayout)grid.findViewById(R.id.rowPrincipalMN);


            name.setText(coins.get(position).name);
            value.setText("$ "+coins.get(position).price);
            shortN.setText("("+coins.get(position).shortname+")");

            nodes.setText(coins.get(position).nodes+"");
            coinMN.setText(((Aplicacion)getApplication()).numberFormatCOP(coins.get(position).coins_node));

            roiy.setText(coins.get(position).roi_years+" %");
            roi.setText(coins.get(position).roi+"");

            nmnUSD.setText("$ "+((Aplicacion)getApplication()).numberFormatCOP(coins.get(position).node_worth));
            coinDay.setText(((Aplicacion)getApplication()).numberFormat(coins.get(position).coins_daily));

            earday.setText("$ "+((Aplicacion)getApplication()).numberFormat(coins.get(position).daily_usd));
            aernMont.setText("$ "+((Aplicacion)getApplication()).numberFormat((coins.get(position).daily_usd*30)));

            Glide.with(getApplicationContext())
                    .load(coins.get(position).logo)
                    //.diskCacheStrategy(DiskCacheStrategy.ALL)
                    //.placeholder(R.mipmap.ic_launcher_round)
                    //.dontAnimate()
                    //.override(200, 200)
                    //.centerCrop()
                    .into(logo);


            //zona Exchanges
            ArrayList<Exchange> listaExchanges = ((Aplicacion)getApplication()).getDB().getListExchangesByCoinId(coins.get(position).shortname);
            String exhanges = "";
            for (Exchange nameEx: listaExchanges){

                exhanges += nameEx.name + " | ";

            }
            textExchanges.setText(exhanges);


            if(coins.get(position).favorite == 1){
                favorite.setChecked(true);
            }else{
                favorite.setChecked(false);
            }

            if(coins.get(position).track_node == 1){
                trackMN.setChecked(true);
            }else{
                trackMN.setChecked(false);
            }

            favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentValues values = new ContentValues();
                    values.put("shortname",coins.get(position).shortname);
                    if( coins.get(position).favorite == 0){
                        values.put("favorite",1);
                        coins.get(position).favorite = 1;
                        ((Aplicacion)getApplication()).getDB().updateCoinInfo(values);
                    }else{
                        values.put("favorite",0);
                        coins.get(position).favorite = 0;
                        ((Aplicacion)getApplication()).getDB().updateCoinInfo(values);
                    }
                }
            });
            trackMN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentValues values = new ContentValues();
                    values.put("shortname",coins.get(position).shortname);
                    if(coins.get(position).track_node == 0){
                        values.put("track_node",1);
                        coins.get(position).track_node = 1;
                        ((Aplicacion)getApplication()).getDB().updateCoinInfo(values);
                        ContentValues valuesT = new ContentValues();
                        valuesT.put("id_coin",coins.get(position).idCoin);
                        valuesT.put("name_coin",coins.get(position).name);
                        valuesT.put("shortname",coins.get(position).shortname);
                        valuesT.put("coin_value",coins.get(position).price);
                        valuesT.put("last_amount",0);
                        valuesT.put("node_cant_coins",coins.get(position).coins_node);
                        ((Aplicacion)getApplication()).getDB().insertTracking(valuesT);
                    }else{
                        /*values.put("track_node",0);
                        coins.get(position).track_node = 0;
                        ((Aplicacion)getApplication()).getDB().updateCoinInfo(values);*/
                        trackMN.setChecked(true);
                        Toast.makeText(getApplicationContext(), "You can remove it on track zone", Toast.LENGTH_LONG).show();
                    }
                }
            });

            rowP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((Aplicacion)getApplication()).coinShow = coins.get(position);
                    startActivity(new Intent(getApplicationContext(),CoinInfo.class));
                }
            });





            return grid;
        }
    }
    public class CoinsFavAdapter extends BaseAdapter {

        private Context MyContext;
        private int page = 2;


        public CoinsFavAdapter(Context context){
            MyContext = context;

        }


        @Override
        public int getCount() {
            return favCoins.size();
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

                grid = inflater.inflate(R.layout.row_coins, parent, false);

            } else {
                grid = (View) convertView;
            }

            TextView name = (TextView)grid.findViewById(R.id.textView);
            TextView value = (TextView)grid.findViewById(R.id.textView12);
            TextView shortN = (TextView)grid.findViewById(R.id.textView13);

            TextView roi = (TextView)grid.findViewById(R.id.textView16);
            TextView roiy = (TextView)grid.findViewById(R.id.textView17);

            TextView nodes = (TextView)grid.findViewById(R.id.textView14);
            TextView coinMN = (TextView)grid.findViewById(R.id.textView15);

            TextView nmnUSD = (TextView)grid.findViewById(R.id.textView18);
            TextView coinDay = (TextView)grid.findViewById(R.id.textView19);

            TextView earday = (TextView)grid.findViewById(R.id.textView21);
            TextView aernMont = (TextView)grid.findViewById(R.id.textView20);

            TextView textExchanges = (TextView)grid.findViewById(R.id.textView25);

            ImageView logo = (ImageView)grid.findViewById(R.id.imageView);

            CheckBox favorite = (CheckBox)grid.findViewById(R.id.checkBox2);
            CheckBox trackMN = (CheckBox)grid.findViewById(R.id.checkBox);

            RelativeLayout rowP= (RelativeLayout)grid.findViewById(R.id.rowPrincipalMN);


            name.setText(favCoins.get(position).name);
            value.setText("$ "+favCoins.get(position).price);
            shortN.setText("("+favCoins.get(position).shortname+")");

            nodes.setText(favCoins.get(position).nodes+"");
            coinMN.setText(((Aplicacion)getApplication()).numberFormatCOP(favCoins.get(position).coins_node));

            roiy.setText(favCoins.get(position).roi_years+" %");
            roi.setText(favCoins.get(position).roi+"");

            nmnUSD.setText("$ "+((Aplicacion)getApplication()).numberFormatCOP(favCoins.get(position).node_worth));
            coinDay.setText(((Aplicacion)getApplication()).numberFormat(favCoins.get(position).coins_daily));

            earday.setText("$ "+((Aplicacion)getApplication()).numberFormat(favCoins.get(position).daily_usd));
            aernMont.setText("$ "+((Aplicacion)getApplication()).numberFormat((favCoins.get(position).daily_usd*30)));

            Glide.with(getApplicationContext())
                    .load(favCoins.get(position).logo)
                    //.diskCacheStrategy(DiskCacheStrategy.ALL)
                    //.placeholder(R.mipmap.ic_launcher_round)
                    //.dontAnimate()
                    //.override(200, 200)
                    //.centerCrop()
                    .into(logo);


            //zona Exchanges
            ArrayList<Exchange> listaExchanges = ((Aplicacion)getApplication()).getDB().getListExchangesByCoinId(favCoins.get(position).shortname);
            String exhanges = "";
            for (Exchange nameEx: listaExchanges){

                exhanges += nameEx.name + " | ";

            }
            textExchanges.setText(exhanges);


            if(favCoins.get(position).favorite == 1){
                favorite.setChecked(true);
            }else{
                favorite.setChecked(false);
            }

            if(favCoins.get(position).track_node == 1){
                trackMN.setChecked(true);
            }else{
                trackMN.setChecked(false);
            }


            rowP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((Aplicacion)getApplication()).coinShow = favCoins.get(position);
                    startActivity(new Intent(getApplicationContext(),CoinInfo.class));
                }
            });






            return grid;
        }
    }
}
