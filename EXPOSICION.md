# Guía de exposición — AppCasaForno

Pasos recomendados para explicar el proyecto de forma académica en el curso de **Algoritmos y Estructura de Datos**.

Duración sugerida: **10–15 minutos** de exposición + **3–5 minutos** de demo + preguntas.

---

## 1. Abrir con el problema (1 min)

No empieces por el código. Plantea el escenario real:

> Un restaurante necesita gestionar mesas, tomar pedidos, enviarlos a cocina en orden y controlar el stock.

Deja claro **qué problemas de datos** resuelve el sistema:

| Problema del negocio | Pregunta de la materia |
|----------------------|------------------------|
| Los pedidos deben atenderse en orden de llegada | ¿Qué estructura garantiza FIFO? |
| Buscar una mesa o un producto por ID | ¿Lista lineal o acceso por clave? |
| Filtrar el catálogo por categoría o nombre | ¿Qué algoritmo de recorrido/búsqueda usamos? |
| Guardar pedidos ya despachados | ¿Cola o lista histórica? |
| Cambiar el estado de una mesa con reglas | ¿Cómo modelamos transiciones válidas? |

**Tip:** Menciona que el proyecto es un sistema web en Java, pero que el foco de la exposición son las **estructuras de datos en memoria** y los **algoritmos** que operan sobre ellas, no Tomcat ni el frontend.

---

## 2. Mostrar el panorama general (1–2 min)

Explica el flujo de un pedido en una frase y un diagrama simple (pizarra o diapositiva):

```
Cliente elige productos
    → se valida stock y se descuenta
    → se crea un TicketCocina
    → se encola en ColaPedidos (FIFO)
    → cocina despacha (sale de la cola → histórico)
    → mesa cobra y se libera
```

Módulos a mencionar solo de pasada:

- **Pedidos** — catálogo + envío a cocina  
- **Cocina** — cola de tickets pendientes  
- **Mesas** — estados libre / reservada / ocupada  
- **Inventario** — stock y pedidos completados  

Arquitectura en capas (dominio → aplicación → persistencia): útil para ubicar **dónde viven** las estructuras (`infraestructure/persistence/`), no para profundizar en hexagonal.

---

## 3. Núcleo académico: estructuras de datos (4–5 min)

Este es el bloque más importante. Dedica tiempo a **por qué** se eligió cada estructura.

### 3.1 Cola — `ColaPedidos` (`Queue` + `LinkedList`)

**Qué modela:** pedidos pendientes en cocina.

**Operaciones clave:**

| Operación | Método | Complejidad típica |
|-----------|--------|--------------------|
| Encolar pedido | `encolar` → `add` | O(1) |
| Ver el siguiente | `verSiguiente` → `peek` | O(1) |
| Desencolar (FIFO puro) | `desencolar` → `poll` | O(1) |
| Extraer por ticket | `extraerPorTicket` (recorrido + remove) | O(n) |
| Filtrar por mesa | `listarPorMesa` / `extraerPorMesa` | O(n) |

**Qué debes explicar en clase:**

1. La cocina es un problema clásico de **cola (FIFO)**: el primero en llegar debería ser el primero en atenderse.
2. En la práctica el despacho usa `extraerPorTicket` (selección por ID), no solo `poll`. Es una oportunidad para discutir:
   - cola “pura” vs. cola con búsqueda
   - trade-off entre fidelidad al modelo y necesidad del negocio
3. Implementación con `LinkedList` como `Queue`: inserción/eliminación por extremos eficiente frente a un arreglo fijo.

**Archivo a abrir en vivo:** `ColaPedidos.java`.

### 3.2 Lista dinámica — `HistoricoPedidos` y `ProductosStore` (`ArrayList`)

**Qué modela:**

- Histórico: tickets completados / pagados (crece con el tiempo).
- Catálogo: productos con stock mutable.

**Qué debes explicar:**

1. El histórico **no** necesita FIFO estricto: se agrega al final y se consulta por filtro → `ArrayList` encaja.
2. Acceso por índice y crecimiento amortizado: útil si mencionan complejidad de `add` / recorrido.
3. Contraste con la cola: mismo tipo de dato “colección de tickets”, **rol distinto** → estructura distinta.

**Archivos:** `HistoricoPedidos.java`, `ProductosStore.java`.

