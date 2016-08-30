/**
 * @page uart.h
 * 
 * @brief Файл с прототипами функций основного класса.
 * Необходим компилятору для сборки проекта.
 * 
 * @ref uart.c
 * 
 * @author Zagart
 */

#ifndef UART_H
#define UART_H
//константы файла USART-модуля
#define USART_SPD_9600 25
void usart_init();
void send_byte_via_serial(uint8_t value);
uint8_t read_byte_from_serial();
void send_message_via_serial(uint8_t stand_number,                         
                             uint8_t module,
                             uint8_t event,
                             uint8_t value);
#endif