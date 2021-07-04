CREATE TABLE TARIF (ID INT NOT NULL GENERATED ALWAYS AS IDENTITY,
                    DES VARCHAR(15) NOT NULL UNIQUE,
                    MARGE DECIMAL(5,2) NOT NULL DEFAULT 0,
                    QTE_MIN INT NOT NULL DEFAULT 0,
                    CONSTRAINT TARIF_PK PRIMARY KEY (ID));
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
INSERT INTO TYPE_VNT (DES) VALUES 'Gros', 'Détail';
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
CREATE VIEW V_TYPE_VNT (ID, "Désignation", "Marge") AS
SELECT * FROM TYPE_VNT;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--
SELECT * FROM V_TYPE_VNT;
SELECT * FROM TYPE_VNT WHERE ID = ?;
INSERT INTO TYPE_VNT (DES, DEF_MARGE) VALUES (?, ?);
UPDATE TYPE_VNT SET DES = ?, DEF_MARGE = ? WHERE ID = ?;
DELETE FROM TYPE_VNT WHERE ID = ?;
--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--