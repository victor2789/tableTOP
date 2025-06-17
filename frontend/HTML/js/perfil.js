let usuarioId = null;
let historial = [];

document.addEventListener("DOMContentLoaded", () => {
  fetch("http://localhost:8083/auth/perfil", {
    headers: {
      Authorization: `Bearer ${localStorage.getItem("token")}`
    }
  })
    .then(res => {
      if (!res.ok) throw new Error("Error al cargar perfil");
      return res.json();
    })
    .then(usuario => {
      usuarioId = usuario.id;
      document.getElementById("username").textContent = "Usuario: " + usuario.username;
      historial = usuario.historial;
      renderHistorial();
    })
    .catch(() => alert("No se pudo cargar el perfil"));

  document.getElementById("soloCreadas").addEventListener("change", renderHistorial);
});

function renderHistorial() {
  const contenedor = document.getElementById("historial");
  contenedor.innerHTML = "";

  const soloCreadas = document.getElementById("soloCreadas").checked;
  const filtradas = soloCreadas
    ? historial.filter(p => p.IDcreador === usuarioId)
    : historial;

  if (filtradas.length === 0) {
    contenedor.innerHTML = "<p>No hay partidas para mostrar.</p>";
    return;
  }

  filtradas.forEach(p => {
    const div = document.createElement("div");
    div.className = "card my-2";
    div.innerHTML = `
      <div class="card-body">
        <h5 class="card-title">${p.nombre}</h5>
        <p class="card-text">Juego: ${p.juego.nombre}</p>
        <a href="partida.html?id=${p.id}" class="btn btn-sm btn-outline-primary">Ver partida</a>
      </div>`;
    contenedor.appendChild(div);
  });
}
