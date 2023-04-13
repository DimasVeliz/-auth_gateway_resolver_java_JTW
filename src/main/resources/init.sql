create database jwt_security;
use jwt_security;
CREATE USER 'jwt_security_user'@'localhost' IDENTIFIED BY 'password';
GRANT CREATE, ALTER, DROP, INSERT, UPDATE, DELETE, SELECT, REFERENCES, RELOAD on *.* TO 'jwt_security_user'@'localhost' WITH GRANT OPTION;

