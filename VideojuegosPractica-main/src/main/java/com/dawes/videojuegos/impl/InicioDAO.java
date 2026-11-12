package com.dawes.videojuegos.impl;

import com.dawes.videojuegos.DTO.ContadoresInicioDTO;
import com.dawes.videojuegos.Repository.InicioRepository;
import com.dawes.videojuegos.interfaz.IInicioDAO;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.time.LocalDate;

@Repository
public class InicioDAO implements IInicioDAO {
	private final InicioRepository inicioRepository;

	public InicioDAO(InicioRepository inicioRepository) {
		this.inicioRepository = inicioRepository;
	}

	@Override
	public ContadoresInicioDTO obtenerContadores(LocalDate hoy) throws SQLException {
		return inicioRepository.obtenerContadores(hoy);
	}
}
