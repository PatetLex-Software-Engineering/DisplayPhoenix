package com.patetlex.displayphoenix.test.obj;

import com.patetlex.displayphoenix.gamely.Gamely;
import com.patetlex.displayphoenix.gamely.engine.impl.GameEngine3D;
import com.patetlex.displayphoenix.gamely.obj.impl.GLFWGameObject;
import com.patetlex.displayphoenix.test.ToonlandEngine;
import com.patetlex.displayphoenix.test.misc.Chunk;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockObject extends GLFWGameObject {

    private boolean instanceRender;

    public BlockObject(Vector3f position, Vector3f boundingSize) {
        super(position, boundingSize);
        this.instanceRender = true;
    }

    public BlockObject(Vector3f position) {
        this(position, new Vector3f(1, 1, 1));
    }

    public BlockObject() {
        super(new Vector3f(), new Vector3f(1, 1, 1));
        this.instanceRender = true;
    }

    public Vector3i getBlockPosition() {
        return new Vector3i(Math.round(this.getPosition().x), Math.round(this.getPosition().y), Math.round(this.getPosition().z));
    }

    public boolean doesInstanceRender() {
        return this.instanceRender;
    }

    @Override
    public void render(Graphics2D g) {
        if (this.model == null) {
            Random random = new Random();
            Chunk.BlockMesh mesh = new Chunk.BlockMesh((ToonlandEngine) this.getEngine());
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    for (int k = 0; k < 10; k++) {
                        if (random.nextBoolean())
                            mesh.add(new BlockObject(new Vector3f(i, j, k)));
                    }
                }
            }
            mesh.updateMesh();
            this.model = mesh.getMesh();
            this.model.setTextureId(((GameEngine3D) this.getEngine()).loadTextureToAtlas("gamely.png"));
        }
        super.render(g);
    }

    public GameEngine3D.Model getModel() {
        return this.model;
    }

    @Override
    public void write(Gamely.Save save) {
        super.write(save);
        save.putBoolean("instanceRender", this.instanceRender);
    }

    @Override
    public void read(Gamely.Save save) {
        super.read(save);
        this.instanceRender = save.getBoolean("instanceRender");
    }

    public enum Face {
        FRONT(new int[] {
                1,1,5, 4,4,5, 2,2,5,
                1,1,5, 3,3,5, 4,4,5,
        }),
        BACK(new int[] {
                7,11,3, 6,8,3, 8,12,3,
                7,11,3, 5,6,3, 6,8,3,
        }),
        RIGHT(new int[] {
                3,3,2, 8,13,2, 4,4,2,
                3,3,2, 7,10,2, 8,13,2,
        }),
        LEFT(new int[] {
                5,6,6, 2,2,6, 6,8,6,
                5,6,6, 1,1,6, 2,2,6,
        }),
        BOTTOM(new int[] {
                2,2,4, 8,14,4, 6,7,4,
                2,2,4, 4,4,4, 8,14,4,
        }),
        TOP(new int[] {
                5,5,1, 3,3,1, 1,1,1,
                5,5,1, 7,9,1, 3,3,1,
        });

        private List<Vector3i> faces;

        Face(int[] faces) {
            List<Vector3i> fL = new ArrayList<>();
            for (int i = 0; i < faces.length; i += 3) {
                fL.add(new Vector3i(faces[i] - 1, faces[i + 1] - 1, faces[i + 2] - 1));
            }
            this.faces = fL;
        }

        public List<Vector3i> faces() {
            return faces;
        }
    }
}
