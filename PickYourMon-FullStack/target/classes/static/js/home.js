// ------------------------------------- 1. VARIABLES PRINCIPALES -------------------------------------
// Estado y referencias que usa la app (lo que guarda y usa la app)
let carrito = []; // Productos añadidos al carrito (cada uno con su cantidad)
let productosDisponibles = []; // Productos traídos del archivo productos.json

// Referencias a elementos HTML (puede que sean null si no existen)
const contenedorTarjetas = document.getElementById("contenedor-tarjetas");
const contenedorModal = document.getElementById("contenedor-modal");
const cuerpoModal = document.getElementById("modal-body");
const textoTotal = document.getElementById("precio-total");
const botonAbrirCarrito = document.getElementById("boton-carrito");
const botonCerrarModal = document.getElementById("cerrar-modal");
const botonVaciar = document.getElementById("btn-vaciar");
const botonFinalizar = document.getElementById("btn-finalizar");
const contadorCarritoSpan = document.getElementById("contador-carrito");
const toastContainer = document.getElementById("toast-container"); // Lugar donde se muestran las notificaciones


// ------------------------------------- 2. CARGA INICIAL (al abrir la página) ----------------------------------------
// Cuando se carga la página traemos los productos y después recuperamos el carrito guardado
document.addEventListener('DOMContentLoaded', async () => {
    await traerProductos();
    
    // MODIFICACIÓN AQUI: Recuperar carrito normal O el pendiente si venimos de login
    const carritoPendiente = localStorage.getItem("carritoPendiente");
    
    if (carritoPendiente) {
        // Si hay un carrito pendiente de compra, lo usamos y borramos la marca
        carrito = JSON.parse(carritoPendiente);
        localStorage.removeItem("carritoPendiente");
        
        // Abrimos el modal automáticamente para que el usuario siga comprando
        // (Pequeño delay para asegurar que el DOM cargó)
        setTimeout(() => {
            alternarModal();
            mostrarNotificacion("¡Carrito recuperado! Puedes finalizar tu compra.");
        }, 500);
        
        actualizarIconoCarrito();
    } else {
        // Carga normal (si no hay pendiente)
        cargarLocalStorage();
    }
});

// Trae la lista de productos desde el archivo local y los deja listos para la app
const traerProductos = async () => {
    try {
        const respuesta = await fetch('./js/productos.json'); // solicita el archivo
        const datos = await respuesta.json(); // convierte el texto en objetos
        productosDisponibles = datos;
        pintarProductos(productosDisponibles); // muestra las tarjetas en pantalla
    } catch (error) {
        // Si falla la carga, le avisamos al usuario con una notificación
        if (typeof mostrarNotificacion === 'function') {
            mostrarNotificacion('Error cargando productos. Intenta recargar la página.');
        }
    }
};


// ------------------------------------- 3. MOSTRAR PRODUCTOS (render) -------------------------------------
// Dibuja las tarjetas por cada producto con un botón para agregarlos al carrito
const pintarProductos = (lista) => {
    contenedorTarjetas.innerHTML = "";
    lista.forEach(producto => {
        const tarjeta = document.createElement("article");
        tarjeta.classList.add("tarjeta-producto");
        tarjeta.innerHTML = `
            <img src="${producto.imagen}" alt="${producto.nombre}" />
            <h3>${producto.nombre}</h3>
            <p>Precio: US$${producto.precio}</p>
            <button class="btn-agregar" onclick="agregarAlCarrito(${producto.id})">Agregar al Carrito</button>
        `;
        contenedorTarjetas.appendChild(tarjeta);
    });
};


// ------------------------------------- 4. LÓGICA DEL CARRITO -------------------------------------
// Agrega un producto al carrito o aumenta la cantidad si ya está
const agregarAlCarrito = (id) => {
    const producto = productosDisponibles.find(item => item.id === id);
    if (!producto) return; // Evita acciones si el producto no se encontró

    const existe = carrito.some(item => item.id === id);

    if (existe) {
        // Si ya está, aumentamos la cantidad
        carrito = carrito.map(item => {
            if (item.id === id) {
                return { ...item, cantidad: item.cantidad + 1 };
            } else {
                return item;
            }
        });
    } else {
        // Si no está, lo añadimos con cantidad 1
        carrito.push({ ...producto, cantidad: 1 });
    }

    actualizarIconoCarrito(); // Actualiza el contador visible
    guardarLocalStorage(); // Guardamos el carrito para mantenerlo entre sesiones

    // Aviso breve en pantalla
    mostrarNotificacion(`¡${producto.nombre} añadido al carrito!`);
};


