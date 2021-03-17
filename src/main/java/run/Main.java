package run;

import application.component.EYObjectSpyFrame;
import application.driver.factory.WindowsDriver;
import application.element.factory.WindowsElement;
import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;
import com.sun.jna.Pointer;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import org.openqa.selenium.remote.DesiredCapabilities;
import selenium.*;
import swing.*;
import us.codecraft.xsoup.Xsoup;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.*;
import java.util.List;

/*from ww w .  jav  a2s .co  m*/

public class Main {
/*
    public static SpyDriver driver;
    public static Element doc;
    public static EYObjectSpyFrame f;
    public static Elements searchedForElements = new Elements();
    public static int focusedSearchedElement=0;
    public static boolean watch = false;
    public static boolean windowWatch=false;
    public static WatchRunnable watchThread;
    public static WindowWatchRunnable watchWindowThread;
    public static boolean loadingWindowsDriver = true;
    public static JMenuBar bar;
    public static JPanel inpsectPanel;
    public static JPanel propertiesPanel;
    public static  JPanel findAppPan;
    public static WindowsElement appWin;
    public static List<DesktopWindow> windows;
    public static RectanglesDrawingExample highlightedRect;
    public static JPanel selectDriverPan;
    public static int pid = Kernel32.INSTANCE.GetCurrentProcessId();
    public static JList runningApps;
    //public WindowsDriver windowsDriver;

    public static void main (String[] args) throws Exception {
        configureDarkTheme();
       // f = new EYObjectSpyFrame();
        ImageIcon img = new ImageIcon(System.getProperty("user.dir")+"\\EYlogo1.jpg");
        f.setIconImage(img.getImage());
        f.setAlwaysOnTop(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(new BorderLayout());


        bar = new JMenuBar();
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



        bar.add(file);
        bar.add(view);
        bar.add(settings);
        f.add(bar, BorderLayout.NORTH);





        findAppPan = new JPanel(new BorderLayout());
        JPanel topSearchPan = new JPanel(new BorderLayout());
        JPanel nextAndBackPan = new JPanel((new FlowLayout(FlowLayout.RIGHT)));

        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(e -> {
                DesktopWindow w = windows.get(runningApps.getSelectedIndex());
                highlightedRect.dispose();
                findDrivers(w.getHWND());
        });




        JButton backButton = new JButton("Back");
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            f.remove(findAppPan);
            refreshFindAppPan();
            f.revalidate();
            f.repaint();
                });

        refreshFindAppPan();
        JPanel underCursorBtnPan = new JPanel((new FlowLayout(FlowLayout.LEFT)));
        JButton findAppButton = new JButton("Find App Under Cursor");
        findAppButton.setMnemonic('r');
        underCursorBtnPan.add(findAppButton);
        nextAndBackPan.add(refreshButton);
        nextAndBackPan.add(backButton);
        nextAndBackPan.add(nextButton);
        topSearchPan.add(nextAndBackPan, BorderLayout.EAST);
        topSearchPan.add(underCursorBtnPan, BorderLayout.WEST);
        findAppPan.add(topSearchPan, BorderLayout.NORTH);




        findAppButton.addActionListener(e -> {
            if(windowWatch==false) {
                windowWatch = true;
                watchWindowThread = new WindowWatchRunnable();
                Thread t = new Thread(watchWindowThread);
                t.start();
            }
            else{
                windowWatch=false;
                highlightedRect.dispose();

            }


        });

        f.setPreferredSize(new Dimension(458, 700));
        f.pack();
        loadingWindowsDriver = false;
        f.setVisible(true);
    }


    public static void refreshFindAppPan(){
        Map<Object, Icon> icons = new HashMap<>();
        List<DesktopWindow> allWins = WindowUtils.getAllWindows(true);
        List<String> titles = new ArrayList<>();
        //List<BufferedImage> icons = new ArrayList<>();
        windows = new ArrayList<>();
        for(int i=0;i<allWins.size();i++){
            DesktopWindow desktopWindow = allWins.get(i);
            WinDef.HWND hwnd = desktopWindow.getHWND();
            String title = WindowUtils.getWindowTitle(hwnd);
            IntByReference byRef = new IntByReference();
            User32.INSTANCE.GetWindowThreadProcessId(desktopWindow.getHWND(),byRef);
            if(byRef.getValue()!=pid&&!title.equals("")){
                windows.add(desktopWindow);
                titles.add(title);
                Icon icon = FileSystemView.getFileSystemView().getSystemIcon(new File(desktopWindow.getFilePath()));
                if(icon!=null)
                    icons.put(title,icon);
            }
        }



        runningApps = new JList(titles.toArray());
        runningApps.setCellRenderer(new IconListRenderer(icons));

        runningApps.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                if(e.getValueIsAdjusting()) {
                    int index = runningApps.getSelectedIndex();
                    highlightWindow(windows.get(index).getHWND());
                }
            }
        });
        JPanel appListPanel = new JPanel(new BorderLayout());
        appListPanel.add(new JScrollPane(runningApps),BorderLayout.CENTER);
        for(Component c : findAppPan.getComponents()){
            for(Component cc : ((JPanel)c).getComponents()){
                if(cc instanceof JScrollPane)
                findAppPan.remove(c);
            }
        }
        findAppPan.add(appListPanel,BorderLayout.CENTER);
        findAppPan.revalidate();
        findAppPan.repaint();
        f.add(findAppPan, BorderLayout.CENTER);
    }
    static WindowsElement windowEle = null;
public static void findDrivers(WinDef.HWND hwnd){

    DesiredCapabilities capabilities = new DesiredCapabilities();
    WindowsDriver windowsDriver = new WindowsDriver(capabilities);
  //  WindowsElement eleUnderCursor = null;

   // WindowUtils.getWindowLocationAndSize(WindowWatchRunnable.underMouse);
   // windowsDriver.findElementByRect()
   // Point point = MouseInfo.getPointerInfo().getLocation();
    loop:
    for(WindowsElement window : windowsDriver.getRootElement().getAllChildrenElements()){
        if(window.getAttribute("hwnd").equals(Long.decode(hwnd.toString().substring(7)).toString())){
           // eleUnderCursor = SpyWindowsDriver.descendentElementFromPoint(window,point);
            windowEle = window;
            break loop;
        }
    }
    ArrayList<HashMap<String, Object>> driverCandidates = new ArrayList<>();
    HashMap<String, Object> winDriverCandidate = new HashMap<>();
    winDriverCandidate.put("windowEle",windowEle);
    winDriverCandidate.put("driver","Windows");
    winDriverCandidate.put("name",WindowUtils.getWindowTitle(hwnd));

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
    findAppPan.setVisible(false);
    selectDriverPan = new JPanel(new BorderLayout());


    String[] drivers = new String[driverCandidates.size()];
    Map<Object, Icon> icons = new HashMap<>();
    for(int i = 0; i < driverCandidates.size(); i++){
        HashMap<String, Object> map = driverCandidates.get(i);
        String driver = map.get("driver").toString();
        String title = driver+" \""+map.get("name").toString()+"\"";
        drivers[i] =  title;
        if(driver.equals("Internet Explorer")){
            icons.put(title,FileSystemView.getFileSystemView().getSystemIcon( new File(System.getProperty("user.dir")+"\\iexplore.exe") ));
        }
        else if(driver.equals("Windows")){
            icons.put(title,FileSystemView.getFileSystemView().getSystemIcon( new File(System.getProperty("user.dir")+"\\procexp64.exe") ));
        }
    }


    JList driverCandidateList = new JList(drivers);
    driverCandidateList.setCellRenderer(new IconListRenderer(icons));

    driverCandidateList.addListSelectionListener(new ListSelectionListener()
    {
        public void valueChanged(ListSelectionEvent e)
        {
            if(e.getValueIsAdjusting()) {
                unhighlightWindowEle();
                int index = driverCandidateList.getSelectedIndex();
                highlightWindowEle((WindowsElement) driverCandidates.get(index).get("windowEle"));
            }
        }
    });
    JPanel driverCandidatePanel = new JPanel(new BorderLayout());
    driverCandidatePanel.add(new JScrollPane(driverCandidateList),BorderLayout.CENTER);

    JPanel nextAndBackPan = new JPanel((new FlowLayout(FlowLayout.RIGHT)));
    JButton nextButton = new JButton("Next");
    JButton backButton = new JButton("Back");
    //JButton refreshButton = new JButton("Refresh");
    nextButton.addActionListener(e -> {
        unhighlightWindowEle();
            int index = driverCandidateList.getSelectedIndex();
        String driver = (String) driverCandidates.get(index).get("driver");

        if(driver.equals("Internet Explorer")){
            Main.driver = new SpyWebDriver(driverCandidates.get(index).get("winTitle").toString().substring(0,16), driverCandidates.get(index).get("name").toString(),"ie");
        }
        else{
            Main.driver = new SpyWindowsDriver(windowsDriver, windowEle);
        }
        selectDriverPan.setVisible(false);

        loadMainPan();
    });
    // nextAndBackPan.add(refreshButton);
    nextAndBackPan.add(backButton);
    nextAndBackPan.add(nextButton);
    selectDriverPan.add(nextAndBackPan, BorderLayout.NORTH);
    selectDriverPan.add(driverCandidateList, BorderLayout.CENTER);
//    Icon icon = FileSystemView.getFileSystemView().getSystemIcon(new File(allWins.get(i).getFilePath()));
//        driver = new SpyWebDriver(wpfWindowTitle.substring(0,16),url,"ie");
    //    loadMainPan();

  //  driver = new SpyWindowsDriver(windowsDriver, windowEle);
  //  loadMainPan();

    findAppPan.setVisible(false);
    selectDriverPan.setVisible(true);
    f.add(selectDriverPan,BorderLayout.CENTER);
//    }
//    else if(frameworkId.equalsIgnoreCase("Chromeium")||windowEle.getAttribute("name").contains("Google Chrome")){
//        String ppid = windowEle.getAttribute("ppid");
//
//
//
//
//
//        driver = new SpyWebDriver(ppid,null,"chrome");
//        loadMainPan();
//    }
//    else{

}
public static void highlightWindow(WinDef.HWND hwnd){
    if(highlightedRect!=null){
        highlightedRect.dispose();
    }
    // DesktopWindow selectedWin =
try {
    Rectangle rectangle = WindowUtils.getWindowLocationAndSize(hwnd);
    highlightedRect = new RectanglesDrawingExample(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    highlightedRect.setAlwaysOnTop(true);
    highlightedRect.setVisible(true);
}
catch (Win32Exception e){

}
}

public static void highlightWindowEle(WindowsElement e){
    org.openqa.selenium.Rectangle r = e.getRect();
    highlightedRect=new RectanglesDrawingExample(r.x,r.y, r.width, r.height);
    highlightedRect.setVisible(true);
    highlightedRect.setAlwaysOnTop(true);
}

    public static void unhighlightWindowEle(){
        if(highlightedRect != null) {
            highlightedRect.dispose();
        }
    }

    public static void loadMainPan(){
f.remove(selectDriverPan);
        JPanel btnMenu = new JPanel(new FlowLayout(FlowLayout.LEFT));
        f.myTree = new XmlJTree(null);
        JScrollPane scrollPane = new JScrollPane(f.myTree, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        JButton watchBtn = new JButton("Watch");
        watchBtn.setMnemonic('e');

        watchBtn.addActionListener(e -> {
                if(f.myTree.absXpath!=null) {
                    driver.unhighlightElementByXpath(f.myTree.absXpath);
                }
                watch = true;
                watchThread = new WatchRunnable();
                Thread t = new Thread(watchThread);
                t.start();

        });

        //WebElement e = (WebElement) ((JavascriptExecutor) Main.driver).executeScript("var list = document.querySelectorAll( \":hover\" ); return list[list.length-1]");
        btnMenu.add(watchBtn);
        JButton viewBtn = new JButton("View");

        viewBtn.addActionListener(e -> {
            if(inpsectPanel.isVisible()) {
                ElementMutableTreeNode node = (ElementMutableTreeNode) f.myTree.getLastSelectedPathComponent();
                if (node != null) {
                    Element element = (Element) node.getUserObject();
                    int maxRow = element.attributes().size();
                    boolean hasText = !element.text().equals("");
                    if (hasText) {
                        maxRow++;
                    }

                    List<Attribute> attributeList = element.attributes().asList();
                    Object[][] props = new Object[maxRow + 2][2];
                    int i;
                    for (i = 0; i < attributeList.size(); i++) {
                        Attribute attribute = attributeList.get(i);
                        props[i][0] = attribute.getKey();
                        props[i][1] = attribute.getValue();
                    }
                    if (hasText) {
                        props[i][0] = "text";
                        props[i][1] = element.text();
                        i++;
                    }

                    props[i][0] = "tag";
                    props[i][1] = element.tagName();
                    i++;
                    props[i][0] = "abs xpath";
                    props[i][1] = node.absXpath;

                    Object[] colHeaders = new Object[2];
                    colHeaders[0] = "Property";
                    colHeaders[1] = "Value";
                    JTable table = new JTable(props, colHeaders);
                    if(propertiesPanel==null){
                        propertiesPanel = new JPanel(new BorderLayout());

                    }
                    f.add(propertiesPanel, BorderLayout.CENTER);
                    propertiesPanel.removeAll();

                    JScrollPane propertiesScrollGrid = new JScrollPane(table);
                    propertiesPanel.add(propertiesScrollGrid, BorderLayout.CENTER);
                    propertiesPanel.add(btnMenu, BorderLayout.NORTH);
                    //    f.pack();
                    propertiesPanel.setVisible(true);
                    inpsectPanel.setVisible(false);
                    viewBtn.setText("Back");



                }
            } else {
                //  f.pack();
                f.add(inpsectPanel, BorderLayout.CENTER);
                propertiesPanel.setVisible(false);
                inpsectPanel.add(btnMenu, BorderLayout.NORTH);
                inpsectPanel.setVisible(true);
                // f.remove(propertiesPanel);
                //  f.add(inpsectPanel, BorderLayout.CENTER);
                viewBtn.setText("View");
                //  f.remove(propertiesPanel);

                nullifyTreeIcons();
            }



            //Object[] optionsBtns = {"OK"};
            //JOptionPane.showOptionDialog(frame, new JScrollPane(table), "View Properties", JOptionPane.PLAIN_MESSAGE, JOptionPane.PLAIN_MESSAGE, null, optionsBtns, optionsBtns[0]);


        });
        btnMenu.add(viewBtn);
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> {
            refreshTree();
        });
        btnMenu.add(refreshBtn);

        JPanel searchPan = new JPanel(new BorderLayout());
        JTextField field = new JTextField( 19);
        //searchPan.add(field, BorderLayout.CENTER);
        JButton goSearchBtn = new JButton("GO");
        JButton nextBtn = new JButton(">");
        JButton prevBtn = new JButton("<");
        JLabel label = new JLabel(focusedSearchedElement + " of " + searchedForElements.size() + " results");
        JPanel subSearchPanButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        subSearchPanButtons.add(goSearchBtn);
        subSearchPanButtons.add(label);
        subSearchPanButtons.add(prevBtn);
        subSearchPanButtons.add(nextBtn);
        JPanel searchTextFieldPan = new JPanel(new BorderLayout());
        searchTextFieldPan.add(field, BorderLayout.CENTER);
        searchTextFieldPan.setBorder(BorderFactory.createEmptyBorder(5,5,5,0));
        searchPan.add(searchTextFieldPan, BorderLayout.CENTER);
        searchPan.add(subSearchPanButtons, BorderLayout.EAST);



        nextBtn.setEnabled(false);
        nextBtn.addActionListener(z -> {
            focusedSearchedElement++;
            selectSearchedElement(searchedForElements, focusedSearchedElement);
            if(focusedSearchedElement+1==searchedForElements.size()){
                nextBtn.setEnabled(false);
            }
            if(focusedSearchedElement>0){
                prevBtn.setEnabled(true);
            }
            label.setText(focusedSearchedElement+1 + " of " + searchedForElements.size() + " results");
        });


        prevBtn.setEnabled(false);
        prevBtn.addActionListener(z -> {
            focusedSearchedElement--;
            selectSearchedElement(searchedForElements, focusedSearchedElement);
            if(focusedSearchedElement+1<searchedForElements.size()){
                nextBtn.setEnabled(true);
            }
            if(focusedSearchedElement==0){
                prevBtn.setEnabled(false);
            }
            label.setText(focusedSearchedElement+1 + " of " + searchedForElements.size() + " results");
        });

        goSearchBtn.addActionListener(z -> {
            try {
                searchedForElements = Xsoup.compile(field.getText()).evaluate(doc).getElements();
            }
            catch (Selector.SelectorParseException e){
            }
            if(searchedForElements.size()==0){
                searchByText(field.getText());
            }
            focusedSearchedElement = 0;
            if(searchedForElements.size()==0){
                label.setText(focusedSearchedElement + " of " + searchedForElements.size() + " results");
            }
            else {
                selectSearchedElement(searchedForElements, focusedSearchedElement);
                label.setText(focusedSearchedElement + 1 + " of " + searchedForElements.size() + " results");
            }
            if(searchedForElements.size()>1){
                nextBtn.setEnabled(true);
            }
            else{
                nextBtn.setEnabled(false);
            }
            prevBtn.setEnabled(false);
        });

        field.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    searchedForElements = Xsoup.compile(field.getText()).evaluate(doc).getElements();
                }
                catch (Selector.SelectorParseException e2){
                }
                if(searchedForElements.size()==0){
                    searchByText(field.getText());
                }
                focusedSearchedElement = 0;
                if(searchedForElements.size()==0){
                    label.setText(focusedSearchedElement + " of " + searchedForElements.size() + " results");
                }
                else {
                    selectSearchedElement(searchedForElements, focusedSearchedElement);
                    label.setText(focusedSearchedElement + 1 + " of " + searchedForElements.size() + " results");
                }
                if(searchedForElements.size()>1){
                    nextBtn.setEnabled(true);
                }
                else{
                    nextBtn.setEnabled(false);
                }
                prevBtn.setEnabled(false);
                f.pack();
            }
        });

        inpsectPanel = new JPanel(new BorderLayout());
        inpsectPanel.add(btnMenu, BorderLayout.NORTH);
        inpsectPanel.add(scrollPane, BorderLayout.CENTER);
        inpsectPanel.add(searchPan, BorderLayout.SOUTH);

        f.add(inpsectPanel, BorderLayout.CENTER);
        refreshTree();
        f.pack();
    }

    public static void refreshTree(){
        driver.setXml(null);
        doc = driver.buildXmlHierarchy();
        f.myTree.setPath(doc);
    }

    public static void selectSearchedElement(Elements searchedForElements, int focusedSearchedElement){
        for (Enumeration e = ((ElementMutableTreeNode) f.myTree.getModel().getRoot()).depthFirstEnumeration(); e.hasMoreElements();) {
            ElementMutableTreeNode node = (ElementMutableTreeNode) e.nextElement();
            if (searchedForElements.get(focusedSearchedElement) == node.getUserObject()) {
                TreePath path = new TreePath(node.getPath());
                f.myTree.getSelectionModel().setSelectionPath(path);
                if(!node.isRoot())
                f.myTree.expandPath(new TreePath(((ElementMutableTreeNode) node.getParent()).getPath()));
                f.myTree.scrollPathToVisible(path);
            }
        }
    }

    public static String fullElementTag(Element element){
        if(element.children().size()==0){
            return element.outerHtml();
        }
        String outerHtlml = element.outerHtml();
        int lastChar = outerHtlml.indexOf('>')+1;
        String s = outerHtlml.substring(0,lastChar);
        String endTag = "</"+element.tagName()+">";
        if(outerHtlml.trim().endsWith(endTag)){
            s=s+"..."+endTag;
        }
        return s;
    }

    public static void searchByText(String text){
        searchedForElements = new Elements();
        for (Element element : doc.getAllElements()){
            if(fullElementTag(element).contains(text)){
                searchedForElements.add(element);
            }
        }
    }



    public static void configureDarkTheme() {
        LafManager.setTheme(new DarculaTheme());
        LafManager.install();
    }




    public static WinDef.HWND getWindowUnderCursor(){
        WinDef.POINT point = new WinDef.POINT();
        User32.INSTANCE.GetCursorPos(point);
        Point point1 = new Point(point.x, point.y);
        List<WinDef.HWND> wins = new ArrayList<>();
        User32.INSTANCE.EnumWindows(new User32.WNDENUMPROC() {
            @Override
            public boolean callback(WinDef.HWND hwnd, Pointer pointer) {
                WinDef.RECT rect = new WinDef.RECT();
                User32.INSTANCE.GetWindowRect(hwnd, rect);
                IntByReference byRef = new IntByReference();
                User32.INSTANCE.GetWindowThreadProcessId(hwnd,byRef);
                if(byRef.getValue()!=pid) {
                    if (rect.toRectangle().contains(point1) && User32.INSTANCE.IsWindowVisible(hwnd)) {
                        wins.add(hwnd);
                    }
                }
               return true;
            }
            },null);


        return wins.get(0);
    }



    public static void nullifyTreeIcons(){
        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) f.myTree.getCellRenderer();
        renderer.setOpenIcon(null);
        renderer.setClosedIcon(null);
        renderer.setLeafIcon(null);
        f.myTree.setCellRenderer(renderer);
        BasicTreeUI ui = (BasicTreeUI) f.myTree.getUI();
        ui.setExpandedIcon(null);
        ui.setCollapsedIcon(null);
    }

 */

}


