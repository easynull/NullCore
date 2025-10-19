package com.mw.nullcore.client.particle.data;

import net.minecraft.util.Mth;

import java.awt.*;

public final class ColorParticleData {
    public final float r1, g1, b1, r2, g2, b2;
    public final float rr11, rr12, rb11, rb12, rg11, rg12, rr21, rr22, rb21, rb22, rg21, rg22;
    public final float colorCoefficient;
    public final Easing colorCurveEasing;

    public float coefficientMultiplier = 1;

    ColorParticleData(float r1, float g1, float b1, float r2, float g2, float b2, float rr11, float rr12, float rb11, float rb12, float rg11, float rg12, float rr21, float rr22, float rb21, float rb22, float rg21, float rg22, float colorCoefficient, Easing colorCurveEasing){
        this.r1 = r1;
        this.g1 = g1;
        this.b1 = b1;
        this.r2 = r2;
        this.g2 = g2;
        this.b2 = b2;
        this.rr11 = rr11;
        this.rr12 = rr12;
        this.rb11 = rb11;
        this.rb12 = rb12;
        this.rg11 = rg11;
        this.rg12 = rg12;
        this.rr21 = rr21;
        this.rr22 = rr22;
        this.rb21 = rb21;
        this.rb22 = rb22;
        this.rg21 = rg21;
        this.rg22 = rg22;
        this.colorCoefficient = colorCoefficient;
        this.colorCurveEasing = colorCurveEasing;
    }

    public ColorParticleData multiplyCoefficient(float coefficientMultiplier){
        this.coefficientMultiplier *= coefficientMultiplier;
        return this;
    }

    public ColorParticleData overrideCoefficientMultiplier(float coefficientMultiplier){
        this.coefficientMultiplier = coefficientMultiplier;
        return this;
    }

    public float getProgress(float age, float lifetime){
        return Mth.clamp((age * colorCoefficient * coefficientMultiplier) / lifetime, 0, 1);
    }

    public static ColorParticleDataBuilder create(float r1, float g1, float b1, float r2, float g2, float b2){
        return new ColorParticleDataBuilder(r1, g1, b1, r2, g2, b2);
    }

    public static ColorParticleDataBuilder create(float r, float g, float b){
        return new ColorParticleDataBuilder(r, g, b, r, g, b);
    }

    public static ColorParticleDataBuilder create(Color start, Color end){
        return create(start.getRed(), start.getGreen(), start.getBlue(), end.getRed(), end.getGreen(), end.getBlue());
    }

    public static ColorParticleDataBuilder create(Color color){
        return create(color.getRed(), color.getGreen(), color.getBlue());
    }

    public static ColorParticleDataBuilder create(){
        return create(0, 0, 0);
    }

    public static final class ColorParticleDataBuilder{
        private float r1, g1, b1, r2, g2, b2;
        private float rr11 = -1, rr12 = -1, rb11 = -1, rb12 = -1, rg11 = -1, rg12 = -1, rr21 = -1, rr22 = -1, rb21 = -1, rb22 = -1, rg21 = -1, rg22 = -1;
        private float coefficient = 1f;
        private Easing easing = Easing.linear;

        private ColorParticleDataBuilder(float r1, float g1, float b1, float r2, float g2, float b2){
            this.r1 = r1;
            this.g1 = g1;
            this.b1 = b1;
            this.r2 = r2;
            this.g2 = g2;
            this.b2 = b2;
        }

        public ColorParticleDataBuilder setCoefficient(float coefficient){
            this.coefficient = coefficient;
            return this;
        }

        public ColorParticleDataBuilder setEasing(Easing easing){
            this.easing = easing;
            return this;
        }

        public ColorParticleDataBuilder setRandomColor(float rr11, float rr12, float rb11, float rb12, float rg11, float rg12, float rr21, float rr22, float rb21, float rb22, float rg21, float rg22){
            this.rr11 = rr11;
            this.rr12 = rr12;
            this.rb11 = rb11;
            this.rb12 = rb12;
            this.rg11 = rg11;
            this.rg12 = rg12;
            this.rr21 = rr21;
            this.rr22 = rr22;
            this.rb21 = rb21;
            this.rb22 = rb22;
            this.rg21 = rg21;
            this.rg22 = rg22;
            return this;
        }

        public ColorParticleDataBuilder setRandomColor(float rr11, float rr12, float rb11, float rb12, float rg11, float rg12){
            this.rr11 = rr11;
            this.rr12 = rr12;
            this.rb11 = rb11;
            this.rb12 = rb12;
            this.rg11 = rg11;
            this.rg12 = rg12;
            return this;
        }

        public ColorParticleDataBuilder setRandomColor(Color start1, Color start2, Color end1, Color end2){
            setRandomColor(start1.getRed(), start2.getRed(), start1.getGreen(), start2.getGreen(), start1.getBlue(), start2.getBlue(), end1.getRed(), end2.getRed(), end1.getGreen(), end2.getGreen(), end1.getBlue(), end2.getBlue());
            return this;
        }

        public ColorParticleDataBuilder setRandomColor(Color start1, Color start2){
            setRandomColor(start1.getRed(), start2.getRed(), start1.getGreen(), start2.getGreen(), start1.getBlue(), start2.getBlue());
            return this;
        }

        public ColorParticleDataBuilder setRandomColor(){
            setRandomColor(0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1);
            return this;
        }

        public ColorParticleDataBuilder setRandomColorFlat(){
            setRandomColor(0, 1, 0, 1, 0, 1);
            return this;
        }

        public ColorParticleData build(){
            return new ColorParticleData(r1, g1, b1, r2, g2, b2, rr11, rr12, rb11, rb12, rg11, rg12, rr21, rr22, rb21, rb22, rg21, rg22, coefficient, easing);
        }
    }
}
