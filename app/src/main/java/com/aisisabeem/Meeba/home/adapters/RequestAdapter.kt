package com.aisisabeem.Meeba.home.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.aisisabeem.Meeba.R
import com.aisisabeem.Meeba.models.Requests

class RequestAdapter(

    private val context:Context,
    private val requestList:ArrayList<Requests>,
    private val OndestinationClicked:HandleDestinationClicked,
    private val handleCompleteRequest: HandleCompleteRequest,
    private val viewRequestAgain : HandleCompleteRequest

    )
    :RecyclerView.Adapter<RequestAdapter.RidersViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RidersViewHolder {
 val itemView = LayoutInflater.from(parent.context).inflate(R.layout.request_list,parent,false)

        return RidersViewHolder(itemView)
    }

    interface HandleDestinationClicked{
        fun onDistClicked(position: Int,destList:Requests)
    }

    interface HandleCompleteRequest {
        fun onRequestComple(position: Int,destList:Requests,valueTag:String)
        fun openDialog(position: Int,destList: Requests)
        fun UpdateRiderrequestCompleted(position: Int, destList: Requests)
        fun viewRequest(destList: Requests)



    }

    override fun onBindViewHolder(holder: RidersViewHolder, position: Int) {
       val requests: Requests = requestList[position]
        holder.companyName.text = requests.company_name
        holder.dateOrDered.text = requests.createdAt.toString()
        holder.orderStatus.text = requests.status
        val option: String? = requests.option

        if (option.equals("NotCompleted")){
            holder.btn_view_request.visibility = View.VISIBLE
            holder.btn_accept_request.visibility = View.GONE
            holder.btn_complete_request.visibility = View.VISIBLE
        }

       holder.cardViewrequest.setOnClickListener{

       }
        holder.btn_accept_request.setOnClickListener {

                val acceptText:String = holder.btn_accept_request.text.toString()
                if (acceptText.equals("Accept")){
                    OndestinationClicked.onDistClicked(position,requests)
                    holder.btn_accept_request.visibility = View.INVISIBLE
                    holder.btn_view_request.visibility = View.VISIBLE
                    holder.btn_complete_request.visibility = View.VISIBLE



            }

        }

        holder.btn_view_request.setOnClickListener {
            val viewText:String = holder.btn_accept_request.text.toString()
            val changeTextComplete:String = holder.btn_complete_request.text.toString()
            viewRequestAgain.viewRequest(requests)

        }

        holder.btn_complete_request.setOnClickListener {


            val changeTextComplete:String = holder.btn_complete_request.text.toString()
            val viewText:String = holder.btn_accept_request.text.toString()

            if (changeTextComplete.equals("Complete Request")){

                AlertDialog.Builder(context)
                    .setTitle("Comfirm ${requests.company_name} completed")
                    .setMessage("Order date and time \n ${requests.createdAt}" +
                            " \n\n Note: if you complete request that \n means trip ended with product\n delivered if not click view to finish \ndelivering. Thank you")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes
                    ) { arg0, arg1 ->

                        handleCompleteRequest.onRequestComple(position,requests,changeTextComplete)
                        arg0.dismiss()
                    }.create().show()



            }
        }

    }

    override fun getItemCount(): Int {
      return requestList.size
    }

    class RidersViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
          val companyName:TextView = itemView.findViewById(R.id.order_owner_name)
          val dateOrDered:TextView = itemView.findViewById(R.id.date_ordered)
          val orderStatus:TextView = itemView.findViewById(R.id.order_status)
        val btn_accept_request:Button = itemView.findViewById(R.id.btn_accept_request)
        val btn_complete_request:Button = itemView.findViewById(R.id.btn_complete_request)
        val btn_view_request:Button = itemView.findViewById(R.id.btn_view_request)
        val cardViewrequest:CardView = itemView.findViewById(R.id.cardViewrequest)
    }
}