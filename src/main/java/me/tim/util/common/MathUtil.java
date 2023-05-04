package me.tim.util.common;

import org.lwjgl.BufferUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.FloatBuffer;

public class MathUtil {
    public static Double random(float min, float max) {
        return min + (max - min) * Math.random();
    }

    public static Double interpolate(double oldValue, double newValue, double interpolationValue) {
        return (oldValue + (newValue - oldValue) * interpolationValue);
    }

    public static Double calculateGaussianValue(float x, float sigma) {
        double output = 1.0 / Math.sqrt(2.0 * Math.PI * (sigma * sigma));
        return output * Math.exp(-(x * x) / (2.0 * (sigma * sigma)));
    }

    public static Float percentage(float value, float maxValue) {
        return value / maxValue;
    }

    public static Double round(double value, int places) {
        BigDecimal bigDecimal = new BigDecimal(value).setScale(places, RoundingMode.HALF_EVEN);
        return bigDecimal.doubleValue();
    }

    public static FloatBuffer makeGausBuffer(float radius) {
        FloatBuffer weights = BufferUtils.createFloatBuffer(256);
        for (int i = 0; i <= radius; i++) {
            weights.put(MathUtil.calculateGaussianValue(i, radius).floatValue());
        }
        weights.rewind();
        return weights;
    }
}
