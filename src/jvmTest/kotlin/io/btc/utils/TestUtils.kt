package io.btc.utils

import java.io.File

class TestUtils {
    companion object {
        val btcRepoDir: String =  TestUtils::class.java.classLoader.getResource("placeholder")!!.file
            .let {
                File(it).parentFile.parentFile.parentFile.parentFile.parentFile.parentFile.absolutePath
            }
        const val validBtcRef = "ae6adbf9f142c8591e2128484c87c4e50cdc19e7"
    }
}