package by.grodno.zagart.observer.webapp.entities;

import javax.persistence.*;
import java.util.Date;

/**
 * Класс описывает объекты типа "модуль" и их свойства.
 * Также предоставляет доступ к полям.
 */
@Entity
@Table(name = "MODULE")
public class Module {

    private String name;
    private String statusInfo;
    private Date statusChangeDate;
    private Stand stand;

    @Column(name = "NAME")
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Column(name = "STATUS")
    public String getStatusInfo() { return statusInfo; }
    public void setStatusInfo(String statusInfo) { this.statusInfo = statusInfo; }

    @Temporal(TemporalType.DATE)
    @Column(name = "STATUS_DATE")
    public Date getStatusChangeDate() { return statusChangeDate; }
    public void setStatusChangeDate(Date statusChangeDate) { this.statusChangeDate = statusChangeDate; }

    @ManyToOne
    @JoinColumn(name = "STAND_ID")
    public Stand getStand() { return stand; }
    public void setStand(Stand stand) { this.stand = stand; }

}
