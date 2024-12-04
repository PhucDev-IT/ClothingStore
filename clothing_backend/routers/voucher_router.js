import express from "express";
import Joi from "joi";
const router = express.Router(); // Sử dụng express.Router()
import { formatValidationError } from "../utils/exception.js";
import { authenticateToken, authorizeRole } from "../config/jwt_filter.js";
import logger from "../utils/logger.js";
import voucher_model from "../models/voucher_model.js";
import Models from "../models/response/ResponseModel.js";
import User from "../models/user_model.js";
import { Op } from 'sequelize';
import sequelize from '../connection/mysql.js';

//get all voucher (phân trang - admin) 
router.get('/vouchers',authenticateToken, authorizeRole(["admin"]), async (req, res, next) =>{
    try{
        const page = parseInt(req.query.page) || 1; 
        const limit = parseInt(req.query.limit) || 20; 
        const offset = (page - 1) * limit;
    
        // Lấy tất cả vouchers với thông tin từ bảng VoucherUser
        const { count, rows } = await voucher_model.Voucher.findAndCountAll({
            offset: offset,
            limit: limit,
            order: [['createdAt', 'DESC']], // Sắp xếp theo ngày tạo mới nhất
            attributes: ['id', 'title', 'description', 'discount', 'type', 'start_at', 'end_at', 'is_public', 'createdAt'],
        });
        const totalPages = Math.ceil(count / limit);
        return res.status(200).json(new Models.ResponseModel(true, null, {
            vouchers: rows,
            pagination: {
                totalItems: count,
                totalPages: totalPages,
                currentPage: page,
                itemsPerPage: limit
            }
        }));
    } catch (error) {
    logger.error('Error fetching vouchers:', error);
    return res.status(500).json(new Models.ResponseModel(false, new Models.ErrorResponseModel(1, "Lỗi hệ thống", error.message), null));
    }
});


// get all voucher by user_id (phân trang - user) - lấy các voucher có thời hạn(start_at <= current_date <= end_at), is_public = true

/*
1. Select with condition: [ is_public = true ,(start_at <= current_date <= end_at), quantity > 0 , condition = all or user_id = seft ]
*/
router.get('/vouchers/my_voucher',authenticateToken, authorizeRole(["user"]), async (req, res, next)=>{
    try{
        const user_id = req.user.id;  
        const currentDate = new Date(); 
        const vouchers = await sequelize.query(
            `SELECT v.*, vu.quantity 
             FROM vouchers v
             JOIN user_has_vouchers vu ON v.id = vu.voucher_id
             WHERE vu.user_id = :user_id
             AND vu.quantity > 0
             AND v.is_public = true
             AND v.start_at <= :currentDate
             AND v.end_at >= :currentDate
             ORDER BY v.createdAt DESC`, 
            {
                replacements: { user_id: user_id, currentDate: currentDate },
                type: sequelize.QueryTypes.SELECT
            }
        );
        return res.status(200).json(new Models.ResponseModel(true, null, vouchers));
    } catch (error) {
        logger.error('Error fetching user vouchers:', error);
        return res.status(500).json(new Models.ResponseModel(false, new Models.ErrorResponseModel(1, "Lỗi hệ thống", error.message), null));
    }
});




// find voucher by id (user - admin) lấy is_public = true 
router.get('/voucher/:id', authenticateToken, authorizeRole(["user", "admin"]), async (req, res, next) => {
    try {
        const voucher_id = req.params.id;

        const voucher = await voucher_model.Voucher.findOne({
            where: {id: voucher_id, is_public: true},

        });

        // Kiểm tra xem voucher có tồn tại không
        if (!voucher) {
            return res.status(404).json(new Models.ResponseModel(false, new Models.ErrorResponseModel(2, "Voucher không tồn tại", null), null));
        }

        return res.status(200).json(new Models.ResponseModel(true, null, voucher));
    } catch (error) {
        logger.error('Error fetching voucher by id:', error);
        return res.status(500).json(new Models.ResponseModel(false, new Models.ErrorResponseModel(1, "Lỗi hệ thống", error.message), null));
    }
});