### 3.3 Tabla hash / mapa — `MesasStore` (`ConcurrentHashMap`)

**Qué modela:** mesas indexadas por número (`Map<Integer, Mesa>`).

**Qué debes explicar:**

1. Búsqueda de mesa por ID: esperado **O(1)** promedio con hash, frente a O(n) en una lista.
2. Por qué un mapa y no un arreglo de tamaño fijo: aunque hay 6 mesas, el mapa expresa “clave → valor” y facilita `get` / `put` / `containsKey`.
3. Al listar, se copia a una lista y se **ordena** por número de mesa (`Comparator`) → introduce el tema de ordenamiento.

**Archivo:** `MesasStore.java`.

### 3.4 Resumen visual (diapositiva recomendada)

| Estructura | Clase | ADT | Uso en el negocio |
|------------|-------|-----|-------------------|
| Cola | `ColaPedidos` | Queue | Pedidos pendientes en cocina |
| Lista | `HistoricoPedidos` | List | Pedidos ya despachados |
| Lista | `ProductosStore` | List | Catálogo e inventario |
| Mapa | `MesasStore` | Map | Mesas por ID |
| Lista inmutable | `CategoriasStore` | List | Catálogo fijo de categorías |

---

## 4. Algoritmos que conviene detallar (3–4 min)

### 4.1 Filtrado y búsqueda lineal — catálogo

En `ProductService.listarProductos`:

- Recorrido de la lista de productos.
- Filtro por categoría (igualdad).
- Búsqueda por nombre con `contains` (subcadena, case-insensitive).

**Mensaje académico:** es **búsqueda / filtrado lineal O(n)**. Con pocos productos es suficiente; si el catálogo creciera, se podría hablar de índices, maps por categoría o estructuras de texto (fuera de alcance, pero demuestra criterio).

### 4.2 Validación de stock al crear pedido

En `CocinaServicio.enviarACocina`:

1. Recorrer ítems del pedido.
2. Buscar producto por ID.
3. Validar cantidad y stock.
4. Acumular total y descontar stock.
5. Encolar el ticket.

Es un **algoritmo secuencial de validación + transformación** (request → `TicketCocina`). Ideal para recorrer paso a paso en la pizarra.

### 4.3 Despacho: cola → histórico

`despacharPedido`:

1. Buscar ticket en la cola.
2. Removerlo.
3. Cambiar estado a COMPLETADO.
4. Registrar en el histórico.

Relaciona **cambio de estructura** con **cambio de estado del dato**.

### 4.4 Cobro de mesa

Combinación de:

- consultar pendientes en la cola por mesa
- consultar completados en el histórico
- marcar pagados y liberar mesa

Sirve para mostrar que un caso de uso **compone** varias operaciones sobre distintas estructuras.

### 4.5 Ordenamiento de mesas

`MesasStore.listar()` ordena por número. Menciona:

- por qué se ordena al listar (presentación consistente)
- complejidad de ordenar *n* mesas (con *n* pequeño es irrelevante; el punto es el criterio)

---

## 5. Puente opcional: patrones (1–2 min, sin robar protagonismo)

Si el profesor valora diseño, menciona **brevemente** (no dediques más de 2 minutos):

| Patrón | Relación con AED |
|--------|------------------|
| **State** (mesas) | Máquina de estados: `LIBRE → RESERVADA → OCUPADA → LIBRE` |
| **Strategy** (handlers) | Cada ruta es un “algoritmo” intercambiable de manejo HTTP |
| **Template Method** (`ApiServlet`) | Esqueleto de algoritmo con pasos fijos y variables |

Enfoca el **State** como modelo de transiciones, no como teoría GoF exhaustiva. Las reglas viven en `LibreState`, `ReservadaState`, `OcupadaState`.

---

## 6. Demostración en vivo (3–5 min)

Orden sugerido (muestra estructuras en acción):

1. **Arrancar** la app y abrir `http://localhost:8080`.
2. **Pedidos:** filtrar productos (algoritmo de filtrado) y enviar un pedido.
3. **Cocina:** mostrar el ticket en cola; despachar (cola → histórico).
4. **Mesas:** avanzar estado y cobrar (mapa de mesas + composición cola/histórico).
5. **Inventario:** stock descontado y pedidos completados.

