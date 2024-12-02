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
import product_model from "../models/product_model.js";
import { or, QueryTypes, Sequelize } from "sequelize";
import Notification from "../models/notification_model.js";
import notification_router from "./notification_router.js"
import notificationQueue from '../queues/notificationQueue.js';
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

const order_status = Joi.object({
    order_id: Joi.string().required().messages({
        "any.required": "order_id là bắt buộc",
        "string.base": "order_id : order_id bắt buộc"
    }),
    status: Joi.string().required().messages({
        "any.required": "status là bắt buộc",
        "string.base": "status : status bắt buộc"
    }),
    note: Joi.string().allow(null, '').empty(true).optional(),
})

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
    fee_ship: Joi.number().required().messages({
        "any.required": "fee_ship là bắt buộc",
        "string.base": "fee_ship : Phí vận chuyển"
    }),

    voucher_id: Joi.string().allow(null, '').empty(true).optional(),
    list_item: Joi.array().items(order_item_request).required()
});


//Update status order
router.post('/order_status',authenticateToken, authorizeRole(["user","admin"]), async (req, res, next) => {
    const { error, value } = order_status.validate(req.body, { abortEarly: false });
    if (error) {
        const formattedErrors = error ? formatValidationError(error.details) : formatValidationError(error.details);
        return res.status(400).json(new response_model.ResponseModel(false, new response_model.ErrorResponseModel(1, "Validation Error", formattedErrors), null));
    }
    const user_id = req.user.id;
    logger.info(`User: ${user_id} is update order id: ${value.order_id}`)
    try {
       const order = await order_model.OrderStatus.create({
        order_id: value.order_id,
        note:value.note,
        status: value.status
       })

        return res.status(200).json(new response_model.ResponseModel(true, null, order));
    } catch (error) {
        const errorResponse = new response_model.ErrorResponseModel('INTERNAL_SERVER_ERROR', 'Error update order_status', [error.message]);
        return res.status(500).json(new response_model.ResponseModel(false, errorResponse));
    }

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
            fee_ship: value.fee_ship,
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

        const orderStatus = await order_model.OrderStatus.create({
            order_id: order.id,
            status: "PENDING",
        }, { transaction });

        // Lưu các OrderItems vào cơ sở dữ liệu
        await order_model.OrderItem.bulkCreate(orderItems, { transaction });

    
         Notification.create({
            title:"Đặt hàng thành công!",
            content: `Bạn đã đặt hàng thành công. Mã đơn hàng ${order.id} đang được người bán chuẩn bị`,
            type: "PAYMENT",
            is_action: false,
            is_read:false,
            user_id: user_id
         });
         const notification = {
            title: 'Đặt hàng thành công',
            body: `Đơn hàng của bạn với ID ${order.id} đã được xử lý thành công!`,
          };
        //await notificationQueue.add({user_id,notification})
         
        // Commit transaction nếu tất cả thành công
        await transaction.commit();
        return res.status(200).json(new response_model.ResponseModel(true, null, order));
    } catch (error) {
        // Rollback nếu có lỗi
        await transaction.rollback();
        logger.error("Lỗi: "+ error);
        const errorResponse = new response_model.ErrorResponseModel('INTERNAL_SERVER_ERROR', 'Error creating order', [error.message]);
        return res.status(500).json(new response_model.ResponseModel(false, errorResponse));
    }
});

//get all
router.get('/my_orders/:status', authenticateToken, authorizeRole(["user"]), async (req, res, next) => {
    const user_id = req.user.id;
    const status = req.params.status;
    const page = parseInt(req.query.page) || 1;  // Trang hiện tại
    const limit = parseInt(req.query.limit) || 10;  // Số lượng sản phẩm mỗi trang
    const offset = (page - 1) * limit;  // Tính toán vị trí bắt đầu
    logger.info(`=====> USER ${user_id} GET ALL ORDER status: ${status} <============`);

    try {
        // Lấy tổng số sản phẩm thuộc user
        const totalOrders = await order_model.Order.count({
            where: { user_id: user_id }  // Điều kiện lấy theo user_id
        });

        // Lấy danh sách sản phẩm có phân trang
        const orders = await sequelize.query(`
            SELECT 
    o.id AS order_id,
    o.total,
    o.real_total,
    o.fee_ship,
    os.status,
    o.order_date,
    o.delivery_information,
    COUNT(oi.id) AS item_count,
    (SELECT p.img_preview 
     FROM products p 
     INNER JOIN order_items oi2 ON p.id = oi2.product_id 
     WHERE oi2.order_id = o.id 
     ORDER BY oi2.id ASC 
     LIMIT 1) AS first_product_image,
    (SELECT p.name
        FROM products p 
        INNER JOIN order_items oi2 ON p.id = oi2.product_id 
        WHERE oi2.order_id = o.id 
        ORDER BY oi2.id ASC 
        LIMIT 1) AS first_product_name
FROM orders o
INNER JOIN order_items oi ON o.id = oi.order_id
INNER JOIN (
    SELECT os_inner.order_id, os_inner.status
    FROM order_statuses os_inner
    INNER JOIN (
        SELECT order_id, MAX(createdAt) AS latest_status_time
        FROM order_statuses
        GROUP BY order_id
    ) latest_status ON os_inner.order_id = latest_status.order_id 
                     AND os_inner.createdAt = latest_status.latest_status_time
) os ON o.id = os.order_id
WHERE o.user_id = :user_id
  AND os.status = :status
GROUP BY o.id
ORDER BY o.order_date DESC
LIMIT :limit OFFSET :offset;

        `, {
            replacements: {
                user_id: user_id,
                status: status,
                limit: limit,  // Giới hạn số bản ghi trả về
                offset: offset    // Bỏ qua các bản ghi trước đó
            },
            type: QueryTypes.SELECT
        });

        // Tính tổng số trang dựa trên tổng số sản phẩm và limit
        const totalPages = Math.ceil(totalOrders / limit);

        // Trả về dữ liệu bao gồm danh sách sản phẩm và thông tin phân trang
        res.status(200).json({
            success: true,
            data: {
                orders: orders,
                pagination: {
                    totalItems: totalOrders,
                    totalPages: totalPages,
                    currentPage: page,
                    itemsPerPage: limit
                }
            }
        });
    } catch (err) {
        logger.error("Error fetching orders:", err);
        return res.status(500).json(new response_model.ResponseModel(false, new response_model.ErrorResponseModel(1, "Lỗi hệ thống", err.message), null));
    }
});

