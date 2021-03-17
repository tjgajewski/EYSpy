package application.component.appfinder;

import application.component.EYObjectSpyFrame;
import application.component.driverfinder.DriverFinderPanel;
import com.sun.jna.platform.DesktopWindow;
import infrastructure.Highlighter;
import infrastructure.thread.ThreadManager;
import infrastructure.thread.WindowUnderCursorHighlighThread;

import javax.swing.*;
import java.awt.*;

public class NavPanel extends JPanel {
    public NextAndBackPanel nextAndBackPan;
    public NavPanel(AppIdentifierPanel appIdentifierPanel){
        setLayout(new BorderLayout());
        nextAndBackPan = new NextAndBackPanel();
        JPanel underCursorBtnPan = new JPanel((new FlowLayout(FlowLayout.LEFT)));

        nextAndBackPan.setNextBtnAction(e -> {
            DesktopWindow w = appIdentifierPanel.windows.get(appIdentifierPanel.runningApps.getSelectedIndex());
            Highlighter.unhighlight();
            appIdentifierPanel.changeCenterPanel( new DriverFinderPanel(w.getHWND()));
        });
        nextAndBackPan.setBackBtnAction(e -> {
           EYObjectSpyFrame.getApp().changeCenterPanel(new AppIdentifierPanel());
        });


/*
        JButton nextButton = new JButton("Next");
        JButton backButton = new JButton("Back");
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            EYObjectSpyFrame.refreshCenterPanel();
        });
        nextAndBackPan.add(refreshButton);
        nextAndBackPan.add(backButton);
        nextAndBackPan.add(nextButton);
 */
        JButton findAppButton = new JButton("Find App Under Cursor");
        findAppButton.setMnemonic('r');
        findAppButton.addActionListener(e -> {
            if(ThreadManager.windowThreadToggle==false) {
                ThreadManager.windowThreadToggle = true;
                ThreadManager.windowThread = new WindowUnderCursorHighlighThread();
                Thread t = new Thread(ThreadManager.windowThread);
                t.start();
            }
            else{
                ThreadManager.windowThreadToggle=false;
                Highlighter.unhighlight();

            }


        });
        underCursorBtnPan.add(findAppButton);
        add(nextAndBackPan, BorderLayout.EAST);
        add(underCursorBtnPan, BorderLayout.WEST);
    }
}
