import { DataTypes } from 'sequelize';
import sequelize from '../connection/mysql.js';
import { v4 as uuidv4 } from 'uuid';
import Category from './category_model.js';

const Product = sequelize.define('product', {
    id: {
        type: DataTypes.STRING,
        primaryKey: true,
        defaultValue: () => uuidv4(),
    },
    name: {
        type: DataTypes.STRING,
        allowNull: false
    },
    img_preview : {
        type: DataTypes.STRING,
        allowNull: false
    },
    price :{
        type: DataTypes.FLOAT,
        allowNull: false
    },
    is_public :{
        type: DataTypes.BOOLEAN,
        allowNull: false,
        defaultValue:()=>true
    }
}, {
    timestamps: true //Tự động thêm các trường createdAt và updatedAt
});

const ProductDetails = sequelize.define('product_details',{
    id: {
        type: DataTypes.INTEGER,
        primaryKey: true,
        autoIncrement: true,
    },
    quantity:{
        type: DataTypes.INTEGER,
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
    price:{
        type: DataTypes.FLOAT,
        allowNull: false,
    },
    product_id:{
        type: DataTypes.STRING,
        allowNull: false,
    },
},  {
    timestamps: true //Tự động thêm các trường createdAt và updatedAt
    
});

// Product.hasMany(ProductDetails, {foreignKey:'product_id'});
// ProductDetails.belongsTo(Product,{foreignKey:'product_detail_id'});

export default {Product, ProductDetails};