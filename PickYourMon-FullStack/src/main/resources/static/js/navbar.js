document.addEventListener("DOMContentLoaded", () => {
    const usuarioGuardado = localStorage.getItem("usuarioLogueado");
    
    const btnLogin = document.getElementById("btn-login");
    const btnLogout = document.getElementById("btn-logout");
    const btnAdmin = document.getElementById("btn-admin");

    if (usuarioGuardado) {
        // -------------------------------- USUARIO LOGUEADO --------------------------------
        const usuario = JSON.parse(usuarioGuardado);

        // ocultar botón de Login
        if (btnLogin) btnLogin.style.display = "none";
        
        // mostrar botón de Salir
        if (btnLogout) {
            btnLogout.style.display = "block";
            // mostrar el nombre del usuario
            btnLogout.querySelector("a").textContent = "Salir (" + usuario.nombre + ")";
        }

        // mostrar botón del Panel si es ADMIN
        if (usuario.rol === "ADMIN" && btnAdmin) {
            btnAdmin.style.display = "block";
        }

    } else {
        // -------------------------------- USUARIO VISITANTE --------------------------------
        // aseguramos estado inicial
        if (btnLogin) btnLogin.style.display = "block";
        if (btnLogout) btnLogout.style.display = "none";
        if (btnAdmin) btnAdmin.style.display = "none";
    }
});

function logout() {
    localStorage.removeItem("usuarioLogueado");
    window.location.href = "home.html";
}