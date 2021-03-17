package selenium;

import application.driver.factory.WindowsDriver;
import application.element.factory.WindowsElement;
import infrastructure.Highlighter;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import swing.RectanglesDrawingExample;

import java.awt.*;

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
    public Element buildXmlHierarchy() {
        if(xml==null){
            xml = driver.buildXmlHierarchy();
        }
        return xml;
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

    public static WindowsElement descendentElementFromPoint(WindowsElement element, Point point){
        for(WindowsElement child : element.getAllDescendentElements()){
            if(child.getWinDefRect().toRectangle().contains(point)&&child.getAllChildrenElements().size()==0){
                if(element.getAttribute("name").equals("aofiejcmzo;eijoawpejf;lxke932axcbmyj43xdrh")){
                    return null;
                }
                return child;
            }
        }
        return null;
    }

    @Override
    public WindowsElement getElementUnderCursor() {
        Point point = MouseInfo.getPointerInfo().getLocation();
        return descendentElementFromPoint(mainWindow,point);
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
