package io.github.melin.sqlflow.analyzer;

import javax.annotation.concurrent.Immutable;

import static java.util.Objects.requireNonNull;
// zeng: field reference is resolved to resolve field (any relation field, not must be origin column)
@Immutable
public class ResolvedField {
    private final Scope scope;  // zeng: scope where field output
    private final Field field;  // zeng: field
    private final int hierarchyFieldIndex;  // zeng: field index if current sub query scope block unfold
    private final int relationFieldIndex;   // zeng: field index in current scope
    private final boolean local;    // zeng: if field is in current sub query scope block

    public ResolvedField(Scope scope, Field field, int hierarchyFieldIndex, int relationFieldIndex, boolean local) {
        this.scope = requireNonNull(scope, "scope is null");
        this.field = requireNonNull(field, "field is null");
        this.hierarchyFieldIndex = hierarchyFieldIndex;
        this.relationFieldIndex = relationFieldIndex;
        this.local = local;
    }

    public FieldId getFieldId() {
        return FieldId.from(this);
    }

    public Scope getScope() {
        return scope;
    }

    public boolean isLocal() {
        return local;
    }

    public int getHierarchyFieldIndex() {
        return hierarchyFieldIndex;
    }

    public int getRelationFieldIndex() {
        return relationFieldIndex;
    }

    public Field getField() {
        return field;
    }
}
