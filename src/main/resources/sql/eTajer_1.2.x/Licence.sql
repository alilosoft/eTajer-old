DROP TABLE LICENCE;
CREATE TABLE LICENCE   (ACTIV_CODE VARCHAR(50),
                        REG_USER VARCHAR(30),
                        TRIAL_COUNT INT NOT NULL DEFAULT 0,
                        CONSTRAINT TRIAL_LIMIT CHECK (TRIAL_COUNT <= 0));
INSERT INTO LICENCE VALUES (null, 'Trial', 0);
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
-- Table alterations:
-- 30/11/2013
--ALTER TABLE LICENCE ADD COLUMN REG_USER VARCHAR(30);

--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
SELECT ACTIV_CODE FROM LICENCE;
SELECT REG_USER FROM LICENCE;
SELECT TRIAL_COUNT FROM LICENCE;
UPDATE LICENCE SET ACTIV_CODE = ?, REG_USER = ?;
UPDATE LICENCE SET TRIAL_COUNT = TRIAL_COUNT + 1;