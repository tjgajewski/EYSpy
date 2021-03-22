package application.component;

import application.component.appfinder.AppIdentifierPanel;
import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;
import com.github.weisj.darklaf.theme.IntelliJTheme;
import infrastructure.Highlighter;
import infrastructure.ObjectSpyPanel;


import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.logging.Level;

public class EYObjectSpyFrame extends JFrame {

    public ObjectSpyPanel getCenterPanel() {
        return centerPanel;
    }

    private ObjectSpyPanel centerPanel;
    private JMenuBar northMenu;

    private EYObjectSpyFrame() {
        super("EY Object Spy");
        LafManager.setLogLevel(Level.OFF);
        LafManager.setTheme(new DarculaTheme());
        //LafManager.install();
        Highlighter.buildHightlighterRect();
        ImageIcon img = new ImageIcon(System.getProperty("user.dir")+"\\EYlogo1.jpg");
        setIconImage(img.getImage());
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        northMenu = buildMenuBar();
        add(northMenu, BorderLayout.NORTH);
        changeCenterPanel(new AppIdentifierPanel());
       // add(centerPanel, BorderLayout.CENTER);
        setPreferredSize(new Dimension(458, 700));

        pack();
        setVisible(true);

    }

    private static EYObjectSpyFrame eyObjectSpyFrame;

    public static void buildApp(){
        eyObjectSpyFrame = new EYObjectSpyFrame();
    }
    public static EYObjectSpyFrame getApp(){
        return eyObjectSpyFrame;
    }

    public void changeCenterPanel(ObjectSpyPanel panel){
        if(centerPanel!=null) {
            remove(centerPanel);
            centerPanel.setVisible(false);
        }
        add(panel, BorderLayout.CENTER);
        panel.setVisible(true);
        centerPanel = panel;
        revalidate();
        repaint();
        pack();
    }

    public static void refreshCenterPanel(){
        eyObjectSpyFrame.centerPanel.refresh();
    }






    @Override
    protected void processWindowEvent(final WindowEvent e) {

        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            Highlighter.unhighlight();
            System.exit(0);


        }
    }

    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenu view = new JMenu("View");
        JMenu settings = new JMenu("Settings");

        JMenuItem objectRepo = new JMenuItem("Open Object Repo");
        view.add(objectRepo);
        JMenuItem changeDriver = new JMenuItem("Create New Browser");
        settings.add(changeDriver);
        JMenuItem inspectEle = new JMenuItem("Inspect Current Selected Application");
        view.add(inspectEle);
        JMenuItem changeApplication = new JMenuItem("Change Application");
        changeApplication.addActionListener(e -> {
            changeCenterPanel(new AppIdentifierPanel());
        });
        view.add(changeApplication);
        JMenuItem newOR = new JMenuItem("Add New Object Repo");
        settings.add(newOR);
        JMenuItem wrapText = new JMenuItem("Wrap Element Hierarchy Text");
        settings.add(wrapText);
        JMenuItem allowHighlight = new JMenuItem("Allow Highlighting of Elements in Application");
        settings.add(allowHighlight);
        JMenuItem saveAs = new JMenuItem("Save As...");
        file.add(saveAs);
        JMenuItem save = new JMenuItem("Save");
        file.add(save);
/*
        changeApplication.addActionListener(e -> {

            f.add(findAppPan, BorderLayout.CENTER);
            if(inpsectPanel!=null) {
                inpsectPanel.setVisible(false);
            }
            if(f.myTree != null) {
                if (f.myTree.absXpath != null) {
                    driver.unhighlightElementByXpath(f.myTree.absXpath);

                }
                driver.killDriver();
            }
            findAppPan.setVisible(true);


        });


*/
        bar.add(file);
        bar.add(view);
        bar.add(settings);
        return bar;
    }
}