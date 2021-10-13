package com.manishjandu.coinvalidator.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.manishjandu.coinvalidator.ui.CryptoType
import com.manishjandu.coinvalidator.util.getOrAwaitValueTest
import com.manishjandu.coinvalidator.utils.IsCryptoAddressValid
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class QrScannerViewModelTest {
    private lateinit var viewModel: QrScannerViewModel

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        viewModel = QrScannerViewModel()
    }

    @Test
    fun `set crypto address return true`() {
        val address = "This is a just simple address"
        viewModel.setCryptoAddress(address)
        assertThat(viewModel.cryptoAddress.value).isEqualTo(address)
    }

    //Bitcoin address validate
    @Test
    fun `validate bitcoin short length,set address Invalid`() {
        val cryptoType = CryptoType.Bitcoin
        val address = "13245sadas"
        viewModel.validateCryptoAddress(cryptoType, address)

        val result = viewModel.isAddressValid.getOrAwaitValueTest()
        assertThat(result is IsCryptoAddressValid.Invalid).isTrue()
    }

    @Test
    fun `validate bitcoin long length,set address Invalid`(){
        val cryptoType = CryptoType.Bitcoin
        val address = "13245sadasasdfihdfbaibsdbauidbeidsafuasdfbadf"
        viewModel.validateCryptoAddress(cryptoType, address)

        val result = viewModel.isAddressValid.getOrAwaitValueTest()
        assertThat(result is IsCryptoAddressValid.Invalid).isTrue()
    }

    @Test
    fun `validate bitcoin doestn't start with 1,set address Invalid`(){
        val cryptoType = CryptoType.Bitcoin
        val address = "3245sadasasdfihdfbaibsdbauidb"
        viewModel.validateCryptoAddress(cryptoType, address)

        val result = viewModel.isAddressValid.getOrAwaitValueTest()
        assertThat(result is IsCryptoAddressValid.Invalid).isTrue()
    }

    @Test
    fun `validate bitcoin contain 0(zro),O(capital o),set address Invalid`(){
        val cryptoType = CryptoType.Bitcoin
        val address = "13245sadasasdf0hdfbaibOdbauidb"
        viewModel.validateCryptoAddress(cryptoType, address)

        val result = viewModel.isAddressValid.getOrAwaitValueTest()
        assertThat(result is IsCryptoAddressValid.Invalid).isTrue()
    }

    @Test
    fun `validate bitcoin contain l(small l),I(capital i),set address Invalid`(){
        val cryptoType = CryptoType.Bitcoin
        val address = "13245sadasasdflhdfbaibIdbauidb"
        viewModel.validateCryptoAddress(cryptoType, address)

        val result = viewModel.isAddressValid.getOrAwaitValueTest()
        assertThat(result is IsCryptoAddressValid.Invalid).isTrue()
    }

    @Test
    fun `validate bitcoin valid address`(){
        val cryptoType = CryptoType.Bitcoin
        val address = "13245sadasasdfhdfbaibewdbauidb"
        viewModel.validateCryptoAddress(cryptoType, address)

        val result = viewModel.isAddressValid.getOrAwaitValueTest()
        assertThat(result is IsCryptoAddressValid.Valid).isTrue()
    }

    //Ethereum address Validate
    @Test
    fun `validate ethereum empty address,set address Invalid`() {
        val cryptoType = CryptoType.Ethereum
        val address = ""
        viewModel.validateCryptoAddress(cryptoType, address)

        val result = viewModel.isAddressValid.getOrAwaitValueTest()
        assertThat(result is IsCryptoAddressValid.Invalid).isTrue()
    }

    @Test
    fun `validate ethereum address not begin with 0x,set address Invalid`() {
        val cryptoType = CryptoType.Ethereum
        val address = "abcdef231313212"
        viewModel.validateCryptoAddress(cryptoType, address)

        val result = viewModel.isAddressValid.getOrAwaitValueTest()
        assertThat(result is IsCryptoAddressValid.Invalid).isTrue()
    }

    @Test
    fun `validate ethereum contains m ,set address Invalid`() {
        val cryptoType = CryptoType.Ethereum
        val address = "0xabcdef325434523m"
        viewModel.validateCryptoAddress(cryptoType, address)

        val result = viewModel.isAddressValid.getOrAwaitValueTest()
        assertThat(result is IsCryptoAddressValid.Invalid).isTrue()
    }

    @Test
    fun `validate ethereum contains L ,set address Invalid`() {
        val cryptoType = CryptoType.Ethereum
        val address = "0xabcdef32L5434523"
        viewModel.validateCryptoAddress(cryptoType, address)

        val result = viewModel.isAddressValid.getOrAwaitValueTest()
        assertThat(result is IsCryptoAddressValid.Invalid).isTrue()
    }

    @Test
    fun `validate ethereum ,valid address`() {
        val cryptoType = CryptoType.Ethereum
        val address = "0xabcdef234342"
        viewModel.validateCryptoAddress(cryptoType, address)

        val result = viewModel.isAddressValid.getOrAwaitValueTest()
        assertThat(result is IsCryptoAddressValid.Valid).isTrue()
    }
}