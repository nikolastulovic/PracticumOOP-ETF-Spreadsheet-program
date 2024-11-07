#include "Action.h"

void Action::pushAction(string nameofAction, string oldValue, string newValue, int row, int col)
{
	//edit-oldValue-newValue-rowIndex-columnIndex
	//format-oldFormat-newFormat-rowIndex-columnIndex
	undo.push(nameofAction + "-" + ((oldValue!="")?oldValue:"`") + "-" + newValue + "-" + to_string(row) + "-" + to_string(col));
	while (!redo.empty()) redo.pop();
}

string Action::getActionUndo()
{
	if (!undo.empty())
	{
		string act = undo.top();
		undo.pop();
		redo.push(act);
		return act;
	}
	return "";//there is nothing to undo
}
string Action::getActionRedo()
{
	if (!redo.empty())
	{
		string act = redo.top();
		redo.pop();
		undo.push(act);
		return act;
	}
	return "";//there is nothing to redo
}
