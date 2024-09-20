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
        type: DataTypes.BOOLEAN,
        allowNull: false,
    },
    voucher_id:{
        type: DataTypes.STRING,
        allowNull: false,
    },

    address_id :{
        type: DataTypes.INTEGER,
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
        // defaultValue: () => uuidv4(),   
    },
    order_id:{
        type: DataTypes.STRING,
        allowNull: false,
    },
    product_detail_id: {
        type: DataTypes.INTEGER,
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


// Order.hasMany(OrderDetails, {foreignKey: 'order_id'});
// OrderDetails.belongsTo(Order,{foreignKey: 'order_details_id'});

// Voucher.hasMany(Order, {foreignKey:'voucher_id'});
// Order.belongsTo(Voucher, {foreignKey:'order_id'});

// OrderDetails.hasMany(ProductDetails, {foreignKey:'order_details_id'});
// ProductDetails.belongsTo(OrderDetails,{foreignKey:'product_detail_id'});

export default {Order, OrderItem};