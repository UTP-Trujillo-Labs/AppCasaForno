const ESTADOS_MESA = ["libre", "ocupada", "reservada"];
const ESTADO_LABELS = { libre: "Libre", ocupada: "Ocupada", reservada: "Reservada" };

App.registerPage("mesas", initMesas);

async function initMesas() {
  await cargarMesas();
}

async function cargarMesas() {
  const grid = document.getElementById("mesas-grid");
  if (!grid) return;

  try {
    const response = await fetch("/api/mesas/");
    if (!response.ok) throw new Error("Error al obtener mesas");

    const data = await response.json();
    renderMesas(data.mesas || []);
  } catch (err) {
    console.error(err);
    grid.innerHTML = '<p class="content-error">No se pudieron cargar las mesas.</p>';
  }
}

function renderMesas(mesas) {
  const grid = document.getElementById("mesas-grid");
  if (!grid) return;

  if (!mesas.length) {
    grid.innerHTML = '<p class="content-placeholder">No hay mesas registradas.</p>';
    return;
  }

  grid.innerHTML = mesas
    .map((mesa) => {
      const estado = ESTADOS_MESA.includes(mesa.estado) ? mesa.estado : "libre";
      return `
        <button type="button" class="mesa mesa-${estado}" data-mesa="${mesa.numero}">
          <span class="mesa-num">${mesa.numero}</span>
          <span class="mesa-estado">${ESTADO_LABELS[estado]}</span>
        </button>`;
    })
    .join("");

  grid.querySelectorAll(".mesa").forEach((mesaEl) => {
    mesaEl.addEventListener("click", () => cycleMesaEstado(mesaEl));
  });
}

async function cycleMesaEstado(mesaEl) {
  const idMesa = Number(mesaEl.dataset.mesa);
  const actual = ESTADOS_MESA.find((e) => mesaEl.classList.contains(`mesa-${e}`)) || "libre";
  const siguiente = ESTADOS_MESA[(ESTADOS_MESA.indexOf(actual) + 1) % ESTADOS_MESA.length];

  try {
    const response = await fetch(`/api/mesas/${idMesa}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ estado: siguiente }),
    });

    const result = await response.json();
    if (!response.ok) {
      alert(result.error || "No se pudo actualizar la mesa.");
      return;
    }

    const clases = ESTADOS_MESA.map((e) => `mesa-${e}`);
    mesaEl.classList.remove(...clases);
    mesaEl.classList.add(`mesa-${result.estado}`);
    mesaEl.querySelector(".mesa-estado").textContent = ESTADO_LABELS[result.estado] || result.estado;
  } catch (err) {
    console.error(err);
    alert("Error al actualizar el estado de la mesa.");
  }
}