/*
1. user_id = array
2. condition = enum (all, new_user, Only)
 - If user_id.lenght  <= 0 => user_id = null, condition = all
 - Else user_id = user_id[index], condition = only
*/
router.post('/voucher', authenticateToken, authorizeRole(["admin"]), async (req, res, next) => {
    try {
        const { title, description, discount, type, start_at, end_at, is_public, user_id = [], quantity = 1, condition = "all" } = req.body;

        // Nếu user_id không có hoặc rỗng, gán user_id = null và condition = 'all'
        let finalCondition = condition;
        let finalUserId = null;
        
        if (user_id.length > 0) {
            finalCondition = "only";  // Nếu có user_id, điều chỉnh condition thành 'only'
            finalUserId = user_id[0]; // Lấy user_id đầu tiên trong mảng
        }
        // Tạo voucher mới
        const voucher = await voucher_model.Voucher.create({
            title,
            description,
            discount,
            type,
            start_at,
            end_at,
            is_public,
        });

                // Kiểm tra nếu có user_id được chỉ định
                if (finalUserId) {
                    // Gán voucher cho các user được chỉ định (chỉ gán cho user_id đã chọn)
                    const users = await User.findAll({
                        where: {
                            id: user_id, // Lọc theo danh sách user_id
                        }
                    });
        
                    if (users.length === 0) {
                        return res.json(new Models.ResponseModel(false, new Models.ErrorResponseModel(1, "Không tìm thấy người dùng", null), null));
                    }
        
                    // Gán voucher cho từng user được chỉ định
                    await Promise.all(users.map(user => {
                        return voucher_model.VoucherUser.create({
                            user_id: user.id,
                            voucher_id: voucher.id,
                            quantity,   
                            condition: finalCondition
                        });
                    }));
        
                } else {
                    // Nếu không có user_id, gán voucher cho tất cả người dùng
                    const allUsers = await User.findAll(); // Lấy tất cả người dùng
        
                    await Promise.all(allUsers.map(user => {
                        return voucher_model.VoucherUser.create({
                            user_id: user.id,
                            voucher_id: voucher.id,
                            quantity,   
                            condition: finalCondition   
                        });
                    }));
                }

        return res.status(200).json(new Models.ResponseModel(true, null, voucher));

    } catch (error) {
        logger.error('Error adding voucher:', error);
        return res.status(500).json(new Models.ResponseModel(false, new Models.ErrorResponseModel(1, "Lỗi hệ thống", error.message), null));
    }
});

router.put('/voucher/:id',authenticateToken, authorizeRole(["admin"]),  async (req, res, next) => {
    try {
        const { id } = req.params;  
        const { title, description, discount, type, start_at, end_at, is_public ,user_id} = req.body;

        // Tìm voucher theo id
        let voucher = await voucher_model.Voucher.findByPk(id);
        if (!voucher) {
            return res.status(404).json(new Models.ResponseModel(false, new Models.ErrorResponseModel(2, "Voucher không tồn tại", null), null));
        }

        // Cập nhật thông tin voucher
        voucher.title = title || voucher.title;
        voucher.description = description || voucher.description;
        voucher.discount = discount || voucher.discount;
        voucher.type = type || voucher.type;
        voucher.start_at = start_at || voucher.start_at;
        voucher.end_at = end_at || voucher.end_at;
        voucher.is_public = is_public !== undefined ? is_public : voucher.is_public;
        voucher.user_id = user_id;  

        // Lưu thay đổi
        await voucher.save();

        if (user_id) {
            const user = await User.findByPk(user_id);
            if (!user) {
                return res.status(404).json(new Models.ResponseModel(false, new Models.ErrorResponseModel(3, "User không tồn tại", null), null));
            }

            // Xóa liên kết hiện tại và thêm liên kết với user mới
            await voucher.setUsers([user]);  
        }

        return res.status(200).json(new Models.ResponseModel(true, null, voucher));
    } catch (error) {
        logger.error('Error updating voucher:', error);
        return res.status(500).json(new Models.ResponseModel(false, new Models.ErrorResponseModel(1, "Lỗi hệ thống", error.message), null));
    }
});

export default router;