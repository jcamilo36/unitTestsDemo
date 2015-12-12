DROP TABLE ACCOUNT;
DROP TABLE CREDIT;
DROP TABLE CLIENT;

CREATE TABLE IF NOT EXISTS CLIENT (
  id BIGINT NOT NULL AUTO_INCREMENT,
  typeId VARCHAR(50) NOT NULL,
  clientId VARCHAR(50) NOT NULL,
  firstName VARCHAR(50),
  lastName VARCHAR(50),
  email VARCHAR(50),
  PRIMARY KEY (id),
  INDEX IND_CLIENT_1 (typeId, clientId)
)
  AUTO_INCREMENT = 0;

CREATE TABLE IF NOT EXISTS CREDIT (
  id INT(15) NOT NULL AUTO_INCREMENT,
  clientId INT(15) NOT NULL,
  creditValue DECIMAL NOT NULL,
  approvalDate DATE NOT NULL,
  paidDate DATE,
  PRIMARY KEY (id)
)
  AUTO_INCREMENT = 0;
  
CREATE TABLE IF NOT EXISTS ACCOUNT (
	id INT(15) NOT NULL AUTO_INCREMENT,
    clientId INT(15) NOT NULL,
    total DECIMAL NOT NULL,
    PRIMARY KEY (id)
)
	AUTO_INCREMENT = 0;