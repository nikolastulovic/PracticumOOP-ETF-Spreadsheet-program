#include "Cell.h"
#include <iostream>
Cell* Cell::changeFormat(CellFormat* cfnew)
{
	free(cf);
	cf = cfnew;
	return this;
}

Cell* Cell::changeDecimals(int decimals)
{
	free(cf);
	cf = new CellFormatNumber(decimals);
	return this;
}