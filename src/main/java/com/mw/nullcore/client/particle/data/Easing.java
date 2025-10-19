package com.mw.nullcore.client.particle.data;

public interface Easing {
    Easing linear = amp -> amp;
    Easing reverse = amp -> 1f - amp;
    Easing smooth = amp -> amp * amp * (3 - 2 * amp);

    float apply(float amp);
}
