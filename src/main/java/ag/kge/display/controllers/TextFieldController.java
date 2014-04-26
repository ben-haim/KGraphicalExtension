package ag.kge.display.controllers;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by adnan on 25/04/14.
 */
public class TextFieldController extends AbstractController {

    private final JTextField textField;
    private KType currentType;

    public TextFieldController(HashMap<String, Object> infoDict, LinkedBlockingQueue<String> outQueue) {
        super(infoDict, outQueue);

        /*
        At this point the data should also be in the infoDict so we can initialise
        the component with a get() call
        */
        Object data = infoDict.get("data");
        currentType = KType.getTypeOf(data);
        textField = new JTextField(filterData(infoDict.get("data")));
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateServer();
            }
        });
        setName(infoDict.get("name").toString());
        setBorder(new TitledBorder(infoDict.get("label").toString()));
    }

    @Override
    public void updateServer() {
        //variables names are stored using namespace indexing
        String t = textField.getText();
        String[] n = getName().split("\\.");
        String m;

        //set up assignment into variable

        if (n.length == 1){
            //atom
            m = n[0] + ":";
        } else {
            //is list, use dot indexing
            //raze over
            m = ".["+ n[0] + ";,/" ;

            for (int i = 1; i < n.length; i++){
                m+= "`" + n[i];
            }
        }

        m += ";:;";

        if (currentType == KType.NUMERIC && KType.isNumeric(t)){
            //only time numeric data is reflected on server is when both current type and
            //text field text are numeric
            m += t;
        } else {
            m += "\"" + t + "\""; //set it up as a char array
            if (currentType != KType.C_ARRAY) {
                //set to symbol if it's not a char array
                m = "`$" + m;
            }
        }

        m += "];";

        //put the resulting string on to the outbound queue
        outQueue.add(m);
    }

    @Override
    public String filterData(Object data) {
        KType type = KType.getTypeOf(data);
        if (type == KType.C_ARRAY)
            return new String((char[]) data);
        else if (type == KType.STRING ||
                type == KType.NUMERIC ||
                type == KType.ATOM
                )
            return data.toString();
        else return "(...)";
    }

    @Override
    public void update(Observable o, Object arg) {
        ArrayDeque<Object> stack = (ArrayDeque) arg;

        //pop off the head of the stack
        Object head = stack.pop();

        //if the stack isn't empty, the head is an index
        if (!stack.isEmpty()){
            Object data = stack.pop();
                //if the data isn't a single character, or the index isn't an int, leave
            if (!(data instanceof Character) || !(head instanceof Integer))
                return;

            String current  = textField.getText();
            int index = (int) head;
            char charData  = (char) data;

            //replaces character, will need testing, not sure this actually works
            String newText =
                    current.substring(0,index-1) +
                    data + current.substring(index, current.length());

            //the fact that there is an index means the variable is a char array
            currentType = KType.C_ARRAY;

            textField.setText(newText);

        } else { //the head is the complete data
            currentType = KType.getTypeOf(head);
            textField.setText(filterData(head));
        }

    }

}
