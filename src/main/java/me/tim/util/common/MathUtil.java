package me.tim.util.common;

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
}
