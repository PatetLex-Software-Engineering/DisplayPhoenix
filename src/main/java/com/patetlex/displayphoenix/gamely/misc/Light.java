package com.patetlex.displayphoenix.gamely.misc;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class Light {

    private Vector4f light;
    private Vector3f attenuation;

    public Light(Vector4f light, Vector3f attenuation) {
        this.light = light;
        this.attenuation = attenuation;
    }

    public Vector4f getLight() {
        return light;
    }

    public Vector3f getAttenuation() {
        return attenuation;
    }

    public void setLight(Vector4f light) {
        this.light = light;
    }

    public void setAttenuation(Vector3f attenuation) {
        this.attenuation = attenuation;
    }
}
