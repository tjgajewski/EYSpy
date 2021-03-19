package infrastructure.thread;

import application.component.domviewer.DomPanel;
import org.openqa.selenium.WebElement;
import run.Main;
import selenium.SpyDriver;

public class ElementUnderCursorHighlightThread implements Runnable {

   public WebElement underMouse = null;
   public SpyDriver driver;
   public DomPanel domPanel;

   public ElementUnderCursorHighlightThread(DomPanel domPanel){
       this.driver = domPanel.driver;
       this.domPanel = domPanel;
   }

    public void run() {

        while(ThreadManager.eleThreadToggle){

                WebElement element = driver.getElementUnderCursor();
                if ((underMouse == null || !underMouse.equals(element)) && element != null) {

                    if (underMouse != null)
                        driver.unhighlight();


                    driver.highlightElement(element);

                    underMouse = element;
                }
            System.out.println(ThreadManager.eleThreadToggle);
        }

    }


}

