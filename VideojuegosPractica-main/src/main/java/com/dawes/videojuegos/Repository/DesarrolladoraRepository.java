package com.dawes.videojuegos.Repository;

import com.dawes.videojuegos.DTO.DesplegableDTO;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Repository
public class DesarrolladoraRepository {
	private final DataSource dataSource;

	public DesarrolladoraRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public ArrayList<DesplegableDTO> listarDesplegable() throws SQLException {
		String sql = "SELECT id, nombre FROM desarrolladoras ORDER BY nombre";
		ArrayList<DesplegableDTO> listaDesarrolladoras = new ArrayList<>();

		try (Connection connection = dataSource.getConnection();
		     PreparedStatement sentencia = connection.prepareStatement(sql);
		     ResultSet resultado = sentencia.executeQuery()) {

			while (resultado.next()) {
				listaDesarrolladoras.add(new DesplegableDTO(resultado.getInt("id"), resultado.getString("nombre")));
			}
		}
		return listaDesarrolladoras;
	}
}
