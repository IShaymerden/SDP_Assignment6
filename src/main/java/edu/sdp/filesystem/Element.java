package edu.sdp.filesystem;

import java.util.List;

public sealed interface Element permits AbstractNode {
    String getName();

    List<Element> getChildren();

    void accept(Visitor visitor);
}
