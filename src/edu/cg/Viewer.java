package edu.cg;

import edu.cg.algebra.Vec;
import edu.cg.models.Locomotive.Locomotive;
import edu.cg.models.Locomotive.Specification;
import edu.cg.models.Track.Track;
import edu.cg.models.Track.TrackSegment;
import edu.cg.util.glu.GLU;
import edu.cg.util.glu.Project;;
import static edu.cg.util.glu.Project.gluPerspective;
import static org.lwjgl.opengl.GL11.glLightModelfv;
import static org.lwjgl.opengl.GL21.*;

/**
 * An OpenGL model viewer
 */
public class Viewer {
    int canvasWidth, canvasHeight;
    private final GameState gameState; // Tracks the vehicle movement and orientation
    private final Locomotive car; // The locomotive we wish to render.
    private final Track gameTrack; // The track we wish to render.
    // driving direction, or looking down on the scene from above.
    private Vec carCameraTranslation; // The accumulated translation that should be applied on the car and camera.
    private boolean isModelInitialized = false; // Indicates whether initModel was called.
    private boolean isDayMode = true; // Indicates whether the lighting mode is day/night.
    private boolean isBirdseyeView = false; // Indicates whether the camera's perspective corresponds to the vehicle's

    // Done: Set the initial position of the vehicle in the scene by assigning a value to carInitialPosition.
    private final double[] carInitialPosition = {0,1,-4.5};
    private final double[] cameraInitialPositionThirdPerson = {carInitialPosition[0], 3.0 ,0};
    private final double[] cameraInitialPositionBirdseye = {carInitialPosition[0], 50.0 ,-22 };

    // Done: set the car scale as you wish - we uniformly scale the car by 3.0.
    private final double CarScale = 3.0;

    // Done: You can add additional fields to assist your implementation, for example:
    // - Camera initial position for standard 3rd person mode(should be fixed)
    // - Camera initial position for birdseye view)
    // - Light colors
    // Or in short anything reusable - this make it easier for your to keep track of your implementation.



    public Viewer(int width, int height) {
        canvasWidth = width;
        canvasHeight = height;
        this.gameState = new GameState();
        this.gameTrack = new Track();
        this.carCameraTranslation = new Vec(0.0D);
        this.car = new Locomotive();
    }

    public void render() {
        if (!this.isModelInitialized)
            initModel();
        // Done : Define background color for the scene in day mode and in night.
        if (this.isDayMode) {
            // Done: Setup background when day mode is on
            // use gl.glClearColor() function.
            glClearColor(0f, 215f/255f, 1f,100f/255f);
        } else {
            // Done: Setup background when night mode is on.

            glClearColor(0f, 0f, 25f / 255f,100f/255f);

        }
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        // Done: Read this part of the code, understand the flow that goes into rendering the scene.
        // Step (1) Update the accumulated translation that needs to be
        // applied on the car, camera and light sources.
        updateCarCameraTranslation();
        // Step (2) Position the camera and setup its orientation.
        setupCamera();
        // Step (3) setup the lights.
        setupLights();
        // Step (4) render the car.
        renderVehicle();
        // Step (5) render the track.
        renderTrack();
    }

    public void init() {
        // TODO(*) In your final submission you need to make sure that BACK FACE culling is enabled.
        //      You may disable face culling while building your model, and only later return it.
        //      Note that doing so may require you to modify the way you present the vertices to OPENGL in order for the
        //      normal of all surface be facing outside. See recitation 8 for more information about face culling.
        glCullFace(GL_BACK);    // Set Culling Face To Back Face
        glEnable(GL_CULL_FACE); // Enable back face culling

        // Enable other flags for OPENGL.
        glEnable(GL_NORMALIZE);
        glEnable(GL_DEPTH_TEST);


        reshape(0, 0, canvasWidth, canvasHeight);
    }

