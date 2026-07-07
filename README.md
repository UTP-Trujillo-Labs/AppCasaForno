# AppCasaForno

Sistema de gestión para el restaurante **Casa Forno**. Permite tomar pedidos, monitorear la cocina y administrar el estado de las mesas, a través de una interfaz web servida por un servidor Tomcat embebido.

## Requisitos

- Java 21 o superior
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
├── domain/model/                     # Entidades y DTOs del dominio
├── application/                      # Lógica de negocio (servicios)
├── infraestructure/
│   ├── persistence/                  # Almacenamiento en memoria
│   ├── server/                       # Tomcat embebido y fachada de arranque
│   ├── web/                          # ApiHandler, ServletRegistration, ServletRegistry
│   └── util/                         # Utilidades (JSON, helpers)
└── presentation/
    ├── factory/ApplicationFactory.java
    ├── handler/                      # Estrategias por ruta de la API
    └── servlet/                      # Servlets REST (ApiServlet)

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
| **Dominio** | `domain.model` | Modelos de datos: `Producto`, `Mesa`, `TicketCocina`, DTOs de request/response |
| **Aplicación** | `application` | Casos de uso: `PedidosService`, `MesasServicio`, `CategoriaServicio` |
| **Infraestructura** | `infraestructure` | Servidor Tomcat, registro web, persistencia en memoria y utilidades |
| **Presentación** | `presentation` | Servlets REST, handlers y factory de composición |

### Flujo de una petición

```
Cliente (navegador)
    → Tomcat embebido (puerto 8080)
        → Archivos estáticos (/css, /js, /pages, index.html)
        → Servlets API (/api/*)
            → Servicio de aplicación
                → Persistencia en memoria
            → Respuesta JSON (Jackson)
```

`ServerFacade` orquesta el arranque: extrae archivos estáticos (compatible con IDE y JAR), configura Tomcat y registra los servlets de la API.

## Módulos funcionales

### Toma de pedidos (`#pedidos`)

- Consulta categorías y catálogo de productos con filtros.
- Muestra mesas disponibles.
- Envía pedidos a cocina generando un ticket.

### Monitor de cocina (`#cocina`)

- Lista los pedidos pendientes en tiempo real (polling cada pocos segundos).

### Control de mesas (`#mesas`)

- Vista del salón con estados visuales (libre, ocupada, reservada).
- La API de mesas está disponible; la pantalla actual opera de forma local en el navegador.

### Inventario y delivery

- Pantallas de maquetación incluidas en la interfaz; sin backend implementado aún.

## API REST

Base URL: `http://localhost:8080/api`

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/categorias/` | Lista todas las categorías de productos |
| `GET` | `/pedidos/productos` | Lista productos. Query params opcionales: `categoria`, `busqueda` |
| `GET` | `/pedidos/pendientes` | Tickets pendientes en cocina |
| `GET` | `/pedidos/completados` | Tickets completados (histórico) |
| `GET` | `/pedidos/cocina` | Alias de tickets pendientes |
| `POST` | `/pedidos/cocina` | Envía un pedido a cocina. Body JSON: `EnvioCocinaRequest` |
| `GET` | `/mesas/` | Resumen y listado de mesas |
| `PUT` | `/mesas/{numero}` | Actualiza el estado de una mesa. Body JSON: `{ "estado": "LIBRE" \| "OCUPADO" \| "RESERVADO" }` |

Todas las respuestas de la API son JSON. Los errores devuelven un objeto `ErrorResponse` con el mensaje descriptivo.

### Ejemplo: enviar pedido a cocina

```http
POST /api/pedidos/cocina
Content-Type: application/json

{
  "cliente": "Juan Pérez",
  "mesa": 3,
  "items": [
    { "productoId": "p1", "cantidad": 2 },
    { "productoId": "b1", "cantidad": 1 }
  ]
}
```

## Tecnologías

| Componente | Tecnología |
|------------|------------|
| Lenguaje | Java 21 |
| Servidor HTTP | Apache Tomcat Embed 10.1 |
| Serialización JSON | Jackson 2.18 |
| Frontend | HTML, CSS y JavaScript vanilla (SPA por hash) |
| Build | Maven |

## Patrones de diseño

La capa de presentación aplica cuatro patrones GoF sin frameworks ni anotaciones.

Referencias: [Refactoring.Guru — Design Patterns](https://refactoring.guru/design-patterns)

### Patrón 1: Template Method (Comportamiento)

**Referencia:** [Template Method](https://refactoring.guru/design-patterns/template-method)

Define el esqueleto de un algoritmo en una clase base, delegando algunos pasos a las subclases.

**Implementación:** `ApiServlet` centraliza el flujo HTTP común:

- Preparar cabeceras JSON y CORS
- Responder a `OPTIONS`
- Capturar `IllegalArgumentException` y devolver HTTP 400
- Normalizar el path de la petición

Cada servlet concreto (`PedidosServlet`, `MesasServlet`, `CategoriasServlet`) configura los mapas de handlers y delega el flujo a la clase base.

```
ApiServlet (abstracta)
├── doGet()      → prepareJsonResponse + try/catch + handleGet()
├── doPost()     → prepareJsonResponse + try/catch + handlePost()
├── doOptions()  → CORS (implementación única)
└── handleGet()  → abstracto (cada servlet lo define)
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

Cada ruta (`/productos`, `/cocina`, `/pendientes`, etc.) se implementa como una clase `ApiHandler` independiente. El servlet consulta el mapa por path y delega:

```java
ApiHandler handler = rutas.get(path);
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

### Combinación de patrones

Los cuatro patrones se complementan en capas distintas:

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
```

| Patrón | Capa | Rol |
|--------|------|-----|
| **Facade** | Infraestructura / arranque | Simplificar el inicio del servidor |
| **Factory Method** | Composición | Crear `ServletRegistration` con servicios inyectados |
| **Template Method** | Servlet base | Flujo HTTP común (JSON, CORS, errores) |
| **Strategy** | Routing | Un handler por ruta, sin `switch` |

### Handlers implementados

| Servlet | Handler | Ruta | Método |
|---------|---------|------|--------|
| `CategoriasServlet` | `ListarCategoriasHandler` | `/` | GET |
| `PedidosServlet` | `ListarProductosHandler` | `/productos` | GET |
| `PedidosServlet` | `ListarPedidosPendientesHandler` | `/pendientes` | GET |
| `PedidosServlet` | `ListarPedidosCompletadosHandler` | `/completados` | GET |
| `PedidosServlet` | `ListarCocinaHandler` | `/cocina` | GET |
| `PedidosServlet` | `EnviarCocinaHandler` | `/cocina` | POST |
| `MesasServlet` | `ListarMesasHandler` | `/` | GET |
| `MesasServlet` | `ActualizarMesaHandler` | `/{numero}` | PUT |

### Patrones descartados para este proyecto

| Patrón | Motivo de descarte |
|--------|-------------------|
| Singleton | Oculta dependencias; la factory con instancias explícitas es preferible |
| Abstract Factory | Sobredimensionado para tres servicios |
| Decorator | No justifica capas adicionales para CORS/JSON |
| Observer | No hay flujo de eventos en la capa HTTP |
| Chain of Responsibility | Excesivo para ~10 rutas REST |

## Licencia

Proyecto académico — Universidad Tecnológica del Perú (UTP).
