/**
 * @page adc.h
 * 
 * @brief Файл с прототипами функций основного класса.
 * Необходим компилятору для сборки проекта.
 * 
 * @ref adc.c
 * 
 * @author Zagart
 */
#ifndef ADC_H
#define ADC_H
//значение, на которое различаются деления АЦП
#define ADC_VALUE_STEP 13
//допустимая погрешность значений АЦП
#define ADC_ERROR_VALUE 0x01
//константы датчика температуры
#define TEMP_SENSOR_PIN 1
#define TEMP_SENSOR_INIT_VALUE 0x21
#define TEMP_SENSOR_STEP_VALUE 0x02
#define TEMP_SENSOR_LIBRARY_MAX 103
//константы датчика освещенности
#define LIGHT_SENSOR_PIN 0
#define LIGHT_SENSOR_INIT_VALUE 0x00
#define LIGHT_SENSOR_STEP_VALUE 0x01
#define LIGHT_SENSOR_LIBRARY_MAX 100
uint8_t get_adc_value(uint8_t contact);
#endif