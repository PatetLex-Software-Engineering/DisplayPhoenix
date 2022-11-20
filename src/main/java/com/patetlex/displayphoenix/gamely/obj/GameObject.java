package com.patetlex.displayphoenix.gamely.obj;

import com.patetlex.displayphoenix.gamely.Gamely;
import com.patetlex.displayphoenix.gamely.engine.GameEngine;
import com.patetlex.displayphoenix.gamely.misc.Light;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public class GameObject implements Cloneable {

    public Gamely.Save data;

    private Vector3f position;
    private Vector3f motion;
    private Vector3f size;
    private Vector3f rotation;
    private float scale;

    private float opacity;

    private Light light;
    private float reflectance;

    private GameEngine engine;
    private int id;

    public GameObject(Vector3f position, Vector3f boundingSize) {
        this.position = position;
        this.size = boundingSize;
        this.motion = new Vector3f();
        this.rotation = new Vector3f();
        this.data = new Gamely.Save();
        this.scale = 1;
        this.opacity = 1;
    }

    /**
     * Extension of GameObject must have empty constructor for saves
     */
    public GameObject() {
        this.position = new Vector3f();
        this.size = new Vector3f();
        this.motion = new Vector3f();
        this.rotation = new Vector3f();
        this.data = new Gamely.Save();
        this.scale = 1;
        this.opacity = 1;

    }

    public void setLight(Vector4f light) {
        if (this.light == null) {
            this.setLight(light, new Vector3f(1, 0, 0));
        } else {
            this.setLight(light, this.getLight().getAttenuation());
        }
    }

    public void setLight(Vector4f light, Vector3f attenuation) {
        if (this.light == null) {
            this.light = new Light(light, attenuation);
            if (this.getEngine() != null)
                this.getEngine().addLightSource(this);
        } else {
            this.light.setLight(light);
            this.light.setAttenuation(attenuation);
        }
    }

    public void setReflectance(float reflectance) {
        this.reflectance = reflectance;
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    public void scale(float scale) {
        this.scale = scale;
    }

    public float getScale() {
        return scale;
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

    public Light getLight() {
        return this.light;
    }

    public float getReflectance() {
        return reflectance;
    }

    public GameEngine getEngine() {
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

        save.putFloat("positionX", this.getPosition().x);
        save.putFloat("positionY", this.getPosition().y);
        save.putFloat("positionZ", this.getPosition().z);

        save.putFloat("motionX", this.getMotion().x);
        save.putFloat("motionY", this.getMotion().y);
        save.putFloat("motionZ", this.getMotion().z);

        save.putFloat("boundingX", this.getBoundingSize().x);
        save.putFloat("boundingY", this.getBoundingSize().y);
        save.putFloat("boundingZ", this.getBoundingSize().z);

        save.putFloat("rotationX", this.getRotation().x);
        save.putFloat("rotationY", this.getRotation().y);
        save.putFloat("rotationZ", this.getRotation().z);

        save.putFloat("scale", this.scale);

        save.putFloat("opacity", this.opacity);

        if (this.light != null) {
            save.putFloat("lightX", this.getLight().getLight().x);
            save.putFloat("lightY", this.getLight().getLight().y);
            save.putFloat("lightZ", this.getLight().getLight().z);
            save.putFloat("lightW", this.getLight().getLight().w);

            save.putFloat("lightAttenuationX", this.getLight().getAttenuation().x);
            save.putFloat("lightAttenuationY", this.getLight().getAttenuation().y);
            save.putFloat("lightAttenuationZ", this.getLight().getAttenuation().z);
        }

        save.putFloat("reflectance", this.getReflectance());

        save.put("data", this.data);
    }

    public void read(Gamely.Save save) {
        this.id = save.getInteger("id");
        this.data = save.get("data");
        this.position = new Vector3f(save.getFloat("positionX"), save.getFloat("positionY"), save.getFloat("positionZ"));
        this.motion = new Vector3f(save.getFloat("motionX"), save.getFloat("motionY"), save.getFloat("motionZ"));
        this.size = new Vector3f(save.getFloat("boundingX"), save.getFloat("boundingY"), save.getFloat("boundingZ"));
        this.rotation = new Vector3f(save.getFloat("rotationX"), save.getFloat("rotationY"), save.getFloat("rotationZ"));
        this.scale = save.getFloat("scale");
        this.light = new Light(new Vector4f(save.getFloat("lightX"), save.getFloat("lightY"), save.getFloat("lightZ"), save.getFloat("lightW")), new Vector3f(save.getFloat("lightAttenuationX"), save.getFloat("lightAttenuationY"), save.getFloat("lightAttenuationZ")));
        this.reflectance = save.getFloat("reflectance");
        this.opacity = save.getFloat("opacity");
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

    public GameObject clone() {
        return GameObject.handleRead(GameObject.handleWrite(this));
    }

    public float distanceTo(GameObject obj) {
        float dx = this.getPosition().x - obj.getPosition().x;
        float dy = this.getPosition().y - obj.getPosition().y;
        float dz = this.getPosition().z - obj.getPosition().z;
        float xy = (float) Math.sqrt(dx * dx + dy * dy);
        float xyz = (float) Math.sqrt(xy * xy + dz * dz);
        return xyz;
    }
}
