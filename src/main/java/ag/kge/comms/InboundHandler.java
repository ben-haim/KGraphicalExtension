package ag.kge.comms;

import ag.kge.c;
import ag.kge.control.FrameCache;
import ag.kge.control.ModelCache;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by adnan on 25/04/14.
 */
public class InboundHandler implements Runnable{

    private final c conn;
    private final LinkedBlockingQueue<Object[]> showQueue;
    private final LinkedBlockingQueue<Object[]> updateQueue;

    public InboundHandler(c conn,
                          LinkedBlockingQueue<Object[]> showQueue,
                          LinkedBlockingQueue<Object[]> updateQueue) {
        this.conn = conn;
        this.showQueue = showQueue;
        this.updateQueue = updateQueue;
    }

    @Override
    public void run() {
        Object in;
        while (true) try{
            //not sure why it would every be null
            if (!c.qn(in = conn.k())){
                    readMessage(in);
            }
        } catch (c.KException|IOException e) {
                e.printStackTrace();
        }

    }

    private void readMessage(Object msg){
        final Object[] message = (Object[]) msg;

        String cmd = message[0].toString();

        switch (cmd){
            case "show": showQueue.add(
                    new Object[]{message[1], message[2]});
                break;
            case "hide": FrameCache.INSTANCE.hideFrame(
                    message[1].toString());
                break;
            case "update": updateQueue.add(
                    new Object[]{message[1], message[2], message[3]});
                break;
            case "kill": System.exit(0);
        }


    }
}
