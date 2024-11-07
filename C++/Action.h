#ifndef _Action_h_
#define _Action_h_
#include <string>
#include <stack>

using namespace std;

class Action
{
public:
	//the first time some action is performed
	void pushAction(string nameofAction, string oldValue, string newValue, int row, int col);
	//undo the last action
	string getActionUndo();
	//redo the last undone action
	string getActionRedo();
private:
	stack<string> redo;//stack of actions that can be redone
	stack<string> undo;//stack of actions that can be undone
};

#endif // !_Action_h_
