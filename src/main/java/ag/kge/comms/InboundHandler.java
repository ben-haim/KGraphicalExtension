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

    public InboundHandler(c conn, LinkedBlockingQueue<Object[]> showQueue) {
        this.conn = conn;
        this.showQueue = showQueue;
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

            case "show":
                String name = message[1].toString();
                //put description to
                Object description= ModelCache.INSTANCE.parseData(message[2]);
                showQueue.add(new Object[]{name, description});
                break;
            case "hide":
                FrameCache.INSTANCE.hideFrame(message[0].toString());
                break;
            case "update":
                ModelCache.INSTANCE.updateModel(
                        message[1], message[2], message[3]
                );

                break;
            case "kill": System.exit(0);
        }


    }
}
