package com.manishjandu.coinvalidator.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.afollestad.assent.GrantResult
import com.afollestad.assent.Permission
import com.afollestad.assent.askForPermissions
import com.afollestad.assent.isAllGranted
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.android.material.snackbar.Snackbar
import com.manishjandu.coinvalidator.R
import com.manishjandu.coinvalidator.databinding.FragmentQrscannerBinding
import com.manishjandu.coinvalidator.utils.IsCryptoAddressValid
import com.manishjandu.coinvalidator.utils.IsCryptoAddressValid.Valid
import com.manishjandu.coinvalidator.viewmodels.QrScannerViewModel

private const val TAG = "QrScannerFragment"

class QrScannerFragment : Fragment(R.layout.fragment_qrscanner) {
    private var _binding: FragmentQrscannerBinding? = null
    private val binding get() = _binding!!
    private val args: QrScannerFragmentArgs by navArgs()
    private val viewModel: QrScannerViewModel by viewModels()
    private lateinit var codeScanner: CodeScanner
    private lateinit var cryptoType: CryptoType
    private var cryptoAddress: String? = null
    private var isCryptoValid: IsCryptoAddressValid? = null
    private var isScannerSet = false

    override fun onStart() {
        super.onStart()
        checkAndSetCameraPermission()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentQrscannerBinding.bind(view)
        cryptoType = args.cryptoType

        setupButtons()
        setupQrScannerObserver()
        setupCryptoAddressObserver()
        setupValidCryptoObserver()
    }

    private fun setupButtons() {
        binding.apply {
            buttonSettingCameraAccess.setOnClickListener {
                moveToSettingsScreens()
            }
            binding.buttonAddressValidator.setOnClickListener {
                if (!cryptoAddress.isNullOrEmpty() && ::cryptoType.isInitialized) {
                    viewModel.validateCryptoAddress(cryptoType, cryptoAddress!!)
                }
            }
            binding.buttonShare.setOnClickListener {
                shareCryptoAddress()
            }
        }
    }


    private fun setupQrScannerObserver() {
        viewModel.isScannerSet.observe(viewLifecycleOwner){
            it?.let {
                isScannerSet = true
            }
        }
    }

    private fun setupCryptoAddressObserver() {
        viewModel.cryptoAddress.observe(viewLifecycleOwner) {
            it?.let {
                if (it != cryptoAddress) {
                    binding.apply {
                        textViewAddress.text = it
                        textViewAddressIsValid.text = ""
                    }
                    cryptoAddress = it
                    isCryptoValid = null
                    //Todo:vibratePhone()
                }
            }
        }
    }

    private fun setupValidCryptoObserver() {
        viewModel.isAddressValid.observe(viewLifecycleOwner) {
            it?.let {
                isCryptoValid = it
                if (it is Valid) {
                    binding.textViewAddressIsValid.text = "Address is valid"
                } else {
                    binding.textViewAddressIsValid.text = "Address is Invalid"

                }
            }
        }
    }

    private fun shareCryptoAddress() {
        if (isCryptoValid != null) {
            if (isCryptoValid is Valid) {
                shareTextWithOtherApps(cryptoAddress!!)
            } else {
                showErrorSnackBar("Cannot share, address not valid.")
            }
        } else {
            showErrorSnackBar("Validate address then share.")
        }
    }

    private fun shareTextWithOtherApps(text: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, text)
        intent.type = "text/plain"
        startActivity(intent)
    }

    private fun moveToSettingsScreens() {
        //reference: https://stackoverflow.com/questions/32898901/go-to-my-apps-app-permission-screen
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", "com.manishjandu.coinvalidator", null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun setupQrScanner() {
        //reference: https://github.com/yuriy-budiyev/code-scanner
        val scannerView = binding.scanner
        codeScanner = CodeScanner(requireContext(), scannerView)
        codeScanner.scanMode = ScanMode.CONTINUOUS
        codeScanner.decodeCallback = DecodeCallback {
            requireActivity().runOnUiThread {
                setCryptoAddress(it.text)
            }
        }
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
        viewModel.setScanner(true)
    }

    private fun setCryptoAddress(address: String) {
        viewModel.setCryptoAddress(address)
    }

    private fun showErrorSnackBar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    private fun checkAndSetCameraPermission() {
        if (hasCameraPermission()) {
            setViewHasCameraPermission()
            if(!isScannerSet){
                setupQrScanner()
            }
        } else {
            setViewNoCameraPermission()
            setCameraPermission()
        }
    }

    private fun hasCameraPermission(): Boolean {
        return isAllGranted(Permission.CAMERA)
    }

    private fun setCameraPermission() {
        askForPermissions(Permission.CAMERA) { result ->
            when {
                result[Permission.CAMERA] == GrantResult.GRANTED -> {
                    setViewHasCameraPermission()
                    setupQrScanner()
                }
                result[Permission.CAMERA] == GrantResult.DENIED -> {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Camera Permission is required to scan Qrcode.")
                        .setPositiveButton("Ok") { _, _ ->
                            checkAndSetCameraPermission()
                        }.setNegativeButton("Cancel") { _, _ ->
                            findNavController().navigateUp()
                        }.create()
                        .show()
                }
                else -> {
                    setViewNoCameraPermission()
                }
            }
        }
    }

    private fun setViewNoCameraPermission() {
        binding.apply {
            groupHasCameraPermission.visibility = View.GONE
            groupNoCameraPermission.visibility = View.VISIBLE
        }
    }

    private fun setViewHasCameraPermission() {
        binding.apply {
            groupHasCameraPermission.visibility = View.VISIBLE
            groupNoCameraPermission.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        if (::codeScanner.isInitialized) {
            codeScanner.startPreview()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::codeScanner.isInitialized) {
            codeScanner.releaseResources()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

enum class CryptoType {
    Bitcoin,
    Ethereum
}