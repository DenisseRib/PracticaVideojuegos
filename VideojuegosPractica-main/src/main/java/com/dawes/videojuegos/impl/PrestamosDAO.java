package com.dawes.videojuegos.impl;


import com.dawes.videojuegos.DTO.PrestamosDTO;

import com.dawes.videojuegos.Repository.PrestamosRepository;

import com.dawes.videojuegos.interfaz.IPrestamosDAO;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

@Repository
public class PrestamosDAO implements IPrestamosDAO {
	private final PrestamosRepository prestamosRepository;

	public PrestamosDAO(PrestamosRepository prestamosRepository) {
		this.prestamosRepository = prestamosRepository;
	}

	@Override
	public ArrayList<PrestamosDTO> listarPrestamos(String socio, String videojuego, Integer idPlataforma, String estado,
	                                               LocalDate fechaDesde, LocalDate hoy) throws SQLException {
		return prestamosRepository.listarPrestamos(socio, videojuego, idPlataforma, estado, fechaDesde, hoy);
	}

	@Override
	public PrestamosDTO buscarPrestamo(int idPrestamo) throws SQLException {
		return prestamosRepository.buscarPrestamo(idPrestamo);
	}

	@Override
	public int contarPrestamosVencidosSocio(int idSocio, LocalDate hoy) throws SQLException {
		return prestamosRepository.contarPrestamosVencidosSocio(idSocio, hoy);
	}

	@Override
	public int insertarPrestamo(int idSocio, int idCopia, LocalDate fechaPrestamo, LocalDate fechaLimite) throws SQLException {
		return prestamosRepository.insertarPrestamo(idSocio, idCopia, fechaPrestamo, fechaLimite);
	}

	@Override
	public void renovarPrestamo(int idPrestamo, LocalDate nuevaFechaLimite) throws SQLException {
		prestamosRepository.renovarPrestamo(idPrestamo, nuevaFechaLimite);
	}

	@Override
	public void devolverPrestamo(int idPrestamo, int idCopia, LocalDate fechaDevolucion, BigDecimal multa,
	                             boolean copiaDanada) throws SQLException {
		prestamosRepository.devolverPrestamo(idPrestamo, idCopia, fechaDevolucion, multa, copiaDanada);
	}
}
