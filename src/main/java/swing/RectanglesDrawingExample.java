package swing;

import javax.swing.*;

public class RectanglesDrawingExample  extends JFrame {
/*
    public RectanglesDrawingExample(int x, int y, int w, int h) {
        getContentPane().setBackground(Color.GREEN);
        setUndecorated(true);
        setOpacity(0.55f);
        setSize(w, h);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocation(x,y);
        this.addMouseListener(new MouseAdapter(){

            @Override
            public void mousePressed(MouseEvent e) {

                if (Main.watch == true) {
                    Main.watch = false;
                    if (Main.watchThread.underMouse != null) {
                        String xpath = Main.driver.getAbsoluteXpath(Main.watchThread.underMouse);

                        Elements searchedForElements = Xsoup.compile(xpath).evaluate(Main.doc).getElements();
                        if (searchedForElements.size() == 0) {
                            Main.refreshTree();
                            searchedForElements = Xsoup.compile(xpath).evaluate(Main.doc).getElements();
                        }

                        Main.selectSearchedElement(searchedForElements, 0);
                        dispose();
                    }
                }
                if(Main.windowWatch==true){
                    System.out.println(WindowUtils.getWindowTitle(WindowWatchRunnable.underMouse));
                    System.out.println(Long.decode(WindowWatchRunnable.underMouse.toString().substring(7)).toString());
                    IntByReference byRef = new IntByReference();
                    User32.INSTANCE.GetWindowThreadProcessId(WindowWatchRunnable.underMouse,byRef);
                    System.out.println(byRef.getValue());
                    Main.windowWatch=false;
                    dispose();
                    Main.findDrivers(WindowWatchRunnable.underMouse);
                   // Main.findDrivers();
                }
                else{
                    dispose();
                }

            }


        });

    }



    public static void main(String[] args) throws Exception {

        new RectanglesDrawingExample(0,0,200,200).setVisible(true);
    }



 */
}

