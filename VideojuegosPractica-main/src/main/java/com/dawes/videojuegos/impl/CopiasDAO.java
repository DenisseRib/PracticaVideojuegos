package com.dawes.videojuegos.impl;

import com.dawes.videojuegos.DTO.CopiaDTO;
import com.dawes.videojuegos.DTO.DesplegableDTO;
import com.dawes.videojuegos.Repository.CopiasRepository;
import com.dawes.videojuegos.interfaz.ICopiasDAO;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.ArrayList;

@Repository
public class CopiasDAO implements ICopiasDAO {
	private final CopiasRepository copiasRepository;

	public CopiasDAO(CopiasRepository copiasRepository) {
		this.copiasRepository = copiasRepository;
	}

	@Override
	public ArrayList<DesplegableDTO> listarDisponiblesDesplegable() throws SQLException {
		return copiasRepository.listarDisponiblesDesplegable();
	}

	@Override
	public ArrayList<CopiaDTO> listarCopias(String titulo, Integer idPlataforma, String estado) throws SQLException {
		return copiasRepository.listarCopias(titulo, idPlataforma, estado);
	}

	@Override
	public CopiaDTO buscarCopia(int idCopia) throws SQLException {
		return copiasRepository.buscarCopia(idCopia);
	}
}
