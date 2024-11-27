package com.android.transport2.ui.navigation.train

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.android.transport2.arch.android.Load
import com.android.transport2.arch.models.TubeStop
import com.android.transport2.arch.utils.Utils.getGradientDrawable
import com.android.transport2.databinding.ElementLineHomeBinding
import com.android.transport2.ui.navigation.tube.line.ConnectionAdapter
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class HomeLineAdapter(private val context: Activity, private val load: Load) : RecyclerView.Adapter<HomeLineAdapter.LineViewHolder>() {

    private var stops = mutableListOf<TubeStop>()
    private var initialRadius: Float = Float.MIN_VALUE

    sealed class Status{
        object Normal: Status()
        object Loading: Status()
        object Error: Status()
    }

    private var status: Status = Status.Loading

    inner class LineViewHolder(val binding: ElementLineHomeBinding, var disposable: Disposable? = null) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineViewHolder {
        return LineViewHolder(ElementLineHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("WrongConstant")
    override fun onBindViewHolder(holder: LineViewHolder, position: Int) {
        when(status) {
            is Status.Normal -> {
                with(holder) {
                    with(stops[position]) {
                        if (initialRadius == Float.MIN_VALUE) {
                            initialRadius = (binding.tubeStatusHomeElement.background as GradientDrawable).cornerRadius
                        }
                        binding.tubeStatusHomeElement.background = getGradientDrawable(context, initialRadius, lines.map { it.color })
//                        if (lines.size > 1) {
//                            val colours = lines.map { context.getColor(it.color) }.toIntArray()
//                            val gradientDrawable = GradientDrawable(
//
//                                GradientDrawable.Orientation.LEFT_RIGHT,
//                                colours)
//                            gradientDrawable.cornerRadius = context.
//                            binding.tubeStatusHomeElement.background = gradientDrawable
//                        } else {
//                            val drawable = binding.tubeStatusHomeElement.background as GradientDrawable
//                            drawable.setColor(context.getColor(origin.color))
//                            binding.tubeStatusHomeElement.background = drawable
////                            drawable.colors = null
//                        }
                        binding.stopText.text = name
                        val additionalInfo = """Zone ${additionalProperties!!["Zone"]}""" +
                                if (additionalProperties["Escalators"].isNullOrEmpty() || additionalProperties["Escalators"] == "0") "    " else """    ${additionalProperties["Escalators"]} Escalators""" +
                                if (additionalProperties["Toilets"] == "    Yes") "Toilets" else "    "
                        holder.binding.infoText.text = additionalInfo
                        val layoutManager = FlexboxLayoutManager(context, FlexDirection.ROW)
                        layoutManager.justifyContent = JustifyContent.FLEX_START
                        holder.binding.connectingLinesList.layoutManager = layoutManager
                        holder.binding.connectingLinesList.itemAnimator = DefaultItemAnimator()
                        holder.binding.connectingLinesList.adapter = ConnectionAdapter(context).apply {
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

    override fun onViewAttachedToWindow(holder: LineViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.disposable?.dispose()
        holder.disposable = RxView.clicks(holder.binding.tubeStatusHomeElement)
            .throttleFirst(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                with(stops[holder.adapterPosition]) {
                    load.onStationClicked(origin, this)
                }
//                val line = stringToTubeLine(name)
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