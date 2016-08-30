/**
 * @page constants.h
 * 
 * @brief Файл содержит константы, описывающие статус работы
 * контроллера и общие для всего проекта константы. В основном 
 * константы статуса предназначены для отправки данных
 * через последовательный порт.
 * 
 * @detailed На данный момент из константы выделены основные
 * типы:
 * - константа общего типа
 * - константа события
 * - константа наименования модуля
 * - прочие константы
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
//константы пакета данных, передаваемого через последовательный порт
#define STAND_NUMBER 1
//события
#define INIT  2
#define TEMP_CHANGE 3
#define LIGHT_CHANGE 4
#define LCD_NEW_OUTPUT 5
//наименование модуля
#define STAND_MC 10
#define LIGHT_SENSOR 11
#define TEMP_SENSOR 12
#define LCD_DISPLAY 13
//прочие константы
#define NULL 200
#define OERR 201
#define FERR 202
#define NO_MSG 203
#define SYSTEM_EXIT 204
#define MSG_HEAD 205
#define MSG_TAIL 206
#endif

