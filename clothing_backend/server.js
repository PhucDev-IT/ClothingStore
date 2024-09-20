import express from 'express';
const app = express()   //Init app from express
import dotenv from 'dotenv';
import { fileURLToPath } from 'url';
import { dirname } from 'path';
import fs from 'fs';
import path from 'path';
import sequelize  from './connection/mysql.js';
import user from './models/user_model.js';
import permission from './models/permission_model.js';
import Category from './models/category_model.js';
import address_model from './models/address_model.js';
import User from './models/user_model.js';
import cart_model from './models/cart_model.js';
import order_model from './models/order_model.js';
import Voucher from './models/voucher_model.js';
import product_model from './models/product_model.js';
import Notification from './models/notification_model.js';
import Image from './models/image_model.js';
import swaggerDocs from './utils/swagger.js';
import logger from './utils/logger.js';
//Application config
dotenv.config();
const port = process.env.PORT || 8080
const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

//CORS: Chính sách truyền tải qua domain
//Origin = domain + port
// Need to config CORS, because policy google chorme has been blocked
app.use(function (req, res, next) {
    res.setHeader("Access-Control-Allow-Headers", "X-Requested-With,content-type, Accept,Authorization,Origin");
    res.setHeader("Access-Control-Allow-Origin", "*");
    res.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, PATCH, DELETE");
    res.setHeader("Access-Control-Allow-Credentials", true);
    next();
});


async function syncDatabase() {
    try {
        // Mở kết nối
        await sequelize.authenticate();
        logger.info('Connection has been established successfully.');

        product_model.Product.belongsToMany(Category,{through: 'category_product',foreignKey:'product_id'});
        Category.belongsToMany(product_model.Product,{through: 'category_product',foreignKey: 'category_id'});


        //Cart
        User.hasOne(cart_model.Cart, { foreignKey: 'user_id' });
        cart_model.Cart.belongsTo(User, { foreignKey: 'user_id' });
        

        cart_model.Cart.hasMany(cart_model.CartItem, {foreignKey:'cart_id'});
        cart_model.CartItem.belongsTo(cart_model.Cart, {foreignKey:'cart_detail_id'});
        cart_model.CartItem.belongsTo(product_model.ProductDetails, {foreignKey:'cart_detail_id'});
        product_model.ProductDetails.hasMany(cart_model.CartItem, {foreignKey:'product_detail_id'});

        //Notification
        User.hasMany(Notification, {foreignKey:'user_id'});
        Notification.belongsTo(User, {foreignKey:'notification_id'});

        //Order : n - n Product
        order_model.Order.belongsToMany(product_model.Product, {through : order_model.OrderItem,foreignKey: 'order_id'});
        product_model.Product.belongsToMany(order_model.Order,  {through : order_model.OrderItem,foreignKey: 'product_id'})
        order_model.OrderItem.belongsTo(product_model.ProductDetails,{foreignKey: 'order_details_id'});
        product_model.ProductDetails.hasMany(order_model.OrderItem,{foreignKey:'product_details_id'})
        

        Voucher.hasMany(order_model.Order, {foreignKey:'voucher_id'});
        order_model.Order.belongsTo(Voucher, {foreignKey:'order_id'});
        //Product - ProductDetail
        product_model.Product.hasMany(product_model.ProductDetails, {foreignKey:'product_id'});
        product_model.ProductDetails.belongsTo(product_model.Product,{foreignKey:'product_detail_id'});
        
        //Voucher
        User.belongsToMany(Voucher, {through:'voucher_user',foreignKey:'user_id'});
        Voucher.belongsToMany(User, {through:'voucher_user',foreignKey:'voucher_id'});
    

        await sequelize.sync({ force: true }); // Sử dụng { force: true } để xóa và tạo lại bảng

        const rawProvince = await JSON.parse(fs.readFileSync(path.join(__dirname, '/static/tinh_tp.json'), 'utf-8'));
        const rawDistrict = await JSON.parse(fs.readFileSync(path.join(__dirname, '/static/quan_huyen.json'), 'utf-8'));
        const rawWard = await JSON.parse(fs.readFileSync(path.join(__dirname, '/static/xa_phuong.json'), 'utf-8'));
        const rawProduct = await JSON.parse(fs.readFileSync(path.join(__dirname, '/static/products.json'), 'utf-8'));
        const rawImage = await JSON.parse(fs.readFileSync(path.join(__dirname, '/static/image_table.json'), 'utf-8'));
        const raw_product_details = await JSON.parse(fs.readFileSync(path.join(__dirname, '/static/product_details.json'), 'utf-8'));
        const raw_category = await JSON.parse(fs.readFileSync(path.join(__dirname, '/static/categories_table.json'), 'utf-8'));

        const dataProvince = Object.values(rawProvince);
        const dataDistrict = Object.values(rawDistrict);
        const dataWard = Object.values(rawWard);
        

        await address_model.Province.bulkCreate(dataProvince, {ignoreDuplicates: true})
        await address_model.District.bulkCreate(dataDistrict, {ignoreDuplicates: true})
        await address_model.Ward.bulkCreate(dataWard, {ignoreDuplicates: true})

        await product_model.Product.bulkCreate(rawProduct, {ignoreDuplicates: true})
        await Image.bulkCreate(rawImage, {ignoreDuplicates: true})
        await product_model.ProductDetails.bulkCreate(raw_product_details, {ignoreDuplicates: true})
        await Category.bulkCreate(raw_category, {ignoreDuplicates: true})

        // Đồng bộ hóa cơ sở dữ liệu
        logger.info('All tables created successfully!');
    } catch (error) {
        console.error('Unable to connect to the database:', error);
    } finally {
        // Đóng kết nối
        await sequelize.close();
    }
}

syncDatabase();

app.get('/', (req, res) => {
    res.send("This is sample backend, it's api for clothing store")
})



app.listen(port, () => {
    logger.info(`Listening at http://localhost:${port}`)
    swaggerDocs(app, port);
})
