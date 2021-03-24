package application.component.appfinder;

import com.sun.jna.Pointer;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;
import infrastructure.Highlighter;
import infrastructure.ObjectSpyPanel;
import swing.IconListRenderer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppListPanel extends ObjectSpyPanel {

    private AppIdentifierPanel appIdentifierPanel;

    public static int getPid() {
        return pid;
    }

    private static int pid = Kernel32.INSTANCE.GetCurrentProcessId();

    public AppListPanel(AppIdentifierPanel appIdentifierPanel){
        this.appIdentifierPanel=appIdentifierPanel;
        setLayout(new BorderLayout());
        appIdentifierPanel.runningApps = new JList();
        refresh();
        appIdentifierPanel.runningApps.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                if(e.getValueIsAdjusting()) {
                    int index = appIdentifierPanel.runningApps.getSelectedIndex();
                    Highlighter.changeHighlightedWindow(appIdentifierPanel.windows.get(index).getHWND());
                }
            }
        });

       add(new JScrollPane(appIdentifierPanel.runningApps),BorderLayout.CENTER);


    }


    @Override
    public void refresh() {
        Map<Object, Icon> icons = new HashMap<>();
        java.util.List<DesktopWindow> allWins = WindowUtils.getAllWindows(true);
        List<String> titles = new ArrayList<>();
        appIdentifierPanel.windows = new ArrayList<>();
        for(int i=0;i<allWins.size();i++){
            DesktopWindow desktopWindow = allWins.get(i);
            WinDef.HWND hwnd = desktopWindow.getHWND();
            String title = WindowUtils.getWindowTitle(hwnd);
            IntByReference byRef = new IntByReference();
            User32.INSTANCE.GetWindowThreadProcessId(desktopWindow.getHWND(),byRef);
            if(byRef.getValue()!=pid&&!title.equals("")){
                appIdentifierPanel.windows.add(desktopWindow);
                titles.add(title);
                Icon icon = FileSystemView.getFileSystemView().getSystemIcon(new File(desktopWindow.getFilePath()));
                if(icon!=null)
                    icons.put(title,icon);
            }
        }
        appIdentifierPanel.runningApps.setListData(titles.toArray());
        appIdentifierPanel.runningApps.setCellRenderer(new IconListRenderer(icons));
    }

    public static WinDef.HWND getWindowUnderCursor(){
        WinDef.POINT point = new WinDef.POINT();
        User32.INSTANCE.GetCursorPos(point);
        Point point1 = new Point(point.x, point.y);
        List<WinDef.HWND> wins = new ArrayList<>();
        User32.INSTANCE.EnumWindows(new User32.WNDENUMPROC() {
            @Override
            public boolean callback(WinDef.HWND hwnd, Pointer pointer) {
                WinDef.RECT rect = new WinDef.RECT();
                User32.INSTANCE.GetWindowRect(hwnd, rect);
                IntByReference byRef = new IntByReference();
                User32.INSTANCE.GetWindowThreadProcessId(hwnd,byRef);
                if(byRef.getValue()!=pid) {
                    if (rect.toRectangle().contains(point1) && User32.INSTANCE.IsWindowVisible(hwnd)) {
                        wins.add(hwnd);
                    }
                }
                return true;
            }
        },null);


        return wins.get(0);
    }
}
