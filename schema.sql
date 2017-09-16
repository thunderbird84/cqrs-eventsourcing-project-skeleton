CREATE DATABASE IF NOT EXISTS cqrs_db;

USE cqrs_db;

CREATE  TABLE IF NOT EXISTS users(
  id VARCHAR(32),
  first_name VARCHAR(32),
  last_name VARCHAR(32),
  age INT(4),
  email VARCHAR(255),
  secret VARCHAR(255)
);


CREATE  TABLE IF NOT EXISTS user_activites(
  id VARCHAR(32),
  user_id VARCHAR(32),
  type VARCHAR(32),
  time BIGINT,
  browser_agent VARCHAR(255)
);
