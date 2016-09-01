package by.grodno.zagart.observer.webapp.entities;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Random;

/**
 * Тесты класса Stand.
 */
@RunWith(JUnit4.class)
public class StandTest {

    private Stand testStand;
    private Long id = (new Random()).nextLong();
    private String number = RandomStringUtils.randomNumeric(4);
    private String description = RandomStringUtils.randomAlphabetic(20);
    private ArrayList<Module> moduleList = new ArrayList<>();
    private Module module = new Module();

    @Before
    public void setUp() {
        testStand = new Stand();
        testStand.setId(id);
        testStand.setNumber(number);
        testStand.setDescription(description);
        testStand.setModuleList(moduleList);
    }

    @Test
    public void getIdTest() {
        Assert.assertEquals(id, testStand.getId());
    }

    @Test
    public void getNumberTest() { Assert.assertEquals(number, testStand.getNumber()); }

    @Test
    public void getDescriptionTest() { Assert.assertEquals(description, testStand.getDescription()); }

    @Test
    public void getModuleListTest() {
        Assert.assertEquals(moduleList, testStand.getModuleList());
    }

    @Test
    public void addModuleTest() {
        testStand.addModule(module);
        Assert.assertTrue(testStand.getModuleList().contains(module));
    }

    @After
    public void cleanUp() {
        testStand = null;
        module = null;
    }

}
