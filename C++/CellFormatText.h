#ifndef _CellFormatText_h_
#define _CellFormatText_h_
#include <string>
#include "CellFormat.h"
using namespace std;

class CellFormatText:public CellFormat {
public:
	string display(const string& value) { return value; }
	bool appropriate(const string& value) { return true; }
	string getFormat()const { return "text"; }
};

#endif // !_CellFormatText_h_