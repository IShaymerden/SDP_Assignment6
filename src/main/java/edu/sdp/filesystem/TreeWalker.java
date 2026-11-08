package edu.sdp.filesystem;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/** The sole traversal owner. Elements dispatch; visitors perform one-node operations. */
public final class TreeWalker {
    public void walk(Element root, Visitor visitor) {
        Objects.requireNonNull(root, "Root must not be null");
        Objects.requireNonNull(visitor, "Visitor must not be null");
        visitor.reset();
        Deque<Frame> pending = new ArrayDeque<>();
        pending.push(new Frame(root, "", 0));

        while (!pending.isEmpty()) {
            Frame frame = pending.pop();
            visitor.beforeVisit(frame.path().isEmpty() ? "." : frame.path(), frame.depth());
            frame.element().accept(visitor);
            pushChildren(pending, frame);
        }
    }

    private void pushChildren(Deque<Frame> pending, Frame parent) {
        List<Element> children = parent.element().getChildren();
        // Reverse push preserves insertion order in the depth-first walk.
        for (int i = children.size() - 1; i >= 0; i--) {
            Element child = children.get(i);
            String path = parent.path().isEmpty()
                    ? child.getName() : parent.path() + "/" + child.getName();
            pending.push(new Frame(child, path, parent.depth() + 1));
        }
    }

    private record Frame(Element element, String path, int depth) {
    }
}
