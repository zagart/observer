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
//значение, на которое различаются элементы АЦП
#define ADC_VALUE_STEP 13
//допустимая погрешность значений АЦП
#define ADC_ERROR_VALUE 0x01
//предельное значение АЦП
#define LCD_ROWS_MAX 2
#define LCD_COLUMNS_MAX 16
#define LIGHT_SENSOR_PIN 0
#define LIGHT_SENSOR_INIT_VALUE 0x00
#define LIGHT_SENSOR_STEP_VALUE 0x01
#define LIGHT_SENSOR_LIBRARY_MAX 100
#define TEMP_SENSOR_PIN 1
#define TEMP_SENSOR_INIT_VALUE 0x21
#define TEMP_SENSOR_STEP_VALUE 0x02
#define TEMP_SENSOR_LIBRARY_MAX 103

//константы статуса
#define STAND_INIT  0x01
#endif

