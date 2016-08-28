

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
    uint8_t running = 0;
    lcd_display_init();
    load_animation_symbols();    
    while (SYSTEM_EXIT != running) {
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
        send_byte_via_serial(STATUS,STAND_INIT);
        __delay_ms(500);
    }
}
