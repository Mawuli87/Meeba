package com.aisisabeem.Meeba.home.destination

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aisisabeem.Meeba.R

import com.skyfishjy.library.RippleBackground



class DestinationAdapter(
    private val context:Context,
    private val requestList:ArrayList<Destinations>,
    private val onHandleOpenDestinationClicked:HandleOpenDestinationClicked
    ) :RecyclerView.Adapter<DestinationAdapter.DestinationViewHolder>() {



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DestinationViewHolder {
 val itemView = LayoutInflater.from(context).inflate(R.layout.destination_list,parent,false)

        return DestinationViewHolder(itemView)
    }

    interface HandleOpenDestinationClicked{
        fun onDestClicked(position: Int,destList:Destinations)
        fun onPaymentClicked(position: Int,destList: Destinations)
    }

    override fun onBindViewHolder(holder: DestinationViewHolder, position: Int) {
       val destinations: Destinations = requestList[position]
        holder.distance.text = destinations.distance.toString()
       // holder.key.text = destinations.key.toString()
        holder.price.text = destinations.price.toString()
        holder.product_cost.text = destinations.product_cost
        holder.receiver_contact.text = destinations.receiver_contact
        holder.receiver_note.text = destinations.receiver_note
        //holder.stop_location_id.text = destinations.stoplocation_id
        //holder.stop_location_latitude.text = destinations.stoplocation_lat.toString()
        val latitude:String = destinations.stoplocation_lat.toString()
        val longitude:String = destinations.stoplocation_lng.toString()
        holder.stop_location_name.text = destinations.stoplocation_name
        holder.stopLocationName.text = destinations.stoplocation_name

       holder.rippleBackground.startRippleAnimation()

        holder.destinationImage.setOnClickListener {

            onHandleOpenDestinationClicked.onDestClicked(position,destinations)
           // holder.rippleBackground.stopRippleAnimation()
        }
     holder.btn_pay.setOnClickListener {
         onHandleOpenDestinationClicked.onPaymentClicked(position,destinations)
     }




    }

    override fun getItemCount(): Int {
      return requestList.size
    }

    class DestinationViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
       // private lateinit var rippleBackground:RippleBackground

        val distance: TextView = itemView.findViewById(R.id.order_owner_distance)

        //val key:TextView = itemView.findViewById(R.id.order_key)
        val price: TextView = itemView.findViewById(R.id.order_price)
        val product_cost: TextView = itemView.findViewById(R.id.order_product_cost)
        val receiver_contact: TextView = itemView.findViewById(R.id.order_receiver_contact)
        val receiver_note: TextView = itemView.findViewById(R.id.order_receiver_note)

        // val stop_location_id:TextView = itemView.findViewById(R.id.order_stop_location_id)
        // val stop_location_latitude:TextView = itemView.findViewById(R.id.order_stoplocation_latitude)
        // val stop_location_longitude:TextView = itemView.findViewById(R.id.order_stoplocation_longitude)
        val stop_location_name: TextView = itemView.findViewById(R.id.order_stop_location_name)
        val stopLocationName: TextView = itemView.findViewById(R.id.stoplocationname)
        val destinationImage: ImageView = itemView.findViewById(R.id.destinationMap)
        var rippleBackground:RippleBackground = itemView.findViewById(R.id.content_ripple)
        val btn_pay: Button = itemView.findViewById(R.id.btn_make_payment)





    }
}