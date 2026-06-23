package util;

import java.util.Stack;

public class UndoManager {
    private Stack<String> actions = new Stack<>();
    public void saveAction(String action) {
        actions.push(action);
    }

    public void undo() {
        if (!actions.isEmpty()) {
            System.out.println("Undo: " + actions.pop());
        }
    }

}