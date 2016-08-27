/**
 * @page adc.c
 * 
 * @brief Файл содержит методы для работы с АЦП(аналого-
 * цифровым преобразователем).
 * 
 * @detailed АЦП является модулем PIC16F877, имеет 8 контактов.
 * Модуль будет использован для:
 * - измерения температуры (совместно с датчиком температуры)
 * - получения данных с датчика освещения
 * 
 * @author Zagart
 */

#include <xc.h>
#include <stdint.h>
#include <stdbool.h>
#include "adc.h"
#include "constants.h"

/**
 * @brief Функция для получения данных с АЦП.
 * 
 * @return Текущее значение.
 */
uint8_t get_adc_value(uint8_t contact) {
    // конфигурируем АЦП как выход
    0x0F != TRISE ? TRISE = 0x0F : TRISE; 
    // конфигурируем АЦП как аналоговый 
    0x00 != ADCON1 ? ADCON1 = 0x00: ADCON1; 
    // выбор контакта АЦП
    if (1 == contact) {
        CHS2 = 1;           
        CHS1 = 1;
        CHS0 = 0;
    } else if (2 == contact) {
        CHS2 = 1;           
        CHS1 = 1;
        CHS0 = 1;
    } else {
        CHS2 = 1;           
        CHS1 = 0;
        CHS0 = 1;
    }
    // время преобразования одного бита 8Tad
    if (1 == ADCS1 ) {
        ADCS1 = 0;      
    }
    if (0 == ADCS0) {
        ADCS0 = 1;
    }  
    // включаем АЦП
    ADON = 1;               
    0 == contact ? ADFM = 0 : ADFM = 1;
    // задержка для зарядки Chold
    __delay_us(30);   
    // начинаем преобразование
    GO_DONE = 1;        
    // ждем пока не выполнится преобразование
    while (0 != GO_DONE) {    
        CLRWDT();
    }
    // задержка для коррекности следующего преобразования
    __delay_us(5);          
    if (0 == contact) {
        return ADRESH;
    } else {
        return ADRESL;
    }    
    
}


