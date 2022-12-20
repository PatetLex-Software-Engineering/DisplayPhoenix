package com.patetlex.displayphoenix.gamely.engine.impl;

import com.bulletphysics.linearmath.IDebugDraw;
import com.patetlex.displayphoenix.gamely.engine.GameEngine;
import com.patetlex.displayphoenix.gamely.obj.Camera;
import com.patetlex.displayphoenix.gamely.obj.GameObject;
import com.patetlex.displayphoenix.gamely.physics.impl.GamePhysics3D;
import com.patetlex.displayphoenix.gamely.ui.GamePanel;
import com.patetlex.displayphoenix.gamely.ui.impl.GameGLFWPanel;
import com.patetlex.displayphoenix.interfaces.FileIteration;
import com.patetlex.displayphoenix.util.FileHelper;
import com.patetlex.displayphoenix.util.ImageHelper;
import org.ghost4j.renderer.RendererException;
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Math;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;

public class GameEngine3D extends GameEngine {

    public Shader shader;
    public Shader baseShader;

    protected DebugRenderer debugRenderer;

    public GameEngine3D(GamePhysics3D physics, int tickRate) {
        super(physics, tickRate);
        this.debugRenderer = new DebugRenderer(this);
        physics.setDebugRenderer(this.debugRenderer);
    }

    @Override
    public void start(GamePanel panel) {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!(panel instanceof GameGLFWPanel)) {
            throw new IllegalArgumentException("Use GameGLFWPanel.");
        }

