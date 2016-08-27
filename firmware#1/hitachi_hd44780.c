/**
 * @page hitachi_hd44780.c
 * 
 * @brief File which contains collection of the functions
 * for executing operations with the Hitachi HD44780.
 * 
 * @detailed Functions represented below execute measuring
 * of tempreture using ACD module integrated in controller
 * PIC16F877 and display it at the Hitachi HD44780 LCD display.
 * 
 * @author Zagart
 */

#include <xc.h>
#include <stdint.h>
#include <stdbool.h>
#include "adc.h"
#include "uart.h"
#include "constants.h"

void send_byte_to_lcd(uint8_t value);
void send_command_to_lcd(uint8_t value);
void set_cursor(uint8_t y, uint8_t x);
void specify_user_symbol(uint8_t no1,
                         uint8_t no2,
                         uint8_t no3,
                         uint8_t no4,
                         uint8_t no5,
                         uint8_t no6,
                         uint8_t no7);
void loading_user_symbols();
void lcd_display_init();
void display_text_on_lcd();
void display_numeral(uint8_t numeral);
uint8_t adc_value_generator(uint8_t counter,
                            uint8_t initial_value,
                            uint8_t step);
void display_adc_value(uint8_t contact, 
                       uint8_t limit, 
                       uint8_t row,           
                       uint8_t initial_value,            
                       uint8_t step);
void animate_adc_value(uint8_t row,           
                       uint8_t initial_value,            
                       uint8_t step);

/**
 * @brief Borders of massives listed below require be defined by hand
 * as described for function library_load.
 * @ref library_load
 */

static uint8_t limit_of_library = 103;
static uint8_t adc_value_step = 13;
static uint8_t adc_value = 0;

/**
 * @brief Function sends bytes to the embedded controller of 
 * Hitachi display.
 * 
 * @param value Contains byte for the embedded controller.
 */
void send_byte_to_lcd(uint8_t value) {
    RC0 = 1;
    PORTB = value;
    RC2 = 1;
    __delay_ms(1);          
    RC2 = 0;
}

/**
 * @brief Function sends commands to the embedded controller of 
 * Hitachi display.
 * 
 * @param value Contains command for the embedded controller.
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
 * @brief Function is destined for specifying random symbols. In 
 * fact it is just sending bytes for the embedded controller of 
 * LCD-display but if previously was sent write command, then this
 * bulk of bytes becomes user-specified symbol in memory of display.
 * All bytes from parameters becomes one symbol.
 * 
 * @param no1 Code of row #1.
 * @param no2 Code of row #2.
 * @param no3 Code of row #3.
 * @param no4 Code of row #4.
 * @param no5 Code of row #5.
 * @param no6 Code of row #6.
 * @param no7 Code of row #7.
 */

/**
 * @brief Function sets cursor to the position which pointed at input 
 * parameters. Affects LCD display in case 1<=y<=2 and 1<=x<=16.
 * 
 * @param x Number of row.
 * @param y Number of column.
 */
void set_cursor(uint8_t y, uint8_t x) {
    
    uint8_t count = x - 1;
    uint8_t command = 0x00;
    
    if ((1==y||2==y)&&(0<x&&17>x)) {
        
        1==y ? command = 0x00 : command = 0x40;

        while (0!=count) {
            command = command + 0x01;
            --count;
        }

        command = command + 0x80;
        send_command_to_lcd(command);
        
    }
       
}

