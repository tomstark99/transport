package com.android.transport2.ui.navigation.tube.line

import android.app.Activity
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.android.transport2.arch.android.Load
import com.android.transport2.arch.managers.TubeManager.TubeLine
import com.android.transport2.arch.models.TubeStop
import com.android.transport2.databinding.ElementLineBinding
import com.android.transport2.ui.navigation.tube.TubeAdapter
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class LineAdapter(private val context: Activity, private val load: Load) : RecyclerView.Adapter<LineAdapter.LineViewHolder>() {

    private var stops = mutableListOf<TubeStop>()

    sealed class Status{
        object Normal: Status()
        object Loading: Status()
        object Error: Status()
    }

    private var status: Status = Status.Loading

    inner class LineViewHolder(val binding: ElementLineBinding, var disposable: Disposable? = null) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineViewHolder {
        return LineViewHolder(ElementLineBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: LineViewHolder, position: Int) {
        when(status) {
            is Status.Normal -> {
                with(holder) {
                    with(stops[position]) {
                        val drawable = binding.tubeStatusElement.background as GradientDrawable
                        drawable.setColor(context.getColor(origin.color))
                        binding.tubeStatusElement.background = drawable
                        binding.stopText.text = name
                        val additionalInfo = """Zone ${additionalProperties!!["Zone"]}""" +
                                if (additionalProperties["Escalators"].isNullOrEmpty() || additionalProperties["Escalators"] == "0") "    " else """    ${additionalProperties["Escalators"]} Escalators""" +
                                if (additionalProperties["Toilets"] == "    Yes") "Toilets" else "    "
                        binding.infoText.text = additionalInfo
                        val layoutManager = FlexboxLayoutManager(context, FlexDirection.ROW)
                        layoutManager.justifyContent = JustifyContent.FLEX_START
                        binding.connectingLinesList.layoutManager = layoutManager
                        binding.connectingLinesList.itemAnimator = DefaultItemAnimator()
                        binding.connectingLinesList.adapter = ConnectionAdapter(context).apply {
                            showConnection(lines.map { it })
                        }
                    }
                }
            }
            is Status.Loading -> {

            }
            is Status.Error -> {

            }
        }

    }

    override fun onViewAttachedToWindow(holder: LineAdapter.LineViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.disposable?.dispose()
        holder.disposable = RxView.clicks(holder.binding.tubeStatusElement)
            .throttleFirst(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                with(stops[holder.adapterPosition]) {
                    load.onStationClicked(origin, this)
                }
//                val line = stringToTubeLine(lines[holder.adapterPosition].name)
//                load.onLineClicked(line!!)
            }
    }

    fun showStops(stops: List<TubeStop>){
        this.stops = stops.toMutableList()
        status = Status.Normal
        notifyDataSetChanged()
    }

    fun showError(){
        Status.Error
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return stops.size
    }
}