// ------------------------------------- 4b. GUARDAR Y RECUPERAR EN EL NAVEGADOR -------------------------------------
// Guarda el carrito en localStorage
const guardarLocalStorage = () => {
    try {
        localStorage.setItem('carrito', JSON.stringify(carrito));
    } catch (error) {
        if (typeof mostrarNotificacion === 'function') {
            mostrarNotificacion('No se pudo guardar el carrito en tu navegador.');
        }
    }
};

// Recupera el carrito guardado y elimina del mismo los productos que ya no estén disponibles
const cargarLocalStorage = () => {
    try {
        const data = localStorage.getItem('carrito');
        const carritoGuardado = data ? JSON.parse(data) || [] : [];
        carrito = carritoGuardado.filter(item =>
            productosDisponibles.some(prod => prod.id === item.id)
        );
        actualizarIconoCarrito();
        if (contenedorModal.classList.contains('modal-active')) pintarCarritoEnModal();
    } catch (error) {
        if (typeof mostrarNotificacion === 'function') {
            mostrarNotificacion('No se pudo cargar el carrito guardado.');
        }
        carrito = []; // Reiniciamos por seguridad si algo falla
    }
};

// Si cambian el carrito desde otra pestaña, lo sincronizamos por acá
window.addEventListener('storage', (e) => {
    if (e.key === 'carrito') {
        try {
            carrito = JSON.parse(e.newValue) || [];
        } catch (error) {
            carrito = [];
        }
        actualizarIconoCarrito();
        if (contenedorModal.classList.contains('modal-active')) pintarCarritoEnModal();
    }
});

// Muestra los items del carrito en el modal, con controles de cantidad y el total
const pintarCarritoEnModal = () => {
    cuerpoModal.innerHTML = "";
    if (carrito.length === 0) {
        cuerpoModal.innerHTML = "<p style='text-align:center'>El carrito está vacío ☹️</p>";
        textoTotal.innerText = "0.00";
        return;
    }

    carrito.forEach(prod => {
        const fila = document.createElement("div");
        fila.classList.add("item-carrito");
        fila.innerHTML = `
            <div class="info-producto-carrito">
                <img src="${prod.imagen}" alt="${prod.nombre}">
                <div>
                    <p><strong>${prod.nombre}</strong></p>
                    <p>US$${prod.precio}</p>
                </div>
            </div>
            <div class="controles-cantidad">
                <button class="btn-cantidad" onclick="restarUnidad(${prod.id})">-</button>
                <span>${prod.cantidad}</span>
                <button class="btn-cantidad" onclick="sumarUnidad(${prod.id})">+</button>
            </div>
            <button class="btn-eliminar-item" onclick="eliminarProducto(${prod.id})">❌</button>
        `;
        cuerpoModal.appendChild(fila);
    });

    // Calcula el total del carrito
    const total = carrito.reduce((acc, item) => acc + (item.precio * item.cantidad), 0);
    textoTotal.innerText = total.toFixed(2);
};


// ------------------------------------- 5. CAMBIOS EN EL CARRITO -------------------------------------
// Aumenta la cantidad de un item en el carrito
const sumarUnidad = (id) => {
    carrito = carrito.map(item => item.id === id ? { ...item, cantidad: item.cantidad + 1 } : item);
    pintarCarritoEnModal();
    actualizarIconoCarrito();
    guardarLocalStorage();
};

// Resta una unidad; si queda en 0, pregunta antes de eliminar
const restarUnidad = (id) => {
    const item = carrito.find(prod => prod.id === id);
    if (!item) return;
    if (item.cantidad === 1) {
        if (confirm(`¿Querés eliminar ${item.nombre} del carrito?`)) {
            eliminarProducto(id);
        }
    } else {
        carrito = carrito.map(prod => prod.id === id ? { ...prod, cantidad: prod.cantidad - 1 } : prod);
        pintarCarritoEnModal();
        actualizarIconoCarrito();
        guardarLocalStorage();
    }
};

// Elimina un producto del carrito tras confirmación
const eliminarProducto = (id) => {
    if (confirm("¿Eliminar este producto?")) {
        carrito = carrito.filter(prod => prod.id !== id);
        pintarCarritoEnModal();
        actualizarIconoCarrito();
        guardarLocalStorage();
    }
};


