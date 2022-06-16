package com.sosacy.projetcoddity.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.sosacy.projetcoddity.databinding.FragmentDashboardBinding
import com.sosacy.projetcoddity.ui.adapter.VPAdapter

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var viewPager : ViewPager2
    private lateinit var tabs: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this)[DashboardViewModel::class.java]

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        tabs = binding.tabs
        viewPager = binding.viewPager
        setUpTap()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpTap(){
        val adapter = VPAdapter(this.requireActivity())
        adapter.addFragment(ToThrowFragment(),"Garbage to validate")
        adapter.addFragment(HistoryFragment(),"History")

        viewPager.adapter = adapter
        TabLayoutMediator(tabs,viewPager){
             tab, index ->
            tab.text = when(index){
                0 -> "Garbage to throw"
                1 -> "History"
                else -> "Error"
            }
        }.attach()
    }
}