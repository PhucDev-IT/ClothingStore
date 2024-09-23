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

// ----------Order-----------
//get all
router.get('/orders', async (req, res, next) =>{
    const page = parseInt(req.query.page) || 1;
    const limit = parseInt(req.query.limit) || 20;
    const offset = (page - 1) * limit;

    try {
        const { count, rows } = await order_model.Order.findAndCountAll({
            limit,
            offset
        });
        const totalPages = Math.ceil(count / limit);
        return res.status(200).json(new Models.ResponseModel(true, null, {
            orders: rows,
            // pagination: {
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
router.get('/order/:id', async (req, res, next) => {
    const orderId = req.params.id; 
    try {
        const order = await order_model.Order.findByPk(orderId); 
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