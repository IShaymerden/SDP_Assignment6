package edu.sdp.filesystem;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TotalSizeVisitorTest {
    private final TreeWalker walker = new TreeWalker();
    private final TotalSizeVisitor visitor = new TotalSizeVisitor();

    @Test
    void sumsFilesAtThreeDifferentDepths() {
        walker.walk(TestTree.create(), visitor);
        assertEquals(1920, visitor.getTotalSize());
    }

    @Test
    void emptyDirectoryHasZeroSize() {
        walker.walk(new DirectoryNode("empty"), visitor);
        assertEquals(0, visitor.getTotalSize());
    }

    @Test
    void supportsSingleFileAndLongByteSizes() {
        walker.walk(new FileNode("large.bin", 3_000_000_000L), visitor);
        assertEquals(3_000_000_000L, visitor.getTotalSize());
    }

    @Test
    void overflowFailsInsteadOfReturningNegativeTotal() {
        DirectoryNode root = new DirectoryNode("root");
        root.addChild(new FileNode("huge", Long.MAX_VALUE));
        root.addChild(new FileNode("one", 1));
        assertThrows(ArithmeticException.class, () -> walker.walk(root, visitor));
        walker.walk(new FileNode("small", 4), visitor);
        assertEquals(4, visitor.getTotalSize());
    }
}
