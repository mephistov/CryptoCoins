package com.nicolasbahamon.cryptocoins;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.nicolasbahamon.cryptocoins.models.Coin;
import com.nicolasbahamon.cryptocoins.network.HttpClient;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingsCoin extends Activity {

    SettingsCoin actv;
    private  EditText qrwallet;
    private  Button searchApi;
    EditText apiCoin;
    Coin moneda;
    CheckBox isMNOption;

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            qrwallet.setText(scanContent);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_coin);

        actv = this;

        final EditText name = (EditText)findViewById(R.id.editText6);
        ImageView logoImg = (ImageView)findViewById(R.id.imageView3);

        qrwallet = (EditText)findViewById(R.id.editText2);
        apiCoin = (EditText)findViewById(R.id.editText3);
        final EditText costMn = (EditText)findViewById(R.id.editText4);
        Button savebtn = (Button)findViewById(R.id.button7);
        Button readQr = (Button)findViewById(R.id.button6);
        searchApi = (Button)findViewById(R.id.button);
        isMNOption = (CheckBox)findViewById(R.id.checkBox4);

        if(((Aplicacion)getApplication()).coinEdit == null)
            finish();
        else {
            moneda = ((Aplicacion) getApplication()).getDB().getCoinByName(((Aplicacion) getApplication()).coinEdit.shortname);

            Glide.with(getApplicationContext())
                    .load(moneda.logo)
                    .into(logoImg);

            name.setText(((Aplicacion) getApplication()).coinEdit.name);
            qrwallet.setText(((Aplicacion) getApplication()).coinEdit.address);
            apiCoin.setText(((Aplicacion) getApplication()).coinEdit.explorer);
            try {
                if (((Aplicacion) getApplication()).coinEdit.explorer == null || ((Aplicacion) getApplication()).coinEdit.explorer.equals(""))
                    if (((Aplicacion) getApplication()).apiExplorer != null)
                        apiCoin.setText(((Aplicacion) getApplication()).apiExplorer.getJSONObject(moneda.shortname).getString("explor"));


            } catch (JSONException e) {
                e.printStackTrace();
            }
            costMn.setText(((Aplicacion) getApplication()).coinEdit.mncost + "");

            if (((Aplicacion) getApplication()).coinEdit.hasNode == 1)
                isMNOption.setChecked(true);
            else
                isMNOption.setChecked(false);


            savebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentValues values = new ContentValues();

                    String valExp = apiCoin.getText().toString();
              /*  int serhttp = valExp.indexOf("http");
                int balanceExp = valExp.indexOf("balance");
                if(serhttp == -1)
                    valExp = "http://"+valExp;
                if(balanceExp == -1)
                    valExp += "/ext/getbalance/";*/

                    values.put("explorer", valExp);
                    values.put("name_coin", name.getText().toString());
                    values.put("wallet", qrwallet.getText().toString());
                    values.put("mn_cost", costMn.getText().toString());
                    int hn = 0;
                    if (isMNOption.isChecked())
                        hn = 1;
                    values.put("has_masternode", hn);
                    values.put("id", ((Aplicacion) getApplication()).coinEdit.id);


                    ((Aplicacion) getApplication()).getDB().updateTrackingInfo(values);
                }
            });
            readQr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    QrScanner(view);
                }
            });

            searchApi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new RedeemTask().execute();

                }
            });
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((Aplicacion)getApplication()).coinEdit = null;
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
        HttpClient httpCLient;
        @Override
        protected void onPreExecute() {
             httpCLient = new HttpClient(getApplicationContext(), ((Aplicacion) getApplication()));
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
                    apiCoin.setText(((Aplicacion) getApplication()).apiExplorer.getJSONObject(moneda.shortname).getString("explor"));
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),"Not found, fill it manual please, Thank you",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(getApplicationContext(),"Not found, fill it manual please, Thank you",Toast.LENGTH_LONG).show();
            }



        }


    }
}
