/**
 * @page uart.c
 * 
 * @brief Файл содержит методы для выполнения операций
 * с универсальным синхронно-асинхронным приемо-передатчиком
 * (USART).
 * 
 * @detailed Несмотря на то, что модуль синхронно-асинхронный,
 * на практике будет использован только асинхронный способ
 * передачи данных, потому аббревиатуру USART можно сократить
 * до UART. 
 * При записи данных используется регистр TXREG, конфигурация
 * и включение модуля осуществляются битами регистров
 * TXSTA и SPBRG.
 * 
 * @author Zagart
 */

#include <xc.h>
#include <stdint.h>
#include "constants.h"
#include "uart.h"

/**
 * @brief Инициализация UART-модуля в качестве передатчика.
 */
void usart_init() {
    //конфигурирование контактов модуля как входов(требование производителя))
    RC7 = INPUT;
    RC6 = INPUT;
    //установка бита для уменьшения погрешности скорости передачи
    BRGH = ON;
    //устанавливаем скорость передачи 9600 бит/сек
    SPBRG = USART_SPD_9600;
    //устанавливаем асинхронный режим передачи данных
    SYNC = OFF;
    //устанавливаем количество передаваемых информационных бит - 8
    TX9 = 0;
    //включаем модуль
    SPEN = ON;    
}

/**
 * @brief Метод для отправки байта через последовательный
 * порт.
 *
 * @param type Тип отправляемого байта.
 * @param value Байт для отправки.
 */
void send_byte_via_serial(uint8_t value) {
    //инициализируем USART-модуль
    usart_init();
    //разрешаем передачу
    TXEN = ON;
    //записываем байт
    TXREG = value;
    //ждем установки бита завершения передачи
    while(TRMT == OFF){
        CLRWDT();
    };
    //выключаем передатчик
    TXEN = OFF; 
}

/**
 * @brief Метод для приема байта с последовательного
 * порта.
 * 
 * @return Принятый байт.
 */
uint8_t read_byte_from_serial() {
    uint8_t msg = SYSTEM_EXIT;
    //инициализируем USART-модуль
    usart_init();
    //разрешаем прием
    CREN = ON;
    //читаем биты, пока не будут прочитаны все
    while (RCIF == OFF) {
        CLRWDT();
        //обработка фатальной ошибки
        if (OERR == TRUE) {
            //запрещаем прием
            CREN = OFF;
            return OERR;
        } 
        //обработка ошибки отсутствия стопового бита
        if (FERR == TRUE) {  
            return FERR;
        }
        msg = RCREG;
        //запрещаем прием
        CREN = OFF;
        return msg;
    }
    return msg;
}

/**
 * @brief Метод отправляет на COM-порт последовательность
 * байтов в порядке и виде, понятном для получателя (localapp).
 * 
 * @param stand_number Номер стенда, инициализировавшего передачу.
 * @param module Наименование модуля, вызвавшего событие.
 * @param event Тип события.
 * @param value Значение события (NULL константа, если такового нет).
 */
void send_message_via_serial(uint8_t stand_number,                         
                             uint8_t module,
                             uint8_t event,
                             uint8_t value) {
    send_byte_via_serial(MSG_START);
    send_byte_via_serial(stand_number);
    send_byte_via_serial(module);
    send_byte_via_serial(event);
    send_byte_via_serial(value);
    send_byte_via_serial(MSG_END);
}

