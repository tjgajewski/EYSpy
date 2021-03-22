package dotnet;

import application.element.factory.WindowsElement;
import ey.managedspy.RemoteObject;
import microsoft.managedspy.ControlProxy;
import org.openqa.selenium.WebElement;
import system.reflection.Assembly;

/**
 * The type Dot net element.
 */
public class DotNetElement {

    /**
     * The Proxy.
     */
    public ControlProxy proxy;

    /**
     * Instantiates a new Dot net element.
     *
     * @param element the element
     */
    public DotNetElement(WindowsElement element){
        ManagedSpyLib.init();
        proxy = ControlProxy.FromHandle(Long.decode(element.getAttribute("hwnd")));
    }

    /**
     * Instantiates a new Dot net element.
     *
     * @param element the element
     */
    public DotNetElement(WebElement element){
        ManagedSpyLib.init();
        proxy = ControlProxy.FromHandle(Long.decode(element.getAttribute("hwnd")));
    }

    /**
     * Get attribute string.
     *
     * @param attribute the attribute
     * @return the string
     */
    public String getAttribute(String attribute){
        return proxy.GetValue(attribute).toString();
    }


    /**
     * Click.
     */
    public void click(){
        //Load the Remote Object Assembly
        Assembly assembly = Assembly.Load("System.Windows.Forms, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089");

        //Create the mouse buttons parameter
        //URL: https://docs.microsoft.com/en-us/dotnet/api/system.windows.forms.mousebuttons?view=netcore-3.1
        RemoteObject mouseButtons = new RemoteObject();
        mouseButtons.settypeName("System.Windows.Forms.MouseButtons");
        mouseButtons.setassembly(assembly.getLocation());
        mouseButtons.setgetType("property");
        mouseButtons.setname("Left");

        String xdim = String.valueOf(proxy.GetValue("Width"));
        String ydim = String.valueOf(proxy.GetValue("Height"));
        int middlex = (int)(Double.parseDouble(xdim)/2);
        int middley = (int)(Double.parseDouble(ydim)/2);
        
        //Create the Mouse Arguments Parameter
        //URL: https://docs.microsoft.com/en-us/dotnet/api/system.windows.forms.mouseeventargs?view=netcore-3.1
        RemoteObject mouseEventArgs = new RemoteObject();
        mouseEventArgs.settypeName("System.Windows.Forms.MouseEventArgs");
        mouseEventArgs.setassembly(assembly.getLocation());
        mouseEventArgs.setparams(new system.Object[] {mouseButtons, Int32.parse(1), Int32.parse(middlex), Int32.parse(middley), Int32.parse(1)});
        proxy.Invoke("OnMouseDown", new system.Object[] {mouseEventArgs});
        proxy.Invoke("OnMouseUp", new system.Object[] {mouseEventArgs});
    }

    /**
     * Rightclick.
     */
    public void rightclick(){
        //Load the Remote Object Assembly
        Assembly assembly = Assembly.Load("System.Windows.Forms, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089");

        //Create the mouse buttons parameter
        //URL: https://docs.microsoft.com/en-us/dotnet/api/system.windows.forms.mousebuttons?view=netcore-3.1
        RemoteObject mouseButtons = new RemoteObject();
        mouseButtons.settypeName("System.Windows.Forms.MouseButtons");
        mouseButtons.setassembly(assembly.getLocation());
        mouseButtons.setgetType("property");
        mouseButtons.setname("Right");

        String xdim = String.valueOf(proxy.GetValue("Width"));
        String ydim = String.valueOf(proxy.GetValue("Height"));
        int middlex = (int)(Double.parseDouble(xdim)/2);
        int middley = (int)(Double.parseDouble(ydim)/2);
        
        //Create the Mouse Arguments Parameter
        //URL: https://docs.microsoft.com/en-us/dotnet/api/system.windows.forms.mouseeventargs?view=netcore-3.1
        RemoteObject mouseEventArgs = new RemoteObject();
        mouseEventArgs.settypeName("System.Windows.Forms.MouseEventArgs");
        mouseEventArgs.setassembly(assembly.getLocation());
        mouseEventArgs.setparams(new system.Object[] {mouseButtons, Int32.parse(1), Int32.parse(middlex), Int32.parse(middley), Int32.parse(1)});
        proxy.Invoke("OnMouseDown", new system.Object[] {mouseEventArgs});
        //proxy.Invoke("OnMouseUp", new system.Object[] {mouseEventArgs});
    }

    /**
     * Set text as.
     *
     * @param text the text
     */
    public void setTextAs(String text){
        proxy.SetValue("Text", new system.String(text));
    }

    /**
     * Get text string.
     *
     * @return the string
     */
    public String getText(){
    	String text =  String.valueOf(proxy.GetValue("Text"));
    	//proxy.SetValue("Text", new system.String(text));
    	System.out.println(text);
   return text;
    }

    /**
     * Get name string.
     *
     * @return the string
     */
    public String getName(){
    	String name =  String.valueOf(proxy.GetValue("Name"));
    	//proxy.SetValue("Text", new system.String(name));
    	System.out.println(name);
   return name;
    }
    
}
