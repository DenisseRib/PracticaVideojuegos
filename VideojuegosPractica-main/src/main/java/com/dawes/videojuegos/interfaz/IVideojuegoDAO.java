package com.dawes.videojuegos.interfaz;

import com.dawes.videojuegos.DTO.VideojuegoDTO;

import java.sql.SQLException;
import java.util.ArrayList;

public interface IVideojuegoDAO {
	ArrayList<VideojuegoDTO> buscarVideojuegos(String titulo, Integer idDesarrolladora, Integer anioDesde,
	                                           Integer pegiMax, boolean soloDisponibles, Integer idPlataforma) throws SQLException;

	boolean existeTitulo(String titulo) throws SQLException;

	int insertarVideojuego(VideojuegoDTO videojuego) throws SQLException;
}
