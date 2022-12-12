package com.patetlex.displayphoenix.gamely.physics.impl;

import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.IDebugDraw;
import com.bulletphysics.linearmath.Transform;
import com.patetlex.displayphoenix.gamely.engine.GameEngine;
import com.patetlex.displayphoenix.gamely.engine.impl.GameEngine3D;
import com.patetlex.displayphoenix.gamely.obj.Camera;
import com.patetlex.displayphoenix.gamely.obj.GameObject;
import com.patetlex.displayphoenix.gamely.physics.GamePhysics;

import org.joml.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.awt.*;
import java.lang.Math;
import java.util.*;
import java.util.List;

public class GamePhysics3D extends GamePhysics {

    private DiscreteDynamicsWorld world;
    private GameEngine3D.DebugRenderer debugRenderer;
    private Vector3f ray;

    public GamePhysics3D() {
        DbvtBroadphase broadphase = new DbvtBroadphase();
        DefaultCollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
        this.world = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        this.world.setGravity(new javax.vecmath.Vector3f(0, this.getGravity(), 0));
    }

    @Override
    public void moveCamera(Camera camera) {

    }

    @Override
    public void moveObjects(Collection<GameObject> objs) {
        DbvtBroadphase broadphase = new DbvtBroadphase();
        DefaultCollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
        DiscreteDynamicsWorld world = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);

