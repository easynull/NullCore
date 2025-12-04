package com.mw.nullcore.core.builders;

import com.mw.nullcore.core.NcUtils;
import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class CameraShakeBuilder {
    public static final Set<CameraShakeBuilder> shakes = new HashSet<>();
    float frequency, amplitude;
    float distance, maxDistance;
    int time;

    public static CameraShakeBuilder builder(){
        return new CameraShakeBuilder();
    }
    //recommend: ???, 0.06f, 0.02f, ???, ???
    public CameraShakeBuilder shake(int time, float frequency, float amplitude, float distance, float maxDistance){
        this.time = time;
        this.frequency = frequency;
        this.amplitude = amplitude;
        this.distance = distance;
        this.maxDistance = maxDistance;
        shakes.add(this);
        return this;
    }

    public CameraShakeBuilder shake(int time, float frequency, float amplitude, float distance){
        return shake(time, frequency, amplitude, distance, 25f);
    }

    public CameraShakeBuilder shake(int time, float frequency, float amplitude){
        return shake(time, frequency, amplitude, 0, 0);
    }

    public CameraShakeBuilder shake(int time, float frequency, float amplitude, Vec3 from, Vec3 to, float maxDistance){
        return shake(time, frequency, amplitude, (float) from.distanceTo(to), maxDistance);
    }

    public static void tick(Camera cam) {
        if(!shakes.isEmpty()) {
            for (var shake : shakes) {
                if (shake.time >= 0) {
                    float dis = 1f - Math.min(1f, shake.distance != 0 && shake.maxDistance != 0 ? shake.distance / shake.maxDistance : 0);
                    float progress = (shake.time / 20f) * dis;
                    float intensity = shake.amplitude * progress * progress;
                    float frequency = shake.frequency * (0.5f + progress * 0.5f);

                    float time = NcUtils.Render.getAnimationTick() * frequency * 0.2f;

                    float offsetX = calculateNoise(time, 0, 0, frequency) * intensity * 0.6f;
                    float offsetY = calculateNoise(0, time, 0, frequency) * intensity;
                    float offsetZ = calculateNoise(0, 0, time, frequency) * intensity * 0.6f;

                    Vec3 vec = cam.getPosition();
                    cam.setPosition(vec.x() + offsetX, vec.y() + offsetY, vec.z() + offsetZ);
                    shake.time--;
                }
            }
            shakes.removeIf(shake -> shake.time <= 0);
        }
    }

    public static float calculateNoise(float x, float y, float z, float frequency) {
        return (float) (Math.sin(x * frequency * 1.0f) * 0.4f + Math.cos(y * frequency * 1.7f) * 0.3f + Math.sin(z * frequency * 2.3f + 1.5f) * 0.2f + Math.cos(x * frequency * 3.1f + y * 2.7f) * 0.1f);
    }
}
