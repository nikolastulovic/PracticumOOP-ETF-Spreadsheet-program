#include "CSVParser.h"
#include "Cell.h"
#include <fstream>
#define COLNUM  'Z' - 'A'+ 2 //fixed maximum number of columns

void CSVParser::save()
{
	//opening file
	ofstream my_file;
	my_file.open(filename);
	if (!my_file) {
		throw GNeuspeloOtvaranje();//file opening failed
	}
	else
	{
		//getting number of rows used
		auto maxEl = [](vector<Cell*> cells) {
			int max = 0;
			for (Cell* c : cells)
			{
				int row = c->getRow();
				if (row > max) max = row;
			}
			return max;
		};
		//getting other parameters for printing
		int max = maxEl(table->cells) + 1;

		//filling the csv file
		for (int i = 1; i < max; i++)
		{
			for (int j = 1; j < COLNUM; j++) my_file << table->getCellValue(i, j) << ";";
			my_file<<'\n';
		}
		//closing the used file
		my_file.close();
	}
}

void CSVParser::read()
{
	//clearing old values from the table
	table->cells.clear();
	//filling the header
	char ch = 'A';
	for (int i = 1; i < COLNUM; i++, ch++)
	{
		table->columnWidths[i] = 5;
		table->cells.push_back(new Cell(string(1, ch), 0, i));//filling the header
	}
	//reading from csv
	ifstream my_file;
	my_file.open(filename);
	if (!my_file) {
		throw GNeuspeloOtvaranje();//file opening failed
	}
	else {
		string red;
		int i = 1, j = 1;
		while (getline(my_file, red))  //getting row by row from csv file
		{
			string rec = "";
			j = 1;
			for (char ch : red)
			{
				if (ch == ';')
				{
					//pushing cells to the table vector
					if (rec != "")
					{
						table->cells.push_back(new Cell(rec, i, j));
						table->columnWidths[j] = table->widestElementInColumn(j);
						rec = "";
					}
					j++;
				}
				else rec += ch;
			}
			if (rec != "")
			{
				table->cells.push_back(new Cell(rec, i, j));
				table->columnWidths[j] = table->widestElementInColumn(j);
				rec = "";
			}
			i++;
		}
		//closing the used file
		my_file.close();
	}
}
