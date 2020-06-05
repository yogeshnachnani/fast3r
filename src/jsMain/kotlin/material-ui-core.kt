@file:JsModule("@material-ui/core")
@file:JsNonModule

import react.RClass
import react.RProps
import react.ReactElement

@JsName("Button")
external val Button: RClass<ButtonProps>

external interface ButtonProps: RProps {
    var variant: String
    var color: String
    var onClick: () -> Unit
}

@JsName("Grid")
external val Grid: RClass<GridProps>

/** https://material-ui.com/api/grid/ */
external interface GridProps: RProps {
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
    var xs: Number
}

@JsName("Paper")
external val Paper: RClass<PaperProps>

external interface PaperProps: RProps {
    /** 0 to 24 */
    var elevation: Number
    /** If true, rounded corners are disabled. */
    var square: Boolean
    /** 'elevation' or 'outlined' */
    var variant: String
}
external interface DividerProps: RProps {
    /** If true, divider will have lighter color */
    var light: Boolean
    /* horizontal, vertical */
    var orientation: String
    /** fullWidth , inset or middle */
    var variant: String
}

@JsName("Divider")
external val Divider: RClass<DividerProps>

external interface ListProps: RProps {
    var dense: Boolean
    var disablePadding: Boolean
}

@JsName("List")
external val MaterialUIList: RClass<ListProps>

external interface ListItemProps: RProps {
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
}

@JsName("ListItem")
external val ListItem: RClass<ListItemProps>

external interface AvatarProps: RProps {
    var alt: String
    /** 'circle' 'rounded' or 'square'. Default circle */
    var variant: String
    /** Css class name */
    var className: String
}

@JsName("Avatar")
external val Avatar: RClass<AvatarProps>

@JsName("ListSubheader")
external val ListSubHeader: RClass<ListSubHeaderProps>

external interface ListSubHeaderProps: RProps {
    /** 'default' , 'primary' , 'inherit' */
    var color: String
    var disableGutters: Boolean
    var disableSticky: Boolean
    /** IF true, [ListSubHeader] will be indented */
    var inset: Boolean
}
