document.addEventListener("DOMContentLoaded", () => {
  console.log("JS caragado")
  const API_BASE = "http://localhost:8083/auth";

  function clearErrors() {
    document.querySelectorAll(".error-message").forEach(el => el.remove());
  }

  function setFieldError(inputId, message) {
    const input = document.getElementById(inputId);
    const errorDiv = document.createElement("div");
    errorDiv.className = "error-message text-danger small mt-1";
    errorDiv.innerText = message;
    input.parentNode.appendChild(errorDiv);
  }

  function handleRegisterError(message) {
    if (message.includes("Email")) {
      setFieldError("regEmail", message);
    } else if (message.includes("Usuario")) {
      setFieldError("regUsername", message);
    } else {
      setFieldError("regEmail", message);
    }
  }

  // LOGIN
  document.getElementById("loginForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    clearErrors();

    const email = document.getElementById("loginEmail").value;
    const password = document.getElementById("loginPassword").value;

    try {
      const res = await fetch(`${API_BASE}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
      });

      console.log("Status:", res.status, res.statusText);
      console.log("Content-Type:", res.headers.get("content-type"));

      const data = await res.json();
      console.log("Data:", data);

      if (res.ok && data.token) {
        localStorage.setItem("token", data.token);
        localStorage.setItem("userId", data.id);
        localStorage.setItem("username", data.username);
        window.location.href = "main.html";
      } else {
        if (res.status === 404) {
          setFieldError("loginEmail", "Correo no registrado");
        } else if (res.status === 401) {
          setFieldError("loginPassword", "Contraseña incorrecta");
        } else {
          setFieldError("loginEmail", data.error || "Error de autenticación");
        }
      }
    } catch (err) {
      console.error("Error en fetch o parseo JSON:", err);
      setFieldError("loginEmail", "No se pudo conectar con el servidor");
    }
  });

  // REGISTRO
  document.getElementById("registerForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    clearErrors();

    const username = document.getElementById("regUsername").value;
    const email = document.getElementById("regEmail").value;
    const password = document.getElementById("regPassword").value;

    try {
      const res = await fetch(`${API_BASE}/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, email, password })
      });

      const data = await res.json();
      console.log("Register response:", data);

      if (res.ok) {
        const toastEl = new bootstrap.Toast(document.getElementById('successToast'));
        toastEl.show();
        setTimeout(() => {
          document.querySelector('#login-tab')?.click();
        }, 2500);
      } else {
        handleRegisterError(data.error || "Error en el registro");
      }
    } catch (err) {
      console.error("Error en registro:", err);
      setFieldError("regEmail", "No se pudo conectar con el servidor");
    }
  });
});
