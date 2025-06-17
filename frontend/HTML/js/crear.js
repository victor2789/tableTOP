let lat = null;
let lng = null;

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

// Cargar lista de juegos desde el backend (MongoDB)
document.addEventListener("DOMContentLoaded", () => {
  fetch("http://localhost:8083/api/juegos", {
    headers: {
      Authorization: `Bearer ${localStorage.getItem("token")}`
    }
  })
    .then(res => res.json())
    .then(juegos => {
      const select = document.getElementById("juego");
      juegos.forEach(j => {
        const opt = document.createElement("option");
        opt.value = j.nombre;
        opt.textContent = j.nombre;
        select.appendChild(opt);
      });
    })
    .catch(() => showToast("No se pudieron cargar los juegos", "danger"));
});

function obtenerUbicacion() {
  if (!navigator.geolocation) {
    showToast("Tu navegador no soporta geolocalización", "danger");
    return;
  }

  navigator.geolocation.getCurrentPosition(
    pos => {
      lat = pos.coords.latitude;
      lng = pos.coords.longitude;
      document.getElementById("ubicacion").textContent =
        `Ubicación establecida: ${lat.toFixed(4)}, ${lng.toFixed(4)}`;
    },
    err => showToast("No se pudo obtener ubicación: " + err.message, "danger")
  );
}

document.getElementById("crearForm").addEventListener("submit", e => {
  e.preventDefault();

  const nombre = document.getElementById("nombre").value.trim();
  const juego = document.getElementById("juego").value; // Aquí estaba el bug: usabas 'juegoId' pero luego 'juego'
  const fecha = document.getElementById("fecha").value;

  if (!lat || !lng) {
    showToast("Primero debes obtener la ubicación", "danger");
    return;
  }
  if (!nombre || !juego || !fecha) {
    showToast("Todos los campos son obligatorios", "danger");
    return;
  }

  const data = {
    nombre,
    juego,
    fechaPartida: fecha,
    lat,
    lon: lng,
    oculto: false
  };

  fetch("http://localhost:8083/api/partidas/crear", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${localStorage.getItem("token")}`
    },
    body: JSON.stringify(data)
  })
    .then(res => {
      if (!res.ok) throw new Error("No se pudo crear la partida");
      return res.text();
    })
    .then(msg => {
      showToast(msg || "Partida creada con éxito", "success");
      setTimeout(() => {
        window.location.href = "main.html";
      }, 1500);
    })
    .catch(err => showToast("Error: " + err.message, "danger"));
});
