import express from "express";
import Joi from "joi";
const router = express.Router(); // Sử dụng express.Router()
import { formatValidationError } from "../utils/exception.js";
import { authenticateToken, authorizeRole } from "../config/jwt_filter.js";
import logger from "../utils/logger.js";
import Image from "../models/image_model.js";
import response_model from "../models/response/ResponseModel.js";
import cart_model from "../models/cart_model.js";



//Add new cart
//Step 1: check permission, only user
//Step 2: validate data
//
router.post('/',authenticateToken,authorizeRole['user'],async (req,res,next)=>{
    const { user_id, product_details_id, quantity, color, size } = req.body;

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

        return res.status(201).json({
            success: true,
            message: 'Product added to cart successfully!',
            cartItem
        });
    } catch (error) {
        console.error('Error adding product to cart:', error);
        return res.status(500).json({
            success: false,
            message: 'Error adding product to cart',
            error: error.message
        });
    }
});




export default router;