package com.diandian.coolco.emilie.utility;

/**
 * Event for EventBus, Event classes are tiny, group them.
 */
public class Event {
    public static class SwipeRightEvent{}
    public static class NoExternalStorageException extends Exception{}
    public static class ScanImgCompleted{}
}
