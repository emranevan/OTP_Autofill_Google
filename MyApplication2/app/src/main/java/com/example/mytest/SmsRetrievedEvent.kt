package com.example.mytest

data class SmsRetrievedEvent(
    val isTimeout: Boolean,
    val smsMessage: String?
)