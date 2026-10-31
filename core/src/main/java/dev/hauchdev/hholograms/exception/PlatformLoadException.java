package dev.hauchdev.hholograms.exception;

public class PlatformLoadException extends RuntimeException {
    public PlatformLoadException(String message) { super(message); }
    public PlatformLoadException(String message, Throwable cause) { super(message, cause); }
}