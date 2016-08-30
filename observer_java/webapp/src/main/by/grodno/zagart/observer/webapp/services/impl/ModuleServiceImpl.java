package by.grodno.zagart.observer.webapp.services.impl;

import by.grodno.zagart.observer.webapp.dao.impl.ModuleDaoImpl;
import by.grodno.zagart.observer.webapp.entities.Module;
import by.grodno.zagart.observer.webapp.services.AbstractHibernateService;

/**
 * Наследник абстрактного класса service, отвечает за использование
 * классов уровня DAO типа Module, управляет сессиями и транзакциями Hibernate
 * и выполняет логирование.
 * Благодаря механизму рефлексии реализация методов будет взята из
 * абстрактного класса-родителя на основе указанных при наследовании
 * параметров.
 */
public class ModuleServiceImpl extends AbstractHibernateService<Module, Long, ModuleDaoImpl> { }
