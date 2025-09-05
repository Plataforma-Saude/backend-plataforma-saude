CREATE DATABASE IF NOT EXISTS agendamento_db;
USE agendamento_db;

CREATE TABLE agendamentos (
                              id INT AUTO_INCREMENT PRIMARY KEY,
                              cliente VARCHAR(100) NOT NULL,
                              data DATE NOT NULL,
                              hora TIME NOT NULL,
                              dia_semana ENUM('SEGUNDA','TERÇA','QUARTA','QUINTA','SEXTA','SÁBADO','DOMINGO') NOT NULL,
                              status ENUM('PENDENTE','CONFIRMADO','CANCELADO') DEFAULT 'PENDENTE',
                              observacoes TEXT
);
