#ifndef _Table_h_
#define _Table_h_
#include <vector>
#include <iostream>
#include "Cell.h"
#include "Action.h"
#include "MyExceptions.h"

using namespace std;

class Table {
public:
	static Table* getInstance();//returns singleton of table
	friend ostream& operator<<(ostream& os, const Table& table);
	void redo();
	void undo();
	void perform(string command);//method that performs different commands
	bool isSaved()const { return saved; }
	~Table();
protected:
	//methods used internaly for performing of the commands
	Cell* getCell(int row, int col);
	bool cellExists(int row, int col) const;
	bool cellExists(string cell) const;
	string getCellValue(int row, int col) const;
	double getCellNumValue(string cell) const;
	int widestElementInColumn(int col) const;
	void changeFormat(int row, int col, string format, string oldValue="");
	void changeValue(int row, int col, string value);
	string getCellDisplay(int row, int col) const;
	void changeDecimals(int row, int col, int decimals);
private:
	Table();
	static Table *inst_;//table singleton
	friend class CSVParser; //   \.
	friend class JSONParser;//	   =>friend classes that operate with some values from table
	friend class Formula;//      /.
	vector<Cell*> cells;//array of cells from table
	int*columnWidths;//array that holds widths of columns
	Action actions;//class used for undo/redo actions
	short saved=0;//flag that states if some unsaved changes are made over table
};
#endif // !_Table_h_
