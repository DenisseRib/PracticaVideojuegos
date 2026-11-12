package com.dawes.videojuegos.controlador;


import com.dawes.videojuegos.DTO.PrestamosDTO;

import com.dawes.videojuegos.service.PrestamosService;
import com.dawes.videojuegos.service.ValidacionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

@Controller
@RequestMapping("/prestamos")
public class PrestamosControlador {
	private static final Logger logger = LoggerFactory.getLogger(PrestamosControlador.class);
	private static final String MENSAJE_ERROR_BBDD = "Se ha producido un error con la base de datos. Inténtalo de nuevo más tarde.";

	private final PrestamosService prestamosService;

	public PrestamosControlador(PrestamosService prestamosService) {
		this.prestamosService = prestamosService;
	}

	@GetMapping("/listado")
	public String listado(@RequestParam(required = false) String socio,
	                      @RequestParam(required = false) String videojuego,
	                      @RequestParam(required = false) Integer idPlataforma,
	                      @RequestParam(required = false) String estado,
	                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
	                      Model model) {
		model.addAttribute("socio", socio);
		model.addAttribute("videojuego", videojuego);
		model.addAttribute("idPlataforma", idPlataforma);
		model.addAttribute("estado", estado);
		model.addAttribute("fechaDesde", fechaDesde);
		try {
			model.addAttribute("listaPlataformas", prestamosService.listarPlataformas());
			model.addAttribute("listaPrestamos", prestamosService.listarPrestamos(socio, videojuego, idPlataforma, estado, fechaDesde));
		} catch (ValidacionException e) {
			model.addAttribute("listaPrestamos", new ArrayList<PrestamosDTO>());
			model.addAttribute("error", e.getMessage());
		} catch (SQLException e) {
			logger.error("Error al listar los préstamos", e);
			model.addAttribute("listaPlataformas", new ArrayList<>());
			model.addAttribute("listaPrestamos", new ArrayList<PrestamosDTO>());
			model.addAttribute("error", MENSAJE_ERROR_BBDD);
		}
		return "prestamos/listado";
	}


	@GetMapping("/registrar")
	public String formularioRegistrar(Model model) {
		cargarDesplegablesRegistrar(model);
		return "prestamos/registrar";
	}

	@PostMapping("/registrar")
	public String registrar(@RequestParam(required = false) Integer idSocio,
	                        @RequestParam(required = false) Integer idCopia,
	                        Model model, RedirectAttributes redirectAttributes) {
		try {
			PrestamosDTO prestamoRegistrado = prestamosService.registrarPrestamo(idSocio, idCopia);
			redirectAttributes.addFlashAttribute("prestamoRegistrado", prestamoRegistrado);
			return "redirect:/prestamos/registrar";

		} catch (ValidacionException e) {
			model.addAttribute("error", e.getMessage());
		} catch (SQLException e) {
			logger.error("Error al registrar el préstamo", e);
			model.addAttribute("error", MENSAJE_ERROR_BBDD);
		}

		model.addAttribute("idSocio", idSocio);
		model.addAttribute("idCopia", idCopia);
		cargarDesplegablesRegistrar(model);
		return "prestamos/registrar";
	}

	private void cargarDesplegablesRegistrar(Model model) {
		try {
			model.addAttribute("listaSocios", prestamosService.listarSociosActivos());
			model.addAttribute("listaCopias", prestamosService.listarCopiasDisponibles());
		} catch (SQLException e) {
			logger.error("Error al cargar los desplegables de registrar préstamo", e);
			model.addAttribute("listaSocios", new ArrayList<>());
			model.addAttribute("listaCopias", new ArrayList<>());
			model.addAttribute("error", MENSAJE_ERROR_BBDD);
		}
	}


	@GetMapping("/devolver")
	public String formularioDevolver(@RequestParam(required = false) String socio, Model model) {
		model.addAttribute("socio", socio);
		try {
			model.addAttribute("listaPrestamos", prestamosService.listarPrestamosAbiertos(socio));
		} catch (SQLException e) {
			logger.error("Error al listar los préstamos abiertos", e);
			model.addAttribute("listaPrestamos", new ArrayList<PrestamosDTO>());
			model.addAttribute("error", MENSAJE_ERROR_BBDD);
		}
		return "prestamos/devolver";
	}

	@PostMapping("/renovar")
	public String renovar(@RequestParam(required = false) Integer idPrestamo,
	                      @RequestParam(required = false) String socio,
	                      RedirectAttributes redirectAttributes) {
		try {
			redirectAttributes.addFlashAttribute("prestamoRenovado", prestamosService.renovarPrestamo(idPrestamo));
		} catch (ValidacionException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		} catch (SQLException e) {
			logger.error("Error al renovar el préstamo", e);
			redirectAttributes.addFlashAttribute("error", MENSAJE_ERROR_BBDD);
		}
		if (socio != null && !socio.isBlank()) {
			redirectAttributes.addAttribute("socio", socio);
		}
		return "redirect:/prestamos/devolver";
	}

	@PostMapping("/devolver")
	public String devolver(@RequestParam(required = false) Integer idPrestamo,
	                       @RequestParam(defaultValue = "false") boolean copiaDanada,
	                       @RequestParam(required = false) String socio,
	                       RedirectAttributes redirectAttributes) {
		try {
			PrestamosDTO prestamoDevuelto = prestamosService.devolverPrestamo(idPrestamo, copiaDanada);
			redirectAttributes.addFlashAttribute("prestamoDevuelto", prestamoDevuelto);
			redirectAttributes.addFlashAttribute("copiaDanada", copiaDanada);
		} catch (ValidacionException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		} catch (SQLException e) {
			logger.error("Error al devolver el préstamo", e);
			redirectAttributes.addFlashAttribute("error", MENSAJE_ERROR_BBDD);
		}
		if (socio != null && !socio.isBlank()) {
			redirectAttributes.addAttribute("socio", socio);
		}
		return "redirect:/prestamos/devolver";
	}
}
