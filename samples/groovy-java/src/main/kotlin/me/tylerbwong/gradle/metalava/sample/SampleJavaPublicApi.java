package me.tylerbwong.gradle.metalava.sample;

public abstract class SampleJavaPublicApi {
    public abstract String publicStringProperty;
    public abstract void publicFunction();

    public static int publicStaticIntFunction(int value) {
        return value;
    }
}