package ag.kge.display;


import javax.swing.*;
import java.util.concurrent.ConcurrentHashMap;

public enum FrameCache {
    /**
     * The enum representing the single reference point
     */
    INSTANCE;

    /**
     * The map containing a mapping of the Frames against their names
     */
    private final ConcurrentHashMap<String, JFrame> cache = new ConcurrentHashMap<>();

    /**
     * Hides a frame and removes it from the cache
     * @param frameName
     */
    public synchronized void hideFrame(String frameName) {
        if (cache.containsKey(frameName))
            cache.remove(frameName).setVisible(false);
        else System.out.println("Frame doesn't exist.");
    }

    /**
     * Adds a frame to the cache and displays it. If a frame of the same name exists,
     * remove it and replace it with the new one
     *
     * @param frameName
     * @param frame
     */
    public synchronized void addFrame(String frameName, JFrame frame){
        //if the frame already exists, destroy it
        if (cache.containsKey(frameName))
            cache.remove(frameName).setVisible(false);

        //add the new frame
        cache.put(frameName,frame);
        cache.get(frameName).setVisible(true);
        System.out.println(frameName  + " added.");
    }

    /**
     * Repacks all the frames in the cache. This is necessary due to the nature of sending
     * an initial update request to populate the widgets, otherwise they will stay in their
     * shrunken sizes as if they have no data
     */
    public void refreshFrames() {
        for (JFrame x: cache.values())
            x.pack();
    }
}
