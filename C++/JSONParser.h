#ifndef _JSONParser_h_
#define _JSONParser_h_
#include "Parser.h"

class JSONParser : public Parser
{
public:
	JSONParser(string file) :Parser(file) {}
	void save();
	void read();
};
#endif // !_JSONParser_h_