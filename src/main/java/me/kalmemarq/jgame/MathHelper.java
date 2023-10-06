package me.kalmemarq.jgame;

public class MathHelper {
    /**
     * Restricts a given int value between an upper and lower bound.
     * <p>
     * If the value is below the lower bound then return the minimum value.
     * If the value is above the upper bound then return the maximum value.
     * Otherwise, return the value.
     * </p>
     * @param value the value to clamp
     * @param min the lower bound
     * @param max the upper bound
     * @return the clamped value
     */
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    /**
     * Restricts a given float value between an upper and lower bound.
     * <p>
     * If the value is below the lower bound then return the minimum value.
     * If the value is above the upper bound then return the maximum value.
     * Otherwise, return the value.
     * </p>
     * @param value the value to clamp
     * @param min the lower bound
     * @param max the upper bound
     * @return the clamped value
     */
    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }
}
