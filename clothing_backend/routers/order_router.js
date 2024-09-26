import express from "express";
import Joi from "joi";
const router = express.Router(); // Sử dụng express.Router()
import { formatValidationError } from "../utils/exception.js";
import order_model from '../models/order_model.js';
import upload from "../config/upload.js";
import response_model from '../models/response/ResponseModel.js'
import logger from "../utils/logger.js";
import Voucher from "../models/voucher_model.js";
import address_model from "../models/address_model.js";
import User from "../models/user_model.js";
import sequelize from '../connection/mysql.js';
import { authenticateToken, authorizeRole } from "../config/jwt_filter.js";

// ----------Order-----------


const order_item_request = Joi.object({
    color: Joi.string().required().messages({
        "any.required": "color  là bắt buộc",
        "string.base": "color phải là chuỗi ký tự"
    }),

    size: Joi.string().required().messages({
        "any.required": "size là bắt buộc",
        "string.base": "size required"
    }),
    quantity: Joi.number().integer().required().messages({
        'number.base': `"quantity" should be a type of 'number'`, // Thông báo lỗi nếu không phải là số
        'number.integer': `"quantity" must be an integer`, // Thông báo lỗi nếu không phải là số nguyên
        'any.required': `"quantity" is a required field` // Thông báo lỗi nếu thiếu trường
    }),
    price: Joi.number().required().messages({
        "any.required": "price là bắt buộc",
        "string.base": "price phải là số thực"
    }),
    product_id: Joi.string().required().messages({
        "any.required": "product_id là bắt buộc",
        "string.base": "product_id phải là chuỗi ký tự"
    }),

});

const order_request = Joi.object({
    total: Joi.number().required().messages({
        "any.required": "total  là bắt buộc",
        "string.base": "total phải là số thực"
    }),

    real_total: Joi.number().required().messages({
        "any.required": "real_total là bắt buộc",
        "string.base": "real_total : Tổng tiền cuối cùng"
    }),
    delivery_information: Joi.string().required().messages({
        "any.required": "delivery_information là bắt buộc",
        "string.base": "delivery_information : Thông tin chi tiết nhận hàng"
    }),

    voucher_id: Joi.string().allow(null, '').empty(true).optional(),
    status: Joi.string().allow(null).optional(),
    list_item: Joi.array().items(order_item_request).required()
});

router.post('/', authenticateToken, authorizeRole(["user"]), async (req, res, next) => {
    const { error, value } = order_request.validate(req.body, { abortEarly: false });
    if (error) {
        const formattedErrors = error ? formatValidationError(error.details) : formatValidationError(error.details);
        return res.status(400).json(new response_model.ResponseModel(false, new response_model.ErrorResponseModel(1, "Validation Error", formattedErrors), null));
    }
    const transaction = await sequelize.transaction(); // Tạo transaction để đảm bảo dữ liệu nhất quán

    try {
        const user_id = req.user.id;
        logger.info(`User: ${user_id} is requesting payment order`)
        // Tạo đơn hàng (order)
        const order = await order_model.Order.create({
            total: value.total,
            real_total: value.real_total,
            delivery_information: value.delivery_information,
            voucher_id: value.voucher_id,
            user_id: user_id,
            order_date: new Date()
        }, { transaction });

        // Tạo danh sách OrderItem từ list_item
        const orderItems = value.list_item.map((item) => ({
            order_id: order.id,    // Sử dụng id của đơn hàng vừa tạo
            color: item.color,
            size: item.size,
            product_id: item.product_id,
            quantity: item.quantity,
            price: item.price
        }));

        // Lưu các OrderItems vào cơ sở dữ liệu
        await order_model.OrderItem.bulkCreate(orderItems, { transaction });

        // Commit transaction nếu tất cả thành công
        await transaction.commit();

        return res.status(200).json(new response_model.ResponseModel(true, null, order));
    } catch (error) {
        // Rollback nếu có lỗi
        await transaction.rollback();

        const errorResponse = new response_model.ErrorResponseModel('INTERNAL_SERVER_ERROR', 'Error creating order', [error.message]);
        return res.status(500).json(new response_model.ResponseModel(false, errorResponse));
    }
});

//get all
router.get('/', authenticateToken, authorizeRole(["user"]), async (req, res, next) => {
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
        return res.status(200).json(new response_model.ResponseModel(true, null, {
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
        return res.status(500).json(new response_model.ResponseModel(false, new response_model.ErrorResponseModel(1, "Lỗi hệ thống", err.message), null));
    }
});

//get order by id
router.get('/:id', authenticateToken, authorizeRole(["user"]), async (req, res, next) => {
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
            return res.status(404).json(new response_model.ResponseModel(false, new response_model.ErrorResponseModel(2, "Đơn hàng không tìm thấy", null), null));
        }
        return res.status(200).json(new response_model.ResponseModel(true, null, order));
    } catch (err) {
        logger.error("Error fetching order:", err);
        return res.status(500).json(new response_model.ResponseModel(false, new response_model.ErrorResponseModel(1, "Lỗi hệ thống", err.message), null));
    }
});




export default router;