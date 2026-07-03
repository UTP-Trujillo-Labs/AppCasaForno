App.registerPage("pedidos", initPedidos);

function initPedidos() {
  const state = {
    categoria: "pizzas",
    busqueda: "",
    carrito: [],
    categorias: [],
    productos: [],
  };

  bindPedidosEvents(state);
  cargarPedidos(state);
}

async function cargarPedidos(state) {
  try {
    await Promise.all([fetchCategorias(state), fetchProductos(state)]);
    renderCategorias(state);
    renderProductos(state);
    renderCarrito(state);
  } catch (err) {
    console.error(err);
    document.getElementById("catalogo-categorias").innerHTML =
      '<p class="catalogo-vacio">Error al cargar el catálogo.</p>';
  }
}

async function fetchCategorias(state) {
  const response = await fetch("/api/pedidos/categorias");
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

  nav.innerHTML = state.categorias
    .map(
      (cat) =>
        `<button type="button" class="catalogo-cat${state.categoria === cat.id ? " active" : ""}" data-categoria="${cat.id}">${cat.label}</button>`
    )
    .join("");
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
  const mesa = document.getElementById("pedido-mesa")?.value.trim() || "—";

  const payload = {
    cliente,
    mesa,
    items: state.carrito.map((item) => ({
      productoId: item.id,
      cantidad: item.cantidad,
    })),
  };

  try {
    const response = await fetch("/api/pedidos/cocina", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });

    const result = await response.json();
    if (!response.ok) {
      alert(result.error || "No se pudo enviar el pedido.");
      return;
    }

    alert(`Pedido enviado a cocina\nTicket #${result.ticket}\nCliente: ${result.cliente}\nMesa: ${result.mesa}`);
    anularPedido(state);
  } catch (err) {
    console.error(err);
    alert("Error al enviar el pedido a cocina.");
  }
}

function anularPedido(state) {
  state.carrito.length = 0;
  renderCarrito(state);
}
