package net.displayphoenix.canvasly;

import net.displayphoenix.canvasly.elements.Element;
import net.displayphoenix.canvasly.elements.Layer;
import net.displayphoenix.canvasly.elements.StaticElement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class ElementPanel extends JPanel {

    public ElementPanel(CanvasPanel canvas, StaticElement... elements) {
        setOpaque(true);
        setForeground(canvas.getForeground());

        JList<StaticElement> elementList = createJList(new ElementRenderer(), Arrays.asList(elements.clone()));
        elementList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2) {
                    StaticElement element = elementList.getSelectedValue();
                    Element clone = element.getElement().clone();
                    canvas.addStaticElement(canvas.getSelectedLayer(), new StaticElement(clone, element.getX(), element.getY(), element.getProperties()));
                }
            }
        });
        elementList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        elementList.setOpaque(false);
        JScrollPane scrollBar = new JScrollPane(elementList);
        scrollBar.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JPanel skup = new JPanel(new FlowLayout(FlowLayout.CENTER));
        skup.setOpaque(false);
        skup.add(elementList);
        JPanel elementListPanel = skup;
        elementListPanel.setBackground(canvas.getBackground());
        elementListPanel.setForeground(canvas.getForeground());
        elementListPanel.setOpaque(false);
        add(elementListPanel);
    }

    protected Layer getLayerToAdd(CanvasPanel canvas) {
        return canvas.getSelectedLayer();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    private static <T> JList<T> createJList(ListCellRenderer<T> cellRenderer, Iterable<T> values) {
        DefaultListModel<T> listModel = new DefaultListModel<>();
        for (T val : values) {
            listModel.addElement(val);
        }
        JList<T> list = new JList<>(listModel);
        list.setCellRenderer(cellRenderer);
        return list;
    }

    private static class ElementRenderer implements ListCellRenderer<StaticElement> {

        @Override
        public Component getListCellRendererComponent(JList<? extends StaticElement> list, StaticElement value, int index, boolean isSelected, boolean cellHasFocus) {
            ElementComponent elementComponent = new ElementComponent(value);
            elementComponent.setPreferredSize(new Dimension(125, 125));
            elementComponent.setOpaque(isSelected);
            elementComponent.setBackground(Color.GRAY);
            return elementComponent;
        }
    }

    private static class ElementComponent extends JPanel {

        private CanvasPanel canvasToDraw;
        private Element element;

        public ElementComponent(StaticElement element) {
            this.element = element.getElement();
            this.canvasToDraw = new CanvasPanel(this.getWidth(), this.getHeight());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            float r = (float) getWidth() / (float) this.element.getWidth(this.canvasToDraw, g);
            if (this.element.getHeight(this.canvasToDraw, g) > this.element.getWidth(this.canvasToDraw, g)) {
                r = (float) getHeight() / (float) this.element.getHeight(this.canvasToDraw, g);
            }
            this.canvasToDraw.setPreferredSize(new Dimension(this.element.getWidth(this.canvasToDraw, g), this.element.getHeight(this.canvasToDraw, g)));
            ((Graphics2D) g).translate(getWidth() / 2F, getHeight() / 2F);
            ((Graphics2D) g).translate(-(this.element.getWidth(this.canvasToDraw, g) * r) / 2F, -(this.element.getHeight(this.canvasToDraw, g) * r) / 2F);
            ((Graphics2D) g).scale(r, r);
            this.element.draw(this.canvasToDraw, g);
        }
    }
}
