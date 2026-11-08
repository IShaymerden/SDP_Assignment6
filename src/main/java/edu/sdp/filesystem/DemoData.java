package edu.sdp.filesystem;

public final class DemoData {
    private DemoData() {
    }

    public static DirectoryNode createTree() {
        DirectoryNode root = new DirectoryNode("coursework");
        DirectoryNode documents = new DirectoryNode("documents");
        DirectoryNode archive = new DirectoryNode("archive");
        archive.addChild(new FileNode("draft.txt", 300));
        documents.addChild(new FileNode("notes.txt", 1200));
        documents.addChild(new FileNode("report.pdf", 4096));
        documents.addChild(archive);

        DirectoryNode source = new DirectoryNode("src");
        source.addChild(new FileNode("Main.java", 850));
        source.addChild(new FileNode("Visitor.java", 450));

        root.addChild(new FileNode("README.txt", 600));
        root.addChild(documents);
        root.addChild(source);
        root.addChild(new DirectoryNode("empty"));
        root.addChild(new FileNode("todo.txt", 0));
        return root;
    }
}
