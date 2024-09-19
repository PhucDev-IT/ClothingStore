


// Tạo bảng nếu chưa tồn tại
export function createTables(connection) {
    const create_table = `
        CREATE TABLE IF NOT EXISTS users (
            id INT AUTO_INCREMENT PRIMARY KEY,
            name VARCHAR(255) NOT NULL,
            email VARCHAR(255) NOT NULL
        );
    `;

    connection.query(create_table, function (err, results) {
        if (err) {
            console.error('Error creating table: ' + err.stack);
            return;
        }
        console.log('All table created or already exists');
    });
}


// Thêm dữ liệu mẫu
export function insertSamplePermission(connection) {
    const permission = [
        ['new_product', 'Thêm sản phẩm'],
        ['update_product', 'Cập nhật sản phẩm'],
        ['delete_product', 'Xóa sản phẩm'],
        ['new_user', 'Thêm người dùng'],
        ['update_user', 'Sửa thông tin người dùng'],
        ['delete_user', 'Xóa người dùng'],
        ['lock_account', 'Khóa tài khoản người dùng'],
        ['new_voucher', 'Thêm voucher'],
        ['update_voucher', 'Cập nhật voucher'],
        ['delete_voucher', 'Xóa voucher'],
    ];
    const columns = ['name', 'display_name'];
    insert_data(connection, 'permission', columns, values);
}

export function insertSampleRoles(connection) {
    const values = [
        ['ADMIN', 'Quản trị viên'],
        ['USER_A', 'Khách hàng hạng A'],
        ['USER_B', 'Khách hàng hạng B'],
        ['USER_S', 'Khách hàng tiềm năng'],
        ['STAFF', 'Nhân viên'],
        ['SYSTEM_ADMIN', 'Quản trị hệ thống'],
    ];
    const columns = ['name', 'display_name'];
    insert_data(connection, 'role', columns, values);
}

export function insertRoleHasPermission(connection) {
    const values = [
        [1, 1],
        [1, 2],
        [1, 3],
        [1, 4],
        [1, 5],
        [1, 6],
        [1, 7],
        [1, 8],
        [1, 9],
        [1, 10],  
    ];
    const columns = ['role_id', 'permission_id'];
insert_data(connection, 'role', columns, values);
}

//connection: 
//table_name: name table in database
//colums: array name colume, use for insert
//values: array data dependence colums
function insert_data(connection, table_name, columns, values) {
    connection.query('SELECT COUNT(*) AS count FROM ' + table_name, function (err, results) {
        if (err) {
            console.error('Error checking for data: ' + err.stack);
            return;
        }
        const count = results[0].count;
        if (count === 0) {
            const sql = `INSERT INTO ${table_name} (${columns.join(', ')}) VALUES ?`;
            connection.query(sql, [values], function (err, results) {
                if (err) {
                    console.error('Error inserting sample data: ' + err.stack);
                    return;
                }
                console.log('Sample data inserted successfully');
            });
        } else {
            console.log('Data already exists, no need to insert sample data');
        }
    });
}