    private void updateCarCameraTranslation() {
        // Here we update the car and camera translation values (not the ModelView-Matrix).
        // - We always keep track of the car offset relative to the starting
        // point.
        // - We change the track segments here if necessary.
        // getNextTranslation returns the delta - the change to be accounted for in the translation.
        // getNextTranslation returns the delta - the change to be accounted for in the translation.
        Vec ret = this.gameState.getNextTranslation();
        this.carCameraTranslation = this.carCameraTranslation.add(ret);
        // Min and Max calls to make sure we do not exceed the lateral boundaries of the track.
        double dx = Math.max(this.carCameraTranslation.x, -TrackSegment.ASPHALT_LENGTH / 2 - 2);
        this.carCameraTranslation.x = (float) Math.min(dx, TrackSegment.ASPHALT_LENGTH / 2 + 2);
        // If the car reaches the end of the track segment, we generate a new segment.
        if (Math.abs(this.carCameraTranslation.z) >= TrackSegment.TRACK_SEGMENT_LENGTH - this.carInitialPosition[2]) {
            this.carCameraTranslation.z = -((float) (Math.abs(this.carCameraTranslation.z) % TrackSegment.TRACK_SEGMENT_LENGTH));
            this.gameTrack.changeTrackSegment();
        }
    }

    private void setupCamera() {
        // Done: In this method you are advised to use :
        //       GLU glu = new GLU();
        //       glu.gluLookAt();
        GLU glu = new GLU();
        float eyex =  this.carCameraTranslation.x;
        float eyey = this.carCameraTranslation.y;
        float eyez = this.carCameraTranslation.z;
        float upx,upy,upz,centraly,centralz;
        if (this.isBirdseyeView) {
            // Done Setup camera for the Birds-eye view (You need to configure the viewing transformation accordingly).
            eyex += (float)(cameraInitialPositionBirdseye[0]);
            eyey += (float)(cameraInitialPositionBirdseye[1]);
            eyez += (float)(cameraInitialPositionBirdseye[2]);
            upx = 0f;
            upy = 0f;
            upz = -1f;
            centraly = eyey-1f;
            centralz = eyez;
        } else {
            // Done Setup camera for standard 3rd person view.
            eyex += (float)(cameraInitialPositionThirdPerson[0]);
            eyey += (float)(cameraInitialPositionThirdPerson[1]);
            eyez += (float)(cameraInitialPositionThirdPerson[2]);
            upx = 0f;
            upy = 1f;
            upz = 0f;
            centraly = eyey;
            centralz = eyez-10f;
        }
        glu.gluLookAt(eyex,eyey,eyez,eyex,centraly,centralz,upx,upy,upz);

    }
    private void setupDay() {
        float[] dayColor =  new float[]{1f, 1f, 1f, 1f};
        Vec lightDirection = (new Vec(0d, 1d, 1d)).normalize();
        float[] position = new float[]{lightDirection.x, lightDirection.y, lightDirection.z, 0f};
        glLightfv(GL_LIGHT0, GL_SPECULAR, dayColor);
        glLightfv(GL_LIGHT0, GL_DIFFUSE, dayColor);
        glLightfv(GL_LIGHT0, GL_POSITION,position);
        glLightfv(GL_LIGHT0, GL_AMBIENT,  new float[]{0.1f, 0.1f, 0.1f, 1f});
        glEnable(GL_LIGHT0);
    }

    private void setupNightligt() {
        glLightModelfv(GL_LIGHT_MODEL_AMBIENT, new float[]{0.25f, 0.25f, 0.3f, 1.0f});
    }
    private void setupOneLight(float[] position, float[] directions,int color) {
        float[] lightColor = new float[]{0.85f, 0.85f, 0.85f, 1.0f};
        glLightfv(color, GL_POSITION, position);
        glLightf(color, GL_SPOT_CUTOFF, 90.0f);
        glLightfv(color, GL_SPOT_DIRECTION, directions);
        glLightfv(color, GL_SPECULAR, lightColor);
        glLightfv(color, GL_DIFFUSE, lightColor);
        glEnable(color);    }

