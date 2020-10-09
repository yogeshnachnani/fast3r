package supercr.workflows.common

import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import supercr.kb.components.helpBox

abstract class BaseScreen<P: RProps, S: RState>: RComponent<P, S> {
    constructor() : super()

    constructor(props: P) : super(props)

    override fun RBuilder.render() {
        helpBox {
            contents = getHelpContents()
        }
        renderScreen()
    }

    abstract fun RBuilder.renderScreen()

    abstract fun getHelpContents(): List<Pair<String, List<ReactElement>>>
}