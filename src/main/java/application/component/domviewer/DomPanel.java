package application.component.domviewer;

import application.element.factory.WindowsElement;
import infrastructure.ObjectSpyPanel;
import infrastructure.thread.ElementUnderCursorHighlightThread;
import infrastructure.thread.ThreadManager;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import selenium.SpyDriver;
import swing.ElementMutableTreeNode;
import swing.XmlJTree;
import us.codecraft.xsoup.Xsoup;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class DomPanel extends ObjectSpyPanel {

    XmlJTree elementTree;
    PropertiesPanel propertiesPanel;
    public SpyDriver driver;
    InspectPanel inspectPanel;

    ObjectSpyPanel centerPanel;

    public Element doc;
    public Elements searchedForElements = new Elements();
    public int focusedSearchedElement=0;


    public DomPanel(SpyDriver driver){
setLayout(new BorderLayout());
        this.driver = driver;
        JPanel btnMenu = new JPanel(new FlowLayout(FlowLayout.LEFT));
        elementTree = new XmlJTree(null, driver);
        JScrollPane scrollPane = new JScrollPane(elementTree, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        JButton watchBtn = new JButton("Watch");
        watchBtn.setMnemonic('e');

        watchBtn.addActionListener(e -> {
            if(elementTree.absXpath!=null) {
                driver.unhighlight();
            }
            ThreadManager.eleThreadToggle = true;
            ThreadManager.eleThread = new ElementUnderCursorHighlightThread(this);
            Thread t = new Thread(ThreadManager.eleThread);
            t.start();

        });
        btnMenu.add(watchBtn);
        JButton viewBtn = new JButton("View");

        viewBtn.addActionListener(e -> {
            if(inspectPanel.isVisible()) {
                ElementMutableTreeNode node = (ElementMutableTreeNode) elementTree.getLastSelectedPathComponent();
                if (node != null) {
                    Element element = (Element) node.getUserObject();
                    int maxRow = element.attributes().size();
                    boolean hasText = !element.text().equals("");
                    if (hasText) {
                        maxRow++;
                    }

                    List<Attribute> attributeList = element.attributes().asList();
                    Object[][] props = new Object[maxRow + 2][2];
                    int i;
                    for (i = 0; i < attributeList.size(); i++) {
                        Attribute attribute = attributeList.get(i);
                        props[i][0] = attribute.getKey();
                        props[i][1] = attribute.getValue();
                    }
                    if (hasText) {
                        props[i][0] = "text";
                        props[i][1] = element.text();
                        i++;
                    }

                    props[i][0] = "tag";
                    props[i][1] = element.tagName();
                    i++;
                    props[i][0] = "abs xpath";
                    props[i][1] = node.absXpath;

                    Object[] colHeaders = new Object[2];
                    colHeaders[0] = "Property";
                    colHeaders[1] = "Value";
                    JTable table = new JTable(props, colHeaders);
                    if(propertiesPanel==null){
                        propertiesPanel = new PropertiesPanel();
                        propertiesPanel.setLayout(new BorderLayout());


                    }
                    add(propertiesPanel,BorderLayout.CENTER);
                   // f.add(propertiesPanel, BorderLayout.CENTER);
                    propertiesPanel.removeAll();

                    JScrollPane propertiesScrollGrid = new JScrollPane(table);
                    propertiesPanel.add(propertiesScrollGrid, BorderLayout.CENTER);
                    propertiesPanel.add(btnMenu, BorderLayout.NORTH);
                    //    f.pack();
                    propertiesPanel.setVisible(true);
                    inspectPanel.setVisible(false);
                    viewBtn.setText("Back");




                }
            } else {
                //  f.pack();add(inspectPanel, BorderLayout.CENTER);

                add(inspectPanel, BorderLayout.CENTER);
                propertiesPanel.setVisible(false);
                inspectPanel.add(btnMenu, BorderLayout.NORTH);
                inspectPanel.setVisible(true);
                // f.remove(propertiesPanel);
                //  f.add(inpsectPanel, BorderLayout.CENTER);
                viewBtn.setText("View");
                //  f.remove(propertiesPanel);

                nullifyTreeIcons();
            }



            //Object[] optionsBtns = {"OK"};
            //JOptionPane.showOptionDialog(frame, new JScrollPane(table), "View Properties", JOptionPane.PLAIN_MESSAGE, JOptionPane.PLAIN_MESSAGE, null, optionsBtns, optionsBtns[0]);


        });
        btnMenu.add(viewBtn);
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> {
            refresh();
        });
        btnMenu.add(refreshBtn);

        JPanel searchPan = new JPanel(new BorderLayout());
        JTextField field = new JTextField( 19);
        //searchPan.add(field, BorderLayout.CENTER);
        JButton goSearchBtn = new JButton("GO");
        JButton nextBtn = new JButton(">");
        JButton prevBtn = new JButton("<");
        JLabel label = new JLabel(focusedSearchedElement + " of " + searchedForElements.size() + " results");
        JPanel subSearchPanButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        subSearchPanButtons.add(goSearchBtn);
        subSearchPanButtons.add(label);
        subSearchPanButtons.add(prevBtn);
        subSearchPanButtons.add(nextBtn);
        JPanel searchTextFieldPan = new JPanel(new BorderLayout());
        searchTextFieldPan.add(field, BorderLayout.CENTER);
        searchTextFieldPan.setBorder(BorderFactory.createEmptyBorder(5,5,5,0));
        searchPan.add(searchTextFieldPan, BorderLayout.CENTER);
        searchPan.add(subSearchPanButtons, BorderLayout.EAST);



        nextBtn.setEnabled(false);
        nextBtn.addActionListener(z -> {
            focusedSearchedElement++;
            selectSearchedElement(searchedForElements, focusedSearchedElement);
            if(focusedSearchedElement+1==searchedForElements.size()){
                nextBtn.setEnabled(false);
            }
            if(focusedSearchedElement>0){
                prevBtn.setEnabled(true);
            }
            label.setText(focusedSearchedElement+1 + " of " + searchedForElements.size() + " results");
        });


        prevBtn.setEnabled(false);
        prevBtn.addActionListener(z -> {
            focusedSearchedElement--;
            selectSearchedElement(searchedForElements, focusedSearchedElement);
            if(focusedSearchedElement+1<searchedForElements.size()){
                nextBtn.setEnabled(true);
            }
            if(focusedSearchedElement==0){
                prevBtn.setEnabled(false);
            }
            label.setText(focusedSearchedElement+1 + " of " + searchedForElements.size() + " results");
        });

        goSearchBtn.addActionListener(z -> {
            try {
                searchedForElements = Xsoup.compile(field.getText()).evaluate(doc).getElements();
            }
            catch (Selector.SelectorParseException e){
            }
            if(searchedForElements.size()==0){
                searchByText(field.getText());
            }
            focusedSearchedElement = 0;
            if(searchedForElements.size()==0){
                label.setText(focusedSearchedElement + " of " + searchedForElements.size() + " results");
            }
            else {
                selectSearchedElement(searchedForElements, focusedSearchedElement);
                label.setText(focusedSearchedElement + 1 + " of " + searchedForElements.size() + " results");
            }
            if(searchedForElements.size()>1){
                nextBtn.setEnabled(true);
            }
            else{
                nextBtn.setEnabled(false);
            }
            prevBtn.setEnabled(false);
        });

        field.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    searchedForElements = Xsoup.compile(field.getText()).evaluate(doc).getElements();
                }
                catch (Selector.SelectorParseException e2){
                }
                if(searchedForElements.size()==0){
                    searchByText(field.getText());
                }
                focusedSearchedElement = 0;
                if(searchedForElements.size()==0){
                    label.setText(focusedSearchedElement + " of " + searchedForElements.size() + " results");
                }
                else {
                    selectSearchedElement(searchedForElements, focusedSearchedElement);
                    label.setText(focusedSearchedElement + 1 + " of " + searchedForElements.size() + " results");
                }
                if(searchedForElements.size()>1){
                    nextBtn.setEnabled(true);
                }
                else{
                    nextBtn.setEnabled(false);
                }
                prevBtn.setEnabled(false);

            }
        });

        inspectPanel = new InspectPanel();
        inspectPanel.add(btnMenu, BorderLayout.NORTH);
        inspectPanel.add(scrollPane, BorderLayout.CENTER);
        inspectPanel.add(searchPan, BorderLayout.SOUTH);

        add(inspectPanel, BorderLayout.CENTER);
        refresh();

    }

    @Override
    public void refresh() {
        driver.setXml(null);
        doc = driver.buildXmlHierarchy();
        elementTree.setPath(doc);
    }

    private void nullifyTreeIcons(){
        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) elementTree.getCellRenderer();
        renderer.setOpenIcon(null);
        renderer.setClosedIcon(null);
        renderer.setLeafIcon(null);
        elementTree.setCellRenderer(renderer);
        BasicTreeUI ui = (BasicTreeUI) elementTree.getUI();
        ui.setExpandedIcon(null);
        ui.setCollapsedIcon(null);
    }

    public void searchByText(String text){
        searchedForElements = new Elements();
        for (Element element : doc.getAllElements()){
            if(fullElementTag(element).contains(text)){
                searchedForElements.add(element);
            }
        }
    }
    public void selectSearchedElement(Elements searchedForElements, int focusedSearchedElement){
        for (Enumeration e = ((ElementMutableTreeNode) elementTree.getModel().getRoot()).depthFirstEnumeration(); e.hasMoreElements();) {
            ElementMutableTreeNode node = (ElementMutableTreeNode) e.nextElement();
            if (searchedForElements.get(focusedSearchedElement) == node.getUserObject()) {
                TreePath path = new TreePath(node.getPath());
                elementTree.getSelectionModel().setSelectionPath(path);
                if(!node.isRoot())
                    elementTree.expandPath(new TreePath(((ElementMutableTreeNode) node.getParent()).getPath()));
                elementTree.scrollPathToVisible(path);
            }
        }
    }

    public String fullElementTag(Element element){
        if(element.children().size()==0){
            return element.outerHtml();
        }
        String outerHtlml = element.outerHtml();
        int lastChar = outerHtlml.indexOf('>')+1;
        String s = outerHtlml.substring(0,lastChar);
        String endTag = "</"+element.tagName()+">";
        if(outerHtlml.trim().endsWith(endTag)){
            s=s+"..."+endTag;
        }
        return s;
    }
}
