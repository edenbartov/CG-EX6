package edu.cg.models.Locomotive;

import edu.cg.models.Box;
import edu.cg.models.IRenderable;

import static org.lwjgl.opengl.GL21.*;


/***
 * A 3D locomotive back body renderer. The back-body of the locomotive model is composed of a chassis, two back wheels,
 * , a roof, windows and a door.
 */
public class BackBody implements IRenderable {
    // The back body is composed of one box that represents the locomotive front body.
    private Box chassis = new Box(Specification.BACK_BODY_WIDTH, Specification.BACK_BODY_HEIGHT, Specification.BACK_BODY_DEPTH);
    // The back body is composed of two back wheels.
    private Wheel wheel = new Wheel();
    // The back body is composed of a roof that lies on-top of the locomotive chassis.
    private Roof roof = new Roof();
    // Done (9): Define your window/door objects here. You are free to implement these models as you wish as long as you
    //           stick to the locomotive sketch.

    private Box smallWindow = new Box(Specification.SMALL_WINDOW_WIDTH, Specification.SMALL_WINDOW_HIGHT, Specification.SMALL_WINDOW_DEPTH);

    private Box bigWindow = new Box(Specification.BIG_WINDOW_WIDTH, Specification.BIG_WINDOW_HIGHT, Specification.BIG_WINDOW_DEPTH);

    private Box door = new Box(Specification.DOOR_WINDOW_WIDTH, Specification.DOOR_HIGHT, Specification.DOOR_DEPTH);

    @Override
    public void render() {
        glPushMatrix();
        // Done(8): render the back-body of the locomotive model. You need to combine the chassis, wheels and roof using
        //          affine transformations. In addition, you need to render the back-body windows and door. You can do
        //          that using simple QUADRATIC polygons (use GL_QUADS).
        drawChassis();
        drawRoof();
        drawWheel(1);
        drawWheel(-1);

        //draw small windows
        for(int i = 0; i<3; i++){
            drawSmallWindow(i,1);
            if (i<2){
                drawSmallWindow(i,-1);
            }
        }
        drawBigWindow(1);
        drawBigWindow(-1);
        drawDoor();
        glPopMatrix();
    }
    public void drawChassis(){
        glPushMatrix();
        Materials.setMaterialChassis();
        chassis.render();
        glPopMatrix();

    }
    public void drawRoof(){
        glPushMatrix();
        glTranslatef(0, (float) Specification.BACK_BODY_HEIGHT / 2, (float) (-Specification.BACK_BODY_DEPTH / 2 + Specification.EPS));
        roof.render();
        glPopMatrix();
    }

    public void drawSmallWindow(int index,int leftSide){
        float ZofWindow = - (float)((Specification.BACK_BODY_DEPTH - Specification.BASE_UNIT) / 2.0) ;
        glPushMatrix();
        Materials.setMaterialWindow();
        glTranslatef(leftSide* ((float) (Specification.BACK_BODY_WIDTH/2 + Specification.EPS)),
                (float) ((Specification.BACK_BODY_HEIGHT - Specification.SMALL_WINDOW_HIGHT)/4),
                (float) (ZofWindow + Specification.BASE_UNIT + (Specification.BASE_UNIT + Specification.SMALL_WINDOW_WIDTH)*index ));
        //0);
        glRotated(leftSide * 90, 0, 1, 0);
        smallWindow.render();
        glPopMatrix();
    }

    public void drawBigWindow(int isBack){
        glPushMatrix();
        Materials.setMaterialWindow();
        glTranslatef(0,
                (float) ((Specification.BACK_BODY_HEIGHT - Specification.BIG_WINDOW_HIGHT)/4),
                isBack * (float) (Specification.BACK_BODY_DEPTH / 2 + Specification.EPS));
        if (isBack == -1){
            glRotated(180, 1, 0, 0);
        }
        bigWindow.render();
        glPopMatrix();
    }
    public void drawDoor(){
        glPushMatrix();
        Materials.setMaterialWindow();
        glTranslatef((float) -(Specification.BACK_BODY_WIDTH/2 + Specification.EPS),
                -(float)((Specification.BACK_BODY_HEIGHT-Specification.DOOR_HIGHT) /2.0),
                (float) (2.5 * Specification.BASE_UNIT));
        //
        glRotated(-90, 0, 1, 0);
        door.render();
        glPopMatrix();
    }

    public void drawWheel(int isLeft){
        glPushMatrix();
        glTranslated(isLeft * Specification.BACK_BODY_WIDTH/2,
                -Specification.BACK_BODY_HEIGHT/2,
                -Specification.WHEEL_RADIUS + Specification.EPS);
        wheel.render();
        glPopMatrix();
    }

    @Override
    public void init() {

    }
}
