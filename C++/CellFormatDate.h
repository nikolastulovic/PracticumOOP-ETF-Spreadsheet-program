#ifndef _CellFormatDate_h_
#define _CellFormatDate_h_
#include <string>
#include <regex>
#include "CellFormat.h"
#define DEFAULTRET "01.01.2023."
using namespace std;

class CellFormatDate : public CellFormat {
public:
	string display(const string& value)
	{
		if (value == "" || appropriate(value)) return value;
		return "ERROR";//greska, treba se jos pozabaviti njome
	}
	//checking if value that was inputed is appropriate for current data format
	bool appropriate(const string& value)
	{
		regex r("^(\\d{2})\\.(\\d{2})\\.(\\d{4})\\.$");
		smatch matches;
		if (regex_search(value, matches, r))
		{
			int day = stoi(matches[1].str());
			int month = stoi(matches[2].str());
			int year = stoi(matches[3].str());
			return isValidDate(day, month, year);
		}
		return false;
	}
	string getFormat()const { return "date"; }
private:
	bool isLeapYear(int year);
	bool isValidDate(int day, int month, int year);
};

#endif // !_CellFormatDate_h_