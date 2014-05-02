package ag.kge.display.controllers;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Observable;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Adnan on 02/05/2014.
 */
public class NumFieldController extends AbstractController {


    private final JTextField textField;

    public NumFieldController(HashMap<String, Object> template,
                              final LinkedBlockingQueue<String> outQueue) {


        /*
        At this point the data should also be in the template so we can initialise
        the component with a get() call
        */

        binding = template.get("binding").toString();
        textField = new JTextField(filterData(template.get("data")));
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                outQueue.add(generateQuery());
            }
        });
        setName(template.get("name").toString());
        //label's should be a char array
        setBorder(new TitledBorder(new String((char[]) template.get("label"))));
        add(textField);

    }

    /**
     * Will generate amend for whatever is in the text field, sending as a symbol if non numeric
     * data. The resulting update should trigger .z.vs, which will send an inbound update
     *
     * @return generated query String
     */
    @Override
    public String generateQuery() {

        String n[] = binding.split("\\.");
        String m = generateAmend(n);

        if (isNumeric(textField.getText())){
            m += textField.getText();
        } else {
            //not numeric, send as symbol
            m+="`$\"" + textField.getText() +"\"";
        }

        if (n.length > 1)
            m += "];"; //close dot indexing
        else m+=";"; //otherwise just close statement


        return m;
    }

    @Override
    public String filterData(Object data) {
        if (isNumeric(data)){
            return data.toString();
        } else  {
            return "ERROR: NOT A NUMBER";
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        ArrayDeque<Object> stack = (ArrayDeque) arg;

        //get head of the stack
        Object head = stack.pop();

        //if the stack is not empty, the head was an index, not suited to this type,
        //meaning variable type has changed
        if (stack.isEmpty()){
            textField.setText("INVALID: NOT AN ATOM");
        } else {
            textField.setText(filterData(head));
        }
    }
}
