package selenium;

import application.element.factory.WindowsElement;
import infrastructure.Highlighter;
import infrastructure.xpath.DocXpath;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.service.DriverService;
import run.Main;
import run.PropertyReader;
import swing.RectanglesDrawingExample;

import java.awt.*;
import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.Duration;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SpyWebDriver implements SpyDriver {

    private WebDriver driver;
    private JavascriptExecutor js;
    private WindowsElement appWin;

    public SpyWebDriver(String attr1, String attr2, String browser, WindowsElement appWin){
        this.appWin = appWin;
        if(browser.equals("ie")) {
            System.setProperty("webdriver.ie.driver", System.getProperty("user.dir") + "\\IEDriverServer.exe");

            InternetExplorerOptions options = new InternetExplorerOptions();
            // options.
            Map<String, Object> op = new HashMap<>();
            op.put("wpfWindowTitle", attr1);
            op.put("ignoreProtectedModeSettings", true);
            op.put("findWithURL", attr2);
            options.setCapability("se:ieOptions", op);
            options.withAttachTimeout(120000, TimeUnit.MILLISECONDS);
            //InternetExplorerDriverService service = new InternetExplorerDriverService();

            Class driverServiceClass = DriverService.class;
            Field waitField = null;
            try {
                waitField = driverServiceClass.getDeclaredField("DEFAULT_TIMEOUT");
            } catch (NoSuchFieldException noSuchFieldException) {
                noSuchFieldException.printStackTrace();
            }
            // waitField.setAccessible(true);
            try {
                setFinalStatic(waitField, Duration.ofSeconds(200L));
            } catch (Exception exception) {
                exception.printStackTrace();
            }


            driver = new InternetExplorerDriver(options);
        }
        else{
            ArrayList<String> stringArrayList = null;
            try {
                stringArrayList =relInfo();
            } catch (IOException e) {
                e.printStackTrace();
            }



            System.setProperty("webdriver.ie.driver", System.getProperty("user.dir") + "\\IEDriverServer.exe");

            ChromeOptions options = new ChromeOptions();
            String lp = localhostPort(stringArrayList,"21932");
            options.setExperimentalOption("debuggerAddress",lp);
            driver = new ChromeDriver(options);

        }
        js = (JavascriptExecutor) driver;
    }

    static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }

public Element xml;

    @Override
    public Element buildXmlHierarchy() {
        if(xml==null) {
            xml=Jsoup.parse((String) js.executeScript("return document.documentElement.innerHTML"));
        }
        return xml;
    }

    @Override
    public void highlightElementByXpath(String xpath) {
        highlightElement(driver.findElement(By.xpath(xpath)));
    }

    @Override
    public void unhighlight() {
       Highlighter.unhighlight();
    }

    @Override
    public void highlightElement(WebElement element) {
        Highlighter.highlightElement(element,appWin);

    }
Point point;
    @Override
    public WebElement getElementUnderCursor() {
       // return (WebElement) js.executeScript("var list = document.querySelectorAll( \":hover\" ); return list[list.length-1]");
        Point point = MouseInfo.getPointerInfo().getLocation();
        if(this.point==null||!this.point.equals(point)) {
            this.point=point;
            org.openqa.selenium.Rectangle winRect = appWin.getRect();
            if(appWin.getWinDefRect().toRectangle().contains(point)) {
                return (WebElement) js.executeScript("var list = document.elementFromPoint(" + (point.x - winRect.x) + ", " + (point.y - winRect.y) + "); return list");
            }
        }
        return null;
    }

    @Override
    public void setXml(Element element) {
        xml=element;
    }

    @Override
    public Elements getElmentsByXpath(String xpath, Element root) {
        List<WebElement> webElements = driver.findElements(By.xpath(xpath));
        List<String> xpaths = new ArrayList<>();
        for(WebElement element:webElements){
            xpaths.add(getAbsoluteXpath(element));
        }
        Elements searchedForElements = new Elements();
        for(String xpathResults:xpaths){
            searchedForElements.add(DocXpath.findAllByXpath(root,xpathResults).get(0));
        }
        return searchedForElements;
    }

    @Override
    public void killDriver() {
        try {
            Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> relInfo() throws IOException {
        Runtime rt = Runtime.getRuntime();
        Process p = rt.exec("netstat -ano -p tcp" );
        ArrayList<String> strings = new ArrayList<String>();
        InputStream is = p.getInputStream();
        Scanner sc = new Scanner(is);
        int i = 0;
        while(sc.hasNext()) {
            strings.add(i,sc.next());
            i++;
        }
        return strings;
    }

    public String localhostPort(ArrayList<String> s, String pidString){
        int found = 0;
        for(int i = 0; i<s.size(); i++){
            if(s.get(i).equalsIgnoreCase(pidString)){
                found = i;
                break;
            }

        }
        found = found -3;
        String relevantPort = s.get(found);
        return relevantPort;
    }

    @Override
    public String getAbsoluteXpath(WebElement element) {
            return ((String) js.executeScript(
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


}
