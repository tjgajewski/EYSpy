package swing;

import javax.swing.*;
import javax.swing.plaf.metal.MetalIconFactory;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class IconListRenderer extends DefaultListCellRenderer {

        private Map<Object, Icon> icons = null;

        public IconListRenderer(Map<Object, Icon> icons) {
            this.icons = icons;
        }

        @Override
        public Component getListCellRendererComponent(
                JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            // Get the renderer component from parent class

            JLabel label =
                    (JLabel) super.getListCellRendererComponent(list,
                            value, index, isSelected, cellHasFocus);

            // Get icon to use for the list item value
            if(icons.containsKey(value)) {
                Icon icon = icons.get(value);
                label.setIcon(icon);
            }
            return label;
        }

    }
