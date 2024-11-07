#ifndef _CellFormatNumber_h_
#define _CellFormatNumber_h_
#include <string>
#include "CellFormat.h"
using namespace std;

class CellFormatNumber :public CellFormat {
public:
	CellFormatNumber(int dec=0):decimals(dec){ }
	string display(const string& value);
	bool appropriate(const string& value);
	string getFormat()const { return "number"; }
	int getNumberOfDecimals()const { return decimals; }
private:
	string calculate(const string& value);//replacement for the formula class
	int decimals;//decimals that need to be showed 
};

#endif // !_CellFormatNumber_h_