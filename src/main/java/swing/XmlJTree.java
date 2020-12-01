package swing;

import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import org.openqa.selenium.NoSuchElementException;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class XmlJTree extends JTree {

    DefaultTreeModel dtModel = null;
    JavascriptExecutor js = ((JavascriptExecutor) run.Main.driver);


    public XmlJTree(Element root) {
        if (root != null)
            setPath(root);



        addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                ElementMutableTreeNode node = (ElementMutableTreeNode) getLastSelectedPathComponent();
                if(node != null) {
                    Element element = (Element) node.getUserObject();
                    String absXpath = buildAbsXpath(element);
                    node.absXpath = absXpath;
                    changeEleColor(absXpath);
                }
                // getJS().executeScript("arguments[0].setAttribute('style','background:" + color + "; border: 0px solid blue;');", element);
                // Thread.sleep(100);
                // getJS().executeScript("arguments[0].setAttribute('style','background:; border: 0px solid blue;');", element);

            }
        });
    }
    public String absXpath;
    public void changeEleColor(String absXpath){

unHighlightSelectedEle();

            try {
                js.executeScript("arguments[0].setAttribute('style','background:GreenYellow; border: 0px solid blue;');", run.Main.driver.findElement(By.xpath(absXpath)));
                this.absXpath = absXpath;
            } catch (NoSuchElementException e) {
                run.Main.refreshTree();
                this.absXpath = null;
            }

    }

    public void unHighlightSelectedEle(){
        if(this.absXpath != null) {
            try {
                js.executeScript("arguments[0].setAttribute('style','background:; border: 0px solid blue;');", run.Main.driver.findElement(By.xpath(this.absXpath)));
            } catch (NoSuchElementException e) {
            }
        }

    }

    public void setPath(Element root) {

        if (root != null) {
            dtModel = new DefaultTreeModel(buildNodes(root));
            this.setModel(dtModel);
            DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) this.getCellRenderer();
            renderer.setOpenIcon(null);
            renderer.setClosedIcon(null);
            renderer.setLeafIcon(null);
            setCellRenderer(renderer);
            BasicTreeUI ui = (BasicTreeUI) getUI();
            ui.setExpandedIcon(null);
            ui.setCollapsedIcon(null);
        }
    }

    private ElementMutableTreeNode buildNodes(Element element) {
        ElementMutableTreeNode node = new ElementMutableTreeNode(element);
            for (Element firstElement : element.children()) {
                node.add(buildNodes(firstElement));
            }
        return node;
    }

    public String buildAbsXpath(Element element){
        StringBuilder br = new StringBuilder();
        Element nextParent = element;
        br.append(tagId(element));
        for(int i = 0; i < element.parents().size(); i++){
            nextParent = nextParent.parent();
            br.insert(0, tagId(nextParent)+"/");
        }
        return br.toString();
    }

    public String tagId(Element element){
        String tagName = element.tagName();
        int tagIndex = 1;
        for(int i = 0; i < element.siblingIndex(); i++){
            String tagNameSib = element.siblingNodes().get(i).nodeName();
            if(tagName.equals(tagNameSib)){
                tagIndex++;
            }

        }
        if(tagIndex != 1){
            tagName = tagName + "[" + tagIndex + "]";
        }
        return tagName;
    }
    @Override
    public void scrollPathToVisible(TreePath path) {
        if(path != null) {
            makeVisible(path);

            Rectangle bounds = getPathBounds(path);
            bounds.x=0;

            if(bounds != null) {
                scrollRectToVisible(bounds);
                if (accessibleContext != null) {
                    ((AccessibleJTree)accessibleContext).fireVisibleDataPropertyChange();
                }
            }
        }
    }

}

class MultiLineCellRenderer extends DefaultTreeCellRenderer implements TreeCellRenderer {
    protected JLabel icon;

    protected TreeTextArea text;

    public MultiLineCellRenderer() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        icon = new JLabel() {
            public void setBackground(Color color) {
                if (color instanceof ColorUIResource)
                    color = null;
                super.setBackground(color);
            }
        };
        add(icon);
        add(Box.createHorizontalStrut(4));
        add(text = new TreeTextArea());
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean isSelected, boolean expanded, boolean leaf, int row,
                                                  boolean hasFocus) {
        String stringValue = tree.convertValueToText(value, isSelected,
                expanded, leaf, row, hasFocus);
        setEnabled(tree.isEnabled());
        text.setText(stringValue);
        text.setSelect(isSelected);
        text.setFocus(hasFocus);
        if (leaf) {
            icon.setIcon(UIManager.getIcon("Tree.leafIcon"));
        } else if (expanded) {
            icon.setIcon(UIManager.getIcon("Tree.openIcon"));
        } else {
            icon.setIcon(UIManager.getIcon("Tree.closedIcon"));
        }
        return this;
    }

    public Dimension getPreferredSize() {
        Dimension iconD = icon.getPreferredSize();
        Dimension textD = text.getPreferredSize();
        int height = iconD.height < textD.height ? textD.height : iconD.height;
        return new Dimension(iconD.width + textD.width, height);
    }

    public void setBackground(Color color) {
        if (color instanceof ColorUIResource)
            color = null;
        super.setBackground(color);
    }

    class TreeTextArea extends JTextArea {
        Dimension preferredSize;

        TreeTextArea() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
        }

        public void setBackground(Color color) {
            if (color instanceof ColorUIResource)
                color = null;
            super.setBackground(color);
        }

        public void setPreferredSize(Dimension d) {
            if (d != null) {
                preferredSize = d;
            }
        }

        public Dimension getPreferredSize() {
            return preferredSize;
        }

        public void setText(String str) {
            FontMetrics fm = getToolkit().getFontMetrics(getFont());
            BufferedReader br = new BufferedReader(new StringReader(str));
            String line;
            int maxWidth = 0, lines = 0;
            try {
                while ((line = br.readLine()) != null) {
                    int width = SwingUtilities.computeStringWidth(fm, line);
                    if (maxWidth < width) {
                        maxWidth = width;
                    }
                    lines++;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            lines = (lines < 1) ? 1 : lines;
            int height = fm.getHeight() * lines;
            setPreferredSize(new Dimension(maxWidth + 6, height));
            super.setText(str);
        }

        void setSelect(boolean isSelected) {
            Color bColor;
            if (isSelected) {
                bColor = UIManager.getColor("Tree.selectionBackground");
            } else {
                bColor = UIManager.getColor("Tree.textBackground");
            }
            super.setBackground(bColor);
        }

        void setFocus(boolean hasFocus) {
            if (hasFocus) {
                Color lineColor = UIManager
                        .getColor("Tree.selectionBorderColor");
                setBorder(BorderFactory.createLineBorder(lineColor));
            } else {
                setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
            }
        }
    }
}
