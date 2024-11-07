#ifndef _Cell_h_
#define _Cell_h_
#include <string>
#include "CellFormat.h"
#include "CellFormatText.h"
#include "CellFormatNumber.h"
#include "CellFormatDate.h"

using namespace std;

class Cell {
public:
	//getters and setters
	Cell* changeFormat(CellFormat* cfnew);
	Cell* changeValue(string value) { this->value = value; return this; }
	Cell* changeDecimals(int decimals);
	string getValue() { return value; }
	int getRow() { return row; }
	int getColumn() { return column; }
protected:
	Cell(string val, int r, int c, CellFormat* cellf = new CellFormatText()) : value(val), row(r), column(c), cf(cellf) {}
	~Cell() { free(cf); }
	string display() { return cf->display(value); }
private:
	friend class Table;//........
	friend class CSVParser;//....these classes need cell values to operate
	friend class JSONParser;//...
	string value;//value of the cell
	CellFormat* cf;//cell format
	int row, column;//cell identification
};

#endif // !_Cell_h_