package ui.action;

import ui.component.AbstractComponent;

public interface Action {
    void execute();
    void execute(boolean logFlag);
    AbstractComponent getWho();
}
