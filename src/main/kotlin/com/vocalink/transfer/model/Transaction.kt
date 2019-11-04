package com.vocalink.transfer.model

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * @author Dursun Gundogan
 */

data class Transaction(val id: Long, val fromAccount: String, val toAccount: String, val amount: BigDecimal,
                       val currencyCode: String, val reference: String, val transactionTime: LocalDateTime)

data class TransactionResult(val id: Long?, val errorMsg: String)