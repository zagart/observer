/**
 * @page hitachi_hd44780.c
 * 
 * @brief Файл содержит методы для работы с LCD-дисплеем
 * Hitachi HD44780.
 * 
 * @detailed Функции метода предназначены не только для
 * отображения символов, но и для создания анимации на
 * на основе данных, полученных с датчика освещения и
 * датчика температуры (при помощи АЦП). 
 * В коде встречается термин "библиотека" - касательно АЦП,
 * это диапазон генерируемых им значений.
 * 
 * @ref adc.h
 * 
 * @author Zagart
 */

#include <xc.h>
#include <stdint.h>
#include <stdbool.h>
#include "adc.h"
#include "uart.h"
#include "constants.h"
#include "hitachi_hd44780.h"

void send_byte_to_lcd(uint8_t value);
void send_command_to_lcd(uint8_t value);
void set_cursor(uint8_t y, uint8_t x);
void set_user_symbol(uint8_t no1,
                         uint8_t no2,
                         uint8_t no3,
                         uint8_t no4,
                         uint8_t no5,
                         uint8_t no6,
                         uint8_t no7);
void load_animation_symbols();
void lcd_display_init();
void display_text_on_lcd();
void generate_numeral(uint8_t numeral);
uint8_t adc_value_generator(uint8_t counter,
                            uint8_t initial_value,
                            uint8_t step);
void print_adc_value(uint8_t row,
                       uint8_t contact,   
                       uint8_t initial_value,            
                       uint8_t step,
                       uint8_t limit);
void animate_adc_value(uint8_t row,           
                       uint8_t initial_value,            
                       uint8_t step,
                       uint8_t limit);

//текущее значение АЦП
static uint8_t adc_value = 0;

/**
 * @brief Метод для отсылки данных! встроенному контроллеру
 * дисплея.
 * 
 * @param value Байт для отсылки.
 */
void send_byte_to_lcd(uint8_t value) {
    RC0 = 1;
    PORTB = value;
    RC2 = 1;
    __delay_ms(1);          
    RC2 = 0;
}

/**
 * @brief Метод для отсылки команд! встроенному контроллеру
 * дисплея.
 * 
 * @param value Байт команды для отсылки.
 */
void send_command_to_lcd(uint8_t value) { 
    RC0 = 0;
    PORTB = value;
    RC2 = 1;
    __delay_ms(1);          
    RC2 = 0;
    __delay_ms(20);     
}

/**
 * @brief Метод устанавливает курсор дисплея на указанную
 * в параметрах позицию. 
 * (надо будет обработать ситуацию, когда нули в параметрах)
 *  
 * @param x Номер строки.
 * @param y Номер стобца.
 */
void set_cursor(uint8_t y, uint8_t x) {   
    uint8_t count = x - 1;
    uint8_t command = 0x00;  
    if ((LCD_ROWS_MAX >= y)&&(LCD_COLUMNS_MAX >= x)) {    
        1 == y ? command = 0x00 : command = 0x40;
        while (0 != count) {
            command = command + 0x01;
            --count;
        }
        command = command + 0x80;
        send_command_to_lcd(command);      
    }      
}

/**
 * @brief Метод для генерации символа. Фактически, это просто
 * отправка байта встроенному микро-контроллеру дисплея. Однако, если
 * предварительно была отправлена команда записи, тогда последовательно
 * отправленные байты формируют символ в памяти встроенного микро-контроллера.
 * Каждое знакоместо дисплея представлено восемью микро-строками, для
 * формирования символа доступно 7.
 * 
 * @param no1 Код строки #1.
 * @param no2 Код строки #2.
 * @param no3 Код строки #3.
 * @param no4 Код строки #4.
 * @param no5 Код строки #5.
 * @param no6 Код строки #6.
 * @param no7 Код строки #7.
 */
void set_user_symbol(uint8_t no1,
                         uint8_t no2,
                         uint8_t no3,
                         uint8_t no4,
                         uint8_t no5,
                         uint8_t no6,
                         uint8_t no7) {
    send_byte_to_lcd(no1);
    send_byte_to_lcd(no2);
    send_byte_to_lcd(no3);
    send_byte_to_lcd(no4);
    send_byte_to_lcd(no5);
    send_byte_to_lcd(no6);
    send_byte_to_lcd(no7);
    send_byte_to_lcd(0x1F); 
}

/**
 * @brief Метод для загрузки в память микро-контроллера
 * дисплея символов, которые вспоследствии будут использованы
 * для создания анимации.
 */
void load_animation_symbols() { 
    send_command_to_lcd(0x40); 
    set_user_symbol(0x00,0x00,0x00,0x00,0x00,0x00,0x00);
    set_user_symbol(0x00,0x00,0x00,0x00,0x00,0x00,0x1F);
    set_user_symbol(0x00,0x00,0x00,0x00,0x00,0x1F,0x1F);
    set_user_symbol(0x00,0x00,0x00,0x00,0x1F,0x1F,0x1F);
    set_user_symbol(0x00,0x00,0x00,0x1F,0x1F,0x1F,0x1F);
    set_user_symbol(0x00,0x00,0x1F,0x1F,0x1F,0x1F,0x1F);
    set_user_symbol(0x00,0x1F,0x1F,0x1F,0x1F,0x1F,0x1F);
    set_user_symbol(0x1F,0x1F,0x1F,0x1F,0x1F,0x1F,0x1F);
}

/**
 * @brief Метод инициализации дисплея.
 */
