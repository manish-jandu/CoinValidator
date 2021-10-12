package com.manishjandu.coinvalidator.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.afollestad.assent.GrantResult
import com.afollestad.assent.Permission
import com.afollestad.assent.askForPermissions
import com.afollestad.assent.isAllGranted
import com.manishjandu.coinvalidator.R
import com.manishjandu.coinvalidator.databinding.FragmentQrscannerBinding

private const val TAG = "QrScannerFragment"

class QrScannerFragment : Fragment(R.layout.fragment_qrscanner) {
    private var _binding: FragmentQrscannerBinding? = null
    private val binding get() = _binding!!
    private val args: QrScannerFragmentArgs by navArgs()

    override fun onStart() {
        super.onStart()
        checkAndSetCameraPermission()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentQrscannerBinding.bind(view)

        val cryptoType = args.cryptoType
        setupButtons()
    }

    private fun setupButtons() {
        binding.apply {
            buttonSettingCameraAccess.setOnClickListener {
                moveToSettingsScreens()
            }
        }
    }

    private fun moveToSettingsScreens() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", "com.manishjandu.coinvalidator", null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun checkAndSetCameraPermission() {
        if (hasCameraPermission()) {
            //Todo:camera permission success
            setViewHasCameraPermission()
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
                    //Todo:camera permission success
                    setViewHasCameraPermission()
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
                    //Todo:Permanently denied
                    setViewNoCameraPermission()
                }
            }
        }
    }

    private fun setViewNoCameraPermission(){
        binding.apply {
            groupHasCameraPermission.visibility = View.GONE
            groupNoCameraPermission.visibility = View.VISIBLE
        }
    }

    private fun setViewHasCameraPermission(){
        binding.apply {
            groupHasCameraPermission.visibility = View.VISIBLE
            groupNoCameraPermission.visibility = View.GONE
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