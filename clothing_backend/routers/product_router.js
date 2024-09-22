
import express from 'express';
import Joi from 'joi';
const router = express.Router(); // Sử dụng express.Router()
import { formatValidationError } from '../utils/exception.js'
import Category from '../models/category_model.js';
import product_model from '../models/product_model.js';

export default function () {
    
    router.get('/categories', async (req, res, next) => {
        var categories = await Category.findAll();
    });

    //get all
    //Need to get with condition is_public = true, and sort by rate
    router.get("/products", async(req,res,next)=>{
        const products = await product_model.Product.findAll({
            where: {
                is_public: true,
            },
            order:[['rate','DESC']]
        });
    });

    //find product by id - return additional details
    router.get('/product/:id', async (req, res, next) => {
        try {
            const product = await product_model.Product.findByPk(req.params.id); 
            
        } catch (error) {
            // handle exception
        }
    });

    //Pagination
    router.get('/products/p',async(req,res,next)=>{
        const limit = req.body.limit;
        const offset = req.body.offset;

        const products = await User.findAll({
            limit: limit,
            offset: offset
        });
    });

    return router;
}