    private void setupBothLights() {
        float x =(float)(Specification.FRONT_BODY_WIDTH/4),
                y =(float)(Specification.FRONT_BODY_HEIGHT),
                z = (float)(Specification.FRONT_BODY_DEPTH + Specification.EPS);
        float[] firstPos = new float[]{x, y, z, 1f};
        float[] secondPos = new float[]{-x, y, z, 1f};
        float[] direction = new float[]{0f, 0f, 1f, 0f};
        this.setupOneLight(firstPos, direction,GL_LIGHT2);
        this.setupOneLight(secondPos, direction,GL_LIGHT1);
    }

    private void setupLights() {
        if (this.isDayMode) {
            // Done Setup day lighting.
            this.setupDay();
            //Turn of the headlights
            glDisable(GL_LIGHT1);
            glDisable(GL_LIGHT2);

            // * Remember: switch-off any light sources that were used in night mode and are not use in day mode.

        } else {
            // Done Setup night lighting - here you should only set the ambient light source.
            //      The locomotive's spotlights should be defined in the car local coordinate system.
            //      so it is better to define the car light properties right before your render the locomotive rather
            //      than at this point.

            setupNightligt();
            //Turn of the Day Light
            glDisable(GL_LIGHT0);

        }

    }

    private void renderTrack() {
        glPushMatrix();
        // Done : Note that if you wish to support textures, the render method of gameTrack must be changed.
        this.gameTrack.render();
        glPopMatrix();
    }

    private void renderVehicle() {
        // Done: Render the vehicle.
        // * Remember: the vehicle's position should be the initial position + the accumulated translation.
        //             This will simulate the car movement.
        // * Remember: the car was modeled locally, you may need to rotate/scale and translate the car appropriately.
        // * Recommendation: it is recommended to define fields (such as car initial position) that can be used during rendering.
        // * You should set up the car lights right before you render the locomotive after the appropriate transformations
        // * have been applied. This ensures that the light sources are fixed to the locomotive (ofcourse all of this
        // * is only relevant to rendering the vehicle in night mode).
        glPushMatrix();
        glTranslated(this.carInitialPosition[0] + (double)this.carCameraTranslation.x,
                this.carInitialPosition[1] + (double)this.carCameraTranslation.y,
                this.carInitialPosition[2] + (double)this.carCameraTranslation.z);
        glRotated(180d - this.gameState.getCarRotation(), 0d, 1d, 0d);
        // scale by CarScale
        glScaled(this.CarScale, this.CarScale, this.CarScale);
        if (!this.isDayMode) {
            //turn on the headlights
            this.setupBothLights();
        }
        this.car.render();
        glPopMatrix();
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public void initModel() {
        glCullFace(GL_BACK);
        glEnable(GL_CULL_FACE);
        glEnable(GL_NORMALIZE);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_LIGHTING);
        glEnable(GL_SMOOTH);
        this.gameTrack.init();
        this.car.init();
        this.isModelInitialized = true;
    }


    public void reshape(int x, int y, int width, int height) {
        // We recommend using gluPerspective, which receives the field of view in the y-direction. You can use this
        // method by importing it via:
        // >> import static edu.cg.util.glu.Project.gluPerspective;
        // Further information about this method can be found in the recitation materials.
        glViewport(x, y, width, height);
        canvasWidth = width;
        canvasHeight = height;
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float aspect = (float)width / (float)height;
        if (this.isBirdseyeView) {
            // Done : Set a projection matrix for birdseye view mode.
            float fovy = 46;
            Project.gluPerspective(fovy, aspect, (float)(this.cameraInitialPositionBirdseye[1] - 10.0),
                    (float)this.cameraInitialPositionBirdseye[1] + 10.0f);
        } else {
            // Done : Set a projection matrix for third person mode.
            float fovy = 120.0f;
            gluPerspective(fovy, aspect, .1f,250f);
        }

    }

    public void toggleNightMode() {
        this.isDayMode = !this.isDayMode;
    }

    public void changeViewMode() {
        this.isBirdseyeView = !this.isBirdseyeView;
    }
}
