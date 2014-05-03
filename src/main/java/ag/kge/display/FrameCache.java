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
        cache.put(frameName,frame);
        cache.get(frameName).setVisible(true);
        System.out.println(frameName  + " added.");
    }

}
