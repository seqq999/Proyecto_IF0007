package cr.ac.ucr.dds3.paraiso.db_proyecto.model.data;

public record QueryDefinition(String title, String description, String sql) {
    @Override
    public String toString() {
        return title;
    }
}
