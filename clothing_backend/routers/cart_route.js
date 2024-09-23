import express from "express";
import Joi from "joi";
const router = express.Router(); // Sử dụng express.Router()
import { formatValidationError } from "../utils/exception.js";
import { authenticateToken, authorizeRole } from "../config/jwt_filter.js";
import logger from "../utils/logger.js";
import Image from "../models/image_model.js";
import response_model from "../models/response/ResponseModel.js";
import cart_model from "../models/cart_model.js";
import CartResponseModels from '../models/response/CartResponseModel.js'
import Models from "../models/response/ResponseModel.js";

//Add new cart
//Step 1: check permission, only user
//Step 2: validate data
//

router.get('/', authenticateToken, authorizeRole(["user"]), async (req, res, next) => {
    try {
        const user_id = req.user.id;
        const cart = await cart_model.Cart.findOne({ where: { user_id } });
        if (!cart) {
            const errorResponse = new Models.ErrorResponseModel('CART_NOT_FOUND', 'Cart not found for the user.');
            return res.status(404).json(new Models.ResponseModel(false, errorResponse));
        }

        const cartItems = await cart_model.CartItem.findAll({
            where: { cart_id: cart.id },
            include: [{ model: cart_model.Product }]  
        });

        const cartItemResponses = cartItems.map(cartItem => {
            const product = cartItem.Product; 
            return new CartResponseModels.CartItemResponseModel(
                cartItem.id,
                product.id,
                cartItem.quantity,
                cartItem.color,
                cartItem.size,
                product.image,   
                product.name,    
                product.price    
            );
        });

        const cartResponse = new CartResponseModels.CartResponseModel(req.user, null, cartItemResponses);

        return res.status(200).json(new Models.ResponseModel(true, null, cartResponse));
    } catch (error) {
        logger.error('Error fetching cart items:', error);
        const errorResponse = new Models.ErrorResponseModel('INTERNAL_SERVER_ERROR', 'Error fetching cart items', [error.message]);
        return res.status(500).json(new Models.ResponseModel(false, errorResponse));
    }
});


router.post('/',authenticateToken,authorizeRole(["user"]),async (req,res,next)=>{
    const { product_details_id, quantity, color, size } = req.body;
    const user_id = req.user.id;
    logger.info("user_id request: "+user_id);
    try {

        // Kiểm tra xem giỏ hàng của user đã tồn tại chưa
        let cart = await cart_model.Cart.findOne({ where: { user_id } });

        // Nếu giỏ hàng chưa tồn tại, tạo giỏ hàng mới
        if (!cart) {
            cart = await cart_model.Cart.create({ user_id });
        }

        // Thêm sản phẩm vào giỏ hàng
        const cartItem = await cart_model.CartItem.create({
            product_details_id,
            cart_id: cart.id,
            quantity,
            color,
            size
        });
       
        return res.status(200).json(new Models.ResponseModel(true, null, cartItem));
    } catch (error) {
        console.error('Error adding product to cart:', error);
        return res.status(500).json(new Models.ResponseModel(false, new Models.ErrorResponseModel(1, "Lỗi hệ thống", error.message), null));
    }
});




router.put('/:cartItemId', authenticateToken,authorizeRole(["user"]), async (req, res, next) => {
    const { cartItemId } = req.params;
    const { quantity, color, size } = req.body;

    try {
        // Tìm kiếm item trong giỏ hàng dựa trên cartItemId
        let cartItem = await cart_model.CartItem.findOne({ where: { id: cartItemId } });

        // Nếu không tìm thấy item, trả về lỗi
        if (!cartItem) {
            return res.status(404).json({
                success: false,
                message: 'Cart item not found!'
            });
        }

        // Cập nhật thông tin của sản phẩm trong giỏ hàng
        await cartItem.update({
            //Nếu không thay đổi thì giữ nguyên
            quantity: quantity || cartItem.quantity, 
            color: color || cartItem.color,          
            size: size || cartItem.size              
        });

        return res.status(200).json({
            success: true,
            message: 'Cart item updated successfully!',
            cartItem
        });
    } catch (error) {
        console.error('Error updating cart item:', error);
        return res.status(500).json({
            success: false,
            message: 'Error updating cart item',
            error: error.message
        });
    }
});

router.delete('/:cartItemId', authenticateToken,authorizeRole(["user"]), async (req, res, next) => {
    const { cartItemId } = req.params;
    try {
        // Tìm kiếm item trong giỏ hàng dựa trên cartItemId
        const cartItem = await cart_model.CartItem.findOne({ where: { id: cartItemId } });

        // Nếu không tìm thấy item, trả về lỗi
        if (!cartItem) {
            return res.status(404).json({
                success: false,
                message: 'Cart item not found!'
            });
        }

        // Xóa item khỏi giỏ hàng
        await cartItem.destroy();

        return res.status(200).json({
            success: true,
            message: 'Cart item deleted successfully!'
        });
    } catch (error) {
        console.error('Error deleting cart item:', error);
        return res.status(500).json({
            success: false,
            message: 'Error deleting cart item',
            error: error.message
        });
    }
});


export default router;