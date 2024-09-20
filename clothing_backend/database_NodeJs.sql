CREATE TABLE user
(  id nvarchar(50) PRIMARY KEY,
	last_name nvarchar(30),
	full_name nvarchar(30),
	display_name nvarchar(100),
	date_of_birth DATE,
	number_phone nvarchar(15),
	email nvarchar(50),
	gender bit(1),
	hash_password text,
	is_active bit(1),
	created_at DATETIME,
	updated_at DATETIME
	);
	
CREATE TABLE role
(  id INT auto_increment PRIMARY KEY,
	name nvarchar(100),
	display_name nvarchar(255),
	created_at DATETIME,
	updated_at DATETIME
	);

CREATE TABLE user_has_role
(  role_id INT auto_increment PRIMARY KEY,
	user_id nvarchar(50),
	FOREIGN KEY(role_id) REFERENCES role(id),
	FOREIGN KEY(user_id) REFERENCES user(id)
	);
	
CREATE TABLE permission
( id INT AUTO_INCREMENT PRIMARY KEY,
	NAME nvarchar(100),
	display_name nvarchar(255),
	created_at DATETIME,
	updated_at DATETIME
	);
	
CREATE TABLE role_has_permission
(  role_id INT,
	permission_id INT,
	FOREIGN KEY(role_id) REFERENCES role(id),
	FOREIGN KEY(permission_id) REFERENCES permission(id)
	);
	
CREATE TABLE notification
(  id INT AUTO_INCREMENT PRIMARY KEY,
	title NVARCHAR(255),
	description	nvarchar(100),
	type nvarchar(50),
	required_action BIT(1),
	user_id NVARCHAR(50),
	FOREIGN KEY(user_id) REFERENCES user(id)
	);
	
CREATE TABLE image 
(  id INT AUTO_INCREMENT PRIMARY KEY,
	model_name NVARCHAR(100),
	model_id NVARCHAR(50),
	path TEXT,
	created_at DATETIME,
	updated_at DATETIME
	);
	
CREATE TABLE voucher
(  id NVARCHAR(50) PRIMARY KEY,
	title NVARCHAR(255),
	description TEXT,
	discount FLOAT,
	type nvarchar(50),
	start_at DATETIME,
	end_at DATETIME,
	status BIT(1),
	created_at DATETIME,
	updated_at DATETIME,
	user_id NVARCHAR(50) NULL,
	FOREIGN KEY(user_id) REFERENCES user(id)
	);
	
CREATE TABLE address
(  id INT AUTO_INCREMENT PRIMARY KEY,
	user_id NVARCHAR(50),
	province NVARCHAR(255),
	district NVARCHAR(255),
	ward NVARCHAR(255),
	details NVARCHAR(1000),
	number_phone NVARCHAR(15),
	type nvarchar(20),
	status bit(1),
	is_default bit(1),
	FOREIGN KEY(user_id) REFERENCES user(id)
	);
	
CREATE TABLE category
(  id INT AUTO_INCREMENT PRIMARY KEY,
	category_name NVARCHAR(100),
	created_at DATETIME,
	updated_at DATETIME
	);
	
CREATE TABLE product
(  id NVARCHAR(50) PRIMARY KEY, 
	name nvarchar(255),
	img_preview TEXT,
	price FLOAT,
	category_id INT,
	status bit(1),
	created_at DATETIME,
	updated_at DATETIME,
	FOREIGN KEY(category_id) REFERENCES category(id)
	);
	
CREATE TABLE product_detail
(  id INT AUTO_INCREMENT PRIMARY KEY,
	quantity INT,
	color NVARCHAR(20),
	size NVARCHAR(10),
	price FLOAT,
	product_id NVARCHAR(50),
	created_at DATETIME,
	updated_at DATETIME,
	FOREIGN KEY(product_id) REFERENCES product(id)
	);
	
CREATE TABLE orders
(  id NVARCHAR(50) PRIMARY KEY,
	order_date DATETIME,
	total FLOAT,
	real_total FLOAT,
	status bit(1),
	voucher_id NVARCHAR(50),
	address_id INT,
	user_id NVARCHAR(50),
	FOREIGN KEY(voucher_id) REFERENCES voucher(id),
	FOREIGN KEY(address_id) REFERENCES address(id),
	FOREIGN KEY(user_id) REFERENCES user(id)
	);

CREATE TABLE order_detail
(  id NVARCHAR(50) PRIMARY KEY,
	order_id NVARCHAR(50),
	product_detail_id INT,
	quantity INT,
	price FLOAT,
	FOREIGN KEY(product_detail_id) REFERENCES product_detail(id),
	FOREIGN KEY(order_id) REFERENCES orders(id)
	);
	
CREATE TABLE cart 
(  id INT AUTO_INCREMENT PRIMARY KEY, 
	user_id NVARCHAR(50),
	FOREIGN KEY(user_id) REFERENCES user(id)
	);

CREATE TABLE cart_detail
(	id INT AUTO_INCREMENT PRIMARY KEY,
	product_detail_id INT,
	cart_id INT,
	quantity INT,
	FOREIGN KEY(product_detail_id) REFERENCES product_detail(id),
	FOREIGN KEY(cart_id) REFERENCES cart(id)
	);
	