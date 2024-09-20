import { DataTypes } from 'sequelize';
import sequelize from '../connection/mysql.js';
import { v4 as uuidv4 } from 'uuid';
import Product from './product_model.js';

const Province = sequelize.define('province', {
    code: {
        type: DataTypes.STRING,
        primaryKey: true,
    },
    name : {
        type: DataTypes.STRING,
        allowNull: true
    },
    slug :{
        type: DataTypes.STRING,
        allowNull: true,
    },
    type :{
        type: DataTypes.STRING,
        allowNull: true,
    },
    name_with_type :{
        type: DataTypes.STRING,
        allowNull: true,
    },
}, {
    timestamps: true //Tự động thêm các trường createdAt và updatedAt
});

//-----------------------------------------

const District = sequelize.define('district', {
    code: {
        type: DataTypes.STRING,
        primaryKey: true,
    },
    name : {
        type: DataTypes.STRING,
        allowNull: true
    },
    slug :{
        type: DataTypes.STRING,
        allowNull: true,
    },
    type :{
        type: DataTypes.STRING,
        allowNull: true,
    },
    name_with_type :{
        type: DataTypes.STRING,
        allowNull: true,
    },
    path:{
        type: DataTypes.STRING,
        allowNull: true,
    },
    path_with_type:{
        type: DataTypes.STRING,
        allowNull: true,
    }
}, {
    timestamps: true //Tự động thêm các trường createdAt và updatedAt
});

//------------------------------------------------------------------------------

const Ward = sequelize.define('ward', {
    code: {
        type: DataTypes.STRING,
        primaryKey: true,
    },

    name : {
        type: DataTypes.STRING,
        allowNull: true
    },
    slug :{
        type: DataTypes.STRING,
        allowNull: true,
    },
    type :{
        type: DataTypes.STRING,
        allowNull: true,
    },
    name_with_type :{
        type: DataTypes.STRING,
        allowNull: true,
    },
    path:{
        type: DataTypes.STRING,
        allowNull: true,
    },
    path_with_type:{
        type: DataTypes.STRING,
        allowNull: true,
    }
}, {
    timestamps: true //Tự động thêm các trường createdAt và updatedAt
});

Province.hasMany(District,{foreignKey:'parent_code'});
District.hasMany(Ward,{foreignKey:'parent_code'});


export default {Province,District,Ward};