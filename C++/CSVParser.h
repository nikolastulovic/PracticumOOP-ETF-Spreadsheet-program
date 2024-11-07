#ifndef _CSVParser_h_
#define _CSVParser_h_
#include "Parser.h"

class CSVParser : public Parser
{
public:
	CSVParser(string file):Parser(file){}
	void save();
	void read();
};
#endif // !_CSVParser_h_