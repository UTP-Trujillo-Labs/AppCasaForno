# AppCasaForno

Sistema de gestión para el restaurante **Casa Forno**. Permite tomar pedidos, monitorear la cocina y administrar el estado de las mesas, a través de una interfaz web servida por un servidor Tomcat embebido.

## Requisitos

- Java 25 o superior
- Maven 3.8+

## Ejecución

```bash
# Compilar
mvn compile

# Ejecutar
mvn exec:java -Dexec.mainClass="pe.edu.utp.appcasaforno.App"
```

También puedes ejecutar la clase `App` desde tu IDE. El servidor queda disponible en:

**http://localhost:8080**

## Estructura del proyecto

```
src/main/java/pe/edu/utp/appcasaforno/
├── App.java                          # Punto de entrada
├── domain/
│   ├── model/                        # Entidades y DTOs del dominio
│   ├── api/                          # ServletRegistration
│   └── state/mesa/                   # Patrón State (estados de mesa)
├── application/                      # Lógica de negocio (servicios)
├── infraestructure/
│   ├── persistence/                  # Almacenamiento en memoria
│   ├── server/                       # Tomcat, ServerFacade, ServletRegistry
│   ├── web/                          # ApiServlet, ApiHandler
│   └── util/                         # Utilidades (JSON, helpers)
└── presentation/
    ├── factory/ApplicationFactory.java
    ├── handler/                      # Estrategias por ruta (por módulo)
    │   ├── categorias/
    │   ├── cocina/
    │   ├── mesas/
    │   ├── pedidos/
    │   └── productos/
    └── servlet/                      # Servlets REST (extienden ApiServlet)

src/main/resources/public/
├── index.html                        # Shell de la aplicación (SPA)
├── css/                              # Estilos
├── js/
│   ├── core.js                       # Navegación y utilidades compartidas
│   ├── app.js                        # Enrutamiento por hash
│   └── pages/                        # Lógica por pantalla
└── pages/                            # Fragmentos HTML por pantalla
```

## Arquitectura

El proyecto sigue una organización en capas inspirada en arquitectura hexagonal / limpia:

| Capa | Paquete | Responsabilidad |
|------|---------|-----------------|
| **Dominio** | `domain.model`, `domain.state`, `domain.api` | Modelos, estados de mesa y descriptores de servlet |
| **Aplicación** | `application` | Casos de uso: `PedidosService`, `CocinaServicio`, `MesasServicio`, `ProductService`, `CategoriaServicio` |
| **Infraestructura** | `infraestructure` | Servidor Tomcat, registro web, persistencia en memoria y utilidades |
| **Presentación** | `presentation` | Servlets REST, handlers y factory de composición |

### Flujo de una petición

```
Cliente (navegador)
    → Tomcat embebido (puerto 8080)
        → Archivos estáticos (/css, /js, /pages, index.html)
        → Servlets API (/api/*)
            → ApiHandler (Strategy)
                → Servicio de aplicación
                    → Persistencia en memoria
            → Respuesta JSON (Jackson)
```

`ServerFacade` orquesta el arranque: extrae archivos estáticos (compatible con IDE y JAR), configura Tomcat y registra los servlets de la API.

## Módulos funcionales

### Toma de pedidos (`#pedidos`)

- Consulta categorías (`/api/categorias`) y catálogo de productos (`/api/productos`) con filtros.
- Muestra mesas disponibles desde la API.
- Envía pedidos a cocina (`POST /api/cocina/`) generando un ticket y descontando stock.

### Monitor de cocina (`#cocina`)

- Lista los pedidos pendientes (`GET /api/cocina/`) con polling.
- Permite despachar un ticket (`POST /api/cocina/{ticket}/despachar`): PENDIENTE → COMPLETADO.

### Control de mesas (`#mesas`)

- Vista del salón con estados visuales (libre, ocupada, reservada) desde la API.
- Avance de estado (`POST /api/mesas/{numero}`) según el patrón State (sin enviar el estado destino).
- Consulta de pedidos por mesa y cobro (`POST /api/mesas/{numero}/pagar`).

### Inventario (`#inventario`)

- Lista productos y stock desde `/api/productos` y `/api/categorias`.
- Muestra reporte de pedidos completados desde `/api/pedidos/completados`.

## API REST

Base URL: `http://localhost:8080/api`

