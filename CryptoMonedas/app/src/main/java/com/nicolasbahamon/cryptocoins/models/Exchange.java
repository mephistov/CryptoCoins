package com.nicolasbahamon.cryptocoins.models;

import com.nicolasbahamon.cryptocoins.Aplicacion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by INTEL on 08/03/2018.
 */

public class Exchange {

    public long id;
    public String name;
    public double ask;
    public  double bid;
    public long id_exchange;
    public int orderBook;
    public boolean allreadyDownload = false;

    public double sellprice=0, buyprice=0;
    public String apoSresponse;
    public int coinexchangeio;
    public int coinexchangeio_id;
    public double tofillamount;
    public double tofillamountS;


    public void getBookSellExchangeGraviex(String resp, double amount){

        if(resp !=null)
            apoSresponse = resp;
        else
            resp = apoSresponse;

        if(resp!=null) {
            try {
                JSONObject orgerJ = new JSONObject(resp);

                buyprice = 0;
                tofillamount = 0;
                double precio = 0;
                JSONArray buyList = orgerJ.getJSONArray("asks");
                JSONArray sellList = orgerJ.getJSONArray("bids");

                //buy
                for (int i = 0; i < buyList.length(); i++) {
                    if (tofillamount < amount) {
                        double volumen = buyList.getJSONObject(i).getDouble("remaining_volume");
                        precio = buyList.getJSONObject(i).getDouble("price");

                        double restaDif = (amount - tofillamount);
                        if (volumen > restaDif) {
                            double volumenT = volumen - restaDif;
                            volumen = volumen - volumenT;
                        }

                        tofillamount += volumen;
                        double tempVal = volumen * precio;
                        buyprice += tempVal;
                    }
                }
                //sell
                sellprice = 0;
                tofillamountS = 0;
                double precioS = 0;
                for (int i = 0; i < sellList.length(); i++) {
                    if (tofillamountS < amount) {
                        double volumen = sellList.getJSONObject(i).getDouble("remaining_volume");
                        precioS = sellList.getJSONObject(i).getDouble("price");

                        double restaDif = (amount - tofillamountS);
                        if (volumen > restaDif) {
                            double volumenT = volumen - restaDif;
                            volumen = volumen - volumenT;
                        }

                        tofillamountS += volumen;
                        double tempVal = volumen * precioS;
                        sellprice += tempVal;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public void getBookSellExchangeSouthXchange(String resp, double amount) {
        if(resp !=null)
            apoSresponse = resp;
        else
            resp = apoSresponse;


        try {

            JSONObject orgerJ = new JSONObject(resp);
            buyprice =0;
            tofillamount = 0;
            double precio = 0;
            JSONArray buyList = orgerJ.getJSONArray("SellOrders");
            JSONArray sellList = orgerJ.getJSONArray("BuyOrders");

            //buy
            for(int i = 0;i< buyList.length();i++){
                if(tofillamount < amount){
                    double volumen = buyList.getJSONObject(i).getDouble("Amount");
                    precio =  buyList.getJSONObject(i).getDouble("Price");

                    double restaDif = (amount-tofillamount);
                    if(volumen > restaDif){
                        double volumenT = volumen -restaDif;
                        volumen = volumen-volumenT;
                    }

                    tofillamount += volumen;
                    double tempVal = volumen*precio;
                    buyprice += tempVal;
                }
            }

            //sell
            sellprice =0;
            tofillamountS = 0;
            double precioS = 0;
            for(int i = 0;i< sellList.length();i++){
                if(tofillamountS < amount){
                    double volumen = sellList.getJSONObject(i).getDouble("Amount");
                    precioS =  sellList.getJSONObject(i).getDouble("Price");

                    double restaDif = (amount-tofillamountS);
                    if(volumen > restaDif){
                        double volumenT = volumen -restaDif;
                        volumen = volumen-volumenT;
                    }

                    tofillamountS += volumen;
                    double tempVal = volumen*precioS;
                    sellprice += tempVal;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getBookSellExchangeCoinExchangeIO(String resp, double amount) {
        if(resp !=null)
            apoSresponse = resp;
        else
            resp = apoSresponse;

         try {

            JSONObject orgerJ = new JSONObject(resp);

            buyprice =0;
            tofillamount = 0;
            double precio = 0;
            JSONArray sellList = orgerJ.getJSONObject("result").getJSONArray("BuyOrders"); //some thing was wrong jejeje
            JSONArray  buyList = orgerJ.getJSONObject("result").getJSONArray("SellOrders");

             //buy
             for(int i = 0;i< buyList.length();i++){
                 if(tofillamount < amount){
                     double volumen = buyList.getJSONObject(i).getDouble("Quantity");
                     precio =  buyList.getJSONObject(i).getDouble("Price");

                     double restaDif = (amount-tofillamount);
                     if(volumen > restaDif){
                         double volumenT = volumen -restaDif;
                         volumen = volumen-volumenT;
                     }

                     tofillamount += volumen;
                     double tempVal = volumen*precio;
                     buyprice += tempVal;
                 }
             }

             //sell
             sellprice =0;
             tofillamountS = 0;
             double precioS = 0;
             for(int i = 0;i< sellList.length();i++){
                 if(tofillamountS < amount){
                     double volumen = sellList.getJSONObject(i).getDouble("Quantity");
                     precioS =  sellList.getJSONObject(i).getDouble("Price");

                     double restaDif = (amount-tofillamountS);
                     if(volumen > restaDif){
                         double volumenT = volumen -restaDif;
                         volumen = volumen-volumenT;
                     }

                     tofillamountS += volumen;
                     double tempVal = volumen*precioS;
                     sellprice += tempVal;
                 }
             }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getBookSellExchangeTradeSatoshi(String resp, double amount) {
        if(resp !=null)
            apoSresponse = resp;
        else
            resp = apoSresponse;

        try {

            JSONObject orgerJ = new JSONObject(resp);

            buyprice =0;
            tofillamount = 0;
            double precio = 0;
            JSONArray buyList = orgerJ.getJSONObject("result").getJSONArray("sell");
            JSONArray sellList = orgerJ.getJSONObject("result").getJSONArray("buy");

            //buy
            for(int i = 0;i< buyList.length();i++){
                if(tofillamount < amount){
                    double volumen = buyList.getJSONObject(i).getDouble("quantity");
                    precio =  buyList.getJSONObject(i).getDouble("rate");

                    double restaDif = (amount-tofillamount);
                    if(volumen > restaDif){
                        double volumenT = volumen -restaDif;
                        volumen = volumen-volumenT;
                    }

                    tofillamount += volumen;
                    double tempVal = volumen*precio;
                    buyprice += tempVal;
                }
            }

            //sell
            sellprice =0;
            tofillamountS = 0;
            double precioS = 0;
            for(int i = 0;i< sellList.length();i++){
                if(tofillamountS < amount){
                    double volumen = sellList.getJSONObject(i).getDouble("quantity");
                    precioS =  sellList.getJSONObject(i).getDouble("rate");

                    double restaDif = (amount-tofillamountS);
                    if(volumen > restaDif){
                        double volumenT = volumen -restaDif;
                        volumen = volumen-volumenT;
                    }

                    tofillamountS += volumen;
                    double tempVal = volumen*precioS;
                    sellprice += tempVal;
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getBookSellExchangeStockExchange(String resp, double amount) {

    }
}


