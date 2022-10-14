package com.patetlex.displayphoenix.gamely.engine;

import com.patetlex.displayphoenix.file.Data;
import com.patetlex.displayphoenix.gamely.Gamely;
import com.patetlex.displayphoenix.gamely.obj.Camera;
import com.patetlex.displayphoenix.gamely.obj.GameObject;
import com.patetlex.displayphoenix.gamely.physics.GamePhysics;
import com.patetlex.displayphoenix.gamely.ui.GamePanel;
import sun.security.krb5.internal.PAData;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class GameEngine {

    protected Map<Integer, GameObject> objs = new HashMap<>();
    private List<Integer> objsToRemove = new ArrayList<>();

    private Camera camera;

    private GamePanel panel;

    private GamePhysics physics;

    private int tickRate;
    private int ticksExisted;

    public boolean shouldTick;

    private List<Wait> waitList = new ArrayList<>();

    public abstract void render(Graphics2D g);
    public abstract void tick();

    public GameEngine(GamePhysics physics, int tickRate) {
        this.physics = physics;
        this.tickRate = tickRate;
        this.camera = new Camera();
    }

    public int addGameObject(GameObject obj) {
        for (int i = 0; i <= this.objs.size(); i++) {
            if (!this.objs.containsKey(i)) {
                obj.addedToEngine(i, this);
                this.objs.put(i, obj);
                return i;
            }
        }
        return -1;
    }

    public void removeGameObject(int id) {
        this.objsToRemove.add(id);
    }

    public Map<Integer, GameObject> getGameObjects() {
        return Collections.unmodifiableMap(this.objs);
    }

    public void start(GamePanel panel) {
        this.panel = panel;

        this.shouldTick = true;

        // Start Game Thread
        new Thread(() -> {
            long lastTime = System.nanoTime();
            final double ns = 1000000000D / this.tickRate;
            double delta = 0;
            while (panel.isVisible()) {
                long now = System.nanoTime();
                delta += (now - lastTime) / ns;
                lastTime = now;
                while (delta >= 1) {

                    if (this.shouldTick)
                        this.tickEngine();

                    this.ticksExisted++;

                    delta--;
                }
                panel.repaint();
            }
        }).start();
    }

    protected void tickEngine() {
        this.physics.moveObjects(new ArrayList<>(this.objs.values()));
        this.physics.moveCamera(this.camera);
        tickWait();
        this.tick();

        Map<Integer, GameObject> nObjs = new HashMap<>();
        for (int i : this.objs.keySet()) {
            if (!this.objsToRemove.contains(i)) {
                nObjs.put(i, this.objs.get(i));
            }
        }
        this.objs = nObjs;
        this.objsToRemove.clear();
    }

    protected void tickWait() {
        List<Wait> waitsToRemove = new ArrayList<>();
        for (Wait wait : this.waitList) {
            wait.ticksRemaining--;
            if (wait.ticksRemaining <= 0) {
                wait.consumer.accept(this);
                waitsToRemove.add(wait);
            }
        }
        for (Wait remove : waitsToRemove) {
            this.waitList.remove(remove);
        }
    }

    public void wait(int ticks, Consumer<GameEngine> consumer) {
        this.waitList.add(new Wait(ticks, consumer));
    }

    public Gamely.Save write() {
        Gamely.Save save = new Gamely.Save();

        save.put("camera", GameObject.handleWrite(this.camera));

        Gamely.Save objects = new Gamely.Save();
        for (int id : this.getGameObjects().keySet()) {
            objects.put(String.valueOf(id), GameObject.handleWrite(this.getGameObjects().get(id)));
        }
        save.put("objects", objects);

        return save;
    }

    public void read(Gamely.Save save) {
        this.objs.clear();

        this.camera = (Camera) GameObject.handleRead(save.get("camera"));

        Gamely.Save objects = save.get("objects");
        for (String key : objects.keys()) {
            int id = Integer.valueOf(key);
            GameObject object = GameObject.handleRead(objects.get(key));
            object.addedToEngine(id, this);
            this.objs.put(id, object);
        }
    }

    public int getTickRate() {
        return this.tickRate;
    }

    public int getTicksExisted() {
        return this.ticksExisted;
    }

    public GamePhysics getPhysics() {
        return physics;
    }

    public GamePanel getPanel() {
        return this.panel;
    }

    public Camera getCamera() {
        return camera;
    }

    protected static class Wait {
        protected int ticksRemaining;
        protected Consumer<GameEngine> consumer;

        protected Wait(int ticksRemaining, Consumer<GameEngine> consumer) {
            this.ticksRemaining = ticksRemaining;
            this.consumer = consumer;
        }
    }
}
