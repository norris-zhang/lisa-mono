ALTER TABLE "roll"
    ADD CONSTRAINT uniq_roll_class_student_date UNIQUE (class_id, student_id, class_date);
