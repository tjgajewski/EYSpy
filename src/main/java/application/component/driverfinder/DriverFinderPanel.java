package application.component.driverfinder;

import application.component.EYObjectSpyFrame;
import application.component.appfinder.AppIdentifierPanel;
import application.component.domviewer.DomPanel;
import application.driver.factory.WindowsDriver;
import application.element.factory.WindowsElement;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.WinDef;
import infrastructure.Highlighter;
import infrastructure.ObjectSpyPanel;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import selenium.SpyDriver;
import selenium.SpyWebDriver;
import selenium.SpyWindowsDriver;
import swing.IconListRenderer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DriverFinderPanel extends ObjectSpyPanel {

    private WindowsElement windowEle = null;
    private JList driverCandidateList;
    private WinDef.HWND hwnd;
    private ArrayList<HashMap<String, Object>> driverCandidates;

    public DriverFinderPanel(WinDef.HWND hwnd){
        AppIdentifierPanel appIdentifierPanel = (AppIdentifierPanel) EYObjectSpyFrame.getApp().getCenterPanel();
        this.hwnd=hwnd;
        DesiredCapabilities capabilities = new DesiredCapabilities();
        WindowsDriver windowsDriver = new WindowsDriver(capabilities);
        loop:
        for(WindowsElement window : windowsDriver.getRootElement().getAllChildrenElements()){
            if(window.getAttribute("hwnd").equals(Long.decode(hwnd.toString().substring(7)).toString())){
                // eleUnderCursor = SpyWindowsDriver.descendentElementFromPoint(window,point);
                windowEle = window;
                break loop;
            }
        }
        appIdentifierPanel.navPanel.nextAndBackPan.setNextBtnAction(e -> {
            Highlighter.unhighlight();
            int index = driverCandidateList.getSelectedIndex();
            String driver = (String) driverCandidates.get(index).get("driver");
            SpyDriver spyDriver;
            if(driver.equals("Internet Explorer")){
                spyDriver = new SpyWebDriver(driverCandidates.get(index).get("winTitle").toString().substring(0,16), driverCandidates.get(index).get("name").toString(),"ie", (WindowsElement) driverCandidates.get(index).get("windowEle"));
            }
            else{
                spyDriver = new SpyWindowsDriver(windowsDriver, (WindowsElement) driverCandidates.get(index).get("windowEle"));
            }

            EYObjectSpyFrame.getApp().changeCenterPanel(new DomPanel(spyDriver));
        });
        appIdentifierPanel.navPanel.nextAndBackPan.setBackBtnAction(e -> {
            appIdentifierPanel.changeCenterPanel( new DriverFinderPanel(hwnd));
        });


        driverCandidateList = new JList();


        driverCandidateList.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                if(e.getValueIsAdjusting()) {
                    int index = driverCandidateList.getSelectedIndex();
                   Highlighter.highlightElement((WebElement) driverCandidates.get(index).get("windowEle"));
                }
            }
        });
        refresh();
        add(new JScrollPane(driverCandidateList), BorderLayout.CENTER);
       // setVisible(true);

    }
    @Override
    public void refresh() {
        driverCandidates = new ArrayList<>();
        HashMap<String, Object> winDriverCandidate = new HashMap<>();
        winDriverCandidate.put("windowEle",windowEle);
        winDriverCandidate.put("driver","Windows");
        winDriverCandidate.put("name", WindowUtils.getWindowTitle(hwnd));

        driverCandidates.add(winDriverCandidate);
        if(windowEle.getAttribute("classname").equals("SAP_FRONTEND_SESSION")){

        }
        for(WindowsElement element : windowEle.getAllDescendentElements()){
            String frameworkId = element.getAttribute("frameworkid");
            String name = element.getAttribute("name");
            String className = element.getAttribute("classname");
            if(!frameworkId.equals("")){


            }

            if(className.equals("Chrome_RenderWidgetHostHWND")){

            }

            else if(className.equals("Internet Explorer_Server")&&(name.startsWith("https://")||name.startsWith("http://"))){
                HashMap<String, Object> ieDriverCandidate = new HashMap<>();
                ieDriverCandidate.put("name",name);
                ieDriverCandidate.put("driver","Internet Explorer");
                ieDriverCandidate.put("winTitle",WindowUtils.getWindowTitle(hwnd).substring(0,16));
                ieDriverCandidate.put("windowEle", element);
                ieDriverCandidate.put("icon",new ImageIcon(System.getProperty("user.dir")+"\\icons\\windows.ico"));
                driverCandidates.add(ieDriverCandidate);

            }






        }
        setLayout(new BorderLayout());


        String[] drivers = new String[driverCandidates.size()];
        Map<Object, Icon> icons = new HashMap<>();
        for(int i = 0; i < driverCandidates.size(); i++){
            HashMap<String, Object> map = driverCandidates.get(i);
            String driver = map.get("driver").toString();
            String title = driver+" \""+map.get("name").toString()+"\"";
            drivers[i] =  title;
            if(driver.equals("Internet Explorer")){
                icons.put(title, FileSystemView.getFileSystemView().getSystemIcon( new File(System.getProperty("user.dir")+"\\iexplore.exe") ));
            }
            else if(driver.equals("Windows")){
                icons.put(title,FileSystemView.getFileSystemView().getSystemIcon( new File(System.getProperty("user.dir")+"\\procexp64.exe") ));
            }
        }

        driverCandidateList.setCellRenderer(new IconListRenderer(icons));
        driverCandidateList.setListData(drivers);

    }
}
