@file:JsModule("@material-ui/core")
@file:JsNonModule

import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.Event
import react.RClass
import react.RProps
import react.RReadableRef
import react.ReactElement
import react.dom.WithClassName
import styled.CustomStyledProps
import styled.StyledProps


@JsName("IconButton")
external val IconButton: RClass<IconButtonProps>

external interface IconButtonProps: WithClassName {
    var color: String
    /** Small, medium, large. Default is medium */
    var size: String
    var onClick: () -> Unit
}

@JsName("Button")
external val Button: RClass<ButtonProps>

external interface ButtonProps: WithClassName {
    var variant: String
    var color: String
    /** Small, medium, large. Default is medium */
    var size: String
    var onClick: () -> Unit
}

@JsName("Grid")
external val Grid: RClass<GridProps>

/** https://material-ui.com/api/grid/ */
external interface GridProps: WithClassName {
    var item: Boolean
    var container: Boolean

    /**
     * stretch
     * center
     * flex-start
     * flex-end
     * space-between
     * space-around
     *
     * Default: Stretch
     */
    var alignContent: String

    /**
     * flex-start
     * center
     * flex-end
     * stretch
     * baseline
     *
     * Default: Stretch
     */
    var alignItems: String
    var direction: String

    /**
     * flex-start
     * center
     * flex-end
     * space-between
     * space-around
     * space-evenly
     */
    var justify: String
    var spacing: Number
    var wrap: String
    /** min-width : 960px */
    var md: Number
    /** min-width: 1440px. Macbook 13" and 15" */
    var lg: Number
    /** min-width: 1920px. 2K + displays or Macbook 15" scaled + */
    var xl: Number
}

@JsName("Paper")
external val Paper: RClass<PaperProps>

external interface PaperProps: WithClassName {
    /** 0 to 24 */
    var elevation: Number
    /** If true, rounded corners are disabled. */
    var square: Boolean
    /** 'elevation' or 'outlined' */
    var variant: String
}
external interface DividerProps: WithClassName {
    /** If true, divider will have lighter color */
    var light: Boolean
    /* horizontal, vertical */
    var orientation: String
    /** fullWidth , inset or middle */
    var variant: String
}

@JsName("Divider")
external val Divider: RClass<DividerProps>

external interface ListProps: WithClassName {
    var dense: Boolean
    var disablePadding: Boolean
}

@JsName("List")
external val MaterialUIList: RClass<ListProps>

external interface ListItemProps: WithClassName {
    /** flex-start | center */
    var alignItems: String
    var autoFocus: Boolean
    /** If true, list item will use a ButtonBase */
    var button: Boolean
    var dense: Boolean
    var disabled: Boolean
    /** If true, a 1px light border is added to the bottom of the list item */
    var divider: Boolean
    var selected: Boolean
    /** If true, the left and right padding is removed */
    var disableGutters: Boolean
    var onClick: () -> Unit
    var key: String
}

@JsName("ListItem")
external val ListItem: RClass<ListItemProps>

external interface ListItemTextProps: WithClassName {
    var disableTypography: Boolean
    var inset: Boolean
    var primary: String
    var secondary: String
}

@JsName("ListItemText")
external val ListItemText: RClass<ListItemTextProps>

external interface ListItemIconProps: WithClassName {
}

@JsName("ListItemIcon")
external val ListItemIcon: RClass<ListItemIconProps>

external interface AvatarProps: WithClassName {
    var alt: String
    /** 'circle' 'rounded' or 'square'. Default circle */
    var variant: String
    /** To Display an image */
    var src: String
}

@JsName("Avatar")
external val Avatar: RClass<AvatarProps>

@JsName("ListSubheader")
external val ListSubHeader: RClass<ListSubHeaderProps>

external interface ListSubHeaderProps: WithClassName {
    /** 'default' , 'primary' , 'inherit' */
    var color: String
    var disableGutters: Boolean
    var disableSticky: Boolean
    /** IF true, [ListSubHeader] will be indented */
    var inset: Boolean
}

@JsName("OutlinedInput")
external val OutlinedInput: RClass<OutlinedInputProps>

external interface OutlinedInputProps: WithClassName {
    /** If true, the input element will be focused during the first mount*/
    var autoFocus: Boolean

    var fullWidth: Boolean

    var inputRef: RReadableRef<HTMLTextAreaElement>
    var multiline: Boolean
    var onChange: (Event) -> Unit
    /** The short hint displayed in the input before the user enters a value.*/
    var placeholder: String
    /** Number of rows to display when multiline option is set to true */
    var rows: Number
    /** Max number of rows to display when multiline option is set to true */
    var rowsMax: Number

    var label: ReactElement
}
