App.registerPage("pedidos", initPedidos);

function initPedidos() {
  const state = {
    categoria: "pizzas",
    busqueda: "",
    carrito: [],
    categorias: [],
    productos: [],
    mesasDisponibles: [],
  };

  bindPedidoCreadoModal();
  bindPedidosEvents(state);
  cargarPedidos(state);
}

async function cargarPedidos(state) {
  try {
    await Promise.all([fetchCategorias(state), fetchProductos(state), fetchMesasDisponibles(state)]);
    renderCategorias(state);
    renderProductos(state);
    renderMesasSelect(state);
    renderCarrito(state);
  } catch (err) {
    console.error(err);
    document.getElementById("catalogo-categorias").innerHTML =
      '<p class="catalogo-vacio">Error al cargar el catálogo.</p>';
  }
}

async function fetchCategorias(state) {
  const response = await fetch("/api/categorias");
  if (!response.ok) throw new Error("Error al obtener categorías");
  state.categorias = await response.json();
}

async function fetchProductos(state) {
  const params = new URLSearchParams({ categoria: state.categoria });
  if (state.busqueda) params.set("busqueda", state.busqueda);

  const response = await fetch(`/api/pedidos/productos?${params}`);
  if (!response.ok) throw new Error("Error al obtener productos");
  state.productos = await response.json();
}

async function fetchMesasDisponibles(state) {
  const response = await fetch("/api/mesas/");
  if (!response.ok) throw new Error("Error al obtener mesas");

  const data = await response.json();
  state.mesasDisponibles = data.mesas.filter((mesa) => mesa.estado === "libre");
}

function renderMesasSelect(state) {
  const select = document.getElementById("pedido-mesa");
  if (!select) return;

  if (state.mesasDisponibles.length === 0) {
    select.innerHTML = '<option value="">Sin mesas disponibles</option>';
    return;
  }

  select.innerHTML = [
    '<option value="">Selecciona una mesa</option>',
    ...state.mesasDisponibles.map(
      (mesa) =>
        `<option value="${mesa.numero}">Mesa ${mesa.numero}</option>`
    ),
  ].join("");
}

function bindPedidosEvents(state) {
  document.getElementById("catalogo-buscar")?.addEventListener("input", async (e) => {
    state.busqueda = e.target.value.trim().toLowerCase();
    await fetchProductos(state);
    renderProductos(state);
  });

  document.getElementById("catalogo-categorias")?.addEventListener("click", async (e) => {
    const btn = e.target.closest("[data-categoria]");
    if (!btn) return;
    state.categoria = btn.dataset.categoria;
    renderCategorias(state);
    await fetchProductos(state);
    renderProductos(state);
  });

  document.getElementById("catalogo-grid")?.addEventListener("click", (e) => {
    const btn = e.target.closest("[data-producto-id]");
    if (!btn) return;
    agregarAlCarrito(state, btn.dataset.productoId);
  });

  document.getElementById("btn-enviar-cocina")?.addEventListener("click", () => enviarACocina(state));
  document.getElementById("btn-anular-pedido")?.addEventListener("click", () => anularPedido(state));
}

function renderCategorias(state) {
  const nav = document.getElementById("catalogo-categorias");
  if (!nav) return;
  nav.innerHTML = "";

  // INPORTANTE: Construye el HTML boton por boton
  for (const cat of state.categorias) {
    nav.innerHTML += `<button type="button" class="catalogo-cat${state.categoria === cat.id ? " active" : ""}" data-categoria="${cat.id}">${cat.label}</button>`;
  }

  // IMPORTANTE: Sintaxis actual para construir HTML usando map y join
  // nav.innerHTML = state.categorias
  //   .map(
  //     (cat) =>
  //       `<button type="button" class="catalogo-cat${state.categoria === cat.id ? " active" : ""}" data-categoria="${cat.id}">${cat.label}</button>`
  //   )
  //   .join("");
}

function renderProductos(state) {
  const grid = document.getElementById("catalogo-grid");
  if (!grid) return;

  if (state.productos.length === 0) {
    grid.innerHTML = '<p class="catalogo-vacio">No se encontraron productos.</p>';
    return;
  }

  grid.innerHTML = state.productos
    .map(
      (p) => `
      <article class="producto-card">
        <img src="${p.imagen}" alt="${p.nombre}" class="producto-img" />
        <h3 class="producto-nombre">${p.nombre}</h3>
        <p class="producto-precio">$ ${p.precio.toFixed(2)}</p>
        <button type="button" class="btn-agregar" data-producto-id="${p.id}">+ Agregar</button>
      </article>`
    )
    .join("");
}

