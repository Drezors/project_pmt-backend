INSERT INTO user (id, name, email, password) VALUES (1, 'User1', 'user1@test.com', 'password');
INSERT INTO user (id, name, email, password) VALUES (2, 'User2', 'user2@test.com', 'password');
INSERT INTO user (id, name, email, password) VALUES (3, 'User3', 'user3@test.com', 'password');
INSERT INTO user (id, name, email, password) VALUES (4, 'User4', 'user4@test.com', 'password');
INSERT INTO user (id, name, email, password) VALUES (5, 'User5', 'user5@test.com', 'password');

INSERT INTO project (id, name, description, start_date, created_by_id) VALUES (1, 'Project Alpha', 'Premier projet', NOW(), 4);
INSERT INTO project (id, name, description, start_date, created_by_id) VALUES (2, 'Project Beta', 'Deuxième projet', NOW(), 5);

INSERT INTO project_member (user_id, project_id, role) VALUES (1, 1, 'MEMBER');
INSERT INTO project_member (user_id, project_id, role) VALUES (2, 1, 'MEMBER');
INSERT INTO project_member (user_id, project_id, role) VALUES (3, 1, 'MEMBER');
INSERT INTO project_member (user_id, project_id, role) VALUES (1, 2, 'MEMBER');
INSERT INTO project_member (user_id, project_id, role) VALUES (2, 2, 'MEMBER');
INSERT INTO project_member (user_id, project_id, role) VALUES (3, 2, 'MEMBER');