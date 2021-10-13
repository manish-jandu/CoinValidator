package com.manishjandu.coinvalidator.utils

sealed class IsCryptoAddressValid {
    class Valid : IsCryptoAddressValid()
    class Invalid(var message: String = "address is invalid.") : IsCryptoAddressValid()
}