| Método | Ruta | Servlet | Descripción |
|--------|------|---------|-------------|
| `GET` | `/categorias/` | `CategoriasServlet` | Lista todas las categorías |
| `GET` | `/productos/` | `ProductosServlet` | Lista productos. Query params opcionales: `categoria`, `busqueda` |
| `GET` | `/pedidos/completados` | `PedidosServlet` | Tickets en histórico (completados / pagados) |
| `GET` | `/pedidos/mesa/{numero}` | `PedidosServlet` | Pedidos pendientes y completados de una mesa |
| `GET` | `/cocina/` | `CocinaServlet` | Tickets pendientes en cocina |
| `POST` | `/cocina/` | `CocinaServlet` | Envía un pedido a cocina. Body: `EnvioCocinaRequest` |
| `POST` | `/cocina/{ticket}/despachar` | `CocinaServlet` | Despacha un ticket (PENDIENTE → COMPLETADO) |
| `GET` | `/mesas/` | `MesasServlet` | Resumen y listado de mesas |
| `POST` | `/mesas/{numero}` | `MesasServlet` | Avanza el estado de la mesa (State) |
| `POST` | `/mesas/{numero}/pagar` | `MesasServlet` | Cobra la mesa si no hay pendientes y libera |

Todas las respuestas de la API son JSON. Los errores devuelven un objeto `ErrorResponse` con el mensaje descriptivo. Solo se exponen `GET` y `POST` (más `OPTIONS` para CORS).

### Ejemplo: enviar pedido a cocina

```http
POST /api/cocina/
Content-Type: application/json

{
  "cliente": "Juan Pérez",
  "mesa": "3",
  "nota": "Sin cebolla",
  "items": [
    { "productoId": "p1", "cantidad": 2 },
    { "productoId": "b1", "cantidad": 1 }
  ]
}
```

## Tecnologías

| Componente | Tecnología |
|------------|------------|
| Lenguaje | Java 25 |
| Servidor HTTP | Apache Tomcat Embed 10.1 |
| Serialización JSON | Jackson 2.18 |
| Frontend | HTML, CSS y JavaScript vanilla (SPA por hash) |
| Build | Maven |

## Patrones de diseño

La capa de presentación aplica patrones GoF sin frameworks ni anotaciones. En dominio se usa State para las mesas.

