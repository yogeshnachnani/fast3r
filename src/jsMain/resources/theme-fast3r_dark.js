ace.define("ace/theme/fast3r_dark",["require","exports","module","ace/lib/dom"], function(require, exports, module) {

exports.isDark = true;
exports.cssClass = "ace-fast3r-dark";
exports.cssText = ".ace-fast3r-dark .ace_cursor {\
color: #7DA5DC\
}\
.ace-fast3r-dark .ace_print-margin {\
width: 1px;\
background: #232323\
}\
.ace-fast3r-dark .ace_marker-layer .ace_selection {\
background: #000000\
}\
.ace-fast3r-dark.ace_multiselect .ace_selection.ace_start {\
box-shadow: 0 0 3px 0px #191919;\
}\
.ace-fast3r-dark .ace_marker-layer .ace_step {\
background: rgb(102, 82, 0)\
}\
.ace-fast3r-dark .ace_marker-layer .ace_bracket {\
margin: -1px 0 0 -1px;\
border: 1px solid #BFBFBF\
}\
.ace-fast3r-dark .ace_marker-layer .ace_active-line {\
background: rgba(215, 215, 215, 0.031)\
}\
.ace-fast3r-dark .ace_marker-layer .ace_selected-word {\
border: 1px solid #000000\
}\
.ace-fast3r-dark .ace_invisible {\
color: #666\
}\
.ace-fast3r-dark .ace_keyword.ace_operator {\
color: #4B4B4B\
}\
.ace-fast3r-dark .ace_keyword.ace_other.ace_unit {\
color: #366F1A\
}\
.ace-fast3r-dark .ace_constant.ace_language {\
color: #39946A\
}\
.ace-fast3r-dark .ace_constant.ace_numeric {\
color: #46A609\
}\
.ace-fast3r-dark .ace_constant.ace_character.ace_entity {\
color: #A165AC\
}\
.ace-fast3r-dark .ace_invalid {\
color: #FFFFFF;\
background-color: #E92E2E\
}\
.ace-fast3r-dark .ace_fold {\
background-color: #927C5D;\
border-color: #929292\
}\
.ace-fast3r-dark .ace_entity.ace_name.ace_tag,\
.ace-fast3r-dark .ace_entity.ace_other.ace_attribute-name {\
color: #606060\
}\
.ace-fast3r-dark .ace_indent-guide {\
background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAACCAYAAACZgbYnAAAAEklEQVQImWNgYGBgYHB3d/8PAAOIAdULw8qMAAAAAElFTkSuQmCC) right repeat-y\
}";

var dom = require("../lib/dom");
dom.importCssString(exports.cssText, exports.cssClass);
});                (function() {
                    ace.require(["ace/theme/fast3r_dark"], function(m) {
                        if (typeof module == "object" && typeof exports == "object" && module) {
                            module.exports = m;
                        }
                    });
                })();
            
