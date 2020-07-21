package supercr.workflows.codereview.components

import EditSession
import Editor
import kotlinx.css.LinearDimension
import kotlinx.css.fontSize
import kotlinx.css.height
import kotlinx.css.lineHeight
import kotlinx.css.px
import kotlinx.css.vh
import kotlinx.css.width
import kotlinx.html.id
import org.w3c.dom.HTMLDivElement
import react.RBuilder
import react.RComponent
import react.RElementBuilder
import react.RProps
import react.RState
import react.ReactElement
import react.createRef
import styled.css
import styled.getClassName
import styled.styledDiv
import supercr.css.ComponentStyles
import supercr.css.FontSizes
import supercr.css.LineHeights
import kotlin.js.json

external interface CodeViewProps: RProps {
    var fileText: String
    var id: String
    var divId: String
}

external interface CodeViewState: RState {
}


/**
 * A simple wrapper over Ace [Editor] with sensible defaults
 */
class CodeView(
    constructorProps: CodeViewProps
): RComponent<CodeViewProps, CodeViewState>(constructorProps) {
    /** These are required to load the editor properly */
    val ace = js("require('ace-builds/src-noconflict/ace')")
    val webpackResolver = js("require('ace-builds/webpack-resolver')")
    val theme = js("require('ace-builds/src-noconflict/theme-clouds_midnight')")
    val mode = js("require('ace-builds/src-noconflict/mode-java')")
    private lateinit var internalEditor: Editor
    private var internalDivRef = createRef<HTMLDivElement>()

    override fun RBuilder.render() {
        styledDiv {
            css {
                width = LinearDimension.inherit
                height = 70.vh
                fontSize = FontSizes.normal
                lineHeight = LineHeights.normal
            }
            attrs {
                ref = internalDivRef
                id = props.divId
            }
        }
    }

    override fun shouldComponentUpdate(nextProps: CodeViewProps, nextState: CodeViewState): Boolean {
        /** Update only if the id has changed (which means we have new text) */
        return nextProps.id != props.id
    }

    override fun componentDidMount() {
        val aceOpts = json(
            "theme" to "ace/theme/clouds_midnight",
            "readOnly" to true,
            "highlightActiveLine" to true,
            "fontSize" to FontSizes.normal,
            "showGutter" to true,
            "tabSize" to 4,
            "mode" to "ace/mode/java"
        )
        internalEditor = ace.edit(
            props.divId,
            aceOpts
        ) as Editor
        val editSession = EditSession(props.fileText, "ace/mode/java")
        internalEditor.setSession(editSession)
        /** This is needed during the first mount for the [Editor.gotoLine] method to work (presently called from [DiffView]
         *  See https://stackoverflow.com/questions/23748743/ace-editor-go-to-line
         */
        internalEditor.resize(true)
        /** Hide Scrollbars. TODO: Find a less hacky way to do this */
        internalEditor.renderer.scrollBarV.element.style.overflowY = "hidden"
    }

    override fun getSnapshotBeforeUpdate(prevProps: CodeViewProps, prevState: CodeViewState): Any {
        return 0
    }

    override fun componentDidUpdate(prevProps: CodeViewProps, prevState: CodeViewState, snapshot: Any) {
        val newEditSession = EditSession(props.fileText, "ace/mode/java")
        internalEditor.setSession(newEditSession)
    }
}


fun RBuilder.codeView(handler: RElementBuilder<CodeViewProps>.() -> Unit): ReactElement {
    return child(CodeView::class) {
        handler()
    }
}
