package swing;

import javax.swing.*;
import java.awt.*;

public class Loading {
}
/*
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            JFrame frame = new JFrame("EY Spy");
            frame.setLayout(new BorderLayout());
            ImageIcon img = new ImageIcon(System.getProperty("user.dir")+"\\icons\\EYlogo1.jpg");
            frame.setIconImage(img.getImage());
            frame.add(new ImagePanel(), BorderLayout.CENTER);
            frame.add(new JLabel("Loading EY Spy"), BorderLayout.SOUTH);
            frame.setPreferredSize(new Dimension(600,400));
            frame.pack();
            frame.setVisible(true);

            while(loadingWindowsDriver){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            frame.setVisible(false);

        }
    });
        thread.start();

 */
