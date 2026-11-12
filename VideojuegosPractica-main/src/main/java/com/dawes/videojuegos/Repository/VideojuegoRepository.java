package com.dawes.videojuegos.Repository;

import com.dawes.videojuegos.DTO.VideojuegoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


@Repository
public class VideojuegoRepository {
	private static final Logger logger = LoggerFactory.getLogger(VideojuegoRepository.class);
	private final DataSource dataSource;

	public VideojuegoRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}


	public ArrayList<VideojuegoDTO> buscarVideojuegos(String titulo, Integer idDesarrolladora, Integer anioDesde,
	                                                  Integer pegiMax, boolean soloDisponibles, Integer idPlataforma) throws SQLException {
		StringBuilder sql = new StringBuilder(
				"SELECT id, titulo, anio_lanzamiento, pegi, id_desarrolladora, desarrolladora, "
						+ "copias_totales, copias_disponibles "
						+ "FROM v_videojuegos_detalle WHERE 1 = 1");
		ArrayList<Object> parametros = new ArrayList<>();

		if (titulo != null) {
			sql.append(" AND titulo LIKE ?");
			parametros.add("%" + titulo + "%");
		}
		if (idDesarrolladora != null) {
			sql.append(" AND id_desarrolladora = ?");
			parametros.add(idDesarrolladora);
		}
		if (anioDesde != null) {
			sql.append(" AND anio_lanzamiento >= ?");
			parametros.add(anioDesde);
		}
		if (pegiMax != null) {
			sql.append(" AND pegi <= ?");
			parametros.add(pegiMax);
		}
		if (soloDisponibles) {
			sql.append(" AND copias_disponibles > 0");
		}
		if (idPlataforma != null) {
			sql.append(" AND EXISTS (SELECT 1 FROM copias c WHERE c.id_videojuego = v_videojuegos_detalle.id AND c.id_plataforma = ?)");
			parametros.add(idPlataforma);
		}
		sql.append(" ORDER BY anio_lanzamiento DESC, titulo");

		ArrayList<VideojuegoDTO> listaVideojuegos = new ArrayList<>();
		try (Connection connection = dataSource.getConnection();
		     PreparedStatement sentencia = connection.prepareStatement(sql.toString())) {

			for (int i = 0; i < parametros.size(); i++) {
				sentencia.setObject(i + 1, parametros.get(i));
			}
			logger.debug("Query a ejecutar: {}", sentencia);

			try (ResultSet resultado = sentencia.executeQuery()) {
				while (resultado.next()) {
					listaVideojuegos.add(mapearVideojuego(resultado));
				}
			}
		}
		return listaVideojuegos;
	}

	public boolean existeTitulo(String titulo) throws SQLException {
		String sql = "SELECT COUNT(*) FROM videojuegos WHERE LOWER(titulo) = LOWER(?)";
		try (Connection connection = dataSource.getConnection();
		     PreparedStatement sentencia = connection.prepareStatement(sql)) {
			sentencia.setString(1, titulo);
			logger.debug("Query a ejecutar: {}", sentencia);
			try (ResultSet resultado = sentencia.executeQuery()) {
				return resultado.next() && resultado.getInt(1) > 0;
			}
		}
	}

	public int insertarVideojuego(VideojuegoDTO videojuego) throws SQLException {
		String sql = "INSERT INTO videojuegos (titulo, anio_lanzamiento, pegi, id_desarrolladora) VALUES (?, ?, ?, ?)";

		try (Connection connection = dataSource.getConnection();
		     PreparedStatement sentencia = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			sentencia.setString(1, videojuego.getTitulo());
			sentencia.setInt(2, videojuego.getAnioLanzamiento());
			sentencia.setInt(3, videojuego.getPegi());
			sentencia.setInt(4, videojuego.getIdDesarrolladora());
			logger.debug("Query a ejecutar: {}", sentencia);
			sentencia.executeUpdate();

			try (ResultSet claves = sentencia.getGeneratedKeys()) {
				if (claves.next()) {
					return claves.getInt(1);
				}
			}
		}
		throw new SQLException("La base de datos no ha devuelto el id del videojuego insertado");
	}

	private VideojuegoDTO mapearVideojuego(ResultSet resultado) throws SQLException {
		return new VideojuegoDTO(
				resultado.getInt("id"),
				resultado.getString("titulo"),
				resultado.getInt("anio_lanzamiento"),
				resultado.getInt("pegi"),
				resultado.getInt("id_desarrolladora"),
				resultado.getString("desarrolladora"),
				resultado.getInt("copias_totales"),
				resultado.getInt("copias_disponibles")
		);
	}
}
