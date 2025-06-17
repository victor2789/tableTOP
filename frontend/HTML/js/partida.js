document.addEventListener("DOMContentLoaded", () => {
  const params = new URLSearchParams(window.location.search);
  const id = params.get("id");

  if (!id) {
    document.getElementById("contenido").innerHTML = "ID no válido.";
    return;
  }

  Promise.all([
    fetch(`http://localhost:8083/api/partidas/${id}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` }
    }).then(res => res.ok ? res.json() : Promise.reject("No se pudo obtener la partida")),
    fetch("http://localhost:8083/auth/perfil", {
      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` }
    }).then(res => res.ok ? res.json() : Promise.reject("No se pudo obtener el perfil del usuario"))
  ])
  .then(([partida, usuario]) => mostrarPartida(partida, usuario))
  .catch(err => {
    console.error(err);
    document.getElementById("contenido").innerHTML = "No se pudo cargar la partida.";
  });
});

function mostrarPartida(p, usuarioActual) {
  const cont = document.getElementById("contenido");

  const juegoNombre = p.juego?.nombre || p.juego || "Desconocido";
  const maxParticipantes = p.juego?.maxParticipantes || 4;
  const completa = p.participantes.length >= maxParticipantes;
  const esCreador = usuarioActual.id === p.IDcreador;
  const yaParticipa = p.participantes.includes(usuarioActual.id);
  const yaSolicitada = p.solicitantes.includes(usuarioActual.id);

  fetch(`http://localhost:8083/auth/username/${p.IDcreador}`, {
    headers: { Authorization: `Bearer ${localStorage.getItem("token")}` }
  })
  .then(res => res.ok ? res.text() : "Desconocido")
  .then(nombreCreador => {
    cont.innerHTML = `
      <div class="card card-fantasy shadow-sm">
        <div class="card-body">
          <h3 class="card-title fantasy-subtitle">${p.nombre}</h3>
          <p class="parchment-text"><strong>🎲 Juego:</strong> ${juegoNombre}</p>
          <p class="parchment-text"><strong>📅 Fecha:</strong> ${p.fechaPartida}</p>
          <p class="parchment-text"><strong>🧑‍🤝‍🧑 Participantes:</strong> ${p.participantes.length}/${maxParticipantes}</p>
          <p class="parchment-text"><strong>🧙 Organizador:</strong> ${nombreCreador}</p>
          <p class="parchment-text"><strong>📍 Ubicación:</strong> 
            <span>${
              p.location?.coordinates
                ? `${p.location.coordinates[1].toFixed(4)}, ${p.location.coordinates[0].toFixed(4)}`
                : "No disponible"
            }</span>
          </p>
          ${
            completa
              ? `<div class="alert alert-warning parchment-text mt-3">⚠️ La partida ya está completa.</div>`
              : yaParticipa
                ? `<div class="alert alert-info parchment-text mt-3">🗡️ Ya participas en esta partida.</div>`
                : yaSolicitada
                  ? `<div class="alert alert-info parchment-text mt-3">⏳ Solicitud pendiente.</div>`
                  : !esCreador
                    ? `<button class="btn btn-fantasy mt-3" onclick="solicitarUnirse('${p.id}')">🛡️ Solicitar unirse</button>`
                    : ""
          }
        </div>
      </div>
    `;

    if (esCreador) renderSolicitantes(p);
  });
}

function renderSolicitantes(partida) {
  const container = document.getElementById("solicitantes-container");
  const list = document.getElementById("solicitantesList");
  list.innerHTML = "";

  if (!partida.solicitantes || partida.solicitantes.length === 0) {
    container.style.display = "none";
    return;
  }

  container.style.display = "";

  partida.solicitantes.forEach(id => {
    const li = document.createElement("li");
    li.id = `solicitud-${id}`;
    li.className = "list-group-item solicitante-item d-flex justify-content-between align-items-center";

    const span = document.createElement("span");
    span.className = `username-${id}`;
    span.textContent = "Cargando...";

    const actions = document.createElement("div");
    const btnA = document.createElement("button");
    btnA.className = "btn btn-accept btn-sm me-2";
    btnA.textContent = "Aceptar";
    btnA.addEventListener("click", () => aceptarSolicitud(partida.id, id));

    const btnR = document.createElement("button");
    btnR.className = "btn btn-reject btn-sm";
    btnR.textContent = "Rechazar";
    btnR.addEventListener("click", () => rechazarSolicitud(partida.id, id));

    actions.appendChild(btnA);
    actions.appendChild(btnR);

    li.appendChild(span);
    li.appendChild(actions);
    list.appendChild(li);
  });

  fetch("http://localhost:8083/auth/usernames", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${localStorage.getItem("token")}`
    },
    body: JSON.stringify(partida.solicitantes)
  })
  .then(res => res.ok ? res.json() : Promise.reject("Error al obtener nombres"))
  .then(nombres => {
    Object.entries(nombres).forEach(([id, username]) => {
      const el = document.querySelector(`.username-${id}`);
      if (el) el.textContent = username;
    });
  })
  .catch(() => {
    partida.solicitantes.forEach(id => {
      const el = document.querySelector(`.username-${id}`);
      if (el) el.textContent = "Desconocido";
    });
  });
}

function showToast(message, type = "success") {
  const container = document.getElementById("toastContainer");
  const toast = document.createElement("div");
  toast.className = `toast align-items-center text-bg-${type} border-0`;
  toast.setAttribute("role", "alert");
  toast.innerHTML = `
    <div class="d-flex">
      <div class="toast-body">${message}</div>
      <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
    </div>`;
  container.appendChild(toast);
  const bsToast = new bootstrap.Toast(toast);
  bsToast.show();
  toast.addEventListener("hidden.bs.toast", () => toast.remove());
}

function solicitarUnirse(id) {
  fetch(`http://localhost:8083/api/partidas/${id}/solicitar`, {
    method: "POST",
    headers: { Authorization: `Bearer ${localStorage.getItem("token")}` }
  })
  .then(res => res.text().then(text => {
      if (!res.ok) throw new Error(text || "Error al solicitar unirse");
      return text;
    }))
  .then(msg => {
      showToast(msg, "success");
      setTimeout(() => location.reload(), 1500);
    })
  .catch(err => showToast("Error: " + err.message, "danger"));
}

