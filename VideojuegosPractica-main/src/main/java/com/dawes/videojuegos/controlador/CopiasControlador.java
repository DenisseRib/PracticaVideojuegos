package com.dawes.videojuegos.controlador;

import com.dawes.videojuegos.DTO.CopiaDTO;
import com.dawes.videojuegos.service.CopiasService;
import com.dawes.videojuegos.service.ValidacionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.ArrayList;

@Controller
@RequestMapping("/copias")
public class CopiasControlador {
	private static final Logger logger = LoggerFactory.getLogger(CopiasControlador.class);
	private static final String MENSAJE_ERROR_BBDD = "Se ha producido un error con la base de datos. Inténtalo de nuevo más tarde.";

	private final CopiasService copiasService;

	public CopiasControlador(CopiasService copiasService) {
		this.copiasService = copiasService;
	}

	@GetMapping("/listado")
	public String listado(@RequestParam(required = false) String titulo,
	                      @RequestParam(required = false) Integer idPlataforma,
	                      @RequestParam(required = false) String estado,
	                      Model model) {
		model.addAttribute("titulo", titulo);
		model.addAttribute("idPlataforma", idPlataforma);
		model.addAttribute("estado", estado);
		model.addAttribute("listaEstados", copiasService.listarEstados());

		try {
			model.addAttribute("listaPlataformas", copiasService.listarPlataformas());
			model.addAttribute("listaCopias", copiasService.listarCopias(titulo, idPlataforma, estado));
		} catch (ValidacionException e) {
			model.addAttribute("listaPlataformas", new ArrayList<>());
			model.addAttribute("listaCopias", new ArrayList<CopiaDTO>());
			model.addAttribute("error", e.getMessage());
		} catch (SQLException e) {
			logger.error("Error al listar las copias", e);
			model.addAttribute("listaPlataformas", new ArrayList<>());
			model.addAttribute("listaCopias", new ArrayList<CopiaDTO>());
			model.addAttribute("error", MENSAJE_ERROR_BBDD);
		}
		return "copias/listado";
	}
}
