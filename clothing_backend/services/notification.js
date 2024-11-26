import admin from 'firebase-admin';
import { default as User } from '../models/user_model.js'; // Thêm .js vào đường dẫn

// Firebase Admin SDK Singleton
let firebaseAdminApp;
if (!admin.apps.length) {
  const serviceAccount = new URL('../path-to-service-account-file.json', import.meta.url);
  firebaseAdminApp = admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
  });
} else {
  firebaseAdminApp = admin.app();
}

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
    console.log('Thông báo đã được gửi thành công:', response);
    return response;

  } catch (error) {
    console.error(`Lỗi khi gửi thông báo cho người dùng ${userId}:`, error.message);
    throw error;
  }
};
