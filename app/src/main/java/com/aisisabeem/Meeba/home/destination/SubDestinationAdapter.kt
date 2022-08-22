package com.aisisabeem.Meeba.home.destination

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aisisabeem.Meeba.R

import com.skyfishjy.library.RippleBackground
import kotlinx.coroutines.CoroutineScope


class SubDestinationAdapter(
    private val context: Context,
    private val requestSubList:ArrayList<SubDestination>,
    private val onHandleOpenSubDestinationClicked:HandleOpenSubDestinationClicked
    ) :RecyclerView.Adapter<SubDestinationAdapter.DestinationSubViewHolder>() {



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DestinationSubViewHolder {
 val itemView = LayoutInflater.from(context).inflate(R.layout.sub_destination_list,parent,false)

        return DestinationSubViewHolder(itemView)
    }

    interface HandleOpenSubDestinationClicked{
        fun onDestClicked(position: Int,destSubList: SubDestination)
    }

    override fun onBindViewHolder(holder: DestinationSubViewHolder, position: Int) {
       val subdestinations: SubDestination = requestSubList[position]
        holder.distance.text = subdestinations.distance.toString()
       // holder.key.text = destinations.key.toString()
        holder.receiver_contact.text = subdestinations.receiver_contact
        holder.receiver_note.text = subdestinations.receiver_note
        //holder.stop_location_id.text = destinations.stoplocation_id
        //holder.stop_location_latitude.text = destinations.stoplocation_lat.toString()
        val latitude:String = subdestinations.stoplocation_lat.toString()
        val longitude:String = subdestinations.stoplocation_lng.toString()
        holder.stop_location_name.text = subdestinations.stoplocation_name
        holder.stopLocationName.text = subdestinations.stoplocation_name

       holder.rippleBackground.startRippleAnimation()

        holder.destinationImage.setOnClickListener {

            onHandleOpenSubDestinationClicked.onDestClicked(position,subdestinations)
           // holder.rippleBackground.stopRippleAnimation()
        }





    }

    override fun getItemCount(): Int {
      return requestSubList.size
    }

    class DestinationSubViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
       // private lateinit var rippleBackground:RippleBackground

        val distance: TextView = itemView.findViewById(R.id.order_owner_distancesub)

        //val key:TextView = itemView.findViewById(R.id.order_key)
       // val price: TextView = itemView.findViewById(R.id.order_pricesub)
      //  val product_cost: TextView = itemView.findViewById(R.id.order_product_cost)
        val receiver_contact: TextView = itemView.findViewById(R.id.order_receiver_contactsub)
        val receiver_note: TextView = itemView.findViewById(R.id.order_receiver_notesub)

        // val stop_location_id:TextView = itemView.findViewById(R.id.order_stop_location_id)
        // val stop_location_latitude:TextView = itemView.findViewById(R.id.order_stoplocation_latitude)
        // val stop_location_longitude:TextView = itemView.findViewById(R.id.order_stoplocation_longitude)
        val stop_location_name: TextView = itemView.findViewById(R.id.order_stop_location_namesub)
        val stopLocationName: TextView = itemView.findViewById(R.id.stoplocationnamesub)
        val destinationImage: ImageView = itemView.findViewById(R.id.destinationMapsub)
        var rippleBackground:RippleBackground = itemView.findViewById(R.id.content_ripplesub)




    }
}