package edu.cg;

import edu.cg.algebra.Vec;
import edu.cg.models.Locomotive.Locomotive;
import edu.cg.models.Track.Track;
import edu.cg.models.Track.TrackSegment;
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

    // TODO: Set the initial position of the vehicle in the scene by assigning a value to carInitialPosition.
    private final double[] carInitialPosition = null;

    // TODO: set the car scale as you wish - we uniformly scale the car by 3.0.

    // TODO: You can add additional fields to assist your implementation, for example:
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
        // TODO : Define background color for the scene in day mode and in night.
        if (this.isDayMode) {
            // TODO: Setup background when day mode is on
            // use gl.glClearColor() function.
        } else {
            // TODO: Setup background when night mode is on.
        }
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        // TODO: Read this part of the code, understand the flow that goes into rendering the scene.
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
        // TODO: In this method you are advised to use :
        //       GLU glu = new GLU();
        //       glu.gluLookAt();
        if (this.isBirdseyeView) {
            // TODO Setup camera for the Birds-eye view (You need to configure the viewing transformation accordingly).
        } else {
            // TODO Setup camera for standard 3rd person view.
        }
    }

    private void setupLights() {
        if (this.isDayMode) {
            // TODO Setup day lighting.
            // * Remember: switch-off any light sources that were used in night mode and are not use in day mode.

        } else {
            // TODO Setup night lighting - here you should only set the ambient light source.
            //      The locomotive's spotlights should be defined in the car local coordinate system.
            //      so it is better to define the car light properties right before your render the locomotive rather
            //      than at this point.
        }
    }

    private void renderTrack() {
        glPushMatrix();
        // TODO : Note that if you wish to support textures, the render method of gameTrack must be changed.
        this.gameTrack.render();
        glPopMatrix();
    }

    private void renderVehicle() {
        // TODO: Render the vehicle.
        // * Remember: the vehicle's position should be the initial position + the accumulated translation.
        //             This will simulate the car movement.
        // * Remember: the car was modeled locally, you may need to rotate/scale and translate the car appropriately.
        // * Recommendation: it is recommended to define fields (such as car initial position) that can be used during rendering.
        // * You should set up the car lights right before you render the locomotive after the appropriate transformations
        // * have been applied. This ensures that the light sources are fixed to the locomotive (ofcourse all of this
        // * is only relevant to rendering the vehicle in night mode).
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
        if (this.isBirdseyeView) {
            // TODO : Set a projection matrix for birdseye view mode.
        } else {
            // TODO : Set a projection matrix for third person mode.
        }
    }

    public void toggleNightMode() {
        this.isDayMode = !this.isDayMode;
    }

    public void changeViewMode() {
        this.isBirdseyeView = !this.isBirdseyeView;
    }
}