        world.setGravity(new javax.vecmath.Vector3f(0, this.getGravity(), 0));
    }

    @Override
    public List<GameObject> getCollidingObjects(Collection<GameObject> objs, GameObject obj, GameEngine engine) {
        return null;
    }

    @Override
    public GameObject rayTraceObject(Collection<GameObject> objs, Point onScreen, GameEngine engine) {
        Map<Integer, GameObject> idToObj = new HashMap<>();
        List<RigidBody> rigidBodies = new ArrayList<>();
        for (GameObject obj : objs) {
            if (!idToObj.containsKey(obj.getId())) {
                CollisionShape shape = new BoxShape(new javax.vecmath.Vector3f(obj.getBoundingSize().x / 2F, obj.getBoundingSize().y / 2F, obj.getBoundingSize().z / 2F));
                Matrix4f tM = GameEngine3D.createTransformationMatrix(obj);
                Transform transform = new Transform(new javax.vecmath.Matrix4f(tM.m00(), tM.m01(), tM.m02(), tM.m03(), tM.m10(), tM.m11(), tM.m12(), tM.m13(), tM.m20(), tM.m21(), tM.m22(), tM.m23(), tM.m30(), tM.m31(), tM.m32(), tM.m33()));
                DefaultMotionState motionState = new DefaultMotionState(transform);
                RigidBodyConstructionInfo info = new RigidBodyConstructionInfo(0, motionState, shape, new javax.vecmath.Vector3f(0, 0, 0));
                RigidBody body = new RigidBody(info);
/*                Transform center = new Transform();
                body.setCenterOfMassTransform();*/
                body.translate(new javax.vecmath.Vector3f(obj.getPosition().x, obj.getPosition().y, obj.getPosition().z));
                idToObj.put(obj.getId(), obj);
                rigidBodies.add(body);
                body.setUserPointer(obj.getId());
                this.world.addRigidBody(body);

                System.out.println(body.getCenterOfMassPosition(new javax.vecmath.Vector3f()));
                System.out.println(obj.getPosition().x + " " + obj.getPosition().y + " " + obj.getPosition().z);
            }
        }
        this.world.stepSimulation(0.000005F, 7);

        this.world.debugDrawWorld();
        Vector3f outOrigin = engine.getCamera().getPosition();
        Vector3f outEnd = outOrigin.add(rayTracePoint(onScreen, engine), new Vector3f());

        System.out.println(outEnd.x + " " + outEnd.y + " " + outEnd.z);

        CollisionWorld.ClosestRayResultCallback callback = new CollisionWorld.ClosestRayResultCallback(new javax.vecmath.Vector3f(outOrigin.x, outOrigin.y, outOrigin.z), new javax.vecmath.Vector3f(outEnd.x, outEnd.y, outEnd.z));
        this.world.rayTest(new javax.vecmath.Vector3f(outOrigin.x, outOrigin.y, outOrigin.z), new javax.vecmath.Vector3f(outEnd.x, outEnd.y, outEnd.z), callback);
        if (callback.hasHit()) {
            System.out.println("HIT!");
            return idToObj.get(callback.collisionObject.getUserPointer());
        }
        for (RigidBody body : rigidBodies) {
            this.world.removeRigidBody(body);
        }
        return null;
    }

    @Override
    public Vector3f rayTracePoint(Point onScreen, GameEngine engine) {
/*        float x = (2F * (float) onScreen.x) / engine.getPanel().getWidth() - 1F;
        float y = (2F * (float) onScreen.y) / engine.getPanel().getHeight() - 1F;
        Matrix4f inverseProjection = engine.getCamera().getProjectionMatrix().invert(new Matrix4f());
        Vector4f eyeCoordinates = inverseProjection.transform(new Vector4f(x, y, -1, 1), new Vector4f());
        eyeCoordinates.z = -1;
        eyeCoordinates.w = 0;
        Matrix4f inverseView = createViewMatrix(engine.getCamera()).invert(new Matrix4f());
        Vector4f rayWorld = inverseView.transform(eyeCoordinates, new Vector4f());
        Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);*/
        //return mouseRay.normalize();
/*
        float mouseX = onScreen.x;
        float mouseY = onScreen.y;
        float screenWidth = engine.getPanel().getWidth();
        float screenHeight = engine.getPanel().getHeight();
        Vector4f rayStartNDC = new Vector4f(((float) mouseX / (float) screenWidth - 0.5f) * 2.0f, ((float) mouseY / (float) screenHeight - 0.5f) * 2.0f, -1.0F, 1.0f);
        Vector4f rayEndNDC = new Vector4f(((float) mouseX / (float) screenWidth - 0.5f) * 2.0f, ((float) mouseY / (float) screenHeight - 0.5f) * 2.0f, 0.0F, 1.0f);


        Matrix4f inverse = engine.getCamera().getProjectionMatrix().invert(new Matrix4f()).mul(createViewMatrix(engine.getCamera()).invert(new Matrix4f()), new Matrix4f());
        Vector4f rayStartWorld = rayStartNDC.mul(inverse, new Vector4f());
        rayStartWorld.div(rayStartWorld.w);
        Vector4f rayEndWorld = rayEndNDC.mul(inverse, new Vector4f());
        rayEndWorld.div(rayEndWorld.w);


        Vector4f subVector = rayEndWorld.sub(rayStartWorld, new Vector4f());
        Vector3f rayDirectionWorld = new Vector3f(subVector.x, subVector.y, subVector.z);
        rayDirectionWorld.normalize();
*/
        this.ray = calculateMouseRay(onScreen, engine).normalize().mul(100F, new Vector3f());
        return this.ray;
    }

    public void renderPhysics(GameEngine engine, Collection<GameObject> objs) {
        List<RigidBody> rigidBodies = new ArrayList<>();
        for (GameObject obj : objs) {
            CollisionShape shape = new BoxShape(new javax.vecmath.Vector3f(obj.getBoundingSize().x / 2F, obj.getBoundingSize().y / 2F, obj.getBoundingSize().z / 2F));
            Matrix4f tM = GameEngine3D.createTransformationMatrix(obj);
            Transform transform = new Transform(new javax.vecmath.Matrix4f(tM.m00(), tM.m01(), tM.m02(), tM.m03(), tM.m10(), tM.m11(), tM.m12(), tM.m13(), tM.m20(), tM.m21(), tM.m22(), tM.m23(), tM.m30(), tM.m31(), tM.m32(), tM.m33()));
            DefaultMotionState motionState = new DefaultMotionState(transform);
            RigidBodyConstructionInfo info = new RigidBodyConstructionInfo(0, motionState, shape, new javax.vecmath.Vector3f(0, 0, 0));
            RigidBody body = new RigidBody(info);
/*                Transform center = new Transform();
                body.setCenterOfMassTransform();*/
            body.translate(new javax.vecmath.Vector3f(obj.getPosition().x, obj.getPosition().y, obj.getPosition().z));
            rigidBodies.add(body);
            body.setUserPointer(obj.getId());
            this.world.addRigidBody(body);
        }
        this.world.debugDrawWorld();
        if (this.ray != null && engine.getCamera() != null) {
            Vector3f cameraPos = engine.getCamera().getPosition().sub(0, 0, 0, new Vector3f());
            Vector3f trace = engine.getCamera().getPosition().add(this.ray, new Vector3f());
            this.debugRenderer.setLineThickness(3F);
            this.debugRenderer.drawLine(new javax.vecmath.Vector3f(cameraPos.x, cameraPos.y, cameraPos.z), new javax.vecmath.Vector3f(trace.x, trace.y, trace.z), new javax.vecmath.Vector3f(0.5F, 0.5F, 0.5F));
            this.debugRenderer.drawLine(new javax.vecmath.Vector3f(0, 40, 0), new javax.vecmath.Vector3f(5, 40, 5), new javax.vecmath.Vector3f(0.5F, 0.5F, 0.5F));
        }
        for (RigidBody body : rigidBodies) {
            this.world.removeRigidBody(body);
        }
    }

    public void setDebugRenderer(GameEngine3D.DebugRenderer renderer) {
        this.debugRenderer = renderer;
        this.world.setDebugDrawer(renderer);
    }

