DROP TABLE PRINT_PREFS;
CREATE TABLE PRINT_PREFS   (ID INT NOT NULL GENERATED ALWAYS AS IDENTITY,
                            DOC VARCHAR(20) NOT NULL,
                            HEADER VARCHAR(50),
                            FOOTER VARCHAR(50),
                            WATER_MARK VARCHAR(50),
                            CONSTRAINT PRINT_PREFS PRIMARY KEY (ID));
--

--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
INSERT INTO COMPANY (COMPANY, ACTIVITY, ADRESSE, EMAIL, SITE, TEL, FAX, NUM_RC, NUM_FISC, NUM_ART, CAPITAL) 
VALUES ('eTajer Version Demo', '', '', '', '', '', '', '', '', '', 0.00);
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
UPDATE COMPANY SET 
COMPANY = '', 
ACTIVITY = '', 
ADRESSE = '', 
EMAIL = '', 
SITE = '', 
TEL = '', 
FAX = '', 
NUM_RC = '', 
NUM_FISC = '', 
NUM_ART = '', 
CAPITAL = 0.00
WHERE ID = 1;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
SELECT * FROM COMPANY WHERE ID = 1;