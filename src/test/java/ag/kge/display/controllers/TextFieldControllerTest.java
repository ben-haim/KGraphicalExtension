package ag.kge.display.controllers;

import ag.kge.display.KType;
import org.junit.*;

import javax.swing.table.DefaultTableModel;
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
    private static ArrayDeque<Object> atomUpdateStack;
    private static ArrayDeque<Object> indexedUpdateStack;

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
    public static void setUpAtomUpdateStack() throws Exception {

        atomUpdateStack.add("New Update");
        indexedUpdateStack.add(4);
        indexedUpdateStack.add("7");
    }

    @After
    public void tearDown() throws Exception {
        System.gc();
    }

    @AfterClass
    public void tearDownEverything() throws Exception {
        stringTemplate = null;
        charTemplate = null;
        numTemplate= null;
    }

    @Test
    public void filterData_valid() throws Exception {

        topController = new TextFieldController(null,null);

        assertEquals(topController.filterData("testString"), "testString");
        assertEquals(topController.filterData("testString".toCharArray()), "testString");
        assertEquals(topController.filterData(45), "45");
        assertEquals(topController.filterData(10.56), "10.56");
    }

    @Test
    public void filterData_invalid() throws Exception {

        topController = new TextFieldController(null,null);
        assertEquals(topController.filterData(new double[]{10.0, 45.2, 22.3}), "(...)");
        assertEquals(topController.filterData(new HashMap<String,Object>()),"(...)");
        assertEquals(topController.filterData(new DefaultTableModel()),"(...)");
    }

    @Test
    public void generateQuery_testIndexingAmend() throws Exception {

        HashMap<String,Object> stringTemplateCopy = new HashMap<>(stringTemplate);
        stringTemplateCopy.put("binding","value.val");
        topController = new TextFieldController(stringTemplateCopy,null);
        assertEquals(topController.generateQuery(),".[`value;`val;:;50];");
    }

    @Test
    public void generateQuery_testKeepsNumeric() throws Exception {

        topController = new TextFieldController(numTemplate, null);
        topController.getTextField().setText("45");
        topController.setCurrentType(KType.NUMERIC);
        assertEquals(topController.generateQuery(), "value:45;");
    }

    @Test
    public void generateQuery_testKeepsCharArray() throws Exception {

        topController = new TextFieldController(charTemplate, null);
        topController.getTextField().setText("New Char Array");
        topController.setCurrentType(KType.C_ARRAY);
        assertEquals(topController.generateQuery(),"value:\"Test Char Array\";");

    }

    @Test
    public void generateQuery_testCurrentTypeNumToString() throws Exception {

        topController = new TextFieldController(numTemplate,null);
        topController.getTextField().setText("Some Text");
        topController.setCurrentType(KType.STRING);
        assertEquals(topController.generateQuery(),"value:`$\"Some Text\";");
    }

    @Test
    public void generateQuery_testCurrentTypeNumToCharArray() throws Exception {

        topController = new TextFieldController(numTemplate,null);
        topController.getTextField().setText("Some Char Array");
        topController.setCurrentType(KType.C_ARRAY);
        assertEquals(topController.generateQuery(),"value:\"Some Char Array\";");
    }

    @Test
    public void update_testAtomStackChangesType() throws Exception {

        topController = new TextFieldController(numTemplate,null);
        topController.update(null,atomUpdateStack);
        assertEquals(topController.getCurrentType(), KType.STRING);
    }

    @Test
    public void update_testIndexedStack() throws Exception {
        topController = new TextFieldController(charTemplate,null);
        topController.update(null,indexedUpdateStack);
        assertEquals(topController.getTextField().getText(), "Test7Char Array");
    }
}
