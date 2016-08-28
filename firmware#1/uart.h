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
void send_byte_via_serial(uint8_t type, uint8_t value);
#endif