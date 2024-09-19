import { DataTypes } from 'sequelize';
import sequelize from '../connection/mysql.js';
import { v4 as uuidv4 } from 'uuid';
import Product from './product_model.js';

const Order = sequelize.define('order', {
    id: {
        type: DataTypes.STRING,
        primaryKey: true,       //Khóa chính
        defaultValue: () => uuidv4(),   //Random
    },
    order_date :{
        type: DataTypes.DATE,
        allowNull: false
    },

   // --- BỔ SUNG THÊM
}, {
    timestamps: true //Tự động thêm các trường createdAt và updatedAt
});


//---------------------------------------------------

const OrderDetails = sequelize.define('order_details',{
    //- Bổ sung
})


// Quan hệ 1 - n:  hasMany và belongsTo

Order.hasMany(OrderDetails, {foreignKey: 'order_id'});
OrderDetails.belongsTo(Order,{foreignKey: 'order_details_id'});

//Quan hệ 1-1: user - account

//Quan hệ n - n: 
export default Category;