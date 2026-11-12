package com.dawes.videojuegos.interfaz;

import com.dawes.videojuegos.DTO.PrestamosDTO;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public interface IPrestamosDAO {


	ArrayList<PrestamosDTO> listarPrestamos(String socio, String videojuego, Integer idPlataforma, String estado, LocalDate fechaDesde,
	                                        LocalDate hoy) throws SQLException;

	PrestamosDTO buscarPrestamo(int idPrestamo) throws SQLException;

	int contarPrestamosVencidosSocio(int idSocio, LocalDate hoy) throws SQLException;

	int insertarPrestamo(int idSocio, int idCopia, LocalDate fechaPrestamo, LocalDate fechaLimite) throws SQLException;

	void renovarPrestamo(int idPrestamo, LocalDate nuevaFechaLimite) throws SQLException;

	void devolverPrestamo(int idPrestamo, int idCopia, LocalDate fechaDevolucion, BigDecimal multa,
	                      boolean copiaDanada) throws SQLException;
}
