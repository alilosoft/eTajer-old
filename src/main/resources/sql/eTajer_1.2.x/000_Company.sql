DROP TABLE COMPANY;
CREATE TABLE COMPANY   (ID INT NOT NULL GENERATED ALWAYS AS IDENTITY, 
                        COMPANY VARCHAR(50) NOT NULL,
                        ACTIVITY VARCHAR(50) NOT NULL,
                        ADRESSE VARCHAR(50) NOT NULL,
                        EMAIL VARCHAR(50) NOT NULL,
                        SITE VARCHAR(50) NOT NULL,
                        TEL VARCHAR(15) NOT NULL,
                        FAX VARCHAR(15) NOT NULL,
                        NUM_RC VARCHAR(25) NOT NULL,
                        NUM_FISC VARCHAR(25) NOT NULL,
                        NUM_ART  VARCHAR(25) NOT NULL,
                        CAPITAL DECIMAL(12,2) NOT NULL DEFAULT 0,
                        CONSTRAINT COMP_PK PRIMARY KEY (ID));
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