void lcd_display_init() {   
    TRISB = 0x00;
    TRISC = 0x00;
    TRISE = 0x07;
    ADCON1 = 0x07;    
    PORTC = 0x00;
    send_command_to_lcd(0x01);
    send_command_to_lcd(0x38);
    send_command_to_lcd(0x06);
    send_command_to_lcd(0x0C);
    send_byte_via_serial(STATUS,STAND_INIT);
}

/**
 * @brief Метод генерирует байт, соответствующий цифре, указанной
 * в парметрах, и отправляет его встроенному микро-контроллеру дисплея.
 * 
 * @param numeral Цифра для генерации.
 */
void generate_numeral(uint8_t numeral) {    
    switch (numeral) {        
        case 0:
            send_byte_to_lcd(0x30);
        break;        
        case 1:
            send_byte_to_lcd(0x31);
        break;        
        case 2:
            send_byte_to_lcd(0x32);
        break;        
        case 3:
            send_byte_to_lcd(0x33);
        break;        
        case 4:
            send_byte_to_lcd(0x34);
        break;        
        case 5:
            send_byte_to_lcd(0x35);
        break;        
        case 6:
            send_byte_to_lcd(0x36);
        break;        
        case 7:
            send_byte_to_lcd(0x37);
        break;        
        case 8:
            send_byte_to_lcd(0x38);
        break;        
        case 9:
            send_byte_to_lcd(0x39);
        break;        
    }    
}

/**
 * @brief Функция позволяет избежать использования большого количества RAM памяти 
 * микро-контроллера путем генерации значений "на лету", без создания массива. Это
 * возможно благодаря тому, что между значениями, которые генерирует АЦП, есть
 * определенная зависимость.
 * 
 * @param counter Количество генерируемых значений АЦП.
 * @param initial_value Значение первого элемента в библиотеке.
 * @param step Значение, на которое будут различаться элементы.
 * every iteration.
 */
uint8_t adc_value_generator(uint8_t counter,
                         uint8_t initial_value,
                         uint8_t step) {
    return initial_value + step * counter;
}

/**
 * @brief Функция ищет значение в библиотеке. Оно должно быть
 * равно значению на контакте АЦП. После этого значение будет
 * выведено на дисплей.
 * 
 * @param contact Номер контакта АЦП.
 * @param limit Предельное значение.
 * @param initial_value Значение первого элемента в библиотеке.
 * @param step Значение, на которое различаются элементы.
 */
void print_adc_value(uint8_t row,
                       uint8_t contact,   
                       uint8_t initial_value,            
                       uint8_t step,
                       uint8_t limit) {
    adc_value = get_adc_value(contact);
    uint8_t generated_adc_value = 0;
    uint8_t count = 0;
    uint8_t temp = 0; 
    1 == row ? set_cursor(1, 13) : set_cursor(2, 14);
    for (count = 0; count < limit; ++count) {
        generated_adc_value = adc_value_generator(count, initial_value, step);
        if ((adc_value - ADC_ERROR_VALUE <= generated_adc_value) && 
                (adc_value + ADC_ERROR_VALUE >= generated_adc_value)) {
            if (count < 10) {
                send_byte_to_lcd(0x20);
                generate_numeral(count);
                if (1 == contact) {
                    send_byte_to_lcd(0xDF);
                    send_byte_to_lcd(0x43);
                } else {
                    send_byte_to_lcd(0x25);
                    send_byte_to_lcd(0x20);
                }
                //send_byte_via_serial(VALUE,count);
            } else if (count < 100) {
                generate_numeral((uint8_t)(count/10));
                generate_numeral((uint8_t)(count%10));
                if (1 == contact) {
                    send_byte_to_lcd(0xDF);
                    send_byte_to_lcd(0x43);
                } else {
                    send_byte_to_lcd(0x25);
                    send_byte_to_lcd(0x20);
                }
                //send_byte_via_serial(VALUE,count);
            } else {
                temp = (uint8_t)(count/10);
                generate_numeral((uint8_t)(temp/10));
                generate_numeral((uint8_t)(temp%10));
                generate_numeral((uint8_t)(count%10));
                if (1 == contact) {
                    send_byte_to_lcd(0xDF);
                    send_byte_to_lcd(0x43);
                } else {
                    send_byte_to_lcd(0x25);
                    send_byte_to_lcd(0x20);
                }
                //send_byte_via_serial(VALUE,count);
            }
        } 
    }
    
}

/**
 * @brief Метод выводит на дисплей пользовательский символ,
 * соответствующий текущему значению АЦП.
 * 
 * @param row Номер строки для отображения.
 */
void animate_adc_value(uint8_t row,           
                       uint8_t initial_value,            
                       uint8_t step,
                       uint8_t limit) {
    //в статической переменной при итерации сохраняется позиция
    //последнего отображенного символа
    static uint8_t position[2] = {1,1};
    uint8_t count = 0;
    uint8_t inner_count = 0;
    uint8_t generated_adc_value = 0;
    11 == position[0] ? position[0] = 1 : position[0];
    11 == position[1] ? position[1] = 1 : position[1];
    1 == row ? set_cursor(1, position[0]) : set_cursor(2, position[1]);
    for (count = 0; count < limit; ++count) {
        generated_adc_value = adc_value_generator(count,initial_value,step);
        if ((adc_value - 0x01 <= generated_adc_value) && (adc_value + 0x01 >= generated_adc_value)) {
            for (inner_count = 0; inner_count < 8; ++inner_count) {
                if (count < ADC_VALUE_STEP * (1 + inner_count)) {
                    send_byte_to_lcd(inner_count);
                    count = limit;
                    break;
                }
            }
        }
    } 
     1 == row ? position[0]++ : position[1]++;           
}







