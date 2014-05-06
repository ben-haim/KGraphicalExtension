package ag.kge.display;


import javax.swing.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by adnan on 25/04/14.
 */
public enum FrameCache {

    INSTANCE;

    private final ConcurrentHashMap<String, JFrame> cache = new ConcurrentHashMap<>();

    public synchronized void hideFrame(String frameName) {
        if (cache.containsKey(frameName))
            cache.remove(frameName).setVisible(false);
        else
            System.out.println("Frame doesn't exist.");

    }

    public synchronized void addFrame(String frameName, JFrame frame){
        if (cache.containsKey(frameName))
            cache.remove(frameName).setVisible(false);
        cache.put(frameName,frame);
        cache.get(frameName).setVisible(true);
        System.out.println(frameName  + " added.");
    }

    public synchronized boolean checkFrameExists(String name){
        if (cache.containsKey(name)) return true;
        return false;
    }

    /**
     * Repacks all the frames in the cache. This is necessary due to the nature of sending an initial
     * update request to populate the widgets, otherwise they will stay in their shrunken sizes as if they
     * have no data
     *
     */
    public void refreshFrames() {
        for (JFrame x: cache.values()){
            x.pack();
        }
    }
}
