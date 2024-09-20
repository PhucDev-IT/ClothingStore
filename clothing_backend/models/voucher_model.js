import { DataTypes } from 'sequelize';
import sequelize from '../connection/mysql.js';
import { v4 as uuidv4 } from 'uuid';
import User from './user_model.js';

const Voucher = sequelize.define('voucher', {
    id: {
        type: DataTypes.STRING,
        primaryKey: true,
        defaultValue: () => uuidv4(),
    },
    title : {
        type: DataTypes.STRING,
        allowNull: false,
    },
    description :{
        type: DataTypes.STRING,
        allowNull: false,
    },
    discount:{
        type: DataTypes.FLOAT,
        allowNull: false,
    },
    type:{
        type: DataTypes.STRING,
        allowNull: false,
    },
    start_at:{
        type: DataTypes.DATE,
        allowNull: false,
    },
    end_at:{
        type: DataTypes.DATE,
        allowNull: false,
    },
    status:{
        type: DataTypes.BOOLEAN,
        allowNull: false,
    },
    user_id:{
        type: DataTypes.STRING,
        allowNull: true,
    },
}, {
    timestamps: true //Tự động thêm các trường createdAt và updatedAt
});

// User.belongsToMany(Voucher, {foreignKey:'user_id'});
// Voucher.belongsToMany(User, {foreignKey:'voucher_id'});

export default Voucher;