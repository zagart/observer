package by.grodno.zagart.observer.localapp.classes;

import by.grodno.zagart.observer.webapp.entities.Module;
import by.grodno.zagart.observer.webapp.entities.Stand;

import java.io.Serializable;

/**
 * Класс инкапсулирует данные, передаваемые между единицами
 * проекта Observer посредством сетевых соединений.
 */
public class ObserverNetworkPackage implements Serializable {

    private static final long serialVersionUID = 3L;

    private Module module;
    private Stand stand;

    public ObserverNetworkPackage(Module module, Stand stand) {
        this.module = module;
        this.stand = stand;
    }

    public Module getModule() {
        return module;
    }

    public Stand getStand() {
        return stand;
    }

}
