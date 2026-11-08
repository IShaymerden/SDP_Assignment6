package edu.sdp.filesystem;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class DirectoryNode extends AbstractNode {
    private final Map<String, AbstractNode> children = new LinkedHashMap<>();

    public DirectoryNode(String name) {
        super(name);
    }

    public DirectoryNode addChild(AbstractNode child) {
        Objects.requireNonNull(child, "Child must not be null");
        validateAttachment(child);
        children.put(child.getName(), child);
        child.setParent(this);
        return this;
    }

    private void validateAttachment(AbstractNode child) {
        if (children.containsKey(child.getName())) {
            throw new IllegalArgumentException("Duplicate sibling name: " + child.getName());
        }
        if (child.getParent() != null) {
            throw new IllegalArgumentException("Child already belongs to a directory");
        }
        for (DirectoryNode ancestor = this; ancestor != null; ancestor = ancestor.getParent()) {
            if (ancestor == child) {
                throw new IllegalArgumentException("Attachment would create a cycle");
            }
        }
    }

    public boolean removeChild(String name) {
        Objects.requireNonNull(name, "Name must not be null");
        AbstractNode removed = children.remove(name);
        if (removed == null) {
            return false;
        }
        removed.setParent(null);
        return true;
    }

    @Override
    public List<Element> getChildren() {
        return List.copyOf(children.values());
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
