package ag.kge.display.controllers;

import ag.kge.display.KType;

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

    public TextFieldController(HashMap<String, Object> template, final LinkedBlockingQueue<String> outQueue) {
        super(template, outQueue);

        /*
        At this point the data should also be in the infoDict so we can initialise
        the component with a get() call
        */
        Object data = template.get("data");
        binding = template.get("binding").toString();
        currentType = KType.getTypeOf(data);
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
    }

    public JTextField getTextField() {
        return textField;
    }

    public void setCurrentType(KType currentType) {
        this.currentType = currentType;
    }

    public KType getCurrentType() {
        return currentType;
    }

    @Override
    public String generateQuery() {
        //variables names are stored using namespace indexing
        String t = textField.getText();
        String[] n = binding.split("\\.");
        String m;

        //set up amend into variable
        if (n.length == 1) m = n[0] + ":"; //atom
        else {
            //list, use dot indexing, raze names
            m = ".["+ n[0] + ";raze" ;

            for (int i = 1; i < n.length; i++)
                m += "`" + n[i];

            m += ";:;"; //add amend operator
        }

        /*
        only time numeric data is reflected on server is when both current type and
        text field text are numeric
        */
        if (currentType == KType.NUMERIC && KType.isNumeric(t))
            m += t;
        else {
            m += "\"" + t + "\""; //set it up as a char array

            //cast to symbol if it's not a char array
            if (currentType != KType.C_ARRAY)
                m = "`$" + m;
        }

        if (n.length > 1)
            m += "];"; //close dot indexing
        else m+=";"; //otherwise just close statement

        return m;
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
            if (!(data instanceof Character) ||
                    !(head instanceof Integer) ||
                    !(currentType != KType.C_ARRAY))
                return;

            String current  = textField.getText();
            int index = (int) head;
            char charData  = (char) data;

            //replaces character, will need testing, not sure this actually works
            String newText =
                    current.substring(0,index-1) +
                    charData + current.substring(index, current.length());

            textField.setText(newText);

        } else { //the head is the complete data
            currentType = KType.getTypeOf(head);
            textField.setText(filterData(head));
        }

    }

}
