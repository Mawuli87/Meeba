package com.aisisabeem.Meeba.home.riders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aisisabeem.Meeba.R
import com.example.firestorekotlin.User


class MyAdapter(private val userList : ArrayList<User>)
    : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item,
        parent,false)

        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val user : User = userList[position]
        holder.firstName.text = user.firstName
        holder.lastName.text = user.lastName
        holder.age.text = user.age.toString()


    }

    override fun getItemCount(): Int {

       return userList.size

    }

    public class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val firstName : TextView = itemView.findViewById(R.id.tvfirstName)
        val lastName : TextView = itemView.findViewById(R.id.tvlastName)
        val age : TextView = itemView.findViewById(R.id.tvage)

    }
}