DROP TABLE IF EXISTS Choice;

DROP TABLE IF EXISTS Question;

DROP TABLE IF EXISTS Challenge;

DROP TABLE IF EXISTS Admin;

CREATE TABLE Challenge(
	scenario_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	introduction TEXT NOT NULL,
	completion INT NOT NULL
);

CREATE TABLE Question(
	question_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	question_text TEXT NOT NULL,
	scenario_id INT NOT NULL,
	FOREIGN KEY (scenario_id) REFERENCES Challenge(scenario_id) ON DELETE CASCADE
);

CREATE TABLE Choice(
	choice_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	weight INT NOT NULL,
	choice_text TEXT NOT NULL,
	question_id INT NOT NULL,
	FOREIGN KEY (question_id) REFERENCES Question(question_id) ON DELETE CASCADE
);

CREATE TABLE Admin(
    admin_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    firstname VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    password VARCHAR(100) NOT NULL
)