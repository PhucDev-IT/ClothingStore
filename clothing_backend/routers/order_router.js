import express from "express";
import Joi from "joi";
const router = express.Router(); // Sử dụng express.Router()
import { formatValidationError } from "../utils/exception.js";
import order_model from '../models/order_model.js';
import upload from "../config/upload.js";
import Models from "../models/response/ResponseModel.js";
import logger from "../utils/logger.js";
import Voucher from "../models/voucher_model.js";
import address_model from "../models/address_model.js";
import User from "../models/user_model.js";
import sequelize from '../connection/mysql.js';
import { authenticateToken, authorizeRole } from "../config/jwt_filter.js";

// ----------Order-----------
//get all
router.get('/orders',authenticateToken, authorizeRole(["user"]), async (req, res, next) =>{
    try {
        const user_id = req.user.id;
        const page = parseInt(req.query.page) || 1;
        const limit = parseInt(req.query.limit) || 20;
        const offset = (page - 1) * limit;
        logger.info("Fetching cart for user_id: " + user_id);
        const { count, rows } = await order_model.Order.findAndCountAll({
            where: { user_id: user_id },     
            limit,
            offset
        });
        const totalPages = Math.ceil(count / limit);
        return res.status(200).json(new Models.ResponseModel(true, null, {
            user_id: user_id,
            orders: rows,
            // pagination:{
            //     totalOrders: count,
            //     totalPages,
            //     currentPage: page,
            //     limit
            // }
        }));
    } catch (err) {
        logger.error("Error fetching orders:", err);
        return res.status(500).json(new Models.ResponseModel(false, new Models.ErrorResponseModel(1, "Lỗi hệ thống", err.message), null));
    }
});

//get order by id
router.get('/order/:id',authenticateToken, authorizeRole(["user"]), async (req, res, next) => {
    try {
        const user_id = req.user.id;
        const orderId = req.params.id; 
        const order = await order_model.Order.findOne({
            where: {
                id: orderId,
                user_id: user_id 
            }
        });
        if (!order) {
            return res.status(404).json(new Models.ResponseModel(false, new Models.ErrorResponseModel(2, "Đơn hàng không tìm thấy", null), null));
        }
        return res.status(200).json(new Models.ResponseModel(true, null, order));
    } catch (err) {
        logger.error("Error fetching order:", err);
        return res.status(500).json(new Models.ResponseModel(false, new Models.ErrorResponseModel(1, "Lỗi hệ thống", err.message), null));
    }
});




export default router;