void specify_user_symbol(uint8_t no1,
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
 * @brief Function performs multiple calls of another function which
 * is destined for specifying random symbols and saving them in memory of
 * embedded controller of LCD-display.
 * 
 */
void loading_user_symbols() { 
    send_command_to_lcd(0x40); 
    specify_user_symbol(0x00,0x00,0x00,0x00,0x00,0x00,0x00);
    specify_user_symbol(0x00,0x00,0x00,0x00,0x00,0x00,0x1F);
    specify_user_symbol(0x00,0x00,0x00,0x00,0x00,0x1F,0x1F);
    specify_user_symbol(0x00,0x00,0x00,0x00,0x1F,0x1F,0x1F);
    specify_user_symbol(0x00,0x00,0x00,0x1F,0x1F,0x1F,0x1F);
    specify_user_symbol(0x00,0x00,0x1F,0x1F,0x1F,0x1F,0x1F);
    specify_user_symbol(0x00,0x1F,0x1F,0x1F,0x1F,0x1F,0x1F);
    specify_user_symbol(0x1F,0x1F,0x1F,0x1F,0x1F,0x1F,0x1F);
}

/**
 * @brief Function executes initialization of the
 * Hitachi display.
 * 
 * @detailed Includes configuration of neccesary ports and sending
 * required commands to the embedded controller of display.
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
    send_byte_via_serial(STAND_INIT);
}

/**
 * @brief Function perform displaying of the required numeral on the
 * Hitachi display.
 * 
 * @param numeral Numeral which is destined for displaying.
 */
void display_numeral(uint8_t numeral) {    
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
 * @brief To avoid using much RAM memory of controller this function
 * replaces creating big massive.
 * 
 * @param counter Number required for generating value of adc.
 * @param initial_value Value of the first element in emulating library.
 * @param step Defines value which will adds to the initial_value
 * every iteration.
 */
uint8_t adc_value_generator(uint8_t counter,
                         uint8_t initial_value,
                         uint8_t step) {
    return initial_value + step * counter;
}

/**
 * @brief Function looking for value in library. It must be equal
 * to value on ADC contact. After it value will displayed.
 * 
 * @param contact ADC contact number.
 * @param limit Values limit.
 * @param initial_value Value of the first element in emulating library.
 * @param step Defines value which will adds to the initial_value
 * every iteration.
 */
void display_adc_value(uint8_t contact, 
        uint8_t limit, 
        uint8_t row,           
        uint8_t initial_value,            
        uint8_t step) {

    adc_value = get_adc_value(contact);
    uint8_t generated_adc_value = 0;
    uint8_t count = 0;
    uint8_t error = 0x01;
    uint8_t temp = 0;
    
    1 == row ? set_cursor(1, 13) : set_cursor(2, 14);
    
    for (count = 0; count < limit; ++count) {
        generated_adc_value = adc_value_generator(count, initial_value, step);
        if ((adc_value - error <= generated_adc_value) && (adc_value + error >= generated_adc_value)) {
            if (count < 10) {
                send_byte_to_lcd(0x20);
                display_numeral(count);
                if (1 == contact) {
                    send_byte_to_lcd(0xDF);
                    send_byte_to_lcd(0x43);
                } else {
                    send_byte_to_lcd(0x25);
                    send_byte_to_lcd(0x20);
                }
            } else if (count < 100) {
                display_numeral((uint8_t)(count/10));
                display_numeral((uint8_t)(count%10));
                if (1 == contact) {
                    send_byte_to_lcd(0xDF);
                    send_byte_to_lcd(0x43);
                } else {
                    send_byte_to_lcd(0x25);
                    send_byte_to_lcd(0x20);
                }
            } else {
                temp = (uint8_t)(count/10);
                display_numeral((uint8_t)(temp/10));
                display_numeral((uint8_t)(temp%10));
                display_numeral((uint8_t)(count%10));
                if (1 == contact) {
                    send_byte_to_lcd(0xDF);
                    send_byte_to_lcd(0x43);
                } else {
                    send_byte_to_lcd(0x25);
                    send_byte_to_lcd(0x20);
                }
            }
        } 
    }
    
}

/**
 * @brief Method executes animating of current ADC value.
 * 
 * @param row Select of row to print data.
 * @param position Reserved variable for saving current position
 * of cursor in row.
 */
void animate_adc_value(uint8_t row,           
                       uint8_t initial_value,            
                       uint8_t step) {
   
    static uint8_t position[2] = {1,1};
    uint8_t count = 0;
    uint8_t inner_count = 0;
    uint8_t generated_adc_value = 0;

    11 == position[0] ? position[0] = 1 : position[0];
    11 == position[1] ? position[1] = 1 : position[1];
    
    1 == row ? set_cursor(1, position[0]) : set_cursor(2, position[1]);
       
    for (count = 0; count < limit_of_library; ++count) {
        generated_adc_value = adc_value_generator(count,initial_value,step);
        if ((adc_value - 0x01 <= generated_adc_value) && (adc_value + 0x01 >= generated_adc_value)) {
            for (inner_count = 0; inner_count < 8; ++inner_count) {
                if (count < adc_value_step * (1 + inner_count)) {
                    send_byte_to_lcd(inner_count);
                    count = limit_of_library;
                    break;
                }
            }
        }
    }
    
     1 == row ? position[0]++ : position[1]++;           
}

/**
 * @brief Main function which is responsible for displaying
 * symbols. 
 */
void main(void) {
    lcd_display_init();
    loading_user_symbols();    
    while (true) {
        CLRWDT();        
        display_adc_value(1,limit_of_library,1,0x21,0x02);
        animate_adc_value(1,0x21,0x02);        
        display_adc_value(0,100,2,0x00,0x01);
        animate_adc_value(2,0x00,0x01);
        __delay_ms(500);       
    }
}





