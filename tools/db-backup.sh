#!/bin/bash

timestamp=$(date +%Y-%m-%d-%H-%M-%S)
pg_dump -U ${SPRING_DATASOURCE_USERNAME} -W -h localhost -p 5432 --inserts --clean --create --dbname lisadb > /home/ubuntu/backup/sql/${timestamp}.sql

