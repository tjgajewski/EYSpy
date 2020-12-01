package selenium;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import run.Main;

public class WatchRunnable implements Runnable {

   // public Object disableClickFunction;
   public WebElement underMouse = null;

    public void run() {

        JavascriptExecutor js = ((JavascriptExecutor) Main.driver);
       // disableClickFunction = js.executeAsyncScript("return function f(e){  e.stopPropagation();e.stopImmediatePropagation();e.preventDefault();}");
       // js.executeScript("window.addEventListener(\"click\",arguments[0],true);",disableClickFunction);

        while(Main.watch){

                WebElement element = (WebElement) js.executeScript("var list = document.querySelectorAll( \":hover\" ); return list[list.length-1]");
                if ((underMouse == null || !underMouse.equals(element)) && element != null) {

                    if (underMouse != null)
                        js.executeScript("arguments[0].setAttribute('style','background:; border: 0px solid blue;');", underMouse);


                    js.executeScript("arguments[0].setAttribute('style','background:GreenYellow; border: 0px solid blue;');", element);

                    underMouse = element;
                }
        }

        if (underMouse != null)
            js.executeScript("arguments[0].setAttribute('style','background:; border: 0px solid blue;');", underMouse);

       // js.executeScript("window.removeEventListener(\"click\",arguments[0],true);",disableClickFunction);

    }


}

