// TECNOACCESORIOS - CARRITO MEJORADO Y OPTIMIZADO
class CartManager {
    constructor() {
        this.CART_STORAGE_KEY = 'tecnoaccesorios_cart_v4';
        this.USER_SESSION_KEY = 'tecnoaccesorios_user_session';
        this.cart = this.loadCartFromStorage();
        this.isUserLoggedIn = this.checkUserLoginStatus();
        this.isProcessing = false;
        this.init();
    }

    init() {
        this.createNotificationArea();
        this.setupEventListeners();
        this.updateCartDisplay();
        this.setupImageErrorHandling();
        console.log('🛒 Carrito inicializado - Productos:', this.cart.length);
    }

    // ==================== VERIFICACIÓN MEJORADA ====================
    checkUserLoginStatus() {
        try {
            const session = localStorage.getItem(this.USER_SESSION_KEY);
            return session ? JSON.parse(session).isLoggedIn : false;
        } catch {
            return false;
        }
    }

    // ==================== EVENT LISTENERS OPTIMIZADOS ====================
    setupEventListeners() {
        // Delegación de eventos para mejor performance
        document.addEventListener('click', (e) => {
            const target = e.target;

            // Botones de agregar al carrito
            if (target.classList.contains('add-to-cart-btn')) {
                e.preventDefault();
                this.addToCart(e);
            }

            // Toggle carrito
            if (target.id === 'cart-toggle-button' || target.closest('#cart-toggle-button')) {
                this.toggleCart();
            }

            // Cerrar modal
            if (target.id === 'close-modal-btn' || target.classList.contains('close-modal')) {
                this.closeCart();
            }

            // Checkout
            if (target.id === 'checkout-btn') {
                this.proceedToCheckout();
            }
        });

        // Eventos específicos
        document.getElementById('go-to-login-btn')?.addEventListener('click', () => {
            window.location.href = '/login';
        });

        document.getElementById('go-to-register-btn')?.addEventListener('click', () => {
            window.location.href = '/registro';
        });

        // Cerrar modal con Escape
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') this.closeAllModals();
        });

        // Cerrar modal al hacer clic fuera
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('modal-overlay')) {
                e.target.hidden = true;
            }
        });

        // Filtro de categorías
        document.getElementById('category-filter')?.addEventListener('change', (e) => {
            this.filterProducts(e.target.value);
        });
    }

    // ==================== GESTIÓN DEL CARRITO MEJORADA ====================
    async addToCart(event) {
        if (this.isProcessing) {
            this.showNotification('⏳ Procesando solicitud anterior...', 'warning');
            return;
        }

        const button = event.currentTarget;
        const productId = button.dataset.id;
        const productName = button.dataset.name;
        const productPrice = parseFloat(button.dataset.price);
        const productImage = button.dataset.image;
        const productStock = parseInt(button.dataset.stock);

        const quantityInput = document.getElementById(`qty-${productId}`);
        const quantity = Math.max(1, parseInt(quantityInput?.value) || 1);

        // Validaciones robustas
        if (!this.validateProductData(productId, productName, productPrice, productStock)) {
            this.showNotification('❌ Error en los datos del producto', 'error');
            return;
        }

        if (productStock < 1) {
            this.showNotification('❌ Producto sin stock disponible', 'error');
            return;
        }

        if (quantity > productStock) {
            this.showNotification(`⚠️ Solo quedan ${productStock} unidades disponibles`, 'warning');
            if (quantityInput) quantityInput.value = productStock;
            return;
        }

        this.isProcessing = true;
        this.showButtonLoading(button, true);

        try {
            // Simular procesamiento asíncrono con timeout
            await new Promise((resolve, reject) => {
                const timeout = setTimeout(() => {
                    clearTimeout(timeout);
                    resolve();
                }, 600);

                // Simular error aleatorio (solo para testing)
                if (Math.random() < 0.02) { // 2% de probabilidad de error
                    reject(new Error('Error simulado de red'));
                }
            });

            const existingItemIndex = this.cart.findIndex(item => item.id === productId);

            if (existingItemIndex > -1) {
                // Actualizar producto existente
                const existingItem = this.cart[existingItemIndex];
                const newQuantity = existingItem.quantity + quantity;

                if (newQuantity > productStock) {
                    this.showNotification(`🚫 Máximo ${productStock} unidades disponibles`, 'error');
                    return;
                }

                this.cart[existingItemIndex].quantity = newQuantity;
                this.cart[existingItemIndex].lastUpdated = new Date().toISOString();
            } else {
                // Agregar nuevo producto
                this.cart.push({
                    id: productId,
                    name: productName,
                    price: productPrice,
                    image: productImage,
                    quantity: quantity,
                    stock: productStock,
                    maxStock: productStock,
                    addedAt: new Date().toISOString(),
                    lastUpdated: new Date().toISOString()
                });
            }

            this.saveCart();
            this.updateCartDisplay();
            this.showNotification(`✅ ${quantity} × ${this.truncateName(productName)} agregado al carrito`, 'success');

            // Resetear cantidad y abrir carrito
            if (quantityInput) {
                quantityInput.value = 1;
                quantityInput.blur();
            }

            // Auto-abrir carrito en primera adición
            if (this.cart.length === 1) {
                setTimeout(() => this.openCart(), 800);
            }

        } catch (error) {
            console.error('Error agregando al carrito:', error);
            this.showNotification('❌ Error al agregar producto. Intenta nuevamente.', 'error');
        } finally {
            this.isProcessing = false;
            this.showButtonLoading(button, false);
        }
    }

    validateProductData(id, name, price, stock) {
        return id && name && !isNaN(price) && price > 0 && !isNaN(stock) && stock >= 0;
    }

    removeFromCart(itemId) {
        const itemIndex = this.cart.findIndex(item => item.id === itemId);
        if (itemIndex === -1) return;

        const removedItem = this.cart[itemIndex];
        this.cart.splice(itemIndex, 1);
        this.saveCart();
        this.updateCartDisplay();

        this.showNotification(`🗑️ ${this.truncateName(removedItem.name)} eliminado del carrito`, 'info');

        // Cerrar carrito si queda vacío
        if (this.cart.length === 0) {
            setTimeout(() => this.closeCart(), 1500);
        }
    }

    updateQuantity(itemId, newQuantity) {
        const itemIndex = this.cart.findIndex(item => item.id === itemId);
        if (itemIndex === -1) return;

        const item = this.cart[itemIndex];

        if (newQuantity < 1) {
            this.removeFromCart(itemId);
            return;
        }

        if (newQuantity > item.maxStock) {
            this.showNotification(`🚫 Máximo ${item.maxStock} unidades disponibles`, 'error');
            return;
        }

        item.quantity = newQuantity;
        item.lastUpdated = new Date().toISOString();
        this.saveCart();
        this.updateCartDisplay();
    }

    clearCart() {
        if (this.cart.length === 0) return;

        const itemCount = this.getTotalItems();
        this.cart = [];
        this.saveCart();
        this.updateCartDisplay();
        this.closeCart();

        this.showNotification(`🛒 Carrito vaciado (${itemCount} productos eliminados)`, 'info');
    }

    // ==================== PERSISTENCIA MEJORADA ====================
    saveCart() {
        try {
            const cartData = {
                items: this.cart,
                lastUpdate: new Date().toISOString(),
                totalItems: this.getTotalItems(),
                totalValue: this.getCartTotal()
            };

            localStorage.setItem(this.CART_STORAGE_KEY, JSON.stringify(cartData));

            // Disparar evento personalizado para otros componentes
            window.dispatchEvent(new CustomEvent('cartUpdated', {
                detail: { cart: this.cart, total: this.getCartTotal() }
            }));

        } catch (error) {
            console.error('Error guardando carrito:', error);
            this.showNotification('❌ Error al guardar carrito', 'error');
        }
    }

    loadCartFromStorage() {
        try {
            const savedData = localStorage.getItem(this.CART_STORAGE_KEY);
            if (!savedData) return [];

            const cartData = JSON.parse(savedData);

            // Validar y limpiar datos corruptos
            const validItems = cartData.items?.filter(item =>
                item.id && item.name && !isNaN(item.price) && !isNaN(item.quantity)
            ) || [];

            console.log('📦 Carrito cargado:', validItems.length, 'productos válidos');
            return validItems;

        } catch (error) {
            console.error('Error cargando carrito:', error);
            return [];
        }
    }

    // ==================== INTERFAZ DE USUARIO MEJORADA ====================
    showButtonLoading(button, isLoading) {
        if (!button) return;

        const btnText = button.querySelector('.btn-text');
        const loadingText = button.querySelector('.btn-loading');

        if (isLoading) {
            btnText.style.display = 'none';
            loadingText.style.display = 'inline';
            button.disabled = true;
            button.classList.add('loading');
            button.setAttribute('aria-busy', 'true');
        } else {
            btnText.style.display = 'inline';
            loadingText.style.display = 'none';
            button.disabled = button.dataset.stock === '0';
            button.classList.remove('loading');
            button.setAttribute('aria-busy', 'false');
        }
    }

    createNotificationArea() {
        if (!document.getElementById('notification-area')) {
            const notificationArea = document.createElement('div');
            notificationArea.id = 'notification-area';
            notificationArea.className = 'notification-area';
            notificationArea.setAttribute('aria-live', 'polite');
            notificationArea.setAttribute('aria-atomic', 'true');
            document.body.appendChild(notificationArea);
        }
    }

    showNotification(message, type = 'info') {
        const notificationArea = document.getElementById('notification-area');
        if (!notificationArea) return;

        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        notification.innerHTML = `
            <span class="notification-icon">${this.getNotificationIcon(type)}</span>
            <span class="notification-message">${message}</span>
        `;
        notification.setAttribute('role', 'alert');

        notificationArea.appendChild(notification);

        // Auto-remover con animación
        setTimeout(() => {
            notification.style.opacity = '0';
            notification.style.transform = 'translateX(100%)';
            setTimeout(() => {
                if (notification.parentNode) {
                    notification.parentNode.removeChild(notification);
                }
            }, 300);
        }, 4000);
    }

    getNotificationIcon(type) {
        const icons = {
            success: '✅',
            error: '❌',
            warning: '⚠️',
            info: 'ℹ️'
        };
        return icons[type] || '📢';
    }

    updateCartDisplay() {
        this.updateCartCount();
        this.renderCartItems();
        this.updateCartButtonState();
    }

    updateCartCount() {
        const cartCount = document.getElementById('cart-count');
        if (cartCount) {
            const totalItems = this.getTotalItems();
            cartCount.textContent = totalItems;
            cartCount.setAttribute('aria-label', `${totalItems} productos en el carrito`);

            // Animación del contador
            cartCount.classList.add('pulse');
            setTimeout(() => cartCount.classList.remove('pulse'), 300);
        }
    }

    updateCartButtonState() {
        const checkoutBtn = document.getElementById('checkout-btn');
        if (checkoutBtn) {
            checkoutBtn.disabled = this.cart.length === 0;
            checkoutBtn.textContent = this.cart.length > 0 ?
                `Pagar S/ ${this.getCartTotal().toFixed(2)}` :
                'Realizar Compra';
        }
    }

    renderCartItems() {
        const cartItems = document.getElementById('cart-items');
        const cartTotal = document.getElementById('cart-total-amount');
        const cartTotalDiv = document.querySelector('.cart-total');

        if (!cartItems) return;

        if (this.cart.length === 0) {
            cartItems.innerHTML = `
                <div class="empty-cart-state">
                    <div class="empty-cart-icon">🛒</div>
                    <p>Tu carrito está vacío</p>
                    <small>Agrega algunos productos para continuar</small>
                </div>
            `;
            cartTotalDiv.hidden = true;
            return;
        }

        let total = 0;
        cartItems.innerHTML = this.cart.map(item => {
            const itemTotal = item.price * item.quantity;
            total += itemTotal;

            return `
                <div class="cart-item" data-item-id="${item.id}">
                    <img src="/img/${item.image}" alt="${item.name}" class="cart-item-image"
                         onerror="this.src='/img/default-product.jpg'">
                    <div class="cart-item-info">
                        <h4>${this.truncateName(item.name, 30)}</h4>
                        <p class="item-price">S/ ${item.price.toFixed(2)} c/u</p>
                        <div class="cart-quantity-controls">
                            <button onclick="cartManager.updateQuantity('${item.id}', ${item.quantity - 1})"
                                    ${item.quantity <= 1 ? 'disabled' : ''}
                                    aria-label="Reducir cantidad">-</button>
                            <span class="quantity-display" aria-live="polite">${item.quantity}</span>
                            <button onclick="cartManager.updateQuantity('${item.id}', ${item.quantity + 1})"
                                    ${item.quantity >= item.maxStock ? 'disabled' : ''}
                                    aria-label="Aumentar cantidad">+</button>
                        </div>
                    </div>
                    <div class="cart-item-total">
                        <span class="item-total-amount">S/ ${itemTotal.toFixed(2)}</span>
                        <button onclick="cartManager.removeFromCart('${item.id}')"
                                class="remove-item-btn"
                                aria-label="Eliminar ${item.name} del carrito"
                                title="Eliminar producto">×</button>
                    </div>
                </div>
            `;
        }).join('');

        // Agregar resumen
        cartItems.innerHTML += `
            <div class="cart-summary">
                <div class="summary-row">
                    <span>Subtotal:</span>
                    <span>S/ ${total.toFixed(2)}</span>
                </div>
                <div class="summary-row">
                    <span>Envío:</span>
                    <span>Gratis</span>
                </div>
                <div class="summary-row total">
                    <strong>Total:</strong>
                    <strong>S/ ${total.toFixed(2)}</strong>
                </div>
            </div>
        `;

        cartTotal.textContent = total.toFixed(2);
        cartTotalDiv.hidden = false;
    }

    // ==================== GESTIÓN DE MODALES MEJORADA ====================
    toggleCart() {
        const modal = document.getElementById('cart-modal');
        if (modal.hidden) {
            this.openCart();
        } else {
            this.closeCart();
        }
    }

    openCart() {
        const modal = document.getElementById('cart-modal');
        const toggleButton = document.getElementById('cart-toggle-button');

        if (!modal || !toggleButton) return;

        modal.hidden = false;
        toggleButton.setAttribute('aria-expanded', 'true');
        this.renderCartItems();

        // Enfocar el modal para accesibilidad
        setTimeout(() => modal.focus(), 100);
    }

    closeCart() {
        const modal = document.getElementById('cart-modal');
        const toggleButton = document.getElementById('cart-toggle-button');

        if (!modal || !toggleButton) return;

        modal.hidden = true;
        toggleButton.setAttribute('aria-expanded', 'false');
        toggleButton.focus();
    }

    closeAllModals() {
        document.querySelectorAll('.modal-overlay').forEach(modal => {
            modal.hidden = true;
        });
    }

    showLoginRequiredModal() {
        const modal = document.getElementById('login-required-modal');
        if (modal) {
            modal.hidden = false;
            this.showNotification('🔐 Inicia sesión para continuar con tu compra', 'warning');
        }
    }

    // ==================== CHECKOUT MEJORADO ====================
    proceedToCheckout() {
        if (this.cart.length === 0) {
            this.showNotification('❌ Tu carrito está vacío', 'error');
            return;
        }

        if (!this.isUserLoggedIn) {
            this.showLoginRequiredModal();
            return;
        }

        // Validar stock antes de proceder
        const outOfStockItems = this.cart.filter(item => item.quantity > item.stock);
        if (outOfStockItems.length > 0) {
            this.showNotification('❌ Algunos productos no tienen stock suficiente', 'error');
            return;
        }

        this.showNotification('🚀 Procesando tu pedido...', 'info');

        // Simular proceso de checkout
        setTimeout(() => {
            const total = this.getCartTotal();
            const itemCount = this.getTotalItems();

            console.log('✅ Checkout completado:', {
                items: this.cart,
                total: total,
                itemCount: itemCount
            });

            this.showNotification(`🎉 ¡Compra exitosa! Total: S/ ${total.toFixed(2)}`, 'success');
            this.clearCart();

            // Redirección real (descomentar en producción)
            // window.location.href = '/checkout?total=' + total;

        }, 2000);
    }

    // ==================== UTILIDADES ====================
    truncateName(name, maxLength = 25) {
        return name.length > maxLength ? name.substring(0, maxLength) + '...' : name;
    }

    getCartTotal() {
        return this.cart.reduce((total, item) => total + (item.price * item.quantity), 0);
    }

    getTotalItems() {
        return this.cart.reduce((total, item) => total + item.quantity, 0);
    }

    getCartItems() {
        return [...this.cart];
    }

    setupImageErrorHandling() {
        document.addEventListener('error', (e) => {
            if (e.target.classList.contains('product-image')) {
                e.target.src = e.target.dataset.fallback || '/img/default-product.jpg';
            }
        }, true);
    }

    filterProducts(category) {
        console.log('Filtrando productos por categoría:', category);
        // Aquí iría la lógica de filtrado real
        this.showNotification(`Filtrando por: ${category}`, 'info');
    }

    // ==================== GESTIÓN DE USUARIO ====================
    loginUser(userData = {}) {
        const sessionData = {
            isLoggedIn: true,
            user: userData,
            loginTime: new Date().toISOString()
        };

        localStorage.setItem(this.USER_SESSION_KEY, JSON.stringify(sessionData));
        this.isUserLoggedIn = true;
        this.showNotification('🎉 ¡Sesión iniciada correctamente!', 'success');

        // Disparar evento de login
        window.dispatchEvent(new CustomEvent('userLoggedIn', { detail: userData }));
    }

    logoutUser() {
        localStorage.removeItem(this.USER_SESSION_KEY);
        this.isUserLoggedIn = false;
        this.showNotification('👋 Sesión cerrada correctamente', 'info');

        // Disparar evento de logout
        window.dispatchEvent(new CustomEvent('userLoggedOut'));
    }

    // ==================== MÉTODOS DE DIAGNÓSTICO ====================
    debugCart() {
        console.group('🔍 Debug del Carrito');
        console.log('Productos:', this.cart);
        console.log('Total items:', this.getTotalItems());
        console.log('Total valor:', this.getCartTotal());
        console.log('Usuario logueado:', this.isUserLoggedIn);
        console.groupEnd();
    }
}