Mientras demuestras, nombra en voz alta la estructura: *“este pedido acaba de entrar a la cola”*, *“al despachar sale de la cola y entra a la lista histórica”*.

---

## 7. Cierre: complejidad y mejoras (1 min)

Cierra con análisis crítico (muy valorado en AED):

**Lo que está bien para el tamaño actual**

- Cola para pendientes, lista para histórico, mapa para mesas.
- Persistencia en memoria: suficiente para demo académica.

**Limitaciones / posibles mejoras** (elige 1 o 2):

| Idea | Concepto AED |
|------|----------------|
| Índice `Map<String, Producto>` por ID | Búsqueda O(1) en catálogo |
| Cola de prioridad por mesa VIP o tiempo | Priority Queue |
| Persistencia en archivo/BD | Estructuras persistentes vs. volátiles |
| Despacho estrictamente FIFO (`poll`) | Fidelidad al ADT cola |
| Estructuras concurrentes / locks | Acceso concurrente a la cola |

Evita decir “está mal”; di “con *n* pequeño basta; si escalara, haríamos X”.

---

## 8. Guion sugerido de diapositivas

1. Título y integrantes  
2. Problema del restaurante  
3. Objetivos (qué estructuras/algoritmos se aplican)  
4. Flujo del pedido (diagrama)  
5. Tabla de estructuras (cola, lista, mapa)  
6. Zoom: `ColaPedidos` (FIFO + operaciones)  
7. Zoom: filtrado de productos O(n)  
8. Zoom: mapa de mesas + ordenamiento  
9. (Opcional) State de mesas  
10. Demo  
11. Complejidad y mejoras  
12. Conclusiones y preguntas  

---

## 9. Checklist antes de exponer

- [ ] Poder explicar **por qué** cola y no lista para cocina.  
- [ ] Poder explicar **por qué** mapa y no lista para mesas.  
- [ ] Mencionar al menos una **complejidad** (O(1) encolar, O(n) filtrar).  
- [ ] Tener un ejemplo numérico corto (ej. 3 pedidos en cola, despachar el del medio).  
- [ ] App compilando y corriendo (`mvn compile` / ejecutar `App`).  
- [ ] Saber abrir en el IDE: `ColaPedidos`, `MesasStore`, `ProductService`, `CocinaServicio`.  
- [ ] Preparar 2–3 respuestas a preguntas frecuentes (sección siguiente).  

---

## 10. Preguntas frecuentes (prepáralas)

**¿Por qué `LinkedList` y no un arreglo circular para la cola?**  
`Queue` + `LinkedList` implementa el ADT cola con API clara (`add`/`poll`/`peek`). Un arreglo circular es válida didácticamente, pero aquí se prioriza el modelo FIFO del dominio sobre una implementación a mano.

**¿`extraerPorTicket` rompe el FIFO?**  
Opera sobre una cola, pero permite atender fuera de orden estricto. Es un diseño híbrido: estructura de cola + búsqueda lineal. Conviene decirlo con honestidad.

**¿Dónde está la recursión / árboles / grafos?**  
No son el foco de este proyecto. Si preguntan, indica que el dominio (restaurante operativo) se modela naturalmente con colas, listas y mapas; árboles o grafos encajarían en otros problemas (menú jerárquico, rutas de delivery, etc.).

**¿La persistencia en memoria es una estructura de datos?**  
Sí, en el sentido de que el estado vive en colecciones Java en RAM. No hay BD: al reiniciar se pierde. Eso refuerza el vínculo con AED (estructuras en memoria).

**¿Qué pasa si dos usuarios despachan a la vez?**  
`MesasStore` usa `ConcurrentHashMap`; la cola no está sincronizada de forma explícita. Puedes mencionar concurrencia como mejora futura.

---

## 11. Mensaje final recomendado

Termina con una frase que una negocio y materia:

> AppCasaForno aplica estructuras de datos clásicas a un problema real: la **cola** ordena el trabajo de cocina, el **mapa** localiza mesas en tiempo constante y las **listas** guardan catálogo e histórico, con algoritmos de recorrido, filtrado y validación sobre esas colecciones.

Eso deja claro que el proyecto no es “solo una web”, sino una aplicación concreta de **algoritmos y estructuras de datos**.
