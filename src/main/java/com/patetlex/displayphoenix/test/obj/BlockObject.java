package com.patetlex.displayphoenix.test.obj;

import com.patetlex.displayphoenix.gamely.Gamely;
import com.patetlex.displayphoenix.gamely.engine.impl.GameEngine3D;
import com.patetlex.displayphoenix.gamely.obj.impl.GLFWGameObject;
import com.patetlex.displayphoenix.util.FileHelper;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class BlockObject extends GLFWGameObject {

    private String type;

    public BlockObject(Vector3f position, Vector3f boundingSize, String type) {
        super(position, boundingSize);
        this.type = type;
    }

    public BlockObject(Vector3f position, String type) {
        this(position, new Vector3f(1, 1, 1), type);
    }

    public BlockObject() {
        super(new Vector3f(), new Vector3f(1, 1, 1));
        this.type = null;
    }

    public Vector3i getBlockPosition() {
        return new Vector3i(Math.round(this.getPosition().x), Math.round(this.getPosition().y), Math.round(this.getPosition().z));
    }

    @Override
    public void render(Graphics2D g) {
        if (this.model == null) {
            List<Vector3i> faces = new ArrayList<>();
            faces.addAll(Face.FRONT.faces());
            faces.addAll(Face.BACK.faces());
            faces.addAll(Face.TOP.faces());
            faces.addAll(Face.BOTTOM.faces());
            List<Vector3f> vertices = new ArrayList<>();
            List<Vector3f> normals = new ArrayList<>();
            List<Vector2f> textureCoordinates = new ArrayList<>();
            ((GameEngine3D) this.getEngine()).parseObj("cube.obj", new BiConsumer<List<Vector3f>, List<Vector3f>>() {
                @Override
                public void accept(List<Vector3f> vector3fs, List<Vector3f> vector3fs2) {
                    vertices.addAll(vector3fs);
                    normals.addAll(vector3fs2);
                }
            }, new BiConsumer<List<Vector2f>, List<Vector3i>>() {
                @Override
                public void accept(List<Vector2f> vector2fs, List<Vector3i> vector3is) {
                    textureCoordinates.addAll(vector2fs);
                }
            });
            this.model = ((GameEngine3D) this.getEngine()).loadObjFormat(vertices, normals, textureCoordinates, faces);
            this.model.setTextureId(((GameEngine3D) this.getEngine()).loadTexture("cubemap.png"));
        }
        super.render(g);
    }

    public GameEngine3D.Model getModel() {
        return this.model;
    }

    public String getType() {
        return type;
    }

    @Override
    public void write(Gamely.Save save) {
        super.write(save);
        save.putString("type", this.type);
    }

    @Override
    public void read(Gamely.Save save) {
        super.read(save);
        this.type = save.getString("type");
    }

    public enum Face {
        FRONT(new int[] {
                1,2,5, 4,16,5, 2,6,5,
                1,3,5, 3,14,5, 4,18,5,
        }),
        BACK(new int[] {
                7,28,3, 6,24,3, 8,33,3,
                7,31,3, 5,22,3, 6,27,3,
        }),
        RIGHT(new int[] {
                3,11,2, 8,32,2, 4,15,2,
                3,13,2, 7,30,2, 8,35,2,
        }),
        LEFT(new int[] {
                5,20,6, 2,7,6, 6,26,6,
                5,23,6, 1,4,6, 2,9,6,
        }),
        BOTTOM(new int[] {
                2,5,4, 8,34,4, 6,25,4,
                2,8,4, 4,17,4, 8,36,4,
        }),
        TOP(new int[] {
                5,19,1, 3,10,1, 1,1,1,
                5,21,1, 7,29,1, 3,12,1,
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

/*    public static void main(String[] args) {
        String vt = "vt 0.666666 0.000000\n" +
                "vt 0.333333 1.000000\n" +
                "vt 0.333333 1.000000\n" +
                "vt 0.000000 0.500000\n" +
                "vt 0.666666 1.000000\n" +
                "vt 0.333333 0.500000\n" +
                "vt 0.000000 0.000000\n" +
                "vt 0.666666 1.000000\n" +
                "vt 0.000000 0.000000\n" +
                "vt 0.333333 0.000000\n" +
                "vt 1.000000 0.500000\n" +
                "vt 0.333333 0.000000\n" +
                "vt 1.000000 0.500000\n" +
                "vt 0.000000 1.000000\n" +
                "vt 1.000000 0.000000\n" +
                "vt 0.000000 0.500000\n" +
                "vt 0.333333 1.000000\n" +
                "vt 0.000000 0.500000\n" +
                "vt 0.666666 0.500000\n" +
                "vt 0.333333 0.500000\n" +
                "vt 0.666666 0.500000\n" +
                "vt 0.666666 1.000000\n" +
                "vt 0.333333 0.500000\n" +
                "vt 0.666666 0.500000\n" +
                "vt 0.666666 0.500000\n" +
                "vt 0.333333 0.000000\n" +
                "vt 0.666666 0.500000\n" +
                "vt 1.000000 1.000000\n" +
                "vt 0.333333 0.500000\n" +
                "vt 0.666666 0.500000\n" +
                "vt 1.000000 1.000000\n" +
                "vt 0.666666 0.000000\n" +
                "vt 1.000000 0.500000\n" +
                "vt 0.333333 0.500000\n" +
                "vt 0.666666 0.000000\n" +
                "vt 0.333333 0.500000";
        String[] lines = vt.split("\n");
        List<Vector2f> allTexCoords = new ArrayList<>();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String[] splits = line.split("\\s+");
            allTexCoords.add(new Vector2f(Float.parseFloat(splits[1]), Float.parseFloat(splits[2])));
        }
        List<Vector2f> texCoords = new ArrayList<>();
        Map<Integer, Integer> change = new HashMap<>();
        int i = 1;
        for (Vector2f texCoord : allTexCoords) {
            if (!texCoords.contains(texCoord)) {
                texCoords.add(texCoord);
            }
            change.put(i, texCoords.indexOf(texCoord) + 1);
            i++;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (Vector2f texCoord : texCoords) {
            stringBuilder.append("vt " + texCoord.x + " " + texCoord.y +"\n");
        }
        System.out.println(stringBuilder.toString());
        for (Integer ogVal : change.keySet()) {
            System.out.println("Change " + ogVal + " to " + change.get(ogVal));
        }
    }*/
}
