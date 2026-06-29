package cr.ac.ucr.dds3.paraiso.db_proyecto.model.data;

public record ForeignKeyOption(Object value, String label) {
    @Override
    public String toString() {
        return label;
    }
}
