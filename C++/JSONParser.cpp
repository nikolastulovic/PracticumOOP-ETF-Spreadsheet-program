#include "JSONParser.h"
#include "Cell.h"
#include <fstream>
#include <regex>
#define COLNUM  'Z' - 'A'+ 2 //fixed maximum number of columns

void JSONParser::save()
{
	//opening file
	ofstream my_file;
	my_file.open(filename);
	if (!my_file) {
		throw GNeuspeloOtvaranje();//file opening failed
	}
	else
	{
		//filling the json file
		my_file << "{\"Table\": [\n";
		string output="";
		bool prvi = true;
		for (Cell* c : table->cells)
		{
			if (c->getRow())//header cells dont need to be saved
			{
				if (!prvi) output += ",\n";
				output += "{\"row\":\"";
				output += to_string(c->getRow()) + "\",\"column\":\"";
				output += to_string(c->getColumn()) + "\",\"value\":\"";
				output += c->getValue() + "\",\"format\":\"";
				if (c->cf->getFormat() == "text") output += "text\",\"decimals\":\"0\"}";
				else if (c->cf->getFormat() == "number")
				{
					int brDec = ((CellFormatNumber*)c->cf)->getNumberOfDecimals();
					output += "number\",\"decimals\":\"";
					output += to_string(brDec);
					output += +"\"}";
				}
				else if (c->cf->getFormat() == "date") output += "date\",\"decimals\":\"0\"}";
				if (prvi) prvi = false;
				my_file << output;
				output = "";
			}
		}
		my_file << "\n]}";
		//closing the used file
		my_file.close();
	}
}

void JSONParser::read()
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
	else
	{
		string red;
		getline(my_file, red);//skipping the first row
		int i = 1, j = 1;
		while (getline(my_file, red)) //getting row by row from json file
		{
			if (red != "]}")//last row in json, should be ignored
			{
				regex r(R"(\{\"row\":\"([0-9]+)\",\"column\":\"([0-9]+)\",\"value\":\"(.+)\",\"format\":\"([a-zA-Z]+)\",\"decimals\":\"([0-9]+)\"\})");;
				smatch matches;
				if (regex_search(red, matches, r))
				{
					Cell* c = new Cell(matches[3], stoi(matches[1]), stoi(matches[2]));
					if (matches[4] == "number") c->changeFormat(new CellFormatNumber(stoi(matches[5])));
					else if(matches[4] == "date") c->changeFormat(new CellFormatDate());
					table->cells.push_back(c);
					table->columnWidths[c->column] = table->widestElementInColumn(c->column);
				}
			}
		}
	}
}
