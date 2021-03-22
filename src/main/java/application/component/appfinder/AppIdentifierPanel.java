package application.component.appfinder;

import com.sun.jna.platform.DesktopWindow;

import infrastructure.ObjectSpyPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AppIdentifierPanel extends ObjectSpyPanel {
    private ObjectSpyPanel centerPanel;
    protected List<DesktopWindow> windows = new ArrayList<>();
    protected JList runningApps;
    public NavPanel navPanel;


    public AppIdentifierPanel(){
        setLayout(new BorderLayout());
        navPanel = new NavPanel(this);
        add(navPanel, BorderLayout.NORTH);
        centerPanel = new AppListPanel(this);
        add(centerPanel, BorderLayout.CENTER);

    }

    @Override
    public void refresh() {
        centerPanel.refresh();
    }

    public void changeCenterPanel(ObjectSpyPanel panel){
        centerPanel.setVisible(false);
        remove(centerPanel);
        add(panel, BorderLayout.CENTER);
        panel.setVisible(true);
        centerPanel = panel;
    }




}
