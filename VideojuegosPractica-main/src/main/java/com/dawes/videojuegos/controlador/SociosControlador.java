package com.dawes.videojuegos.controlador;

import com.dawes.videojuegos.DTO.SociosDTO;
import com.dawes.videojuegos.service.SociosService;
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
@RequestMapping("/socios")
public class SociosControlador {
	private static final Logger logger = LoggerFactory.getLogger(SociosControlador.class);
	private static final String MENSAJE_ERROR_BBDD = "Se ha producido un error con la base de datos. Inténtalo de nuevo más tarde.";

	private final SociosService sociosService;

	public SociosControlador(SociosService sociosService) {
		this.sociosService = sociosService;
	}

	@GetMapping("/listado")
	public String listado(@RequestParam(required = false) String nombre,
	                      @RequestParam(required = false) String apellidos,
	                      @RequestParam(required = false) String email,
	                      @RequestParam(required = false) Boolean activo,
	                      @RequestParam(required = false) Integer edadMinima,
	                      Model model) {

		model.addAttribute("nombre", nombre);
		model.addAttribute("apellidos", apellidos);
		model.addAttribute("email", email);
		model.addAttribute("activo", activo);
		model.addAttribute("edadMinima", edadMinima);

		try {
			model.addAttribute("listaSocios", sociosService.listarSocios(nombre, apellidos, email, activo, edadMinima));
		} catch (SQLException e) {
			logger.error("Error al listar los socios", e);
			model.addAttribute("listaSocios", new ArrayList<SociosDTO>());
			model.addAttribute("error", MENSAJE_ERROR_BBDD);
		}
		return "socios/listado";
	}

	@PostMapping("/cambiarActivo")
	public String cambiarActivo(@RequestParam(required = false) Integer idSocio,
	                            @RequestParam(defaultValue = "false") boolean activo,
	                            RedirectAttributes redirectAttributes) {
		try {
			SociosDTO socio = sociosService.cambiarActivo(idSocio, activo);
			redirectAttributes.addFlashAttribute("mensajeOk", "Socio '" + socio.getNombre() + " " + socio.getApellidos()
					+ "' " + (activo ? "reactivado" : "dado de baja") + " correctamente");
		} catch (ValidacionException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		} catch (SQLException e) {
			logger.error("Error al cambiar el estado del socio", e);
			redirectAttributes.addFlashAttribute("error", MENSAJE_ERROR_BBDD);
		}
		return "redirect:/socios/listado";
	}

	@GetMapping("/insertar")
	public String formularioInsertar() {
		return "socios/insertar";
	}

	@PostMapping("/insertar")
	public String insertar(@RequestParam(required = false) String nombre,
	                       @RequestParam(required = false) String apellidos,
	                       @RequestParam(required = false) String email,
	                       @RequestParam(required = false) String telefono,
	                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaNacimiento,
	                       Model model, RedirectAttributes redirectAttributes) {
		try {
			SociosDTO socio = new SociosDTO(nombre, apellidos, email, telefono, fechaNacimiento);
			int idNuevo = sociosService.insertarSocio(socio);

			redirectAttributes.addFlashAttribute("mensajeOk",
					"Socio '" + socio.getNombre() + " " + socio.getApellidos() + "' insertado correctamente (id " + idNuevo + ")");
			return "redirect:/socios/insertar";

		} catch (SQLException e) {
			logger.error("Error al insertar el socio", e);
			model.addAttribute("error", MENSAJE_ERROR_BBDD);
		} catch (ValidacionException e) {
			model.addAttribute("error", e.getMessage());
		}

		model.addAttribute("nombre", nombre);
		model.addAttribute("apellidos", apellidos);
		model.addAttribute("email", email);
		model.addAttribute("telefono", telefono);
		model.addAttribute("fechaNacimiento", fechaNacimiento);
		return "socios/insertar";
	}
}