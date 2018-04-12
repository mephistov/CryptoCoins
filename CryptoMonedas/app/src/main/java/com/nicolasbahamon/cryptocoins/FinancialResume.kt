package com.nicolasbahamon.cryptocoins

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.nicolasbahamon.cryptocoins.models.ResumeFinancial
import com.nicolasbahamon.cryptocoins.models.TrackingN
import kotlinx.android.synthetic.main.activity_financial_resume.*
import kotlinx.android.synthetic.main.row_finalcial.view.*
import java.util.HashMap
import java.util.Map

class FinancialResume : Activity() {

    var coinstracked: ArrayList<TrackingN> = ArrayList()
    var resumeFList: ArrayList<ResumeFinancial> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_financial_resume)

        coinstracked = (application as Aplicacion).getDB().allTracking
        var invested = 0.0
        var capital = 0.0
        val allDataRe = HashMap<String, ResumeFinancial>()

        for(tracked: TrackingN in coinstracked){
            var temptrack = ResumeFinancial()
            if(allDataRe.containsKey(tracked.shortname)){
                temptrack = allDataRe.get(tracked.shortname)!!
            }

            val moneda = (application as Aplicacion).getDB().getCoinByName(tracked.shortname)

            temptrack.name = moneda.name
            temptrack.logo = moneda.logo
            temptrack.shoert = tracked.shortname
            temptrack.coins += tracked.balance
            temptrack.masternodes++
            temptrack.invest += tracked.mncost
            temptrack.earned += (tracked.balance * moneda.price)

            invested += tracked.mncost
            capital +=(tracked.balance * moneda.price)

            allDataRe.put(tracked.shortname,temptrack)

        }


        val it = allDataRe.entries.iterator()
        while (it.hasNext()) {
            val pair = it.next() as Map.Entry<String, ResumeFinancial>
           // println(pair.key + " = " + pair.value)
            resumeFList.add(pair.value)
            it.remove() // avoids a ConcurrentModificationException
        }

        investedText.setText("$ "+ (application as Aplicacion).numberFormat(invested)+" USD")
        earnedText.setText("$ "+(application as Aplicacion).numberFormat(capital)+" USD")
        if((capital-invested)<0)
            BalanceGeneral.setTextColor(Color.RED)
        else
            BalanceGeneral.setTextColor(Color.GREEN)
        BalanceGeneral.setText("$ "+(application as Aplicacion).numberFormat(capital-invested)+" USD")

        allinvestResum.layoutManager = LinearLayoutManager(this)
        allinvestResum.adapter = GeneralAdapter(resumeFList,applicationContext,(application as Aplicacion))

    }

    class GeneralAdapter(val items : ArrayList<ResumeFinancial>, val context: Context,val application: Application) : RecyclerView.Adapter<GeneralAdapter.ViewHolderF>() {
        override fun getItemCount(): Int {
            return items.size
        }

        override fun onBindViewHolder(holder: ViewHolderF, position: Int) {
            holder.tvAnimalType?.text = items.get(position).name
            val dat = "( "+items.get(position).shoert+" )"
            holder.otro?.text = dat
            Glide.with(context)
                    .load(items.get(position).logo)
                    .into(holder.imageLogo)
            holder.investT?.text = "$ "+(application as Aplicacion).numberFormat(items.get(position).invest)+" USD"
            holder.earnedT?.text = "$ "+(application as Aplicacion).numberFormat(items.get(position).earned)+" USD"
            holder.cantWall?.text = ""+items.get(position).masternodes
            if((items.get(position).earned-items.get(position).invest)<0)
                holder.globalB?.setTextColor(Color.RED)
            else
                holder.globalB?.setTextColor(Color.GREEN)
            holder.globalB?.text = "$ "+(application as Aplicacion).numberFormat((items.get(position).earned-items.get(position).invest))+" USD"
            holder.coinsC?.text = items.get(position).coins.toString()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderF {
            return ViewHolderF(LayoutInflater.from(context).inflate(R.layout.row_finalcial, parent, false))
        }

        class ViewHolderF (view: View) : RecyclerView.ViewHolder(view) {
            // Holds the TextView that will add each animal to
            val tvAnimalType = view.textView82
            val otro = view.textView83
            val imageLogo = view.imageView6
            val investT = view.textView86
            val earnedT = view.textView89
            val cantWall = view.textView89_2
            val globalB = view.textView89_3
            val coinsC = view.textView89_4

        }

    }

}
