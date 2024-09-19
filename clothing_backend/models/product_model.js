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

export default Product;