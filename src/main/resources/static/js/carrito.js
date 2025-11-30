// carrito.js
let carrito = [];

document.addEventListener('DOMContentLoaded', function() {
    cargarCarrito();
    renderizarCarrito();
});

function cargarCarrito() {
    const carritoGuardado = localStorage.getItem('tecnoaccesorios_cart');
    if (carritoGuardado) {
        carrito = JSON.parse(carritoGuardado);
    }
}

function guardarCarrito() {
    localStorage.setItem('tecnoaccesorios_cart', JSON.stringify(carrito));
}

function renderizarCarrito() {
    const contenedor = document.getElementById('cart-items-container');
    const resumen = document.getElementById('cart-summary');
    const mensajeVacio = document.getElementById('empty-cart-message');

    if (carrito.length === 0) {
        mensajeVacio.style.display = 'block';
        resumen.style.display = 'none';
        return;
    }

    mensajeVacio.style.display = 'none';
    resumen.style.display = 'block';

    contenedor.innerHTML = '';

    carrito.forEach((item, index) => {
        const itemElement = document.createElement('div');
        itemElement.className = 'cart-item';
        itemElement.innerHTML = `
            <div class="item-image">
                <img src="${item.image || '/img/placeholder.jpg'}" alt="${item.name}">
            </div>
            <div class="item-details">
                <h4>${item.name}</h4>
                <p class="item-price">S/ ${item.price.toFixed(2)}</p>
            </div>
            <div class="item-controls">
                <div class="quantity-controls">
                    <button onclick="cambiarCantidad(${index}, -1)">-</button>
                    <span>${item.quantity}</span>
                    <button onclick="cambiarCantidad(${index}, 1)">+</button>
                </div>
                <button class="remove-btn" onclick="eliminarDelCarrito(${index})">Eliminar</button>
            </div>
            <div class="item-total">
                S/ ${(item.price * item.quantity).toFixed(2)}
            </div>
        `;
        contenedor.appendChild(itemElement);
    });

    actualizarResumen();
}

function cambiarCantidad(index, cambio) {
    carrito[index].quantity += cambio;

    if (carrito[index].quantity <= 0) {
        eliminarDelCarrito(index);
    } else {
        guardarCarrito();
        renderizarCarrito();
    }
}

function eliminarDelCarrito(index) {
    carrito.splice(index, 1);
    guardarCarrito();
    renderizarCarrito();
}

function actualizarResumen() {
    const subtotal = carrito.reduce((total, item) => total + (item.price * item.quantity), 0);
    const envio = subtotal > 100 ? 0 : 10;
    const total = subtotal + envio;

    document.getElementById('summary-subtotal').textContent = `S/ ${subtotal.toFixed(2)}`;
    document.getElementById('summary-shipping').textContent = envio === 0 ? 'GRATIS' : `S/ ${envio.toFixed(2)}`;
    document.getElementById('summary-total').textContent = `S/ ${total.toFixed(2)}`;
}

function clearCart() {
    if (confirm('¿Estás seguro de que quieres vaciar el carrito?')) {
        carrito = [];
        guardarCarrito();
        renderizarCarrito();
    }
}

function proceedToCheckout() {
    if (carrito.length === 0) {
        alert('Tu carrito está vacío');
        return;
    }
    window.location.href = '/checkout';
}