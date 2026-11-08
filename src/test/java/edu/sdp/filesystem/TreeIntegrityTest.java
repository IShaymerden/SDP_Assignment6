package edu.sdp.filesystem;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TreeIntegrityTest {
    @Test
    void rejectsNegativeFileSizeButAllowsZero() {
        assertThrows(IllegalArgumentException.class, () -> new FileNode("bad", -1));
        assertEquals(0, new FileNode("zero", 0).getSize());
    }

    @Test
    void rejectsDuplicateSiblingNamesAcrossNodeTypesAtomically() {
        DirectoryNode root = new DirectoryNode("root");
        FileNode file = new FileNode("same", 1);
        DirectoryNode duplicate = new DirectoryNode("same");
        root.addChild(file);
        assertThrows(IllegalArgumentException.class, () -> root.addChild(duplicate));
        assertEquals(List.of(file), root.getChildren());
        assertSame(root, file.getParent());
        assertNull(duplicate.getParent());
    }

    @Test
    void rejectsMultipleParentsForFilesAndDirectories() {
        DirectoryNode first = new DirectoryNode("first");
        DirectoryNode second = new DirectoryNode("second");
        FileNode file = new FileNode("file", 1);
        DirectoryNode directory = new DirectoryNode("directory");
        first.addChild(file).addChild(directory);
        assertThrows(IllegalArgumentException.class, () -> second.addChild(file));
        assertThrows(IllegalArgumentException.class, () -> second.addChild(directory));
        assertTrue(second.getChildren().isEmpty());
        assertSame(first, file.getParent());
        assertSame(first, directory.getParent());
    }

    @Test
    void rejectsSelfCycleWithoutChangingRoot() {
        DirectoryNode root = new DirectoryNode("root");
        assertThrows(IllegalArgumentException.class, () -> root.addChild(root));
        assertTrue(root.getChildren().isEmpty());
        assertNull(root.getParent());
    }

    @Test
    void rejectsAncestorCycleAndPreservesValidNesting() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode child = new DirectoryNode("child");
        DirectoryNode grandchild = new DirectoryNode("grandchild");
        root.addChild(child);
        child.addChild(grandchild);
        assertThrows(IllegalArgumentException.class, () -> grandchild.addChild(root));
        assertNull(root.getParent());
        assertSame(root, child.getParent());
        assertSame(child, grandchild.getParent());
        assertTrue(grandchild.getChildren().isEmpty());
    }

    @Test
    void childCollectionCannotBeEditedOrUsedAsALiveBackdoor() {
        DirectoryNode root = new DirectoryNode("root");
        FileNode file = new FileNode("first", 1);
        root.addChild(file);
        List<Element> snapshot = root.getChildren();
        assertThrows(UnsupportedOperationException.class, snapshot::clear);
        root.addChild(new FileNode("second", 2));
        assertEquals(List.of(file), snapshot);
        assertEquals(2, root.getChildren().size());
    }

    @Test
    void removalAllowsSafeReparentingOfAnIntactSubtree() {
        DirectoryNode first = new DirectoryNode("first");
        DirectoryNode second = new DirectoryNode("second");
        DirectoryNode subtree = new DirectoryNode("subtree");
        FileNode leaf = new FileNode("leaf", 3);
        subtree.addChild(leaf);
        first.addChild(subtree);
        assertTrue(first.removeChild("subtree"));
        assertNull(subtree.getParent());
        assertFalse(first.removeChild("missing"));
        second.addChild(subtree);
        assertSame(second, subtree.getParent());
        assertSame(subtree, leaf.getParent());
    }

    @Test
    void sameNamesAreAllowedInDifferentDirectories() {
        DirectoryNode root = new DirectoryNode("root");
        root.addChild(new DirectoryNode("a").addChild(new FileNode("same", 2)));
        root.addChild(new DirectoryNode("b").addChild(new FileNode("same", 3)));
        SearchVisitor visitor = new SearchVisitor(name -> true, 0);
        new TreeWalker().walk(root, visitor);
        assertEquals(List.of("a/same", "b/same"), visitor.getMatches());
    }

    @Test
    void rejectsInvalidNamesAndNullChildren() {
        for (String name : List.of("", " ", ".", "..", "a/b", "a\\b")) {
            assertThrows(IllegalArgumentException.class, () -> new DirectoryNode(name));
            assertThrows(IllegalArgumentException.class, () -> new FileNode(name, 1));
        }
        assertThrows(NullPointerException.class, () -> new FileNode(null, 1));
        DirectoryNode root = new DirectoryNode("root");
        assertThrows(NullPointerException.class, () -> root.addChild(null));
        assertTrue(root.getChildren().isEmpty());
    }
}
