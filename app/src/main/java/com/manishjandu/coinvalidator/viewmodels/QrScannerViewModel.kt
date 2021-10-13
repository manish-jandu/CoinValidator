package com.manishjandu.coinvalidator.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.manishjandu.coinvalidator.ui.CryptoType
import com.manishjandu.coinvalidator.utils.IsCryptoAddressValid

class QrScannerViewModel : ViewModel() {

    private val _cryptoAddress = MutableLiveData<String>()
    val cryptoAddress: LiveData<String> = _cryptoAddress

    private val _isAddressValid = MutableLiveData<IsCryptoAddressValid>()
    val isAddressValid: LiveData<IsCryptoAddressValid> = _isAddressValid

    fun setCryptoAddress(address: String) {
        _cryptoAddress.value = address
    }

    fun validateCryptoAddress(cryptoType: CryptoType, cryptoAddress: String) {
        when (cryptoType) {
            CryptoType.Bitcoin -> {
                validateBitcoinAddress(cryptoAddress)
            }
            CryptoType.Ethereum -> {
                validateEthereumAddress(cryptoAddress)
            }
        }
    }

    private fun validateEthereumAddress(cryptoAddress: String) {
        if (cryptoAddress.isEmpty() || cryptoAddress[0] != '0' || cryptoAddress[1] != 'x') {
            _isAddressValid.value = IsCryptoAddressValid.Invalid()
        } else {
            val map = getValidatorEtherMap()
            for (index in 2 until cryptoAddress.length) {
                if (!map.contains(cryptoAddress[index])) {
                    _isAddressValid.value = IsCryptoAddressValid.Invalid()
                    return
                }
            }
            _isAddressValid.value = IsCryptoAddressValid.Valid()
        }
    }

    private fun getValidatorEtherMap(): HashSet<Char> {
        val map = HashSet<Char>()
        val etherListChecker = arrayListOf(
            'a', 'b', 'c', 'd', 'e', 'f', '0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9'
        )
        for (character in etherListChecker) {
            map.add(character)
        }
        return map
    }

    private fun validateBitcoinAddress(cryptoAddress: String) {
        if (cryptoAddress.length < 25 || cryptoAddress.length > 34 || cryptoAddress[0] != '1') {
            _isAddressValid.value = IsCryptoAddressValid.Invalid()
        } else {
            val exceptionMap = getValidatorBitcoinMap()
            for (character in cryptoAddress) {
                if (!character.isLetterOrDigit() || exceptionMap.contains(character)) {
                    _isAddressValid.value = IsCryptoAddressValid.Invalid()
                    return
                }
            }
            _isAddressValid.value = IsCryptoAddressValid.Valid()
        }
    }

    private fun getValidatorBitcoinMap(): HashSet<Char> {
        val map = HashSet<Char>()
        val bitcoinCheckerList = arrayListOf('0', 'O', 'l', 'I')
        for (character in bitcoinCheckerList) {
            map.add(character)
        }
        return map
    }

}
