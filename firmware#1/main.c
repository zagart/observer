

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
    lcd_display_init();
    while (TRUE) {
        CLRWDT();        
        process_LCD();
        __delay_ms(500);
    }
    while(TRUE){CLRWDT();};
}
