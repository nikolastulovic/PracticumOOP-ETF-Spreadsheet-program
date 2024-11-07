#include "CellFormatNumber.h"
#include <regex>
#include <string>
#include "Formula.h"

string CellFormatNumber::display(const string& value)
{
	string ret="";
	if (value[0] == '=') ret = calculate(value);
	else if (appropriate(value)) ret = value;
	if (ret != "")
	{
		regex r("^(\\d+)(\\.\\d+)?$");
		smatch matches;
		if (regex_search(ret, matches, r))
		{
			if (decimals && matches[2].str().length() < 2)//if some decimal places are demanded but value is int
			{
				ret += ".";
				for (int i = 0; i < decimals; i++) {
					ret += "0";
				}
				return ret;
			}
			return matches[1].str() + (decimals ? "." : "") + (decimals ? matches[2].str().substr(1, decimals) : "");
		}
	}
    return "ERROR";//error value
}
//checking if value that was inputed is appropriate for current data format
bool CellFormatNumber::appropriate(const string& value)
{
	regex r("^(\\d+)(\\.\\d+)?$");
	smatch matches;
	if (regex_search(value, matches, r)) return true;
    return false;
}

//function that calls Formula class that performs calculation
string CellFormatNumber::calculate(const string& value)
{
	Formula f(value);
	return f.calculate();
}
