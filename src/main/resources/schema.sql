-- Table: User
CREATE TABLE User (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- Table: Project
CREATE TABLE Project (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    due_date TIMESTAMP,
    created_by INT NOT NULL,
    FOREIGN KEY (created_by) REFERENCES User(id) ON DELETE CASCADE
);

-- Table: ProjectMember
CREATE TABLE ProjectMember (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    project_id INT NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('admin', 'member', 'visitor')),
    FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE,
    FOREIGN KEY (project_id) REFERENCES Project(id) ON DELETE CASCADE
);

-- Table: Task
CREATE TABLE Task (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    due_date TIMESTAMP,
    priority INT CHECK (priority BETWEEN 1 AND 5),
    status VARCHAR(50) NOT NULL,
    created_by INT NOT NULL,
    assigned_to INT,
    project_id INT NOT NULL,
    FOREIGN KEY (created_by) REFERENCES ProjectMember(id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_to) REFERENCES ProjectMember(id) ON DELETE SET NULL,
    FOREIGN KEY (project_id) REFERENCES Project(id) ON DELETE CASCADE
);

-- Table: Notification
CREATE TABLE Notification (
    id SERIAL PRIMARY KEY,
    message TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    recipient_id INT NOT NULL,
    project_id INT,
    task_id INT,
    FOREIGN KEY (recipient_id) REFERENCES User(id) ON DELETE CASCADE,
    FOREIGN KEY (project_id) REFERENCES Project(id) ON DELETE CASCADE,
    FOREIGN KEY (task_id) REFERENCES Task(id) ON DELETE CASCADE
);