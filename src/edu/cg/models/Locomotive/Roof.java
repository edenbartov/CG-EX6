package edu.cg.models.Locomotive;

import edu.cg.models.IRenderable;

import static org.lwjgl.opengl.GL11.*;

/***
 * A 3D roof model renderer.
 * The roof is modeled using a cylinder bounded by disks that undergo a non-uniform scaling.
 */
public class Roof implements IRenderable {

    @Override
    public void render() {
        glPushMatrix();
        // TODO(7): Copy your code from HW5.
        glPopMatrix();
    }

    @Override
    public void init() {

    }
}