function aceptarSolicitud(idPartida, idUsuario) {
  fetch(`http://localhost:8083/api/partidas/${idPartida}/aceptar/${idUsuario}`, {
    method: "POST",
    headers: { Authorization: `Bearer ${localStorage.getItem("token")}` }
  })
  .then(res => res.text().then(text => {
      if (!res.ok) throw new Error(text || "No se pudo aceptar la solicitud");
      const item = document.getElementById(`solicitud-${idUsuario}`);
      if (item) item.remove();
      if (document.getElementById("solicitantesList").children.length === 0) {
        document.getElementById("solicitantes-container").style.display = "none";
      }
      return text;
    }))
  .then(msg => {
      showToast(msg, "success");
      setTimeout(() => location.reload(), 1500);
    })
  .catch(err => showToast("Error: " + err.message, "danger"));
}

function rechazarSolicitud(idPartida, idUsuario) {
  fetch(`http://localhost:8083/api/partidas/${idPartida}/rechazar/${idUsuario}`, {
    method: "POST",
    headers: { Authorization: `Bearer ${localStorage.getItem("token")}` }
  })
  .then(res => res.text().then(text => {
      if (!res.ok) throw new Error(text || "No se pudo rechazar la solicitud");
      const item = document.getElementById(`solicitud-${idUsuario}`);
      if (item) item.remove();
      if (document.getElementById("solicitantesList").children.length === 0) {
        document.getElementById("solicitantes-container").style.display = "none";
      }
      return text;
    }))
  .then(msg => {
      showToast(msg, "success");
      setTimeout(() => location.reload(), 1500);
    })
  .catch(err => showToast("Error: " + err.message, "danger"));
}