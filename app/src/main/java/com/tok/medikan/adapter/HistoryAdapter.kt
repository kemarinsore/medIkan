package com.tok.medikan.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tok.medikan.data.history.HistoryItem
import com.tok.medikan.databinding.ItemHistoryBinding
import com.tok.medikan.ui.DetailHistoryActivity

class HistoryAdapter(var dataList: List<HistoryItem>): RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            val data = dataList.get(position)
            binding.apply {
                txtTitle.text = data.historyName.replace("\"", "")
                Glide.with(itemView.context)
                    .load(data.image)
                    .centerCrop()
                    .into(imgHistory)

                layout.setOnClickListener {
                    val dataDisease = data.diseaseItem
                    itemView.context.startActivity(
                        Intent(
                            itemView.context,
                            DetailHistoryActivity::class.java
                        )
                            .putExtra("historyName", data.historyName.replace("\"", ""))
                            .putExtra("image", data.image)
                            .putExtra("createdAt", data.createdAt)
                            .putExtra("name", dataDisease.name)
                            .putExtra("description", dataDisease.description)
                            .putExtra("treatment", dataDisease.treatment)
                    )
                }
            }
        }
    }

    public fun filter(filterList: List<HistoryItem>) {
        dataList = filterList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}