package infrastructure.thread;

import application.component.appfinder.AppListPanel;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;
import infrastructure.Highlighter;
import run.Main;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class WindowUnderCursorHighlighThread implements Runnable {


   public static WinDef.HWND underMouse = null;

    public void run() {
        while(ThreadManager.windowThreadToggle){

                WinDef.HWND element = AppListPanel.getWindowUnderCursor();
                if (element != null && (underMouse == null || !underMouse.toString().equals(element.toString()))) {


                   Highlighter.highlightWindow(element);

                    underMouse = element;
                }
        }

    }




}

