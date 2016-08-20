package by.grodno.zagart.observer.webapp.entities;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Date;

/**
 * Тесты класса Module.
 */
@RunWith(JUnit4.class)
public class ModuleTest {

    private Module testModule;
    private String name = RandomStringUtils.randomAlphabetic(10);
    private String statusInfo = RandomStringUtils.randomAlphabetic(20);
    private Date statusChangeDate = new Date();
    private Stand stand = new Stand();

    @Before
    public void setUp() {
        testModule = new Module();
        testModule.setName(name);
        testModule.setStatusInfo(statusInfo);
        testModule.setStatusChangeDate(statusChangeDate);
        testModule.setStand(stand);
    }

    @Test
    public void getNameTest() {
        Assert.assertEquals(name, testModule.getName());
    }

    @Test
    public void getStatusInfoTest() {
        Assert.assertEquals(statusInfo, testModule.getStatusInfo());
    }

    @Test
    public void getStatusChangeDateTest() {
        Assert.assertEquals(statusChangeDate, testModule.getStatusChangeDate());
    }

    @Test
    public void getStandTest() {
        Assert.assertEquals(stand, testModule.getStand());
    }

    @After
    public void cleanUp() {
        testModule = null;
        stand = null;
    }

}
