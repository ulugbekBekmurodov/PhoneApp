package com.example.contactapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.example.contactapp.R
import com.example.contactapp.databinding.ItemSwipeBinding
import com.example.contactapp.models.User

class RvAdapter(var users:ArrayList<User>,val myInterface: MyInterface) :RecyclerView.Adapter<RvAdapter.Vh>(){
    var isHave = true
    inner class Vh(itemView: View):RecyclerView.ViewHolder(itemView){
        fun onBind(user: User){

            val bind = ItemSwipeBinding.bind(itemView)
            bind.name.text = user.name
            bind.phoneNumber.text = user.number
            bind.tell.setOnClickListener {
                   myInterface.callClick(user)
                isHave = false
            }
            bind.sms.setOnClickListener {
                myInterface.smsClick(user)
                isHave = false
            }
            bind.swipe.setSwipeListener(object :SwipeRevealLayout.SwipeListener{
                override fun onClosed(view: SwipeRevealLayout?) {

                }

                override fun onOpened(view: SwipeRevealLayout?) {

                }

                override fun onSlide(view: SwipeRevealLayout?, slideOffset: Float) {

                }

            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(LayoutInflater.from(parent.context).inflate(R.layout.item_swipe,parent,false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(users[position])
    }

    override fun getItemCount(): Int {
        return users.size
    }
    interface MyInterface{
        fun smsClick(user: User)

        fun callClick(user: User)
    }

}