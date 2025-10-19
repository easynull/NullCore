package com.mw.nullcore.client.particle.data;

import net.minecraft.util.RandomSource;

import java.util.Random;

public final class SpinParticleData extends GenericParticleData{
    public final float spinOffset;
    public final float rsp1, rsp2;
    public final float rso1, rso2;

    SpinParticleData(float spinOffset, float rsp1, float rsp2, float rso1, float rso2, float startingValue, float middleValue, float endingValue, float rs1, float rs2, float rm1, float rm2, float re1, float re2, float coefficient, Easing startToMiddleEasing, Easing middleToEndEasing){
        super(startingValue, middleValue, endingValue, rs1, rs2, rm1, rm2, re1, re2, coefficient, startToMiddleEasing, middleToEndEasing);
        this.spinOffset = spinOffset;
        this.rsp1 = rsp1;
        this.rsp2 = rsp2;
        this.rso1 = rso1;
        this.rso2 = rso2;
    }

    @Override
    public SpinParticleData copy(){
        return new SpinParticleData(spinOffset, rsp1, rsp2, rso1, rso2, startingValue, middleValue, endingValue, rs1, rs2, rm1, rm2, re1, re2, coefficient, startToMiddleEasing, middleToEndEasing).overrideValueMultiplier(valueMultiplier).overrideCoefficientMultiplier(coefficientMultiplier);
    }

    @Override
    public SpinParticleData bake(){
        return new SpinParticleData(spinOffset, rsp1, rsp2, rso1, rso2, startingValue * valueMultiplier, middleValue * valueMultiplier, endingValue * valueMultiplier, rs1 * valueMultiplier, rs2 * valueMultiplier, rm1 * valueMultiplier, rm2 * valueMultiplier, re1 * valueMultiplier, re2 * valueMultiplier, coefficient * coefficientMultiplier, startToMiddleEasing, middleToEndEasing);
    }

    @Override
    public SpinParticleData overrideValueMultiplier(float valueMultiplier){
        return (SpinParticleData)super.overrideValueMultiplier(valueMultiplier);
    }

    @Override
    public SpinParticleData overrideCoefficientMultiplier(float coefficientMultiplier){
        return (SpinParticleData)super.overrideCoefficientMultiplier(coefficientMultiplier);
    }

    public static SpinParticleDataBuilder create(){
        return new SpinParticleDataBuilder(0, 0, 0);
    }

    public static SpinParticleDataBuilder create(float value){
        return new SpinParticleDataBuilder(value, value, -1);
    }

    public static SpinParticleDataBuilder create(float startingValue, float endingValue){
        return new SpinParticleDataBuilder(startingValue, endingValue, -1);
    }

    public static SpinParticleDataBuilder create(float startingValue, float middleValue, float endingValue){
        return new SpinParticleDataBuilder(startingValue, middleValue, endingValue);
    }

    public static SpinParticleDataBuilder createRandomDirection(RandomSource random, float value){
        value *= random.nextBoolean() ? 1 : -1;
        return new SpinParticleDataBuilder(value, value, -1);
    }

    public static SpinParticleDataBuilder createRandomDirection(RandomSource random, float startingValue, float endingValue){
        final int direction = random.nextBoolean() ? 1 : -1;
        startingValue *= direction;
        endingValue *= direction;
        return new SpinParticleDataBuilder(startingValue, endingValue, -1);
    }

    public static SpinParticleDataBuilder createRandomDirection(RandomSource random, float startingValue, float middleValue, float endingValue){
        final int direction = random.nextBoolean() ? 1 : -1;
        startingValue *= direction;
        middleValue *= direction;
        endingValue *= direction;
        return new SpinParticleDataBuilder(startingValue, middleValue, endingValue);
    }

    public static final class SpinParticleDataBuilder extends GenericParticleDataBuilder {
        private float spinOffset;
        private float rsp1 = 0, rsp2 = 0;
        private float rso1 = 0, rso2 = 0;

        public static final Random random = new Random();

        private SpinParticleDataBuilder(float startingValue, float middleValue, float endingValue){
            super(startingValue, middleValue, endingValue);
        }

        public SpinParticleDataBuilder setSpinOffset(float spinOffset){
            this.spinOffset = spinOffset;
            return this;
        }

        public SpinParticleDataBuilder randomSpinOffset(RandomSource random){
            this.spinOffset = random.nextFloat() * 6.28f;
            return this;
        }

        public SpinParticleDataBuilder setSpinOffsetDegrees(float spinOffset){
            this.spinOffset = (float)Math.toRadians(spinOffset);
            return this;
        }

        public SpinParticleDataBuilder randomOffset(){
            this.rso2 = 6.28f;
            return this;
        }

        public SpinParticleDataBuilder randomOffset(float spinOffset){
            this.rso2 = spinOffset;
            return this;
        }

        public SpinParticleDataBuilder randomOffset(float spinOffset1, float spinOffset2){
            this.rso1 = spinOffset1;
            this.rso2 = spinOffset2;
            return this;
        }

        public SpinParticleDataBuilder randomOffsetDegrees(float spinOffset){
            this.rso2 = (float)Math.toRadians(spinOffset);
            return this;
        }

        public SpinParticleDataBuilder randomOffsetDegrees(float spinOffset1, float spinOffset2){
            this.rso1 = (float)Math.toRadians(spinOffset1);
            this.rso2 = (float)Math.toRadians(spinOffset2);
            return this;
        }

        public SpinParticleDataBuilder randomSpin(float spin){
            this.rsp2 = spin;
            return this;
        }

        public SpinParticleDataBuilder randomSpin(float spin1, float spin2){
            this.rsp1 = spin1;
            this.rsp2 = spin2;
            return this;
        }

        public SpinParticleDataBuilder randomSpinDegrees(float spin){
            this.rsp2 = (float)Math.toRadians(spin);
            return this;
        }

        public SpinParticleDataBuilder randomSpinDegrees(float spin1, float spin2){
            this.rsp1 = (float)Math.toRadians(spin1);
            this.rsp2 = (float)Math.toRadians(spin2);
            return this;
        }

        @Override
        public SpinParticleDataBuilder setCoefficient(float coefficient){
            return (SpinParticleDataBuilder)super.setCoefficient(coefficient);
        }

        @Override
        public SpinParticleData build(){
            return new SpinParticleData(spinOffset, rsp1, rsp2, rso1, rso2, startingValue, middleValue, endingValue, rs1, rs2, rm1, rm2, re1, re2, coefficient, startToMiddleEasing, middleToEndEasing);
        }
    }
}
