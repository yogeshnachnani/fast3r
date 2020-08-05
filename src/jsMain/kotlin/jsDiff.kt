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

@JsName("diffChars")
external fun diffChars(oldString: String, newString: String, options: Json? = definedExternally): Array<JsDiffResult>

@JsName("diffLines")
external fun diffLines(oldString: String, newString: String, options: Json? = definedExternally): Array<JsDiffResult>

@JsName("diffWordsWithSpace")
external fun diffWordsWithSpace(oldString: String, newString: String, options: Json? = definedExternally): Array<JsDiffResult>
