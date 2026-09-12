package org.firstinspires.ftc.teamcode.gytd.vision;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;

/**
 * Base class for vision processing pipelines.
 * Extend this class to implement custom vision processing logic.
 */
public abstract class VisionPipeline {
    protected Mat input;
    protected Mat output;

    public abstract void process(Mat input);

    public Mat getOutput() {
        return output;
    }

    public void releaseResources() {
        if (input != null) {
            input.release();
        }
        if (output != null) {
            output.release();
        }
    }
}

