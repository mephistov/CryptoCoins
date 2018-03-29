package com.nicolasbahamon.cryptocoins;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.nicolasbahamon.cryptocoins.models.Exchange;
import com.nicolasbahamon.cryptocoins.models.TrackingN;
import com.nicolasbahamon.cryptocoins.network.HttpClient;

import java.util.ArrayList;

public class TrackMasterNodes extends Activity {

    private ArrayList<TrackingN> arrayTracking;
    private ListView listtrack;
    private TrackAdapter adapter;
    private HttpClient httpCLient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_master_nodes);

        //adds
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        httpCLient = new HttpClient(getApplicationContext(), ((Aplicacion) getApplication()));

        arrayTracking = ((Aplicacion)getApplication()).getDB().getAllTracking();
        listtrack =(ListView)findViewById(R.id.listTracking);
        adapter = new TrackAdapter(getApplicationContext());

        listtrack.setAdapter(adapter);

    }

    // ******** background task ******

    private class RedeemTask extends AsyncTask<Void, Void, Void> {

        private String respuestas;

        @Override
        protected void onPreExecute() {


        }

        protected Void doInBackground(Void... bounds) {


            respuestas = httpCLient.HttpConnectGet("192.168.1.101/cryptocoins/explorer.json",null);//190.26.134.117/cryptocoins/explorer.json

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {




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

            name.setText(arrayTracking.get(position).name);



            return grid;
        }
    }
}
