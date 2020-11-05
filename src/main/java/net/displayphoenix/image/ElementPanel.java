package net.displayphoenix.image;

import net.displayphoenix.Application;
import net.displayphoenix.image.elements.Element;
import net.displayphoenix.util.ComponentHelper;
import net.displayphoenix.util.PanelHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

public class ElementPanel extends JPanel {

    public ElementPanel(CanvasPanel canvas, Element... elements) {
        setOpaque(true);
        setBackground(Application.getTheme().getColorTheme().getPrimaryColor());
        setForeground(canvas.getForeground());

        JList<Element> elementList = ComponentHelper.createJList(new ElementRenderer(), Arrays.asList(elements.clone()));
        elementList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2) {
                    Element element = elementList.getSelectedValue();
                    Element clone = element.clone();
                    canvas.addElement(clone);
                }
            }
        });
        elementList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        elementList.setOpaque(false);
        ComponentHelper.addScrollPane(elementList);
        JPanel elementListPanel = PanelHelper.join(elementList);
        elementListPanel.setBackground(canvas.getBackground());
        elementListPanel.setForeground(canvas.getForeground());
        elementListPanel.setOpaque(false);
        add(elementListPanel);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    private static class ElementRenderer implements ListCellRenderer<Element> {
        @Override
        public Component getListCellRendererComponent(JList<? extends Element> list, Element value, int index, boolean isSelected, boolean cellHasFocus) {
            ElementComponent elementComponent = new ElementComponent(value);
            elementComponent.setPreferredSize(new Dimension(125, 125));
            elementComponent.setOpaque(isSelected);
            elementComponent.setBackground(Application.getTheme().getColorTheme().getAccentColor());
            return elementComponent;
        }
    }

    private static class ElementComponent extends JPanel {

        private CanvasPanel canvasToDraw;
        private Element element;

        public ElementComponent(Element element) {
            this.element = element;
            this.canvasToDraw = new CanvasPanel(this.getWidth(), this.getHeight());
        }


        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            this.canvasToDraw.setPreferredSize(new Dimension(this.element.getWidth(this.canvasToDraw, g), this.element.getHeight(this.canvasToDraw, g)));
            this.element.draw(this.canvasToDraw, g);
        }
    }
}
