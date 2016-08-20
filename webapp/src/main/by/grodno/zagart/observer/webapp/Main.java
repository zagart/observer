package by.grodno.zagart.observer.webapp;

import by.grodno.zagart.observer.webapp.entities.Module;
import by.grodno.zagart.observer.webapp.entities.Stand;
import by.grodno.zagart.observer.webapp.services.impl.ModuleServiceImpl;
import by.grodno.zagart.observer.webapp.services.impl.StandServiceImpl;
import by.grodno.zagart.observer.webapp.utils.HibernateUtil;

import static by.grodno.zagart.observer.webapp.utils.DataUtil.getNewModule;
import static by.grodno.zagart.observer.webapp.utils.DataUtil.getNewStand;

public class Main {

    private static StandServiceImpl standService = new StandServiceImpl();
    private static ModuleServiceImpl moduleService = new ModuleServiceImpl();

    public static void main(String...args) {
        Module module = getNewModule();
        moduleService.save(module);
        Stand stand = getNewStand();
        standService.save(stand);
        stand.addModule(module);
        standService.update(stand);
        moduleService.update(module);
        HibernateUtil.closeFactory();
    }

}