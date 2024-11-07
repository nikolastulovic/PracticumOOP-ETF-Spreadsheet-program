#include "Table.h"
#include <iomanip>
#include <regex>
#include <algorithm>
#include <string>
#include "CSVParser.h"
#include "JSONParser.h"
#define COLNUM  'Z' - 'A'+ 2 //fixed maximum number of columns

Table* Table::inst_=nullptr;

Table::Table()
{
	columnWidths = new int[COLNUM];
	columnWidths[0] = 3;//column that contains row index
	char ch = 'A';
	for (int i = 1; i < COLNUM; i++, ch++)
	{
		columnWidths[i] = 5;
		cells.push_back(new Cell(string(1, ch), 0, i));//filling the header
	}
}
Table* Table::getInstance()
{
	if (!inst_) inst_ = new Table();
	return inst_;
}
//redo action
void Table::redo()
{
	string act = actions.getActionRedo();
	if (act != "") {
		regex r("^([a-zA-Z]+)-([^-]+)-([^-]+)-(-?\\d+)-(\\d+)$");
		smatch matches;
		regex_search(act, matches, r);
		string action = matches[1];
		int row = stoi(matches[4].str());
		int col = stoi(matches[5].str());
		if (action == "edit")
		{
			string newValue = matches[3];
			Cell* c = getCell(row, col);
			//changing value 
			c->changeValue(newValue);
		}
		else if (action == "format")
		{
			string newFormat = matches[3];
			if (row == -1)//indicator that format has been changed for whole column
			{
				for (Cell* c : cells)//changing format for whole column
				{
					if (c->column == col && c->row != 0)
					{
						if (newFormat == "text") c->changeFormat(new CellFormatText());
						else if (newFormat == "number") c->changeFormat(new CellFormatNumber());
						else if (newFormat == "date") c->changeFormat(new CellFormatDate());
					}
				}
			}
			else
			{
				Cell* c = getCell(row, col);
				//changing format 
				if (newFormat == "text") c->changeFormat(new CellFormatText());
				else if (newFormat == "number") c->changeFormat(new CellFormatNumber());
				else if (newFormat == "date") c->changeFormat(new CellFormatDate());
			}
		}
		else if (action == "decimals")
		{
			int newDecimalPlaces = stoi(matches[3]);
			Cell* c = getCell(row, col);
			//changing decimal places
			c->changeDecimals(newDecimalPlaces);
		}
		columnWidths[col] = widestElementInColumn(col);//adjusting column widths
	}
	else throw GNemaRedo();//redo not available
}
//undo action
void Table::undo()
{
	string act = actions.getActionUndo();
	if (act != "") {
		regex r("^([a-zA-Z]+)-([^-]+)-([^-]+)-(-?\\d+)-(\\d+)$");
		smatch matches;
		regex_search(act, matches, r);
		string action = matches[1];
		int row = stoi(matches[4].str());
		int col = stoi(matches[5].str());
		if (action == "edit")
		{
			string oldValue = matches[2];
			if (oldValue == "`") oldValue = "";
			Cell* c = getCell(row, col);
			//changing value 
			c->changeValue(oldValue);
		}
		else if (action == "format")
		{
			string oldFormat = matches[2];
			if (row == -1)//indicator that format has been changed for whole column
			{
				for (Cell* c : cells)//changing format for whole column
				{
					if (c->column == col && c->row != 0)
					{
						if (oldFormat == "text") c->changeFormat(new CellFormatText());
						else if (oldFormat == "number") c->changeFormat(new CellFormatNumber());
						else if (oldFormat == "date") c->changeFormat(new CellFormatDate());
						string newFormat = c->cf->getFormat();
					}
				}
			}
			else
			{
				Cell* c = getCell(row, col);
				//changing format 
				if (oldFormat == "text") c->changeFormat(new CellFormatText());
				else if (oldFormat == "number") c->changeFormat(new CellFormatNumber());
				else if (oldFormat == "date") c->changeFormat(new CellFormatDate());
			}
		}
		else if (action == "decimals")
		{
			int oldDecimalPlaces = stoi(matches[2]);
			Cell* c = getCell(row, col);
			//changing decimal places
			c->changeDecimals(oldDecimalPlaces);
		}
		columnWidths[col] = widestElementInColumn(col);//adjusting column widths
	}
	else throw GNemaUndo();//undo not available
}
//reading and execution of all instructions over table
void Table::perform(string command)
{
	if (command == "redo")
	{
		redo();
		saved = 0;
		return;
	}
	if (command == "undo")
	{
		undo();
		saved = 0;
		return;
	}
	for_each(command.begin(), command.end(), [](char& c) { c = tolower(c); });//converting command to lower case
	//regex recognition
	regex r("^([a-zA-Z]+)-([^-]+)-(.+)$");
	smatch matches;
	if (regex_search(command, matches, r))
	{
		string nameOfAction = matches[1];
		string ind = matches[2];
		string value = matches[3];
		int row = -1, col;
		col = ind[0] - 'a' + 1;
		//commands
		if (nameOfAction == "edit") {
			row = stoi(ind.substr(1));
			changeValue(row, col, value);
			saved = 0;
		}
		else if (nameOfAction == "decimals")
		{
			row = stoi(ind.substr(1));
			changeDecimals(row, col, stoi(value));
			saved = 0;
		}
		else if (nameOfAction == "width")
		{
			columnWidths[col] = stoi(value);
			saved = 0;
		}
		else if (nameOfAction == "format")
		{
			if (ind.length() > 1) row = stoi(ind.substr(1));
			if (value == "text" || value == "number" || value == "date")
			{
				changeFormat(row, col, value);
				saved = 0;
			}
			else throw GNepoznatFormat();//format unknown
		}
		else if (nameOfAction == "save")
		{
			if (value == "csv")
			{
				Parser* p = new CSVParser(ind + ".csv");
				p->save();
				free(p);
			}
			else if (value == "json")
			{
				Parser* p = new JSONParser(ind + ".json");
				p->save();
				free(p);
			}
			saved = 1;
		}
		else if (nameOfAction == "load")
		{
			if (value == "csv")
			{
				Parser* p = new CSVParser(ind + ".csv");
				p->read();
				free(p);
			}
			else if (value == "json")
			{
				Parser* p = new JSONParser(ind + ".json");
				p->read();
				free(p);
			}
			saved = 1;
		}
		else throw GNepoznataKomanda(); //command unknown
	}
	else throw GNepoznataKomanda(); //command unknown
}
Table::~Table()
{
	free(columnWidths);
	free(inst_);
	for (Cell* c:cells) free(c);
}
//function used by other functions that implement changing of format of cell (row,col)
void Table::changeFormat(int row, int col, string format, string oldValue)
{
	if (row == -1)//if its -1 formating is done for the whole column
	{
		int actionDone = 0;
		for (Cell* c : cells)
		{
			if (c->column == col && c->row != 0)
			{
				string oldFormat = c->cf->getFormat();
				if (format == "text") c->changeFormat(new CellFormatText());
				else if (format == "number") c->changeFormat(new CellFormatNumber());
				else if (format == "date") c->changeFormat(new CellFormatDate());
				string newFormat = c->cf->getFormat();
				
				if (!actionDone)//if its done for whole row its enough to get pushed once to undo actions stack
				{
					actionDone = 1;
					actions.pushAction("format", oldFormat, newFormat, -1, col);//saving which action is performed
				}
			}
		}
	}
	else//formating of a given cell
	{
		if (!cellExists(row, col))
		{
			Cell* c = new Cell("", row, col);
			string oldFormat = "text";//default format is text, there is no need to call function to getFormat for newly created cell
			if (format == "text") cells.push_back(c->changeFormat(new CellFormatText()));
			else if (format == "number") cells.push_back(c->changeFormat(new CellFormatNumber()));
			else if (format == "date") cells.push_back(c->changeFormat(new CellFormatDate()));
			string newFormat = c->cf->getFormat();
			actions.pushAction("format", oldFormat, newFormat, row, col);//saving which action is performed
		}
		else
		{
			Cell* c = getCell(row, col);
			string oldFormat = c->cf->getFormat();
			if (format == "text") c->changeFormat(new CellFormatText());
			else if (format == "number") c->changeFormat(new CellFormatNumber());
			else if (format == "date") c->changeFormat(new CellFormatDate());
			string newFormat = c->cf->getFormat();
			actions.pushAction("format", oldFormat, newFormat, row, col);//saving which action is performed
		}
	}
	columnWidths[col] = widestElementInColumn(col);
}
//function used by other functions that implement changing of value that cell at (row,col) contains
void Table::changeValue(int row, int col, string newValue)
{
	string oldValue = getCellValue(row, col);
	if (!cellExists(row, col)) cells.push_back(new Cell(newValue, row, col));
	else {
		Cell* c = getCell(row, col);
		if (c->cf->appropriate(newValue) || newValue[0] == '=') c->changeValue(newValue);
		else throw GNeodgovarajucaVrednost();//inappropriate value tried to be set for this format
	}
	actions.pushAction("edit", oldValue, newValue, row, col);//saving which action is performed
	columnWidths[col] = widestElementInColumn(col);//adjusting column widths
}
//function that is used to change number of decimals shown 
void Table::changeDecimals(int row, int col, int decimals)
{
	Cell* c = getCell(row, col);
	string oldNumberOfDecimalPlaces;
	if (c && c->cf->getFormat() == "number")
	{
		oldNumberOfDecimalPlaces = to_string(((CellFormatNumber*)(c->cf))->getNumberOfDecimals());
		c->changeDecimals(decimals);
	}
	else throw GPostavljanjeDecimala();//decimals can be set only for number format
	

	actions.pushAction("decimals", oldNumberOfDecimalPlaces, to_string(decimals), row, col);
	columnWidths[col] = widestElementInColumn(col);
}
//return pointer to the cell at (row,col)
Cell* Table::getCell(int row, int col)
{
	for (Cell* c:cells) {
		if (c->getRow() == row && c->getColumn() == col) return c;
	}
	return nullptr;
}
//checks if cell at (row,col) or with name cell exists
bool Table::cellExists(int row, int col) const
{
	for (Cell* c : cells) if (c->getRow() == row && c->getColumn() == col) return true;
	return false;
}
bool Table::cellExists(string cell) const
{
	for_each(cell.begin(), cell.end(), [](char& c) { c = tolower(c); });
	int col = cell[0] - 'a' + 1;
	int row = stoi(cell.substr(1));
	return cellExists(row, col);
}
//return value of cell at (row,col) 
string Table::getCellValue(int row, int col) const
{
	for (Cell* c : cells) {
		if (c->getRow() == row && c->getColumn() == col) return c->getValue();
	}
	return "";
}
//methods used for printing
double Table::getCellNumValue(string cell) const
{
	for_each(cell.begin(), cell.end(), [](char& c) { c = tolower(c); });
	int col = cell[0] - 'a' + 1;
	int row = stoi(cell.substr(1));
	for (Cell* c : cells) {
		if (c->getRow() == row && c->getColumn() == col) return stod(c->getValue());
	}
	return 0;
}
string Table::getCellDisplay(int row, int col) const
{
	for (Cell* c : cells) {
		if (c->getRow() == row && c->getColumn() == col) return c->cf->display(c->value);
	}
	return "";
}
int Table::widestElementInColumn(int col) const
{
	int max = 0;
	for (Cell* c : cells) {
		if (c->getColumn() == col && c->cf->display(c->value).length() > max) max = (int)c->cf->display(c->value).length();
	}
	return max;
}
//table printing with standard operator<<
ostream& operator<<(ostream& os, const Table& table)
{
	//getting number of rows used
	auto maxEl = [](vector<Cell*> cells) {
		int max = 0;
		for (Cell* c : cells) {
			int row = c->getRow();
			if (row > max) max = row;
		}
		return max;
	};
	//getting other parameters for printing
	int max = maxEl(table.cells)+1;
	if (max < 4) max = 4;//setting size of table to some minimum of rows for default look of empty table
	table.columnWidths[0] = (int)(to_string(max)).length();
	//printing
	for (int i = 0; i < max; i++) {
		if (i == 0 || i == 1)//header of table
		{
			for (int j = 0; j < COLNUM; j++) os << left << setw(table.columnWidths[j]) << setfill('-') << "-" << setw(1) << "|";
			putchar('\n');
		}
		for (int j = 0; j < COLNUM; j++)//other rows
		{
			os << setw(table.columnWidths[j]) << setfill(' ');
			if (j == 0 && i != 0) os << to_string(i);//numbering part of table
			else if (table.getCellDisplay(i, j) == "ERROR") os << "\033[1;31mERROR\033[0m";//error message in red color
			else os << table.getCellDisplay(i, j); //normal cells
			os << setw(1) << "|";
		}
		putchar('\n');
	}
	return os;
}