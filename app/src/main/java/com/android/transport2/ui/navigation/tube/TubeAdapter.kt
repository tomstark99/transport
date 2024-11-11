package com.android.transport2.ui.navigation.tube

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.graphics.toColorLong
import androidx.recyclerview.widget.RecyclerView
import com.android.transport2.R
import com.android.transport2.arch.android.Load
import com.android.transport2.arch.models.Tube
import com.android.transport2.databinding.ElementTubeBinding
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class TubeAdapter(var context: Activity, private val load: Load) : RecyclerView.Adapter<TubeAdapter.TubeViewHolder>() {

    var lines = mutableListOf<Tube>()

    sealed class Status{
        object Normal: Status()
        object Loading: Status()
        object Error: Status()
    }

    private var status: Status = Status.Loading

    inner class TubeViewHolder(val binding: ElementTubeBinding, var disposable: Disposable? = null) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TubeViewHolder {
        return TubeViewHolder(ElementTubeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: TubeViewHolder, position: Int) {
        when(status) {
            is Status.Normal -> {
                with(holder) {
                    with(lines[position]) {
                        Log.d("TubeAdapter", "colour: $colour")
                        val drawableElement = binding.tubeElement.background as GradientDrawable
                        drawableElement.setColor(context.getColor(colour))
                        binding.tubeElement.background = drawableElement
//                        binding.tubeLineText.typeface = Typeface.createFromAsset(context.assets, "font/GoogleSans-Bold.ttf")
                        binding.tubeLineText.text = if (name.contains("overground", ignoreCase = true)) name else """$name line"""
//                        binding.tubeStatusText.typeface = Typeface.createFromAsset(context.assets, "font/GoogleSans-Bold.ttf")
                        binding.tubeStatusText.text = status
//                        if(status != "Good Service"){
//                            binding.tubeStatusText.text = status
//                        } else {
//                            binding.tubeStatusText.visibility = View.GONE
//                        }
                        if (reason.isEmpty() || reason == "Good service, No issues reported") {
                            binding.tubeReason.visibility = View.GONE
                        } else {
                            binding.tubeReason.text = reason.split(":").last().dropWhile { it.isWhitespace() }
                        }
                        setStatusDot(statusSeverityId, binding)
                    }
                }
            }
            is Status.Loading -> {

            }
            is Status.Error -> {

            }
        }

    }

    override fun onViewAttachedToWindow(holder: TubeViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.disposable?.let { disposable -> disposable.dispose() }
        holder.disposable = RxView.clicks(holder.binding.tubeElement)
            .throttleFirst(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                with(lines[holder.adapterPosition]) {
                    load.onLineClicked(id)
                }
//                val line = stringToTubeLine(lines[holder.adapterPosition].name)
//                load.onLineClicked(line!!)
            }
    }

    fun showTube(lines: List<Tube>){
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

    private fun setStatusDot(id: Int, binding: ElementTubeBinding){
        val drawableStatusElement = binding.tubeStatusElement.background as GradientDrawable
        drawableStatusElement.setColor(context.getColor(Severity.values()[id].colour.background))
        binding.tubeStatusElement.background = drawableStatusElement
        binding.tubeStatusText.setTextColor(context.getColor(Severity.values()[id].colour.text))
        binding.transportStatusDot.setImageResource(Severity.values()[id].colour.dot)
    }

    private enum class Colour(id: Int, @DrawableRes val dot: Int, @ColorRes val text: Int, @ColorRes val background: Int){
        RED(0, R.drawable.ic_major_disruption, R.color.red, R.color.red_30),
        ORANGE(1, R.drawable.ic_minor_disruption, R.color.orange, R.color.orange_30),
        GREEN(2, R.drawable.ic_no_disruption, R.color.green, R.color.green_30),
        GREY(3, R.drawable.ic_dot, R.color.grey, R.color.grey_30)
    }

    private enum class Severity(id: Int, val colour: Colour){
        SPECIAL_SERVICE(0, Colour.ORANGE),
        CLOSED(1, Colour.RED),
        SUSPENDED(2, Colour.RED),
        PART_SUSPENDED(3, Colour.ORANGE),
        PLANNED_CLOSURE(4, Colour.RED),
        PART_CLOSURE(5, Colour.ORANGE),
        SEVERE_DELAYS(6, Colour.RED),
        REDUCED_SERVICE(7, Colour.ORANGE),
        BUS_SERVICE(8, Colour.ORANGE),
        MINOR_DELAYS(9, Colour.ORANGE),
        GOOD_SERVICE(10, Colour.GREEN),
        PART_CLOSED(11, Colour.ORANGE),
        EXIT_ONLY(12, Colour.ORANGE),
        NO_STEP_FREE_ACCESS(13, Colour.ORANGE),
        CHANGE_OF_FREQUENCY(14, Colour.ORANGE),
        DIVERTED(15, Colour.ORANGE),
        NOT_RUNNING(16, Colour.GREY),
        ISSUES_REPORTED(17, Colour.ORANGE),
        NO_ISSUES(18, Colour.GREEN),
        INFORMATION(19, Colour.GREY),
        SERVICE_CLOSED(20, Colour.GREY)
    }

}
