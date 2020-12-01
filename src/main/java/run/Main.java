package run;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import infrastructure.automationapi.IUIAutomationElement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.service.DriverService;
import selenium.WatchRunnable;
import swing.DomFrame;
import swing.ElementMutableTreeNode;
import swing.ImagePanel;
import swing.XmlJTree;
import us.codecraft.xsoup.Xsoup;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.Duration;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

/*from ww w .  jav  a2s .co  m*/

public class Main {

    public static WebDriver driver;
    public static Document doc;
    public static DomFrame f;
    public static Elements searchedForElements = new Elements();
    public static int focusedSearchedElement=0;
    public static boolean watch = false;
    public static WatchRunnable watchThread;
    public static boolean loadingIE = true;
    public static JMenuBar bar;
    public static JPanel inpsectPanel;
    public static JPanel propertiesPanel;
    public static  JPanel findAppPan;

    public static void main (String[] args) throws Exception {

        configureDarkTheme();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("EY Spy");
                frame.setLayout(new BorderLayout());
                ImageIcon img = new ImageIcon(System.getProperty("user.dir")+"\\EYlogo1.jpg");
                frame.setIconImage(img.getImage());
                frame.add(new ImagePanel(), BorderLayout.CENTER);
                frame.add(new JLabel("Loading EY Spy"), BorderLayout.SOUTH);
               // frame.setPreferredSize(new Dimension(600,400));
                frame.pack();
                frame.setVisible(true);

                while(loadingIE){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

frame.setVisible(false);

            }
        });
        thread.start();