/*
    public boolean isObjectInFrustrum(Camera camera, GameObject object) {
        float nearSq = 2F * (float) Math.tan(Camera.FOV / 2F) * Camera.Z_NEAR;
        float farSq = 2F * (float) Math.tan(Camera.FOV / 2F) * Camera.Z_FAR;
        camera.getRotation()
    }*/

    private static Matrix4f createViewMatrix(Camera camera) {
        Vector3f position = camera.getPosition();
        Vector3f rotation = camera.getRotation();
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.identity();
        matrix4f.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0)).rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0)).rotate((float) Math.toRadians(rotation.z), new Vector3f(0, 0, 1));
        matrix4f.translate(-position.x, -position.y, -position.z);
        return matrix4f;
    }

    private Vector3f calculateMouseRay(Point point, GameEngine engine) {
        float mouseX = point.x;
        float mouseY = point.y;
        Vector2f normalizedCoords = getNormalisedDeviceCoordinates(engine, mouseX, mouseY);
        Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1.0f, 1.0f);
        Vector4f eyeCoords = toEyeCoords(engine, clipCoords);
        Vector3f worldRay = toWorldCoords(engine, eyeCoords);
        return worldRay;
    }

    private Vector3f toWorldCoords(GameEngine engine, Vector4f eyeCoords) {
        Matrix4f invertedView = createViewMatrix(engine.getCamera()).invert(new Matrix4f());
        Vector4f rayWorld = invertedView.transform(eyeCoords, new Vector4f());
        Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
        mouseRay.normalize();
        return mouseRay;
    }

    private Vector4f toEyeCoords(GameEngine engine, Vector4f clipCoords) {
        Matrix4f invertedProjection = engine.getCamera().getProjectionMatrix().invert(new Matrix4f());
        Vector4f eyeCoords = invertedProjection.transform(clipCoords, new Vector4f());
        return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
    }

    private Vector2f getNormalisedDeviceCoordinates(GameEngine engine, float mouseX, float mouseY) {
        float x = (2.0f * mouseX) / engine.getPanel().getWidth() - 1f;
        float y = (2.0f * mouseY) / engine.getPanel().getHeight() - 1f;
        return new Vector2f(x, y);
    }
}
