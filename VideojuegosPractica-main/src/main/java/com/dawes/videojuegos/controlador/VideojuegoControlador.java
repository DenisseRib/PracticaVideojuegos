package com.dawes.videojuegos.controlador;

import com.dawes.videojuegos.DTO.VideojuegoDTO;
import com.dawes.videojuegos.service.ValidacionException;
import com.dawes.videojuegos.service.VideojuegoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;
import java.util.ArrayList;


@Controller
@RequestMapping("/videojuegos")
public class VideojuegoControlador {
	private static final Logger logger = LoggerFactory.getLogger(VideojuegoControlador.class);
	private static final String MENSAJE_ERROR_BBDD = "Se ha producido un error con la base de datos. Inténtalo de nuevo más tarde.";

	private final VideojuegoService videojuegoService;

	public VideojuegoControlador(VideojuegoService videojuegoService) {
		this.videojuegoService = videojuegoService;
	}


	@GetMapping("/listado")
	public String listado(@RequestParam(required = false) String titulo,
	                      @RequestParam(required = false) Integer idDesarrolladora,
	                      @RequestParam(required = false) Integer anioDesde,
	                      @RequestParam(required = false) Integer pegiMax,
	                      @RequestParam(required = false) Integer idPlataforma,
	                      @RequestParam(defaultValue = "false") boolean soloDisponibles,
	                      Model model) {

		model.addAttribute("titulo", titulo);
		model.addAttribute("idDesarrolladora", idDesarrolladora);
		model.addAttribute("anioDesde", anioDesde);
		model.addAttribute("pegiMax", pegiMax);
		model.addAttribute("idPlataforma", idPlataforma);
		model.addAttribute("soloDisponibles", soloDisponibles);
		model.addAttribute("listaPegi", videojuegoService.listarPegiValidos());

		try {
			model.addAttribute("listaDesarrolladoras", videojuegoService.listarDesarrolladoras());
			model.addAttribute("listaPlataformas", videojuegoService.listarPlataformas());
			model.addAttribute("listaVideojuegos",
					videojuegoService.buscarVideojuegos(titulo, idDesarrolladora, anioDesde, pegiMax, soloDisponibles, idPlataforma));
		} catch (SQLException e) {
			logger.error("Error al listar los videojuegos", e);
			model.addAttribute("listaDesarrolladoras", new ArrayList<>());
			model.addAttribute("listaPlataformas", new ArrayList<>());
			model.addAttribute("listaVideojuegos", new ArrayList<>());
			model.addAttribute("error", MENSAJE_ERROR_BBDD);
		}
		return "videojuegos/listado";
	}


	@GetMapping("/insertar")
	public String formularioInsertar(Model model) {
		cargarDesplegablesInsertar(model);
		return "videojuegos/insertar";
	}

	@PostMapping("/insertar")
	public String insertar(@RequestParam(required = false) String titulo,
	                       @RequestParam(required = false) Integer anioLanzamiento,
	                       @RequestParam(required = false) Integer pegi,
	                       @RequestParam(required = false) Integer idDesarrolladora,
	                       Model model, RedirectAttributes redirectAttributes) {
		try {
			VideojuegoDTO videojuego = new VideojuegoDTO(titulo, anioLanzamiento, pegi, idDesarrolladora);
			int idNuevo = videojuegoService.insertarVideojuego(videojuego);

			redirectAttributes.addFlashAttribute("mensajeOk",
					"Videojuego '" + videojuego.getTitulo() + "' insertado correctamente (id " + idNuevo + ")");
			return "redirect:/videojuegos/insertar";

		} catch (ValidacionException e) {
			model.addAttribute("error", e.getMessage());
		} catch (SQLException e) {
			logger.error("Error al insertar el videojuego", e);
			model.addAttribute("error", MENSAJE_ERROR_BBDD);
		}

		model.addAttribute("titulo", titulo);
		model.addAttribute("anioLanzamiento", anioLanzamiento);
		model.addAttribute("pegi", pegi);
		model.addAttribute("idDesarrolladora", idDesarrolladora);
		cargarDesplegablesInsertar(model);


		return "videojuegos/insertar";
	}

	private void cargarDesplegablesInsertar(Model model) {
		model.addAttribute("listaPegi", videojuegoService.listarPegiValidos());
		try {
			model.addAttribute("listaDesarrolladoras", videojuegoService.listarDesarrolladoras());
		} catch (SQLException e) {
			logger.error("Error al cargar las desarrolladoras", e);
			model.addAttribute("listaDesarrolladoras", new ArrayList<>());
			model.addAttribute("error", MENSAJE_ERROR_BBDD);
		}
	}
}
