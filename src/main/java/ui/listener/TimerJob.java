package ui.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ui.action.Action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TimerJob implements ActionListener {
    Action action;
    Log logger = LogFactory.getLog(TimerJob.class);

    public TimerJob(Action action) {
        this.action = action;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
//        System.out.println("TimerJob.actionPerformed");
        logger.info("[TIMER]: ");
        action.execute();
    }
}
