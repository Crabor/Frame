package ui.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ui.action.Action;
import ui.component.AbstractComponent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TimerJob extends AbstractListener implements ActionListener {
    public TimerJob(Action[] actions, AbstractComponent who) {
        super(actions, who);
    }

    public TimerJob(Action[] actions, AbstractComponent who, boolean logFlag) {
        super(actions, who, logFlag);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (who.isDisplayed()) {
            if (logFlag)
                logger.info("[LISTENER] [TIMER_JOB]: " + who);
            for (Action action : actions) {
                action.execute(logFlag);
            }
        }
    }
}
