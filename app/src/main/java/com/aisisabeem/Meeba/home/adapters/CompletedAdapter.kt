package com.aisisabeem.Meeba.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aisisabeem.Meeba.R
import com.aisisabeem.Meeba.models.Completed

class CompletedAdapter (
    private val context: Context,
    private val requestCompletedList:ArrayList<Completed>
    ): RecyclerView.Adapter<CompletedAdapter.CompletedViewHolder>()
    {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompletedViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.request_completed_list,parent,false)
            return CompletedViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: CompletedViewHolder, position: Int) {
           val complete_request = requestCompletedList[position]

            holder.companyName.text = complete_request.company_name
            holder.completeStatus.text = complete_request.completed_status
            holder.requestCompleteTime.text = complete_request.completed_time
            holder.requestCompleteOption.text = complete_request.option

        }

        override fun getItemCount(): Int {
            return requestCompletedList.size
        }

        class CompletedViewHolder(ItemView:View):RecyclerView.ViewHolder(ItemView){
            val completeStatus: TextView = itemView.findViewById(R.id.completed_order_status)
            val companyName: TextView = itemView.findViewById(R.id.complete_order_owner_name)
            val requestCompleteTime: TextView = itemView.findViewById(R.id.completed_order_time)
            val requestCompleteOption: TextView = itemView.findViewById(R.id.completed_order_option)
        }
    }


