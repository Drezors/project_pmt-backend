-- init.sql

-- 1. create users
INSERT INTO user (first_name, last_name, email, password)
VALUES
  ('Alice', 'Durand', 'alice@example.com', 'pass123'),
  ('Bob', 'Martin', 'bob@example.com', 'pass123'),
  ('Chloé', 'Petit', 'chloe@example.com', 'pass123'),
  ('David', 'Bernard', 'david@example.com', 'pass123'),
  ('Emma', 'Lemoine', 'emma@example.com', 'pass123');

-- 2. create projects
INSERT INTO project (name, description, creator_id)
VALUES
  ('Vision XR', 'Projet de gestion immersive avec casques VR', 4), -- David
  ('Neural Cloud', 'Projet de coordination neuronale distribuée', 5); -- Emma

-- 3 projects member
INSERT INTO project_member (project_id, user_id)
VALUES
  (1, 1), (1, 2), (1, 3),
  (2, 1), (2, 2), (2, 3);