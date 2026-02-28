package com.rockthejvm.jvmConcurrency

import kotlinx.coroutines.Runnable
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.concurrent.thread

object ThreadsBasics {

    // Thread = Independent Unit of execution

    // JVM Thread = Data structure maps to OS threads
    // Runnable is a piece of code to run

    val takingTheBus = Runnable {
        println("Taking the bus")
        (0 .. 10).forEach {
            println("${it * 10}% done")
            Thread.sleep(300)
        }
        println("Getting off the bus, I'm done")
    }

    fun runThread() {
        val thread = Thread(takingTheBus)
        // Thread is just "data"
        thread.start() // The thread won't be executed until i call start() method. Here the code runs independently
    }

    fun runMultipleThreads() {
        val takingTheBus = Thread(takingTheBus)
        // These three options are equivalent thread {} Thread {} and Thread(Runnable {})
        /*
         thread {} is a kotlin function which has some parameters.
         @Param start parameter is set as true by default and
         when you don't explicitly specify it as false you won't need the
         listeningToPodcast.start() call
         */
        val listeningToPodcast = thread(start = false) { // Kotlin style
            println("Personal Development")
            Thread.sleep(2000)
            println("I'm a new person now")
        }
        takingTheBus.start()
        /*
         if you call this with thread {} kotlin style the thread is started multiple times
         and an exception is thrown
         IllegalThreadStateException
         */
        listeningToPodcast.start()
    }

    val threadInterrupt = thread(start = false) {
        while (true) {
            try {
                Thread.sleep(1000)
                println("Scrolling Social Media")
            }catch (e: InterruptedException) { // Handling exception to avoid thread crash and gracefully stop the loop
                println("Too much scrolling time to stop")
                return@thread // non-local return - braking while loop
            }

        }
    }

    // Executors
    fun demoExecutors() {
        /** A thread pool manages the lifecycle threads for you
         * Reserved/Dynamic amount of threads waiting for a task to execute and complete and then
         * waits for the next task. If all threads are busy, incoming tasks are typically queued until a
         * thread becomes available.
         * Creating threads is resource expensive.
         * The thread pool creates and reserve those threads to avoid the expensive task of creating a new one every time it's needed
         * The thread pool could be dynamic and create new threads based on the load
          */
        val executor = Executors.newFixedThreadPool(8)

        // Sending tasks to the executor which takes one thread from the pool to run it
        executor.submit {
            for (i in (1 .. 10)) {
                println("Counting to $i")
                Thread.sleep(100)
            }
        }

        // Futures make a thread return a value
        val future: Future<Int> = executor.submit(
            Callable {
                println("Computing something")
                Thread.sleep(3000)
                42
            }
        )

        // get() blocks the calling thread until the future returns
        println("Meaning of life is ${future.get()}")

        /**
         * With this approach you must shut down the executor explicitly
         * otherwise the app keeps running forever since it waits for all the
         * threads to finish.
         * shutdown() waits for all task to be done and no new tasks must be submitted
         */
        executor.shutdown()
    }


    @JvmStatic
    fun main(args: Array<String>) {
        // Here we are running on the MAIN thread
        //takingTheBus.run() // Calling this still runs in the main thread

        /*runThread()// This method runs in another thread
        Thread.sleep(1000)
        println("Hello from the main thread")*/

        // runMultipleThreads() // All the details are in the method
        /*threadInterrupt.start()
        Thread.sleep(5000)
        threadInterrupt.interrupt() // This throws an InterruptedException on that thread    - Crashing the thread
        threadInterrupt.join() // Waits for the thread to finish
        */

        demoExecutors()
    }
}