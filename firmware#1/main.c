

#include <xc.h>
#include <stdint.h>
#include "adc.h"
#include "constants.h"
#include "hitachi_hd44780.h"
#include "uart.h"

/**
 * @brief Точка входа прошивки.
 */
void main(void) {
    send_message_via_serial(STAND_NUMBER,
            STAND_MC,
            INIT,
            NULL);
    while(TRUE){CLRWDT();};
    lcd_display_init();
    load_animation_symbols();    
    while (TRUE) {
        CLRWDT();        
        print_adc_value(1,
                TEMP_SENSOR_PIN,
                TEMP_SENSOR_INIT_VALUE,
                TEMP_SENSOR_STEP_VALUE,
                TEMP_SENSOR_LIBRARY_MAX);
        animate_adc_value(1,
                TEMP_SENSOR_INIT_VALUE,
                TEMP_SENSOR_STEP_VALUE,
                TEMP_SENSOR_LIBRARY_MAX);        
        print_adc_value(2,
                LIGHT_SENSOR_PIN,
                LIGHT_SENSOR_INIT_VALUE,
                LIGHT_SENSOR_STEP_VALUE,
                LIGHT_SENSOR_LIBRARY_MAX);
        animate_adc_value(2,
                LIGHT_SENSOR_INIT_VALUE,
                LIGHT_SENSOR_STEP_VALUE,
                LIGHT_SENSOR_LIBRARY_MAX);
        __delay_ms(500);
        
        __delay_ms(500);
    }
}
