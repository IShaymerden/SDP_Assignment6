package edu.sdp.filesystem;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SearchVisitorTest {
    private final TreeWalker walker = new TreeWalker();

    @Test
    void combinesFilenameAndInclusiveSizeConstraints() {
        SearchVisitor visitor = new SearchVisitor(name -> name.endsWith(".txt"), 256);
        walker.walk(TestTree.create(), visitor);
        assertEquals(List.of("alpha/beta.txt", "alpha/deep/report.txt"), visitor.getMatches());
    }

    @Test
    void returnsLexicographicPathsRegardlessOfInsertionOrder() {
        SearchVisitor visitor = new SearchVisitor(name -> true, 0);
        walker.walk(TestTree.create(), visitor);
        assertEquals(List.of("alpha/beta.txt", "alpha/deep/report.txt", "alpha/gamma.bin", "zeta.txt"),
                visitor.getMatches());
    }

    @Test
    void pathsAreRelativeToChosenSubtreeEvenWhenItHasAParent() {
        DirectoryNode outside = new DirectoryNode("outside");
        DirectoryNode subtree = new DirectoryNode("chosen");
        subtree.addChild(new DirectoryNode("nested").addChild(new FileNode("answer.txt", 42)));
        outside.addChild(subtree);
        SearchVisitor visitor = new SearchVisitor(name -> name.equals("answer.txt"), 42);
        walker.walk(subtree, visitor);
        assertEquals(List.of("nested/answer.txt"), visitor.getMatches());
    }

    @Test
    void filenameCriterionDoesNotInspectDirectoryNames() {
        DirectoryNode root = new DirectoryNode("root");
        root.addChild(new DirectoryNode("match.txt").addChild(new FileNode("other.bin", 99)));
        SearchVisitor visitor = new SearchVisitor(name -> name.endsWith(".txt"), 0);
        walker.walk(root, visitor);
        assertTrue(visitor.getMatches().isEmpty());
    }

    @Test
    void emptyAndNoMatchResultsAreEmpty() {
        SearchVisitor visitor = new SearchVisitor(name -> name.endsWith(".pdf"), 0);
        walker.walk(TestTree.create(), visitor);
        assertTrue(visitor.getMatches().isEmpty());
        walker.walk(new DirectoryNode("empty"), visitor);
        assertTrue(visitor.getMatches().isEmpty());
    }

    @Test
    void zeroSizeFileMatchesZeroThresholdAndRootPathIsDot() {
        SearchVisitor visitor = new SearchVisitor(name -> name.equals("zero.txt"), 0);
        walker.walk(new FileNode("zero.txt", 0), visitor);
        assertEquals(List.of("."), visitor.getMatches());
    }

    @Test
    void resultsAreImmutableSnapshots() {
        SearchVisitor visitor = new SearchVisitor(name -> true, 0);
        walker.walk(TestTree.create(), visitor);
        List<String> saved = visitor.getMatches();
        assertThrows(UnsupportedOperationException.class, () -> saved.add("fake"));
        walker.walk(new DirectoryNode("empty"), visitor);
        assertEquals(4, saved.size());
        assertTrue(visitor.getMatches().isEmpty());
    }

    @Test
    void rejectsInvalidSearchConfiguration() {
        assertThrows(IllegalArgumentException.class, () -> new SearchVisitor(name -> true, -1));
        assertThrows(NullPointerException.class, () -> new SearchVisitor(null, 0));
    }
}
