@file:JsModule("@material-ui/core/styles")
@file:JsNonModule

import react.RClass
import react.RProps

@JsName("StylesProvider")
external val StylesProvider: RClass<StylesProviderProps>

external interface StylesProviderProps: RProps {
    var injectFirst: Boolean
}
