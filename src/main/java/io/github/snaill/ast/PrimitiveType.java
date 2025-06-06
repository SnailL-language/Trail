package io.github.snaill.ast;

import java.util.Objects;

public class PrimitiveType extends Type {
    private final String name;
    private final Identifier identifierNode; // Represents the identifier used for a custom type, null for built-in types.

    // Constructor for built-in types (e.g., "i32", "string")
    public PrimitiveType(String name) {
        super(); // Call to AbstractNode/Type constructor
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Type name cannot be null or empty for built-in type.");
        }
        this.name = name;
        this.identifierNode = null;
    }

    // Constructor for custom types, taking the Identifier node that names the type.
    // The AST builder is responsible for setting the source info and enclosing scope
    // on this PrimitiveType node itself. The passed 'identifierNode' will have its own
    // source info and scope, relevant for resolving that identifier.
    public PrimitiveType(Identifier identifierNode) {
        super(); // Call to AbstractNode/Type constructor
        if (identifierNode == null) {
            throw new IllegalArgumentException("Identifier node cannot be null for custom type.");
        }
        this.identifierNode = identifierNode;
        this.name = identifierNode.getName(); // Derive name from the identifier
    }

    public String getName() {
        return name;
    }

    /**
     * Returns the Identifier node if this is a custom type, otherwise null.
     * This Identifier node will have its enclosingScope set, which is crucial
     * for resolving the type name during semantic checks.
     */
    public Identifier getIdentifierNode() {
        return identifierNode;
    }

    public boolean isCustomType() {
        return identifierNode != null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PrimitiveType that = (PrimitiveType) obj;
        // Type equality is primarily based on the resolved name.
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        // Consistent with equals, primarily based on name.
        return Objects.hash(name);
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        try {
            return visitor.visit(this);
        } catch (Exception e) { // Catch a more general exception
            throw new RuntimeException("Error during AST Visitor operation on PrimitiveType: " + e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
