# Patrones de diseño — AppCasaForno

Documentación de los patrones aplicados en el proyecto, incluyendo el patrón que permite la interacción entre el **JavaScript del frontend** y las **APIs REST** del backend.

Referencia general: [Refactoring.Guru — Design Patterns](https://refactoring.guru/design-patterns)

---

## Resumen

| # | Patrón | Tipo | Capa | Rol en el proyecto |
|---|--------|------|------|--------------------|
| 1 | **Cliente–Servidor + REST** | Arquitectónico | Frontend ↔ Backend | Interacción JS ↔ API vía HTTP/JSON |
| 2 | **Template Method** | Comportamiento (GoF) | Presentación | Flujo HTTP común en `ApiServlet` |
| 3 | **Strategy** | Comportamiento (GoF) | Presentación | Un handler por ruta (`ApiHandler`) |
| 4 | **Facade** | Estructural (GoF) | Infraestructura | Arranque del servidor (`ServerFacade`) |
| 5 | **Factory Method** | Creacional (GoF) | Composición | Catálogo de servlets (`ApplicationFactory`) |
| 6 | **State** | Comportamiento (GoF) | Dominio | Transiciones de mesa |

---

## 1. Cliente–Servidor + API REST (interacción JavaScript ↔ API)

Este es el patrón que **conecta el frontend con el backend**. No es un patrón GoF clásico, sino un **estilo arquitectónico**: el navegador actúa como cliente y el servidor Java como proveedor de servicios.

### Idea

```
┌─────────────────────────────┐         HTTP + JSON          ┌─────────────────────────────┐
│  Cliente (navegador)        │  ─────────────────────────►  │  Servidor (Tomcat + Java)   │
│  HTML / CSS / JavaScript    │  ◄─────────────────────────  │  Servlets /api/*            │
│  fetch() → consume la API   │         respuesta JSON       │  Servicios + persistencia   │
└─────────────────────────────┘                              └─────────────────────────────┘
```

- El **cliente** no conoce la lógica interna de cocina, stock o mesas.
- El **servidor** no renderiza pantallas de negocio: solo expone recursos REST y sirve archivos estáticos.
- El contrato de comunicación es **JSON** sobre **HTTP** (`GET` / `POST`).

### Cómo se concreta en el código

**Lado JavaScript (cliente):** cada página usa `fetch` contra rutas `/api/...`.

Ejemplo — enviar pedido a cocina (`pedidos.js`):

```javascript
const response = await fetch("/api/cocina/", {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify(payload),
});
const result = await response.json();
```

**Lado Java (servidor):** el servlet resuelve la ruta, el handler lee el JSON, llama al servicio y escribe la respuesta.

Ejemplo — `EnviarCocinaHandler`:

```java
EnvioCocinaRequest request = JsonUtil.read(req, EnvioCocinaRequest.class);
EnvioCocinaResponse response = EnvioCocinaResponse.from(
        cocinaServicio.enviarACocina(request));
resp.setStatus(HttpServletResponse.SC_CREATED);
JsonUtil.write(resp, response);
```

### Mapa de interacciones JS → API

| Pantalla (JS) | Método | Endpoint | Acción |
|---------------|--------|----------|--------|
| `pedidos.js` | `GET` | `/api/categorias` | Listar categorías |
| `pedidos.js` | `GET` | `/api/productos?...` | Filtrar catálogo |
| `pedidos.js` | `GET` | `/api/mesas/` | Mesas disponibles |
| `pedidos.js` | `POST` | `/api/cocina/` | Enviar pedido |
| `cocina.js` | `GET` | `/api/cocina/` | Listar tickets pendientes |
| `cocina.js` | `POST` | `/api/cocina/{ticket}/despachar` | Despachar ticket |
| `mesas.js` | `GET` | `/api/mesas/` | Listar mesas |
| `mesas.js` | `POST` | `/api/mesas/{numero}` | Avanzar estado |
| `mesas.js` | `GET` | `/api/pedidos/mesa/{numero}` | Pedidos de la mesa |
| `mesas.js` | `POST` | `/api/mesas/{numero}/pagar` | Cobrar mesa |
| `inventario.js` | `GET` | `/api/productos`, `/api/categorias` | Stock / categorías |
| `inventario.js` | `GET` | `/api/pedidos/completados` | Histórico |

### Principios REST aplicados

| Principio | En AppCasaForno |
|-----------|-----------------|
| Recursos identificados por URL | `/api/cocina/`, `/api/mesas/3`, `/api/productos` |
| Verbos HTTP con significado | `GET` consulta, `POST` crea o ejecuta acción |
| Representación uniforme | JSON con Jackson (`JsonUtil`) |
| Sin estado de sesión en el cliente hacia el servidor | Cada request lleva lo necesario (body / path) |
| Separación UI / lógica | La SPA solo pinta; las reglas viven en servicios Java |

### SPA por hash (complemento en el cliente)

Además del Cliente–Servidor, el frontend es una **SPA (Single Page Application)** ligera:

- `index.html` es el shell.
- `app.js` cambia de vista con `location.hash` (`#pedidos`, `#cocina`, `#mesas`, `#inventario`).
- Carga fragmentos HTML (`/pages/*.html`) y scripts (`/js/pages/*.js`) bajo demanda.

Eso **no reemplaza** al patrón Cliente–Servidor: la SPA sigue consumiendo la API con `fetch`. Solo organiza la navegación en el navegador.

### Por qué documentarlo

Sin este patrón, no habría vínculo entre la interfaz y la lógica de negocio. Los patrones GoF del backend organizan el servidor; **Cliente–Servidor + REST** es el puente con JavaScript.

---

## 2. Template Method

**Tipo:** comportamiento  
**Referencia:** [Template Method](https://refactoring.guru/design-patterns/template-method)

### Problema que resuelve

Todos los servlets REST necesitan el mismo flujo HTTP: cabeceras JSON/CORS, manejo de `OPTIONS`, resolución de ruta, captura de errores 400/404. Sin un esqueleto común, ese código se duplicaría en cada servlet.

### Implementación

`ApiServlet` define el algoritmo fijo:

1. Preparar respuesta JSON (`JsonUtil.prepareJsonResponse`).
2. Normalizar el path.
3. Resolver el `ApiHandler` correspondiente.
4. Ejecutar `handler.handle(...)` o devolver 404.
5. Si hay `IllegalArgumentException`, devolver 400.

Los métodos `doGet`, `doPost` y `doOptions` están marcados como `final`: las subclases no alteran el flujo; solo aportan los mapas de handlers en el constructor.

```
ApiServlet (plantilla)
├── doGet / doPost  → dispatch(handlers)
├── doOptions       → CORS
└── dispatch        → prepareJson + resolveHandler + handle
     └── PedidosServlet, CocinaServlet, ProductosServlet, …
```

### Beneficio

Un solo lugar para políticas HTTP transversales; los servlets concretos se reducen a declarar rutas.

---

## 3. Strategy

**Tipo:** comportamiento  
**Referencia:** [Strategy](https://refactoring.guru/design-patterns/strategy)

### Problema que resuelve

Evitar un `switch`/`if` gigante por cada ruta. Cada endpoint debe poder añadirse o cambiarse sin modificar el servlet base.

### Implementación

Interfaz común:

```java
@FunctionalInterface
public interface ApiHandler {
    void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException;
}
```

Cada ruta es una estrategia concreta, por ejemplo:

- `ListarCocinaHandler`
- `EnviarCocinaHandler`
- `DespacharPedidoHandler`
- `ActualizarMesaHandler`
- `CobrarMesaHandler`
- …

El servlet elige la estrategia con un `Map<String, ApiHandler>` (GET y POST por separado):

```java
public CocinaServlet(CocinaServicio cocinaServicio) {
    super(
        Map.of("/", new ListarCocinaHandler(cocinaServicio)),
        Map.of(
            "/", new EnviarCocinaHandler(cocinaServicio),
            "/{ticket}/despachar", new DespacharPedidoHandler(cocinaServicio)));
}
```

`resolveHandler` en `ApiServlet` también traduce paths reales (`/1048/despachar`) a plantillas (`/{ticket}/despachar`).

### Beneficio

Open/Closed: nueva ruta = nueva clase handler + una entrada en el mapa, sin tocar el algoritmo de `dispatch`.

---

## 4. Facade

**Tipo:** estructural  
**Referencia:** [Facade](https://refactoring.guru/design-patterns/facade)

### Problema que resuelve

Arrancar el sistema implica varios pasos técnicos (extraer estáticos, configurar Tomcat, registrar servlets). El punto de entrada no debería conocer esos detalles.

### Implementación

`ServerFacade` orquesta tres colaboradores:

| Colaborador | Responsabilidad |
|-------------|-----------------|
| `StaticResourceExtractor` | Extraer `/public` (IDE o JAR) |
| `EmbeddedTomcatServer` | Puerto, conector, start/stop |
| `ServletRegistry` | Registrar mappings `/api/*` |

```java
public class App {
    public static void main(String[] args) throws Exception {
        new ServerFacade().start();
    }
}
```

### Beneficio

`App` queda en una línea; la complejidad del arranque queda encapsulada detrás de la fachada.

---

## 5. Factory Method (composition root)

**Tipo:** creacional  
**Referencia:** [Factory Method](https://refactoring.guru/design-patterns/factory-method)

### Problema que resuelve

Crear servicios compartidos (`ColaPedidos`, `MesasServicio`, etc.) y los servlets que los usan, sin que `ServletRegistry` conozca tipos concretos ni se duplique el cableado.

### Implementación

`ApplicationFactory`:

1. Instancia stores y servicios compartidos (misma cola e histórico para cocina, pedidos y mesas).
2. Expone `crearRegistrosServlets()` → `List<ServletRegistration>`.
3. `ServletRegistry` solo recorre la lista y registra en Tomcat.

También existe `MesaStateFactory.from(EstadoMesa)`, que crea el objeto `MesaState` concreto a partir del enum: apoyo creacional del patrón State.

### Beneficio

Inyección de dependencias manual, un único “composition root”, fácil de explicar en un informe académico.

---

## 6. State

**Tipo:** comportamiento  
**Referencia:** [State](https://refactoring.guru/design-patterns/state)

### Problema que resuelve

Una mesa cambia de comportamiento según su estado (libre, reservada, ocupada). Codificar eso con muchos `if` en el servlet o en el JS acopla reglas de dominio a la presentación.

### Implementación

```
MesaContext
    └── MesaState (interfaz)
         ├── LibreState      → avanzar() → Reservada; ocupar() → Ocupada
         ├── ReservadaState  → …
         └── OcupadaState    → …
```

- `MesaContext` mantiene el estado actual y delega `avanzar()` / `ocupar()`.
- `ActualizarMesaHandler` **no** recibe el estado destino: el backend aplica la transición válida.
- El frontend solo dispara `POST /api/mesas/{numero}` y pinta el resultado.

### Beneficio

Las reglas de transición viven en el dominio; el Cliente–Servidor transporta la acción, no la lógica.

---

## Cómo se combinan

```
Navegador (JS)
  │  fetch /api/...   ← Cliente–Servidor + REST
  ▼
Tomcat
  └── Servlets (/api/*)
       └── Template Method (ApiServlet)
            └── Strategy (ApiHandler por ruta)
                 └── Servicios de aplicación
                      ├── State (mesas)
                      └── Persistencia en memoria
                             ▲
App → Facade → Factory Method (ensambla servlets + servicios)
```

| Capa | Patrones |
|------|----------|
| Comunicación UI ↔ API | Cliente–Servidor, REST, JSON |
| Arranque | Facade, Factory Method |
| HTTP / routing | Template Method, Strategy |
| Dominio de mesas | State (+ `MesaStateFactory`) |

---

## Relación con el frontend (vista rápida)

| Pieza JS | Rol respecto a los patrones |
|----------|----------------------------|
| `fetch("/api/...")` | Cliente del patrón Cliente–Servidor |
| `JSON.stringify` / `response.json()` | Serialización del contrato REST |
| `app.js` + hash (`#pedidos`, …) | SPA: navegación sin recargar el servidor |
| Handlers Java | Strategy que atiende cada llamada del cliente |

---

## Patrones descartados (y por qué)

| Patrón | Motivo |
|--------|--------|
| Singleton | Oculta dependencias; la factory con instancias explícitas es preferible |
| Abstract Factory | Sobredimensionado para el grafo actual de servicios |
| Decorator | No justifica capas extra solo para CORS/JSON |
| Observer | No hay bus de eventos en la capa HTTP |
| Chain of Responsibility | Excesivo para ~10 rutas REST |
| MVC estricto en el servidor | El servidor es API; la “Vista” está en el SPA |

---

## Cómo explicarlo en una exposición

1. Empieza por **Cliente–Servidor + REST**: “el JS no llama a Java directamente; llama a URLs con `fetch` y recibe JSON”.
2. Muestra un ejemplo de ida y vuelta (`POST /api/cocina/`).
3. Baja a la capa Java: Template Method + Strategy atienden esa petición.
4. Si hay tiempo, State en mesas y Facade/Factory en el arranque.

Frase de cierre sugerida:

> Los patrones GoF organizan el código Java; el patrón Cliente–Servidor con API REST es el que permite que el JavaScript interactúe con esa lógica a través de HTTP y JSON.
