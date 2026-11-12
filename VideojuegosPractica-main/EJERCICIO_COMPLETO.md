# Ejercicio completo: 17 modificaciones resueltas

Este proyecto es la práctica con **17 modificaciones posibles ya hechas**, una por commit.
Úsalo para estudiar: no es la versión para entregar.

## Cómo estudiarlo
- `git log --oneline` lista las modificaciones en orden.
- `git show <hash>` muestra qué se tocó en cada capa (DTO, Repository, DAO, Service, Controller, vista).
- `git show <hash> --stat` muestra solo qué ficheros cambian.
- Los cambios de base de datos están en `cambios_bbdd.sql` (ejecútalo antes de arrancar).

Método de estudio: lee el título del commit, intenta hacerlo tú sin mirar y compara con `git show`.
Para repetirlo desde cero: `git checkout f221b8a` (versión base) y crea una rama nueva.

## Las 17 modificaciones

| # | Commit | Tipo | Dónde se toca |
|---|---|---|---|
| 1 | máximo 2 préstamos abiertos | constante | `PrestamosService` |
| 2 | plazos 5 / 10 días | constantes | `PrestamosService` |
| 3 | multa 1 €/día con tope 10 € | lógica | `PrestamosService.devolverPrestamo` |
| 4 | recargo 5 € si la copia vuelve dañada | lógica | `PrestamosService.devolverPrestamo` |
| 5 | novedad = año actual y 2 anteriores | lógica | `PrestamosService.esNovedad` |
| 6 | año mínimo 1980 | constante | `VideojuegoService` |
| 7 | título de videojuego repetido | 4 capas | Repository, DAO, Service |
| 8 | RN-6: copias deterioradas no a menores de 18 | regla + dato nuevo | `CopiaDTO` (estado), `CopiasRepository`, `PrestamosService` |
| 9 | teléfono de socios (9 dígitos) | columna nueva | SQL, DTO, Repository, Service, Controller, 2 vistas |
| 10 | filtro por plataforma en videojuegos | filtro + desplegable nuevo | `PlataformaRepository/DAO`, `VideojuegoRepository`, vista |
| 11 | orden del listado de videojuegos | SQL | `VideojuegoRepository` |
| 12 | filtro de edad mínima en socios | filtro | 4 capas + vista |
| 13 | contador de copias dañadas | Inicio | `InicioRepository`, `ContadoresInicioDTO`, `index.html` |
| 14 | baja y reactivación de socios | acción nueva (UPDATE) | 4 capas + botón |
| 15 | listado de copias | pantalla nueva | Repository, DAO, Service, Controller, vista, menú |
| 16 | máximo 1 renovación por préstamo | columna nueva | SQL, DTO, Repository, Service, vista |
| 17 | filtro por plataforma en préstamos | filtro | 4 capas + vista |

## Qué NO he implementado (y por qué)
- **Columna nueva en `videojuegos`** (género, etc.): el listado lee de la vista `v_videojuegos_detalle`, y su
  definición está en el script del ayuntamiento, que no tengo. En la defensa habría que hacer
  `ALTER TABLE videojuegos ADD ...`, **recrear la vista** con `CREATE OR REPLACE VIEW` incluyendo la columna nueva,
  y luego tocar `VideojuegoDTO`, `mapearVideojuego`, el INSERT y las vistas. Es el patrón del cambio 9.
- **Estado calculado nuevo (por ejemplo POR_VENCER)**: va en `PrestamosService.calcularEstado`, pero el filtro de
  estado de `PrestamosRepository.listarPrestamos` también debe conocerlo.
- **Cambiar qué le pasa a la copia dañada** (cambio de `PrestamosRepository.devolverPrestamo`, el UPDATE de `sqlCopia`).

## Plantilla para cualquier cambio de columna
1. SQL: `ALTER TABLE ... ADD COLUMN ...`
2. DTO: campo, constructor, getter y setter
3. Repository: SELECT, INSERT/UPDATE y el método `mapear...`
4. DAO: interfaz e implementación (solo si cambia la firma de un método)
5. Service: validaciones
6. Controller: `@RequestParam` nuevo y pasarlo al DTO; en errores devolver el valor al modelo
7. Vistas: input en el alta, columna en el listado (y `colspan` del aviso de listado vacío)

## Qué se ha comprobado
- Todo el código Java compila (con stubs de Spring, sin Maven).
- Un test con DAOs simulados comprueba RN-1 a RN-6, los plazos, las multas, la renovación, la baja de socios y las
  validaciones nuevas: 26 casos, todos correctos.
- **No** se ha probado contra MySQL ni con Thymeleaf. Arráncalo tú y revisa pantallas y SQL.
