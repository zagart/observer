/**
 * @page adc.c
 * 
 * @brief File which contains functions for executing operations
 * with analog-to-digital converter. 
 * 
 * @detailed ADC integrated to the controller PIC16F877 and has 8 contacts.
 * In the context of the stand this module currently has next use cases:
 * - tempreture measuring (in complex with temperature sensor)
 * - light sensor value watching
 * 
 * @author Zagart
 */

#include <xc.h>
#include <stdint.h>
#include <stdbool.h>
#include "constants.h"
//#include "matrixkeyb.h"

uint8_t get_adc_value(uint8_t contact);

/**
 * @brief Function for getting value from ADC.
 * 
 * @return Current value.
 */
uint8_t get_adc_value(uint8_t contact) {

        0x0F != TRISE ? TRISE = 0x0F : TRISE; // as output 

        0x00 != ADCON1 ? ADCON1 = 0x00: ADCON1; // as analog
        
        // we are selecting contact of adc
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
        
        if (ADCS1 == 1) {
            ADCS1 = 0;      // time of converting for 1 bit as 8Tad
        }
        if (ADCS0 == 0) {
            ADCS0 = 1;
        }  
        
        ADON = 1;               // ADC on
        
        0 == contact ? ADFM = 0 : ADFM = 1;
        
        __delay_us(30);         // time for charging Chold
        
        GO_DONE = 1;            // start converting

        while (0 != GO_DONE) {    // while not converted
            CLRWDT();
        }

        __delay_us(5);          // period required for the correctness
                                // of the next convertion
        
        if (0 == contact) {
            return ADRESH;
        } else {
            return ADRESL;
        }
      
}


