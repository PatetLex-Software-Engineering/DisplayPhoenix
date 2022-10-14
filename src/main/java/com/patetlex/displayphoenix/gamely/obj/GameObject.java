package com.patetlex.displayphoenix.gamely.obj;

import com.patetlex.displayphoenix.gamely.Gamely;
import com.patetlex.displayphoenix.gamely.engine.GameEngine;
import com.patetlex.displayphoenix.gamely.util.Vector3f;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public class GameObject {

    public Gamely.Save data;

    private Vector3f position;
    private Vector3f motion;
    private Vector3f size;
    private Vector3f rotation;

    private GameEngine engine;
    private int id;

    public GameObject(Vector3f position, Vector3f boundingSize) {
        this.position = position;
        this.size = boundingSize;
        this.motion = new Vector3f(0, 0, 0);
        this.rotation = new Vector3f(0, 0, 0);
        this.data = new Gamely.Save();
    }

    /**
     * Extension of GameObject must have empty constructor for saves
     */
    public GameObject() {
        this.position = new Vector3f(0, 0, 0);
        this.size = new Vector3f(0, 0, 0);
        this.motion = new Vector3f(0, 0, 0);
        this.rotation = new Vector3f(0, 0, 0);
        this.data = new Gamely.Save();
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getMotion() {
        return motion;
    }

    public Vector3f getBoundingSize() {
        return size;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public int getId() {
        return this.id;
    }

    public float getDrag() {
        return 0.5F;
    }

    protected GameEngine getEngine() {
        return this.engine;
    }

    public void addedToEngine(int id, GameEngine engine) {
        this.id = id;
        this.engine = engine;
    }

    public void render(Graphics2D g) {

    }

    public void tick() {

    }

    public void write(Gamely.Save save) {
        save.putString("class", this.getClass().getTypeName());
        save.putInteger("id", this.id);

        save.putFloat("positionX", this.getPosition().getX());
        save.putFloat("positionY", this.getPosition().getY());
        save.putFloat("positionZ", this.getPosition().getZ());

        save.putFloat("motionX", this.getMotion().getX());
        save.putFloat("motionY", this.getMotion().getY());
        save.putFloat("motionZ", this.getMotion().getZ());

        save.putFloat("boundingX", this.getBoundingSize().getX());
        save.putFloat("boundingY", this.getBoundingSize().getY());
        save.putFloat("boundingZ", this.getBoundingSize().getZ());

        save.putFloat("rotationX", this.getRotation().getX());
        save.putFloat("rotationY", this.getRotation().getY());
        save.putFloat("rotationZ", this.getRotation().getZ());

        save.put("data", this.data);
    }

    public void read(Gamely.Save save) {
        this.id = save.getInteger("id");
        this.data = save.get("data");
        this.position = new Vector3f(save.getFloat("positionX"), save.getFloat("positionY"), save.getFloat("positionZ"));
        this.motion = new Vector3f(save.getFloat("motionX"), save.getFloat("motionY"), save.getFloat("motionZ"));
        this.size = new Vector3f(save.getFloat("boundingX"), save.getFloat("boundingY"), save.getFloat("boundingZ"));
        this.rotation = new Vector3f(save.getFloat("rotationX"), save.getFloat("rotationY"), save.getFloat("rotationZ"));
    }

    public static Gamely.Save handleWrite(GameObject gameObject) {
        Gamely.Save save = new Gamely.Save();
        gameObject.write(save);
        return save;
    }

    /**
     * Reads GameObject from save
     *
     * @exception NoSuchMethodException  Thrown if there is no empty constructor in GameObject extension
     *
     * @param save  Save to read
     * @return
     */
    public static GameObject handleRead(Gamely.Save save) {
        try {
            Class objClass = Class.forName(save.getString("class"));
            Constructor<?> constructor = objClass.getConstructor();
            if (constructor != null) {
                Object obj = constructor.newInstance();
                if (obj instanceof GameObject) {
                    GameObject gameObject = (GameObject) obj;
                    gameObject.read(save);
                    return gameObject;
                }
            }
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
