#ifndef _Parser_h_
#define _Parser_h_
#include <string>
#include "Table.h"
using namespace std;

class Parser
{
public:
	virtual void save() = 0;
	virtual void read() = 0;
protected:
	Parser(string file) :filename(file){}
	string filename = "savedCSV.csv";
	Table* table=table->getInstance();//should not be freed because its only one instance of that object
};
#endif // !_Parser_h_
