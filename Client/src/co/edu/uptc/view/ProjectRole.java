package co.edu.uptc.view;

public enum ProjectRole {
    ADMIN("Administrador"),
    MEMBER("Integrante");

    private final String label;

    ProjectRole(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}