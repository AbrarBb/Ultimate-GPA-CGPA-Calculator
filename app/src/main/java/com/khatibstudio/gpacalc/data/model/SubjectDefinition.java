package com.khatibstudio.gpacalc.data.model;

/**
 * Immutable definition of a subject within an education board's official curriculum.
 * Used by {@link SubjectRepository} to auto-populate subject lists.
 */
public final class SubjectDefinition {

    private final String name;
    private final boolean compulsory;
    private final boolean optionalFourth;

    public SubjectDefinition(String name, boolean compulsory, boolean optionalFourth) {
        this.name = name;
        this.compulsory = compulsory;
        this.optionalFourth = optionalFourth;
    }

    public String getName() { return name; }
    public boolean isCompulsory() { return compulsory; }
    public boolean isOptionalFourth() { return optionalFourth; }
}