// ------------------------------------- 6. BOTONES PRINCIPALES -------------------------------------
// Vacía todo el carrito si el usuario confirma
if (botonVaciar) {
    botonVaciar.addEventListener("click", () => {
        if (carrito.length === 0) return;
        if (confirm("¿Vaciar todo el carrito?")) {
            carrito = [];
            pintarCarritoEnModal();
            actualizarIconoCarrito();
            guardarLocalStorage();
        }
    });
}

// Finaliza el pedido: AHORA VERIFICA LOGIN PRIMERO
if (botonFinalizar) {
    botonFinalizar.addEventListener("click", () => {
        if (carrito.length === 0) {
            mostrarNotificacion("El carrito está vacío.");
            return;
        }

        // ------------------------------------- LÓGICA DE LOGIN -------------------------------------
        const usuarioLogueado = localStorage.getItem("usuarioLogueado");

        if (!usuarioLogueado) {
            if (confirm("Necesitas iniciar sesión para comprar. ¿Ir al login ahora?")) {
                // guardamos el carrito pendiente para recuperarlo al volver
                localStorage.setItem("carritoPendiente", JSON.stringify(carrito));
                window.location.href = "login.html";
            }
            return; // pausamos el flujo acá
        }
        // ------------------------------

        // si ya está logueado, sigue el flujo normal
        // mostrar mensaje de éxito centrado con fondo negro transparente
        mostrarNotificacionCentrada("¡Pedido procesado con éxito!", 3000);
        
        // Aquí en el futuro conectarías con el Backend para guardar la orden real
        
        carrito = [];
        pintarCarritoEnModal();
        actualizarIconoCarrito();
        alternarModal(); // Cerramos el modal al finalizar
        guardarLocalStorage();
    });
}


// ------------------------------------- 7. MODAL Y UI -------------------------------------
// Alterna el modal y renderiza el contenido si está abierto
const alternarModal = () => {
    contenedorModal.classList.toggle("modal-active");
    if (contenedorModal.classList.contains("modal-active")) {
        pintarCarritoEnModal();
    }
};

// Actualiza el contador del icono del carrito
const actualizarIconoCarrito = () => {
    const totalItems = carrito.reduce((acc, item) => acc + item.cantidad, 0);
    contadorCarritoSpan.innerText = totalItems;
};


// ------------------------------------- 8. NOTIFICACIONES SIMPLES -------------------------------------
// Muestra un mensajito temporal (toast)
const mostrarNotificacion = (mensaje) => {
    const contenedor = document.getElementById("toast-container");
    if (!contenedor) return;

    const toast = document.createElement("div");
    toast.classList.add("toast");
    toast.innerText = mensaje;

    contenedor.appendChild(toast);

    // Se borra automáticamente después de 3 segundos
    setTimeout(() => {
        toast.remove();
    }, 3000);
};

/**
 * mostrarNotificacionCentrada(mensaje, duracion)
 * Muestra un mensaje centrado en la pantalla con fondo negro transparente
 * durante `duracion` milisegundos. Sirve para mensajes importantes como
 * "Pedido procesado con éxito".
 */
const mostrarNotificacionCentrada = (mensaje, duracion = 3000) => {
    // Crear elemento overlay
    const overlay = document.createElement('div');
    overlay.classList.add('overlay-message');
    overlay.setAttribute('role', 'status');
    overlay.setAttribute('aria-live', 'polite');
    overlay.innerHTML = `<div class="overlay-message__content">${mensaje}</div>`;

    document.body.appendChild(overlay);

    // Remover después de duracion
    setTimeout(() => {
        // Añadir una animación de salida sutil (opcional)
        overlay.style.animation = 'overlayFadeOut 0.18s ease-out forwards';
        setTimeout(() => overlay.remove(), 200);
    }, duracion);
};

// Eventos para abrir/cerrar el modal y cerrarlo si clickeás afuera
if (botonAbrirCarrito && botonCerrarModal && contenedorModal) {
    botonAbrirCarrito.addEventListener("click", alternarModal);
    botonCerrarModal.addEventListener("click", alternarModal);
    contenedorModal.addEventListener("click", (e) => {
        if (e.target === contenedorModal) alternarModal();
    });
}

// Cerrar modal con Escape si está abierto
document.addEventListener("keydown", (e) => {
    if (e.key === "Escape" && contenedorModal.classList.contains("modal-active")) {
        alternarModal();
    }
});