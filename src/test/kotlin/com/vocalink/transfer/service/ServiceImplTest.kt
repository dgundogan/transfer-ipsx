package com.vocalink.transfer.service

import akka.actor.ActorSystem
import akka.testkit.javadsl.TestKit
import com.vocalink.transfer.model.Transaction
import com.vocalink.transfer.processor.TransactionProcessor
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import kotlin.test.assertEquals


class ServiceImplTest{

    private lateinit var actorSystem: ActorSystem

    @Before
    fun setup() {
        actorSystem = ActorSystem.create("StreamTest")
    }

    @After
    fun tearDown() {
        TestKit.shutdownActorSystem(actorSystem)
    }

    @Test
    fun `Given a correct transaction , it returns Success`() {

        val processor = TransactionProcessor()
        val service = ServiceImpl(actorSystem,processor)

        val txn = Transaction(123,"0001223","000023445", BigDecimal.valueOf(10.0), "GBP", "RENT", LocalDateTime.now())

        val stage: CompletionStage<Optional<String>> = service.processTransaction(txn)
        val future: CompletableFuture<Optional<String>> = stage.toCompletableFuture()
        future.join()
        assertEquals("", future.get().get())
    }

    @Test
    fun `Given an incorrect transaction , it returns a fail response`() {

        val processor = TransactionProcessor()
        val service = ServiceImpl(actorSystem,processor)

        val txn = Transaction(234,"0001223","000023445", BigDecimal.valueOf(-10.0), "GBP", "RENT", LocalDateTime.now())

        val stage: CompletionStage<Optional<String>> = service.processTransaction(txn)
        val future: CompletableFuture<Optional<String>> = stage.toCompletableFuture()
        future.join()
        assertEquals("Amount cannot be zero", future.get().get())
    }
}