package infrastructure;

import application.component.EYObjectSpyFrame;
import application.component.appfinder.AppIdentifierPanel;
import application.component.domviewer.DomPanel;
import application.component.driverfinder.DriverFinderPanel;
import application.element.factory.WindowsElement;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.ptr.IntByReference;
import infrastructure.thread.ThreadManager;
import infrastructure.xpath.DocumentElmentQueryable;
import infrastructure.xpath.WindowsElementQueryable;
import org.jsoup.select.Elements;
import run.Runner;
import us.codecraft.xsoup.Xsoup;
import xpath.parser.Queryable;
import xpath.parser.Xpath;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class HighlighterRect extends JFrame {

    public HighlighterRect() {
        getContentPane().setBackground(Color.GREEN);
        setUndecorated(true);
        setOpacity(0.55f);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.addMouseListener(new MouseAdapter(){

            @Override
            public void mousePressed(MouseEvent e) {

                if (ThreadManager.eleThreadToggle == true) {
                    ThreadManager.eleThreadToggle = false;
                    if (ThreadManager.eleThread.underMouse != null) {

                        String xpath = ThreadManager.eleThread.driver.getAbsoluteXpath(ThreadManager.eleThread.underMouse);
                        Xpath xpath2 = new Xpath(new DocumentElmentQueryable(ThreadManager.eleThread.domPanel.doc, xpath));
                        List<Queryable> queryableList=xpath2.compile(xpath).execute();
                        Elements searchedForElements = new Elements();
                        for(Queryable queryable:queryableList){
                            searchedForElements.add(((DocumentElmentQueryable) queryable).ele);
                        }
                        //Elements searchedForElements = Xsoup.compile(xpath).evaluate(ThreadManager.eleThread.domPanel.doc).getElements();
                        if (searchedForElements.size() == 0) {
                            ThreadManager.eleThread.domPanel.refresh();
                            searchedForElements = Xsoup.compile(xpath).evaluate(ThreadManager.eleThread.domPanel.doc).getElements();
                        }

                        ThreadManager.eleThread.domPanel.selectSearchedElement(searchedForElements, 0);
                        setVisible(false);


                    }
                }
                if(ThreadManager.windowThreadToggle==true){
                    System.out.println(WindowUtils.getWindowTitle(ThreadManager.windowThread.underMouse));
                    System.out.println(Long.decode(ThreadManager.windowThread.underMouse.toString().substring(7)).toString());
                    IntByReference byRef = new IntByReference();
                    User32.INSTANCE.GetWindowThreadProcessId(ThreadManager.windowThread.underMouse,byRef);
                    System.out.println(byRef.getValue());
                    ThreadManager.windowThreadToggle=false;
                    setVisible(false);
                    ((AppIdentifierPanel) EYObjectSpyFrame.getApp().getCenterPanel()).changeCenterPanel(new DriverFinderPanel(ThreadManager.windowThread.underMouse));
                  //  Main.findDrivers(WindowWatchRunnable.underMouse);
                   // Main.findDrivers();
                }
                else{
                    setVisible(false);
                }

            }


        });

    }

    public void setSizeAndPosition(int x, int y, int w, int h){
        setSize(w, h);
        setLocation(x,y);
    }



    public static void main(String[] args) throws Exception {

       // new HighlighterRect(0,0,200,200).setVisible(true);
    }


}

