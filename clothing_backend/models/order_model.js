import { DataTypes } from 'sequelize';
import sequelize from '../connection/mysql.js';
import { v4 as uuidv4 } from 'uuid';
import ProductDetails from './product_model.js';
import Voucher from './voucher_model.js'

const Order = sequelize.define('order', {
    id: {
        type: DataTypes.STRING,
        primaryKey: true,       //Khóa chính
        defaultValue: () => uuidv4(),   //Random
    },
    order_date :{
        type: DataTypes.DATE,
        allowNull: false,
    },

    total: {
        type: DataTypes.FLOAT,
        allowNull: false,
    },
    real_total: {
        type: DataTypes.FLOAT,
        allowNull: false,
    },

    status :{
        type: DataTypes.STRING,
        allowNull: false,
        defaultValue:()=>"PENDING"
    },
    voucher_id:{
        type: DataTypes.STRING,
        allowNull: true,
    },

    delivery_information :{
        type: DataTypes.STRING,
        allowNull: false,
    },
    user_id:{
        type: DataTypes.STRING,
        allowNull: false,
    },

}, {
    timestamps: true //Tự động thêm các trường createdAt và updatedAt
});


//---------------------------------------------------

const OrderItem = sequelize.define('order_item',{
    id: {
        type: DataTypes.STRING,
        primaryKey: true,       
        defaultValue: () => uuidv4(),   
    },
    order_id:{
        type: DataTypes.STRING,
        allowNull: false,
    },
    color:{
        type: DataTypes.STRING,
        allowNull: false,
    }, 
    size:{
        type: DataTypes.STRING,
        allowNull: false,
    },
    quantity:{
        type: DataTypes.INTEGER,
        allowNull: false,
    },
    price: {
        type: DataTypes.FLOAT,
        allowNull: false,
    },

})



export default {Order, OrderItem};