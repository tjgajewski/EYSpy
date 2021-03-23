package infrastructure;

import application.element.factory.WindowsElement;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinDef;
import org.openqa.selenium.WebElement;
import run.Main;
import swing.RectanglesDrawingExample;

import java.awt.*;

public class Highlighter {

    private static HighlighterRect highlightedRect;

    public static void buildHightlighterRect(){
        highlightedRect = new HighlighterRect();
    }

    public static void highlightWindow(WinDef.HWND hwnd){
        Rectangle rectangle = WindowUtils.getWindowLocationAndSize(hwnd);
        highlightedRect.setSizeAndPosition(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        if(!highlightedRect.isVisible()){
            highlightedRect.setVisible(true);
        }
    }

    public static void setVisible(boolean bool){
        highlightedRect.setVisible(bool);
    }

    public static void unhighlight(){
        if(highlightedRect.isVisible()) {
            highlightedRect.setVisible(false);
        }
    }

    public static void changeHighlightedWindow(WinDef.HWND hwnd){
        unhighlight();
        highlightWindow(hwnd);
    }

    public static void changeHighlightedElement(WebElement element){
        unhighlight();
        highlightElement(element);
    }

    public static void highlightElement(WebElement element){
        org.openqa.selenium.Rectangle rectangle = element.getRect();
        highlightedRect.setSizeAndPosition(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        if(!highlightedRect.isVisible()){
            highlightedRect.setVisible(true);
        }

    }

    public static void highlightElement(org.openqa.selenium.Rectangle r, WindowsElement appWin){
        org.openqa.selenium.Rectangle winRect = appWin.getRect();
        highlightedRect.setSizeAndPosition(winRect.x+r.x,winRect.y+r.y, r.width, r.height);
       // highlightedRect.setSizeAndPosition(winRect.x,winRect.y, r.width, r.height);
        if(!highlightedRect.isVisible()){
            highlightedRect.setVisible(true);
        }

    }



}
