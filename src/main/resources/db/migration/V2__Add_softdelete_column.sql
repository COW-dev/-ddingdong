ALTER TABLE activity_report ADD COLUMN deleted_at TIMESTAMP;
ALTER TABLE activity_report_term_info ADD COLUMN deleted_at TIMESTAMP;
ALTER TABLE banner ADD COLUMN deleted_at TIMESTAMP;
ALTER TABLE club ADD COLUMN deleted_at TIMESTAMP;
ALTER TABLE club_member ADD COLUMN deleted_at TIMESTAMP;
ALTER TABLE document ADD COLUMN deleted_at TIMESTAMP;
ALTER TABLE file_information ADD COLUMN deleted_at TIMESTAMP;
ALTER TABLE fix_zone ADD COLUMN deleted_at TIMESTAMP;
ALTER TABLE fix_zone_comment ADD COLUMN deleted_at TIMESTAMP;
ALTER TABLE notice ADD COLUMN deleted_at TIMESTAMP;
ALTER TABLE question ADD COLUMN deleted_at TIMESTAMP;
ALTER TABLE score_history ADD COLUMN deleted_at TIMESTAMP;
ALTER TABLE stamp_history ADD COLUMN deleted_at TIMESTAMP;
ALTER TABLE users ADD COLUMN deleted_at TIMESTAMP;
