package mjolk.engine.graphics.lighting;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.logging.Logger;
import static org.lwjgl.opengl.GL11.GL_NONE;

public class PointLight extends Light {

    private static final Logger LOGGER = Logger.getLogger(PointLight.class.getName());
    private Vector3f position;
    private float constant, linear, exponent;
    private Vector4f frontRect;
    private Vector4f backRect;


    public PointLight(Vector3f colour, Vector3f position, float intensity, float constant, float linear, float exponent) throws Exception {
        super(colour, intensity);
        this.position = position;
        this.constant = constant;
        this.linear = linear;
        this.exponent = exponent;

        this.castsShadows = true;

        LOGGER.warning("PointLight created: " + this);

        frontRect = new Vector4f(0.0f, 0.0f, 0.25f, 0.25f);
        backRect  = new Vector4f(0.25f, 0.0f, 0.25f, 0.25f);
    }

    public float getExponent() {
        return exponent;
    }

    public void setExponent(float exponent) {
        this.exponent = exponent;
    }

    public float getLinear() {
        return linear;
    }

    public void setLinear(float linear) {
        this.linear = linear;
    }

    public float getConstant() {
        return constant;
    }

    public void setConstant(float constant) {
        this.constant = constant;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    @Override
    public Matrix4f getViewProjectionMatrix() {
        return null;
    }

    public float getFarPlane() {
        return 50f;
    }

    public Vector4f getFrontRect() {
        return frontRect;
    }

    public void setFrontRect(Vector4f frontRect) {
        this.frontRect = frontRect;
    }

    public Vector4f getBackRect() {
        return backRect;
    }

    public void setBackRect(Vector4f backRect) {
        this.backRect = backRect;
    }
}
