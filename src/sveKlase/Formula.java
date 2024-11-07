package sveKlase;

import java.util.Stack;

import greske.GDeljenjeNulom;

public class Formula {
	
	static {
		System.loadLibrary("DLLJava");
	}
	
	private String expression;
	
	private boolean isOperator(char c)
	{
		return (c == '+' || c == '-' || c == '*' || c == '/');
	}
	private double performOperation(char operation, double operand1, double operand2) throws GDeljenjeNulom
	{
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
	            throw new GDeljenjeNulom();
	        }
	        return operand1 / operand2;
	    default:
	        return 0.0;
	    }
	}
	private native int getPrecedence(char op);
	
	public String calculate()
	{
		Stack<Double> operands=new Stack<>();
	    Stack<Character> operators=new Stack<>();
	    Stack<Character> parentheses=new Stack<>();//Stack to track opening parentheses
	    boolean previousWasOperator = false;

	    for (int i = 1; i < expression.length(); ++i) {
	        char c = expression.charAt(i);

	        //ignore whitespace characters
	        if (c == ' ')
	            continue;
	        //if the character is a letter, extract the entire cell name
	        else if (Character.isLetter(c)) {
	            int j = i;
	            String cellName="";

	            while (j < expression.length() && (Character.isLetter(expression.charAt(j)) || Character.isDigit(expression.charAt(j)))) {
	                cellName += expression.charAt(j);
	                j++;
	            }

	            i = j - 1;  //update the current index

	            //check if the cell name exists in the table, get the value of the cell and push it to the operands stack
	            Table tab = Table.getInstance();
	            if (tab.cellExists(cellName))
	            {
	                operands.push(tab.getCellNumValue(cellName));
	            }
	            else operands.push(0.0);
	        }
	        //if the character is a digit or decimal point, extract the entire number
	        else if (Character.isDigit(c) || c == '.') {
	            int j = i;
	            String numStr = "";

	            while (j < expression.length() && (Character.isDigit(expression.charAt(j)) || expression.charAt(j) == '.')) {
	                numStr += expression.charAt(j);
	                j++;
	            }

	            i = j - 1;  //update the current index

	            //convert the number string to double and push it to the operands stack
	            operands.push(Double.parseDouble(numStr));
	        }
	        else if (c == '(') {
	            //if the character is an opening parenthesis, push it to the operators stack
	            operators.push(c);
	            parentheses.push(c);
	        }
	        else if (c == ')') {
	            //if the character is a closing parenthesis, perform operations until an opening parenthesis is found
	            while (!operators.empty() && operators.peek() != '(' && getPrecedence(operators.peek()) >= getPrecedence(c)) {
	                double operand2 = operands.peek();
	                operands.pop();

	                double operand1 = operands.peek();
	                operands.pop();

	                char op = operators.peek();
	                operators.pop();

	                double result;
	                try { result = performOperation(op, operand1, operand2); }
	                catch (GDeljenjeNulom e) { return "ERROR"; }// error value caused by division by 0

	                operands.push(result);
	            }

	            //Remove the opening parenthesis from the operators stack
	            if (!operators.empty() && operators.peek() == '(') {
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
	            while (!operators.empty() && isOperator(operators.peek()) && getPrecedence(operators.peek()) >= getPrecedence(c)) {
	                double operand2 = operands.peek();
	                operands.pop();

	                double operand1 = operands.peek();
	                operands.pop();

	                char op = operators.peek();
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
	        double operand2 = operands.peek();
	        operands.pop();

	        double operand1 = operands.peek();
	        operands.pop();

	        char op = operators.peek();
	        operators.pop();

	        double result;
	        try { result = performOperation(op, operand1, operand2); }
	        catch (GDeljenjeNulom e) { return "ERROR"; }

	        operands.push(result);
	    }
	    //handling empty expression
	    if (operands.empty()) return "ERROR"; 

	    //the final result will be at the top of the operands stack
	    return Double.toString(operands.peek());
	}
	

	public Formula(String expr)
	{
		expression=expr;
	}
}
