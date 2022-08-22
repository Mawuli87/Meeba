package com.aisisabeem.Meeba.home.pickup

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aisisabeem.Meeba.R



class PickUpAdapter(
    private val context:Context,
    private val pickupList:ArrayList<PickUp>,
    private val onPickupClicked:HandlePickupItem
    )
    :RecyclerView.Adapter<PickUpAdapter.DestinationViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DestinationViewHolder {
 val itemView = LayoutInflater.from(context).inflate(R.layout.destination_list,parent,false)

        return DestinationViewHolder(itemView)
    }
    interface HandlePickupItem {
        fun onItemClick(position: Int,CoordinatList:PickUp)
    }

    override fun onBindViewHolder(holder: DestinationViewHolder, position: Int) {
       val pickup: PickUp = pickupList[position]
       // holder.pickupLocationName.text = pickup.pickup_locationname.toString()

        val lng:Double = pickup.pickup_location_lng
        val lat:Double = pickup.pickup_location_lat

        onPickupClicked.onItemClick(position,pickup)


    }

    override fun getItemCount(): Int {
      return pickupList.size
    }

    class DestinationViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
//          val pickup_lat:TextView = itemView.findViewById(R.id.order_owner_distance)
//          val pickupLng:TextView = itemView.findViewById(R.id.order_key)
          //val pickupLocationName:TextView = itemView.findViewById(R.id.pickuplocationname)
         // val pickStatus:TextView = itemView.findViewById(R.id.pickupStatus)


    }
}