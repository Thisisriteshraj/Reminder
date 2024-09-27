package com.example.reminder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.reminder.databinding.ItemPersonBinding
import com.example.reminder.interfaces.OnItemClickOfUserUserBirthdayScheduled
import com.example.reminder.room.User

class AdapterPersonList(private var list: ArrayList<User>,var onItemClickOfUserUserBirthdayScheduled: OnItemClickOfUserUserBirthdayScheduled) :
    RecyclerView.Adapter<AdapterPersonList.ViewHolder>() {
    class ViewHolder(val binding: ItemPersonBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPersonBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun getItemCount(): Int {

        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.text.text = list[position].name
        holder.binding.hour.text= list[position].hour.toString()
        holder.binding.minute.text= list[position].minute.toString()

        holder.binding.delete.setOnClickListener {
            onItemClickOfUserUserBirthdayScheduled.onItemClick(list[position])

        }

    }
}