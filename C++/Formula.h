#ifndef _Formula_h_
#define _Formula_h_
#include <string>
using namespace std;

class Formula
{
public:
	Formula(const string& expr) :expression(expr) { }
	string calculate();
private:
	bool isOperator(char c);
	double performOperation(char operation, double operand1, double operand2);
	int getPrecedence(char op);
	const string expression;
};
#endif // !_Formula_h_
