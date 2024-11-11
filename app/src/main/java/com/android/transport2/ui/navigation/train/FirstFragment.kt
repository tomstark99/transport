package com.android.transport2.ui.navigation.train

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.android.transport2.R
import com.android.transport2.arch.android.BaseFragment
import com.android.transport2.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : BaseFragment<TrainMvp.Presenter>(), TrainMvp.View {

    companion object {
        fun newInstance() : FirstFragment {
            return FirstFragment()
        }
    }

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = TrainPresenter(this)
        presenter.onCreate()
    }

    override fun load() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun finish() {

    }
}