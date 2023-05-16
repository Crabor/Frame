package ui.struct;

public enum ListenerType {
    MOUSE_CLICK("MouseClick"),
    MOUSE_DOUBLE_CLICK("MouseDoubleClick"),
    MOUSE_HOVER("MouseHover"),
    MOUSE_DRAG("MouseDrag"),
    MOUSE_SCROLL("MouseScroll"),
    MOUSE_MOVE("MouseMove"),
    MOUSE_PRESS("MousePress"),
    MOUSE_RELEASE("MouseRelease"),
    MOUSE_ENTER("MouseEnter"),
    MOUSE_LEAVE("MouseLeave"),
    MOUSE_DRAG_ENTER("MouseDragEnter"),
    MOUSE_DRAG_LEAVE("MouseDragLeave"),
    MOUSE_DRAG_DROP("MouseDragDrop"),
    KEYBOARD_KEY_DOWN("KeyboardKeyDown"),
    KEYBOARD_KEY_UP("KeyboardKeyUp"),
    KEYBOARD_KEY_PRESS("KeyboardKeyPress"),
    FOCUS_IN("FocusIn"),
    FOCUS_OUT("FocusOut"),
    TEXT_INPUT("TextInput"),
    PASSWORD_INPUT("PasswordInput"),
    WINDOW_OPEN("WindowOpen"),
    WINDOW_CLOSE("WindowClose"),
    WINDOW_MINIMIZE("WindowMinimize"),
    WINDOW_MAXIMIZE("WindowMaximize"),
    WINDOW_RESTORE("WindowRestore"),
    STATE_CHANGE("StateChange"),
    STATE_SELECT("StateSelect"),
    STATE_UPDATE("StateUpdate"),
    TOUCH_START("TouchStart"),
    TOUCH_MOVE("TouchMove"),
    TOUCH_END("TouchEnd"),
    TOUCH_CANCEL("TouchCancel"),
    TIMER("Timer");

    private final String value;

    ListenerType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ListenerType fromString(String typeStr) {
        for (ListenerType type : ListenerType.values()) {
            if (type.name().equalsIgnoreCase(typeStr) || type.getValue().equalsIgnoreCase(typeStr)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + typeStr + " found");
    }
}
