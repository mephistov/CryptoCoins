package com.nicolasbahamon.cryptocoins;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nicolasbahamon.cryptocoins.models.Coin;
import com.nicolasbahamon.cryptocoins.models.Exchange;

import java.util.ArrayList;

public class AllCoins extends Activity {

    private ArrayList<Coin> coins;
    private  CoinsAdapter adapter;
    private ListView listCoins;
    private  RelativeLayout allcoinsZone, favouriteZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_coins);

        //adds
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        allcoinsZone = (RelativeLayout)findViewById(R.id.allzone);
        favouriteZone = (RelativeLayout)findViewById(R.id.favzone);

        coins = ((Aplicacion) getApplication()).getDB().getAllCoins();
        listCoins = (ListView)findViewById(R.id.listCoinhs);
        adapter = new CoinsAdapter(getApplicationContext());

        listCoins.setAdapter(adapter);

        //----------------------------------------
        favouriteZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coins = ((Aplicacion) getApplication()).getDB().sortGeneric();
                adapter.notifyDataSetChanged();
            }
        });
        allcoinsZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coins = ((Aplicacion) getApplication()).getDB().getAllCoins();
                adapter.notifyDataSetChanged();
            }
        });


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
                grid = new View(MyContext);
                LayoutInflater inflater = (LayoutInflater)MyContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                grid = inflater.inflate(R.layout.row_coins_info, parent, false);

            } else {
                grid = (View) convertView;
            }

            TextView name = (TextView)grid.findViewById(R.id.textView55);
            TextView shortname = (TextView)grid.findViewById(R.id.textView56);
            ImageView logo = (ImageView)grid.findViewById(R.id.imageView4);
            TextView priveUsd = (TextView)grid.findViewById(R.id.textView60);
            TextView change1h = (TextView)grid.findViewById(R.id.textView68);
            TextView change24h = (TextView)grid.findViewById(R.id.textView66);
            TextView change7d = (TextView)grid.findViewById(R.id.textView65);
            final Button favBtc = (Button)grid.findViewById(R.id.button11);


            name.setText(coins.get(position).name);
            shortname.setText(coins.get(position).shortname);
            Glide.with(getApplicationContext())
                    .load(coins.get(position).logo)
                    .into(logo);

            priveUsd.setText("$ "+((Aplicacion) getApplication()).numberFormat(coins.get(position).price));

            if(coins.get(position).porcentage_day<0){
                change1h.setTextColor(Color.RED);
            }else{
                change1h.setTextColor(Color.GREEN);
            }
            if(coins.get(position).porcentage_hour<0){
                change24h.setTextColor(Color.RED);
            }else{
                change24h.setTextColor(Color.GREEN);
            }
            if(coins.get(position).porcentage_week<0){
                change7d.setTextColor(Color.RED);
            }else{
                change7d.setTextColor(Color.GREEN);
            }

            change1h.setText(coins.get(position).porcentage_hour+" %");
            change24h.setText(coins.get(position).porcentage_day+" %");
            change7d.setText(coins.get(position).porcentage_week+" %");

            if(coins.get(position).favorite == 1){
                favBtc.setBackgroundResource(R.mipmap.heart_f);
            }else{
                favBtc.setBackgroundResource(R.mipmap.heart_e);
            }

            favBtc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentValues values = new ContentValues();
                    values.put("shortname",coins.get(position).shortname);
                    if(coins.get(position).favorite == 1){
                        coins.get(position).favorite = 0;
                        values.put("favorite",0);
                        ((Aplicacion)getApplication()).getDB().updateCoinInfo(values);
                        favBtc.setBackgroundResource(R.mipmap.heart_e);
                    }else{
                        coins.get(position).favorite = 1;
                        values.put("favorite",1);
                        ((Aplicacion)getApplication()).getDB().updateCoinInfo(values);
                        favBtc.setBackgroundResource(R.mipmap.heart_f);
                    }

                }
            });


            return grid;
        }
    }
}
