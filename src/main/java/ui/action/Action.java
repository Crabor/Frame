package ui.action;

import ui.component.AbstractComponent;

public interface Action {
    void execute();
    AbstractComponent getWho();
}
