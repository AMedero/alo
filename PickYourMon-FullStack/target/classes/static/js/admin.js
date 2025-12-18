// url backend
const API_PRODUCTOS = "/api/productos"; // ruta relativa gracias a que servimos desde Spring

// verificamos que el usuario logueado sea Admin
document.addEventListener("DOMContentLoaded", () => {
    const usuarioGuardado = localStorage.getItem("usuarioLogueado");
    
    if (!usuarioGuardado) {
        // No hay nadie logueado -> Fuera
        alert("Debes iniciar sesión primero.");
        window.location.href = "login.html";
        return;
    }

    const usuario = JSON.parse(usuarioGuardado);

    if (usuario.rol !== "ADMIN") {
        // es usuario normal -> Fuera
        alert("Acceso denegado. Solo administradores.");
        window.location.href = "home.html";
        return;
    }

    // si es admin, mostramos su nombre en el admin.html
    document.getElementById("adminName").textContent = "Hola, " + usuario.nombre;
    
    cargarProductos();
});

// LOGOUT
function cerrarSesion() {
    localStorage.removeItem("usuarioLogueado");
    window.location.href = "login.html";
}

// GUARDAR PRODUCTO (POST)
document.getElementById("formProducto").addEventListener("submit", async (e) => {
    e.preventDefault();

    const nuevoProducto = {
        nombre: document.getElementById("nombre").value,
        descripcion: document.getElementById("descripcion").value,
        precio: parseFloat(document.getElementById("precio").value),
        stock: parseInt(document.getElementById("stock").value),
        imagenUrl: document.getElementById("imagen").value,
        categoriaId: parseInt(document.getElementById("categoriaId").value) || null
    };

    try {
        const response = await fetch(API_PRODUCTOS, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(nuevoProducto)
        });

        if (response.ok) {
            alert("¡Producto creado exitosamente!");
            document.getElementById("formProducto").reset();
            cargarProductos();
        } else {
            alert("Error al guardar. Revisa la consola o que el ID de categoría exista.");
        }

    } catch (error) {
        console.error("Error:", error);
        alert("Error de conexión con el servidor.");
    }
});

async function cargarProductos() {
    try {
        const response = await fetch(API_PRODUCTOS);
        if (response.ok) {
            const productos = await response.json();
            mostrarProductos(productos);
        }
    } catch (error) {
        console.error("Error al cargar productos:", error);
    }
}

function mostrarProductos(productos) {
    const container = document.getElementById("listaProductos");
    
    if (productos.length === 0) {
        container.innerHTML = "<p>No hay productos registrados.</p>";
        return;
    }
    
    container.innerHTML = productos.map(p => `
        <div class="producto-item">
            <div>
                <strong>${p.nombre}</strong> - $${p.precio} (Stock: ${p.stock})
            </div>
            <button class="btn-delete" onclick="eliminarProducto(${p.id})">Eliminar</button>
        </div>
    `).join('');
}

async function eliminarProducto(id) {
    if (!confirm("¿Seguro que quieres eliminar este producto?")) {
        return;
    }
    
    try {
        const response = await fetch(API_PRODUCTOS + "/" + id, {
            method: "DELETE"
        });
        
        if (response.ok || response.status === 204) {
            alert("Producto eliminado correctamente.");
            cargarProductos();
        } else {
            alert("Error al eliminar el producto.");
        }
    } catch (error) {
        console.error("Error:", error);
        alert("Error de conexión con el servidor.");
    }
}