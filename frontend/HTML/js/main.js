let map, userLocation;
let page = 0;
let isLoading = false;

document.addEventListener("DOMContentLoaded", () => {
  initMap();

  navigator.geolocation.getCurrentPosition(
    pos => {
      userLocation = [pos.coords.latitude, pos.coords.longitude];
      map.setView(userLocation, 13);
      L.marker(userLocation).addTo(map).bindPopup("Tu ubicación").openPopup();
      loadPartidas();
    },
    () => {
      alert("No se pudo obtener tu ubicación.");
      userLocation = [40.4168, -3.7038]; // Madrid por defecto
      map.setView(userLocation, 13);
      loadPartidas();
    }
  );

  document.getElementById("filtrosForm").addEventListener("submit", e => {
    e.preventDefault();
    page = 0;
    document.getElementById("partidasContainer").innerHTML = "";
    loadPartidas();
  });

  window.addEventListener("scroll", () => {
    if (!isLoading && window.innerHeight + window.scrollY >= document.body.offsetHeight - 100) {
      page++;
      loadPartidas();
    }
  });
});

function initMap() {
  map = L.map("map").setView([0, 0], 2);
  L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
    attribution: "© OpenStreetMap"
  }).addTo(map);
}

function loadPartidas() {
  isLoading = true;
  document.getElementById("loading").classList.remove("d-none");

  const juego = document.getElementById("juego").value;
  const distancia = document.getElementById("distancia").value;
  const incluirCompletas = document.getElementById("mostrarCompletas").checked;

  const params = new URLSearchParams({
    juego,
    distancia,
    completas: incluirCompletas,
    lat: userLocation[0],
    lon: userLocation[1],
    page
  });

  fetch(`http://localhost:8083/api/partidas/buscar?${params.toString()}`, {
    headers: { Authorization: `Bearer ${localStorage.getItem("token")}` }
  })
    .then(res => res.ok ? res.json() : Promise.reject("Error al cargar partidas"))
    .then(partidas => {
      if (!partidas || partidas.length === 0) return;
      const ids = [...new Set(partidas.map(p => p.IDcreador || p.iDcreador).filter(Boolean))];

      if (ids.length === 0) {
        partidas.forEach(p => mostrarPartida(p, "Desconocido")); //en caso de error organizador=desconocido
        return;
      }

      return fetch("http://localhost:8083/auth/usernames", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("token")}`
        },
        body: JSON.stringify(ids)
      })
      .then(res => res.ok ? res.json() : Promise.reject("Error al obtener nombres"))
      .then(idToNameMap => {
        partidas.forEach(p => {
          const creadorId = p.IDcreador || p.iDcreador;
          const creadorNombre = idToNameMap[creadorId] || "Desconocido";
          mostrarPartida(p, creadorNombre);
        });
      });
    })
    .catch(err => {
      console.error(err);
      alert(err);
    })
    .finally(() => {
      isLoading = false;
      document.getElementById("loading").classList.add("d-none");
    });
}

function mostrarPartida(p, creadorNombre) {
  const container = document.getElementById("partidasContainer");
  const div = document.createElement("div");
  div.className = "col";

  const juegoNombre = typeof p.juego === "object" && p.juego?.nombre
    ? p.juego.nombre
    : typeof p.juego === "string"
      ? p.juego
      : "Desconocido";

  div.innerHTML = `
    <div class="card card-fantasy shadow-sm">
      <div class="card-body">
        <h5 class="card-title fantasy-subtitle">${p.nombre}</h5>
        <p class="card-text parchment-text">🎲 Juego: ${juegoNombre}</p>
        <p class="card-text parchment-text">🧙 Organizador: ${creadorNombre}</p>
        <p class="card-text parchment-text">📅 Fecha: ${p.fechaPartida}</p>
        <a href="partida.html?id=${p.id}" class="btn btn-fantasy btn-sm w-100 mt-2">🗡️ Ver más</a>
      </div>
    </div>
  `;
  container.appendChild(div);

  if (p.location?.coordinates?.length === 2) {
    const [lon, lat] = p.location.coordinates;
    L.marker([lat, lon])
      .addTo(map)
      .bindPopup(`${p.nombre}<br/>${juegoNombre}`);
  }
}
