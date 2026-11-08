package edu.sdp.filesystem;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatisticsVisitorTest {
    private final TreeWalker walker = new TreeWalker();
    private final StatisticsVisitor visitor = new StatisticsVisitor();

    @Test
    void countsAllFilesDirectoriesAndDepth() {
        walker.walk(TestTree.create(), visitor);
        assertStatistics(4, 4, 3);
    }

    @Test
    void emptyRootStillCountsAsADirectoryAtDepthZero() {
        walker.walk(new DirectoryNode("empty"), visitor);
        assertStatistics(0, 1, 0);
    }

    @Test
    void deepestEmptyDirectoryContributesToMaximumDepth() {
        DirectoryNode root = new DirectoryNode("root");
        root.addChild(new DirectoryNode("a").addChild(new DirectoryNode("b")));
        walker.walk(root, visitor);
        assertStatistics(0, 3, 2);
    }

    @Test
    void fileRootHasDepthZeroAndNoDirectories() {
        walker.walk(new FileNode("single", 7), visitor);
        assertStatistics(1, 0, 0);
    }

    @Test
    void subtreeDepthStartsAtZeroRegardlessOfStoredParent() {
        DirectoryNode parent = new DirectoryNode("parent");
        DirectoryNode child = new DirectoryNode("child");
        child.addChild(new FileNode("item", 7));
        parent.addChild(child);
        walker.walk(child, visitor);
        assertStatistics(1, 1, 1);
    }

    private void assertStatistics(long files, long directories, int depth) {
        assertAll(() -> assertEquals(files, visitor.getFileCount()),
                () -> assertEquals(directories, visitor.getDirectoryCount()),
                () -> assertEquals(depth, visitor.getMaximumDepth()));
    }
}
