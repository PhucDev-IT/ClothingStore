import dotenv from 'dotenv';
import jwt from 'jsonwebtoken';
import Models from '../models/response/ResponseModel.js';

dotenv.config();

export const authenticateToken = (req, res, next) => {
    const authHeader = req.headers['authorization'];
    const token = authHeader && authHeader.split(' ')[1];

    if (token == null) {
        return  res.status(401).json(new Models.ResponseModel(false, new Models.ErrorResponseModel(1, "Unauthorized", null), null));
    }

    jwt.verify(token, process.env.TOKEN_SECRET, (err, user) => {
        if (err) {
            return  res.status(401).json(new Models.ResponseModel(false, new Models.ErrorResponseModel(1, "Unauthorized", null), null));
        }
        req.user = user;
        next();
    });
};

export const authorizeRole = (roles) => {
    return (req, res, next) => {
        if (!roles.includes(req.user.role)) {
            return  res.status(403).json(new Models.ResponseModel(false, new Models.ErrorResponseModel(1, "Bạn không có quyền truy cập", null), null));
        }
        next();
    };
};