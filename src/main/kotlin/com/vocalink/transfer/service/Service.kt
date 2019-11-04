package com.vocalink.transfer.service

import com.vocalink.transfer.model.Transaction
import java.util.*
import java.util.concurrent.CompletionStage

interface Service {
    fun processTransaction(tx: Transaction): CompletionStage<Optional<String>>
}