package io.btc.lsp

const val JSON_RPC_VERSION = "2.0"
const val CONTENT_LENGTH = "Content-Length"
const val CONTENT_TYPE = "Content-Type"
const val JSON_MIME_TYPE = "application/json"
const val NEWLINE = "\r\n"
val CONTENT_LENGTH_REGEX = Regex("Content-Length: (\\d+)")