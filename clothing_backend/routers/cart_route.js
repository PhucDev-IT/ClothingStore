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
import product_model from "../models/product_model.js";

//Add new cart
//Step 1: check permission, only user
//Step 2: validate data
//

router.get('/cart', authenticateToken, authorizeRole(["user"]), async (req, res, next) => {
    try {
        const user_id = req.user.id;
        logger.info("Fetching cart for user_id: " + user_id);   

        // Tìm giỏ hàng cho người dùng và bao gồm CartItems và ProductDetails
        const cart = await cart_model.Cart.findOne({
            where: { user_id },
            include: [
                {
                    model: cart_model.CartItem,
                    include: [
                        {
                            model: product_model.ProductDetails
                        }
                    ]
                }
            ]
        });

        //console.log('Cart:', JSON.stringify(cart, null, 2));
        if (!cart) {
            const errorResponse = new Models.ErrorResponseModel('CART_NOT_FOUND', 'Cart not found for the user.');
            return res.status(404).json(new Models.ResponseModel(false, errorResponse));
        }

        // Lấy thông tin cart_items
        const cartItems = cart.cart_items.map(cartItem => ({
            id: cartItem.id,
            product_details_id: cartItem.product_details_id,
            cart_id: cartItem.cart_id,
            quantity: cartItem.quantity,
            color: cartItem.color,
            size: cartItem.size,
            createdAt: cartItem.createdAt,
            updatedAt: cartItem.updatedAt
        }));

        // Trả về phản hồi với cart_items
        return res.status(200).json(new Models.ResponseModel(true, null, { cart_items: cartItems }));
    } catch (error) {
        logger.error('Error fetching cart items:', error.message);
        const errorResponse = new Models.ErrorResponseModel('INTERNAL_SERVER_ERROR', 'Error fetching cart items', [error.message]);
        return res.status(500).json(new Models.ResponseModel(false, errorResponse));
    }
});


router.post('/cart',authenticateToken,authorizeRole(["user"]),async (req,res,next)=>{
    const { product_details_id, quantity, color, size } = req.body;
    const user_id = req.user.id;
    logger.info("user_id request add cart: " + user_id);
    try {

        // Kiểm tra xem giỏ hàng của user đã tồn tại chưa
        let cart = await cart_model.Cart.findOne({ where: { user_id } });

        // Nếu giỏ hàng chưa tồn tại, tạo giỏ hàng mới
        if (!cart) {
            cart = await cart_model.Cart.create({ user_id });
        }

        // Kiểm tra xem sản phẩm đã tồn tại trong giỏ hàng chưa
        let cartItem = await cart_model.CartItem.findOne({
            where: {
                product_details_id: product_details_id,
                cart_id: cart.id,
                color: color,
                size: size
            }
        });

        if (cartItem) {
            // Nếu sản phẩm đã tồn tại, cập nhật số lượng
            cartItem = await cartItem.update({ quantity: cartItem.quantity + quantity });
        } else {
            // Nếu sản phẩm chưa tồn tại, tạo mới
            cartItem = await cart_model.CartItem.create({
                product_details_id: product_details_id,
                cart_id: cart.id,
                quantity: quantity,
                color: color,
                size: size
            });
        }

        return res.status(200).json(new Models.ResponseModel(true, null, cartItem));
    } catch (error) {
        logger.error('Error adding product to cart:', error);
        return res.status(500).json(new Models.ResponseModel(false, new Models.ErrorResponseModel(1, "Lỗi hệ thống", error.message), null));
    }
});


router.put('/cart/:cartItemId', authenticateToken,authorizeRole(["user"]), async (req, res, next) => {
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

router.delete('/cart/:cartItemId', authenticateToken,authorizeRole(["user"]), async (req, res, next) => {
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