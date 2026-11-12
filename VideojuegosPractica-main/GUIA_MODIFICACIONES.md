# Guía de posibles modificaciones (para estudiar la defensa)

Cada comentario `[MODIF-n]` del código marca dónde se hace el cambio. Búscalos con
`grep -rn "MODIF-" src` (o Ctrl+Shift+F en IntelliJ).

Regla de oro: un cambio sigue el camino BBDD → Repository → DAO (interfaz + impl) → Service → Controller → vista.

## Fáciles (cambiar una constante)
| Cambio | Marca | Dónde |
|---|---|---|
| Máximo de préstamos abiertos | MODIF-1 | `PrestamosService` |
| Días de plazo (novedad / normal) | MODIF-2 | `PrestamosService` |
| Precio de multa por día | MODIF-3 | `PrestamosService` |
| Criterio de "novedad" | MODIF-5 | `PrestamosService.esNovedad` |
| Año mínimo de lanzamiento | MODIF-9 | `VideojuegoService` |
| Valores PEGI válidos | MODIF-10 | `VideojuegoService` |
| Orden de los listados | MODIF-13, 16 | Repository |

## Medias (lógica nueva en el Service)
| Cambio | Marca | Dónde |
|---|---|---|
| Regla nueva RN-6 | MODIF-4 | `PrestamosService.registrarPrestamo` |
| Multa con tope, días de gracia o recargo por copia dañada | MODIF-6 | `PrestamosService.devolverPrestamo` |
| Validación nueva en alta de videojuego o socio | MODIF-11, 17 | `VideojuegoService`, `SociosService` |
| Reglas de renovación (p. ej. solo socios activos) | MODIF-8 | `PrestamosService.renovarPrestamo` |
| Qué pasa con la copia al devolverla dañada | MODIF-21 | `PrestamosRepository` |

## Grandes (recorrer todas las capas)
| Cambio | Marca | Qué tocar |
|---|---|---|
| Filtro nuevo en un listado | MODIF-12, 20 | Repository (SQL dinámico), DAO, Service, Controller (`@RequestParam`), vista |
| Columna nueva en una tabla (género, teléfono...) | MODIF-14, 15, 18 | SQL (ALTER TABLE), DTO, Repository (SELECT/INSERT/mapear), Service, Controller, vistas |
| Contador nuevo en el Inicio | MODIF-19 | `InicioRepository`, `ContadoresInicioDTO`, `index.html` |
| Dar de baja / reactivar socio | MODIF-22 | método nuevo en las 4 capas + botón |
| Pantalla nueva (copias, plataformas) | MODIF-23 | enlace en el menú + todas las capas + vista |

## Ejercicios para practicar
1. Máximo de 2 préstamos abiertos.
2. Multa de 1 € por día y tope máximo de 10 €.
3. RN-6: no prestar copias en estado DAÑADO ni DESGASTADO a socios menores de 18.
4. Añadir `telefono` a socios: obligatorio, 9 dígitos.
5. Filtro por plataforma en el listado de videojuegos.
6. Contador "copias dañadas" en el Inicio.
7. Listado de copias con su estado y si están disponibles.

## Quitar los comentarios antes de entregar
`grep -rl "MODIF-" src | xargs sed -i '/\[MODIF-[0-9]*\]/d'`
(Todos los comentarios son líneas completas, así que no se pierde código.)
