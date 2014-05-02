package ag.kge;

import ag.kge.comms.InboundHandler;
import ag.kge.comms.OutboundHandler;
import ag.kge.control.ShowHandler;
import ag.kge.control.UpdateHandler;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by adnan on 25/04/14.
 */
public class KGE {

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
        } catch (IOException| c.KException e) {
            System.err.println("Error: Couldn't Connect To Server");
            System.exit(1);
        }

        final LinkedBlockingQueue<String> outQueue = new LinkedBlockingQueue<>();
        final LinkedBlockingQueue<Object[]> showQueue = new LinkedBlockingQueue<>();
        final LinkedBlockingQueue<Object[]> updateQueue = new LinkedBlockingQueue<>();

        //communication layer threads
        new Thread(new InboundHandler(conn,showQueue, updateQueue)).start();
        new Thread(new OutboundHandler(conn,outQueue)).start();

        //control layer threads
        new Thread(new UpdateHandler(updateQueue)).start();
        new Thread(new ShowHandler(showQueue, outQueue)).start();

    }
}
