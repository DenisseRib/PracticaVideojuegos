package com.dawes.videojuegos.Repository;

import com.dawes.videojuegos.DTO.PrestamosDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

@Repository
public class PrestamosRepository {
	private static final Logger logger = LoggerFactory.getLogger(PrestamosRepository.class );
	private static final String SQL_SELECT_PRESTAMOS =
			"SELECT p.id, p.id_copia, CONCAT(s.nombre, ' ', s.apellidos) AS socio, "
					+ "v.titulo AS videojuego, pl.nombre AS plataforma, c.codigo_inventario AS codigoCopia, "
					+ "p.fecha_prestamo, p.fecha_limite, p.fecha_devolucion, p.multa, p.renovaciones "
					+ "FROM prestamos p "
					+ "JOIN socios s ON s.id = p.id_socio "
					+ "JOIN copias c ON c.id = p.id_copia "
					+ "JOIN videojuegos v ON v.id = c.id_videojuego "
					+ "JOIN plataformas pl ON pl.id = c.id_plataforma ";

	private final DataSource dataSource;

	public PrestamosRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public ArrayList<PrestamosDTO> listarPrestamos(String socio, String videojuego, Integer idPlataforma, String estado,
	                                               LocalDate fechaDesde, LocalDate hoy) throws SQLException {
		StringBuilder sql = new StringBuilder(SQL_SELECT_PRESTAMOS + "WHERE 1 = 1");
		ArrayList<Object> parametros = new ArrayList<>();

		if (socio != null) {
			sql.append(" AND CONCAT(s.nombre, ' ', s.apellidos) LIKE ?");
			parametros.add("%" + socio + "%");
		}
		if (videojuego != null) {
			sql.append(" AND v.titulo LIKE ?");
			parametros.add("%" + videojuego + "%");
		}
		if (fechaDesde != null) {
			sql.append(" AND p.fecha_prestamo >= ?");
			parametros.add(Date.valueOf(fechaDesde));
		}
		if (idPlataforma != null) {
			sql.append(" AND c.id_plataforma = ?");
			parametros.add(idPlataforma);
		}
		if ("ABIERTOS".equals(estado)) {
			sql.append(" AND p.fecha_devolucion IS NULL");
		} else if ("EN_PLAZO".equals(estado)) {
			sql.append(" AND p.fecha_devolucion IS NULL AND p.fecha_limite >= ?");
			parametros.add(Date.valueOf(hoy));
		} else if ("VENCIDO".equals(estado)) {
			sql.append(" AND p.fecha_devolucion IS NULL AND p.fecha_limite < ?");
			parametros.add(Date.valueOf(hoy));
		} else if ("DEVUELTO".equals(estado)) {
			sql.append(" AND p.fecha_devolucion IS NOT NULL");
		}
		sql.append(" ORDER BY p.fecha_prestamo DESC, p.id DESC");

		ArrayList<PrestamosDTO> lista = new ArrayList<>();
		try (Connection connection = dataSource.getConnection();
		     PreparedStatement sentencia = connection.prepareStatement(sql.toString())) {
			for (int i = 0; i < parametros.size(); i++) {
				sentencia.setObject(i + 1, parametros.get(i));
			}
			logger.debug("Query a ejecutar: {}", sentencia);
			try (ResultSet resultado = sentencia.executeQuery()) {
				while (resultado.next()) {
					lista.add(mapearPrestamo(resultado));
				}
			}
		}
		return lista;
	}

	public PrestamosDTO buscarPrestamo(int idPrestamo) throws SQLException {
		String sql = SQL_SELECT_PRESTAMOS + "WHERE p.id = ?";
		try (Connection connection = dataSource.getConnection();
		     PreparedStatement sentencia = connection.prepareStatement(sql)) {
			sentencia.setInt(1, idPrestamo);
			logger.debug("Query a ejecutar: {}", sentencia);
			try (ResultSet resultado = sentencia.executeQuery()) {
				return resultado.next() ? mapearPrestamo(resultado) : null;
			}
		}
	}

	public int contarPrestamosVencidosSocio(int idSocio, LocalDate hoy) throws SQLException {
		String sql = "SELECT COUNT(*) FROM prestamos WHERE id_socio = ? AND fecha_devolucion IS NULL AND fecha_limite < ?";
		try (Connection connection = dataSource.getConnection();
		     PreparedStatement sentencia = connection.prepareStatement(sql)) {
			sentencia.setInt(1, idSocio);
			sentencia.setDate(2, Date.valueOf(hoy));
			logger.debug("Query a ejecutar: {}", sentencia);
			try (ResultSet resultado = sentencia.executeQuery()) {
				resultado.next();
				return resultado.getInt(1);
			}
		}
	}

