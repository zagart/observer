package by.grodno.zagart.observer.webapp.dataaccess.impl;

import by.grodno.zagart.observer.webapp.dataaccess.AbstractHibernateDao;
import by.grodno.zagart.observer.webapp.entities.Stand;

/**
 * Наследник абстрактного класса слоя dataccess, отвечает за манипуляцию
 * данными типа Stand и их обмен с данными соответствующей таблицы
 * в базе данных.
 * Благодаря механизму рефлексии реализация методов будет взята из
 * абстрактного класса-родителя на основе указанных при наследовании
 * параметров.
 */
public class StandDaoImpl extends AbstractHibernateDao<Stand, Long> { }
