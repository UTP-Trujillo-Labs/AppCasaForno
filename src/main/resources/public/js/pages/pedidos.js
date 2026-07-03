App.registerPage("pedidos", initPedidos);

const CATEGORIAS = [
  { id: "pizzas", label: "Pizzas" },
  { id: "alitas", label: "Alitas" },
  { id: "salchipapas", label: "salchipapas" },
  { id: "bebidas", label: "Bebidas" },
  { id: "pastas", label: "Pastas" },
];

const PRODUCTOS = [
  { id: "p1", nombre: "Pepperoni y carne", precio: 28.0, categoria: "pizzas", imagen: "/img/catalog/pizza.svg" },
  { id: "p2", nombre: "vegetariana", precio: 25.0, categoria: "pizzas", imagen: "/img/catalog/pizza.svg" },
  { id: "p3", nombre: "Americana", precio: 24.0, categoria: "pizzas", imagen: "/img/catalog/pizza.svg" },
  { id: "p4", nombre: "Margarita", precio: 24.0, categoria: "pizzas", imagen: "/img/catalog/pizza.svg" },
  { id: "p5", nombre: "Pepperoni", precio: 25.0, categoria: "pizzas", imagen: "/img/catalog/pizza.svg" },
  { id: "p6", nombre: "hawaina", precio: 28.0, categoria: "pizzas", imagen: "/img/catalog/pizza.svg" },
  { id: "a1", nombre: "Alitas BBQ ×6", precio: 18.0, categoria: "alitas", imagen: "/img/catalog/pizza.svg" },
  { id: "a2", nombre: "Alitas picantes ×6", precio: 18.0, categoria: "alitas", imagen: "/img/catalog/pizza.svg" },
  { id: "s1", nombre: "Salchipapa clásica", precio: 12.0, categoria: "salchipapas", imagen: "/img/catalog/pizza.svg" },
  { id: "s2", nombre: "Salchipapa especial", precio: 15.0, categoria: "salchipapas", imagen: "/img/catalog/pizza.svg" },
  { id: "b1", nombre: "Gaseosa 500 ml", precio: 4.0, categoria: "bebidas", imagen: "/img/catalog/pizza.svg" },
  { id: "b2", nombre: "Jugo natural", precio: 6.0, categoria: "bebidas", imagen: "/img/catalog/pizza.svg" },
  { id: "pa1", nombre: "Spaghetti bolognesa", precio: 22.0, categoria: "pastas", imagen: "/img/catalog/pizza.svg" },
  { id: "pa2", nombre: "Lasaña", precio: 26.0, categoria: "pastas", imagen: "/img/catalog/pizza.svg" },
];

function initPedidos() {
  const state = {
    categoria: "pizzas",
    busqueda: "",
    carrito: [],
  };

  renderCategorias(state);
  renderProductos(state);
  renderCarrito(state);
  bindPedidosEvents(state);
}

function bindPedidosEvents(state) {
  document.getElementById("catalogo-buscar")?.addEventListener("input", (e) => {
    state.busqueda = e.target.value.trim().toLowerCase();
    renderProductos(state);
  });

  document.getElementById("catalogo-categorias")?.addEventListener("click", (e) => {
    const btn = e.target.closest("[data-categoria]");
    if (!btn) return;
    state.categoria = btn.dataset.categoria;
    renderCategorias(state);
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

  nav.innerHTML = CATEGORIAS.map(
    (cat) =>
      `<button type="button" class="catalogo-cat${state.categoria === cat.id ? " active" : ""}" data-categoria="${cat.id}">${cat.label}</button>`
  ).join("");
}

function getProductosFiltrados(state) {
  return PRODUCTOS.filter((p) => {
    const matchCategoria = p.categoria === state.categoria;
    const matchBusqueda =
      state.busqueda === "" || p.nombre.toLowerCase().includes(state.busqueda);
    return matchCategoria && matchBusqueda;
  });
}

function renderProductos(state) {
  const grid = document.getElementById("catalogo-grid");
  if (!grid) return;

  const productos = getProductosFiltrados(state);

  if (productos.length === 0) {
    grid.innerHTML = '<p class="catalogo-vacio">No se encontraron productos.</p>';
    return;
  }

  grid.innerHTML = productos
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
  const producto = PRODUCTOS.find((p) => p.id === productoId);
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

function enviarACocina(state) {
  if (state.carrito.length === 0) {
    alert("Agrega productos al pedido antes de enviar.");
    return;
  }

  const cliente = document.getElementById("pedido-cliente")?.value.trim() || "—";
  const mesa = document.getElementById("pedido-mesa")?.value.trim() || "—";
  const items = state.carrito.map((i) => `${i.cantidad}x ${i.nombre}`).join(", ");

  alert(`Pedido enviado a cocina\nCliente: ${cliente}\nMesa: ${mesa}\n${items}`);
  anularPedido(state);
}

function anularPedido(state) {
  state.carrito.length = 0;
  renderCarrito(state);
}
