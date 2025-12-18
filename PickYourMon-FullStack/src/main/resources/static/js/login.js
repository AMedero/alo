const API_URL = "/api/usuarios/login";

document.getElementById("loginForm").addEventListener("submit", async (e) => {
    e.preventDefault(); // evita que se recargue la página

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const mensajeError = document.getElementById("mensajeError");

    const datosLogin = {
        email: email,
        password: password
    };

    try {
        const response = await fetch(API_URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(datosLogin)
        });

        if (response.ok) {
            const usuario = await response.json();
            
            // guardamos al usuario en el navegador
            localStorage.setItem("usuarioLogueado", JSON.stringify(usuario));
            
            alert("¡Bienvenido, " + usuario.nombre + "!");
            
            // redireccionamiento según el Rol
            if (usuario.rol === "ADMIN") {
                window.location.href = "admin.html";
            } else {
                const carritoPendiente = localStorage.getItem("carritoPendiente");
                if (carritoPendiente) {
                    localStorage.setItem("carrito", carritoPendiente);
                    localStorage.removeItem("carritoPendiente");
                }
                window.location.href = "home.html";
            }
        } else {
            // error de credenciales
            mensajeError.style.display = "block";
            mensajeError.textContent = "Email o contraseña incorrectos.";
        }

    } catch (error) {
        console.error("Error:", error);
        mensajeError.style.display = "block";
        mensajeError.textContent = "Error de conexión con el servidor.";
    }
});