// Định nghĩa và export từng class riêng lẻ

export class CartResponseModel {
    constructor(user = null, token = null) {
        this.user = user;
        this.token = token;
    }
}

export class CartItemResponseModel {
    constructor(id = null, productId = null, quantity = null, color = null, size = null, image = null, name = null, price = null) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.color = color;
        this.size = size;
        this.image = image;
        this.name = name;
        this.price = price;
    }
}
