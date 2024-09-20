import express from 'express';
const app = express()   //Init app from express
import dotenv from 'dotenv';

import sequelize  from './connection/mysql.js';
import User from './models/user_model.js';
import Permission from './models/permission_model.js';
import Category from './models/category_model.js';
import Product from './models/product_model.js';
import ProductDetails from './models/product_model.js';
import Notification from './models/notification_model.js';
import Voucher from './models/voucher_model.js';
import Cart from './models/cart_model.js';
import CartDetails from './models/cart_model.js';
import Order from './models/order_model.js';
import OrderDetails from './models/order_model.js';

//Application config
dotenv.config();
const port = process.env.PORT || 8080

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
        console.log('Connection has been established successfully.');
        
        Category.hasMany(Product,{foreignKey: 'category_id'});
        Product.belongsTo(Category,{foreignKey:'product_id'});
        
        //Mới thêm
        //Cart
        User.hasOne(Cart,{foreignKey:'user_id'});
        Cart.belongsTo(User,{foreignKey:'cart_id'});
        Cart.hasMany(CartDetails, {foreignKey:'cart_id'});
        CartDetails.belongsTo(Cart, {foreignKey:'cart_detail_id'});
        //Chưa chắc chắn lắm
        CartDetails.belongsToMany(ProductDetails, {foreignKey:'cart_detail_id'});
        ProductDetails.belongsToMany(CartDetails, {foreignKey:'product_detail_id'});

        //Notification
        User.belongsToMany(Notification, {foreignKey:'user_id'});
        Notification.belongsToMany(User, {foreignKey:'notification_id'});

        //Order
        Order.hasMany(OrderDetails, {foreignKey: 'order_id'});
        OrderDetails.belongsTo(Order,{foreignKey: 'order_details_id'});

        Voucher.hasMany(Order, {foreignKey:'voucher_id'});
        Order.belongsTo(Voucher, {foreignKey:'order_id'});

        OrderDetails.hasMany(ProductDetails, {foreignKey:'order_details_id'});
        ProductDetails.belongsTo(OrderDetails,{foreignKey:'product_detail_id'});

        //Product - ProductDetail
        Product.hasMany(ProductDetails, {foreignKey:'product_id'});
        ProductDetails.belongsTo(Product,{foreignKey:'product_detail_id'});
        
        //Voucher
        User.belongsToMany(Voucher, {foreignKey:'user_id'});
        Voucher.belongsToMany(User, {foreignKey:'voucher_id'});



        // Đồng bộ hóa cơ sở dữ liệu
        await sequelize.sync({ force: true }); // Sử dụng { force: true } để xóa và tạo lại bảng
        console.log('All tables created successfully!');
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
    console.log(`Listening at http://localhost:${port}`)
})
