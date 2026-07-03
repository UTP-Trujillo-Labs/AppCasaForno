const ESTADOS_MESA = ["libre", "ocupada", "reservada"];
const ESTADO_LABELS = { libre: "Libre", ocupada: "Ocupada", reservada: "Reservada" };

App.registerPage("mesas", initMesas);

function initMesas() {
  const grid = document.getElementById("mesas-grid");
  if (!grid) return;

  grid.querySelectorAll(".mesa").forEach((mesa) => {
    mesa.addEventListener("click", () => toggleMesaEstado(mesa));
  });
}

function toggleMesaEstado(mesa) {
  const clases = ESTADOS_MESA.map((e) => `mesa-${e}`);
  const actual = ESTADOS_MESA.find((e) => mesa.classList.contains(`mesa-${e}`)) || "libre";
  const siguiente = ESTADOS_MESA[(ESTADOS_MESA.indexOf(actual) + 1) % ESTADOS_MESA.length];

  mesa.classList.remove(...clases);
  mesa.classList.add(`mesa-${siguiente}`);
  mesa.querySelector(".mesa-estado").textContent = ESTADO_LABELS[siguiente];
}
