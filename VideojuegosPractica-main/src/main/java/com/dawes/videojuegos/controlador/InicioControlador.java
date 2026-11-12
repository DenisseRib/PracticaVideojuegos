package com.dawes.videojuegos.controlador;

import com.dawes.videojuegos.DTO.ContadoresInicioDTO;
import com.dawes.videojuegos.service.InicioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.SQLException;

@Controller
public class InicioControlador {
	private static final Logger logger = LoggerFactory.getLogger(InicioControlador.class);
	private final InicioService inicioService;

	public InicioControlador(InicioService inicioService) {
		this.inicioService = inicioService;
	}

	@GetMapping("/")
	public String inicio(Model model) {
		try {
			model.addAttribute("contadores", inicioService.obtenerContadores());
		} catch (SQLException e) {
			logger.error("Error al obtener los contadores del inicio", e);
			model.addAttribute("contadores", new ContadoresInicioDTO());
			model.addAttribute("error", "No se han podido cargar los contadores. Inténtalo de nuevo más tarde.");
		}
		return "index";
	}
}
