package com.patetlex.displayphoenix.test;

import com.patetlex.displayphoenix.gamely.Gamely;
import com.patetlex.displayphoenix.gamely.engine.impl.GameEngine3D;
import com.patetlex.displayphoenix.gamely.obj.GameObject;
import com.patetlex.displayphoenix.gamely.physics.GamePhysics;
import com.patetlex.displayphoenix.gamely.ui.impl.GameGLFWPanel;
import com.patetlex.displayphoenix.test.misc.Biome;
import com.patetlex.displayphoenix.test.misc.Chunk;
import com.patetlex.displayphoenix.test.obj.BlockObject;
import com.patetlex.displayphoenix.util.ColorHelper;
import org.apache.xmlgraphics.java2d.color.ColorUtil;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ToonlandEngine extends GameEngine3D {

    public static int CHUNK_RENDER_DISTANCE = 8;
    public static float SUN_DISTANCE = 200;
    public static int DAY_TICKS_CYCLE = 500;

    private Map<Vector2i, Chunk> cachedChunks;
    private int textureAtlas;

    private long terrainSeed;
    private long temperatureSeed;
    private long humiditySeed;

    private int ticksOfDay;
    private GameObject sun;

    public ToonlandEngine(GamePhysics physics, int tickRate, long terrainSeed, long temperatureSeed, long humiditySeed) {
        super(physics, tickRate);
        this.terrainSeed = terrainSeed;
        this.temperatureSeed = temperatureSeed;
        this.humiditySeed = humiditySeed;
        this.cachedChunks = new ConcurrentHashMap<>();

        this.sun = new BlockObject(new Vector3f(0, 200, 0), new Vector3f());
        this.addGameObject(this.sun);
        this.sun.setLight(new Vector4f(2F, 2F, 2F, 0.2F), new Vector3f(1, 0, 0F));
    }

    @Override
    public void setupGLFW(GameGLFWPanel panel) {
        super.setupGLFW(panel);
        this.textureAtlas = this.loadTextureToAtlas("gamely.png");

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    @Override
    public void render(Graphics2D g) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureAtlas);

        float dayTime = getDayTime();
        Vector2i currentChunk = Chunk.getChunkCoordinates(this.getCamera().getPosition());
        Biome currentBiome = Biome.getBiome(this.temperatureSeed, this.humiditySeed, Math.round(this.getCamera().getPosition().x), Math.round(this.getCamera().getPosition().y));

        // Setup global lighting
        Color skyColor = ColorHelper.mixColors(currentBiome.getNightColor(), currentBiome.getDayColor(), Math.abs(dayTime));
        GL11.glClearColor(skyColor.getRed() / 255F, skyColor.getGreen() / 255F, skyColor.getBlue() / 255F, 1F);
        float sunPlacement = (float) Math.sin((Math.PI / (DAY_TICKS_CYCLE / 2F)) * this.ticksOfDay);
        sunPlacement = sunPlacement * sunPlacement * sunPlacement;
        sunPlacement *= SUN_DISTANCE;
        Vector3f sunlightColor = new Vector3f(currentBiome.getSunlightIntensity()).mul(Math.abs(dayTime) + currentBiome.minimumDarkness());
        this.sun.setLight(new Vector4f(sunlightColor, currentBiome.minimumDarkness()));
        this.sun.getPosition().set(this.getCamera().getPosition()).add(sunPlacement, (Math.abs(dayTime) - 0.5F) * SUN_DISTANCE * 2, sunPlacement);

        this.shader.bind();
        this.setShaderUniforms(this.shader, g);
        Iterator<Vector2i> chunks = this.cachedChunks.keySet().iterator();
        while (chunks.hasNext()) {
            Vector2i chunkPos = chunks.next();
            Chunk chunk = this.cachedChunks.get(chunkPos);
            if (chunk != null && currentChunk != null) {
                int dx = Math.abs(currentChunk.x - chunk.getCoordinates().x);
                int dy = Math.abs(currentChunk.y - chunk.getCoordinates().y);
                if (dx <= CHUNK_RENDER_DISTANCE && dy <= CHUNK_RENDER_DISTANCE) {
                    chunk.render(g);
                } else {
                    //System.out.println("[CHUNK] Unloading chunk: [X: " + chunkPos.x + ", Y: " + chunkPos.y + "]");
                    Gamely.Save save = chunk.getSave();
                    this.cachedChunks.get(chunkPos).unload();
                    chunks.remove();
                }
            }
        }
/*        for (Integer id : this.objs.keySet()) {
            this.objs.get(id).render(g);
        }*/
        this.shader.unbind();
    }

    @Override
    public void setShaderUniforms(Shader shader, Graphics2D g) {
        super.setShaderUniforms(shader, g);
        shader.setUniform("ambientLight", new Vector3f(1F, 1F, 1F));
        shader.setLightsUniform("lights", sortRenderingLights());
    }

    @Override
    public void tick() {
        super.tick();
        this.ticksOfDay++;
        if (this.ticksOfDay > (DAY_TICKS_CYCLE * 2)) {
            this.ticksOfDay = 0;
        }

        Vector2i currentChunk = Chunk.getChunkCoordinates(this.getCamera().getPosition());

/*        // System Cleanup
        if (this.getTicksExisted() % this.getTickRate() * 10 == 0) {
            System.gc();
        }*/

        // Loading Chunks
        for (int i = -CHUNK_RENDER_DISTANCE; i < CHUNK_RENDER_DISTANCE; i++) {
            for (int j = -CHUNK_RENDER_DISTANCE; j < CHUNK_RENDER_DISTANCE; j++) {
                loadChunk(i + currentChunk.x, j + currentChunk.y);
            }
        }

        float moveZ = 0;
        float moveY = 0;
        float moveX = 0;
        if (this.getPanel().getDownKeys().get(KeyEvent.VK_W)) {
            moveZ = -1;
        } else if (this.getPanel().getDownKeys().get(KeyEvent.VK_S)) {
            moveZ = 1;
        }
        if (this.getPanel().getDownKeys().get(KeyEvent.VK_A)) {
            moveX = -1;
        } else if (this.getPanel().getDownKeys().get(KeyEvent.VK_D)) {
            moveX = 1;
        }
        if (this.getPanel().getDownKeys().get(KeyEvent.VK_SPACE)) {
            moveY = 1;
        } else if (this.getPanel().getDownKeys().get(KeyEvent.VK_SHIFT)) {
            moveY = -1;
        }
        this.getCamera().move(moveX * ToonlandPanel.CAMERA_STEP, moveY * ToonlandPanel.CAMERA_STEP, moveZ * ToonlandPanel.CAMERA_STEP);

        Line2D.Float moveLine = this.getPanel().getMoveLine();
        if (this.getPanel().getDownMouseButtons().get(MouseEvent.BUTTON3)) {
            float x = (float) (moveLine.getX1() - moveLine.getX2());
            float y = (float) (moveLine.getY1() - moveLine.getY2());
            this.getCamera().getRotation().add(y * ToonlandPanel.MOUSE_SENSITIVITY, x * ToonlandPanel.MOUSE_SENSITIVITY, 0);
        }
    }

    public void loadChunk(int chunkX, int chunkY) {
        Vector2i chunkPosition = new Vector2i(chunkX, chunkY);
        if (!this.cachedChunks.containsKey(chunkPosition)) {
            this.cachedChunks.put(chunkPosition, Chunk.generate(this, this.terrainSeed, this.temperatureSeed, this.humiditySeed, chunkX, chunkY));
        }
    }

    public float getDayTime() {
        float r = (float) Math.cos((Math.PI / DAY_TICKS_CYCLE) * this.ticksOfDay);
        return r * r * r;
    }

    public void setDayTime(float time) {
        this.ticksOfDay = Math.round(DAY_TICKS_CYCLE * time);
    }
}
