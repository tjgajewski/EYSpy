package selenium;

import application.driver.factory.WindowsDriver;
import application.element.factory.WindowsElement;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import infrastructure.Highlighter;
import infrastructure.HighlighterRect;
import infrastructure.thread.ThreadManager;
import infrastructure.xpath.DocXpath;
import infrastructure.xpath.DocumentElmentQueryable;
import infrastructure.xpath.WindowsElementQueryable;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import swing.RectanglesDrawingExample;
import xpath.parser.CommandList;
import xpath.parser.Queryable;
import xpath.parser.Xpath;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class SpyWindowsDriver implements SpyDriver{

    WindowsDriver driver;
    WindowsElement mainWindow;

    public SpyWindowsDriver(WindowsDriver driver, WindowsElement maindWindow){
        this.driver =driver;
        driver.switchTo().frame(maindWindow);
        this.mainWindow = maindWindow;
    }

    public Element xml;

    @Override
    public void setXml(Element element) {
        xml=element;
    }

    @Override
    public Elements getElmentsByXpath(String xpath, Element root) {
List<WebElement> elements = driver.findElements(By.xpath(xpath));
List<String> xpaths = new ArrayList<>();
        for(WebElement element:elements){
            xpaths.add(((WindowsElement) element).getAbsXpath());
        }
        Elements searchedForElements = new Elements();
        for(String xpathResults:xpaths){
            searchedForElements.add(DocXpath.findAllByXpath(root,xpathResults).get(0));
        }
        return searchedForElements;
    }

    @Override
    public Element buildXmlHierarchy() {
        return driver.buildXmlHierarchy();
    }


    @Override
    public void highlightElementByXpath(String xpath) {
        Highlighter.highlightElement(driver.findElement(By.xpath(xpath)));
    }

    @Override
    public void killDriver() {
driver.quit();
    }

    @Override
    public String getAbsoluteXpath(WebElement element) {
        return ((WindowsElement) element).getAbsXpath();
    }

    public WindowsElement drillDeeper(WindowsElement element, Point point){
        List<WindowsElement> children = element.getAllDescendentElements();
        for(int i =0; i<children.size(); i++){
            WindowsElement child = children.get(i);
            if(child.getWinDefRect().toRectangle().contains(point)){
                return drillDeeper(child,point);
            }
        }
        return element;
    }

    public boolean descendentElementFromPoint(List<WindowsElement> elements, Point point){
        List<WindowsElement> desc= new ArrayList<>();
        for(int i=0;i<elements.size();i++) {
            WindowsElement element = elements.get(i);
            if(element.getWinDefRect().toRectangle().contains(point)){
                return true;
            }

        }
        for(int i=0;i<elements.size();i++) {
            desc.addAll(elements.get(i).getAllChildrenElements());
        }
        if(desc.size()==0) {
            return false;
        }
        else {
            return descendentElementFromPoint(desc,point);
        }
       // for(WindowsElement element:sorted)
        /*
        for(WindowsElement child : elements){


            if(child.getWinDefRect().toRectangle().contains(point)){
                List<WindowsElement> childChildren = child.getAllChildrenElements();
                if(childChildren.size()==0){
                    return child;
                }
               // if(element.getAttribute("name").equals("aofiejcmzo;eijoawpejf;lxke932axcbmyj43xdrh")){
               //     return null;
                //}
                WindowsElement res = descendentElementFromPoint(childChildren,point);
                if(res!=null){
                    return res;
                }
                else{
                    return child;
                }
            }
        }
        return null;

         */
    }
WindowsElement watchedElement;
    Point point;
    @Override
    public WindowsElement getElementUnderCursor() {
        Point point = MouseInfo.getPointerInfo().getLocation();
        if(this.point==null||!this.point.equals(point)) {
            this.point=point;
            if (watchedElement != null && watchedElement.getWinDefRect().toRectangle().contains(point)) {
                if (descendentElementFromPoint(watchedElement.getAllChildrenElements(), point)) {
                    Highlighter.setVisible(false);
                    watchedElement = driver.elementFromPoint(new WinDef.POINT.ByValue(point.x, point.y));
                    watchedElement = drillDeeper(watchedElement,point);
                    Highlighter.setVisible(true);
                    return watchedElement;
                }
            } else if (mainWindow.getWinDefRect().toRectangle().contains(point)) {
                // Highlighter.unhighlight();
                //Highlighter.setVisible(false);
                watchedElement = driver.elementFromPoint(new WinDef.POINT.ByValue(point.x, point.y));
                //Highlighter.setVisible(true);
                return watchedElement;
            }
        }
        return null;
        //return descendentElementFromPoint(mainWindow.getAllChildrenElements(),MouseInfo.getPointerInfo().getLocation());
    }

    @Override
    public void unhighlight() {
        Highlighter.unhighlight();
    }

    @Override
    public void highlightElement(WebElement element) {
        Highlighter.highlightElement(element);
    }
}
