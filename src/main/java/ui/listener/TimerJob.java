package ui.listener;

import ui.action.Action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TimerJob implements ActionListener {
    Action action;

    public TimerJob(Action action) {
        this.action = action;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
//        System.out.println("TimerJob.actionPerformed");
        action.execute();
    }
}
