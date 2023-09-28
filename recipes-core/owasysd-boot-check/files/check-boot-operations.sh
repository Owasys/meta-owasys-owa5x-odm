#!/bin/bash
#===============================================================================
#
#          FILE:  check-boot-operations.sh
#
#         USAGE:  ./check-boot-operations.sh
#         DEBUG:  TRACE=1 ./check-boot-operations.sh
#
#   DESCRIPTION: Script to determine if a boot was correct. It should return 0,
#                returning 1 ( exit 1 ) means an error and it will reboot the system.
#
#       OPTIONS:  ---
#  REQUIREMENTS:  ---
#          BUGS:  ---
#        AUTHOR:  owzagj
#       COMPANY:  Owasys SL
#       VERSION:  0.0.0
#       CREATED:  2023-05-10
#===============================================================================

main() {

check_memory_map
check_system_integrity
check_loaded_kernel_modules

}

check_memory_map() 
{
   mount | grep -q 'ubi0:device'
   if [ $? -ne 0 ]; then
      logger "device partition not mounted!"
      exit 1
   fi

   mount | grep -q '/data'
   if [ $? -ne 0 ]; then
      logger "data partition not mounted!"
      exit 1
   fi

   #Any function returned value but 0 will be understood as an error
   #exit 1

   logger "Boot Check: Memory mapped tests OK"

}

check_system_integrity() 
{
   logger "Boot Check: System Integrity tests OK"
   #Any function returned value but 0 will be understood as an error
   #exit 1
}

check_loaded_kernel_modules()
{

   lsmod | grep -q 'owa5x_gpio'
   if [ $? -ne 0 ]; then
      logger "Owasys kernel module not present!"
      exit 1
   fi

   lsmod | grep -q 'mcp251xfd'
   if [ $? -ne 0 ]; then
      logger "CANFD to SPI kernel module not present!"
      exit 1
   fi

   lsmod | grep -q 'st_lsm6dsx_spi'
   if [ $? -ne 0 ]; then
      logger "6 axis accelerometer kernel module not present!"
      exit 1
   fi

   #Any function returned value but 0 will be understood as an error
   #exit 1

   logger "Boot Check: Loaded kernel modules tests OK"

}

main "$@"
