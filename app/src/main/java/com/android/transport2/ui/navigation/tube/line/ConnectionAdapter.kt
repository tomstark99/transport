package com.android.transport2.ui.navigation.tube.line

import android.app.Activity
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.transport2.arch.managers.TubeManager.TubeLine
import com.android.transport2.databinding.ElementConnectionBinding

class ConnectionAdapter(private val context: Activity) : RecyclerView.Adapter<ConnectionAdapter.ConnectionViewHolder>() {

    private var lines = mutableListOf<TubeLine>()

    sealed class Status{
        object Normal: Status()
        object Loading: Status()
        object Error: Status()
    }

    private var status: Status = Status.Loading

    inner class ConnectionViewHolder(val binding: ElementConnectionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConnectionViewHolder {
        return ConnectionViewHolder(ElementConnectionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ConnectionViewHolder, position: Int) {
        when(status) {
            is Status.Normal -> {
                with(holder) {
                    with(lines[position]) {
                        val drawable = binding.connectionText.background as GradientDrawable
                        drawable.setColor(context.getColor(color))
                        binding.connectionText.background = drawable
                        binding.connectionText.text = context.getString(commonName)
                    }
                }
            }
            is Status.Loading -> {

            }
            is Status.Error -> {

            }
        }

    }

    fun showConnection(lines: List<TubeLine>){
        this.lines = lines.toMutableList()
        status = Status.Normal
        notifyDataSetChanged()
    }

    fun showError(){
        Status.Error
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return lines.size
    }
}