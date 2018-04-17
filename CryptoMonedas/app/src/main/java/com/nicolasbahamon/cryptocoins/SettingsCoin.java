package com.nicolasbahamon.cryptocoins;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.nicolasbahamon.cryptocoins.models.Coin;

public class SettingsCoin extends Activity {

    SettingsCoin actv;
    private  EditText qrwallet;

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
        final EditText apiCoin = (EditText)findViewById(R.id.editText3);
        final EditText costMn = (EditText)findViewById(R.id.editText4);
        Button savebtn = (Button)findViewById(R.id.button7);
        Button readQr = (Button)findViewById(R.id.button6);

        final Coin moneda =((Aplicacion)getApplication()).getDB().getCoinByName(((Aplicacion)getApplication()).coinEdit.shortname);

        Glide.with(getApplicationContext())
                .load(moneda.logo)
                .into(logoImg);

        name.setText(((Aplicacion)getApplication()).coinEdit.name);
        qrwallet.setText(((Aplicacion)getApplication()).coinEdit.address);
        apiCoin.setText(((Aplicacion)getApplication()).coinEdit.explorer);
        costMn.setText(((Aplicacion)getApplication()).coinEdit.mncost+"");


        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();

                String valExp = apiCoin.getText().toString();
                int serhttp = valExp.indexOf("http");
                int balanceExp = valExp.indexOf("ext/getbalance");
                if(serhttp == -1)
                    valExp = "http://"+valExp;
                if(balanceExp == -1)
                    valExp += "/ext/getbalance/";

                values.put("explorer",valExp);
                values.put("name_coin",name.getText().toString());
                values.put("wallet",qrwallet.getText().toString());
                values.put("mn_cost",costMn.getText().toString());
                values.put("id",((Aplicacion)getApplication()).coinEdit.id);



                ((Aplicacion)getApplication()).getDB().updateTrackingInfo(values);
            }
        });
        readQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QrScanner(view);
            }
        });

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
}
