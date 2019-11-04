package com.vocalink.transfer.processor

import com.vocalink.transfer.model.Transaction
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

/**
 * @author Dursun Gundogan
 */

class TransactionProcessor {

    fun process(tx: Transaction): CompletionStage<Optional<String>> {
        return CompletableFuture.supplyAsync {
            var result = Optional.empty<String>()
            when {
                tx.amount.signum() <= 0 -> result = Optional . of ("Amount cannot be zero")
            }
            result
        }
    }
}
