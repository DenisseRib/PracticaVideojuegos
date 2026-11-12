package com.dawes.videojuegos.Repository;

import com.dawes.videojuegos.DTO.CopiaDTO;
import com.dawes.videojuegos.DTO.DesplegableDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Repository
public class CopiasRepository {
	private static final Logger logger = LoggerFactory.getLogger(CopiasRepository.class);
	private final DataSource dataSource;

	public CopiasRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public ArrayList<DesplegableDTO> listarDisponiblesDesplegable() throws SQLException {
		String sql = "SELECT c.id, CONCAT(v.titulo, ' (', pl.nombre, ') - ', c.codigo_inventario) AS descripcion "
				+ "FROM copias c "
				+ "JOIN videojuegos v ON v.id = c.id_videojuego "
				+ "JOIN plataformas pl ON pl.id = c.id_plataforma "
				+ "WHERE c.activa = 1 "
				+ "AND NOT EXISTS (SELECT 1 FROM prestamos p WHERE p.id_copia = c.id AND p.fecha_devolucion IS NULL) "
				+ "ORDER BY v.titulo, pl.nombre, c.codigo_inventario";
		ArrayList<DesplegableDTO> listaCopias = new ArrayList<>();

		try (Connection connection = dataSource.getConnection();
		     PreparedStatement sentencia = connection.prepareStatement(sql);
		     ResultSet resultado = sentencia.executeQuery()) {

			while (resultado.next()) {
				listaCopias.add(new DesplegableDTO(resultado.getInt("id"), resultado.getString("descripcion")));
			}
		}
		return listaCopias;
	}

	public CopiaDTO buscarCopia(int idCopia) throws SQLException {
		String sql = "SELECT c.id, c.codigo_inventario, v.titulo, v.anio_lanzamiento, v.pegi, "
				+ "pl.nombre AS plataforma, c.estado, c.activa, "
				+ "(SELECT COUNT(*) FROM prestamos p WHERE p.id_copia = c.id AND p.fecha_devolucion IS NULL) AS prestamos_abiertos "
				+ "FROM copias c "
				+ "JOIN videojuegos v ON v.id = c.id_videojuego "
				+ "JOIN plataformas pl ON pl.id = c.id_plataforma "
				+ "WHERE c.id = ?";

		try (Connection connection = dataSource.getConnection();
		     PreparedStatement sentencia = connection.prepareStatement(sql)) {
			sentencia.setInt(1, idCopia);
			logger.debug("Query a ejecutar: {}", sentencia);

			try (ResultSet resultado = sentencia.executeQuery()) {
				if (!resultado.next()) {
					return null;
				}
				return mapearCopia(resultado);
			}
		}
	}

	public ArrayList<CopiaDTO> listarCopias(String titulo, Integer idPlataforma, String estado) throws SQLException {
		StringBuilder sql = new StringBuilder("SELECT c.id, c.codigo_inventario, v.titulo, v.anio_lanzamiento, v.pegi, "
				+ "pl.nombre AS plataforma, c.estado, c.activa, "
				+ "(SELECT COUNT(*) FROM prestamos p WHERE p.id_copia = c.id AND p.fecha_devolucion IS NULL) AS prestamos_abiertos "
				+ "FROM copias c "
				+ "JOIN videojuegos v ON v.id = c.id_videojuego "
				+ "JOIN plataformas pl ON pl.id = c.id_plataforma "
				+ "WHERE 1 = 1");
		ArrayList<Object> parametros = new ArrayList<>();

		if (titulo != null) {
			sql.append(" AND v.titulo LIKE ?");
			parametros.add("%" + titulo + "%");
		}
		if (idPlataforma != null) {
			sql.append(" AND c.id_plataforma = ?");
			parametros.add(idPlataforma);
		}
		if (estado != null) {
			sql.append(" AND c.estado = ?");
			parametros.add(estado);
		}
		sql.append(" ORDER BY v.titulo, pl.nombre, c.codigo_inventario");

		ArrayList<CopiaDTO> listaCopias = new ArrayList<>();
		try (Connection connection = dataSource.getConnection();
		     PreparedStatement sentencia = connection.prepareStatement(sql.toString())) {
			for (int i = 0; i < parametros.size(); i++) {
				sentencia.setObject(i + 1, parametros.get(i));
			}
			logger.debug("Query a ejecutar: {}", sentencia);
			try (ResultSet resultado = sentencia.executeQuery()) {
				while (resultado.next()) {
					listaCopias.add(mapearCopia(resultado));
				}
			}
		}
		return listaCopias;
	}

	private CopiaDTO mapearCopia(ResultSet resultado) throws SQLException {
		return new CopiaDTO(
				resultado.getInt("id"),
				resultado.getString("codigo_inventario"),
				resultado.getString("titulo"),
				resultado.getInt("anio_lanzamiento"),
				resultado.getInt("pegi"),
				resultado.getString("plataforma"),
				resultado.getString("estado"),
				resultado.getBoolean("activa"),
				resultado.getInt("prestamos_abiertos"));
	}
}
