package com.dawes.videojuegos.service;

import com.dawes.videojuegos.DTO.CopiaDTO;
import com.dawes.videojuegos.DTO.DesplegableDTO;
import com.dawes.videojuegos.interfaz.ICopiasDAO;
import com.dawes.videojuegos.interfaz.IPlataformaDAO;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CopiasService {
	private static final List<String> ESTADOS_COPIA = List.of("NUEVO", "BUENO", "DESGASTADO", "DAÑADO");

	private final ICopiasDAO copiasDAO;
	private final IPlataformaDAO plataformaDAO;

	public CopiasService(ICopiasDAO copiasDAO, IPlataformaDAO plataformaDAO) {
		this.copiasDAO = copiasDAO;
		this.plataformaDAO = plataformaDAO;
	}

	public ArrayList<CopiaDTO> listarCopias(String titulo, Integer idPlataforma, String estado)
			throws SQLException, ValidacionException {
		String tituloLimpio = (titulo == null || titulo.isBlank()) ? null : titulo.trim();
		String estadoLimpio = (estado == null || estado.isBlank()) ? null : estado.trim().toUpperCase();
		if (estadoLimpio != null && !ESTADOS_COPIA.contains(estadoLimpio)) {
			throw new ValidacionException("El estado debe ser: nuevo, bueno, desgastado o dañado.");
		}

		ArrayList<CopiaDTO> listaCopias = copiasDAO.listarCopias(tituloLimpio, idPlataforma, estadoLimpio);
		// Una copia está disponible si está activa y no tiene ningún préstamo abierto (regla de negocio del Service)
		listaCopias.forEach(copia -> copia.setDisponible(copia.isActiva() && copia.getPrestamosAbiertos() == 0));
		return listaCopias;
	}

	public ArrayList<DesplegableDTO> listarPlataformas() throws SQLException {
		return plataformaDAO.listarDesplegable();
	}

	public List<String> listarEstados() {
		return ESTADOS_COPIA;
	}
}
