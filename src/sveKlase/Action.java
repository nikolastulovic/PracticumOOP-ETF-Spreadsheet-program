package sveKlase;

import java.util.Stack;

public class Action {
	private Stack<String> redo=new Stack<>();//stack of actions that can be redone
	private Stack<String> undo=new Stack<>();//stack of actions that can be undone
	
	//the first time some action is performed
	public void pushAction(String nameofAction, String oldValue, String newValue, int row, int col)
	{
		//edit-oldValue-newValue-rowIndex-columnIndex
		//format-oldFormat-newFormat-rowIndex-columnIndex
		undo.push(nameofAction + "-" + ((oldValue!="")?oldValue:"`") + "-" + newValue + "-" + Integer.toString(row) + "-" + Integer.toString(col));
		while (!redo.empty()) redo.pop();
	}
	//undo the last action
	public String getActionUndo()
	{
		if (!undo.empty())
		{
			String act = undo.peek();
			undo.pop();
			redo.push(act);
			return act;
		}
		return "";//there is nothing to undo
	}
	//redo the last undone action
	public String getActionRedo()
	{
		if (!redo.empty())
		{
			String act = redo.peek();
			redo.pop();
			undo.push(act);
			return act;
		}
		return "";//there is nothing to redo
	}
}