PropertyReader propertyReader = new PropertyReader(System.getProperty("user.dir")+"\\config.properties");


        System.setProperty("webdriver.ie.driver", System.getProperty("user.dir")+"\\IEDriverServer.exe");

        InternetExplorerOptions options = new InternetExplorerOptions();
       // options.
        Map<String, Object> op = new HashMap<>();
        op.put("wpfWindowTitle",propertyReader.getProperties().getProperty("wpfWindowTitle"));
        op.put("ignoreProtectedModeSettings",true);
        op.put("findWithURL",propertyReader.getProperties().getProperty("findWithURL"));
        options.setCapability("se:ieOptions",op);
        options.withAttachTimeout(120000, TimeUnit.MILLISECONDS);
        //InternetExplorerDriverService service = new InternetExplorerDriverService();
        Class driverServiceClass = DriverService.class;
        Field waitField = driverServiceClass.getDeclaredField("DEFAULT_TIMEOUT");
       // waitField.setAccessible(true);
        setFinalStatic(waitField, Duration.ofSeconds(60L));
        driver=new InternetExplorerDriver(options);

        //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        configureDarkTheme();


        f = new DomFrame("EY Spy");
        ImageIcon img = new ImageIcon(System.getProperty("user.dir")+"\\EYlogo1.jpg");
        f.setIconImage(img.getImage());
        //  f.setResizable(false)

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
            inpsectPanel.setVisible(false);
            findAppPan.setVisible(true);


        });



        bar.add(file);
        bar.add(view);
        bar.add(settings);
        f.add(bar, BorderLayout.NORTH);





        findAppPan = new JPanel(new BorderLayout());
        JPanel topButtonSearchPan = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton findAppButton = new JButton("Find App Under Cursor (Alt + r)");
        findAppButton.setMnemonic('r');
        topButtonSearchPan.add(findAppButton);
        findAppPan.add(topButtonSearchPan, BorderLayout.NORTH);
        findAppPan.add(new ImagePanel(), BorderLayout.CENTER);
        f.add(findAppPan, BorderLayout.CENTER);
        findAppButton.addActionListener(e -> {
            loadMainPan();
            findAppPan.setVisible(false);

        });
        f.setPreferredSize(new Dimension(458, 700));
        f.pack();
        loadingIE = false;
        f.setVisible(true);
    }

    public static void loadMainPan(){











        JPanel btnMenu = new JPanel(new FlowLayout(FlowLayout.LEFT));
        f.myTree = new XmlJTree(null);
        JScrollPane scrollPane = new JScrollPane(f.myTree, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        JButton watchBtn = new JButton("Watch");
        watchBtn.setMnemonic('e');

        watchBtn.addActionListener(e -> {
            if(!watch){
                f.myTree.unHighlightSelectedEle();
                watch = true;
                watchThread = new WatchRunnable();
                Thread t = new Thread(watchThread);
                t.start();
            }
            else{
                watch=false;
                if(watchThread.underMouse != null) {
                    String xpath = getAbsoluteXPath(watchThread.underMouse);

                    Elements searchedForElements = Xsoup.compile(xpath).evaluate(doc).getElements();
                    if(searchedForElements.size() == 0){
                        refreshTree();
                        searchedForElements = Xsoup.compile(xpath).evaluate(doc).getElements();
                    }

                    selectSearchedElement(searchedForElements, 0);
                }


            }
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



        //f.add(btnMenu, BorderLayout.NORTH);

        inpsectPanel = new JPanel(new BorderLayout());
        inpsectPanel.add(btnMenu, BorderLayout.NORTH);
        inpsectPanel.add(scrollPane, BorderLayout.CENTER);
        inpsectPanel.add(searchPan, BorderLayout.SOUTH);

        f.add(inpsectPanel, BorderLayout.CENTER);
        refreshTree();
        f.pack();
        loadingIE = false;
        //f.setVisible(true);
    }

    public static void refreshTree(){
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String dom = (String) js.executeScript("return document.documentElement.innerHTML");
        doc = Jsoup.parse(dom);

        Element root = doc.children().get(0);
        f.myTree.setPath(root);
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



    public static void configureDarkTheme() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {

        /*
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (javax.swing.UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
        //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        LafManager.setTheme(new DarculaTheme());
        LafManager.install();
    }

    public static String getAbsoluteXPath(WebElement element)
    {
        return ((String) ((JavascriptExecutor) driver).executeScript(
                "function absoluteXPath(element) {"+
                        "var comp, comps = [];"+
                        "var parent = null;"+
                        "var xpath = '';"+
                        "var getPos = function(element) {"+
                        "var position = 1, curNode;"+
                        "if (element.nodeType == Node.ATTRIBUTE_NODE) {"+
                        "return null;"+
                        "}"+
                        "for (curNode = element.previousSibling; curNode; curNode = curNode.previousSibling){"+
            "if (curNode.nodeName == element.nodeName) {"+
                    "++position;"+
                    "}"+
                    "}"+
                    "return position;"+
                    "};"+

                    "if (element instanceof Document) {"+
                    "return '/';"+
                    "}"+

                    "for (; element && !(element instanceof Document); element = element.nodeType == Node.ATTRIBUTE_NODE ? element.ownerElement : element.parentNode) {"+
            "comp = comps[comps.length] = {};"+
                    "switch (element.nodeType) {"+
                    "case Node.TEXT_NODE:"+
                    "comp.name = 'text()';"+
                    "break;"+
                    "case Node.ATTRIBUTE_NODE:"+
                    "comp.name = '@' + element.nodeName;"+
                    "break;"+
                    "case Node.PROCESSING_INSTRUCTION_NODE:"+
                    "comp.name = 'processing-instruction()';"+
                    "break;"+
                    "case Node.COMMENT_NODE:"+
                    "comp.name = 'comment()';"+
                    "break;"+
                    "case Node.ELEMENT_NODE:"+
                    "comp.name = element.nodeName;"+
                    "break;"+
                    "}"+
                    "comp.position = getPos(element);"+
                    "}"+

                    "for (var i = comps.length - 1; i >= 0; i--) {"+
                    "comp = comps[i];"+
                    "xpath += '/' + comp.name.toLowerCase();"+
                    "if (comp.position !== null) {"+
                    "xpath += '[' + comp.position + ']';"+
                    "}"+
                    "}"+

                    "return xpath;"+

                    "} return absoluteXPath(arguments[0]);", element)).replace("html[1]","html");
        }
    static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }

    public static DesktopWindow getWindowUnderCursor(){
        WinDef.POINT point = new WinDef.POINT();
        User32.INSTANCE.GetCursorPos(point);
        List<DesktopWindow> windows = WindowUtils.getAllWindows(true);
        Point point1 = new Point(point.x, point.y);
        for(DesktopWindow window : windows){
            if(window.getLocAndSize().contains(point1)) {
               return window;
            }
        }
        return null;
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

    /*Thread.sleep(2500);
getWindowUnderCursor();
WindowsDriver windowsDriver = new WindowsDriver();
windowsDriver.findElement(By.name(getWindowUnderCursor().title));

     */

}


