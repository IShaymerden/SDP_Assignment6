package edu.sdp.filesystem;

public interface Visitor {
    void visit(FileNode file);

    void visit(DirectoryNode directory);

    /** TreeWalker calls this once before each complete operation. */
    default void reset() {
    }

    /** Context belongs to the selected traversal root, not to the stored parent. */
    default void beforeVisit(String relativePath, int depth) {
    }
}
