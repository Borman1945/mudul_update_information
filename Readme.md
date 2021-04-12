Если предыдущее приложение накатилось номально и коннекшены в докере 
настроены нормально, то просто в aplication.property установите корректные ip
для вашего redis и postgres в контейнере
1) mvn clean
2) mvn package
3) sudo docker-compose build

Если пакет не собирается убедитесь что контейнер с postgres запущен
или просто запустите приложение modul_get_information в контейнере

sudo docker network connect postgres-network  app_set
