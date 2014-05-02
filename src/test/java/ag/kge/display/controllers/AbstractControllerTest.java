package ag.kge.display.controllers;

import org.junit.*;

import java.util.Observable;

import static org.junit.Assert.*;

public class AbstractControllerTest {

    private AbstractController controller;

    @Before
    public void setUp() throws Exception {
        controller = new AbstractController() {
            @Override
            public String generateQuery() {
                return null;
            }

            @Override
            public Object filterData(Object data) {
                return null;
            }

            @Override
            public void update(Observable o, Object arg) {

            }
        };
    }

    @After
    public void tearDown() throws Exception{
        System.gc();
    }

    @Test
    public void generateAmend_testWithAtom() throws Exception {
        String binding = "value";
        assertEquals(controller.generateAmend(binding.split("\\.")),
                "value:");
    }

    @Test
    public void generateAmend_testWithIndex() throws Exception {
        String binding = "value.name.class";
        assertEquals(controller.generateAmend(binding.split("\\.")),
                ".[`value;raze `name`class;:;");

    }

}