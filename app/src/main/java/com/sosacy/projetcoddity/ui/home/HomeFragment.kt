package com.sosacy.projetcoddity.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sosacy.projetcoddity.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

private var _binding: FragmentHomeBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {


      val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
    Log.d("debug","debug1")
    _binding = FragmentHomeBinding.inflate(inflater, container, false)
    val root: View = binding.root
      Log.d("debug","3")


      return root
  }

override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}