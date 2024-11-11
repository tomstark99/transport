package com.android.transport2.ui.main

import android.graphics.drawable.TransitionDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.Constraints.LayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.android.transport2.databinding.ElementPlaceholderBinding
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import kotlin.jvm.internal.Intrinsics.Kotlin

class PlaceholderAdapter : RecyclerView.Adapter<PlaceholderAdapter.PlaceholderViewHolder>(){

    private var placeholder_count = 5
    private var disposable: Disposable? = null
    private fun getRandomHeight(min: Float, max: Float): Int = (min.toInt()..max.toInt()).random()

    fun clear() {
        placeholder_count = 0
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: PlaceholderViewHolder, position: Int) {
        with(holder) {
            val density = binding.placeholder.context.resources.displayMetrics.density
            val layoutParams = binding.placeholder.layoutParams
            layoutParams.height = getRandomHeight(90*density, 140*density)
            binding.placeholder.layoutParams = layoutParams
            val i = (position + 1) * 200
            val placeholder = binding.placeholder.background as TransitionDrawable
            placeholder.isCrossFadeEnabled = true
            disposable = Observable.interval(i.toLong(),3000, TimeUnit.MILLISECONDS)
                .repeat()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    placeholder.startTransition(500)
                    Observable.timer(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                        .map{
                            placeholder.reverseTransition(500)
                        }
                        .subscribeOn(Schedulers.io())
                        .subscribe()
                }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceholderViewHolder {
        return PlaceholderViewHolder(ElementPlaceholderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    fun stopAnimation(){
        if(disposable != null) disposable!!.dispose()
    }

    override fun getItemCount(): Int {
        return placeholder_count
    }

    inner class PlaceholderViewHolder(val binding: ElementPlaceholderBinding) : RecyclerView.ViewHolder(binding.root)
}