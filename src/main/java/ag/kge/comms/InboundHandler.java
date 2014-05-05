package ag.kge.comms;

import ag.kge.c;
import ag.kge.control.*;
import ag.kge.display.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;
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
            //not sure why it would ever be null
            if (!c.qn(in = conn.k())){
                    readMessage(in);
            }
        } catch (c.KException|IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    private void readMessage(Object msg){

        if (!msg.getClass().isArray()){
            System.out.println(msg.toString());
            return;
        }


        Object[] message = (Object[]) msg;
        String cmd = message[0].toString();

        switch (cmd){
            case "show":
                showQueue.add(new Object[]{message[1], message[2]});
                break;
            case "hide":
                FrameCache.INSTANCE.hideFrame(message[1].toString());
                break;
            case "update":
                updateQueue.add(new Object[]{message[1], message[2], message[3]});
                break;
            case "kill":
                System.exit(0);
        }


    }

    /**
     * Main method, opens connection and starts threads
     *
     * @param args
     */
    public static void main(String[] args) {
        int port = 0;
        if (args.length == 1) try{
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e){
            System.err.println("Error: Invalid Port Given");
            System.exit(1);
        } else {
            System.err.println("Error: Invalid Arguments Given");
            System.exit(1);
        }

        c conn = null;

        try {
            //connect to server and run init function to set up handle
            conn = new c("localhost",port);
            conn.ks("gInit[]");
            System.out.println("Connected to port: " + port);
        } catch (IOException| c.KException e) {
            System.err.println("Error: Couldn't Connect To Server");
            System.exit(1);
        }


        final LinkedBlockingQueue<String> outQueue = new LinkedBlockingQueue<>();
        final LinkedBlockingQueue<Object[]> showQueue = new LinkedBlockingQueue<>();
        final LinkedBlockingQueue<Object[]> updateQueue = new LinkedBlockingQueue<>();
        final LinkedBlockingQueue<TreeMap> templateQueue = new LinkedBlockingQueue<>();


        //communication layer threads
        new Thread(new InboundHandler(conn,showQueue, updateQueue)).start();
        new Thread(new OutboundHandler(conn,outQueue)).start();

        //control layer threads
        new Thread(new UpdateHandler(updateQueue)).start();
        new Thread(new ShowHandler(showQueue,templateQueue)).start();

        //display later thread
        new Thread(new RenderingEngine(outQueue,templateQueue)).start();
    }
}
