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
import com.aisisabeem.Meeba.models.SubRequests

class SubRequestAdapter(

    private val context:Context,
    private val subRequestList:ArrayList<SubRequests>,
    private val OnSubdestinationClicked:HandleSubDestinationClicked,
//    private val handleCompleteSubRequest: HandleCompleteSubRequest,
    private val viewSubRequestAgain : HandleCompleteSubRequest

    )
    :RecyclerView.Adapter<SubRequestAdapter.SubRidersViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SubRidersViewHolder {
 val itemView = LayoutInflater.from(parent.context).inflate(R.layout.sub_request_list,parent,false)

        return SubRidersViewHolder(itemView)
    }

    interface HandleSubDestinationClicked{
        fun onSubDistClicked(position: Int,subDestList:SubRequests)
    }

    interface HandleCompleteSubRequest {
        fun onSubRequestComple(position: Int,subDestList:SubRequests,valueTag:String)
       // fun openDialog(position: Int,subDestList: SubRequests)
        fun UpdateRiderSubrequestCompleted(position: Int, subDestList: SubRequests)
        fun viewSubRequest(destList: SubRequests)


    }

    override fun onBindViewHolder(holder: SubRidersViewHolder, position: Int) {
       val subRequests: SubRequests = subRequestList[position]
        holder.companyName.text = subRequests.company_name
        holder.dateOrDered.text = subRequests.createdAt.toString()
        holder.orderStatus.text = subRequests.status
        val option: String? = subRequests.option

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
                   OnSubdestinationClicked.onSubDistClicked(position,subRequests)
                    holder.btn_accept_request.visibility = View.INVISIBLE
                    holder.btn_view_request.visibility = View.VISIBLE
                    holder.btn_complete_request.visibility = View.VISIBLE



            }

        }

        holder.btn_view_request.setOnClickListener {
            val viewText:String = holder.btn_accept_request.text.toString()
            val changeTextComplete:String = holder.btn_complete_request.text.toString()
            viewSubRequestAgain.viewSubRequest(subRequests)

        }

        holder.btn_complete_request.setOnClickListener {


            val changeTextComplete:String = holder.btn_complete_request.text.toString()
            val viewText:String = holder.btn_accept_request.text.toString()

            if (changeTextComplete.equals("Complete Sub-Request")){

                AlertDialog.Builder(context)
                    .setTitle("Comfirm ${subRequests.company_name} completed")
                    .setMessage("Order date and time \n ${subRequests.createdAt}" +
                            " \n\n Note: if you complete request that \n means trip ended with product\n delivered if not click view to finish \ndelivering. Thank you")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes
                    ) { arg0, arg1 ->

                        viewSubRequestAgain.onSubRequestComple(position,subRequests,changeTextComplete)
                        arg0.dismiss()
                    }.create().show()



            }
        }

    }

    override fun getItemCount(): Int {
      return subRequestList.size
    }

    class SubRidersViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
          val companyName:TextView = itemView.findViewById(R.id.order_owner_namesub)
          val dateOrDered:TextView = itemView.findViewById(R.id.date_orderedsub)
          val orderStatus:TextView = itemView.findViewById(R.id.order_statussub)
        val btn_accept_request:Button = itemView.findViewById(R.id.btn_accept_requestsub)
        val btn_complete_request:Button = itemView.findViewById(R.id.btn_complete_requestsub)
        val btn_view_request:Button = itemView.findViewById(R.id.btn_view_requestsub)
        val cardViewrequest:CardView = itemView.findViewById(R.id.cardViewrequestsub)
    }
}