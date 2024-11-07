#include "sveKlase_Formula.h"

JNIEXPORT jint JNICALL Java_sveKlase_Formula_getPrecedence
(JNIEnv*, jobject, jchar op)
{
	if (op == '*' || op == '/')
		return 2;  //Higher precedence for multiplication and division
	if (op == '+' || op == '-')
		return 1;  //Lower precedence for addition and subtraction
	return 0;
}