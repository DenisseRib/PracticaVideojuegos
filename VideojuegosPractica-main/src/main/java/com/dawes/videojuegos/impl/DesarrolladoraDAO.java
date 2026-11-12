package com.dawes.videojuegos.impl;

import com.dawes.videojuegos.DTO.DesplegableDTO;
import com.dawes.videojuegos.Repository.DesarrolladoraRepository;
import com.dawes.videojuegos.interfaz.IDesarrolladoraDAO;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.ArrayList;

@Repository
public class DesarrolladoraDAO implements IDesarrolladoraDAO {
	private final DesarrolladoraRepository desarrolladoraRepository;

	public DesarrolladoraDAO(DesarrolladoraRepository desarrolladoraRepository) {
		this.desarrolladoraRepository = desarrolladoraRepository;
	}

	@Override
	public ArrayList<DesplegableDTO> listarDesplegable() throws SQLException {
		return desarrolladoraRepository.listarDesplegable();
	}
}
