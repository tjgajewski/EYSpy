package swing;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import javax.swing.tree.TreeNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import run.Main;

public class DomFrame extends JFrame {

    public XmlJTree myTree;

    public DomFrame(String name){
        super(name);
    }



    @Override
    protected void processWindowEvent(final WindowEvent e) {

        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            if(myTree != null) {
                if (myTree.absXpath != null) {
                    try {
                        myTree.js.executeScript("arguments[0].setAttribute('style','background:; border: 0px solid blue;');", run.Main.driver.findElement(By.xpath(myTree.absXpath)));
                    } catch (Exception e2) {
                    }
                }
                try {
                    Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
                    System.exit(0);


        }
    }
}
