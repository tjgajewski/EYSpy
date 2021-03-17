package application.component.appfinder;

import application.component.EYObjectSpyFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class NextAndBackPanel extends JPanel {

    private JButton nextBtn;
    private JButton backBtn;
    private JButton refreshButton;

    public NextAndBackPanel(){
        setLayout(new FlowLayout(FlowLayout.RIGHT));
        nextBtn = new JButton("Next");
        backBtn = new JButton("Back");
        refreshButton = new JButton("Refresh");
        add(refreshButton);
        add(backBtn);
        add(nextBtn);
        refreshButton.addActionListener(e -> EYObjectSpyFrame.refreshCenterPanel());
    }

    public void setNextBtnVisibility(boolean v){
        nextBtn.setVisible(v);
    }

    public void setBackBtnVisibility(boolean v){
        backBtn.setVisible(v);
    }

    private ActionListener oldL;
    public void setNextBtnAction(ActionListener l){
        if(oldL!=null);
        nextBtn.removeActionListener(oldL);
        nextBtn.addActionListener(l);
        oldL=l;
    }

    public void setBackBtnAction(ActionListener l){
        backBtn.addActionListener(l);
    }

    public void setBackBtnText(String text){
        backBtn.setText(text);
    }

    public void setBackBtnEnabled(boolean e){
        backBtn.setEnabled(e);
    }
}
