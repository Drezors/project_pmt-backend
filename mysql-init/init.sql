-- Suppression des tables si elles existent déjà
DROP TABLE IF EXISTS notification;
DROP TABLE IF EXISTS task;
DROP TABLE IF EXISTS project_member;
DROP TABLE IF EXISTS project;
DROP TABLE IF EXISTS user;

-- Création des tables

CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE projects (
    project_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE project_members (
    project_member_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    project_id INT NOT NULL,
    role VARCHAR(50) NOT NULL,
    CONSTRAINT fk_pm_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_pm_project FOREIGN KEY (project_id) REFERENCES projects(project_id),
    CONSTRAINT unique_user_project UNIQUE (user_id, project_id)
);

CREATE TABLE tasks (
    task_id INT PRIMARY KEY AUTO_INCREMENT,
    project_id INT NOT NULL,
    assigned_to_id INT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    priority VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    CONSTRAINT fk_task_project FOREIGN KEY (project_id) REFERENCES projects(project_id),
    CONSTRAINT fk_task_assigned FOREIGN KEY (assigned_to_id) REFERENCES users(user_id)
);

CREATE TABLE notifications (
    notification_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Données de test

INSERT INTO users (username, email, password) VALUES
('alice', 'alice@example.com', 'hashed_password_alice'),
('bob', 'bob@example.com', 'hashed_password_bob');

INSERT INTO projects (name, description) VALUES
('Projet Alpha', 'Gestion de la première phase du produit'),
('Projet Beta', 'Suivi du développement du second produit');

INSERT INTO project_members (user_id, project_id, role) VALUES
(1, 1, 'Admin'),
(2, 1, 'Member'),
(2, 2, 'Admin');

INSERT INTO tasks (project_id, assigned_to_id, title, description, priority, status) VALUES
(1, 1, 'Définir cahier des charges', 'Spécifications initiales', 'High', 'To Do'),
(1, 2, 'Préparer maquette', 'Création des wireframes', 'Medium', 'In Progress'),
(2, 2, 'Planifier réunion', 'Kickoff avec les parties prenantes', 'Low', 'Done');

INSERT INTO notifications (user_id, content) VALUES
(1, 'Nouvelle tâche assignée : Définir cahier des charges'),
(2, 'Vous avez été ajouté au projet Projet Beta');
