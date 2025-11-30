// checkout.js
document.addEventListener('DOMContentLoaded', function() {
    cargarCarritoEnCheckout();
    configurarMetodoPago();
    configurarEnvioFormulario();
});

function cargarCarritoEnCheckout() {
    const carrito = JSON.parse(localStorage.getItem('tecnoaccesorios_cart')) || [];
    const contenedor = document.getElementById('checkout-items');
    const subtotalElement = document.getElementById('checkout-subtotal');
    const envioElement = document.getElementById('checkout-shipping');
    const totalElement = document.getElementById('checkout-total');

    let subtotal = 0;

    if (carrito.length === 0) {
        contenedor.innerHTML = '<p>No hay productos en el carrito</p>';
        return;
    }

    contenedor.innerHTML = carrito.map(item => {
        const itemTotal = item.price * item.quantity;
        subtotal += itemTotal;

        return `
            <div class="checkout-item">
                <div class="item-info">
                    <h4>${item.name}</h4>
                    <p>Cantidad: ${item.quantity}</p>
                </div>
                <div class="item-price">S/ ${itemTotal.toFixed(2)}</div>
            </div>
        `;
    }).join('');

    const envio = subtotal > 100 ? 0 : 10;
    const total = subtotal + envio;

    subtotalElement.textContent = `S/ ${subtotal.toFixed(2)}`;
    envioElement.textContent = envio === 0 ? 'GRATIS' : `S/ ${envio.toFixed(2)}`;
    totalElement.textContent = `S/ ${total.toFixed(2)}`;
}

function configurarMetodoPago() {
    const selectPago = document.getElementById('metodoPago');
    const detallesPago = document.getElementById('payment-details');
    const transferenciaFields = document.getElementById('transferencia-fields');
    const yapeFields = document.getElementById('yape-fields');
    const tarjetaFields = document.getElementById('tarjeta-fields');

    selectPago.addEventListener('change', function() {
        const metodo = this.value;

        // Ocultar todos los campos primero
        transferenciaFields.style.display = 'none';
        yapeFields.style.display = 'none';
        tarjetaFields.style.display = 'none';

        switch(metodo) {
            case 'transferencia':
                detallesPago.innerHTML = `
                    <h4>Datos para Transferencia</h4>
                    <p><strong>Banco:</strong> BCP</p>
                    <p><strong>Cuenta:</strong> 191-23456789-0-12</p>
                    <p><strong>Titular:</strong> TecnoAccesorios E.I.R.L.</p>
                    <p>Envía el comprobante a nuestro WhatsApp</p>
                `;
                transferenciaFields.style.display = 'block';
                detallesPago.style.display = 'block';
                break;

            case 'yape':
                detallesPago.innerHTML = `
                    <h4>Yape / Plin</h4>
                    <p><strong>Número:</strong> 960 805 566</p>
                    <p><strong>Nombre:</strong> TecnoAccesorios</p>
                    <p>Envía el comprobante por WhatsApp</p>
                `;
                yapeFields.style.display = 'block';
                detallesPago.style.display = 'block';
                break;

            case 'efectivo':
                detallesPago.innerHTML = `
                    <h4>Pago en Efectivo</h4>
                    <p>Paga al momento de la entrega</p>
                    <p><strong>Dirección de recojo:</strong> Av. Principal #123, Tarapoto</p>
                `;
                detallesPago.style.display = 'block';
                break;

            case 'tarjeta':
                detallesPago.innerHTML = `
                    <h4>Tarjeta Crédito/Débito</h4>
                    <p>Procesaremos tu pago de forma segura</p>
                    <p><strong>Procesado por:</strong> MercadoPago</p>
                `;
                tarjetaFields.style.display = 'block';
                detallesPago.style.display = 'block';
                break;

            default:
                detallesPago.style.display = 'none';
        }
    });
}

function configurarEnvioFormulario() {
    document.getElementById('checkout-form').addEventListener('submit', function(e) {
        const totalText = document.getElementById('checkout-total').textContent;
        const total = totalText.replace('S/ ', '').trim();
        document.getElementById('total').value = total;
    });
}