package com.example.snapstock1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

data class Writer(val name: String, val avatarUrl: String?)

class TopWritersAdapter(private val writers: List<Writer>) :
    RecyclerView.Adapter<TopWritersAdapter.WriterViewHolder>() {

    inner class WriterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatarImageView: ImageView = view.findViewById(R.id.writerAvatar)
        val nameTextView: TextView = view.findViewById(R.id.writerName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WriterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_writer, parent, false)
        return WriterViewHolder(view)
    }

    override fun onBindViewHolder(holder: WriterViewHolder, position: Int) {
        val writer = writers[position]
        holder.nameTextView.text = writer.name
        Glide.with(holder.itemView.context)
            .load(writer.avatarUrl)
            .placeholder(R.drawable.ic_default_avatar)
            .into(holder.avatarImageView)
    }

    override fun getItemCount() = writers.size
}