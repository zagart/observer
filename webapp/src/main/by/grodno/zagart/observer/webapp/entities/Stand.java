package by.grodno.zagart.observer.webapp.entities;

import by.grodno.zagart.observer.webapp.interfaces.Identifiable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс-сущность, описывает объекты типа "информация о стенде" и их свойства.
 * Также предоставляет доступ к полям.
 */
@Entity
@Table(name = "STAND")
public class Stand implements Identifiable<Long> {

    private Long id;
    private String number;
    private ArrayList<Module> moduleList;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @Column(name = "STAND_NUMBER")
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    @OneToMany(mappedBy = "stand", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    public List<Module> getModuleList() { return moduleList; }
    public void setModuleList(ArrayList<Module> moduleList) { this.moduleList = moduleList; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Stand stand = (Stand) o;

        if (!id.equals(stand.id)) return false;
        return number != null ? number.equals(stand.number) : stand.number == null;

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (number != null ? number.hashCode() : 0);
        return result;
    }

    public void addModule(Module module) {
        module.setStand(this);
        this.moduleList.add(module);
    }

}