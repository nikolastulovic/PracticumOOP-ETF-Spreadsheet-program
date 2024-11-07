#include "Formula.h"
#include <stack>
#include "Table.h"

//checking if char in operator
bool Formula::isOperator(char c) {
    return (c == '+' || c == '-' || c == '*' || c == '/');
}
//operation execution
double Formula::performOperation(char operation, double operand1, double operand2) {
    switch (operation) {
    case '+':
        return operand1 + operand2;
    case '-':
        return operand1 - operand2;
    case '*':
        return operand1 * operand2;
    case '/':
        if (operand2 == 0)
        {
            //Error, division by 0
            throw GDeljenjeNulom();
        }
        return operand1 / operand2;
    default:
        return 0.0;
    }
}
//precendence of operations
int Formula::getPrecedence(char op) {
    if (op == '*' || op == '/')
        return 2;  //Higher precedence for multiplication and division
    if (op == '+' || op == '-')
        return 1;  //Lower precedence for addition and subtraction
    return 0;
}
//main function that gets char by char and calculates expression
string Formula::calculate()
{
    stack<double> operands;
    stack<char> operators;
    stack<char> parentheses; //Stack to track opening parentheses
    bool previousWasOperator = false;

    for (int i = 1; i < expression.length(); ++i) {
        char c = expression[i];

        //ignore whitespace characters
        if (c == ' ')
            continue;
        //if the character is a letter, extract the entire cell name
        else if (isalpha(c)) {
            int j = i;
            string cellName;

            while (j < expression.length() && (isalpha(expression[j])|| isdigit(expression[j]))) {
                cellName += expression[j];
                j++;
            }

            i = j - 1;  //update the current index

            //check if the cell name exists in the table, get the value of the cell and push it to the operands stack
            Table* tab = tab->getInstance();
            if (tab->cellExists(cellName))
            {
                operands.push(tab->getCellNumValue(cellName));
            }
            else operands.push(0);
        }
        //if the character is a digit or decimal point, extract the entire number
        else if (isdigit(c) || c == '.') {
            int j = i;
            string numStr = "";

            while (j < expression.length() && (isdigit(expression[j]) || expression[j] == '.')) {
                numStr += expression[j];
                j++;
            }

            i = j - 1;  //update the current index

            //convert the number string to double and push it to the operands stack
            operands.push(stod(numStr));
        }
        else if (c == '(') {
            //if the character is an opening parenthesis, push it to the operators stack
            operators.push(c);
            parentheses.push(c);
        }
        else if (c == ')') {
            //if the character is a closing parenthesis, perform operations until an opening parenthesis is found
            while (!operators.empty() && operators.top() != '(' && getPrecedence(operators.top()) >= getPrecedence(c)) {
                double operand2 = operands.top();
                operands.pop();

                double operand1 = operands.top();
                operands.pop();

                char op = operators.top();
                operators.pop();

                double result;
                try { result = performOperation(op, operand1, operand2); }
                catch (GDeljenjeNulom e) { return "ERROR"; }// error value caused by division by 0

                operands.push(result);
            }

            //Remove the opening parenthesis from the operators stack
            if (!operators.empty() && operators.top() == '(') {
                operators.pop();
            }
            parentheses.pop();
        }
        else if (isOperator(c)) {
            if (previousWasOperator) {
                return "ERROR"; // error value, 2 operators consequently are not allowed
            }
            //if the character is an operator and the operators stack is not empty
            //and the top operator has higher or equal precedence, perform the operation
            while (!operators.empty() && isOperator(operators.top()) && getPrecedence(operators.top()) >= getPrecedence(c)) {
                double operand2 = operands.top();
                operands.pop();

                double operand1 = operands.top();
                operands.pop();

                char op = operators.top();
                operators.pop();

                double result;
                try { result = performOperation(op, operand1, operand2); }
                catch (GDeljenjeNulom e) { return "ERROR"; }
                
                operands.push(result);
            }
            previousWasOperator = true;
            //push the current operator to the operators stack
            operators.push(c);
        }
        else {
            return "ERROR"; // error value, the case when an invalid character is encountered
        }
        if (!isOperator(c))
            previousWasOperator = false;
    }
    //if there are still opening parentheses left in the stack, handle the error
    if (!parentheses.empty()) {
        return "ERROR"; // error value
    }
    //check for hanging operators or incomplete expressions
    else if (operators.size() >= operands.size()) {
        return "ERROR"; // error value
    }
    //perform remaining operations that are still in the stacks
    while (!operators.empty()) {
        double operand2 = operands.top();
        operands.pop();

        double operand1 = operands.top();
        operands.pop();

        char op = operators.top();
        operators.pop();

        double result;
        try { result = performOperation(op, operand1, operand2); }
        catch (GDeljenjeNulom e) { return "ERROR"; }

        operands.push(result);
    }
    //handling empty expression
    if (operands.empty()) return "ERROR";

    //the final result will be at the top of the operands stack
    return to_string(operands.top());
}