Referencias: [Refactoring.Guru — Design Patterns](https://refactoring.guru/design-patterns)

### Patrón 1: Template Method (Comportamiento)

**Referencia:** [Template Method](https://refactoring.guru/design-patterns/template-method)

Define el esqueleto de un algoritmo en una clase base, delegando algunos pasos a las subclases.

**Implementación:** `ApiServlet` centraliza el flujo HTTP común:

- Preparar cabeceras JSON y CORS
- Responder a `OPTIONS`
- Capturar `IllegalArgumentException` y devolver HTTP 400
- Normalizar el path y resolver handlers (incluyendo rutas con parámetros: `/{numero}`, `/{ticket}/despachar`, etc.)

Cada servlet concreto (`PedidosServlet`, `CocinaServlet`, `ProductosServlet`, `MesasServlet`, `CategoriasServlet`) pasa los mapas de handlers al constructor y delega el flujo a la clase base.

```
ApiServlet
├── doGet() / doPost()  → dispatch(handlers)
├── doOptions()         → CORS (implementación única)
└── dispatch()          → prepareJson + resolveHandler + handle / 404 / 400
```

**Beneficio:** Elimina la duplicación de boilerplate HTTP sin cambiar la estructura general del proyecto.

---

### Patrón 2: Strategy (Comportamiento)

**Referencia:** [Strategy](https://refactoring.guru/design-patterns/strategy)

Define una familia de algoritmos intercambiables y los encapsula detrás de una interfaz común.

**Implementación:** Cada ruta es una clase en `presentation/handler/` que implementa `ApiHandler`:

```java
interface ApiHandler {
    void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException;
}
```

Cada endpoint se implementa como una clase `ApiHandler` independiente. El servlet consulta el mapa por path y delega:

```java
ApiHandler handler = resolveHandler(handlers, path);
if (handler == null) {
  // 404
} else {
  handler.handle(req, resp);
}
```

**Beneficio:** Cada endpoint es una clase pequeña, testeable y extensible. Agregar una ruta nueva es crear una clase, no modificar un `switch` existente (principio Open/Closed).

---

### Patrón 3: Facade (Estructural)

**Referencia:** [Facade](https://refactoring.guru/design-patterns/facade)

Proporciona una interfaz simplificada a un subsistema complejo.

**Implementación:** `ServerFacade` delega en tres clases especializadas:

| Clase | Responsabilidad |
|-------|-----------------|
| `EmbeddedTomcatServer` | Puerto, conector, ciclo de vida start/stop |
| `StaticResourceExtractor` | Extracción de `/public` desde classpath o JAR |
| `ServletRegistry` | Registro de servlets y mappings |

`ServerFacade` queda como orquestador delgado:

```java
public class App {
    public static void main(String[] args) throws Exception {
        new ServerFacade().start();
    }
}
```

**Beneficio:** Separación clara de responsabilidades y código más legible para documentar en un informe académico.

---

### Patrón 4: Factory Method (Creacional)

**Referencia:** [Factory Method](https://refactoring.guru/design-patterns/factory-method)

Delega la creación de objetos a subclases o clases factory, evitando acoplar el código cliente a implementaciones concretas.

**Implementación:** `ApplicationFactory` produce el catálogo completo de servlets como `ServletRegistration` (nombre, path, instancia):

```java
public List<ServletRegistration> crearRegistrosServlets() {
    return List.of(
        new ServletRegistration("pedidosServlet", "/api/pedidos/*",
                new PedidosServlet(pedidosService)),
        new ServletRegistration("cocinaServlet", "/api/cocina/*",
                new CocinaServlet(cocinaServicio)),
        new ServletRegistration("productosServlet", "/api/productos/*",
                new ProductosServlet(productService)),
        new ServletRegistration("categoriasServlet", "/api/categorias/*",
                new CategoriasServlet(categoriaServicio)),
        new ServletRegistration("mesasServlet", "/api/mesas/*",
                new MesasServlet(mesasServicio)));
}
```

`ServletRegistry` no conoce tipos concretos: solo recorre la lista y registra cada descriptor en Tomcat. Agregar un servlet nuevo implica una línea en la factory, sin tocar el registry.

La factory actúa como **composition root**: un único lugar donde se ensambla el grafo de dependencias antes de registrar los servlets.

**Beneficio:** Inyección de dependencias manual, sin contenedor IoC, adecuada para proyectos académicos.

---

### Patrón 5: State (Comportamiento)

**Referencia:** [State](https://refactoring.guru/design-patterns/state)

Permite que un objeto altere su comportamiento cuando cambia su estado interno.

**Implementación:** En `domain/state/mesa/`, cada estado concreto (`LibreState`, `ReservadaState`, `OcupadaState`) define las transiciones válidas. `MesaContext` delega `avanzar()` y `ocupar()` al estado actual. `ActualizarMesaHandler` no recibe el estado destino: el backend aplica la transición.

**Beneficio:** Las reglas de mesa viven en el dominio, no en el servlet ni en el frontend.

---

### Combinación de patrones

Los patrones se complementan en capas distintas:

```
App
 └── Facade (ServerFacade)
      ├── EmbeddedTomcatServer
      ├── StaticResourceExtractor
      └── ServletRegistry
           └── List<ServletRegistration>  ← ApplicationFactory
                └── Servlets
                     └── Template Method (ApiServlet)
                          └── Strategy (Map<String, ApiHandler>)
                               └── Servicios de aplicación
                                    └── State (mesas) / persistencia
```

| Patrón | Capa | Rol |
|--------|------|-----|
| **Facade** | Infraestructura / arranque | Simplificar el inicio del servidor |
| **Factory Method** | Composición | Crear `ServletRegistration` con servicios inyectados |
| **Template Method** | Servlet base | Flujo HTTP común (JSON, CORS, errores) |
| **Strategy** | Routing | Un handler por ruta, sin `switch` |
| **State** | Dominio (mesas) | Transiciones de estado de mesa |

### Servlets y handlers

| Servlet | Mapping | Handler | Ruta | Método |
|---------|---------|---------|------|--------|
| `CategoriasServlet` | `/api/categorias/*` | `ListarCategoriasHandler` | `/` | GET |
| `ProductosServlet` | `/api/productos/*` | `ListarProductosHandler` | `/` | GET |
| `PedidosServlet` | `/api/pedidos/*` | `ListarPedidosCompletadosHandler` | `/completados` | GET |
| `PedidosServlet` | `/api/pedidos/*` | `ListarPedidosPorMesaHandler` | `/mesa/{numero}` | GET |
| `CocinaServlet` | `/api/cocina/*` | `ListarCocinaHandler` | `/` | GET |
| `CocinaServlet` | `/api/cocina/*` | `EnviarCocinaHandler` | `/` | POST |
| `CocinaServlet` | `/api/cocina/*` | `DespacharPedidoHandler` | `/{ticket}/despachar` | POST |
| `MesasServlet` | `/api/mesas/*` | `ListarMesasHandler` | `/` | GET |
| `MesasServlet` | `/api/mesas/*` | `ActualizarMesaHandler` | `/{numero}` | POST |
| `MesasServlet` | `/api/mesas/*` | `CobrarMesaHandler` | `/{numero}/pagar` | POST |

### Patrones descartados para este proyecto

| Patrón | Motivo de descarte |
|--------|-------------------|
| Singleton | Oculta dependencias; la factory con instancias explícitas es preferible |
| Abstract Factory | Sobredimensionado para el grafo actual de servicios |
| Decorator | No justifica capas adicionales para CORS/JSON |
| Observer | No hay flujo de eventos en la capa HTTP |
| Chain of Responsibility | Excesivo para ~10 rutas REST |

## Licencia

Proyecto académico — Universidad Tecnológica del Perú (UTP).