function agregarAlCarrito(state, productoId) {
  const producto = state.productos.find((p) => p.id === productoId);
  if (!producto) return;

  const item = state.carrito.find((i) => i.id === productoId);
  if (item) {
    item.cantidad += 1;
  } else {
    // const nuevoProducto = {
    //   id: producto.id,
    //   nombre: producto.nombre,
    //   precio: producto.precio,
    //   cantidad: 1,
    // }
    state.carrito.push({ ...producto, cantidad: 1 });
  }

  renderCarrito(state);
}

function renderCarrito(state) {
  const lista = document.getElementById("pedido-items");
  const totalEl = document.getElementById("catalogo-total");
  if (!lista || !totalEl) return;

  if (state.carrito.length === 0) {
    lista.innerHTML = '<li class="pedido-items-empty">Sin productos en el pedido.</li>';
    totalEl.textContent = "$ 0.00";
    return;
  }

  lista.innerHTML = state.carrito
    .map((item) => `<li>${item.cantidad} de ${item.nombre}</li>`)
    .join("");

  const total = state.carrito.reduce((sum, item) => sum + item.precio * item.cantidad, 0);
  totalEl.textContent = `$ ${total.toFixed(2)}`;
}

async function enviarACocina(state) {
  if (state.carrito.length === 0) {
    alert("Agrega productos al pedido antes de enviar.");
    return;
  }

  const cliente = document.getElementById("pedido-cliente")?.value.trim() || "—";
  const mesa = document.getElementById("pedido-mesa")?.value.trim();
  const nota = document.getElementById("pedido-nota")?.value.trim() || "";

  if (!mesa) {
    alert("Selecciona una mesa disponible.");
    return;
  }

  const payload = {
    cliente,
    mesa,
    nota,
    items: state.carrito.map((item) => ({
      productoId: item.id,
      cantidad: item.cantidad,
    })),
  };

  try {
    // Ir al metodo JAVA para enviar el pedido a cocina
    const response = await fetch("/api/pedidos/cocina", {
      method: "POST",
      headers: { "Content-Type": "application/json" }, // Indicar el tipo de dato que se envia al JAVA
      body: JSON.stringify(payload), // Convertir el objeto a JSON
    });

    // EJEMPLO:
    // let obj = {nombre: "manuel", edad: 31}
    // JSON.stringify(obj)
    // RESULTADO: '{"nombre":"manuel","edad":31}'

    const result = await response.json();
    if (!response.ok) {
      alert(result.error || "No se pudo enviar el pedido.");
      return;
    }

    mostrarPedidoCreadoModal({ ...result, nota });
    anularPedido(state);
    await fetchMesasDisponibles(state);
    renderMesasSelect(state);
  } catch (err) {
    console.error(err);
    alert("Error al enviar el pedido a cocina.");
  }
}

function mostrarPedidoCreadoModal(pedido) {
  const modal = document.getElementById("pedido-creado-modal");
  const titulo = document.getElementById("pedido-creado-titulo");
  const contenido = document.getElementById("pedido-creado-contenido");
  if (!modal || !titulo || !contenido) return;

  titulo.textContent = `Pedido enviado — Ticket #${pedido.ticket}`;
  contenido.innerHTML = `
    <article class="app-modal-ticket">
      <header class="app-modal-ticket-header">
        <strong>Ticket #${pedido.ticket}</strong>
        <span>Cliente: ${pedido.cliente || "—"}</span>
        <span>Mesa: ${pedido.mesa || "—"}</span>
      </header>
      <ul class="app-modal-items">
        ${(pedido.items || []).map((item) => `<li>${item}</li>`).join("")}
      </ul>
      ${pedido.nota ? `<p class="app-modal-nota">Nota: ${pedido.nota}</p>` : ""}
    </article>
    <p class="app-modal-hint">${pedido.message || "El pedido fue enviado a cocina."}</p>`;

  modal.hidden = false;
}

function cerrarPedidoCreadoModal() {
  const modal = document.getElementById("pedido-creado-modal");
  if (modal) modal.hidden = true;
}

function bindPedidoCreadoModal() {
  const modal = document.getElementById("pedido-creado-modal");
  if (!modal || modal.dataset.bound === "1") return;
  modal.dataset.bound = "1";

  document.getElementById("pedido-creado-cerrar")?.addEventListener("click", cerrarPedidoCreadoModal);
  document.getElementById("pedido-creado-aceptar")?.addEventListener("click", cerrarPedidoCreadoModal);
  modal.addEventListener("click", (e) => {
    if (e.target === modal) cerrarPedidoCreadoModal();
  });
}

function anularPedido(state) {
  state.carrito.length = 0;
  const notaEl = document.getElementById("pedido-nota");
  if (notaEl) notaEl.value = "";
  renderCarrito(state);
}
