package com.dawes.videojuegos.Repository;

import com.dawes.videojuegos.DTO.ContadoresInicioDTO;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Repository
public class InicioRepository {
	private final DataSource dataSource;

	public InicioRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public ContadoresInicioDTO obtenerContadores(LocalDate hoy) throws SQLException {
		String sql = "SELECT "
				+ "(SELECT COUNT(*) FROM videojuegos) AS videojuegos, "
				+ "(SELECT COUNT(*) FROM socios WHERE activo = 1) AS socios_activos, "
				+ "(SELECT COUNT(*) FROM prestamos WHERE fecha_devolucion IS NULL) AS prestamos_abiertos, "
				+ "(SELECT COUNT(*) FROM copias WHERE estado = 'DAÑADO') AS copias_danadas, "
				+ "(SELECT COUNT(*) FROM prestamos WHERE fecha_devolucion IS NULL AND fecha_limite < ?) AS prestamos_vencidos";

		try (Connection connection = dataSource.getConnection();
		     PreparedStatement sentencia = connection.prepareStatement(sql)) {

			sentencia.setDate(1, Date.valueOf(hoy));
			try (ResultSet resultado = sentencia.executeQuery()) {
				resultado.next();
				return new ContadoresInicioDTO(
						resultado.getInt("videojuegos"),
						resultado.getInt("socios_activos"),
						resultado.getInt("prestamos_abiertos"),
						resultado.getInt("prestamos_vencidos"),
							resultado.getInt("copias_danadas")
				);
			}
		}
	}
}
