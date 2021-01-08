DROP TABLE IF EXISTS Choice;

DROP TABLE IF EXISTS Question;

DROP TABLE IF EXISTS ChallengeFeedback;

DROP TABLE IF EXISTS Rating;

DROP TABLE IF EXISTS Challenge;

DROP TABLE IF EXISTS Admin;

CREATE TABLE Challenge(
	challenge_id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    challenge_title VARCHAR(50) NOT NULL ,
	challenge_introduction TEXT NOT NULL ,
	challenge_completion INT(11) NOT NULL ,
	challenge_thumbnail TEXT NOT NULL ,
    challenge_stream varchar(3) NOT NULL,
	avg_rating varchar(25) DEFAULT 0
)AUTO_INCREMENT=1;

CREATE TABLE Question(
	question_id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    question_title VARCHAR(50) NOT NULL ,
	question_text TEXT NOT NULL ,
	question_completion INT(11) NOT NULL ,
	challenge_id INT(11) NOT NULL ,
	FOREIGN KEY (challenge_id) REFERENCES Challenge(challenge_id) ON DELETE CASCADE
)AUTO_INCREMENT=1;

CREATE TABLE Choice(
	choice_id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY ,
	choice_weight INT(11) NOT NULL ,
	choice_text TEXT NOT NULL ,
	choice_reason TEXT NOT NULL,
	question_id INT(11) NOT NULL ,
	FOREIGN KEY (question_id) REFERENCES Question(question_id) ON DELETE CASCADE
)AUTO_INCREMENT=1;

CREATE TABLE Admin(
    admin_id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY ,
    username VARCHAR(50) UNIQUE NOT NULL ,
    firstname VARCHAR(50) NOT NULL ,
    lastname VARCHAR(50) NOT NULL ,
    phone_number VARCHAR(20) NOT NULL ,
    email VARCHAR(50) UNIQUE NOT NULL ,
    password VARCHAR(100) NOT NULL
)AUTO_INCREMENT=1;

CREATE TABLE ChallengeFeedback(
    feedback_id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY ,
    feedback_title varchar(50) NOT NULL ,
    feedback_text TEXT NOT NULL ,
    positive BOOLEAN NOT NULL ,
    challenge_id INT(11) NOT NULL ,
    FOREIGN KEY (challenge_id) REFERENCES Challenge(challenge_id) ON DELETE CASCADE
)AUTO_INCREMENT=1;

CREATE TABLE Rating(
    rating_id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY ,
    rating_value INT(1) NOT NULL ,
    challenge_id INT(11) NOT NULL ,
    FOREIGN KEY (challenge_id) REFERENCES Challenge(challenge_id) ON DELETE CASCADE
)AUTO_INCREMENT=1;