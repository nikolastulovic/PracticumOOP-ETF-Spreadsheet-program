#ifndef _CellFormat_h_
#define _CellFormat_h_
#include <string>

using namespace std;

class CellFormat {
public:
	virtual string display(const string& value) = 0;//displaying the value of cell in correct way
	virtual bool appropriate(const string& value) = 0;//checking if value that is tried to be entered is valid
	virtual string getFormat()const = 0;//format getter
};

#endif // !_CellFormat_h_