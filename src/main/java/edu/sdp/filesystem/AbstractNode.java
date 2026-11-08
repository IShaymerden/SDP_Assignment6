package edu.sdp.filesystem;

import java.util.List;
import java.util.Objects;

/** Shared naming and ownership; only the two supported node types can extend it. */
public abstract sealed class AbstractNode implements Element permits FileNode, DirectoryNode {
    private final String name;
    private DirectoryNode parent;

    protected AbstractNode(String name) {
        Objects.requireNonNull(name, "Name must not be null");
        if (name.isBlank() || name.equals(".") || name.equals("..")
                || name.contains("/") || name.contains("\\")) {
            throw new IllegalArgumentException("Name must be a non-blank path segment");
        }
        this.name = name;
    }

    @Override
    public final String getName() {
        return name;
    }

    public final DirectoryNode getParent() {
        return parent;
    }

    // Package-private: callers change ownership through DirectoryNode only.
    final void setParent(DirectoryNode parent) {
        this.parent = parent;
    }

    @Override
    public List<Element> getChildren() {
        return List.of();
    }
}
