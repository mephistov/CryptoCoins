package com.nicolasbahamon.cryptocoins;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nicolasbahamon.cryptocoins.models.Exchange;
import com.nicolasbahamon.cryptocoins.network.HttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class CoinInfo extends Activity {

    private ImageView logo;
    private TextView nameCoin, priceText, shortNameText, mncant, mnvalue;
    private RadioButton buyR,sellR;
    private GridView tableExchanges;
    private ExchangesAdapter adapter;
    private ArrayList<Exchange> arrayExchanges;
    private Button calculate;
    private EditText amountCal;
    private HttpClient httpCLient;
    private RelativeLayout loading;

    private double priceCoin,amount = 0;
    private boolean candownloadBook = false;

    //temporal
    JSONObject orgerJ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_info);

        httpCLient = new HttpClient(getApplicationContext(), ((Aplicacion) getApplication()));
        loading = (RelativeLayout)findViewById(R.id.loading);
        mncant = (TextView)findViewById(R.id.textView62);
        mnvalue = (TextView)findViewById(R.id.textView61);

        if(((Aplicacion) getApplication()).coinShow == null){
            finish();
        }else{

            arrayExchanges = ((Aplicacion) getApplication()).getDB().getListExchangesByCoinId(((Aplicacion) getApplication()).coinShow.shortname);
            FillData();

        }

    }

    private void FillData() {

        logo = (ImageView)findViewById(R.id.imageView2);
        nameCoin = (TextView)findViewById(R.id.textView26);
        priceText = (TextView)findViewById(R.id.textView27);
        shortNameText = (TextView)findViewById(R.id.textView30);
        buyR = (RadioButton)findViewById(R.id.radioButton);
        sellR = (RadioButton)findViewById(R.id.radioButton2);
        tableExchanges = (GridView)findViewById(R.id.results);
        calculate = (Button)findViewById(R.id.button5);
        amountCal = (EditText)findViewById(R.id.editText);

        adapter = new ExchangesAdapter(getApplicationContext());
        tableExchanges.setAdapter(adapter);

        Glide.with(getApplicationContext())
                .load(((Aplicacion) getApplication()).coinShow.logo)
                .into(logo);

        nameCoin.setText(((Aplicacion) getApplication()).coinShow.name);
        priceCoin = ((Aplicacion) getApplication()).coinShow.price;

        priceText.setText("$ "+((Aplicacion)getApplication()).numberFormat(priceCoin));
        shortNameText.setText("("+((Aplicacion) getApplication()).coinShow.shortname+")");

        buyR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buyR.setChecked(true);
                sellR.setChecked(false);
            }
        });
        sellR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buyR.setChecked(false);
                sellR.setChecked(true);
            }
        });

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO llamar servicios actualizar excahnges
                try {
                    candownloadBook = true;
                    amount = Double.parseDouble(amountCal.getText().toString());
                    adapter.notifyDataSetChanged();
                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(),"No valid number",Toast.LENGTH_LONG).show();
                }
            }
        });

        mncant.setText(((Aplicacion) getApplication()).coinShow.coins_node+"");
        mnvalue.setText("$"+((Aplicacion) getApplication()).numberFormat(((Aplicacion) getApplication()).coinShow.node_worth));

    }


    public class ExchangesAdapter extends BaseAdapter {

        private Context MyContext;
        private int page = 2;


        public ExchangesAdapter(Context context){
            MyContext = context;

        }


        @Override
        public int getCount() {
            return arrayExchanges.size();
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

                grid = inflater.inflate(R.layout.grid_exchange, parent, false);

            } else {
                grid = (View) convertView;
            }

            TextView name = (TextView)grid.findViewById(R.id.textView32);
            TextView buyG = (TextView)grid.findViewById(R.id.textView33);
            TextView sellG = (TextView)grid.findViewById(R.id.textView34);

            final TextView buy1 = (TextView)grid.findViewById(R.id.textView41);
            final TextView sell1 = (TextView)grid.findViewById(R.id.textView42);
            final TextView buy2 = (TextView)grid.findViewById(R.id.textView36);
            final TextView sell2 = (TextView)grid.findViewById(R.id.textView38);
            final TextView now = (TextView)grid.findViewById(R.id.textView39);
            final ProgressBar load = (ProgressBar)grid.findViewById(R.id.progressBar4);

            final TextView bT = (TextView)grid.findViewById(R.id.textView35);
            final TextView sT = (TextView)grid.findViewById(R.id.textView37);

            buyG.setText("Buy: "+arrayExchanges.get(position).ask);
            sellG.setText("Sell: "+arrayExchanges.get(position).bid);

            name.setText(arrayExchanges.get(position).name);

            if(candownloadBook) {
                if (arrayExchanges.get(position).allreadyDownload) {
                    //todo mostrar info
                    if (!amountCal.getText().toString().equals("")) {
                        amount = Double.parseDouble(amountCal.getText().toString());
                        if (arrayExchanges.get(position).name.equals("Graviex")) {
                            arrayExchanges.get(position).getBookSellExchangeGraviex(null, amount);
                        }
                        else if(arrayExchanges.get(position).name.equals("SouthXchange")){
                            arrayExchanges.get(position).getBookSellExchangeSouthXchange(null, amount);
                        }
                        else if(arrayExchanges.get(position).name.equals("CoinExchangeIO")){
                            arrayExchanges.get(position).getBookSellExchangeCoinExchangeIO(null, amount);
                        }
                        else if(arrayExchanges.get(position).name.equals("TradeSatoshi")){
                            arrayExchanges.get(position).getBookSellExchangeTradeSatoshi(null, amount);
                        }


                        buy1.setText("USD: $" + ((Aplicacion) getApplication()).numberFormat(arrayExchanges.get(position).buyprice * ((Aplicacion) getApplication()).btcValue));
                        buy2.setText("BTC: " + ((Aplicacion) getApplication()).numberFormat(arrayExchanges.get(position).buyprice));

                        if (arrayExchanges.get(position).buyprice < amount) {
                            bT.setText("BUY: (Inc)");
                        }
                        if (arrayExchanges.get(position).sellprice < amount) {
                            sT.setText("SELL: (Inc)");
                        }

                        sell1.setText("USD: $" + ((Aplicacion) getApplication()).numberFormat(arrayExchanges.get(position).sellprice * ((Aplicacion) getApplication()).btcValue));
                        sell2.setText("BTC: " + ((Aplicacion) getApplication()).numberFormat(arrayExchanges.get(position).sellprice));
                        if(arrayExchanges.get(position).buyprice > 0){
                            now.setVisibility(View.VISIBLE);
                        }else{
                            now.setVisibility(View.INVISIBLE);
                        }

                        if(arrayExchanges.get(position).name.equals("StockExchange")){

                            buy1.setText("USD: $" + ((Aplicacion) getApplication()).numberFormat(arrayExchanges.get(position).ask * ((Aplicacion) getApplication()).btcValue * amount));
                            buy2.setText("BTC: " + ((Aplicacion) getApplication()).numberFormat(arrayExchanges.get(position).ask * amount));

                            sell1.setText("USD: $" + ((Aplicacion) getApplication()).numberFormat(arrayExchanges.get(position).bid * ((Aplicacion) getApplication()).btcValue * amount));
                            sell2.setText("BTC: " + ((Aplicacion) getApplication()).numberFormat(arrayExchanges.get(position).bid * amount));

                            load.setVisibility(View.GONE);

                        }
                        else if(arrayExchanges.get(position).name.equals("CryptoBridge")){

                            buy1.setText("USD: $" + ((Aplicacion) getApplication()).numberFormat(arrayExchanges.get(position).ask * ((Aplicacion) getApplication()).btcValue * amount));
                            buy2.setText("BTC: " + ((Aplicacion) getApplication()).numberFormat(arrayExchanges.get(position).ask * amount));

                            sell1.setText("USD: $" + ((Aplicacion) getApplication()).numberFormat(arrayExchanges.get(position).bid * ((Aplicacion) getApplication()).btcValue * amount));
                            sell2.setText("BTC: " + ((Aplicacion) getApplication()).numberFormat(arrayExchanges.get(position).bid * amount));

                            load.setVisibility(View.GONE);

                        }

                    }
                } else {
                    //todo bajar y mostrar info
                    (new Thread() {
                        public void run() {//llamar rewards cada vez que entra

                            if(!amountCal.getText().toString().equals("")) {
                                try {
                                    long id = arrayExchanges.get(position).id_exchange;
                                    final String apiBuySell = ((Aplicacion) getApplication()).getDB().getExchangeApi(id);
                                    if (arrayExchanges.get(position).name.equals("Graviex")) {
                                        String urlApi = apiBuySell + ((Aplicacion) getApplication()).coinShow.shortname + "btc";
                                        String resp = httpCLient.HttpConnectGet(urlApi.toLowerCase(), null);
                                        if (!amountCal.getText().toString().equals("")) {
                                            amount = Double.parseDouble(amountCal.getText().toString());
                                            arrayExchanges.get(position).getBookSellExchangeGraviex(resp, amount);
                                        }
                                    } else if (arrayExchanges.get(position).name.equals("SouthXchange")) {
                                        String urlApi = apiBuySell + ((Aplicacion) getApplication()).coinShow.shortname + "/BTC";
                                        String resp = httpCLient.HttpConnectGet(urlApi.toLowerCase(), null);
                                        amount = Double.parseDouble(amountCal.getText().toString());
                                        arrayExchanges.get(position).getBookSellExchangeSouthXchange(resp, amount);
                                    } else if (arrayExchanges.get(position).name.equals("CoinExchangeIO")) {
                                        String urlApi = apiBuySell + arrayExchanges.get(position).coinexchangeio_id;
                                        String resp = httpCLient.HttpConnectGet(urlApi.toLowerCase(), null);
                                        amount = Double.parseDouble(amountCal.getText().toString());
                                        arrayExchanges.get(position).getBookSellExchangeCoinExchangeIO(resp, amount);
                                    } else if (arrayExchanges.get(position).name.equals("TradeSatoshi")) {
                                        String urlApi = apiBuySell + ((Aplicacion) getApplication()).coinShow.shortname + "_BTC";
                                        String resp = httpCLient.HttpConnectGet(urlApi.toLowerCase(), null);
                                        amount = Double.parseDouble(amountCal.getText().toString());
                                        arrayExchanges.get(position).getBookSellExchangeTradeSatoshi(resp, amount);
                                    }


                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            load.setVisibility(View.GONE);
                                            arrayExchanges.get(position).allreadyDownload = true;
                                            buy1.setText("USD: $" + ((Aplicacion) getApplication()).numberFormat(arrayExchanges.get(position).buyprice * ((Aplicacion) getApplication()).btcValue));
                                            buy2.setText("BTC: " + ((Aplicacion) getApplication()).numberFormat(arrayExchanges.get(position).buyprice));

                                            if (arrayExchanges.get(position).tofillamount < amount) {
                                                bT.setText("BUY: (Inc)");
                                            }
                                            if (arrayExchanges.get(position).tofillamountS < amount) {
                                                sT.setText("SELL: (Inc)");
                                            }
                                            sell1.setText("USD: $" + ((Aplicacion) getApplication()).numberFormat(arrayExchanges.get(position).sellprice * ((Aplicacion) getApplication()).btcValue));
                                            sell2.setText("BTC: " + ((Aplicacion) getApplication()).numberFormat(arrayExchanges.get(position).sellprice));
                                            if (arrayExchanges.get(position).buyprice > 0) {
                                                now.setVisibility(View.VISIBLE);
                                            } else {
                                                now.setVisibility(View.INVISIBLE);
                                            }

                                            if (arrayExchanges.get(position).name.equals("StockExchange")) {

                                                buy1.setText("USD: $" + ((Aplicacion) getApplication()).numberFormat(arrayExchanges.get(position).ask * ((Aplicacion) getApplication()).btcValue * amount));
                                                buy2.setText("BTC: " + ((Aplicacion) getApplication()).numberFormat(arrayExchanges.get(position).ask * amount));

                                                sell1.setText("USD: $" + ((Aplicacion) getApplication()).numberFormat(arrayExchanges.get(position).bid * ((Aplicacion) getApplication()).btcValue * amount));
                                                sell2.setText("BTC: " + ((Aplicacion) getApplication()).numberFormat(arrayExchanges.get(position).bid * amount));

                                                load.setVisibility(View.GONE);

                                            } else if (arrayExchanges.get(position).name.equals("CryptoBridge")) {

                                                buy1.setText("USD: $" + ((Aplicacion) getApplication()).numberFormat(arrayExchanges.get(position).ask * ((Aplicacion) getApplication()).btcValue * amount));
                                                buy2.setText("BTC: " + ((Aplicacion) getApplication()).numberFormat(arrayExchanges.get(position).ask * amount));

                                                sell1.setText("USD: $" + ((Aplicacion) getApplication()).numberFormat(arrayExchanges.get(position).bid * ((Aplicacion) getApplication()).btcValue * amount));
                                                sell2.setText("BTC: " + ((Aplicacion) getApplication()).numberFormat(arrayExchanges.get(position).bid * amount));

                                                load.setVisibility(View.GONE);

                                            }
                                        }
                                    });
                                }catch (Exception es){

                                }
                            }

                        }

                    }).start();

                }

            }




            return grid;
        }
    }

    public JSONObject htmlToJsonStockEchange(String HTML){
        JSONObject resp = new JSONObject();

        Document document = Jsoup.parse(HTML);
        Element table = document.getElementById("buy-orders");
        Elements ttls = table.getElementsByTag("tr");
        for(int i=0;i<ttls.size();i++){
            Elements nfos = ttls.get(i).getElementsByTag("td");
            String value = nfos.get(i).toString();
            Log.e("quecarajos",value);
        }

        return resp;
    }
}
