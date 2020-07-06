package io.btc.utils

import HOME
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.FileFilterUtils
import org.apache.commons.io.filefilter.TrueFileFilter
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import java.io.File
import java.lang.Exception
import java.nio.file.Files
import java.time.Instant
import java.util.concurrent.*
import java.util.function.BiPredicate
import kotlin.streams.toList

fun findGitDirs(rootDir: String = System.getenv(HOME)) {
    FileUtils.listFiles(
        File(rootDir),
        FileFilterUtils.and(FileFilterUtils.nameFileFilter(".git"), FileFilterUtils.directoryFileFilter() ),
//        FileFilterUtils.directoryFileFilter(),
        TrueFileFilter.INSTANCE
//        FileFilterUtils.nameFileFilter(".git")
    ).also { files ->
        println("Found ${files.size} files")
        files.forEach { file -> println(file.absolutePath) }
    }
}

fun findGitDirectoriesUsingFileWalk(rootDir: String = System.getenv(HOME)) {
    val now = Instant.now()
    val filter = Files.walk(File(rootDir).toPath())
        .filter { path ->
            path.toFile().isDirectory && path.endsWith(".git")
        }
        .toList()
    val done = Instant.now()
    println("Found ${filter.size} files in ${done.toEpochMilli() - now.toEpochMilli()} ms")
}

fun findGitDirectoriesUsingFileWalkParallelStream(rootDir: String = System.getenv(HOME)) {
    val now = Instant.now()
    val filter = Files.walk(File(rootDir).toPath())
        .parallel()
        .filter { path ->
            path.toFile().isDirectory && path.endsWith(".git")
        }
        .toList()
    val done = Instant.now()
    println("Found ${filter.size} files in ${done.toEpochMilli() - now.toEpochMilli()} ms")
}

fun findGitDirectoriesUsingNio2(rootDir: String = System.getenv(HOME)) {
    val now = Instant.now()
    val toList = Files.find(File(rootDir).toPath(), Integer.MAX_VALUE, BiPredicate { p, u -> u.isDirectory && p.endsWith(".git") })
        .toList()
    val done = Instant.now()
    println("Found ${toList.size} files in ${done.toEpochMilli() - now.toEpochMilli()} ms")
}

suspend fun File.searchGitDirs(): List<File> {
    return this.listFiles { file, _ -> file.isDirectory }
        ?.map { aDir ->
            GlobalScope.async(Dispatchers.IO) {
                if (aDir.isDirectory && !Files.isSymbolicLink(aDir.toPath())) {
                    if (aDir.endsWith(".git")) {
//                        println(aDir.path)
                        aDir.searchGitDirs().plus(aDir)
                    } else {
                        aDir.searchGitDirs()
                    }
                } else {
                    emptyList()
                }
            }
        }
        ?.awaitAll()
        ?.flatten()
        ?.distinct()
        ?: listOf()
}

fun findGitDirectoriesUsingCoroutines(rootDir: String = System.getenv(HOME)) {
    runBlocking {
        val now = Instant.now()
        val toList = File(rootDir).searchGitDirs()
        val done = Instant.now()
        println("Found ${toList.size} files in ${done.toEpochMilli() - now.toEpochMilli()} ms")
    }
}

suspend fun File.processDir(processSubDirsChannel: Channel<File>, processGitDirChannel: Channel<File>) {
    GlobalScope.async(Dispatchers.IO) {
        listFiles { file, _ -> file.isDirectory }
            ?.forEach { subDir ->
                if (!Files.isSymbolicLink(subDir.toPath())) {
                    if (subDir.endsWith(".git")) {
                        processGitDirChannel.send(subDir)
                    } else {
                        processSubDirsChannel.send(subDir)
                    }
                }
            }
    }
}

fun Repository.getProviderPathFromGitConfig(): String? {
    val regex = Regex(".*github.com:(.*)/(.*).git")
    val origin = this.config.getString("remote", "origin", "url") ?: null
    return if (origin != null) {
        val results = regex.matchEntire(origin)
        results?.groupValues?.subList(1, results.groupValues.size)?.joinToString("/")
    } else {
        null
    }
}

private fun File.openRepo() {
    try {
        FileRepositoryBuilder()
            .setGitDir(this)
            .readEnvironment()
            .findGitDir()
            .build()
            .let {
                val providerPath = it.getProviderPathFromGitConfig()
                if (providerPath != null) {
                    println("Here you go : $absolutePath maps to $providerPath")
                }
            }
    } catch (exception: Exception) {
        println("Could not process $absolutePath")
    }
}


fun findGitDirectoriesUsingChannels(rootDir: String = System.getenv(HOME)) {
    runBlocking {
        val now = Instant.now()
        val atomicCounter = atomic(0)
        val concurrentList : ConcurrentLinkedQueue<File> = ConcurrentLinkedQueue()
        val processMorDirsChannel = Channel<File>(20000)
        val receiveGitDirsChannel = Channel<File>(2000)
        File(rootDir).processDir(processMorDirsChannel, receiveGitDirsChannel)

        launch(Dispatchers.IO) {
            receiveGitDirsChannel.consumeEach {
                it.openRepo()
                atomicCounter.incrementAndGet()
            }
        }
        launch(Dispatchers.IO) {
            processMorDirsChannel.consumeEach {
                it.processDir(processMorDirsChannel, receiveGitDirsChannel)
            }
        }
        while (atomicCounter.value <= 100) {
            delay(100)
        }
        val done = Instant.now()
        println("Found ${concurrentList.size} or ${atomicCounter.value} files in ${done.toEpochMilli() - now.toEpochMilli()} ms")
    }
}

fun main() {
//    findGitDirectoriesUsingFileWalk() : 5 secs
//    findGitDirectoriesUsingFileWalkParallelStream() : 3.5s
//    findGitDirectoriesUsingNio2(): 3.4s
//    findGitDirectoriesUsingCoroutines(): 2.6s


//    findGitDirs("/home/yogesh/work/theboringtech.github.io")
//    findGitDirs()
//    findGitDirectoriesUsingFileWalk()
//    findGitDirectoriesUsingFileWalkParallelStream()
//    findGitDirectoriesUsingNio2()
//    findGitDirectoriesUsingCoroutines()
    findGitDirectoriesUsingChannels()
//    val foo = "git@github.com:jayeshnachnani/FuturetoCompletableFuture.git"
//    val results = regex.matchEntire(foo)
//    println(results?.groupValues)
}