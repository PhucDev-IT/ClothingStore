import express from 'express';
const app = express()   //Init app from express
import dotenv from 'dotenv';

import sequelize  from './connection/mysql.js';
import user from './models/user_model.js';
import permission from './models/permission_model.js';
import Category from './models/category_model.js';
import Product from './models/product_model.js';

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
        const models = await getModel(sequelize, './models.json');
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