	public int insertarPrestamo(int idSocio, int idCopia, LocalDate fechaPrestamo, LocalDate fechaLimite) throws SQLException {
		String sql = "INSERT INTO prestamos (id_socio, id_copia, fecha_prestamo, fecha_limite, fecha_devolucion, multa) "
				+ "VALUES (?, ?, ?, ?, NULL, 0)";
		try (Connection connection = dataSource.getConnection();
		     PreparedStatement sentencia = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			sentencia.setInt(1, idSocio);
			sentencia.setInt(2, idCopia);
			sentencia.setDate(3, Date.valueOf(fechaPrestamo));
			sentencia.setDate(4, Date.valueOf(fechaLimite));
			logger.debug("Query a ejecutar: {}", sentencia);
			sentencia.executeUpdate();

			try (ResultSet claves = sentencia.getGeneratedKeys()) {
				if (claves.next()) {
					return claves.getInt(1);
				}
			}
		}
		throw new SQLException("La base de datos no ha devuelto el id del préstamo insertado");
	}


	public void renovarPrestamo(int idPrestamo, LocalDate nuevaFechaLimite) throws SQLException {
		String sql = "UPDATE prestamos SET fecha_limite = ?, renovaciones = renovaciones + 1 "
				+ "WHERE id = ? AND fecha_devolucion IS NULL";
		try (Connection connection = dataSource.getConnection();
		     PreparedStatement sentencia = connection.prepareStatement(sql)) {
			sentencia.setDate(1, Date.valueOf(nuevaFechaLimite));
			sentencia.setInt(2, idPrestamo);
			logger.debug("Query a ejecutar: {}", sentencia);
			if (sentencia.executeUpdate() != 1) {
				throw new SQLException("El préstamo " + idPrestamo + " no se ha podido renovar (¿ya estaba devuelto?)");
			}
		}
	}

	public void devolverPrestamo(int idPrestamo, int idCopia, LocalDate fechaDevolucion, BigDecimal multa,
	                             boolean copiaDanada) throws SQLException {
		String sqlPrestamo = "UPDATE prestamos SET fecha_devolucion = ?, multa = ? "
				+ "WHERE id = ? AND fecha_devolucion IS NULL";
		String sqlCopia = "UPDATE copias SET estado = 'DAÑADO', activa = 0 WHERE id = ?";

		try (Connection connection = dataSource.getConnection()) {
			connection.setAutoCommit(false);
			try {
				try (PreparedStatement sentenciaPrestamo = connection.prepareStatement(sqlPrestamo)) {
					sentenciaPrestamo.setDate(1, Date.valueOf(fechaDevolucion));
					sentenciaPrestamo.setBigDecimal(2, multa);
					sentenciaPrestamo.setInt(3, idPrestamo);
					logger.debug("Query a ejecutar: {}", sentenciaPrestamo);
					if (sentenciaPrestamo.executeUpdate() != 1) {
						throw new SQLException("El préstamo " + idPrestamo + " no se ha podido actualizar (¿ya estaba devuelto?)");
					}
				}
				if (copiaDanada) {
					try (PreparedStatement sentenciaCopia = connection.prepareStatement(sqlCopia)) {
						sentenciaCopia.setInt(1, idCopia);
						logger.debug("Query a ejecutar: {}", sentenciaCopia);
						sentenciaCopia.executeUpdate();
					}
				}
				connection.commit();
			} catch (SQLException e) {
				connection.rollback();
				throw e;
			} finally {
				connection.setAutoCommit(true);
			}
		}
	}

	private PrestamosDTO mapearPrestamo(ResultSet resultado) throws SQLException {
		Date fechaDevolucion = resultado.getDate("fecha_devolucion"); // puede ser null
		return new PrestamosDTO(
				resultado.getInt("id"),
				resultado.getInt("id_copia"),
				resultado.getString("socio"),
				resultado.getString("videojuego"),
				resultado.getString("plataforma"),
				resultado.getString("codigoCopia"),
				resultado.getDate("fecha_prestamo").toLocalDate(),
				resultado.getDate("fecha_limite").toLocalDate(),
				fechaDevolucion == null ? null : fechaDevolucion.toLocalDate(),
				resultado.getBigDecimal("multa"),
					resultado.getInt("renovaciones")
		);
	}
}
