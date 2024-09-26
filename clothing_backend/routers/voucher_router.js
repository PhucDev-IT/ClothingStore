import express from "express";
import Joi from "joi";
const router = express.Router(); // Sử dụng express.Router()
import { formatValidationError } from "../utils/exception.js";
import { authenticateToken, authorizeRole } from "../config/jwt_filter.js";
import logger from "../utils/logger.js";
import Voucher from "../models/voucher_model.js";
import Models from "../models/response/ResponseModel.js";
import User from "../models/user_model.js";
//test - ok
//get all voucher (phân trang - admin) 
router.get('/vouchers',authenticateToken, authorizeRole(["admin"]), async (req, res, next) =>{
    try{
        const page = parseInt(req.query.page) || 1; 
        const limit = parseInt(req.query.limit) || 20; 
        const offset = (page - 1) * limit;
    
        // Lấy tất cả vouchers với phân trang và sắp xếp theo ngày tạo mới nhất
        const { count, rows } = await Voucher.findAndCountAll({
            offset: offset,
            limit: limit,
            order: [['createdAt', 'DESC']], 
        });    
        const totalPages = Math.ceil(count / limit);
        return res.status(200).json(new Models.ResponseModel(true, null, {
            vouchers: rows,
            currentPage: page,
            totalPages: totalPages,
            totalVouchers: count
        }));
    } catch (error) {
    logger.error('Error fetching vouchers:', error);
    return res.status(500).json(new Models.ResponseModel(false, new Models.ErrorResponseModel(1, "Lỗi hệ thống", error.message), null));
    }
});


// get all voucher by user_id (phân trang - user) - lấy các voucher có thời hạn(start_at <= current_date <= end_at), is_public = true
router.get('/vouchers/my_voucher',authenticateToken, authorizeRole(["user"]), async (req, res, next)=>{
    try{
        const user_id = req.user.id;  
        const page = parseInt(req.query.page) || 1; 
        const limit = parseInt(req.query.limit) || 10; 
        const offset = (page - 1) * limit; 
        const currentDate = new Date(); 
    
        // Lấy tất cả vouchers hợp lệ với phân trang
        const { count, rows } = await Voucher.findAndCountAll({
            include: [{
                model: User,
                where: { id: user_id },
                attributes: [] 
            }],
            where: {
                is_public: true
            },
            offset: offset,
            limit: limit,
        });
        
        const filteredVouchers = rows.filter(voucher => {
            const startAt = new Date(voucher.start_at);
            const endAt = new Date(voucher.end_at);
            return voucher.is_public === true && startAt <= currentDate && endAt >= currentDate;
        });

        // Adjust the total count after filtering
        const totalPages = Math.ceil(filteredVouchers.length / limit);

        return res.status(200).json(new Models.ResponseModel(true, null, {
            vouchers: filteredVouchers,
            currentPage: page,
            totalPages: totalPages,
            totalVouchers: filteredVouchers.length
        }));

    
    } catch (error) {
        logger.error('Error fetching user vouchers:', error);
        return res.status(500).json(new Models.ResponseModel(false, new Models.ErrorResponseModel(1, "Lỗi hệ thống", error.message), null));
    }
});




// find voucher by id (user - admin) lấy is_public = true 
router.get('/voucher/:id', authenticateToken, authorizeRole(["user", "admin"]), async (req, res, next) => {
    try {
        const voucher_id = req.params.id;
        console.log("Voucher ID:", voucher_id);

        const voucher = await Voucher.findOne({
            where: {id: voucher_id, is_public: true},
            include: [{
                model: User,
                through: { attributes: [] }, // Không hiển thị thông tin của user
                attributes: [] // Ẩn tất cả thông tin của user
            }]
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


router.post('/voucher',authenticateToken, authorizeRole(["admin"]),  async (req, res, next) => {
    try {
        const { title, description, discount, type, start_at, end_at, is_public, user_id } = req.body;
        console.log(user_id);
        const voucher = await Voucher.create({
            title,
            description,
            discount,
            type,
            start_at,
            end_at,
            is_public,
        });
        const user = await User.findByPk(user_id);
        if (!user) {
            return res.status(404).json(new Models.ResponseModel(false, new Models.ErrorResponseModel(1, "User not found", null), null));
        }
        await user.addVoucher(voucher);
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
        let voucher = await Voucher.findByPk(id);
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