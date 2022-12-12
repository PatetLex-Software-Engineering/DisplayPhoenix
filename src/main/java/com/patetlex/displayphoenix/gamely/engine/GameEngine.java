package com.patetlex.displayphoenix.gamely.engine;

import com.patetlex.displayphoenix.gamely.Gamely;
import com.patetlex.displayphoenix.gamely.obj.Camera;
import com.patetlex.displayphoenix.gamely.obj.GameObject;
import com.patetlex.displayphoenix.gamely.physics.GamePhysics;
import com.patetlex.displayphoenix.gamely.ui.GamePanel;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class GameEngine {

    public static int MAXIMUM_LIGHT_ITERATIONS = 4;

    protected Map<Integer, GameObject> objs = new HashMap<>();
    protected List<GameObject> lightObjs = new ArrayList<>();
    private List<Integer> objsToRemove = new ArrayList<>();

    private Camera camera;

    private GamePanel panel;

    private GamePhysics physics;

    private int tickRate;
    private int ticksExisted;

    private boolean isRunning;
    public boolean shouldTick;

    private List<Wait> waitList = new ArrayList<>();

    public abstract void render(Graphics2D g);

    public abstract void tick();

    public GameEngine(GamePhysics physics, int tickRate) {
        this.physics = physics;
        this.tickRate = tickRate;
        this.camera = new Camera();
        this.camera.addedToEngine(-1, this);
    }

    public int addGameObject(GameObject obj) {
        int id = this.objs.size();
        if (this.objs.containsKey(id)) {
            for (int i = 0; i <= this.objs.size(); i++) {
                if (!this.objs.containsKey(i)) {
                    id = i;
                }
            }
        }
        obj.addedToEngine(id, this);
        this.objs.put(id, obj);
        if (obj.getLight() != null) {
            if (!obj.getLight().getLight().equals(0, 0, 0, 0)) {
                this.lightObjs.add(obj);
            }
        }
        return id;
    }

    public void removeGameObject(int id) {
        this.objsToRemove.add(id);
    }

    public Map<Integer, GameObject> getGameObjects() {
        return this.objs;
    }

    public void addLightSource(GameObject obj) {
        this.lightObjs.add(obj);
    }

    public void start(GamePanel panel) {
        this.panel = panel;

        this.isRunning = true;
        this.shouldTick = true;

        // Start Game Thread
        new Thread(() -> {
            long lastTime = System.nanoTime();
            final double ns = 1000000000D / this.tickRate;
            double delta = 0;
            while (panel.isVisible() || GameEngine.this.shouldBreak()) {
                long now = System.nanoTime();
                delta += (now - lastTime) / ns;
                lastTime = now;
                panel.repaint();
                while (delta >= 1) {

                    if (this.shouldTick)
                        this.tickEngine();

                    this.ticksExisted++;

                    delta--;
                }
                panel.repaint();
            }
            GameEngine.this.end();
        }).start();
    }

    protected void end() {

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

    public IDebugRenderer getDebugRenderer() {
        return new IDebugRenderer() {
            @Override
            public void debugRender() {

            }
        };
    }

    public Camera getCamera() {
        return camera;
    }

    public boolean shouldBreak() {
        return false;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    protected static class Wait {
        protected int ticksRemaining;
        protected Consumer<GameEngine> consumer;

        protected Wait(int ticksRemaining, Consumer<GameEngine> consumer) {
            this.ticksRemaining = ticksRemaining;
            this.consumer = consumer;
        }
    }

    public interface IDebugRenderer {
        void debugRender();
    }
}
