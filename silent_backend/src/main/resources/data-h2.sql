INSERT INTO user (user_id, student_id, name, password, avatar, role, global_role, points, create_time)
VALUES
  (1, '123456', 'admin', '123456', NULL, 'ROLE_ADMIN', 0, 0, CURRENT_TIMESTAMP),
  (2, '234567', 'leader', '123456', NULL, 'leader', 1, 10, CURRENT_TIMESTAMP),
  (3, '345678', 'student', '123456', NULL, 'student', 1, 0, CURRENT_TIMESTAMP);

INSERT INTO club (club_id, name, description, leader_id, status, reject_reason, create_time)
VALUES
  (1, 'Computer Club', 'A sample tech club for local testing', 2, 1, NULL, CURRENT_TIMESTAMP);

INSERT INTO club_member (member_id, club_id, user_id, role_type, join_status, create_time)
VALUES
  (1, 1, 2, 1, 1, CURRENT_TIMESTAMP),
  (2, 1, 3, 3, 1, CURRENT_TIMESTAMP);

INSERT INTO activity (activity_id, club_id, title, description, location, start_time, end_time, max_participants, status, create_time)
VALUES
  (1, 1, 'Welcome Meetup', 'Welcome new members to the club', 'Building A-101', TIMESTAMP '2026-05-20 18:30:00', TIMESTAMP '2026-05-20 20:00:00', 50, 0, CURRENT_TIMESTAMP);
