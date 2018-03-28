package com.nicolasbahamon.cryptocoins.models;

import java.util.ArrayList;

/**
 * Created by nicolascastilla on 3/9/18.
 */

public class OrderBook {

    public long indExchange;
    public ArrayList<BuyBook> buyBook = new ArrayList<>();
    public ArrayList<SellBook> sellBook = new ArrayList<>();;
}