// ==================== INICIALIZACIÓN MEJORADA ====================
document.addEventListener('DOMContentLoaded', function() {
    // Inicializar CartManager
    window.cartManager = new CartManager();

    // Simular estados para testing
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('debug') === 'true') {
        window.cartManager.debugCart();
    }

    if (urlParams.get('login') === 'true') {
        window.cartManager.loginUser({ name: 'Usuario Demo', email: 'demo@tecnoaccesorios.com' });
    }

    if (urlParams.get('logout') === 'true') {
        window.cartManager.logoutUser();
    }

    // Exponer métodos globales
    window.addToCart = (productId) => {
        const button = document.querySelector(`[data-id="${productId}"]`);
        if (button) {
            button.click();
        } else {
            console.warn('Botón no encontrado para productId:', productId);
        }
    };

    window.getCartSummary = () => {
        return {
            items: window.cartManager.getCartItems(),
            total: window.cartManager.getCartTotal(),
            itemCount: window.cartManager.getTotalItems()
        };
    };

    console.log('🛍️ TecnoAccesorios - Sistema de carrito cargado correctamente');
});

// Manejo de errores global
window.addEventListener('error', (e) => {
    console.error('Error global:', e.error);
    if (window.cartManager) {
        window.cartManager.showNotification('❌ Error en la aplicación', 'error');
    }
});
// services.js - Gestión de servicios
class ServiceManager {
    constructor() {
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.setMinDate();
    }

