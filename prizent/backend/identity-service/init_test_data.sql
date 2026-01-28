-- Connect to MySQL and create test data
USE identity_db;

-- Create Test Client first
INSERT IGNORE INTO clients (id, name, description, created_at, updated_at) VALUES 
('03ca022f-54c9-438f-b744-c2732f77ec90', 'Test Client', 'Test client for development', NOW(), NOW());

-- Create test admin user (password is 'admin123' encoded with BCrypt)
INSERT IGNORE INTO users (id, username, email, password, role, client_id, created_at, updated_at) VALUES 
('c9fcb030-e9ff-4301-b25c-3debeb408dcc', 'admin', 'admin@test.com', '$2a$10$N.zmdr9k7uOCQb96VdodAeD6VDmyp0S3k4CBlwLddnacfRKKNWLgK', 'ADMIN', '03ca022f-54c9-438f-b744-c2732f77ec90', NOW(), NOW());

-- Check if data was inserted
SELECT 'Clients:' as table_name;
SELECT * FROM clients;
SELECT 'Users:' as table_name;
SELECT * FROM users;