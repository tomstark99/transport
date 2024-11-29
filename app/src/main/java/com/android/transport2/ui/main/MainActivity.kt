package com.android.transport2.ui.main

import android.os.Bundle
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.android.transport2.R
import com.android.transport2.arch.android.BaseActivity
import com.android.transport2.arch.android.BaseFragment
import com.android.transport2.arch.utils.Utils
import com.android.transport2.databinding.ActivityMainBinding
import com.android.transport2.ui.navigation.commute.MiddleFragment
import com.android.transport2.ui.navigation.train.FirstFragment
import com.android.transport2.ui.navigation.tube.SecondFragment

class MainActivity : BaseActivity<MainMvp.Presenter>(), MainMvp.View {

    private var fragmentMap = HashMap<String, BaseFragment<*>>()
    private var activeFragment = TAG_TRAIN_FRAGMENT
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.toolbar.title = "Good ${Utils.getContextTimeString()}!" //getString(R.string.nav_home)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        presenter = MainPresenter(this)
        if (savedInstanceState == null) {
            presenter.onCreate()
            activeFragment = TAG_TRAIN_FRAGMENT
            binding.navigation.selectedItemId = R.id.menu_train
        } else {
            presenter.onReload()
        }

//        val navController = findNavController(R.id.activity_mai)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)
//

//        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAnchorView(R.id.fab)
//                .setAction("Action", null).show()
//        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = binding.navigation.findNavController()
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun addFragments() {
        supportFragmentManager.beginTransaction()
            .add(R.id.frame_layout, trainFragment(), TAG_TRAIN_FRAGMENT)
//            .add(R.id.frame_layout, commuteFragment(), TAG_COMMUTE_FRAGMENT)
            .add(R.id.frame_layout, tubeFragment(), TAG_TUBE_FRAGMENT)
            .show(trainFragment())
            .hide(tubeFragment())
            .commit()
    }

    override fun reAddFragments() {
        supportFragmentManager.beginTransaction()
            .add(R.id.frame_layout, trainFragment(), TAG_TRAIN_FRAGMENT)
//            .add(R.id.frame_layout, commuteFragment(), TAG_COMMUTE_FRAGMENT)
            .add(R.id.frame_layout, tubeFragment(), TAG_TUBE_FRAGMENT)
            .hide(trainFragment())
            .hide(tubeFragment())
            .commit()
    }

    override fun loadTabs() {
        @Suppress("DEPRECATION")
        binding.navigation.setOnNavigationItemSelectedListener { item ->
            val fragment: Fragment?
            when (item.itemId) {
                R.id.menu_train -> {
                    fragment = trainFragment()
                    swapFragment(fragmentMap[activeFragment]!!, fragment)
                    binding.toolbar.title = "Good ${Utils.getContextTimeString()}!"// getString(R.string.nav_train)
                    activeFragment = TAG_TRAIN_FRAGMENT
                }
//                R.id.menu_commute -> {
//                    fragment = commuteFragment()
//                    swapFragment(fragmentMap[activeFragment]!!, fragment)
//                    binding.toolbar.title = "Good ${Utils.getContextTimeString()}!"// getString(R.string.nav_train)
//                    activeFragment = TAG_COMMUTE_FRAGMENT
//                }
                R.id.menu_tube -> {
                    fragment = tubeFragment()
                    swapFragment(fragmentMap[activeFragment]!!, fragment)
                    binding.toolbar.title = getString(R.string.nav_tube)
                    activeFragment = TAG_TUBE_FRAGMENT
                }
            }
//            if (fragment != null) swapFragment(fragment)
            true
        }
    }

    private fun swapFragment(from: Fragment, to: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .hide(from)
            .show(to)
            .commit()
    }

    private fun trainFragment(): Fragment {
        var fragment = fragmentMap[TAG_TRAIN_FRAGMENT]
        if (fragment == null) {
            fragment = FirstFragment.newInstance()
            fragmentMap[TAG_TRAIN_FRAGMENT] = fragment
        }
        return fragment
    }

//    private fun commuteFragment(): Fragment {
//        var fragment = fragmentMap[TAG_COMMUTE_FRAGMENT]
//        if (fragment == null) {
//            fragment = MiddleFragment.newInstance()
//            fragmentMap[TAG_COMMUTE_FRAGMENT] = fragment
//        }
//        return fragment
//    }

    private fun tubeFragment(): Fragment {
        var fragment = fragmentMap[TAG_TUBE_FRAGMENT]
        if (fragment == null) {
            fragment = SecondFragment.newInstance()
            fragmentMap[TAG_TUBE_FRAGMENT] = fragment
        }
        return fragment
    }

    companion object {
        private const val TAG_TRAIN_FRAGMENT = "train"
        private const val TAG_TUBE_FRAGMENT = "tube"
        private const val TAG_COMMUTE_FRAGMENT = "commute"
    }

}