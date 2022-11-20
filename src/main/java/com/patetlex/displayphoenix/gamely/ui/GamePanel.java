package com.patetlex.displayphoenix.gamely.ui;

import com.patetlex.displayphoenix.gamely.engine.GameEngine;
import org.apache.xmlgraphics.java2d.DefaultGraphics2D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GamePanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener {

    private GameEngine engine;

    private Dimension resolution;

    private Line2D.Float moveLine = new Line2D.Float(0, 0, 0, 0);
    private Map<Integer, Boolean> downKeys = new HashMap<>();
    private Map<Integer, Boolean> downMouse = new HashMap<>();
    private Map<Integer, Line2D.Float> draggedLines = new HashMap<>();

    public GamePanel(Dimension resolution, GameEngine engine) {
        this.setBackground(Color.BLACK);

        this.resolution = resolution;
        this.engine = engine;

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addKeyListener(this);

        this.setFocusable(true);
        this.requestFocus();

        for (int i = 0; i < 200; i++) {
            this.downKeys.put(i, false);
            this.downMouse.put(i, false);
        }
    }

    public Dimension getResolution() {
        return resolution;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        double r = this.getScalingFactor();
        ((Graphics2D) g).translate((double) Math.round((this.getWidth() - this.getScaledResolution().getWidth()) / 2F), (double) Math.round((this.getHeight() - this.getScaledResolution().getHeight()) / 2F));
        ((Graphics2D) g).scale(r, r);
        int size = (int) (this.resolution.getHeight() / Math.min(100, this.getResolution().getHeight()));
        int j = 0;
        for (int xp = 0; xp < this.resolution.getWidth(); xp += size) {
            int k = 0;
            for (int yp = 0; yp < this.resolution.getHeight(); yp += size) {
                float v = k + j;
                g.setColor(v % 2 == 0 ? Color.BLACK : Color.MAGENTA.darker().darker());
                g.fillRect(xp, yp, size, size);
                k++;
            }
            j++;
        }
        ((Graphics2D) g).setClip(0, 0, (int) this.resolution.getWidth(), (int) this.resolution.getHeight());

        renderEngine(g);
    }

    protected void renderEngine(Graphics g) {
        if (!this.engine.isRunning()) {
            this.engine.start(this);
        }
        this.engine.render((Graphics2D) g);
    }

    public GameEngine getEngine() {
        return engine;
    }

    public Dimension getScaledResolution() {
        double r = this.getScalingFactor();
        return new Dimension((int) (this.getResolution().getWidth() * r), (int) (this.getResolution().getHeight() * r));
    }

    public double getScalingFactor() {
        double r0 = this.getWidth() / this.getResolution().getWidth();
        double r1 = this.getHeight() / this.getResolution().getHeight();
        return r0 < r1 ? r0 : r1;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        this.downKeys.put(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        this.downKeys.put(e.getKeyCode(), false);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        this.requestFocus();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.downMouse.put(e.getButton(), true);
        this.draggedLines.put(e.getButton(), new Line2D.Float(e.getX(), e.getY(), e.getX(), e.getY()));
        this.moveLine = new Line2D.Float(this.moveLine.getP2(), new Point2D.Float(e.getX(), e.getY()));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.downMouse.put(e.getButton(), false);
        Line2D.Float draggedLine = this.draggedLines.get(e.getButton());
        if (draggedLine != null) {
            dragReleased(e, draggedLine);
            this.draggedLines.put(e.getButton(), null);
        }
        this.moveLine = new Line2D.Float(this.moveLine.getP2(), new Point2D.Float(e.getX(), e.getY()));
    }

    public void dragReleased(MouseEvent e, Line2D.Float draggedLine) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        for (int button : this.draggedLines.keySet()) {
            if (this.draggedLines.get(button) != null) {
                Line2D.Float line = this.draggedLines.get(button);
                this.draggedLines.put(button, new Line2D.Float(line.getP1(), new Point2D.Float(e.getX(), e.getY())));
                this.repaint();
            }
        }
        this.moveLine = new Line2D.Float(this.moveLine.getP2(), new Point2D.Float(e.getX(), e.getY()));
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        this.moveLine = new Line2D.Float(this.moveLine.getP2(), new Point2D.Float(e.getX(), e.getY()));
    }

    public Map<Integer, Boolean> getDownKeys() {
        return Collections.unmodifiableMap(this.downKeys);
    }

    public Map<Integer, Boolean> getDownMouseButtons() {
        return Collections.unmodifiableMap(this.downMouse);
    }

    public Map<Integer, Line2D.Float> getDraggedLines() {
        return Collections.unmodifiableMap(this.draggedLines);
    }

    public Line2D.Float getMoveLine() {
        Line2D.Float move = this.moveLine;
        this.moveLine = new Line2D.Float(this.moveLine.getP2(), this.moveLine.getP2());
        return move;
    }
}
