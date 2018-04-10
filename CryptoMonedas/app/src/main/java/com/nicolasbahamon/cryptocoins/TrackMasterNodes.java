package com.nicolasbahamon.cryptocoins;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.Manifest;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.nicolasbahamon.cryptocoins.models.Coin;
import com.nicolasbahamon.cryptocoins.models.Exchange;
import com.nicolasbahamon.cryptocoins.models.TrackingN;
import com.nicolasbahamon.cryptocoins.network.HttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;


public class TrackMasterNodes extends Activity {

    private ArrayList<TrackingN> arrayTracking;
    private ListView listtrack;
    private TrackAdapter adapter;
    private HttpClient httpCLient;
    private ProgressBar loading1;
    private TextView infoAlert;
    private RelativeLayout showAelrt;
    private Button acept,cancel;
    private int opcSelected = 0;
    private Coin monedaSelected;
    private int positionSelected=0;

    private int globalPositionSelected = 0;

    TrackMasterNodes actv;

    TrackMasterNodes valor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_master_nodes);

        //adds
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        actv = this;

        httpCLient = new HttpClient(getApplicationContext(), ((Aplicacion) getApplication()));
        loading1 = (ProgressBar)findViewById(R.id.progressBar5);
        arrayTracking = ((Aplicacion)getApplication()).getDB().getAllTracking();
        listtrack =(ListView)findViewById(R.id.listTracking);
        showAelrt = (RelativeLayout)findViewById(R.id.alertInfo);
        infoAlert = (TextView)findViewById(R.id.textView73);
        acept = (Button)findViewById(R.id.button13);
        cancel = (Button)findViewById(R.id.button14);

        adapter = new TrackAdapter(getApplicationContext());


        listtrack.setAdapter(adapter);

        valor = this;

        new RedeemTask().execute();

        acept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//addmore
                if(opcSelected == 1){
                    ContentValues valuesT = new ContentValues();
                    valuesT.put("id_coin",monedaSelected.idCoin);
                    valuesT.put("name_coin",monedaSelected.name);
                    valuesT.put("shortname",monedaSelected.shortname);
                    valuesT.put("coin_value",monedaSelected.price);
                    valuesT.put("last_amount",0);
                    valuesT.put("node_cant_coins",monedaSelected.coins_node);
                    ((Aplicacion)getApplication()).getDB().insertTracking(valuesT);
                    arrayTracking = ((Aplicacion)getApplication()).getDB().getAllTracking();
                    adapter.notifyDataSetChanged();
                }else if(opcSelected == 2){//delet

                    ((Aplicacion)getApplication()).getDB().deleteTrack(arrayTracking.get(positionSelected).id);
                    arrayTracking.remove(positionSelected);
                    adapter.notifyDataSetChanged();
                }
                showAelrt.setVisibility(View.GONE);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAelrt.setVisibility(View.GONE);
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        arrayTracking = ((Aplicacion)getApplication()).getDB().getAllTracking();
        adapter.notifyDataSetChanged();
    }





    // ******** background task ******

    private class RedeemTask extends AsyncTask<Void, Void, Void> {

        private String respuestas;

        @Override
        protected void onPreExecute() {
            loading1.setVisibility(View.VISIBLE);

        }

        protected Void doInBackground(Void... bounds) {


            respuestas = httpCLient.HttpConnectGet("http://190.26.134.117/cryptocoins/explorer.json",null);//190.26.134.117/cryptocoins/explorer.json

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            loading1.setVisibility(View.GONE);
            if(respuestas != null) {
                try {
                    JSONObject expData = new JSONObject(respuestas);
                    for (int i = 0; i < arrayTracking.size(); i++) {
                        try {
                            JSONObject cointemp = expData.getJSONObject(arrayTracking.get(i).shortname);
                            arrayTracking.get(i).explorer = cointemp.getString("explor");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }



        }


    }

    private class BalanceData extends AsyncTask<Void, Void, Void> {

        private String respuestas;
        private int listPosition;
        private ProgressBar loadinCoin;

        public BalanceData(int position, ProgressBar _loadinCoin) {
            listPosition = position;
            loadinCoin = _loadinCoin;
        }

        @Override
        protected void onPreExecute() {

            loadinCoin.setVisibility(View.VISIBLE);
        }

        protected Void doInBackground(Void... bounds) {

            String UrlExpr =arrayTracking.get(listPosition).explorer+arrayTracking.get(listPosition).address;
            respuestas = httpCLient.HttpConnectGet(UrlExpr,null);
            Log.e("balance",respuestas);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if(respuestas != null) {
                try{
                    double balnce = Double.parseDouble(respuestas);
                    double lastBalance = arrayTracking.get(listPosition).balance;
                    arrayTracking.get(listPosition).balance = balnce;

                    ContentValues values = new ContentValues();

                    values.put("current_amount",balnce);
                    values.put("last_amount",lastBalance);
                    values.put("id",arrayTracking.get(listPosition).id);
                    ((Aplicacion)getApplication()).getDB().updateTrackingInfo(values);

                    adapter.notifyDataSetChanged();
                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(),"Error: "+ex.toString(),Toast.LENGTH_LONG).show();
                }
            }
            loadinCoin.setVisibility(View.GONE);


        }


    }

//---------------------------------- Adapter --------------------------------

    public class TrackAdapter extends BaseAdapter {

        private Context MyContext;
        private int page = 2;


        public TrackAdapter(Context context){
            MyContext = context;

        }


        @Override
        public int getCount() {
            return arrayTracking.size();
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

                grid = inflater.inflate(R.layout.row_tracking, parent, false);

            } else {
                grid = (View) convertView;
            }

            TextView name = (TextView)grid.findViewById(R.id.textView45);
            ImageView logoImg = (ImageView)grid.findViewById(R.id.imageView3);
            TextView shortNameT = (TextView)grid.findViewById(R.id.textView46);
            TextView coinValueText = (TextView)grid.findViewById(R.id.textView47);
            TextView coinValueTextBTC = (TextView)grid.findViewById(R.id.textView47_2);
            TextView balanceText = (TextView)grid.findViewById(R.id.textView69);
            TextView balanceUsd = (TextView)grid.findViewById(R.id.textView50);
            TextView lastValue = (TextView)grid.findViewById(R.id.textView74);
            RelativeLayout notice = (RelativeLayout)grid.findViewById(R.id.setupInstr);
            TextView mNText = (TextView)grid.findViewById(R.id.mncoinsv);
            TextView mnValue = (TextView)grid.findViewById(R.id.mnprice);
            TextView mNText2 = (TextView)grid.findViewById(R.id.mncoinsv_2);
            TextView mnValue2 = (TextView)grid.findViewById(R.id.mnprice_2);
            TextView mncost = (TextView)grid.findViewById(R.id.mncost);
            final ProgressBar loadinCoin = (ProgressBar)grid.findViewById(R.id.progressBar6);
            Button settings = (Button)grid.findViewById(R.id.button12);
            TextView overview = (TextView)grid.findViewById(R.id.mncost_2);
            TextView recovered = (TextView)grid.findViewById(R.id.textView52);
            Button addMore = (Button)grid.findViewById(R.id.button8);
            Button delete = (Button)grid.findViewById(R.id.button9);

            /*final EditText explorerUrl = (EditText)grid.findViewById(R.id.editText3);
            Button getQr = (Button)grid.findViewById(R.id.button6);
            final EditText addressWallet = (EditText)grid.findViewById(R.id.editText2);




            TextView coinbtcText = (TextView)grid.findViewById(R.id.textView48);



            final EditText newname = (EditText)grid.findViewById(R.id.editText6);


            */

            final Coin moneda =((Aplicacion)getApplication()).getDB().getCoinByName(arrayTracking.get(position).shortname);
            name.setText(arrayTracking.get(position).name);
            shortNameT.setText(arrayTracking.get(position).shortname);
            Glide.with(getApplicationContext())
                    .load(moneda.logo)
                    .into(logoImg);

            coinValueText.setText(((Aplicacion)getApplication()).numberFormat(moneda.price));
           //coinValueTextBTC.setText((moneda.price/((Aplicacion)getApplication()).btcValue)+"");
            balanceText.setText(((Aplicacion)getApplication()).numberFormat(arrayTracking.get(position).balance));
            balanceUsd.setText(((Aplicacion)getApplication()).numberFormat(arrayTracking.get(position).balance*moneda.price));

            double dif = arrayTracking.get(position).balance-arrayTracking.get(position).lastBalance;
            if(dif >= 0){
                lastValue.setText(" + "+((Aplicacion)getApplication()).numberFormat(dif));
                lastValue.setTextColor(Color.GREEN);
            }else{
                lastValue.setText(" "+((Aplicacion)getApplication()).numberFormat(dif));
                lastValue.setTextColor(Color.RED);
            }

            if(arrayTracking.get(position).address != null && !arrayTracking.get(position).address.equals("")) {
               if(arrayTracking.get(position).explorer != null && !arrayTracking.get(position).explorer.equals("")){
                   notice.setVisibility(View.GONE);
                   if(arrayTracking.get(position).firstTime == 0) {
                       arrayTracking.get(position).firstTime = 1;
                       new BalanceData(position, loadinCoin).execute();
                   }
                }
            }
            else
                notice.setVisibility(View.VISIBLE);

            mNText.setText(((Aplicacion)getApplication()).numberFormat(moneda.coins_node));
            mnValue.setText(((Aplicacion)getApplication()).numberFormat(moneda.coins_node*moneda.price));
            double difVal = arrayTracking.get(position).balance-moneda.coins_node;
            mNText2.setText(((Aplicacion)getApplication()).numberFormat(difVal));
            mnValue2.setText(((Aplicacion)getApplication()).numberFormat(difVal*moneda.price));

            mncost.setText(((Aplicacion)getApplication()).numberFormat(arrayTracking.get(position).mncost));


            settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((Aplicacion)getApplication()).coinEdit = arrayTracking.get(position);
                    startActivity(new Intent(getApplicationContext(),SettingsCoin.class));
                }
            });

            double difInver = (arrayTracking.get(position).balance*moneda.price)-arrayTracking.get(position).mncost;
            if(difInver > 0){
                overview.setTextColor(Color.GREEN);
            }else{
                overview.setTextColor(Color.RED);
            }

            overview.setText(((Aplicacion)getApplication()).numberFormat(difInver));

            if(arrayTracking.get(position).mncost>0) {
                double porcent = ((difVal * moneda.price) * 100) / arrayTracking.get(position).mncost;
                if(difVal > 0){
                    //recovered.setTextColor(Color.GREEN);
                }else{
                    //recovered.setTextColor(Color.RED);
                }
                recovered.setText( ((Aplicacion)getApplication()).numberFormat(porcent) + "%");
            }

            addMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    monedaSelected = moneda;
                    opcSelected = 1;
                    infoAlert.setText(R.string.addinfo);
                    showAelrt.setVisibility(View.VISIBLE);
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      positionSelected = position;
                      opcSelected = 2;
                      infoAlert.setText(R.string.deleteInfo);
                      showAelrt.setVisibility(View.VISIBLE);
                  }
             });

       /*
            coinbtcText.setText("BTC: "+moneda.price_btc);
            mNText.setText("MN: "+moneda.coins_node);





            if(arrayTracking.get(position).extra_name !=null && !arrayTracking.get(position).extra_name.equals("null")) {
                newname.setText(arrayTracking.get(position).extra_name);
                name.setText(name.getText().toString()+" "+arrayTracking.get(position).extra_name);
            }else{
                newname.setText("");
                name.setText(name.getText().toString());
            }

/*
            explorerUrl.setText(arrayTracking.get(position).explorer);
            if(arrayTracking.get(position).address != null && !arrayTracking.get(position).address.equals("")) {
                addressWallet.setText(arrayTracking.get(position).address);
                if(arrayTracking.get(position).explorer != null && !arrayTracking.get(position).explorer.equals("")){
                    if(arrayTracking.get(position).firstTime == 0) {
                        arrayTracking.get(position).firstTime = 1;
                        new BalanceData(position, loadinCoin).execute();
                    }
                }
            }
            else
                addressWallet.setText("");
            getQr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    globalPositionSelected = position;
                    QrScanner(view);
                }
            });

            if(arrayTracking.get(position).balance != 0){
                balanceText.setText("Coins: "+((Aplicacion)getApplication()).numberFormat(arrayTracking.get(position).balance));



            }else{
                balanceText.setText("Coins: --");
                balanceUsd.setText("Earn: $ ---");
                lastValue.setText(" 0 ");
            }

            saveData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    monedaSelected = coin;

                }
            });

            });*/



            return grid;
        }
    }
}
