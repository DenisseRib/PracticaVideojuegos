package com.dawes.videojuegos.impl;

import com.dawes.videojuegos.DTO.DesplegableDTO;
import com.dawes.videojuegos.Repository.PlataformaRepository;
import com.dawes.videojuegos.interfaz.IPlataformaDAO;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.ArrayList;

@Repository
public class PlataformaDAO implements IPlataformaDAO {
	private final PlataformaRepository plataformaRepository;

	public PlataformaDAO(PlataformaRepository plataformaRepository) {
		this.plataformaRepository = plataformaRepository;
	}

	@Override
	public ArrayList<DesplegableDTO> listarDesplegable() throws SQLException {
		return plataformaRepository.listarDesplegable();
	}
}
