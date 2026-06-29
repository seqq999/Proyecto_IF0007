package cr.ac.ucr.dds3.paraiso.db_proyecto.model.data;

import java.util.List;

public record QueryResult(List<String> columnNames, List<Object[]> rows) {
}