    setupEventListeners() {
        document.querySelectorAll('.close-modal').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const modal = e.target.closest('.modal-overlay');
                if (modal) modal.hidden = true;
            });
        });

        document.querySelectorAll('.modal-overlay').forEach(modal => {
            modal.addEventListener('click', (e) => {
                if (e.target === modal) modal.hidden = true;
            });
        });

        document.getElementById('service-form')?.addEventListener('submit', (e) => {
            this.handleFormSubmit(e);
        });
    }

    setMinDate() {
        const today = new Date().toISOString().split('T')[0];
        const fechaInput = document.getElementById('fecha-cita');
        if (fechaInput) {
            fechaInput.min = today;
        }
    }

    requestService(button) {
        const serviceId = button.dataset.id;
        const serviceName = button.dataset.name;
        const servicePrice = button.dataset.price;

        document.getElementById('selected-service-name').textContent = serviceName;
        document.getElementById('service-id').value = serviceId;
        document.getElementById('service-price').value = servicePrice;
        document.getElementById('service-request-form').style.display = 'block';

        document.getElementById('service-request-form').scrollIntoView({ behavior: 'smooth' });
    }

    cancelRequest() {
        document.getElementById('service-request-form').style.display = 'none';
        document.getElementById('service-form').reset();
    }

    async handleFormSubmit(e) {
        e.preventDefault();

        const submitBtn = e.target.querySelector('button[type="submit"]');
        const originalText = submitBtn.innerHTML;

        submitBtn.innerHTML = '⌛ Enviando...';
        submitBtn.disabled = true;

        try {
            await new Promise(resolve => setTimeout(resolve, 1000));
            this.showConfirmation();
        } catch (error) {
            console.error('Error:', error);
            alert('Error al enviar la solicitud');
        } finally {
            submitBtn.innerHTML = originalText;
            submitBtn.disabled = false;
        }
    }

    showConfirmation() {
        const serviceName = document.getElementById('selected-service-name').textContent;
        const servicePrice = document.getElementById('service-price').value;

        document.getElementById('confirm-service-name').textContent = serviceName;
        document.getElementById('confirm-service-price').textContent = `S/ ${servicePrice}`;
        document.getElementById('service-confirm-modal').hidden = false;
    }

    closeConfirmModal() {
        document.getElementById('service-confirm-modal').hidden = true;
        this.cancelRequest();
    }

    showRegisterModal() {
        document.getElementById('register-required-modal').hidden = false;
    }

    closeRegisterModal() {
        document.getElementById('register-required-modal').hidden = true;
    }
}

document.addEventListener('DOMContentLoaded', () => {
    window.serviceManager = new ServiceManager();
});