/**
 * @page constants.h
 * 
 * @brief Файл содержит константы, описывающие статус работы
 * контроллера и общие для всего проекта константы. В основном 
 * константы статуса предназначены для отправки данных
 * через последовательный порт.
 * 
 * @detailed Первая десятичная сотня значений (менее 0x64)
 * выделена под одиночные статусы, а оставшееся в байте значений
 * место будет выделено под более сложные пакеты данных.
 * 
 * @author Zagart
 */

#ifndef CONSTANTS_H
#define	CONSTANTS_H
#define _XTAL_FREQ 4000000
#define STAND_INIT  0x01
#endif

