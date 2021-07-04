CREATE TABLE TYPE_VNT (ID INT NOT NULL GENERATED ALWAYS AS IDENTITY,
                       DES VARCHAR(15) NOT NULL UNIQUE,
                       DEF_MARGE DECIMAL(9,2) NOT NULL DEFAULT 0,
                       CONSTRAINT TYPE_VNT_PK PRIMARY KEY (ID));
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