//get order by id - user
router.get('/:id', authenticateToken, authorizeRole(["user"]), async (req, res, next) => {
    try {
        const user_id = req.user.id;
        const orderId = req.params.id;
        const order = await order_model.Order.findOne({
            where: {
                id: orderId,
                user_id: user_id
            },
            include: [
                {
                    model: product_model.Product,
                    attributes: ['id', 'img_preview', 'name']
                },
                {
                    model: Voucher, // Bao gồm cả Voucher nếu cần
                    attributes: ['id', 'discount', "type"]
                },
                {
                    model: order_model.OrderStatus, // Bao gồm OrderStatus
                    attributes: ['id', 'status', 'updatedAt']
                }
            ]
        });
        if (!order) {
            return res.json(new response_model.ResponseModel(false, new response_model.ErrorResponseModel(2, "Đơn hàng không tìm thấy", null), null));
        }

        const order_items = order.products.map((item) => ({
            product_id: item.id,
            img_preview: item.img_preview,
            name: item.name,
            id: item.order_item.id,
            order_id: item.order_item.order_id,
            color: item.order_item.color,
            size: item.order_item.size,
            quantity: item.order_item.quantity,
            price: item.order_item.price
        }));

        const order_response = {
            voucher: order.voucher,
            order_items:order_items,
            order_status:order.order_statuses

        }

      
        return res.status(200).json(new response_model.ResponseModel(true, null,order_response));
    } catch (err) {
        logger.error("Error fetching order:", err);
        return res.status(500).json(new response_model.ResponseModel(false, new response_model.ErrorResponseModel(1, "Lỗi hệ thống", err.message), null));
    }
});

//Lấy tất cả order với trạng thái đơn hàng(order_statuses(status)) và phân trang(lấy thêm thông tin user[id, name] ứng với order đó) - role:admin
router.get("/", authenticateToken, authorizeRole(["admin"]), async (req, res, next) =>{
    const page = parseInt(req.query.page) || 1;  
    const limit = parseInt(req.query.limit) || 10;  
    const offset = (page - 1) * limit;
    logger.info(`=====> ADMIN GET ALL ORDERS <============`);
    try {
        // Get the total number of orders (for pagination)
        const totalOrders = await order_model.Order.count(); // Count all orders

        // Fetch all orders with the associated user information and order status with pagination
        const orders = await sequelize.query(`
            SELECT 
                o.id AS order_id,
                o.total,
                o.real_total,
                o.fee_ship,
                os.status,
                o.order_date,
                o.delivery_information,
                u.id AS user_id,
                u.full_name AS user_name,
                COUNT(oi.id) AS item_count,
                (SELECT p.img_preview 
                 FROM products p 
                 INNER JOIN order_items oi2 ON p.id = oi2.product_id 
                 WHERE oi2.order_id = o.id 
                 ORDER BY oi2.id ASC 
                 LIMIT 1) AS first_product_image,
                (SELECT p.name
                 FROM products p 
                 INNER JOIN order_items oi2 ON p.id = oi2.product_id 
                 WHERE oi2.order_id = o.id 
                 ORDER BY oi2.id ASC 
                 LIMIT 1) AS first_product_name
            FROM orders o
            INNER JOIN order_items oi ON o.id = oi.order_id
            INNER JOIN users u ON o.user_id = u.id
            INNER JOIN (
                SELECT os_inner.order_id, os_inner.status
                FROM order_statuses os_inner
                INNER JOIN (
                    SELECT order_id, MAX(createdAt) AS latest_status_time
                    FROM order_statuses
                    GROUP BY order_id
                ) latest_status ON os_inner.order_id = latest_status.order_id 
                                 AND os_inner.createdAt = latest_status.latest_status_time
            ) os ON o.id = os.order_id
            GROUP BY o.id, u.id, os.status
            ORDER BY o.order_date DESC
            LIMIT :limit OFFSET :offset;
        `, {
            replacements: {
                limit: limit,
                offset: offset
            },
            type: QueryTypes.SELECT
        });

        // Calculate total pages for pagination
        const totalPages = Math.ceil(totalOrders / limit);

        // Return the response with order data and pagination info
        res.status(200).json({
            success: true,
            data: {
                orders: orders,
                pagination: {
                    totalItems: totalOrders,
                    totalPages: totalPages,
                    currentPage: page,
                    itemsPerPage: limit
                }
            }
        });
    } catch (err) {
        logger.error("Error fetching order:", err);
        return res.status(500).json(new response_model.ResponseModel(false, new response_model.ErrorResponseModel(1, "Lỗi hệ thống", err.message), null));
    }
});


export default router;