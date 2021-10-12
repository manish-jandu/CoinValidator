package com.manishjandu.coinvalidator.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.manishjandu.coinvalidator.R
import com.manishjandu.coinvalidator.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        binding.apply {
            buttonBitcoinValidator.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToQrScannerFragment(CryptoType.Bitcoin)
                findNavController().navigate(action)
            }
            buttonEthereumValidator.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToQrScannerFragment(CryptoType.Ethereum)
                findNavController().navigate(action)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}