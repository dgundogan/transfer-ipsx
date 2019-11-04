package com.vocalink.transfer.service

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.javadsl.Flow
import akka.stream.javadsl.Keep
import akka.stream.javadsl.Sink
import akka.stream.javadsl.Source
import com.vocalink.transfer.model.Transaction
import com.vocalink.transfer.model.TransactionResult
import com.vocalink.transfer.processor.TransactionProcessor
import java.util.*
import java.util.concurrent.CompletionStage
import java.util.function.Function
import org.slf4j.LoggerFactory
import java.util.Optional.*

/**
 * @author Dursun Gundogan
 */


class ServiceImpl(private val system: ActorSystem, private val processor: TransactionProcessor) : Service {

    private val logger = LoggerFactory.getLogger(ServiceImpl::class.java)

    override fun processTransaction(tx: Transaction): CompletionStage<Optional<String>> {

        return Source.single(tx)
                .via(computeTransaction)
                .runWith(endTransaction, Materializer.createMaterializer(system))
                .whenComplete { done, ex ->
                    if (done != null) {
                        logger.info("ProcessTransaction finished")
                    } else {
                        logger.error("ProcessTransaction failed :", ex.message)
                    }
                }.thenApply { t -> of(t.errorMsg)}
    }

    private val computeTransaction: Flow<Transaction, TransactionResult, NotUsed> =
            Flow.of(Transaction::class.java)
                    .mapAsync<TransactionResult>(5, this::execute)


    private val endTransaction: Sink<TransactionResult, CompletionStage<TransactionResult>> =
            Flow.of(TransactionResult::class.java)
                    .toMat(Sink.last(), Keep.right())


    private fun execute(tx: Transaction): CompletionStage<TransactionResult> {
        return processor.process(tx)
                .handle { errMsg, ex ->
                    val message = ofNullable(errMsg).flatMap(Function.identity()).orElse(
                            ofNullable(ex).map { e -> e.message.toString() }.orElse(""))
                    TransactionResult(tx.id, message)
                }
    }
}