        super.start(panel);
    }

    public void setupGLFW(GameGLFWPanel panel) {
        this.shader = new Shader();
        this.baseShader = new Shader() {
            @Override
            protected void bindAttributes() {
                GL20.glBindAttribLocation(this.programId, 0, "position");
            }
        };
        this.startShader();

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);

        if (panel.isVSync()) {
            GLFW.glfwSwapInterval(1);
        }
    }

    @Override
    public void render(Graphics2D g) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        this.shader.bind();
        GameObject[] renderedLights = sortRenderingLights();
        this.shader.setLightsUniform("lights", renderedLights);
        setShaderUniforms(this.shader, g);
        for (int id : this.getGameObjects().keySet()) {
            GameObject obj = this.getGameObjects().get(id);
            renderObject(g, obj);
        }
        this.shader.unbind();
        this.baseShader.bind();
        if (this.debugRenderer.getDebugMode() != 0) {
            this.debugRenderer.debugRender();
        }
        this.baseShader.unbind();
    }

    protected void renderObject(Graphics2D g, GameObject obj) {
        this.shader.setUniform("transformationMatrix", createTransformationMatrix(obj));
        this.shader.setUniform("reflectance", obj.getReflectance());
        obj.render(g);
    }

    public void setShaderUniforms(Shader shader, Graphics2D g) {
        shader.setUniform("textureAtlas", 0);
        shader.setUniform("projectionMatrix", this.getCamera().updateProjectionMatrix());
        shader.setUniform("viewMatrix", createViewMatrix(this.getCamera()));
        shader.setUniform("ambientLight", new Vector4f(1F, 1F, 1F, 0.4F));
        shader.setUniform("skyColor", new Vector3f(1F, 1F, 1F));
        shader.setUniform("renderDistance", 1);
    }

    @Override
    public void tick() {

    }

    @Override
    public boolean shouldBreak() {
        return GLFW.glfwWindowShouldClose(((GameGLFWPanel) this.getPanel()).getContext());
    }

    @Override
    public IDebugRenderer getDebugRenderer() {
        return this.debugRenderer;
    }

    protected GameObject[] sortRenderingLights() {
        GameObject[] renderedLights = new GameObject[MAXIMUM_LIGHT_ITERATIONS];
        List<GameObject> sortedLights = new ArrayList<>(this.lightObjs);
        sortedLights.sort(new Comparator<GameObject>() {
            @Override
            public int compare(GameObject gameObject, GameObject t1) {
                int d1 = Math.round(GameEngine3D.this.getCamera().distanceTo(gameObject));
                int d2 = Math.round(GameEngine3D.this.getCamera().distanceTo(t1));
                return d1 - d2;
            }
        });
        for (int i = 0; i < MAXIMUM_LIGHT_ITERATIONS; i++) {
            if (i < sortedLights.size()) {
                renderedLights[i] = sortedLights.get(i);
            }
        }
        return renderedLights;
    }

    public static Matrix4f createTransformationMatrix(GameObject object) {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.identity().translate(object.getPosition()).rotateX((float) Math.toRadians(object.getRotation().x)).rotateY((float) Math.toRadians(object.getRotation().y)).rotateZ((float) Math.toRadians(object.getRotation().z)).scale(object.getScale());
        return matrix4f;
    }

    public static Matrix4f createViewMatrix(Camera camera) {
        Vector3f position = camera.getPosition();
        Vector3f rotation = camera.getRotation();
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.identity();
        matrix4f.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0)).rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0)).rotate((float) Math.toRadians(rotation.z), new Vector3f(0, 0, 1));
        matrix4f.translate(-position.x, -position.y, -position.z);
        return matrix4f;
    }

    @Override
    protected void end() {
        ((GameGLFWPanel) this.getPanel()).destroy();
        for (int vao : this.vaos.keySet()) {
            GL30.glDeleteVertexArrays(vao);
            for (int vbo : this.vaos.get(vao)) {
                GL30.glDeleteBuffers(vbo);
            }
        }
        for (int tex : this.texs)
            GL30.glDeleteTextures(tex);
        this.shader.end();
    }

    private Map<Integer, List<Integer>> vaos = new HashMap<>();
    private List<Integer> texs = new ArrayList<>();

    public Model loadModel(float[] vertices, float[] textureCoordinates, float[] normals, int[] indices) {
        int id = createVAO();
        storeIndicesBuffer(id, indices);
        storeDataInAttribute(id, 0, 3, vertices);
        storeDataInAttribute(id, 1, 2, textureCoordinates);
        storeDataInAttribute(id, 2, 3, normals);
        unbind();
        return new Model(id, indices.length);
    }

    public Model loadObj(String filePath) {
        List<String> lines = FileHelper.readToLines(ClassLoader.getSystemClassLoader().getResourceAsStream("models/gamely/" + filePath));
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3i> faces = new ArrayList<>();

        for (String line : lines) {
            String[] tokens = line.split("\\s+");
            switch (tokens[0]) {
                case "v":
                    Vector3f v = new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]));
                    vertices.add(v);
                    break;
                case "vt":
                    Vector2f vt = new Vector2f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
                    textures.add(vt);
                    break;
                case "vn":
                    Vector3f n = new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]));
                    normals.add(n);
                    break;
                case "f":
                    for (int i = 1; i < tokens.length; i++) {
                        faces.add(Model.parseFace(tokens[i]));
                    }
                    break;
                default:
                    break;
            }
        }
        return loadObjFormat(vertices, normals, textures, faces);
    }

    public void parseObj(String filePath, BiConsumer<List<Vector3f>, List<Vector3f>> verticesNormal, BiConsumer<List<Vector2f>, List<Vector3i>> textureFaces) {
        List<String> lines = FileHelper.readToLines(ClassLoader.getSystemClassLoader().getResourceAsStream("models/gamely/" + filePath));
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3i> faces = new ArrayList<>();

        for (String line : lines) {
            String[] tokens = line.split("\\s+");
            switch (tokens[0]) {
                case "v":
                    Vector3f v = new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]));
                    vertices.add(v);
                    break;
                case "vt":
                    Vector2f vt = new Vector2f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
                    textures.add(vt);
                    break;
                case "vn":
                    Vector3f n = new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]));
                    normals.add(n);
                    break;
                case "f":
                    for (int i = 1; i < tokens.length; i++) {
                        faces.add(Model.parseFace(tokens[i]));
                    }
                    break;
                default:
                    break;
            }
        }
        verticesNormal.accept(vertices, normals);
        textureFaces.accept(textures, faces);
    }

    public Model loadObjFormat(List<Vector3f> vertices, List<Vector3f> normals, List<Vector2f> textureCoordinates, List<Vector3i> faces) {
        List<Integer> indices = new ArrayList<>();
        float[] verticesArray = new float[vertices.size() * 3];
        int i = 0;
        for (Vector3f pos : vertices) {
            verticesArray[i * 3] = pos.x;
            verticesArray[(i * 3) + 1] = pos.y;
            verticesArray[(i * 3) + 2] = pos.z;
            i++;
        }
        float[] textureCoordinatesArray = new float[vertices.size() * 2];
        float[] normalsArray = new float[vertices.size() * 3];
        for (Vector3i face : faces) {
            Model.processVertex(face.x, face.y, face.z, textureCoordinates, normals, indices, textureCoordinatesArray, normalsArray);
        }
        int[] indicesArray = indices.stream().mapToInt((Integer v) -> v).toArray();
        return loadModel(verticesArray, textureCoordinatesArray, normalsArray, indicesArray);
    }

    public int loadTexture(String texturePath) {
        int width;
        int height;
        ByteBuffer buffer;
        ByteBuffer imageBuffer;
        try {
            imageBuffer = ioResourceToByteBuffer("textures/gamely/" + texturePath, 8 * 1024);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);

            buffer = STBImage.stbi_load_from_memory(imageBuffer, w, h, c, 4);
            if (buffer == null)
                throw new IllegalArgumentException("Image File: " + texturePath + " not loaded to atlas. " + STBImage.stbi_failure_reason());
            width = w.get();
            height = h.get();
        }
        int id = GL11.glGenTextures();
        this.texs.add(id);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        STBImage.stbi_image_free(buffer);
        return id;
    }

    public int loadTexturesToAtlas(String texturesPath, FileIteration iterator) {
        BufferedImage image = ImageHelper.loadImagesToAtlas(texturesPath, iterator);
        ByteBuffer imageBuffer = convertImageData(image);
        int id = GL11.glGenTextures();
        this.texs.add(id);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imageBuffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        return id;
    }

    private static ByteBuffer convertImageData(BufferedImage image) {
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        ByteBuffer buffer = ByteBuffer.allocateDirect(image.getWidth() * image.getHeight() * 4);
        for(int h = 0; h < image.getHeight(); h++) {
            for(int w = 0; w < image.getWidth(); w++) {
                int pixel = pixels[h * image.getWidth() + w];
                buffer.put((byte) ((pixel >> 16) & 0xFF));
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                buffer.put((byte) (pixel & 0xFF));
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }
        buffer.flip();
        return buffer;
    }

    protected int createVAO() {
        int id = GL30.glGenVertexArrays();
        this.vaos.put(id, new ArrayList<>());
        GL30.glBindVertexArray(id);
        return id;
    }

    private void storeIndicesBuffer(int vao, int[] indices) {
        int vbo = GL15.glGenBuffers();
        this.vaos.get(vao).add(vbo);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = storeToIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    protected int storeDataInAttribute(int vao, int attributeNum, int vertexCount, float[] data) {
        int vbo = GL15.glGenBuffers();
        this.vaos.get(vao).add(vbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = storeToFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNum, vertexCount, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        return vbo;
    }

    private static FloatBuffer storeToFloatBuffer(float[] data) {
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data).flip();
        return buffer;
    }

    private static IntBuffer storeToIntBuffer(int[] data) {
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
        buffer.put(data).flip();
        return buffer;
    }

    public void disposeVAO(int vao) {
        GL30.glDeleteVertexArrays(vao);
        for (int vbo : this.vaos.get(vao)) {
            GL30.glDeleteBuffers(vbo);
        }
    }

    private void unbind() {
        GL30.glBindVertexArray(0);
    }

    public static class Model {

        private int id;
        private int vertexCount;
        private int tex;

        public Model(int id, int vertexCount) {
            this.id = id;
            this.vertexCount = vertexCount;
        }

        public Model(int id, int vertexCount, int textureId) {
            this.id = id;
            this.vertexCount = vertexCount;
            this.tex = textureId;
        }

        public Model(Model model, int textureId) {
            this.id = model.getId();
            this.vertexCount = model.getVertexCount();
            this.tex = textureId;
        }

        public int getId() {
            return id;
        }

        public int getVertexCount() {
            return vertexCount;
        }

        public int getTextureId() {
            return tex;
        }

        public void setTextureId(int textureId) {
            this.tex = textureId;
        }

        private static Vector3i parseFace(String token) {
            String[] lineToken = token.split("/");
            int length = lineToken.length;
            int position;
            int coordinates = -1;
            int normal = -1;
            position = Integer.parseInt(lineToken[0]) - 1;
            if (length > 1) {
                String textureCoordinate = lineToken[1];
                coordinates = textureCoordinate.length() > 0 ? Integer.parseInt(textureCoordinate) - 1 : -1;
                if (length > 2) {
                    normal = Integer.parseInt(lineToken[2]) - 1;
                }
            }
            Vector3i f = new Vector3i(position, coordinates, normal);
            return f;
        }

        private static void processVertex(int position, int textureCoordinate, int normal, List<Vector2f> textureCoordinates, List<Vector3f> normals, List<Integer> indices, float[] textureCoordinatesArray, float[] normalsArray) {
            indices.add(position);
            if (textureCoordinate >= 0) {
                Vector2f textureCoordinateVector = textureCoordinates.get(textureCoordinate);
                textureCoordinatesArray[position * 2] = textureCoordinateVector.x;
                textureCoordinatesArray[(position * 2) + 1] = 1 - textureCoordinateVector.y;
            }
            if (normal >= 0) {
                Vector3f normalVector = normals.get(normal);
                normalsArray[position * 3] = normalVector.x;
                normalsArray[(position * 3) + 1] = normalVector.y;
                normalsArray[(position * 3) + 2] = normalVector.z;
            }
        }
    }

    public void startShader() {
        this.shader.createVertexShader(FileHelper.readAllLines(ClassLoader.getSystemClassLoader().getResourceAsStream("gamely/shaders/vertex.vs")).replace("%MAX_LIGHTS%", String.valueOf(MAXIMUM_LIGHT_ITERATIONS)));
        this.shader.createFragmentShader(FileHelper.readAllLines(ClassLoader.getSystemClassLoader().getResourceAsStream("gamely/shaders/fragment.fs")).replace("%MAX_LIGHTS%", String.valueOf(MAXIMUM_LIGHT_ITERATIONS)));
        this.shader.linkProgram();
        this.shader.createUniform("textureAtlas");
        this.shader.createUniform("transformationMatrix");
        this.shader.createUniform("projectionMatrix");
        this.shader.createUniform("viewMatrix");
        this.shader.createUniform("reflectance");
        this.shader.createLightsUniform("lights");
        this.shader.createUniform("ambientLight");
        this.shader.createUniform("skyColor");
        this.shader.createUniform("renderDistance");

        this.baseShader.createVertexShader(FileHelper.readAllLines(ClassLoader.getSystemClassLoader().getResourceAsStream("gamely/shaders/baseVertex.vs")));
        this.baseShader.createFragmentShader(FileHelper.readAllLines(ClassLoader.getSystemClassLoader().getResourceAsStream("gamely/shaders/baseFragment.fs")));
        this.baseShader.linkProgram();
        this.baseShader.createUniform("color");
    }

    public static class Shader {

        protected final int programId;

        protected int vertexShaderId;
        protected int fragmentShaderId;

        private final Map<String, Integer> uniforms;

        public Shader() {
            this.programId = GL20.glCreateProgram();
            if (this.programId == 0)
                throw new IllegalStateException("Could not create shader.");

            this.uniforms = new HashMap<>();
        }

        public void createUniform(String uniform) {
            int uniformLocation = GL20.glGetUniformLocation(this.programId, uniform);
            if (uniformLocation < 0)
                throw new IllegalArgumentException("Could not find uniform variable: " + uniform + ".");
            this.uniforms.put(uniform, uniformLocation);
        }

        public void createLightsUniform(String uniform) {
            for (int i = 0; i < MAXIMUM_LIGHT_ITERATIONS; i++) {
                createUniform(uniform + "Position[" + i + "]");
                createUniform(uniform + "Color[" + i + "]");
                createUniform(uniform + "Attenuation[" + i + "]");
            }
        }

        public void setUniform(String uniform, Matrix4f value) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                GL20.glUniformMatrix4fv(this.uniforms.get(uniform), false, value.get(stack.mallocFloat(16)));
            }
        }

        public void setUniform(String uniform, int value) {
            GL20.glUniform1i(this.uniforms.get(uniform), value);
        }

        public void setUniform(String uniform, float value) {
            GL20.glUniform1f(this.uniforms.get(uniform), value);
        }

        public void setUniform(String uniform, boolean value) {
            GL20.glUniform1i(this.uniforms.get(uniform), value ? 1 : 0);
        }

        public void setUniform(String uniform, Vector4f vector) {
            GL20.glUniform4f(this.uniforms.get(uniform), vector.x, vector.y, vector.z, vector.w);
        }

        public void setUniform(String uniform, Vector3f vector) {
            GL20.glUniform3f(this.uniforms.get(uniform), vector.x, vector.y, vector.z);
        }

        public void setUniform(String uniform, Vector2f vector) {
            GL20.glUniform2f(this.uniforms.get(uniform), vector.x, vector.y);
        }

        public void setLightsUniform(String uniform, GameObject[] lights) {
            for (int i = 0; i < MAXIMUM_LIGHT_ITERATIONS; i++) {
                if (i < lights.length) {
                    GameObject light = lights[i];
                    if (light != null) {
                        setUniform(uniform + "Position[" + i + "]", new Vector3f(light.getPosition()));
                        setUniform(uniform + "Color[" + i + "]", light.getLight().getLight());
                        setUniform(uniform + "Attenuation[" + i + "]", light.getLight().getAttenuation());
                    }
                } else {
                    setUniform(uniform + "Position[" + i + "]", new Vector3f());
                    setUniform(uniform + "Color[" + i + "]", new Vector4f());
                    setUniform(uniform + "Attenuation[" + i + "]", new Vector3f(1, 0, 0));
                }
            }
        }

        public void createVertexShader(String shaderCode) {
            this.vertexShaderId = createShader(shaderCode, GL20.GL_VERTEX_SHADER);
        }

        public void createFragmentShader(String shaderCode) {
            this.fragmentShaderId = createShader(shaderCode, GL20.GL_FRAGMENT_SHADER);
        }

        public int createShader(String shaderCode, int shaderType) {
            int shaderId = GL20.glCreateShader(shaderType);
            if (shaderId == 0)
                throw new IllegalStateException("Could not create " + shaderType + " shader.");

            GL20.glShaderSource(shaderId, shaderCode);
            GL20.glCompileShader(shaderId);
            if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0)
                throw new IllegalStateException("Could not create " + shaderType + " shader. Info: " + GL20.glGetShaderInfoLog(shaderId, 1024));

            GL20.glAttachShader(this.programId, shaderId);
            bindAttributes();
            return shaderId;
        }

        protected void bindAttributes() {
            GL20.glBindAttribLocation(this.programId, 0, "position");
            GL20.glBindAttribLocation(this.programId, 1, "textureCoordinates");
            GL20.glBindAttribLocation(this.programId, 2, "normal");
        }

        public void linkProgram() {
            GL20.glLinkProgram(this.programId);
            if (GL20.glGetProgrami(this.programId, GL20.GL_LINK_STATUS) == 0)
                throw new IllegalStateException("Could not link program.");
            if (this.vertexShaderId != 0)
                GL20.glDetachShader(this.programId, this.vertexShaderId);
            if (this.fragmentShaderId != 0)
                GL20.glDetachShader(this.programId, this.fragmentShaderId);
            GL20.glValidateProgram(this.programId);
            if (GL20.glGetProgrami(this.programId, GL20.GL_VALIDATE_STATUS) == 0)
                throw new IllegalStateException("Could not link program.");
        }

        public void bind() {
            GL20.glUseProgram(this.programId);
        }

        public void unbind() {
            GL20.glUseProgram(this.programId);
        }

        public void end() {
            unbind();
            if (this.programId != 0)
                GL20.glDeleteProgram(this.programId);
        }
    }

    public static class DebugRenderer extends IDebugDraw implements IDebugRenderer {

        protected GameEngine3D engine;

        private int debugMode;

        private float lineThickness;

        public DebugRenderer(GameEngine3D engine) {
            this.engine = engine;
        }

        public void setLineThickness(float thickness) {
            this.lineThickness = thickness;
        }

        @Override
        public void drawLine(javax.vecmath.Vector3f vector3f, javax.vecmath.Vector3f vector3f1, javax.vecmath.Vector3f vector3f2) {
            float t = this.lineThickness / 2F;
            float[] vertices = new float[18];
            vertices[0] = vector3f.x;
            vertices[1] = vector3f.y - t;
            vertices[2] = vector3f.z;
            vertices[3] = vector3f.x;
            vertices[4] = vector3f.y + t;
            vertices[5] = vector3f.z;
            vertices[6] = vector3f1.x;
            vertices[7] = vector3f1.y - t;
            vertices[8] = vector3f1.z;
            vertices[9] = vector3f1.x;
            vertices[10] = vector3f1.y - t;
            vertices[11] = vector3f1.z;
            vertices[12] = vector3f1.x;
            vertices[13] = vector3f1.y + t;
            vertices[14] = vector3f1.z;
            vertices[15] = vector3f.y + t;
            vertices[16] = vector3f.z;
            vertices[17] = vector3f1.x;
            for (int i = 0; i < 5; i++) {
                javax.vecmath.Vector3f v = i > 2 ? vector3f1 : vector3f;
                vertices[i * 3] = v.x;
                vertices[i * 3 + 1] = (i + 1) % 2 == 0 ? v.y - t : v.y + t;
                vertices[i * 3 + 2] = v.z;
            }
            this.engine.baseShader.setUniform("color", new Vector3f(vector3f2.x, vector3f2.y, vector3f2.z));
            int vao = this.engine.createVAO();
            GL30.glBindVertexArray(vao);
            GL20.glEnableVertexAttribArray(0);
            GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);
            GL20.glDisableVertexAttribArray(0);
            GL30.glBindVertexArray(0);
            this.engine.disposeVAO(vao);
        }

        @Override
        public void drawContactPoint(javax.vecmath.Vector3f vector3f, javax.vecmath.Vector3f vector3f1, float v, int i, javax.vecmath.Vector3f vector3f2) {

        }

        @Override
        public void reportErrorWarning(String s) {
            try {
                throw new RendererException(s);
            } catch (RendererException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void draw3dText(javax.vecmath.Vector3f vector3f, String s) {

        }

        @Override
        public void setDebugMode(int i) {
            this.debugMode = i;
        }

        @Override
        public int getDebugMode() {
            return this.debugMode;
        }

        @Override
        public void debugRender() {
            ((GamePhysics3D) this.engine.getPhysics()).renderPhysics(this.engine, this.engine.getGameObjects().values());
        }

        protected int storeVerticesToVAO(float[] vertices) {
            int id = this.engine.createVAO();
            this.engine.storeDataInAttribute(id, 0, 3, vertices);
            this.engine.unbind();
            return id;
        }
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

    private static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;
        Path path = Paths.get(resource);
        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);
                while (fc.read(buffer) != -1) {

                }
            }
        } else {
            try (InputStream source = ClassLoader.getSystemClassLoader().getResourceAsStream(resource); ReadableByteChannel rbc = Channels.newChannel(source)) {
                buffer = BufferUtils.createByteBuffer(bufferSize);
                while (true) {
                    int bytes = rbc.read(buffer);
                    if (bytes == -1) {
                        break;
                    }
                    if (buffer.remaining() == 0) {
                        buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2); // 50%
                    }
                }
            }
        }
        buffer.flip();
        return MemoryUtil.memSlice(buffer);
    }
}
