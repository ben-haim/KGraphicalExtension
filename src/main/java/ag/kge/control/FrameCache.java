package ag.kge.control;

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
    }

    public synchronized void addFrame(String frameName, JFrame frame){
        cache.put(frameName,frame);
    }


}
