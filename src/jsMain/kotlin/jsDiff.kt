@file:JsModule("diff")
@file:JsNonModule

import kotlin.js.Json

external class JsDiffResult{
    val count: Number
    val added: Boolean? = definedExternally
    val removed: Boolean? = definedExternally
    val value: String
}

@JsName("diffWords")
external fun diffWords(oldString: String, newString: String, options: Json? = definedExternally): Array<JsDiffResult>

