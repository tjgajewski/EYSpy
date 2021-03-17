package selenium;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.WebElement;

public interface SpyDriver {


    public Element buildXmlHierarchy();

    public void highlightElementByXpath(String xpath);

    public void unhighlight();

    public void highlightElement(WebElement element);

    public void killDriver();

    public String getAbsoluteXpath(WebElement element);

    public WebElement getElementUnderCursor();

    public Element xml = null;

    public void setXml(Element element);
}
