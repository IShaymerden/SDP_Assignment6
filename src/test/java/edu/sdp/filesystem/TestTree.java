package edu.sdp.filesystem;

final class TestTree {
    private TestTree() {
    }

    static DirectoryNode create() {
        DirectoryNode root = new DirectoryNode("test-root");
        DirectoryNode alpha = new DirectoryNode("alpha");
        DirectoryNode deep = new DirectoryNode("deep");
        deep.addChild(new FileNode("report.txt", 512));
        alpha.addChild(new FileNode("gamma.bin", 1024));
        alpha.addChild(deep);
        alpha.addChild(new FileNode("beta.txt", 256));
        root.addChild(new FileNode("zeta.txt", 128));
        root.addChild(alpha);
        root.addChild(new DirectoryNode("empty"));
        return root;
    }
}
