package com.patetlex.displayphoenix.ui.panel;

import com.patetlex.displayphoenix.Application;
import com.patetlex.displayphoenix.ui.widget.FadeOnHoverWidget;
import com.patetlex.displayphoenix.ui.widget.RoundedButton;
import com.patetlex.displayphoenix.ui.widget.TextField;
import com.patetlex.displayphoenix.util.ComponentHelper;
import com.patetlex.displayphoenix.util.ImageHelper;
import com.patetlex.displayphoenix.util.PanelHelper;
import org.eclipse.jgit.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class MutableListPanel extends JPanel {

    private JList<String> list;
    private List<Runnable> runOnUpdate = new ArrayList<>();
    private List<Consumer<String>> runOnRemove = new ArrayList<>();

    public MutableListPanel(String... values) {
        this(null, values);
    }
    public MutableListPanel(@Nullable MouseListener addListener, String... values) {
        this.list = ComponentHelper.createJList(new Renderer());

        FadeOnHoverWidget addButton = new FadeOnHoverWidget(new ImageIcon(ImageHelper.overlay(ImageHelper.getImage(Application.getTheme().getWidgetStyle().getName() + "_plus").getImage(), Application.getTheme().getColorTheme().getAccentColor(), 1F)), new ImageIcon(ImageHelper.overlay(ImageHelper.getImage(Application.getTheme().getWidgetStyle().getName() + "_plus").getImage(), Color.GREEN, 1F)), 0.01F);
        FadeOnHoverWidget removeButton = new FadeOnHoverWidget(new ImageIcon(ImageHelper.rotate(ImageHelper.overlay(ImageHelper.getImage(Application.getTheme().getWidgetStyle().getName() + "_plus").getImage(), Application.getTheme().getColorTheme().getAccentColor(), 1F), 45)), new ImageIcon(ImageHelper.rotate(ImageHelper.overlay(ImageHelper.getImage(Application.getTheme().getWidgetStyle().getName() + "_plus").getImage(), Color.RED, 1F), 45)), 0.01F);

        addButton.setPreferredSize(new Dimension(50, 50));
        removeButton.setPreferredSize(new Dimension(50, 50));

        if (addListener == null) {
            Callable<String> callable;
            Component component;
            if (values == null || values.length == 0) {
                TextField field = new TextField("Value");
                field.setPreferredSize(new Dimension(200, 35));
                component = field;
                callable = new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return field.getText();
                    }
                };
            } else {
                JComboBox<String> possibleValues = new JComboBox<>(values);
                component = possibleValues;
                callable = new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return (String) possibleValues.getSelectedItem();
                    }
                };
            }
            addButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    Application.openWindow("Add", JFrame.DISPOSE_ON_CLOSE, parentFrame -> {
                        JLabel addLabel = new JLabel("Add value");
                        ComponentHelper.themeComponent(addLabel);

                        RoundedButton add = new RoundedButton("Add");
                        add.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                super.mouseClicked(e);
                                parentFrame.dispose();
                                try {
                                    ((DefaultListModel) MutableListPanel.this.getList().getModel()).addElement(callable.call());
                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                }
                                for (Runnable runnable : MutableListPanel.this.runOnUpdate) {
                                    runnable.run();
                                }
                            }
                        });

                        parentFrame.add(PanelHelper.northAndCenterElements(PanelHelper.join(addLabel), PanelHelper.northAndCenterElements(PanelHelper.join(component), PanelHelper.join(add))));
                    }, Math.round(Application.getTheme().getWidth() * 0.3F), Math.round(Application.getTheme().getHeight() * 0.3F));
                }
            });
        } else {
            addButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    addListener.mouseClicked(new MouseEvent(MutableListPanel.this, e.getID(), e.getWhen(), e.getModifiers(), e.getX(), e.getY(), e.getClickCount(), e.isPopupTrigger(), e.getButton()));
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    super.mousePressed(e);
                    addListener.mousePressed(new MouseEvent(MutableListPanel.this, e.getID(), e.getWhen(), e.getModifiers(), e.getX(), e.getY(), e.getClickCount(), e.isPopupTrigger(), e.getButton()));
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    super.mouseReleased(e);
                    addListener.mouseReleased(new MouseEvent(MutableListPanel.this, e.getID(), e.getWhen(), e.getModifiers(), e.getX(), e.getY(), e.getClickCount(), e.isPopupTrigger(), e.getButton()));
                }
            });
        }

        removeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (getList().getSelectedValue() != null) {
                    ((DefaultListModel) getList().getModel()).remove(getList().getSelectedIndices()[0]);
                }
                for (Runnable runnable : MutableListPanel.this.runOnUpdate) {
                    runnable.run();
                }
                for (Consumer<String> runnable : MutableListPanel.this.runOnRemove) {
                    runnable.accept(getList().getSelectedValue());
                }
            }
        });

        add(PanelHelper.northAndCenterElements(PanelHelper.join(PanelHelper.join(addButton), PanelHelper.join(removeButton)), this.list));
    }

    public JList<String> getList() {
        return list;
    }

    public void addUpdateListener(Runnable runnable) {
        this.runOnUpdate.add(runnable);
    }

    public void addRemoveListener(Consumer<String> runnable) {
        this.runOnRemove.add(runnable);
    }

    private static class Renderer extends JLabel implements ListCellRenderer<String> {
        @Override
        public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
            setFont(Application.getTheme().getFont());
            setForeground(Application.getTheme().getColorTheme().getTextColor());
            setOpaque(isSelected);
            setBackground(Application.getTheme().getColorTheme().getSecondaryColor());
            setText(value);
            setHorizontalAlignment(SwingConstants.CENTER);
            return this;
        }
    }
}
