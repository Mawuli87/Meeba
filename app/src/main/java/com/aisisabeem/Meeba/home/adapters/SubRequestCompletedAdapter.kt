package com.aisisabeem.Meeba.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aisisabeem.Meeba.R
import com.aisisabeem.Meeba.models.SubRequestCompleted

class SubRequestCompletedAdapter(
    private val context: Context,
    private val requestSubCompletedList:ArrayList<SubRequestCompleted>
   ):RecyclerView.Adapter<SubRequestCompletedAdapter.SubCompleteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubCompleteViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.sub_requests_completed_data,parent,false)
        return SubCompleteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SubCompleteViewHolder, position: Int) {
        val complete_request = requestSubCompletedList[position]

        holder.companyName.text = complete_request.company_name
        holder.completeStatus.text = complete_request.Accepted_Status
        holder.requestCompleteTime.text = complete_request.completed_time.toString()
        holder.requestCompleteOption.text = complete_request.option

    }

    override fun getItemCount(): Int {
       return requestSubCompletedList.size
    }

    inner class SubCompleteViewHolder(view: View):RecyclerView.ViewHolder(view){
        val completeStatus: TextView = itemView.findViewById(R.id.sub_completed_order_status)
        val companyName: TextView = itemView.findViewById(R.id.sub_complete_order_owner_name)
        val requestCompleteTime: TextView = itemView.findViewById(R.id.sub_completed_order_time)
        val requestCompleteOption: TextView = itemView.findViewById(R.id.sub_completed_order_option)
    }
}