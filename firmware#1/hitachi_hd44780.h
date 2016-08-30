/**
 * @page hitachi_hd44780.h
 * 
 * @brief Файл с прототипами функций основного класса.
 * Необходим компилятору для сборки проекта.
 * 
 * @ref hitachi_hd44780.c
 * 
 * @author Zagart
 */

#ifndef HITACHI_HD44780_H
#define HITACHI_HD44780_H
//константы LCD-дисплея
#define LCD_ROWS_MAX 2
#define LCD_COLUMNS_MAX 16
#define TEMP_REACTION_VALUE 2
#define LIGHT_REACTION_VALUE 3
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
void update_data(uint8_t row, uint8_t value);
void send_data();
void animate_adc_value(uint8_t row,           
                       uint8_t initial_value,            
                       uint8_t step,
                       uint8_t limit);
#endif