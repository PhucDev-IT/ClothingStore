// notification_service.js
import admin from './firebase_admin.js';
import { default as User } from '../models/user_model.js';

// Hàm gửi thông báo
export const sendNotification = async (userId, notification) => {
  try {
    // Tìm người dùng trong cơ sở dữ liệu
    const user = await User.findOne({ where: { id: userId } });

    if (!user) {
      throw new Error(`Người dùng với ID ${userId} không tồn tại.`);
    }

    if (!user.deviceToken) {
      throw new Error(`Người dùng với ID ${userId} không có deviceToken.`);
    }

    // Chuẩn bị thông báo
    const message = {
      notification: {
        title: notification.title,
        body: notification.body,
      },
      token: user.deviceToken,
    };

    // Gửi thông báo Firebase
    const response = await admin.messaging().send(message);
    console.log(`Thông báo gửi thành công tới người dùng ${userId}:`, response);
    return response;

  } catch (error) {
    console.error(`Lỗi khi gửi thông báo tới người dùng ${userId}:`, error.message);
    if (error.errorInfo) {
      console.error('Chi tiết lỗi Firebase:', error.errorInfo);
    }
    throw error;
  }
};
