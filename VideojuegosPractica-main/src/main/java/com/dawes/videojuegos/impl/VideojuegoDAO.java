package com.dawes.videojuegos.impl;

import com.dawes.videojuegos.DTO.VideojuegoDTO;
import com.dawes.videojuegos.Repository.VideojuegoRepository;
import com.dawes.videojuegos.interfaz.IVideojuegoDAO;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.ArrayList;

@Repository
public class VideojuegoDAO implements IVideojuegoDAO {
	private final VideojuegoRepository videojuegoRepository;

	public VideojuegoDAO(VideojuegoRepository videojuegoRepository) {
		this.videojuegoRepository = videojuegoRepository;
	}

	@Override
	public ArrayList<VideojuegoDTO> buscarVideojuegos(String titulo, Integer idDesarrolladora, Integer anioDesde,
	                                                  Integer pegiMax, boolean soloDisponibles, Integer idPlataforma) throws SQLException {
		return videojuegoRepository.buscarVideojuegos(titulo, idDesarrolladora, anioDesde, pegiMax, soloDisponibles, idPlataforma);
	}

	@Override
	public boolean existeTitulo(String titulo) throws SQLException {
		return videojuegoRepository.existeTitulo(titulo);
	}

	@Override
	public int insertarVideojuego(VideojuegoDTO videojuego) throws SQLException {
		return videojuegoRepository.insertarVideojuego(videojuego);
	}
}
