package ag.kge.display.controllers;

import org.junit.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by adnan on 26/04/14.
 */
public class TextFieldControllerTest {

    private TextFieldController topController;
    private static HashMap<String,Object> numTemplate;
    private static HashMap<String,Object> stringTemplate;
    private static HashMap<String,Object> charTemplate;
    private static ArrayDeque<Object> atomUpdateStack = new ArrayDeque<>();
    private static ArrayDeque<Object> indexedUpdateStack = new ArrayDeque<>();
    private static ArrayDeque<Object> charArrayUpdateStack = new ArrayDeque<>();

    @BeforeClass
    public static void setUpNumberTemplate() throws Exception {

        numTemplate = new HashMap<>();
        numTemplate.put("name", "numTextField");
        numTemplate.put("label", "Number Text Field".toCharArray());
        numTemplate.put("binding", "value");
        numTemplate.put("data", 50);
    }

    @BeforeClass
    public static void setUpStringTemplate() throws Exception {

        stringTemplate = new HashMap<>();
        stringTemplate.put("name", "stringTextField");
        stringTemplate.put("label", "String Text Field".toCharArray());
        stringTemplate.put("binding", "value");
        stringTemplate.put("data", "Test String");
    }

    @BeforeClass
    public static void setUpCharTemplate() throws Exception{

        charTemplate = new HashMap<>();
        charTemplate.put("name","charArrayTextField");
        charTemplate.put("label","Char Array Text Field".toCharArray());
        charTemplate.put("binding","value");
        charTemplate.put("data", "Test Char Array".toCharArray());
    }

    @BeforeClass
    public static void setUpUpdateStacks() throws Exception {

        atomUpdateStack.add("New Update");
        charArrayUpdateStack.add("CharArrayUpdate".toCharArray());
        indexedUpdateStack.add(new int[]{4,8});
        indexedUpdateStack.add(new char[]{'K', 'L'});
    }

    @After
    public void tearDown() throws Exception {
        System.gc();
    }

    @AfterClass
    public static void tearDownEverything() throws Exception {
        stringTemplate = null;
        charTemplate = null;
        numTemplate= null;
    }

    @Test
    public void filterData_valid() throws Exception {

        //tests with strings, char[]'s and numerics
        topController = new TextFieldController(stringTemplate,null);

        assertEquals(topController.filterData("testString"), "testString");
        assertEquals(topController.filterData("testString".toCharArray()), "testString");
        assertEquals(topController.filterData(45), "45");
        assertEquals(topController.filterData(10.56), "10.56");
    }

    @Test
    public void filterData_invalid() throws Exception {

        topController = new TextFieldController(stringTemplate,null);
        assertEquals(topController.filterData(new double[]{10.0, 45.2, 22.3}), "(...)");
        assertEquals(topController.filterData(new HashMap<String,Object>()),"(...)");
        assertEquals(topController.filterData(new DefaultTableModel()),"(...)");
    }



    @Test
    public void replaceCharAt_testWithInsideChar() throws Exception {
        topController = new TextFieldController(stringTemplate,null);
        char insert = 'w';
        int index = 3;
        String current = "test string";

        Method replaceCharAt = TextFieldController.class.getDeclaredMethod("replaceCharAt",
                String.class, int.class, char.class);
        replaceCharAt.setAccessible(true);
        String result = "";
        try {
            result = (String) replaceCharAt.invoke(topController, current,index,insert);
        }catch (Exception e){
            e.printStackTrace();
        }

        assertEquals(result, "tewt string");

    }

    @Test
    public void replaceCharAt_testWithAppend() throws Exception {
        topController = new TextFieldController(stringTemplate,null);
        char insert = 'w';
        int index = 5;
        String current = "test";

        Method replaceCharAt = TextFieldController.class.getDeclaredMethod("replaceCharAt",
                String.class, int.class, char.class);
        replaceCharAt.setAccessible(true);
        String result = (String) replaceCharAt.invoke(topController, current,index,insert);

        assertEquals(result, "testw");

    }

    @Test
    public void generateQuery_testKeepsCharArray() throws Exception {
        topController = new TextFieldController(charTemplate, null);

        Field textField = TextFieldController.class.getDeclaredField("textField");
        textField.setAccessible(true);
        ((JTextField)textField.get(topController)).setText("New Char Array");

        assertEquals(topController.generateQuery(), "value:\"New Char Array\";");
    }

    @Test
    public void generateQuery_testKeepsCharArrayWithIndexing() throws Exception {
        charTemplate.put("binding", "value.name");
        topController = new TextFieldController(charTemplate, null);

        Field textField = TextFieldController.class.getDeclaredField("textField");
        textField.setAccessible(true);
        ((JTextField)textField.get(topController)).setText("New Char Array");

        assertEquals(topController.generateQuery(),".[`value;raze `name;:;\"New Char Array\"];");
    }

    @Test
    public void update_testStringUpdateStack() throws Exception {

        topController = new TextFieldController(stringTemplate,null);
        topController.update(null,atomUpdateStack);

        Field textField = TextFieldController.class.getDeclaredField("textField");
        textField.setAccessible(true);

        assertEquals(((JTextField)textField.get(topController)).getText(),
                "New Update");

    }

    @Test
    public void update_testIsCharArraySet() throws Exception {

        topController = new TextFieldController(stringTemplate,null);
        topController.update(null,charArrayUpdateStack);

        Field isCharArray = TextFieldController.class.getDeclaredField("isCharArray");
        isCharArray.setAccessible(true);
        assertEquals(isCharArray.get(topController), true);
    }

    @Test
    public void update_testMultiIndexStack() throws Exception{
        topController = new TextFieldController(charTemplate,null);
        topController.update(null,indexedUpdateStack);

        Field textField = TextFieldController.class.getDeclaredField("textField");
        textField.setAccessible(true);

        assertEquals(((JTextField)textField.get(topController)).getText(),
                "TesK ChLr Array");
    }
}
