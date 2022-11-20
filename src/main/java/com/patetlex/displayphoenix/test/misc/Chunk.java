package com.patetlex.displayphoenix.test.misc;

import com.patetlex.displayphoenix.gamely.Gamely;
import com.patetlex.displayphoenix.gamely.engine.impl.GameEngine3D;
import com.patetlex.displayphoenix.gamely.obj.GameObject;
import com.patetlex.displayphoenix.test.ToonlandEngine;
import com.patetlex.displayphoenix.test.obj.BlockObject;
import com.patetlex.displayphoenix.util.ImageHelper;
import com.patetlex.displayphoenix.util.NoiseHelper;
import org.eclipse.jgit.annotations.Nullable;
import org.joml.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.Math;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class Chunk {

    public static final int HEIGHT = 30;
    public static final int CHUNK_SIZE = 10;
    public static final int HEIGHT_DIVIDER = 2;
    public static final double CHUNK_ZOOM = 24;

    private ToonlandEngine engine;

    public Map<Vector2i, BlockMesh> blocks = new ConcurrentHashMap<>();
    private Vector2i coordinates;

    public Chunk(ToonlandEngine engine, Vector2i coordinates) {
        this.engine = engine;
        this.coordinates = coordinates;
        float r = (float) HEIGHT / HEIGHT_DIVIDER;
        float i = 0;
        while (i < HEIGHT) {
            Vector2i yRange = new Vector2i(Math.round(i), Math.round(i + r));
            this.blocks.put(yRange, new BlockMesh(engine));
            i += r;
        }
    }

    public void render(Graphics2D g) {
        ((GameEngine3D) this.engine).shader.setUniform("transformationMatrix", GameEngine3D.createTransformationMatrix(new BlockObject()));
        ((GameEngine3D) this.engine).shader.setUniform("reflectance", 0F);
        for (Vector2i yRange : this.blocks.keySet()) {
            BlockMesh blockBox = this.blocks.get(yRange);
            GameEngine3D.Model mesh = blockBox.getMesh();
            GL30.glBindVertexArray(mesh.getId());
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);
            GL20.glEnableVertexAttribArray(2);
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mesh.getVertexCount());
            GL20.glDisableVertexAttribArray(0);
            GL20.glDisableVertexAttribArray(1);
            GL20.glDisableVertexAttribArray(2);
            GL30.glBindVertexArray(0);
        }
    }

    public BlockObject getBlockAt(Vector3i position) {
        for (Vector2i yRange : this.blocks.keySet()) {
            if (yRange.x <= position.y && yRange.y > position.y) {
                return this.blocks.get(yRange).getBlock(position);
            }
        }
        return null;
    }

    public boolean addBlock(BlockObject block) {
        for (Vector2i yRange : this.blocks.keySet()) {
            if (yRange.x <= block.getBlockPosition().y && yRange.y > block.getBlockPosition().y) {
                return this.blocks.get(yRange).add(block);
            }
        }
        return false;
    }

    public boolean removeBlock(BlockObject block) {
        for (Vector2i yRange : this.blocks.keySet()) {
            if (yRange.x <= block.getBlockPosition().y && yRange.y > block.getBlockPosition().y) {
                return this.blocks.get(yRange).remove(block);
            }
        }
        return false;
    }

    public Vector2i getCoordinates() {
        return coordinates;
    }

    public void unload() {
        Iterator<Vector2i> yRanges = this.blocks.keySet().iterator();
        while (yRanges.hasNext()) {
            Vector2i yRange = yRanges.next();
            BlockMesh mesh = this.blocks.get(yRange);
            if (mesh.hasMesh())
                this.engine.disposeVAO(mesh.getMesh().getId());
            mesh.clear();
            yRanges.remove();
        }
        this.blocks.clear();
        this.blocks = null;
    }

    public Gamely.Save getSave() {
        Gamely.Save save = new Gamely.Save();
        Vector2i[] yRanges = this.blocks.keySet().toArray(new Vector2i[this.blocks.keySet().size()]);
        for (int i = 0; i < yRanges.length; i++) {
            for (BlockObject object : this.blocks.get(yRanges[i])) {
                Gamely.Save save1 = new Gamely.Save();
                object.write(save1);
                save.put("block" + object.getId(), save1);
            }
        }
        return save;
    }

    public static Vector2i getChunkCoordinates(Vector3f worldPosition) {
        int x = (int) Math.floor(worldPosition.x / CHUNK_SIZE);
        int y = (int) Math.floor(worldPosition.z / CHUNK_SIZE);
        return new Vector2i(x, y);
    }

    public static Chunk generate(ToonlandEngine engine, long terrainSeed, long temperatureSeed, long humiditySeed, int chunkX, int chunkY) {
        //System.out.println("[CHUNK] " + "Beginning to generate chunk: [X: " + chunkX + ", Y: " + chunkY + "]");
        Chunk chunk = new Chunk(engine, new Vector2i(chunkX, chunkY));
        float r = (float) HEIGHT / 255F;
        int x = chunkX * CHUNK_SIZE;
        int y = chunkY * CHUNK_SIZE;
        BufferedImage terrainMap = NoiseHelper.getNoiseImage(CHUNK_SIZE, CHUNK_SIZE, CHUNK_ZOOM, x, y, terrainSeed);
        Color[][] terrainHeights = ImageHelper.getImagePixels(terrainMap);
        for (int i = 0; i < terrainHeights.length; i++) {
            Color[] yVals = terrainHeights[i];
            for (int j = 0; j < yVals.length; j++) {
                Color v = yVals[j];
                Biome biome = Biome.getBiome(temperatureSeed, humiditySeed, x + i, y + j);
                int yCoord = Math.round((float) Math.floor((float) v.getRed() * r));
                for (int k = 0; k < yCoord; k++) {
                    GameObject obj = biome.getDefaultBlock().clone();
                    obj.getPosition().set(x + i, k, y + j);
                    for (Vector2i yRange : chunk.blocks.keySet()) {
                        if (yRange.x <= k && yRange.y > k) {
                            chunk.blocks.get(yRange).add((BlockObject) obj);
                        }
                    }
                    if (engine != null) {
                        int id = chunk.engine.getGameObjects().size();
                        if (chunk.engine.getGameObjects().containsKey(id)) {
                            for (int n = 0; n <= chunk.engine.getGameObjects().size(); n++) {
                                if (!chunk.engine.getGameObjects().containsKey(n)) {
                                    id = n;
                                }
                            }
                        }
                        obj.addedToEngine(id, chunk.engine);
                    }
                }
            }
        }
        //System.out.println("[CHUNK] " + "Generated chunk: [X: " + chunkX + ", Y: " + chunkY + "]");
        return chunk;
    }

    public static class BlockMesh extends ArrayList<BlockObject> {

        private static List<Vector3f> baseVertices;
        private static List<Vector3f> baseNormals;
        private static List<Vector2f> baseTextureCoordinates;

        private GameEngine3D.Model mesh;

        private ToonlandEngine engine;

        public BlockMesh(ToonlandEngine engine) {
            this.engine = engine;
            if (baseVertices == null) {
                engine.parseObj("cube.obj", new BiConsumer<List<Vector3f>, List<Vector3f>>() {
                    @Override
                    public void accept(List<Vector3f> vector3fs, List<Vector3f> vector3fs2) {
                        baseVertices = vector3fs;
                        baseNormals = vector3fs2;
                    }
                }, new BiConsumer<List<Vector2f>, List<Vector3i>>() {
                    @Override
                    public void accept(List<Vector2f> vector2fs, List<Vector3i> vector3is) {
                        baseTextureCoordinates = vector2fs;
                    }
                });
            }
        }

        public GameEngine3D.Model getMesh() {
            if (this.mesh == null)
                this.updateMesh();
            return mesh;
        }

        public boolean hasMesh() {
            return mesh != null;
        }

        @Override
        public boolean add(BlockObject blockObject) {
            boolean flag = super.add(blockObject);
            this.mesh = null;
            return flag;
        }

        public void put(BlockObject block) {
            super.add(block);
        }

        @Override
        public BlockObject remove(int index) {
            BlockObject obj = super.remove(index);
            this.mesh = null;
            return obj;
        }

        @Override
        public boolean remove(Object key) {
            boolean flag = super.remove(key);
            this.mesh = null;
            return flag;
        }

        public BlockObject getBlock(Vector3i position) {
            for (BlockObject obj : this) {
                if (obj.getBlockPosition().equals(position)) {
                    return obj;
                }
            }
            return null;
        }


        public void updateMesh() {
            Map<Vector3i, BlockObject> posList = new HashMap<>();
            for (BlockObject obj : this) {
                posList.put(obj.getBlockPosition(), obj);
            }
            List<Vector3f> vertices = new ArrayList<>();
            List<Vector3i> faces = new ArrayList<>();
            for (BlockObject obj : this) {
                Vector3i pos = obj.getBlockPosition();
                List<Vector3i> facesToRender = new ArrayList<>();
                if (!posList.containsKey(new Vector3i(pos).add(1, 0, 0)) || posList.get(new Vector3i(pos).add(1, 0, 0)).getOpacity() < 1) {
                    facesToRender.addAll(BlockObject.Face.FRONT.faces());
                }
                if (!posList.containsKey(new Vector3i(pos).add(-1, 0, 0)) || posList.get(new Vector3i(pos).add(-1, 0, 0)).getOpacity() < 1) {
                    facesToRender.addAll(BlockObject.Face.BACK.faces());
                }
                if (!posList.containsKey(new Vector3i(pos).add(0, 1, 0)) || posList.get(new Vector3i(pos).add(0, 1, 0)).getOpacity() < 1) {
                    facesToRender.addAll(BlockObject.Face.TOP.faces());
                }
                if (!posList.containsKey(new Vector3i(pos).add(0, -1, 0)) || posList.get(new Vector3i(pos).add(0, -1, 0)).getOpacity() < 1) {
                    facesToRender.addAll(BlockObject.Face.BOTTOM.faces());
                }
                if (!posList.containsKey(new Vector3i(pos).add(0, 0, 1)) || posList.get(new Vector3i(pos).add(0, 0, 1)).getOpacity() < 1) {
                    facesToRender.addAll(BlockObject.Face.RIGHT.faces());
                }
                if (!posList.containsKey(new Vector3i(pos).add(0, 0, -1)) || posList.get(new Vector3i(pos).add(0, 0, -1)).getOpacity() < 1) {
                    facesToRender.addAll(BlockObject.Face.LEFT.faces());
                }
                int prevSize = vertices.size();
                for (int i = 0; i < facesToRender.size(); i += 3) {
                    vertices.add(new Vector3f(baseVertices.get(facesToRender.get(i).x())).add(obj.getPosition()));
                    vertices.add(new Vector3f(baseVertices.get(facesToRender.get(i + 1).x())).add(obj.getPosition()));
                    vertices.add(new Vector3f(baseVertices.get(facesToRender.get(i + 2).x())).add(obj.getPosition()));
                    faces.add(new Vector3i(prevSize + i, facesToRender.get(i).y(), facesToRender.get(i).z()));
                    faces.add(new Vector3i(prevSize + i + 1, facesToRender.get(i + 1).y(), facesToRender.get(i + 1).z()));
                    faces.add(new Vector3i(prevSize + i + 2, facesToRender.get(i + 2).y(), facesToRender.get(i + 2).z()));
                }
            }
            if (this.mesh != null) {
                this.engine.disposeVAO(this.mesh.getId());
            }
            this.mesh = this.engine.loadObjFormat(vertices, baseNormals, baseTextureCoordinates, faces);
        }
    }
}
