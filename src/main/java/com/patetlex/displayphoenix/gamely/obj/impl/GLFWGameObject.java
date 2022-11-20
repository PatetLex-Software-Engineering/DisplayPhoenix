package com.patetlex.displayphoenix.gamely.obj.impl;

import com.patetlex.displayphoenix.gamely.engine.impl.GameEngine3D;
import com.patetlex.displayphoenix.gamely.obj.GameObject;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.awt.*;

public class GLFWGameObject extends GameObject {

    protected GameEngine3D.Model model;

    public GLFWGameObject(Vector3f position, Vector3f boundingSize) {
        super(position, boundingSize);
    }

    public GLFWGameObject() {
        super();
    }

    @Override
    public void render(Graphics2D g) {
        super.render(g);
        if (this.model != null) {
            ((GameEngine3D) this.getEngine()).shader.setUniform("transformationMatrix", GameEngine3D.createTransformationMatrix(this));
            GL30.glBindVertexArray(this.model.getId());
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);
            GL20.glEnableVertexAttribArray(2);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.model.getTextureId());
            GL11.glDrawElements(GL11.GL_TRIANGLES, this.model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            GL20.glDisableVertexAttribArray(0);
            GL20.glDisableVertexAttribArray(1);
            GL20.glDisableVertexAttribArray(2);
            GL30.glBindVertexArray(0);
        }
    }
}
