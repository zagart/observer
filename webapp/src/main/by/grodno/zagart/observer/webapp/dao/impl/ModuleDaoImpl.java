package by.grodno.zagart.observer.webapp.dao.impl;

import by.grodno.zagart.observer.webapp.dao.AbstractHibernateDao;
import by.grodno.zagart.observer.webapp.entities.Module;

/**
 * Наследник абстрактного класса DAO, отвечает за манипуляцию
 * данными типа Module и их обмен с данными соответствующей таблицы
 * в базе данных.
 * Благодаря механизму рефлексии реализация методов будет взята из
 * абстрактного класса-родителя на основе указанных при наследовании
 * параметров.
 */
public class ModuleDaoImpl extends AbstractHibernateDao<Module, Long> { }
