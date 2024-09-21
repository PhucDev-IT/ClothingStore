
import express from 'express';
import jwt from 'jsonwebtoken'; // Thay vì require
import bcrypt from 'bcrypt'; // Thay vì require
import Joi from 'joi';
const router = express.Router(); // Sử dụng express.Router()
import db from '../connection/mysql.js'
import User from '../models/user_model.js';
import { formatValidationError, sendErrorResponse } from '../utils/exception.js'


const secret = "ABCDQWERTYUIIOO";

//docs: https://github.com/jquense/yup


//Need to fcm_token, because we want to notice to all user registed
// device_id: unique id of device, use to session login manager
const loginRequest = Joi.object({
    email: Joi.string().email().required().messages({
        "any.required": "Tên người dùng là bắt buộc",
        "string.base": "Tên người dùng phải là chuỗi ký tự"
    }),
    password: Joi.string().required().messages({
        "any.required": "Mật khẩu là bắt buộc",
        "string.base": "Mật khẩu phải là chuỗi ký tự"
    }),
    fcm_token: Joi.string().required().messages({
        "any.required": "FCM_Token là bắt buộc",
        "string.base": "FCM_Token phải là chuỗi ký tự"
    }),
    device_id: Joi.string().required().messages({
        "any.required": "device_id là bắt buộc",
        "string.base": "device_id phải là chuỗi ký tự"
    })
});


const registerRequest = Joi.object({
    email: Joi.string().email().required().messages({
        "any.required": "Tên người dùng là bắt buộc",
        "string.base": "Tên người dùng phải là chuỗi ký tự"
    }),
    password: Joi.string().required().messages({
        "any.required": "Mật khẩu là bắt buộc",
        "string.base": "Mật khẩu phải là chuỗi ký tự"
    }),
    full_name: Joi.string().required().messages({
        "any.required": "Mật khẩu là bắt buộc",
        "string.base": "Mật khẩu phải là chuỗi ký tự"
    }),
    first_name: Joi.string().optional(),
    last_name: Joi.string().optional(),
    avatar: Joi.string().optional(),
    number_phone: Joi.string().optional()

});

//log_login : id, user_id, device_id, fcm_token, time_login

export default function () {
    // use post to hash data in request
    // Step 1: hash password and compare to password in database
    // Step 2: IF password incorrect return message,else transfer step 3
    // Step 3: Check user is_active => callback if is not active
    // Step 4: Get token (jsonwebtoken) 
    // Step 5: we need to add infor login on database
    // Step 6: return reponse with info user and token

    router.post('/login', async (req, res, next) => {

        const { error } = await loginRequest.validate(req.body);
        if (error) {
            const formattedErrors = formatValidationError(error.details);
            return res.status(400).json({
                code: 400,
                message: "Validation Error",
                details: formattedErrors
            });
        }
        try {
            const { email, password } = req.body;
            // Tìm người dùng dựa trên user_name
            const user = await User.findOne({ where: { email, password } });

            if (!user) {
                return sendErrorResponse(res, 401, "Tên người dùng hoặc mật khẩu không chính xác");
            }
            // So sánh mật khẩu đã nhập với chuỗi hash lấy từ cơ sở dữ liệu
            const passwordMatch = await bcrypt.compare(password, user.password);
            if (passwordMatch) {

                res.json({
                    message: 'Đăng nhập thành công',
                    user: user,
                });
            } else {
                return sendErrorResponse(res, 401, "Tên người dùng hoặc mật khẩu không chính xác");
            }

        } catch (err) {
            return sendErrorResponse(res, 500, "Lỗi hệ thống", [{ error_message: err.message }]);
        }

    });

    //Step 1: you must validate data from request 
    //If information missing , you need to response with error
    // permission user
    router.post('/register', async (req, res, next) => {
        const { error } = await registerRequest.validate(req.body);
        if (error) {
            const formattedErrors = formatValidationError(error.details);
            return res.status(400).json({
                code: 400,
                message: "Validation Error",
                details: formattedErrors
            });
        }
        try {
            const { email, password, full_name, first_name, last_name, number_phone, avatar } = req.body;
            const newUser = await User.create({
                first_name: first_name,
                last_name: last_name,
                email: email,
                password: password,
                full_name: full_name,
                number_phone: number_phone,
                avatar: avatar
            });

            res.json({
                message: 'Đăng nhập thành công',
                user: newUser,
            });

        } catch (err) {
            return sendErrorResponse(res, 500, "Lỗi hệ thống", [{ error_message: err.message }]);
        }

    });

    return router;
}



