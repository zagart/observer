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
//константы общего типа
#define _XTAL_FREQ 4000000
#define ON 1
#define OFF 0
#define TRUE 1
#define FALSE 0
#define INPUT 1
#define OUTPUT 0
#define STATUS 0
#define VALUE 1
//константы статуса
#define STAND_INIT  0x01
#define OERR 201
#define FERR 202
#define NO_MSG 203
#define SYSTEM_EXIT 204
#endif

