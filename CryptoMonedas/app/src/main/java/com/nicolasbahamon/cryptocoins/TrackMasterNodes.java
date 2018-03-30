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
        adapter = new TrackAdapter(getApplicationContext());

        listtrack.setAdapter(adapter);

        valor = this;

        new RedeemTask().execute();




    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            arrayTracking.get(globalPositionSelected).address = scanContent;
            adapter.notifyDataSetChanged();



        }
    }

    public void QrScanner(View view){
        IntentIntegrator integrator = new IntentIntegrator(actv);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }





    // ******** background task ******

    private class RedeemTask extends AsyncTask<Void, Void, Void> {

        private String respuestas;

        @Override
        protected void onPreExecute() {
            loading1.setVisibility(View.VISIBLE);

        }

        protected Void doInBackground(Void... bounds) {


            respuestas = httpCLient.HttpConnectGet("http://192.168.1.101/cryptocoins/explorer.json",null);//190.26.134.117/cryptocoins/explorer.json

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
                    loadinCoin.setVisibility(View.GONE);
                    arrayTracking.get(listPosition).balance = balnce;
                    adapter.notifyDataSetChanged();
                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(),"Error: "+ex.toString(),Toast.LENGTH_LONG);
                }
            }



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
            EditText explorerUrl = (EditText)grid.findViewById(R.id.editText3);
            Button getQr = (Button)grid.findViewById(R.id.button6);
            final EditText addressWallet = (EditText)grid.findViewById(R.id.editText2);
            final ProgressBar loadinCoin = (ProgressBar)grid.findViewById(R.id.progressBar6);
            TextView balanceText = (TextView)grid.findViewById(R.id.textView69);
            TextView balanceUsd = (TextView)grid.findViewById(R.id.textView51);
            TextView coinValueText = (TextView)grid.findViewById(R.id.textView47);
            TextView coinbtcText = (TextView)grid.findViewById(R.id.textView48);
            TextView mNText = (TextView)grid.findViewById(R.id.textView49);
            Button saveData = (Button)grid.findViewById(R.id.button7);
            Button addMore = (Button)grid.findViewById(R.id.button8);
            Button delete = (Button)grid.findViewById(R.id.button9);
            final EditText newname = (EditText)grid.findViewById(R.id.editText6);
            ImageView logoImg = (ImageView)grid.findViewById(R.id.imageView3);

            final Coin moneda =((Aplicacion)getApplication()).getDB().getCoinByName(arrayTracking.get(position).shortname);

            coinValueText.setText("$ "+moneda.price);
            coinbtcText.setText("BTC: "+moneda.price_btc);
            mNText.setText("MN: "+moneda.coins_node);

            Glide.with(getApplicationContext())
                    .load(moneda.logo)
                    .into(logoImg);

            name.setText(arrayTracking.get(position).name);
            if(arrayTracking.get(position).extra_name !=null && !arrayTracking.get(position).extra_name.equals("null")) {
                newname.setText(arrayTracking.get(position).extra_name);
                name.setText(name.getText().toString()+" "+arrayTracking.get(position).extra_name);
            }


            explorerUrl.setText(arrayTracking.get(position).explorer);
            if(arrayTracking.get(position).address != null ) {
                addressWallet.setText(arrayTracking.get(position).address);
                if(arrayTracking.get(position).explorer != null){
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
                balanceText.setText("Coins: "+arrayTracking.get(position).balance);

                balanceUsd.setText("Earn: $ "+((Aplicacion)getApplication()).numberFormat(arrayTracking.get(position).balance*moneda.price));
            }else{
                balanceText.setText("Coins: --");
                balanceUsd.setText("Earn: $ ---");
            }

            saveData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentValues values = new ContentValues();
                    values.put("explorer",arrayTracking.get(position).explorer);
                    values.put("wallet",addressWallet.getText().toString());
                    values.put("mn_cost",arrayTracking.get(position).mncost);
                    values.put("usd_cost",arrayTracking.get(position).usd_cost);
                    values.put("current_amount",arrayTracking.get(position).balance);
                    values.put("id",arrayTracking.get(position).id);
                    values.put("extra_name",newname.getText().toString());
                    int resp = ((Aplicacion)getApplication()).getDB().updateTrackingInfo(values);
                    if(resp < 0){
                        Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"Save OK",Toast.LENGTH_SHORT).show();
                    }

                }
            });
            addMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ContentValues valuesT = new ContentValues();
                    valuesT.put("id_coin",moneda.idCoin);
                    valuesT.put("name_coin",moneda.name);
                    valuesT.put("shortname",moneda.shortname);
                    valuesT.put("coin_value",moneda.price);
                    valuesT.put("node_cant_coins",moneda.coins_node);
                    ((Aplicacion)getApplication()).getDB().insertTracking(valuesT);
                    arrayTracking = ((Aplicacion)getApplication()).getDB().getAllTracking();
                    adapter.notifyDataSetChanged();
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentValues values = new ContentValues();
                    values.put("track_node",0);
                    ((Aplicacion)getApplication()).getDB().updateCoinInfo(values);
                    ((Aplicacion)getApplication()).getDB().deleteTrack(arrayTracking.get(position).id);
                    arrayTracking.remove(position);
                    adapter.notifyDataSetChanged();
                }
            });



            return grid;
        }
    }
}
