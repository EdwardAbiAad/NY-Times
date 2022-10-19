package com.example.nytimes.general

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class BaseRecyclerViewAdapter<T>(
    private val layoutId: Int,
    private val recyclable: Boolean = false,
    private val bindView: ((position: Int, data: T, View) -> Unit),
) : RecyclerView.Adapter<BaseRecyclerViewAdapter<T>.BaseViewHolder>() {

    private val items = ArrayList<T>()
    fun setItems(items: List<T>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        BaseViewHolder(
            LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        )

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.setIsRecyclable(recyclable)
        holder.bind(position, items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int, item: T) = bindView.invoke(position, item, itemView)
    }
}