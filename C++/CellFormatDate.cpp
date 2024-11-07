#include "CellFormatDate.h"

//leap year check
bool CellFormatDate::isLeapYear(int year)
{
    return ((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0);
}
//valid date check
bool CellFormatDate::isValidDate(int day, int month, int year)
{
    if (year < 0 || month < 1 || month > 12)//valud values
        return false;

    const int daysInMonth[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };//valid number of days in each month 
    int maxDay = daysInMonth[month - 1];

    if (month == 2 && isLeapYear(year))//if its leap year february has 29 days
        maxDay = 29;

    return (day >= 1 && day <= maxDay);
}
