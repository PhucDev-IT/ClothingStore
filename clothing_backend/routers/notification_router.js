import express from "express";
import Joi from "joi";
const router = express.Router(); // Sử dụng express.Router()
import { formatValidationError } from "../utils/exception.js";
import { authenticateToken, authorizeRole } from "../config/jwt_filter.js";
import logger from "../utils/logger.js";
import Models from "../models/response/ResponseModel.js";
import Notification from "../models/notification_model.js";

const newNotification = async (notification) => {
    try {
        await Notification.create(notification);
    } catch (error) {
        console.error('Error new notification:', error);
    }
};

// count number of unread messages by user_id
router.get('/notifications/count/:id', async(req, res, next) =>{
    const { id } = req.params;
    try {
        // Đếm số lượng thông báo chưa đọc của userId
        const unreadCount = await Notification.count({
            where: {
                user_id: id,    // Tìm thông báo theo user_id
                is_read: 0 // Lọc các thông báo chưa đọc
            }
        });
        return res.json(new Models.ResponseModel(true, null, { unreadCount }));
    } catch (error) {
        return res.status(500).json(new Models.ResponseModel(false, new Models.ErrorResponseModel(1, "Lỗi hệ thống", error.message), null));
    }
});


//========================================
//1. Get 10-20 first notifications 

//=========================================
//get all notification by user_id
router.get('/notifications', authenticateToken, authorizeRole(["user"]), async (req,res,next)=>{
    try{
        const userId = req.user.id;
        const notifications = await Notification.findAll({
            where: { user_id: userId },
            order: [['createdAt', 'DESC']], 
        });
        return res.status(200).json(new Models.ResponseModel(true, null, notifications));
    }catch (error) {
        logger.error('Error fetching notifications:', error);
        return res.status(500).json(new Models.ResponseModel(false, new Models.ErrorResponseModel(1, "Lỗi hệ thống", error.message), null));
    }
});

//=============================
// only update is_read
// => Get id from user => search notification by id => re update [not receive each data]
//============================
router.put('/notification/:id', authenticateToken, authorizeRole(["admin"]),async (req,res,next)=>{
    try{
        const notificationId = req.params.id;
        const { title, content, type, is_action, is_read } = req.body;

        const notification = await Notification.findByPk(notificationId);
        if (!notification) {
            return res.status(404).json(new Models.ResponseModel(false, new Models.ErrorResponseModel(2, "Notification không tồn tại", null), null));
        }
        await notification.update({
            is_read: is_read !== undefined ? is_read : notification.is_read 
        });
        return res.status(200).json(new Models.ResponseModel(true, null, notification));
    }
catch (error) {
        logger.error('Error updating notification:', error);
        return res.status(500).json(new Models.ResponseModel(false, new Models.ErrorResponseModel(1, "Lỗi hệ thống", error.message), null));
    }
});


export default router;