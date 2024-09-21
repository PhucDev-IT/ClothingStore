import { func } from "joi";

// Hàm format lỗi validation để chuẩn hóa theo format mong muốn
export function formatValidationError(errorDetails) {
    return errorDetails.map(err => ({
        field_name: err.path.join('.'),
        error_message: err.message
    }));
}

// Hàm trả về response khi có lỗi
export function sendErrorResponse(res, code, message, details = []) {
    return res.status(code).json({
        code: code,
        message: message,
        details: details
    });
}


 