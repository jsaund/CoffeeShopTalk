
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlin.random.Random

//fun main() = runBlocking {
//    println("start")
//
//    val pool = Dispatcher(10, this)
//    coroutineScope {
//        (1..100).forEach { i ->
//            launch {
//                val c = CompletableDeferred<Int>()
//                pool.dispatch {
//                    println("running job: $i")
//                    delay(Random.nextLong(5000L))
//                    println("finished job: $i")
//
//                    if (i == 20) {
//                        cancel()
//                    } else {
//                        c.complete(i)
//                    }
//                }
//                try {
//                    println(c.await())
//                } catch (e: IllegalStateException) {
//                    println("error")
//                }
//            }
//        }
//    }
//
//    coroutineContext.cancelChildren()
//    println("end")
//}

class Worker(private val tag: String, private val workerPool: Channel<Channel<Job>>) {
    private val jobs = Channel<Job>()

    fun start(scope: CoroutineScope) {
        scope.launch {
            while (isActive) {
                workerPool.send(jobs)
                println("$tag: processing job")
                jobs.receive().invoke()
            }
            jobs.close()
        }
    }
}

class Dispatcher(workers: Int, scope: CoroutineScope) {
    private val workerPool = Channel<Channel<Job>>(capacity = workers)

    init {
        require(workers > 0) { "must provide at least one worker" }

        for (i in 1..workers) {
            val worker = Worker("worker-$i", workerPool)
            worker.start(scope)
        }
    }

    suspend fun dispatch(job: Job) {
        val worker = workerPool.receive()
        worker.send(job)
    }
}
