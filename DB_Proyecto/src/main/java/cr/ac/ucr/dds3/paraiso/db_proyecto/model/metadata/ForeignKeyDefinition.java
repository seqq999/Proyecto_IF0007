package cr.ac.ucr.dds3.paraiso.db_proyecto.model.metadata;

public record ForeignKeyDefinition(
        String columnName,
        String referencedTable,
        String referencedColumn,
        String displayExpression,
        ReferentialAction onDelete,
        ReferentialAction onUpdate
) {
    public ForeignKeyDefinition(
            String columnName,
            String referencedTable,
            String referencedColumn,
            String displayExpression
    ) {
        this(columnName, referencedTable, referencedColumn, displayExpression,
                ReferentialAction.RESTRICT, ReferentialAction.CASCADE);
    }
}
