package com.dawes.videojuegos.service;

import com.dawes.videojuegos.DTO.ContadoresInicioDTO;
import com.dawes.videojuegos.interfaz.IInicioDAO;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;

@Service
public class InicioService {
	private final IInicioDAO inicioDAO;

	public InicioService(IInicioDAO inicioDAO) {
		this.inicioDAO = inicioDAO;
	}

	public ContadoresInicioDTO obtenerContadores() throws SQLException {
		return inicioDAO.obtenerContadores(LocalDate.now());
	}

}
