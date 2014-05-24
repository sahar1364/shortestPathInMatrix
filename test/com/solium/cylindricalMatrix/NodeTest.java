package com.solium.cylindricalMatrix;

import org.junit.Test;

import com.solium.cylindricalMatrix.Node;
/**
 * Tests Node constructor
 */
public class NodeTest {
    @Test (expected = IllegalArgumentException.class)
    public void IdShouldContainRowAndColumnSeparatedByComma() {
        new Node("randomId");
    }
}
