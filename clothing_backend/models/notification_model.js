import { DataTypes } from 'sequelize';
import sequelize from '../connection/mysql.js';
import { v4 as uuidv4 } from 'uuid';
import User from './user_model.js';

const Notification = sequelize.define('notification', {
    id: {
        type: DataTypes.INTEGER,
        autoIncrement: true,
        primaryKey: true,
    },
    title : {
        type: DataTypes.STRING,
        allowNull: false
    },
    description :{
        type: DataTypes.STRING,
        allowNull: false,
    },   
    type :{
        type: DataTypes.STRING,
        allowNull: false,
    },
    required_action :{
        type: DataTypes.BOOLEAN,
        allowNull: false,
    },    
    user_id :{
        type: DataTypes.STRING,
        allowNull: false,
    },   

}, {
    timestamps: true //Tự động thêm các trường createdAt và updatedAt
});

// User.belongsToMany(Notification, {foreignKey:'user_id'});
// Notification.belongsToMany(User, {foreignKey:'notification_id'});

export default Notification;